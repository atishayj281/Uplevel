package android.example.uptoskills.Fragment

import android.example.uptoskills.Adapters.CourseItemClicked
import android.example.uptoskills.Adapters.MyPaidCourseAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.uptoskills.R
import android.example.uptoskills.daos.PaidCourseDao
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.models.PaidCourse
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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
 * Use the [MyPaidCourseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyPaidCourseFragment : Fragment(), CourseItemClicked {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var userDao: UsersDao
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: MyPaidCourseAdapter
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
        val view = inflater.inflate(R.layout.fragment_my_paid_course, container, false)
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
        adapter = MyPaidCourseAdapter(view.context, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        userDao.ref.child(currentUserId).child("paidcourses").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children

                GlobalScope.launch(Dispatchers.IO) {
                    val paidCourseDao = PaidCourseDao()
                    var courseList: ArrayList<PaidCourse> = ArrayList()
                    children.forEach {
                        val courseId = it.key
                        if (courseId != null) {
                            var course: PaidCourse = paidCourseDao.getCoursebyId(courseId).await().toObject(
                                PaidCourse::class.java)!!
                            courseList.add(course)
                        }
                    }

                    withContext(Dispatchers.Main) {
                        adapter.updateCourses(courseList)
                        progressBar.visibility = View.GONE
                    }

                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyPaidCourseFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyPaidCourseFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCourseCLick(courseId: String) {

    }
}