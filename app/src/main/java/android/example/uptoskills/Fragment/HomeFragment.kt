package android.example.uptoskills.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.example.uptoskills.*
import android.example.uptoskills.Adapters.*
import android.example.uptoskills.daos.*
import android.example.uptoskills.models.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.facebook.FacebookSdk
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


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

class  HomeFragment : Fragment(), IBlogAdapter, CourseItemClicked, JobItemClicked, paidCourseclicked {
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
    var menuItemSelectedId: Int = -1
    private lateinit var menuItemSelectedListener: onMenuItemSelectedListener
    private lateinit var auth: FirebaseAuth
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var courseDao: CourseDao
    private lateinit var jobDao: JobDao
    private lateinit var paidcourseAdapter: PaidCourseAdapter
    private lateinit var paidCourseDao: PaidCourseDao
    private lateinit var paidCourseRecyclerView: RecyclerView
    private lateinit var courseEnquirybtn: MaterialCardView
    private lateinit var courseEnqClosebtn: MaterialTextView
    private lateinit var courseEnquiryLayout: MaterialCardView
    private lateinit var popUpback: TextView
    private lateinit var popUpLayout: FrameLayout
    private lateinit var popUpKnowMore: MaterialCardView
    private lateinit var referandEarnCardView: MaterialCardView
    private var curUser: Users = Users()
    private lateinit var hometoolBar: MaterialToolbar
    private lateinit var homedrawerLayout: DrawerLayout
    private lateinit var homeNavigationView: NavigationView
    private var displayName: String = ""
    private lateinit var menuBar: ImageView

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

        hometoolBar = view.findViewById(R.id.hometoolBar)
        homedrawerLayout = view.findViewById(R.id.homedrawerLayout)
        homeNavigationView = view.findViewById(R.id.homeNavigationView)
        menuBar = view.findViewById(R.id.menuBar)


        courseEnqClosebtn = view.findViewById(R.id.delNotification)
        courseEnquirybtn = view.findViewById(R.id.courseEnquiry)
        courseEnquiryLayout = view.findViewById(R.id.courseEnquiryLayout)

        popUpback = view.findViewById(R.id.popUpback)
        popUpLayout = view.findViewById(R.id.popUpLayout)
        popUpKnowMore = view.findViewById(R.id.popUpKnowMore)

        referandEarnCardView = view.findViewById(R.id.referEarn)

        paidCourseDao = PaidCourseDao()
        paidCourseRecyclerView = view.findViewById(R.id.PaidCourserecyclerview)

        refreshLayout = view.findViewById(R.id.swiperefresh)

        auth = FirebaseAuth.getInstance()
        userDao = UsersDao()

        recyclerView = view.findViewById(R.id.blogrecyclerview)
        progressBar = view.findViewById(R.id.homeProgressbar)
        jobRecyclerView = view.findViewById(R.id.HomeJobrecyclerview)


        progressBar.visibility = View.VISIBLE
        menuBar.setOnClickListener {
            homedrawerLayout.openDrawer(GravityCompat.START)
        }

        setUpJobRecyclerView(view)
        setUpBlogRecyclerView()
        setUpcourses(view)
        setUppaidCourses(view)

        progressBar.visibility = View.GONE

        // Initialising "view All" textViews
        val allFreeCourses = view.findViewById<TextView>(R.id.viewFreeAllCourses)
        val allPaidCourses = view.findViewById<TextView>(R.id.viewAllPaidCourses)
        val allBlogs = view.findViewById<TextView>(R.id.viewAllBlogs)
        val allJobs = view.findViewById<TextView>(R.id.viewAllJobs)

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

        // Refreshing fragment
        refreshLayout.setOnRefreshListener {
            refreshLayout.handler
                .postDelayed(Runnable { refreshLayout.isRefreshing = false }, 2000)
        }

        courseEnqClosebtn.setOnClickListener {
            courseEnquiryLayout.visibility = View.GONE
        }

        courseEnquirybtn.setOnClickListener {
            val intent = Intent(activity, CourseEnquiryActivity::class.java)
            startActivity(intent)
        }

        popUpback.setOnClickListener {
            popUpLayout.visibility = View.GONE
        }

        popUpKnowMore.setOnClickListener {
            
        }

        referandEarnCardView.setOnClickListener {
            auth.currentUser?.let { createReferLink(it.uid, "1234533dsd") }
        }

        GlobalScope.launch(Dispatchers.IO) {
            val temp = auth.currentUser?.uid?.let { userDao.getUserById(it).await().toObject(Users::class.java) }
            if(temp != null) {
                curUser = temp
                displayName = curUser.displayName.toString()
            }
        }

        homeNavigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            menuItem.isChecked = true
            homedrawerLayout.closeDrawer(GravityCompat.START)

            when(menuItem.itemId){
                R.id.MyCourses -> {
                    val intent = Intent(activity, MyCourseActivity::class.java)
                    startActivity(intent)
                }
                R.id.LogOut ->{
                    val gso =
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()

                    val googleSignInClient = activity?.let { GoogleSignIn.getClient(it, gso) }
                    googleSignInClient?.signOut()?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            FirebaseAuth.getInstance()
                                .signOut() // very important if you are using firebase.
                            val login_intent = Intent(FacebookSdk.getApplicationContext(),
                                SignInActivity::class.java)
                            login_intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // clear previous task (optional)
                            startActivity(login_intent)
                            activity?.finish()
                        }
                    }
                }
                R.id.privacyPolicy -> {
                    val intent = Intent(activity, PrivacyPolicyActivity::class.java)
                    startActivity(intent)
                }

                R.id.share -> {
                    auth.currentUser?.let { createReferLink(it.uid, "1234533dsd") }
                }

                R.id.bookmark ->
                {
                    val intent = Intent(activity, BookmarkActivity::class.java)
                    startActivity(intent)
                }

                R.id.appliedJobs -> {
                    val intent = Intent(activity, MyJobsActivity::class.java)
                    startActivity(intent)
                }

                R.id.certificate -> {
                    val intent = Intent(activity, CertificateActivity::class.java)
                    startActivity(intent)
                }

            }
            menuItem.isChecked = false


            true
        }

        // setUp Navigation header Profile click
        val headerView: View = homeNavigationView.getHeaderView(0)
        headerView.findViewById<MaterialCardView>(R.id.profile).setOnClickListener {
            var intent = Intent(activity, UserDetailsActivity::class.java)
            intent.putExtra("username", displayName )
            intent.putExtra("id", FirebaseAuth.getInstance().currentUser?.uid.toString())
            intent.putExtra("parent", "MoreFragment")
            startActivity(intent)
        }


        return view
    }

    private fun setUpNavigationViewHeader(activity: Activity) {
        val navigationView = activity.findViewById<NavigationView>(R.id.homeNavigationView)
        val headerView: View = navigationView.getHeaderView(0)
        val username: TextView = headerView.findViewById(R.id.username)
        val email: TextView = headerView.findViewById(R.id.email)
        var profile: ImageView = headerView.findViewById(R.id.headerProfileImage)
        val coins: TextView = headerView.findViewById(R.id.coins)


        GlobalScope.launch(Dispatchers.IO) {
            val temp = auth.currentUser?.let { userDao.getUserById(it.uid).await().toObject(Users::class.java) }
            if(temp != null) {
                withContext(Dispatchers.Main) {
                    username.text = temp.displayName
                    displayName = temp.displayName.toString()
                    email.text = temp.email
                    coins.text = temp.coins.toString()
                    val profileImage = temp.userImage
                    if(profileImage.trim().isNotBlank() && profileImage != "null") {
                        profile.setImageResource(R.drawable.image_circle)
                        view?.context?.let { Glide.with(it).load(profileImage).circleCrop().into(profile) }
                    }

                }
            }
        }


    }

    // create referral link
    private fun createReferLink(uId: String, productId: String) {
        var link: String = "https://uptoskills.page.link/?"+
                "link=https://www.uptoskills.com/myrefer.php?uId="+uId+"-"+productId+
                "&apn="+activity?.packageName+
                "&st=Join me on UptoSkills"+
                "&sd=Reward UsCash 500"+
                "&si=https://www.uptoskills.com/wp-content/uploads/2019/10/logo-dark.png"

        // https://uptoskills.page.link?apn=android.example.getwork&ibi=com.example.ios&link=https%3A%2F%2Fwww.uptoskills.com%2F
        Log.e("sharelink", link)
        // shorten the link

        val shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse(link))
            .setDomainUriPrefix("https://uptoskills.page.link") // Set parameters
            // ...
            .buildShortDynamicLink()
            .addOnCompleteListener(activity
            ) { task ->
                if (task.isSuccessful) {
                    // Short link created
                    val shortLink = task.result.shortLink
                    val flowchartLink = task.result.previewLink
                    Log.e("short link", ""+shortLink)

                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
                    intent.type = "text/plain"
                    startActivity(intent)
                    // ------ click -> link -> google play store -> installed/not ---------

                } else {
                    // Error
                    // ...
                }
            }
//                    Log.e("long link", ""+dynamicLinkUri)
    }

    private fun setUpcourses(view: View) {
        courseDao = CourseDao()
        courseRecyclerView = view.findViewById(R.id.FreeCourserecyclerview)
        val courseCollection = courseDao.courseCollection
        val query = courseCollection.orderBy("category", Query.Direction.DESCENDING).limitToLast(10)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<FreeCourse>().setQuery(query, FreeCourse::class.java).build()

        courseAdapter = CourseAdapter(view.context,this, R.layout.home_course_item, recyclerViewOptions)
        courseRecyclerView.adapter = courseAdapter
        courseRecyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
    }


    private fun setUpJobRecyclerView(view: View){
        jobDao = JobDao()
        val jobCollection = jobDao.jobCollection
        val query = jobCollection.limitToLast(10).orderBy("id", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Job>().setQuery(query, Job::class.java).build()

        jobAdapter = JobAdapter(recyclerViewOptions, this, R.layout.home_job_item)

        jobRecyclerView.adapter =jobAdapter
        jobRecyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
    }


    override fun onStart() {
        super.onStart()
        activity?.let { setUpNavigationViewHeader(it) }
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
        val query = courseCollection.limitToLast(10).orderBy("category", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<PaidCourse>().setQuery(query, PaidCourse::class.java).build()

        paidcourseAdapter = PaidCourseAdapter(view.context,this, R.layout.home_course_item, recyclerViewOptions)
        paidCourseRecyclerView.adapter = paidcourseAdapter
        paidCourseRecyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)

    }



    private fun setUpBlogRecyclerView() {
        postDao = BlogDao()
        val postsCollections = postDao.postCollections
        val query = postsCollections.limitToLast(10).orderBy("heading", Query.Direction.DESCENDING)
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

    override fun onBookmarkClicked(blogId: String, item: String) {

    }

    override fun onCourseCLick(courseId: String) {
        val intent = Intent(activity, CourseViewActivity::class.java)
        intent.putExtra("courseId", courseId)
        intent.putExtra("courseCategory", "free")
        startActivity(intent)
    }

    override fun onJobCLick(jobId: String) {
        val intent = Intent(view?.context, JobViewActivity::class.java)
        intent.putExtra("jobId", jobId)
        intent.putExtra("category", "job")
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onbookmarkCLick(itemId: String, itemtype: String) {
        if(curUser.bookmarks?.containsKey(itemId) == true) {
            curUser.bookmarks!!.remove(itemId, itemtype)
        } else {
            curUser.bookmarks?.set(itemId, itemtype)
        }
        jobDao.addbookmark(itemId)
        auth.currentUser?.let { userDao.updateUser(curUser, it.uid) }
        Log.e(itemtype, itemId)
    }

    override fun onpaidCourseClicked(courseId: String) {
        val intent = Intent(activity, CourseViewActivity::class.java)
        intent.putExtra("courseId", courseId)
        intent.putExtra("courseCategory", "paid")
        startActivity(intent)
    }
}

