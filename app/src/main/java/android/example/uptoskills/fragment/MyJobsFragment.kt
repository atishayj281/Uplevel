package android.example.uptoskills.fragment

import android.content.Intent
import android.example.uptoskills.Adapters.JobItemClicked
import android.example.uptoskills.Adapters.MyJobAdapter
import android.example.uptoskills.JobViewActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.uptoskills.R
import android.example.uptoskills.daos.JobDao
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.models.Job
import android.example.uptoskills.models.UserJobDetails
import android.example.uptoskills.models.Users
import android.widget.ProgressBar
import android.widget.TextView
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
 * Use the [MyJobsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyJobsFragment : Fragment(), JobItemClicked, onJobSearch {
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
    private lateinit var jobDao: JobDao
    private val myJobs: ArrayList<Job> = ArrayList()
    private lateinit var adapter: MyJobAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var noJobs: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_jobs, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        userDao = UsersDao()
        auth = FirebaseAuth.getInstance()
        jobDao = JobDao()
        progressBar = view.findViewById(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        noJobs = view.findViewById(R.id.noJobs)
        noJobs.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.IO) {
            val temp = auth.currentUser?.let { userDao.getUserById(it.uid).await().toObject(Users::class.java) }
            if(temp != null) {
                curUser = temp
                val jobList: HashMap<String, UserJobDetails>? = curUser.appliedJobs
                if (jobList != null) {
                    if(jobList.isNotEmpty()) {
                        jobList.forEach { (s, s2) ->
                            if (s2.jobType == "Job") {
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
                    setUprecyclerView(view)
                }
            }
        }
        return view
    }

    private fun setUprecyclerView(view: View) {
        adapter = MyJobAdapter(view.context, this, R.layout.internship_item, curUser.id)
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
        intent.putExtra("category", "job")
        intent.putExtra("parent", "applied")
        startActivity(intent)
    }

    override fun onbookmarkCLick(itemId: String, itemtype: String): Boolean {
        return false
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
}