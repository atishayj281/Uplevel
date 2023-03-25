package android.example.uptoskills

import android.content.Intent
import android.example.uptoskills.Adapters.SignInSlider
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.databinding.ActivitySignInBinding
import android.example.uptoskills.models.SliderData
import android.example.uptoskills.models.Users
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.*
import com.smarteist.autoimageslider.SliderView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*


class SignInActivity : AppCompatActivity() {

    private lateinit var callbackManager: CallbackManager
    private val RC_SIGN_IN: Int = 123
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivitySignInBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var userDao: UsersDao
    private lateinit var authStateChangeListener: FirebaseAuth.AuthStateListener
    private final val PRODUCT = "1"


    var url1 = R.drawable.onboarding1
    var url2 = R.drawable.onboarding2
    var url3 = R.drawable.onboarding3
    var url4 = R.drawable.onboarding4
    var url5 = R.drawable.onboarding5

    private fun setUpSlider(){
        val sliderDataArrayList: ArrayList<SliderData> = ArrayList()

        // initializing the slider view.

        // initializing the slider view.
        val sliderView = findViewById<SliderView>(R.id.imageSlider)

        // adding the urls inside array list

        // adding the urls inside array list
        sliderDataArrayList.add(SliderData(url1))
        sliderDataArrayList.add(SliderData(url2))
        sliderDataArrayList.add(SliderData(url3))
        sliderDataArrayList.add(SliderData(url4))
        sliderDataArrayList.add(SliderData(url5))

        // passing this array list inside our adapter class.

        // passing this array list inside our adapter class.
        val adapter = SignInSlider(this, sliderDataArrayList)

        // below method is used to set auto cycle direction in left to
        // right direction you can change according to requirement.

        // below method is used to set auto cycle direction in left to
        // right direction you can change according to requirement.
        sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR

        // below method is used to
        // setadapter to sliderview.

        // below method is used to
        // setadapter to sliderview.
        sliderView.setSliderAdapter(adapter)

        // below method is use to set
        // scroll time in seconds.

        // below method is use to set
        // scroll time in seconds.
        sliderView.scrollTimeInSec = 3

        // to set it scrollable automatically
        // we use below method.

        // to set it scrollable automatically
        // we use below method.
        sliderView.isAutoCycle = true

        // to start autocycle below method is used.

        // to start autocycle below method is used.
        sliderView.startAutoCycle()

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDao = UsersDao()
        callbackManager = CallbackManager.Factory.create();
        db = FirebaseFirestore.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()

        setUpSlider()

        binding.createAccount.setOnClickListener {
            val signUpintent = Intent(this, SignUpActivity::class.java)
            signUpintent.putExtra("ReferId", intent.getStringExtra("ReferId"))
            startActivity(signUpintent)
            finish()
        }

        binding.SignInWithEmail.setOnClickListener {
            if(binding.email.text.toString().isNotBlank() && binding.password.text.toString().isNotBlank()){
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
            } else {
                binding.signInprogressbar.visibility = View.GONE
                Toast.makeText(this, "Please fill the required details", Toast.LENGTH_SHORT).show()
            }

        }

        binding.SignInWithGoogle.setOnClickListener {
            binding.signInprogressbar.visibility = View.VISIBLE
            signInwithGoogle()
        }

        binding.forgetPass.setOnClickListener {
            binding.passwordresetlayout.visibility = View.VISIBLE
            binding.resetButton.setOnClickListener {
                resetPassword()
            }
        }

        binding.SignInWithFaceBook.setOnClickListener {

            binding.signInprogressbar.visibility = View.VISIBLE
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile"));

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
                    Toast.makeText(this@SignInActivity, exception.message, Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this@SignInActivity, "Login Failed", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        authStateChangeListener = FirebaseAuth.AuthStateListener {
            var user: FirebaseUser? = auth.currentUser
            if(user != null) {
                updateUI(user)
            }
        }

        binding.resetCloseBtn.setOnClickListener {
            binding.passwordresetlayout.visibility = View.GONE
        }

    }


    override fun onStop() {
        super.onStop()
        if(authStateChangeListener != null) {
            auth.removeAuthStateListener(authStateChangeListener)
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

    private fun resetPassword() {
        binding.signInprogressbar.visibility = View.VISIBLE
        Log.e("email", binding.passwordResetEmail.text.toString().trim())
        if(binding.passwordResetEmail.text.toString().trim().isNotEmpty() && binding.passwordResetEmail.text.toString().trim() != "null") {
            FirebaseAuth.getInstance()
                .sendPasswordResetEmail(binding.passwordResetEmail.text.toString())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Email Sent Successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, it.exception?.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                    binding.passwordresetlayout.visibility = View.GONE
                    binding.signInprogressbar.visibility = View.GONE
                }
        } else {
            Toast.makeText(this, "Please Enter the Email...", Toast.LENGTH_SHORT).show()
        }
    }
    private fun startMainActivity(){
        var Mainintent = Intent(this, MainActivity::class.java)
        Mainintent.putExtra(PRODUCT, intent.getStringExtra(PRODUCT))
        startActivity(Mainintent)
        binding.signInprogressbar.visibility = View.GONE
        finish()
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateChangeListener)
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
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)

            } catch (e: ApiException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
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
            GlobalScope.launch(Dispatchers.IO) {

                var upRef: Users? = null

                if(intent.getStringExtra("ReferId").toString().lowercase().trim() != "null" &&
                    intent.getStringExtra("ReferId").toString().lowercase().trim().isNotBlank()) {
                    upRef =
                        userDao.getUserById(intent.getStringExtra("ReferId").toString()).await().toObject(Users::class.java)
                }
                userDao.userCollection.document(auth.currentUser?.uid.toString()).get().addOnSuccessListener {
                    if(!it.exists()) {
                        //Toast.makeText(this@SignInActivity, "creating user", Toast.LENGTH_SHORT).show()
                        val user = Users("","", "", "", "","", "", hashMapOf(),hashMapOf(), auth.currentUser?.displayName.toString(), auth.currentUser?.displayName.toString(),
                            auth.currentUser?.email.toString(), "", "", "",
                            auth.currentUser?.photoUrl.toString(), "", auth.currentUser?.uid.toString(), "",
                            intent.getStringExtra("ReferId").toString(), 250)
                        userDao.addUser(user, auth.currentUser?.uid.toString())
                        if(upRef != null) {
                            upRef.coins += 250
                            userDao.updateUser(upRef, intent.getStringExtra("ReferId").toString())
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    CONSTANTS.getInstance(this@SignInActivity)
                    CONSTANTS.setEmail(user.email.toString())
                    CONSTANTS.setUsername(user.displayName.toString())
                    startMainActivity()
                }
            }
        }
    }


}