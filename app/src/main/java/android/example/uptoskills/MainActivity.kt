package android.example.uptoskills

import android.example.uptoskills.Fragment.*
import android.example.uptoskills.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, HomeFragment())
        transaction.commit()

        setContentView(binding.root)

        binding.bottomNavigation.setOnNavigationItemSelectedListener {

            val transaction = supportFragmentManager.beginTransaction()

            when(it.itemId){
                R.id.home -> transaction.replace(R.id.container, HomeFragment())
                R.id.Job -> transaction.replace(R.id.container, JobFragment())
                R.id.courses -> transaction.replace(R.id.container, CourseFragment())
                R.id.Blog -> transaction.replace(R.id.container, BlogFragment())
                R.id.More -> transaction.replace(R.id.container, MoreFragment())
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
}

