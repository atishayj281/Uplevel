package android.example.uptoskills

import android.content.Intent
import android.example.uptoskills.daos.CourseDao
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.databinding.ActivityCourseViewBinding
import android.example.uptoskills.models.FreeCourse
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CourseViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCourseViewBinding
    private lateinit var courseId: String
    private lateinit var courseDao: CourseDao
    private lateinit var course: FreeCourse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        courseId = intent.getStringExtra("courseId").toString()
        courseDao = CourseDao()

        GlobalScope.launch(Dispatchers.IO) {

            course = courseDao.getCoursebyId(courseId).await().toObject(FreeCourse::class.java)!!
            withContext(Dispatchers.Main) {
                binding.CourseCategory.text = course.category
                binding.CourseDescription.text = course.course_description
                binding.InstructorName.text = course.mentor_name
                binding.certificate.text = if(course.certificate) "Yes" else "No"
                binding.duration.text = course.course_duration
                binding.language.text = course.language
                binding.lectures.text = course.lectures.toString()
                binding.price.text = if(course.price == 0) "Free" else course.price.toString()
                binding.courseCurriculum.text = String.format(course.curriculum)
                Glide.with(this@CourseViewActivity).load(course.course_image).centerCrop().into(binding.courseImage)
            }
        }

        binding.enroll.setOnClickListener {
            UsersDao().ref.child(FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(
                object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var displayName:String = snapshot.child("displayName").toString()
                        var college_name = snapshot.child("college_name").toString()
                        var education = snapshot.child("education").toString()
                        var email = snapshot.child("email").toString()
                        var full_name = snapshot.child("full_name").toString()
                        var mobileNo = snapshot.child("mobileNo").toString()
                        if(displayName.isNotBlank() && college_name.isNotBlank()
                            && education.isNotBlank() && email.isNotBlank()
                            && full_name.isNotBlank() && mobileNo.isNotBlank()) {
                            courseDao.EnrollStudents(courseId)
                            Toast.makeText(this@CourseViewActivity, "Successfully Enrolled", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@CourseViewActivity, "Please Provide your details", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@CourseViewActivity, UserDetailsActivity::class.java)
                            intent.putExtra("parent", "course")
                            startActivity(intent)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
    }
}