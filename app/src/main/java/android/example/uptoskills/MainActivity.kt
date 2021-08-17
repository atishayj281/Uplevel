package android.example.uptoskills

import android.content.Intent
import android.example.uptoskills.Fragment.*
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.databinding.ActivityMainBinding
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.facebook.FacebookSdk
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks


class MainActivity : AppCompatActivity(), onMenuItemSelectedListener{

    private var displayName: String = ""
    private lateinit var binding: ActivityMainBinding
    private lateinit var userDao: UsersDao
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, HomeFragment())
        transaction.commit()

        setContentView(binding.root)
        invalidateOptionsMenu()

        userDao = UsersDao()
        auth = FirebaseAuth.getInstance()

        binding.bottomNavigation.setOnNavigationItemSelectedListener {

            val transaction = supportFragmentManager.beginTransaction()
            when(it.itemId){
                R.id.home -> {
                    transaction.replace(R.id.container, HomeFragment())
                }
                R.id.Job -> {
                    transaction.replace(R.id.container, JobFragment())
                }
                R.id.courses -> {
                    transaction.replace(R.id.container, CourseFragment())
                }
                R.id.Blog -> {
                    transaction.replace(R.id.container, BlogFragment())
                }
            }

            transaction.commit()
            true
        }

//        binding.hometoolBar.setOnClickListener {
//            binding.homedrawerLayout.openDrawer(GravityCompat.START)
//        }

//        binding.homeNavigationView.setNavigationItemSelectedListener { menuItem ->
//            // Handle menu item selected
//            menuItem.isChecked = true
//            binding.homedrawerLayout.closeDrawer(GravityCompat.START)
//
//            when(menuItem.itemId){
//                R.id.Job -> binding.bottomNavigation.selectedItemId = R.id.Job
//                R.id.courses -> binding.bottomNavigation.selectedItemId = R.id.courses
//                R.id.MyCourses -> {
//                    val intent = Intent(this, MyCourseActivity::class.java)
//                    startActivity(intent)
//                }
//                R.id.certificate -> {
//
//                }
//                R.id.LogOut ->{
//                    val gso =
//                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
//
//                    val googleSignInClient = GoogleSignIn.getClient(this, gso)
//                    googleSignInClient.signOut()
//                        .addOnCompleteListener(object : OnCompleteListener<Void?> {
//                            override fun onComplete(task: Task<Void?>) {
//                                if (task.isSuccessful) {
//                                    FirebaseAuth.getInstance()
//                                        .signOut() // very important if you are using firebase.
//                                    val login_intent = Intent(FacebookSdk.getApplicationContext(),
//                                        SignInActivity::class.java)
//                                    login_intent.flags =
//                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // clear previous task (optional)
//                                    startActivity(login_intent)
//                                    finish()
//                                }
//                            }
//                        })
//                }
//                R.id.privacyPolicy -> {
//                    val intent = Intent(this, PrivacyPolicyActivity::class.java)
//                    startActivity(intent)
//                }
//
//                R.id.share -> {
//                    auth.currentUser?.let { createReferLink(it.uid, "1234533dsd") }
//                }
//
//                R.id.bookmark ->
//                {
//                    val intent = Intent(this, BookmarkActivity::class.java)
//                    startActivity(intent)
//                }
//
//            }
//            menuItem.isChecked = false
//
//
//            true
//        }



//        //set NavigationViewheader
//                if(!this.isDestroyed){
//                    setUpNavigationViewHeader()
//                }

    }


    // create referral link
    private fun createReferLink(uId: String, productId: String) {
        var link: String = "https://uptoskills.page.link/?"+
                "link=https://www.uptoskills.com/myrefer.php?uId="+uId+"-"+productId+
                "&apn="+packageName+
                "&st=Join me on UptoSkills"+
                "&sd=Reward UsCash 500"+
                "&si=https://www.uptoskills.com/wp-content/uploads/2019/10/logo-dark.png"

        // https://uptoskills.page.link?apn=android.example.getwork&ibi=com.example.ios&link=https%3A%2F%2Fwww.uptoskills.com%2F
        Log.e("sharelink", link)
        // shorten the link

        val shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse(link))
            .setDomainUriPrefix("https://uptoskills.page.link") // Set parameters
            // ...
            .buildShortDynamicLink()
            .addOnCompleteListener(this
            ) { task ->
                if (task.isSuccessful) {
                    // Short link created
                    val shortLink = task.result.shortLink
                    val flowchartLink = task.result.previewLink
                    Log.e("short link", ""+shortLink)

                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
                    intent.type = "text/plain"
                    startActivity(intent)
                    // ------ click -> link -> google play store -> installed/not ---------

                } else {
                    // Error
                    // ...
                }
            }
//                    Log.e("long link", ""+dynamicLinkUri)
    }


    override fun onBackPressed() {
        if(binding.bottomNavigation.selectedItemId == R.id.home){
            super.onBackPressed()
        } else {
            binding.bottomNavigation.selectedItemId = R.id.home
        }

    }

    override fun onItemSelected(itemId: Int) {
        binding.bottomNavigation.selectedItemId = itemId
    }
}

