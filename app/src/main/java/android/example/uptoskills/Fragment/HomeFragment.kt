package android.example.uptoskills.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.example.uptoskills.*
import android.example.uptoskills.Adapters.*
import android.example.uptoskills.daos.*
import android.example.uptoskills.models.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.Query


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


interface onMenuItemSelectedListener{
    fun onItemSelected(itemId: Int)
}

class HomeFragment : Fragment(), IBlogAdapter, CourseItemClicked, JobItemClicked, IProfileAdapter,
    paidCourseclicked {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var postDao: BlogDao
    private lateinit var adapter: BlogsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var courseRecyclerView: RecyclerView
    private lateinit var courseAdapter: CourseAdapter
    private lateinit var jobRecyclerView: RecyclerView
    private lateinit var jobAdapter: JobAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var userDao: UsersDao
    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var profileRecyclerView: RecyclerView
    var menuItemSelectedId: Int = -1
    private lateinit var menuItemSelectedListener: onMenuItemSelectedListener
    private lateinit var auth: FirebaseAuth
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var courseDao: CourseDao
    private lateinit var jobDao: JobDao
    private lateinit var paidcourseAdapter: PaidCourseAdapter
    private lateinit var paidCourseDao: PaidCourseDao
    private lateinit var paidCourseRecyclerView: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            var parent: Activity
            if (context is Activity) {
                parent = context
                menuItemSelectedListener = parent as onMenuItemSelectedListener
            }
        } catch (e: Exception) {

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        paidCourseDao = PaidCourseDao()
        paidCourseRecyclerView = view.findViewById(R.id.PaidCourserecyclerview)

        refreshLayout = view.findViewById(R.id.swiperefresh)

        auth = FirebaseAuth.getInstance()

        recyclerView = view.findViewById(R.id.blogrecyclerview)
        progressBar = view.findViewById(R.id.homeProgressbar)
        jobRecyclerView = view.findViewById(R.id.HomeJobrecyclerview)


        progressBar.visibility = View.VISIBLE

        setUpJobRecyclerView(view)
        setUpBlogRecyclerView()
        setUpProfileRecyclerView(view)
        setUpcourses(view)
        setUppaidCourses(view)

        progressBar.visibility = View.GONE

        // Initialising "view All" textViews
        val allFreeCourses = view.findViewById<TextView>(R.id.viewFreeAllCourses)
        val allPaidCourses = view.findViewById<TextView>(R.id.viewAllPaidCourses)
        val allBlogs = view.findViewById<TextView>(R.id.viewAllBlogs)
        val allJobs = view.findViewById<TextView>(R.id.viewAllJobs)
        val allProfiles = view.findViewById<TextView>(R.id.viewAllProfiles)

        // Set OnClickListeners
        allFreeCourses.setOnClickListener {
            menuItemSelectedListener.onItemSelected(R.id.courses)
        }
        allPaidCourses.setOnClickListener {
            menuItemSelectedListener.onItemSelected(R.id.courses)
        }
        allBlogs.setOnClickListener {
            menuItemSelectedListener.onItemSelected(R.id.Blog)
        }
        allJobs.setOnClickListener {
            menuItemSelectedListener.onItemSelected(R.id.Job)
        }
        allProfiles.setOnClickListener {
            var intent = Intent(view.context, AllProfilesActivity::class.java)
            startActivity(intent)
        }

        // Refreshing fragment
        refreshLayout.setOnRefreshListener {
            setUpJobRecyclerView(view)
            setUpProfileRecyclerView(view)
            refreshLayout.handler
                .postDelayed(Runnable { refreshLayout.isRefreshing = false }, 2000)
        }


        return view
    }

    private fun setUpcourses(view: View) {
        courseDao = CourseDao()
        courseRecyclerView = view.findViewById(R.id.FreeCourserecyclerview)
        val courseCollection = courseDao.courseCollection
        val query = courseCollection.orderBy("category", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<FreeCourse>().setQuery(query, FreeCourse::class.java).build()

        courseAdapter = CourseAdapter(view.context,this, R.layout.home_course_item, recyclerViewOptions)
        courseRecyclerView.adapter = courseAdapter
        courseRecyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
    }


    private fun setUpJobRecyclerView(view: View){
        jobDao = JobDao()
        val jobCollection = jobDao.jobCollection
        val query = jobCollection.orderBy("id", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Job>().setQuery(query, Job::class.java).build()

        jobAdapter = JobAdapter(recyclerViewOptions, this, R.layout.home_job_item)

        jobRecyclerView.adapter =jobAdapter
        jobRecyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
    }


    private fun setUpProfileRecyclerView(view: View?) {
        userDao = UsersDao()
        var profiles: ArrayList<Users> = ArrayList()
        if (view != null) {
            profileRecyclerView = view.findViewById(R.id.HomeProfilesrecyclerview)
        }
        profileAdapter = view?.let { ProfileAdapter(it.context, this, R.layout.profile) }!!
        profileRecyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        profileRecyclerView.adapter = profileAdapter

        userDao.ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                children.forEach {
                    var user = it.getValue(Users::class.java)
                    if (user != null) {
                        profiles.add(user)
                    }
                }
                profileAdapter.updateProfiles(profiles)
            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(view.context, error.message, Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
        courseAdapter.startListening()
        jobAdapter.startListening()
        paidcourseAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
        courseAdapter.stopListening()
        jobAdapter.stopListening()
        paidcourseAdapter.stopListening()
    }

    private fun setUppaidCourses(view: View){
        val courseCollection = paidCourseDao.courseCollection
        val query = courseCollection.orderBy("category", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<PaidCourse>().setQuery(query, PaidCourse::class.java).build()

        paidcourseAdapter = PaidCourseAdapter(view.context,this, R.layout.home_course_item, recyclerViewOptions)
        paidCourseRecyclerView.adapter = paidcourseAdapter
        paidCourseRecyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)

    }



    private fun setUpBlogRecyclerView() {
        postDao = BlogDao()
        val postsCollections = postDao.postCollections
        val query = postsCollections.orderBy("heading", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Blog>().setQuery(query, Blog::class.java).build()

        adapter = BlogsAdapter(recyclerViewOptions, this, R.layout.home_blog_item)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view?.context, LinearLayoutManager.HORIZONTAL, false)
    }


    override fun onBlogClicked(postId: String) {
        var intent = Intent(view?.context, BlogViewActivity::class.java)
        intent.putExtra("123", postId)
        startActivity(intent)
    }

    override fun onCourseCLick(courseId: String) {
        val intent = Intent(activity, CourseViewActivity::class.java)
        intent.putExtra("courseId", courseId)
        intent.putExtra("courseCategory", "free")
        startActivity(intent)
    }

    override fun onProfileClicked(uid: String) {
    }

    override fun onJobCLick(jobId: String) {
        val intent = Intent(view?.context, JobViewActivity::class.java)
        intent.putExtra("jobId", jobId)
        intent.putExtra("category", "job")
        startActivity(intent)
    }

    override fun onpaidCourseClicked(courseId: String) {
        val intent = Intent(activity, CourseViewActivity::class.java)
        intent.putExtra("courseId", courseId)
        intent.putExtra("courseCategory", "paid")
        startActivity(intent)
    }
}

