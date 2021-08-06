package android.example.uptoskills

import android.example.uptoskills.Adapters.CourseAdapter
import android.example.uptoskills.Adapters.CourseItemClicked
import android.example.uptoskills.Adapters.MyCourseAdapter
import android.example.uptoskills.daos.CourseDao
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.models.FreeCourse
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MyCourseActivity : AppCompatActivity(), CourseItemClicked {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userDao: UsersDao
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: MyCourseAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_course)

        recyclerView = findViewById(R.id.myCourseRecyclerView)
        userDao = UsersDao()
        auth = FirebaseAuth.getInstance()
        val currentUserId: String = auth.currentUser!!.uid
        progressBar = findViewById(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE
        adapter = MyCourseAdapter(this, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this@MyCourseActivity)

        userDao.ref.child(currentUserId).child("Courses").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children

                GlobalScope.launch(Dispatchers.IO) {
                    val courseDao = CourseDao()
                    var courseList: ArrayList<FreeCourse> = ArrayList()
                    children.forEach {
                        val courseId = it.key
                        if (courseId != null) {
                            var course: FreeCourse = courseDao.getCoursebyId(courseId).await().toObject(FreeCourse::class.java)!!
                            courseList.add(course)
                        }
                    }

                    withContext(Dispatchers.Main) {
                        adapter.updateCourses(courseList)
                    }

                }

                progressBar.visibility = View.GONE

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onCourseCLick(paidCourse: String) {

    }
}