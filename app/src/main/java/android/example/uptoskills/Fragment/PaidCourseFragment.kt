package android.example.uptoskills.Fragment

import android.content.Intent
import android.example.uptoskills.Adapters.CourseAdapter
import android.example.uptoskills.Adapters.CourseItemClicked
import android.example.uptoskills.Adapters.PaidCourseAdapter
import android.example.uptoskills.Adapters.paidCourseclicked
import android.example.uptoskills.CourseViewActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.uptoskills.R
import android.example.uptoskills.daos.CourseDao
import android.example.uptoskills.daos.PaidCourseDao
import android.example.uptoskills.models.PaidCourse
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
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
 * Use the [PaidCourseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PaidCourseFragment : Fragment(), paidCourseclicked {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


//    private lateinit var courseRecyclerView: RecyclerView
//    private lateinit var courseAdapter: CourseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var courseRecyclerView: RecyclerView
    private lateinit var courseAdapter: PaidCourseAdapter
    private lateinit var courseDao: PaidCourseDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_paid_course, container, false)

        setUpcourses(view)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PaidCourseFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PaidCourseFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun setUpcourses(view: View) {
        courseDao = PaidCourseDao()
        courseRecyclerView = view.findViewById(R.id.paidCourseRecyclerView)
        val courseCollection = courseDao.courseCollection
        val query = courseCollection.orderBy("category", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<PaidCourse>().setQuery(query, PaidCourse::class.java).build()

        courseAdapter = PaidCourseAdapter(view.context,this, R.layout.item_course, recyclerViewOptions)
        courseRecyclerView.adapter = courseAdapter
        courseRecyclerView.layoutManager = LinearLayoutManager(view.context)
    }

    override fun onStart() {
        super.onStart()
        courseAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        courseAdapter.stopListening()
    }

    override fun onpaidCourseClicked(courseId: String) {
        val intent = Intent(activity, CourseViewActivity::class.java)
        intent.putExtra("courseId", courseId)
        intent.putExtra("courseCategory", "paid")
        startActivity(intent)
    }
}