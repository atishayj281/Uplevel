package android.example.uptoskills

import android.Manifest
import android.content.pm.PackageManager
import android.example.uptoskills.Fragment.*
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.databinding.ActivityMainBinding
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity(), onMenuItemSelectedListener{

    private lateinit var binding: ActivityMainBinding
    private lateinit var userDao: UsersDao
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        FirebaseMessaging.getInstance().subscribeToTopic("Notification")
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PackageManager.PERMISSION_GRANTED)
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

