package android.example.uptoskills

import android.content.Intent
import android.example.uptoskills.Adapters.CourseViewPagerAdapter
import android.example.uptoskills.fragment.MyInternshipsFragment
import android.example.uptoskills.fragment.MyJobsFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MyJobsActivity : AppCompatActivity() {


    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var backbtn: ImageView
    private lateinit var search: EditText
    private lateinit var myJobsFragment: MyJobsFragment
    private lateinit var myInternshipsFragment: MyInternshipsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_jobs)


        tabLayout = findViewById(R.id.courseTabLayout)
        viewPager = findViewById(R.id.courseViewPager)
        backbtn = findViewById(R.id.back)
        search = findViewById(R.id.search)
        myInternshipsFragment = MyInternshipsFragment()
        myJobsFragment = MyJobsFragment()

        tabLayout.post { tabLayout.setupWithViewPager(viewPager) }

        var adapter = CourseViewPagerAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        adapter.addFragment(myJobsFragment, "Jobs")
        adapter.addFragment(myInternshipsFragment, "Internships")
        viewPager.adapter = adapter

        search.setOnKeyListener { view, i, keyEvent ->
            if(keyEvent.action == KeyEvent.ACTION_DOWN
                && i == KeyEvent.KEYCODE_ENTER) {
                myInternshipsFragment.updateRecyclerView(search.text.toString())
                myJobsFragment.updateRecyclerView(search.text.toString())
                search.text.clear()
            }
            false
        }


        backbtn.setOnClickListener {
            finish()
        }


    }

    override fun onBackPressed() {
        if(parent == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        super.onBackPressed()
    }
}