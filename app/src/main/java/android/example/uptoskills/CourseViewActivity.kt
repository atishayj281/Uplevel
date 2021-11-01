package android.example.uptoskills

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.example.uptoskills.daos.CourseDao
import android.example.uptoskills.daos.PaidCourseDao
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.databinding.ActivityCourseViewBinding
import android.example.uptoskills.models.FreeCourse
import android.example.uptoskills.models.PaidCourse
import android.example.uptoskills.models.Users
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject

class CourseViewActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivityCourseViewBinding
    private lateinit var courseId: String
    private lateinit var courseDao: CourseDao
    private lateinit var course: FreeCourse
    private lateinit var paidCourse: PaidCourse
    private var curUser: Users = Users()
    private lateinit var userDao: UsersDao
    private lateinit var auth: FirebaseAuth
    private val READ_SMS_CODE: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDao = UsersDao()
        auth = FirebaseAuth.getInstance()
        binding.back.setOnClickListener {
            finish()
        }

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

        GlobalScope.launch(Dispatchers.IO) {
            val temp = auth.currentUser?.let { userDao.getUserById(it.uid).await().toObject(Users::class.java) }
            if(temp != null) {
                curUser = temp
            }
        }


        binding.enroll.setOnClickListener {
            val isFree: Boolean = intent.getStringExtra("courseCategory") == "free"
            if (isFree) {
                courseDao.EnrollStudents(courseId, this@CourseViewActivity, curUser)

            } else {
                if(curUser.full_name.trim().isEmpty() || curUser.job.trim().isEmpty()
                    || curUser.education.trim().isEmpty() || curUser.mobileNo.trim().isEmpty()
                    || curUser.email.trim().isEmpty()) {
                    Toast.makeText(this, "Please Complete Your Profile and Try Again...", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, UserDetailsActivity::class.java)
                    intent.putExtra("parent", "course")
                    startActivity(intent)
                } else {
                    isEnrolled(curUser.email, curUser.mobileNo, curUser.coins)
                }
            }
        }
    }


    // checking if Student is Already Enrolled in the course
    private fun isEnrolled(email: String, phoneNo:String, wallet: Int){
        if(courseId.isNotBlank()) {
            GlobalScope.launch(Dispatchers.IO) {
                val course: PaidCourse = PaidCourseDao().getCoursebyId(courseId).await().toObject(PaidCourse::class.java)!!
                withContext(Dispatchers.Main) {
                    if(course.enrolledStudents.contains(auth.currentUser!!.uid)) {
                        Toast.makeText(this@CourseViewActivity, "Already Enrolled", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        // Checking the Permissions
                        if(ContextCompat.checkSelfPermission(this@CourseViewActivity, android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                            startpayment(email, phoneNo, wallet)
                        } else {
                            ActivityCompat.requestPermissions(this@CourseViewActivity, arrayOf(android.Manifest.permission.READ_SMS), READ_SMS_CODE)
                        }

                    }
                }
            }
        }

    }

    // Starting Payment
    var isCoinsUsed: Boolean = false
    private fun startpayment(email: String, contact: String, wallet: Int){

        if(email.isBlank() && contact.isBlank()){
            Toast.makeText(this, "Please Provide Your Full Details", Toast.LENGTH_SHORT).show()
        } else {
            var price = paidCourse.price

            if(curUser.coins >= 100) {
                price -= 100
                isCoinsUsed = true
            }

            if(price == 0) {
                PaidCourseDao().EnrollStudents(courseId, this, curUser)
            } else {
                price *= 100
                if(price > 0) {
                    val activity: Activity = this
                    val checkout = Checkout()
                    try {
                        val orderRequest = JSONObject()
                        orderRequest.put("name", "UptoSkills")
                        orderRequest.put("description", paidCourse.course_name+" Payment")
                        orderRequest.put("currency", "INR")
                        orderRequest.put("amount", price) // amount in the smallest currency unit
                        orderRequest.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
                        orderRequest.put("prefill.email", email);
                        orderRequest.put("prefill.contact",contact);

                        checkout.open(activity, orderRequest)
                    } catch (e: Exception){
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    override fun onPaymentSuccess(p0: String?) {

        Toast.makeText(this, "Successful Payment Id: $p0", Toast.LENGTH_SHORT).show()
        PaidCourseDao().EnrollStudents(courseId, this, curUser)
        if(isCoinsUsed) {
            curUser.coins -= 100
            userDao.updateUser(curUser, auth.currentUser?.uid!!)
        }

    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
        Log.d("Payment", p0.toString())
    }

    override fun onBackPressed() {
        if(parent == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        super.onBackPressed()
    }


}