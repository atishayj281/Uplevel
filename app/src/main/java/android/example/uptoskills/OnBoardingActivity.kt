package android.example.uptoskills

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.example.uptoskills.Adapters.SliderAdapter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var slideViewPager: ViewPager
    private lateinit var dotsLayout: LinearLayout
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var btnNext: TextView
    private lateinit var btnSkip: TextView
    private var referId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        if(Build.VERSION.SDK_INT >= 21) {
            var window: Window = this.window
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    Log.e("my refer link", deepLink.toString())
                    var referralLink = deepLink.toString()
                    try {
                        referralLink = referralLink.substring(referralLink.lastIndexOf("=")+1)
                        Log.e("subReferLink", referralLink)
                        referId = referralLink.substring(0, referralLink.indexOf("-"))
                        Log.e("refer", referId.toString())
                        val productId: String = referralLink.substring(referralLink.indexOf("-")+1)

                    } catch (e: Exception) {
                        Log.e("error", e.message.toString())
                    }

                }
            }
            .addOnFailureListener(this) { e -> Log.w("starting Activity", "getDynamicLink:onFailure", e) }

        slideViewPager = findViewById(R.id.slideViewPager)
        dotsLayout = findViewById(R.id.dotLayout)
        sliderAdapter = SliderAdapter(this)
        btnNext = findViewById(R.id.btn_next)
        btnSkip = findViewById(R.id.btn_skip)
        slideViewPager.adapter = sliderAdapter

        if(!isFirstTimeStartApp()){
            Log.e("refer", referId.toString())
            startMainActivity()
        }

        slideViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if(position == 2){
                    // LAST LAYOUT
                    btnNext.text = "Start"
                    btnSkip.visibility = View.GONE
                }
                else {
                    btnNext.text = "Next"
                    btnSkip.visibility = View.VISIBLE
                }
                //addDotsIndicator(position)

            }
            override fun onPageSelected(position: Int) {

            }

        })


        //addDotsIndicator(0)
        btnSkip.setOnClickListener {
            Log.e("refer", referId.toString())
            startMainActivity()
        }

        btnNext.setOnClickListener {
            var currentPage = slideViewPager.currentItem+1
            if(currentPage < 3) {
                slideViewPager.setCurrentItem(currentPage)
            } else {
                startMainActivity()
            }
        }
    }

    fun startMainActivity(){
        setFirstTimeStartStatus(false)
        var intent = Intent(this, SignInActivity::class.java)
        intent.putExtra("ReferId", referId)
        Log.e("ReferId", referId)
        startActivity(intent)
        finish()
    }

    fun isFirstTimeStartApp():Boolean {
        var ref: SharedPreferences = application.getSharedPreferences("IntroSliderApp", Context.MODE_PRIVATE)
        return ref.getBoolean("FirstTimeStartFlag", true)
    }

    fun setFirstTimeStartStatus(stt: Boolean){
        var ref: SharedPreferences = application.getSharedPreferences("IntroSliderApp", Context.MODE_PRIVATE)
        var editor = ref.edit()
        editor.putBoolean("FirstTimeStartFlag", stt)
        editor.commit()
    }

}