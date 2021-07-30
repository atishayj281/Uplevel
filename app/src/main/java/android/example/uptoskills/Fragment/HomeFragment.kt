package android.example.uptoskills.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.example.uptoskills.*
import android.example.uptoskills.Adapters.*
import android.example.uptoskills.daos.BlogDao
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.models.Blog
import android.example.uptoskills.models.Course
import android.example.uptoskills.models.Job
import android.example.uptoskills.models.Users
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
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
class HomeFragment : Fragment(), IBlogAdapter, CourseItemClicked, JobItemClicked, IProfileAdapter {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    interface onMenuItemSelectedListener{
        fun onItemSelected(itemId: Int)
    }

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
    private lateinit var profile: ImageView
    private lateinit var displayName: String
    private lateinit var toolbar: MaterialToolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    var menuItemSelectedId: Int = -1
    private lateinit var menuItemSelectedListener: onMenuItemSelectedListener

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
        var view = inflater.inflate(R.layout.fragment_home, container, false)

        toolbar = view.findViewById(R.id.hometoolBar)
        drawerLayout = view.findViewById(R.id.homedrawerLayout)

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView = view.findViewById(R.id.homeNavigationView)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            menuItem.isChecked = true
            drawerLayout.closeDrawer(GravityCompat.START)

            when(menuItem.itemId){
                R.id.home -> menuItemSelectedListener.onItemSelected(R.id.home)
                R.id.Job -> menuItemSelectedListener.onItemSelected(R.id.Job)
                R.id.courses -> menuItemSelectedListener.onItemSelected(R.id.courses)
                R.id.Blog -> menuItemSelectedListener.onItemSelected(R.id.Blog)
                R.id.LogOut ->{
                    FirebaseAuth.getInstance().signOut()
                    var intent = Intent(activity, SignInActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
            }

            true
        }

        recyclerView = view.findViewById(R.id.blogrecyclerview)
        progressBar = view.findViewById(R.id.homeProgressbar)
        profile = view.findViewById(R.id.profileImage)

        progressBar.visibility = View.VISIBLE
        courseRecyclerView = view.findViewById(R.id.Courserecyclerview)
        courseAdapter = CourseAdapter(view.context, this, R.layout.home_course_item)
        courseRecyclerView.adapter = courseAdapter
        courseRecyclerView.layoutManager = LinearLayoutManager(view?.context, LinearLayoutManager.HORIZONTAL, false)

        setUpJobRecyclerView(view)
        setUpBlogRecyclerView()
        setUpProfileRecyclerView(view)
        progressBar.visibility = View.GONE

        // Initialising "view All" textViews
        var allCourses = view.findViewById<TextView>(R.id.viewAllCourses)
        var allBlogs = view.findViewById<TextView>(R.id.viewAllBlogs)
        var allJobs = view.findViewById<TextView>(R.id.viewAllJobs)
        var allProfiles = view.findViewById<TextView>(R.id.viewAllProfiles)

        // Set OnClickListeners
        allCourses.setOnClickListener {
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
            activity?.finish()
        }

        profile.setOnClickListener {
            var intent = Intent(activity, UserDetailsActivity::class.java)
            intent.putExtra("username", displayName )
            intent.putExtra("id", FirebaseAuth.getInstance().currentUser?.uid.toString())
            intent.putExtra("parent", "MoreFragment")
            startActivity(intent)
        }

        // setUpProfileImage
        setUpProfileImage()
        return view
    }

    private fun setUpProfileImage() {
        userDao = UsersDao()

        userDao.ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImage = snapshot.child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("userImage").getValue(String::class.java).toString()
                displayName = snapshot.child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("displayName").getValue(String::class.java).toString()
                if(profileImage.isNotEmpty() && !profileImage.equals("null")){
                    profile.setImageResource(R.drawable.image_circle)
                    view?.context?.let { Glide.with(it).load(profileImage).circleCrop().into(profile) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }


    private fun setUpJobRecyclerView(view: View) {
        jobRecyclerView = view.findViewById(R.id.HomeJobrecyclerview)!!
        jobAdapter = JobAdapter(view.context, this, R.layout.home_job_item)
        jobRecyclerView.adapter = jobAdapter
        jobRecyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
    }


    private fun setUpProfileRecyclerView(view: View?) {
        userDao = UsersDao()
        var profiles: ArrayList<Users> = ArrayList()
        if (view != null) {
            profileRecyclerView = view.findViewById(R.id.HomeProfilesrecyclerview)
        }
        profileAdapter = view?.let { ProfileAdapter(it.context, this) }!!
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
                TODO("Not yet implemented")
            }

        })
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
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

    override fun onCourseCLick(course: Course) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        view?.let { customTabsIntent.launchUrl(it.context, Uri.parse(course.url)) }
    }

    override fun onJobCLick(job: Job) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        view?.let { customTabsIntent.launchUrl(it.context, Uri.parse(job.url)) }
    }

    override fun onProfileClicked(uid: String) {
        TODO("Not yet implemented")
    }
}
