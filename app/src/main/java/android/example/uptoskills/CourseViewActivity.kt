package android.example.uptoskills

import android.content.Intent
import android.example.uptoskills.daos.CourseDao
import android.example.uptoskills.daos.PaidCourseDao
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.databinding.ActivityCourseViewBinding
import android.example.uptoskills.models.FreeCourse
import android.example.uptoskills.models.PaidCourse
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
    private lateinit var paidCourse: PaidCourse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        courseId = intent.getStringExtra("courseId").toString()
        courseDao = CourseDao()
        val isFree: Boolean = intent.getStringExtra("courseCategory") == "free"
            if (isFree) {
                GlobalScope.launch(Dispatchers.IO) {
                    course =
                        courseDao.getCoursebyId(courseId).await().toObject(FreeCourse::class.java)!!
                    withContext(Dispatchers.Main) {
                        binding.CourseCategory.text = course.category
                        binding.CourseDescription.text = course.course_description
                        binding.InstructorName.text = course.mentor_name
                        binding.certificate.text = if (course.certificate) "Yes" else "No"
                        binding.duration.text = course.course_duration
                        binding.language.text = course.language
                        binding.lectures.text = course.lectures.toString()
                        binding.price.text =
                            if (course.price == 0) "Free" else course.price.toString()
                        binding.courseCurriculum.text = String.format(course.curriculum)
                        Glide.with(this@CourseViewActivity).load(course.course_image).centerCrop()
                            .into(binding.courseImage)
                    }
                }
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    paidCourse =
                        PaidCourseDao().getCoursebyId(courseId).await().toObject(PaidCourse::class.java)!!
                withContext(Dispatchers.Main) {
                    binding.CourseCategory.text = paidCourse.category
                    binding.CourseDescription.text = paidCourse.course_description
                    binding.InstructorName.text = paidCourse.mentor_name
                    binding.certificate.text = if (paidCourse.certificate) "Yes" else "No"
                    binding.duration.text = paidCourse.course_duration
                    binding.language.text = paidCourse.language
                    binding.lectures.text = paidCourse.lectures.toString()
                    binding.price.text =
                        if (paidCourse.price == 0) "Free" else paidCourse.price.toString()
                    binding.courseCurriculum.text = String.format(paidCourse.curriculum)
                    Glide.with(this@CourseViewActivity).load(paidCourse.course_image).centerCrop()
                        .into(binding.courseImage)
                }

            }
        }

        binding.enroll.setOnClickListener {
            val isFree: Boolean = intent.getStringExtra("courseCategory") == "free"
            if (isFree) {
                Toast.makeText(this, "clicked", Toast.LENGTH_SHORT)
                courseDao.EnrollStudents(courseId, this@CourseViewActivity)
            } else {
                val intent = Intent(this@CourseViewActivity, UserDetailsActivity::class.java)
                intent.putExtra("parent", "course")
                intent.putExtra("courseId", courseId)
                intent.putExtra("coursePrice", paidCourse.price)
                intent.putExtra("courseName", paidCourse.course_name)
                startActivity(intent)
            }
        }

    }
}