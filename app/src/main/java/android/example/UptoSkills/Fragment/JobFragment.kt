package android.example.UptoSkills.Fragment

import android.example.UptoSkills.Adapters.JobAdapter
import android.example.UptoSkills.Adapters.JobItemClicked
import android.example.UptoSkills.R
import android.example.UptoSkills.models.Course
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.example.UptoSkills.models.Job
import android.net.Uri
import android.widget.ProgressBar
import androidx.browser.customtabs.CustomTabsIntent


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [JobFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class JobFragment : Fragment(), JobItemClicked {
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

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JobAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_job, container, false)

        recyclerView = view.findViewById(R.id.Jobsrecyclerview)
        progressBar = view.findViewById(R.id.jobProgressBar)
        progressBar.visibility = View.VISIBLE

        adapter = JobAdapter(view.context, this, R.layout.job_item)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        progressBar.visibility = View.GONE
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun fetchJob(){
        
    }

    override fun onJobCLick(job: Job) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        view?.let { customTabsIntent.launchUrl(it.context, Uri.parse(job.url)) }
    }
}

