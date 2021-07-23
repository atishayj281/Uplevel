package android.example.UptoSkills

import android.content.Intent
import android.example.UptoSkills.daos.GoogleUsersDao
import android.example.UptoSkills.databinding.ActivitySignInBinding
import android.example.UptoSkills.models.GoogleUser
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore


class SignInActivity : AppCompatActivity() {

    private val RC_SIGN_IN: Int = 123
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivitySignInBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(Build.VERSION.SDK_INT >= 21) {
            var window: Window = this.window
            window.statusBarColor = ContextCompat.getColor(this, R.color.LightningYellow)
        }

        db = FirebaseFirestore.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()

        binding.createAccount.setOnClickListener {
            var intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.SignInWithEmail.setOnClickListener {
            if(binding.email.text.toString().length != 0 && binding.password.text.toString().length != 0){
                binding.signInprogressbar.visibility = View.VISIBLE
                auth.signInWithEmailAndPassword(binding.email.text.toString(), binding.password.text.toString())
                    .addOnCompleteListener{
                    if(it.isSuccessful){
                        binding.signInprogressbar.visibility = View.GONE
                        startMainActivity()
                    } else {
                        Toast.makeText(this, it.exception?.message.toString(),
                            Toast.LENGTH_SHORT).show()
                        binding.signInprogressbar.visibility = View.GONE
                    }
                }

            }

        }

        binding.SignInWithGoogle.setOnClickListener {
            binding.signInprogressbar.visibility = View.VISIBLE
            signInwithGoogle()
            binding.signInprogressbar.visibility = View.GONE
        }

        binding.forgetPass.setOnClickListener {
            binding.passwordresetlayout.visibility = View.VISIBLE
            binding.resetButton.setOnClickListener {
                resetPassword()
            }
        }

    }

    private fun resetPassword() {
        binding.signInprogressbar.visibility = View.VISIBLE
        FirebaseAuth.getInstance().sendPasswordResetEmail(binding.passwordResetEmail.text.toString()).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(this, "Email Sent Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
            }
            binding.passwordresetlayout.visibility = View.GONE
            binding.signInprogressbar.visibility = View.GONE
        }
    }

    fun startMainActivity(){
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun signInwithGoogle() {
        binding.signInprogressbar.visibility = View.VISIBLE
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
        binding.signInprogressbar.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                var account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)

            } catch (e: ApiException) {
                Log.d("TAG", "Google Sign in failed")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user != null) {
            val dao = GoogleUsersDao()
            var isExists = true
            dao.ref.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(!snapshot.child(user.uid).exists()){
                        isExists = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("")
                }

            })

            if(!isExists){
                var intent = Intent(this, UserDetailsActivity::class.java)
                intent.putExtra("id", auth.uid)
                intent.putExtra("username", auth.currentUser?.displayName)
                intent.putExtra("userImage", auth.currentUser?.photoUrl)
                startActivity(intent)
                finish()
            }
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}