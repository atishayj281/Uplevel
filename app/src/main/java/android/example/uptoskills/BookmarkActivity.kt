package android.example.uptoskills

import android.content.Intent
import android.example.uptoskills.Adapters.BookmarkedJobAdapter
import android.example.uptoskills.Adapters.CourseViewPagerAdapter
import android.example.uptoskills.Adapters.JobItemClicked
import android.example.uptoskills.Adapters.MyJobAdapter
import android.example.uptoskills.daos.JobDao
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.fragment.BookmarkedInternshipFragment
import android.example.uptoskills.fragment.BookmarkedJobFragment
import android.example.uptoskills.fragment.MyInternshipsFragment
import android.example.uptoskills.fragment.MyJobsFragment
import android.example.uptoskills.models.Job
import android.example.uptoskills.models.Users
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BookmarkActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var usersDao: UsersDao
    private lateinit var auth: FirebaseAuth
    private lateinit var jobDao: JobDao
    private lateinit var adapter: BookmarkedJobAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var noBookmark: TextView
    private lateinit var backbtn: ImageView
    private lateinit var curUser: Users
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var search: EditText
    private lateinit var myJobsFragment: BookmarkedJobFragment
    private lateinit var myInternshipsFragment: BookmarkedInternshipFragment

    private fun MapUI(){

//        recyclerView = findViewById(R.id.recycler_view)
        usersDao = UsersDao()
        auth = FirebaseAuth.getInstance()
        jobDao = JobDao()
//        progressBar = findViewById(R.id.progress_bar)
//        noBookmark = findViewById(R.id.noBookmark)
//        backbtn = findViewById(R.id.back)
//        progressBar.visibility = View.VISIBLE
//        adapter = BookmarkedJobAdapter(this@BookmarkActivity, this@BookmarkActivity)
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(this@BookmarkActivity)
//        swipeRefreshLayout = findViewById(R.id.swiperefresh)

        tabLayout = findViewById(R.id.courseTabLayout)
        viewPager = findViewById(R.id.courseViewPager)
        backbtn = findViewById(R.id.back)
//        search = findViewById(R.id.search)
    }

    private fun getUser() {
        GlobalScope.launch(Dispatchers.IO) {
            curUser = auth.currentUser?.uid?.let { usersDao.getUserById(it).await().toObject(Users::class.java) }!!
        }
    }

    private fun setUpSwipeRefreshLayout(){
        swipeRefreshLayout.setOnRefreshListener {
            GlobalScope.launch(Dispatchers.IO) {
                curUser = auth.currentUser?.uid?.let { usersDao.getUserById(it).await().toObject(Users::class.java) }!!
                withContext(Dispatchers.Main) {
                    setUpBokmarksView()
                }
            }
            swipeRefreshLayout.handler
                .postDelayed(Runnable { swipeRefreshLayout.isRefreshing = false }, 2000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)
        MapUI();
        getUser();
//        setUpSwipeRefreshLayout()
//        setUpBokmarksView()
        setUpFragment()


        backbtn.setOnClickListener {
            finish()
        }
    }

    private fun setUpFragment() {
        myInternshipsFragment = BookmarkedInternshipFragment()
        myJobsFragment = BookmarkedJobFragment()

        tabLayout.post { tabLayout.setupWithViewPager(viewPager) }

        var adapter = CourseViewPagerAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        adapter.addFragment(myJobsFragment, "Jobs")
        adapter.addFragment(myInternshipsFragment, "Internships")
        viewPager.adapter = adapter
    }

    private fun setUpBokmarksView() {
        GlobalScope.launch(Dispatchers.IO) {
            val temp = auth.currentUser?.let { usersDao.getUserById(it.uid).await().toObject(Users::class.java) }
            if(temp != null) {
                val bookmark = temp.bookmarks
                val bookmarks = ArrayList<Job>()
                bookmark?.forEach {
                    if(it.value.equals("job")) {
                        val jobId: String = it.key
                        val job: Job = jobDao.getJobbyId(jobId).await().toObject(Job::class.java)!!
                        bookmarks.add(job)
                    }
                }
                withContext(Dispatchers.Main) {
                    adapter.updateJobs(bookmarks)
                    if(bookmarks.isEmpty()) {
                        noBookmark.visibility = View.VISIBLE
                    } else {
                        noBookmark.visibility = View.GONE
                    }
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun onBackPressed() {
        if(parent == null) {
            val intnt = Intent(this, MainActivity::class.java)
            startActivity(intnt)
            finish()
        }
        super.onBackPressed()
    }

}