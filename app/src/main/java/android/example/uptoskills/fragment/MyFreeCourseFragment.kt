package android.example.uptoskills.fragment

import android.content.Intent
import android.example.uptoskills.Adapters.CourseItemClicked
import android.example.uptoskills.Adapters.MyFreeCourseAdapter
import android.example.uptoskills.CourseVideoActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.uptoskills.R
import android.example.uptoskills.daos.CourseDao
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.models.FreeCourse
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
 * Use the [MyFreeCourseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyFreeCourseFragment : Fragment(), CourseItemClicked, onJobSearch {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var userDao: UsersDao
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: MyFreeCourseAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var noCourse: TextView
    private val courseList: ArrayList<FreeCourse> = ArrayList()
    private var curUser = Users()

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
        val view = inflater.inflate(R.layout.fragment_my_free_course, container, false)
        noCourse = view.findViewById(R.id.noCourse)
        setUpCourses(view)
        return view
    }

    private fun setUpCourses(view: View) {
        recyclerView = view.findViewById(R.id.myCourseRecyclerView)
        userDao = UsersDao()
        auth = FirebaseAuth.getInstance()
        val currentUserId: String = auth.currentUser!!.uid
        progressBar = view.findViewById(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE
        adapter = MyFreeCourseAdapter(view.context, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        GlobalScope.launch(Dispatchers.IO) {
            val temp = userDao.getUserById(auth.currentUser!!.uid).await().toObject(Users::class.java)
            if(temp != null) {
                curUser = temp
                val courseDao = CourseDao()
                curUser.freecourses?.forEach {
                    val courseId = it.key
                    if (courseId != "null") {
                        val course: FreeCourse? = courseDao.getCoursebyId(courseId).await().toObject(FreeCourse::class.java)
                        if(course != null) courseList.add(course)
                    }
                }
                withContext(Dispatchers.Main) {
                    adapter.updateCourses(courseList)
                    progressBar.visibility = View.GONE
                    if(courseList.size != 0) {
                        noCourse.visibility = View.GONE
                    } else {
//                        Toast.makeText(view.context, "No Course Found", Toast.LENGTH_SHORT).show()
                        noCourse.visibility = View.VISIBLE
                    }
                }
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
         * @return A new instance of fragment MyFreeCourseFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyFreeCourseFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCourseCLick(courseId: String) {
        val intent = Intent(activity, CourseVideoActivity::class.java)
        intent.putExtra("courseId", courseId)
        startActivity(intent)
    }

    override fun updateRecyclerView(query: String) {
        if(query.trim().isEmpty()) {
            noCourse.visibility = View.GONE
            adapter.updateCourses(courseList)
        } else {
            val newCourseList = ArrayList<FreeCourse>()
            courseList.forEach {
                if(it.category.contains(query.trim(), true) || it.course_name.contains(query.trim(), true)) {
                    newCourseList.add(it)
                    noCourse.visibility = View.GONE
                }
            }
            if(newCourseList.isEmpty()) {
                adapter.updateCourses(newCourseList)
                noCourse.visibility = View.VISIBLE
//                Toast.makeText(view?.context, "No Course Found", Toast.LENGTH_SHORT).show()
                } else {
                noCourse.visibility = View.GONE
                adapter.updateCourses(newCourseList)
            }
        }
    }
}