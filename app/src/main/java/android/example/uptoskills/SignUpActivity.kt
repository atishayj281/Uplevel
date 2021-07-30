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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        callbackManager = CallbackManager.Factory.create();
        userDao = UsersDao()

        supportActionBar?.hide()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()
        createAccount = findViewById(R.id.SignUpWithEmail)

        binding.SignIn.setOnClickListener {
            var intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        createAccount.setOnClickListener {
            var intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)

        }

        binding.SignUpWithGoogle.setOnClickListener {
            binding.signUpProgressBar.visibility = View.VISIBLE
            signUp()
        }

        binding.facebookLoginButton.setPermissions("email", "public_profile")
        binding.SignUpWithFaceBook.setOnClickListener {
            binding.signUpProgressBar.visibility = View.VISIBLE

            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

            LoginManager.getInstance().retrieveLoginStatus(this, object : LoginStatusCallback {
                override fun onCompleted(accessToken: AccessToken) {
                    // User was previously logged in, can log them in directly here.
                    // If this callback is called, a popup notification appears that says
                    // "Logged in as <User Name>"
                    handelFacebookToken(accessToken)
                }

                override fun onFailure() {
                }

                override fun onError(exception: Exception) {
                    // An error occurred
                    Toast.makeText(this@SignUpActivity, exception.message, Toast.LENGTH_SHORT).show()
                }
            })
            binding.facebookLoginButton.registerCallback(callbackManager,
                object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(loginResult: LoginResult?) {
                        handelFacebookToken(loginResult?.accessToken)
                    }
                    override fun onCancel() {
                        // App code
                    }
                    override fun onError(exception: FacebookException) {
                        Toast.makeText(this@SignUpActivity, "Login Failed", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        authStateChangeListener = FirebaseAuth.AuthStateListener {
            var user: FirebaseUser? = auth.currentUser
            if(user != null) {
                updateUI(user)
            }
        }
    }

    private fun handelFacebookToken(accessToken: AccessToken?) {
        var credential: AuthCredential = FacebookAuthProvider.getCredential(accessToken?.token.toString())
        auth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful) {
                var user: FirebaseUser = auth.currentUser!!
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

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateChangeListener)
    }

    override fun onStop() {
        super.onStop()
        if(authStateChangeListener != null) {
            auth.removeAuthStateListener(authStateChangeListener)
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

            userDao.ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var ref = snapshot.child(auth.uid.toString())
                    if(!ref.exists()) {
                        var user: Users = Users(auth.currentUser?.displayName.toString(), auth.currentUser?.displayName.toString(),
                            auth.currentUser?.email.toString(), "", "", "",
                            auth.currentUser?.photoUrl.toString(), "", auth.currentUser?.uid.toString(), "")
                        userDao.addUser(user, auth.currentUser?.uid.toString())

                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
            binding.signUpProgressBar.visibility = View.VISIBLE
            var intent = Intent(this@SignUpActivity, UserDetailsActivity::class.java)
            intent.putExtra("id", auth.uid)
            intent.putExtra("username", auth.currentUser?.displayName)
            intent.putExtra("userImage", auth.currentUser?.photoUrl)
            intent.putExtra("Activity", "NewUser")
            startActivity(intent)
            finish()
        }
    }
}