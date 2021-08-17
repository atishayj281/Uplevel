package android.example.uptoskills

import android.example.uptoskills.Adapters.CourseItemClicked
import android.example.uptoskills.Adapters.CourseViewPagerAdapter
import android.example.uptoskills.Fragment.MyFreeCourseFragment
import android.example.uptoskills.Fragment.MyPaidCourseFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MyCourseActivity : AppCompatActivity(), CourseItemClicked {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var backbtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_course)

        tabLayout = findViewById(R.id.courseTabLayout)
        viewPager = findViewById(R.id.courseViewPager)
        backbtn = findViewById(R.id.back)


        tabLayout.post { tabLayout.setupWithViewPager(viewPager) }

        var adapter = CourseViewPagerAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        adapter.addFragment(MyFreeCourseFragment(), "Free")
        adapter.addFragment(MyPaidCourseFragment(), "Paid")
        viewPager.adapter = adapter


        backbtn.setOnClickListener {
            finish()
        }
    }

    override fun onCourseCLick(courseId: String) {



    }
}