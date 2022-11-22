package android.example.uptoskills

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import java.lang.Exception

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var referId: String = ""
    private lateinit var logoAnimation: Animation
    private lateinit var logo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        auth = FirebaseAuth.getInstance()
        logoAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_logo)
        logo = findViewById(R.id.logo)
        logo.animation = logoAnimation


        Handler(Looper.getMainLooper()).postDelayed(Runnable(){
              if(auth.currentUser != null) {
                  val intent = Intent(this, MainActivity::class.java)
                  startActivity(intent)
                  finish()
              } else {

                  FirebaseDynamicLinks.getInstance()
                      .getDynamicLink(intent)
                      .addOnSuccessListener(this) { pendingDynamicLinkData ->
                          // Get deep link from result (may be null if no link is found)
                          var deepLink: Uri? = null
                          if (pendingDynamicLinkData != null) {
                              deepLink = pendingDynamicLinkData.link
                              var referralLink = deepLink.toString()
                              try {
                                  referralLink = referralLink.substring(referralLink.lastIndexOf("=")+1)

                                  referId = referralLink.substring(0, referralLink.indexOf("-"))

                                  val productId: String = referralLink.substring(referralLink.indexOf("-")+1)
                                  val intent = Intent(this, OnBoardingActivity::class.java)
                                  intent.putExtra("referId", referId)
                                  startActivity(intent)
                                  finish()

                              } catch (e: Exception) {
                              }

                          }
                      }
                      .addOnCanceledListener {

                      }
                      .addOnFailureListener(this) {
                              e -> Log.w("starting Activity", "getDynamicLink:onFailure", e)
                              }
                  val intent = Intent(this, OnBoardingActivity::class.java)

                  startActivity(intent)
                  finish()


              }
        }, 2500)

    }
}