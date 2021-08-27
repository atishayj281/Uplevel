package android.example.uptoskills

import android.content.Intent
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.databinding.ActivitySignUpBinding
import android.example.uptoskills.models.Users
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*


class SignUpActivity : AppCompatActivity() {

    private lateinit var callbackManager: CallbackManager
    private val RC_SIGN_IN: Int = 123
    private lateinit var createAccount: Button
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val TAG = "SignUpActivity Tag"
    private lateinit var userDao: UsersDao
    private lateinit var authStateChangeListener: FirebaseAuth.AuthStateListener
    private lateinit var referId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        callbackManager = CallbackManager.Factory.create();
        userDao = UsersDao()

        supportActionBar?.hide()

        referId = intent.getStringExtra("ReferId").toString()
//        Log.e("referId", referId)
//        Log.e("differ", "")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()
        createAccount = findViewById(R.id.SignUpWithEmail)

        binding.SignIn.setOnClickListener {
            var intent = Intent(this, SignInActivity::class.java)
            intent.putExtra("ReferId", referId)
            startActivity(intent)
            finish()
        }

        createAccount.setOnClickListener {
            var crtintent = Intent(this, CreateAccountActivity::class.java)
            crtintent.putExtra("ReferId", referId)
            startActivity(crtintent)

        }

        binding.SignUpWithGoogle.setOnClickListener {
            binding.signUpProgressBar.visibility = View.VISIBLE
            signUp()
        }

        binding.facebookLoginButton.setPermissions("email", "public_profile")
        binding.SignUpWithFaceBook.setOnClickListener {
            binding.signUpProgressBar.visibility = View.VISIBLE

            LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("public_profile"));

            LoginManager.getInstance().retrieveLoginStatus(this, object : LoginStatusCallback {
                override fun onCompleted(accessToken: AccessToken) {
                    // User was previously logged in, can log them in directly here.
                    // If this callback is called, a popup notification appears that says
                    // "Logged in as <User Name>"
                    handelFacebookToken(accessToken)
                }

                override fun onFailure() {
                    binding.signUpProgressBar.visibility = View.GONE
                }

                override fun onError(exception: Exception) {
                    // An error occurred
                    binding.signUpProgressBar.visibility = View.GONE
                    Toast.makeText(this@SignUpActivity, exception.message, Toast.LENGTH_SHORT)
                        .show()
                }
            })
            binding.facebookLoginButton.registerCallback(callbackManager,
                object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(loginResult: LoginResult?) {
                        handelFacebookToken(loginResult?.accessToken)
                    }

                    override fun onCancel() {
                        // App code
                        binding.signUpProgressBar.visibility = View.GONE
                    }

                    override fun onError(exception: FacebookException) {
                        binding.signUpProgressBar.visibility = View.GONE
                        Toast.makeText(this@SignUpActivity, "Login Failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        }

        authStateChangeListener = FirebaseAuth.AuthStateListener {
            val user: FirebaseUser? = auth.currentUser
            if (user != null) {
                updateUI(user)
            }
        }
    }

    private fun handelFacebookToken(accessToken: AccessToken?) {
        val credential: AuthCredential =
            FacebookAuthProvider.getCredential(accessToken?.token.toString())
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val user: FirebaseUser = auth.currentUser!!
                updateUI(user)
            } else {
                Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun signUp() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        callbackManager.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)

            } catch (e: ApiException) {
                binding.signUpProgressBar.visibility = View.GONE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateChangeListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateChangeListener)
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
                    binding.signUpProgressBar.visibility = View.GONE
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {

        if (user != null) {
            userDao.ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var ref = snapshot.child(auth.currentUser?.uid.toString())
                    if (!ref.exists()) {
                        var user = Users(hashMapOf(),
                            hashMapOf(),
                            auth.currentUser?.displayName.toString(),
                            auth.currentUser?.displayName.toString(),
                            auth.currentUser?.email.toString(),
                            "",
                            "",
                            "",
                            auth.currentUser?.photoUrl.toString(),
                            "",
                            auth.currentUser?.uid.toString(),
                            "",
                            referId,
                            250)
                        userDao.addUser(user, auth.currentUser?.uid.toString())

                        var upRef: Users? =
                            referId
                                .let { snapshot.child(it).getValue(Users::class.java) }

                        if (upRef != null) {
                            upRef.coins += 500
                            Log.e("coins", upRef.coins.toString())
                            userDao.updateUser(upRef, referId)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

            startMainActivity()
        }

    }

    fun startMainActivity() {
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        binding.signUpProgressBar.visibility = View.GONE
        finish()
    }
}