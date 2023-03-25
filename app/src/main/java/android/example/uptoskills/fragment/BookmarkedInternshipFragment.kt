package android.example.uptoskills.fragment

import android.content.Intent
import android.example.uptoskills.Adapters.BookmarkedInternshipAdapter
import android.example.uptoskills.Adapters.JobItemClicked
import android.example.uptoskills.Adapters.MyJobAdapter
import android.example.uptoskills.JobViewActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.uptoskills.R
import android.example.uptoskills.daos.InternshipDao
import android.example.uptoskills.daos.JobDao
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.models.Internship
import android.example.uptoskills.models.Job
import android.example.uptoskills.models.Users
import android.os.Build
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BookmarkedInternshipFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BookmarkedInternshipFragment : Fragment(), JobItemClicked, onJobSearch {
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

    private lateinit var userDao: UsersDao
    private lateinit var auth: FirebaseAuth
    private lateinit var curUser: Users
    private lateinit var jobDao: InternshipDao
    private val myJobs: ArrayList<Job> = ArrayList()
    private lateinit var adapter: BookmarkedInternshipAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var noJobs: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bookmarked_internship, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        userDao = UsersDao()
        auth = FirebaseAuth.getInstance()
        jobDao = InternshipDao()
        progressBar = view.findViewById(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        noJobs = view.findViewById(R.id.noJobs)
        noJobs.visibility = View.VISIBLE
        setUpInternship()

        return view
    }

    private fun setUpInternship() {
        GlobalScope.launch(Dispatchers.IO) {
            val temp = auth.currentUser?.let { userDao.getUserById(it.uid).await().toObject(Users::class.java) }
            if(temp != null) {
                curUser = temp
                val jobList: HashMap<String, String>? = curUser.bookmarks
                if (jobList != null) {
                    if(jobList.isNotEmpty()) {
                        jobList.forEach { (s, s2) ->
                            Log.e(s, s2)
                            if (s2.lowercase() == "internship") {
                                val job: Job? =
                                    jobDao.getJobbyId(s).await().toObject(Job::class.java)
                                if (job != null) {
                                    myJobs.add(job)
                                }
                            }
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    view?.let { setUprecyclerView(it) }
                }
            }
        }
    }


    private fun setUprecyclerView(view: View) {
        adapter = BookmarkedInternshipAdapter(view.context, this, R.layout.internship_item, curUser.id)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        adapter.updateJobs(myJobs)
        progressBar.visibility = View.GONE
        if(myJobs.size != 0) {
            noJobs.visibility = View.GONE
        }
    }

    override fun onJobCLick(jobId: String) {
        val intent = Intent(view?.context, JobViewActivity::class.java)
        intent.putExtra("jobId", jobId)
        intent.putExtra("category", "internship")
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onbookmarkCLick(itemId: String, itemtype: String): Boolean {
        var idBookmarked = true
        if(curUser.bookmarks?.containsKey(itemId) == true && curUser.bookmarks!![itemId] == itemtype) {
            idBookmarked = false
            curUser.bookmarks!!.remove(itemId, itemtype)
        } else {
            curUser.bookmarks?.set(itemId, itemtype)
        }
        jobDao.addbookmark(itemId)
        auth.currentUser?.let { userDao.updateUser(curUser, it.uid) }
        return idBookmarked
    }

    override fun shareJob(itemId: String, itemtype: String) {

    }

    override fun updateRecyclerView(query: String) {
        if(query.trim().isEmpty()) {
            noJobs.visibility = View.GONE
            adapter.updateJobs(myJobs)
        } else {
            val newjobs = ArrayList<Job>()
            myJobs.forEach {
                if(it.category.contains(query.trim(), true) ||
                    it.title.contains(query.trim(), true) ||
                    it.company_name.contains(query.trim(), true)) {
                    newjobs.add(it)
                }
            }
            Log.e("Internship", newjobs.size.toString())
            if(newjobs.size != 0) {
                adapter.updateJobs(newjobs)
                noJobs.visibility = View.GONE
            } else {
                noJobs.visibility = View.VISIBLE
//                Toast.makeText(view?.context, "No Job Found...", Toast.LENGTH_SHORT).show()
                adapter.updateJobs(newjobs)
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BookmarkedInternshipFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BookmarkedInternshipFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}