package android.example.UptoSkills.Fragment

import android.content.Intent
import android.example.UptoSkills.Adapters.*
import android.example.UptoSkills.BlogViewActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.UptoSkills.R
import android.example.UptoSkills.daos.BlogDao
import android.example.UptoSkills.daos.UsersDao
import android.example.UptoSkills.models.Blog
import android.example.UptoSkills.models.Course
import android.example.UptoSkills.models.Job
import android.example.UptoSkills.models.Users
import android.net.Uri
import android.widget.ProgressBar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.blogrecyclerview)
        progressBar = view.findViewById(R.id.homeProgressbar)

        progressBar.visibility = View.VISIBLE
        courseRecyclerView = view.findViewById(R.id.Courserecyclerview)
        courseAdapter = CourseAdapter(view.context, this)
        courseRecyclerView.adapter = courseAdapter
        courseRecyclerView.layoutManager = LinearLayoutManager(view?.context, LinearLayoutManager.HORIZONTAL, false)

        setUpJobRecyclerView(view)
        setUpBlogRecyclerView()
        setUpProfileRecyclerView(view)
        progressBar.visibility = View.GONE
        return view
    }



    private fun setUpJobRecyclerView(view: View) {
        jobRecyclerView = view.findViewById(R.id.HomeJobrecyclerview)!!
        jobAdapter = JobAdapter(view.context, this)
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
        profileRecyclerView.layoutManager = LinearLayoutManager(view?.context, LinearLayoutManager.HORIZONTAL, false)
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
        val query = postsCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Blog>().setQuery(query, Blog::class.java).build()

        adapter = BlogsAdapter(recyclerViewOptions, this)

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

