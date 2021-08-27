package android.example.uptoskills.Fragment

import android.content.Intent
import android.example.uptoskills.Adapters.BlogsAdapter
import android.example.uptoskills.Adapters.JobAdapter
import android.example.uptoskills.Adapters.JobItemClicked
import android.example.uptoskills.JobViewActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.uptoskills.R
import android.example.uptoskills.daos.JobDao
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.models.Blog
import android.example.uptoskills.models.Job
import android.example.uptoskills.models.Users
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
 * Use the [OnlyJobsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OnlyJobsFragment : Fragment(), JobItemClicked, onJobSearch {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JobAdapter
    private lateinit var jobDao: JobDao
    private lateinit var progressBar: ProgressBar
    private var curUser: Users = Users()
    private lateinit var userDao: UsersDao
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_only_jobs, container, false)
        auth = FirebaseAuth.getInstance()
        userDao = UsersDao()
        userDao.ref.child(auth.currentUser?.uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: Users? = snapshot.getValue(Users::class.java)
                if(user != null) curUser = user
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        recyclerView = view.findViewById(R.id.jobrecyclerView)
        progressBar = view.findViewById(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        setUpJobRecyclerView(view, "")

        return view
    }

    private fun setUpJobRecyclerView(view: View, filter: String){
        jobDao = JobDao()
        val jobCollection = jobDao.jobCollection
        val query: Query
        if(filter.isEmpty()) {
            query = jobCollection.orderBy("id", Query.Direction.DESCENDING).limitToLast(30)
            val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Job>().setQuery(query, Job::class.java).build()
            adapter = JobAdapter(recyclerViewOptions, this, R.layout.job_item)
        } else {
            query = jobCollection
                .whereEqualTo("category", filter)
            val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Job>().setQuery(query, Job::class.java).build()
            adapter.updateOptions(recyclerViewOptions)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        progressBar.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OnlyJobsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OnlyJobsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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

    override fun updateRecyclerView(query: String) {
        if(query.isEmpty()) {
            Log.e("Checked", "Empty")
        }
        view?.let { setUpJobRecyclerView(it, query) }
    }
}