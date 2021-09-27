package android.example.uptoskills

import android.content.Intent
import android.example.uptoskills.daos.PaidCourseDao
import android.example.uptoskills.databinding.ActivityPaidCourseViewBinding
import android.example.uptoskills.models.PaidCourse
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PaidCourseViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaidCourseViewBinding
    private lateinit var courseId: String
    private lateinit var paidCourse: PaidCourse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaidCourseViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        courseId = intent.getStringExtra("courseId").toString()
        if (courseId != "null" && courseId.trim().isNotEmpty()) {
            GlobalScope.launch(Dispatchers.IO) {
                paidCourse =
                    PaidCourseDao().getCoursebyId(courseId.trim()).await()
                        .toObject(PaidCourse::class.java)!!
                withContext(Dispatchers.Main) {
                    binding.CourseCategory.text = paidCourse.category
                    binding.CourseDescription.text = paidCourse.course_description
                    binding.InstructorName.text = paidCourse.mentor_name
                    binding.certificate.text = if (paidCourse.certificate) "Yes" else "No"
                    binding.duration.text = paidCourse.course_duration
                    binding.language.text = paidCourse.language
                    binding.lectures.text = paidCourse.lectures.toString()
                    binding.classtime.text = paidCourse.start_time.trim()
                    binding.price.text =
                        if (paidCourse.price == 0) "Free" else paidCourse.price.toString()
                    binding.courseCurriculum.text = String.format(paidCourse.curriculum)
                    Glide.with(this@PaidCourseViewActivity).load(paidCourse.course_image)
                        .centerCrop()
                        .into(binding.courseImage)
                }
            }
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
        binding.join.setOnClickListener {
            val intent = Intent()
            intent.setData(Uri.parse(paidCourse.link))
            intent.action = Intent.ACTION_VIEW
            startActivity(intent)
        }
    }
}