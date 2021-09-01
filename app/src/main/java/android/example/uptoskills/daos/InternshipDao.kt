package android.example.uptoskills.daos

import android.content.Context
import android.example.uptoskills.models.Job
import android.example.uptoskills.models.Users
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class InternshipDao {
    private val db = FirebaseFirestore.getInstance()
    val jobCollection = db.collection("internships")
    val auth = Firebase.auth

    fun getJobbyId(jobId: String): Task<DocumentSnapshot> {
        return jobCollection.document(jobId).get()
    }

    fun applyJob(jobId: String, context: Context, user: Users): Boolean {
        if(jobId.isNotBlank()) {

            var isSuccessful = true

            GlobalScope.launch {
                val currentUserId = auth.currentUser!!.uid
                val job = getJobbyId(jobId).await().toObject(Job::class.java)!!
                val isEnrolled = job.applied.containsKey(currentUserId)
                if(!isEnrolled) {
                    isSuccessful = true
                    auth.currentUser!!.email?.let { job.applied.put(currentUserId, it) }
                    jobCollection.document(jobId).set(job)
                    user.appliedJobs?.put(jobId, "Internship")
                    UsersDao().addUser(user, currentUserId)
                    withContext(Dispatchers.Main){
                        Toast.makeText(context, "Successfully Applied", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Already Applied", Toast.LENGTH_SHORT).show()
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