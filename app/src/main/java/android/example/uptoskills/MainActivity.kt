package android.example.uptoskills

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.example.uptoskills.fragment.*
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.databinding.ActivityMainBinding
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import android.net.Uri


class MainActivity : AppCompatActivity(), onMenuItemSelectedListener, onNavDrawerListener{

    private lateinit var binding: ActivityMainBinding
    private lateinit var userDao: UsersDao
    private lateinit var auth: FirebaseAuth
    private final val PRODUCT = "1"


    companion object {
        var selectedItemData: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        FirebaseMessaging.getInstance().subscribeToTopic("Notification")
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PackageManager.PERMISSION_GRANTED)
        setJOB()
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
                R.id.events -> {
                    transaction.replace(R.id.container, EventFragment())
                }
            }

            transaction.commit()
            true
        }
        // ATTENTION: This was auto-generated to handle app links.
        val appLinkIntent: Intent = intent
        val appLinkAction: String? = appLinkIntent.action
        val appLinkData: Uri? = appLinkIntent.data
    }

    private fun setJOB() {
        val productID = intent.getStringExtra(PRODUCT);
        if(productID != null && productID.trim().isNotEmpty()) {
            val product: List<String> = productID.split("#")
            val intent = Intent(this, JobViewActivity::class.java)
            try {
                intent.putExtra("category", product[0]);
                intent.putExtra("jobId", product[1]);
            } catch (e: Exception) {}
            startActivity(intent)
        }
    }

    private var backPressed = 2

    override fun onBackPressed() {
        if(backPressed > 1) {
            Toast.makeText(this, "Press back one more time to exit", Toast.LENGTH_SHORT).show()
            backPressed--
        }
        if(binding.bottomNavigation.selectedItemId == R.id.home){
            super.onBackPressed()
        } else {
            binding.bottomNavigation.selectedItemId = R.id.home
        }

    }

    override fun onItemSelected(itemId: Int, data: String) {
        selectedItemData = data
        binding.bottomNavigation.selectedItemId = itemId
    }

    override fun onDrawerVisible(isVisible: Boolean) {
        binding.bottomNavigation.isVisible = !isVisible
    }
}

