package android.example.uptoskills.daos

import android.content.Context
import android.example.uptoskills.models.FreeCourse
import android.example.uptoskills.models.PaidCourse
import android.example.uptoskills.models.Users
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class PaidCourseDao {

    val db = FirebaseFirestore.getInstance()
    val courseCollection = db.collection("PaidCourses")
    val auth = Firebase.auth

    fun getCoursebyId(courseId: String): Task<DocumentSnapshot> {
        return courseCollection.document(courseId).get()
    }

    fun EnrollStudents(courseId: String, context: Context, user: Users): Boolean {
        if(courseId.isNotBlank()) {

            var isSuccessful = true

            GlobalScope.launch {
                val currentUserId = auth.currentUser!!.uid
                val course = getCoursebyId(courseId).await().toObject(PaidCourse::class.java)!!
                val isEnrolled = course.enrolledStudents.contains(currentUserId)
                if(!isEnrolled) {
                    isSuccessful = true
                    course.enrolledStudents[currentUserId] = "NO"
                    courseCollection.document(courseId).set(course)
                    user.paidcourses?.put(courseId, "NO")
                    auth.currentUser?.uid?.let { UsersDao().addUser(user, it) }
                    Toast.makeText(context, "Successfully Enrolled", Toast.LENGTH_SHORT).show()
                } else {
                    isSuccessful = false
                }
            }
            return isSuccessful
        }
        else {
            return false;
        }
    }




}