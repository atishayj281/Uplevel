package android.example.uptoskills

import android.example.uptoskills.Adapters.CourseItemClicked
import android.example.uptoskills.Adapters.CourseViewPagerAdapter
import android.example.uptoskills.Fragment.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MyCourseActivity : AppCompatActivity(), CourseItemClicked {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var backbtn: ImageView
    private lateinit var search: EditText
    private lateinit var freeCourseFragment: MyFreeCourseFragment
    private lateinit var paidCourseFragment: MyPaidCourseFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_course)

        tabLayout = findViewById(R.id.courseTabLayout)
        viewPager = findViewById(R.id.courseViewPager)
        backbtn = findViewById(R.id.back)
        search = findViewById(R.id.search)

        tabLayout.post { tabLayout.setupWithViewPager(viewPager) }

        freeCourseFragment = MyFreeCourseFragment()
        paidCourseFragment = MyPaidCourseFragment()

        var adapter = CourseViewPagerAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        adapter.addFragment(freeCourseFragment, "Free")
        adapter.addFragment(paidCourseFragment, "Paid")
        viewPager.adapter = adapter


        search.setOnKeyListener { view, i, keyEvent ->
            if((keyEvent.action == KeyEvent.ACTION_DOWN) &&
                (i == KeyEvent.KEYCODE_ENTER)) {
                    freeCourseFragment.updateRecyclerView(search.text.toString())
                    paidCourseFragment.updateRecyclerView(search.text.toString())
                search.text.clear()
                true
            }
            false
        }

        backbtn.setOnClickListener {
            finish()
        }
    }

    override fun onCourseCLick(courseId: String) {

    }

}