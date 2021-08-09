package android.example.uptoskills.daos

import android.content.Context
import android.example.uptoskills.models.FreeCourse
import android.example.uptoskills.models.PaidCourse
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PaidCourseDao {

    val db = FirebaseFirestore.getInstance()
    val courseCollection = db.collection("PaidCourses")
    val auth = Firebase.auth

    fun getCoursebyId(courseId: String): Task<DocumentSnapshot> {
        return courseCollection.document(courseId).get()
    }

    fun EnrollStudents(courseId: String, context: Context): Boolean {
        if(courseId.isNotBlank()) {

            var isSuccessful = true

            GlobalScope.launch {
                val currentUserId = auth.currentUser!!.uid
                val course = getCoursebyId(courseId).await().toObject(PaidCourse::class.java)!!
                val isEnrolled = course.enrolledStudents.contains(currentUserId)
                if(!isEnrolled) {
                    isSuccessful = true
                    course.enrolledStudents.put(currentUserId, "No")
                    courseCollection.document(courseId).set(course)
                    UsersDao().ref.child(currentUserId).child("paidcourses").child(courseId).setValue(course.course_name).addOnSuccessListener {
                        Log.d("Enrolled", "YES")
                        Toast.makeText(context, "Successfully Enrolled", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Log.d("failure", it.message.toString())
                    }
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