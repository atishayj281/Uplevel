package android.example.uptoskills.daos

import android.content.Context
import android.example.uptoskills.models.FreeCourse
import android.example.uptoskills.models.Users
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CourseDao {
    val db = FirebaseFirestore.getInstance()
    val courseCollection = db.collection("FreeCourses")
    val auth = Firebase.auth

    fun getCoursebyId(courseId: String): Task<DocumentSnapshot> {
        return courseCollection.document(courseId).get()
    }

    fun EnrollStudents(courseId: String, context: Context): Boolean {
        if(courseId.isNotBlank()) {

            var isSuccessful = true

            GlobalScope.launch {
                val currentUserId = auth.currentUser!!.uid
                val course = getCoursebyId(courseId).await().toObject(FreeCourse::class.java)
                val isEnrolled = course?.enrolledStudents?.contains(currentUserId)
                if(!isEnrolled!!) {
                    isSuccessful = true
                    course.enrolledStudents.add(currentUserId)
                    courseCollection.document(courseId).set(course)
                    UsersDao().ref.child(currentUserId).child("freecourses").child(courseId).setValue(course.course_name).addOnSuccessListener {
                        Log.d("Enrolled", "YES")
                        Toast.makeText(context, "Successfully Enrolled", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Log.d("failure", it.message.toString())
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Already Enrolled", Toast.LENGTH_SHORT).show()
                    }
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