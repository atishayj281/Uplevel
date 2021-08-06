package android.example.uptoskills.Fragment

import android.example.uptoskills.Adapters.CourseAdapter
import android.example.uptoskills.Adapters.CourseItemClicked
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.uptoskills.R
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PaidCourseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PaidCourseFragment : Fragment(), CourseItemClicked {
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_paid_course, container, false)

//        courseRecyclerView = view.findViewById(R.id.paidCourseRecyclerView)
//        courseAdapter = CourseAdapter(view.context, this, R.layout.item_course)
//        courseRecyclerView.adapter = courseAdapter
//        courseRecyclerView.layoutManager = LinearLayoutManager(view?.context)


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

    override fun onCourseCLick(paidCourse: String) {
//        val builder = CustomTabsIntent.Builder()
//        val customTabsIntent = builder.build()
//        view?.let { customTabsIntent.launchUrl(it.context, Uri.parse(paidCourse.url)) }
    }
}