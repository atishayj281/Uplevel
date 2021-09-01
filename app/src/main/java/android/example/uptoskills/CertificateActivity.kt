package android.example.uptoskills

import android.example.uptoskills.daos.PaidCourseDao
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.databinding.ActivityCertificateBinding
import android.example.uptoskills.models.PaidCourse
import android.example.uptoskills.models.Users
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.temporal.ValueRange

class CertificateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCertificateBinding
    private var user = Users()
    private lateinit var usersDao: UsersDao
    private lateinit var auth: FirebaseAuth
    private val completedCourseId = ArrayList<String>()
    private val completedCourse = ArrayList<PaidCourse>()
    private lateinit var paidCourseDao: PaidCourseDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertificateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        paidCourseDao = PaidCourseDao()
        auth = FirebaseAuth.getInstance()
        usersDao = UsersDao()

        GlobalScope.launch(Dispatchers.IO) {
            val temp = auth.currentUser?.let { usersDao.getUserById(it.uid).await().toObject(Users::class.java) }
            if(temp != null) {
                user = temp;
                user.paidcourses?.forEach { (s, s2) ->
                    if(s2.lowercase() == "yes") {
                        completedCourseId.add(s)
                    }
                }
                if(completedCourseId.isNotEmpty()) {
                    GlobalScope.launch {
                        completedCourseId
                            .forEach {
                                val course = paidCourseDao.getCoursebyId(it).await().toObject(PaidCourse::class.java)
                                if(course != null) {
                                    completedCourse.add(course)
                                }
                            }
                        withContext(Dispatchers.Main) {

                        }
                    }


                }
            }
        }


    }
}