package android.example.uptoskills.fragment

import android.content.Intent
import android.example.uptoskills.Adapters.InternshipAdapter
import android.example.uptoskills.Adapters.JobItemClicked
import android.example.uptoskills.JobViewActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.uptoskills.R
import android.example.uptoskills.daos.InternshipDao
import android.example.uptoskills.models.Internship
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InternshipFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InternshipFragment : Fragment(), JobItemClicked, onJobSearch {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InternshipAdapter
    private lateinit var internshipDao: InternshipDao
    private lateinit var progressBar: ProgressBar

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
        val view = inflater.inflate(R.layout.fragment_internship, container, false)
        recyclerView = view.findViewById(R.id.internshiprecyclerView)
        progressBar = view.findViewById(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        // Setting the internship
        setUpInternshipRecyclerView(view, "")
        return view
    }

    private fun setUpInternshipRecyclerView(view: View, filter: String){
        internshipDao = InternshipDao()
        val internshipCollection = internshipDao.jobCollection
        val query: Query

        if(filter.isEmpty()) {
            query = internshipCollection.orderBy("id", Query.Direction.DESCENDING)
            val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Internship>().setQuery(query, Internship::class.java).build()
            adapter = InternshipAdapter(recyclerViewOptions, this, R.layout.internship_item)
        } else {
            query = internshipCollection.whereEqualTo("category", filter)
            val recyclerOptions = FirestoreRecyclerOptions.Builder<Internship>().setQuery(query, Internship::class.java).build()
            adapter.updateOptions(recyclerOptions)
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
         * @return A new instance of fragment InternshipFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InternshipFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onJobCLick(jobId: String) {
        val intent = Intent(view?.context, JobViewActivity::class.java)
        intent.putExtra("jobId", jobId)
        intent.putExtra("category", "internship")
        startActivity(intent)
    }

    override fun onbookmarkCLick(itemId: String, itemtype: String) {

    }

    override fun updateRecyclerView(query: String) {
        view?.let { setUpInternshipRecyclerView(it, query) }
    }
}