package android.example.UptoSkills

import android.example.UptoSkills.Fragment.*
import android.example.UptoSkills.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, HomeFragment())
        transaction.commit()

        setContentView(binding.root)

        binding.bottomNavigation.setOnNavigationItemSelectedListener {

            var transaction = supportFragmentManager.beginTransaction()

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
}

