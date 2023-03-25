package android.example.uptoskills

import android.content.Intent
import android.example.uptoskills.Adapters.SignInSlider
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.databinding.ActivitySignUpBinding
import android.example.uptoskills.models.SliderData
import android.example.uptoskills.models.Users
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.smarteist.autoimageslider.SliderView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*


class SignUpActivity : AppCompatActivity() {

    private lateinit var callbackManager: CallbackManager
    private val RC_SIGN_IN: Int = 123
    private lateinit var createAccount: CardView
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val TAG = "SignUpActivity Tag"
    private lateinit var userDao: UsersDao
    private lateinit var authStateChangeListener: FirebaseAuth.AuthStateListener
    private lateinit var referId: String
    private lateinit var SignIn: TextView


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
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        callbackManager = CallbackManager.Factory.create();
        userDao = UsersDao()

        supportActionBar?.hide()

        referId = intent.getStringExtra("ReferId").toString()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()
        createAccount = findViewById(R.id.SignUpWithEmail)

//        binding.SignIn.setOnClickListener {
//            var intent = Intent(this, SignInActivity::class.java)
//            intent.putExtra("ReferId", referId)
//            startActivity(intent)
//            finish()
//        }

        createAccount.setOnClickListener {
            CreateAccountWithEmail()
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

        binding.back.setOnClickListener {
            val signUpintent = Intent(this, SignInActivity::class.java)
            signUpintent.putExtra("ReferId", referId)
            startActivity(signUpintent)
            finish()
        }

        setUpSlider()
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


    private fun changeAuth() {
        val user = FirebaseAuth.getInstance().currentUser

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(binding.crtUsername.text.toString()).build()
        user!!.updateProfile(profileUpdates)
    }

    private fun CreateAccountWithEmail(){
        binding.signUpProgressBar.visibility = View.VISIBLE
        if(binding.crtemail.text.toString().trim().isNotBlank() && binding.crtpass.text.toString().trim()
                .isNotBlank() && binding.crtUsername.text.toString().trim().isNotBlank() &&
                    binding.mobileNo.text.toString().trim().isNotBlank()
        ){
            auth.createUserWithEmailAndPassword(binding.crtemail.text.toString(), binding.crtpass.text.toString()).addOnCompleteListener {
                if(it.isSuccessful){
                    changeAuth()
                    binding.signUpProgressBar.visibility = View.GONE

                    //adding user to db
                    val user: Users = Users("","", "", "", "", "", "", hashMapOf(), hashMapOf(),binding.crtUsername.text.toString(), binding.crtUsername.text.toString(),
                        binding.crtemail.text.toString(), "", "", "", "", binding.mobileNo.text.toString().trim(),
                        it.result.user?.uid!!, "", referId, 250)
                    val userDao = UsersDao()


                    GlobalScope.launch(Dispatchers.IO) {
                        var referer: Users? = null
                        if(referId.trim().isNotBlank() && referId.trim().lowercase() == "null") {

                            referer =
                                userDao.getUserById(referId).await().toObject(Users::class.java)
                            if (referer != null) {
                                referer.coins += 250
                                userDao.updateUser(referer, referId)
                            }

                        }
                        userDao.addUser(user, it.result.user?.uid.toString())
                    }
                    val id: String? = it.result.user?.uid
//                    val intent = Intent(this, UserDetailsActivity::class.java)
//                    intent.putExtra("username", binding.crtUsername.text.toString())
//                    intent.putExtra("email", binding.crtemail.text.toString())
//                    intent.putExtra("id", id)
//                    intent.putExtra("userImage", "")
//                    intent.putExtra("Activity", "NewUser")
//                    startActivity(intent)
                    startMainActivity()
                    finish()
                }
                else {
                    binding.signUpProgressBar.visibility = View.GONE
                    Toast.makeText(this, it.exception?.message.toString(),
                        Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            binding.signUpProgressBar.visibility = View.GONE
            Toast.makeText(this, "Please Fill the required details",
                Toast.LENGTH_SHORT).show()
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
                        val user = Users("","", "", "", "","", "",hashMapOf(),hashMapOf(), auth.currentUser?.displayName.toString(), auth.currentUser?.displayName.toString(),
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
                    CONSTANTS.getInstance(this@SignUpActivity)
                    CONSTANTS.setEmail(user.email.toString())
                    if(binding.crtUsername.text.toString().isEmpty())
                        CONSTANTS.setUsername(user.displayName.toString())
                    else CONSTANTS.setUsername(binding.crtUsername.text.toString())
                    startMainActivity()
                }
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        binding.signUpProgressBar.visibility = View.GONE
        finish()
    }
}