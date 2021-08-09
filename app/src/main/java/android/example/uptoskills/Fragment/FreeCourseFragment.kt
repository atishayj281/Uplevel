package android.example.uptoskills.Fragment

import android.content.Intent
import android.example.uptoskills.Adapters.BlogsAdapter
import android.example.uptoskills.Adapters.CourseAdapter
import android.example.uptoskills.Adapters.CourseItemClicked
import android.example.uptoskills.CourseViewActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.uptoskills.R
import android.example.uptoskills.daos.BlogDao
import android.example.uptoskills.daos.CourseDao
import android.example.uptoskills.models.Blog
import android.example.uptoskills.models.FreeCourse
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
 * Use the [FreeCourseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FreeCourseFragment : Fragment(), CourseItemClicked {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var courseRecyclerView: RecyclerView
    private lateinit var courseAdapter: CourseAdapter
    private lateinit var courseDao: CourseDao

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
        val view = inflater.inflate(R.layout.fragment_free_course, container, false)


        setUpcourses(view)
        return view
    }

    private fun setUpcourses(view: View) {
        courseDao = CourseDao()
        courseRecyclerView = view.findViewById(R.id.freeCourseRecyclerView)
        val courseCollection = courseDao.courseCollection
        val query = courseCollection.orderBy("category", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<FreeCourse>().setQuery(query, FreeCourse::class.java).build()

        courseAdapter = CourseAdapter(view.context,this, R.layout.item_course, recyclerViewOptions)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FreeCourseFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FreeCourseFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCourseCLick(courseId: String) {
         val intent = Intent(activity, CourseViewActivity::class.java)
        intent.putExtra("courseId", courseId)
        intent.putExtra("courseCategory", "free")
        startActivity(intent)
    }
}