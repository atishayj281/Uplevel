package android.example.uptoskills.daos

import android.content.Context
import android.example.uptoskills.mail.JavaMailAPI
import android.example.uptoskills.models.FreeCourse
import android.example.uptoskills.models.Job
import android.example.uptoskills.models.Users
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class JobDao {

    private val db = FirebaseFirestore.getInstance()
    val jobCollection = db.collection("jobs")
    val auth = FirebaseAuth.getInstance()
    private val usersDao = UsersDao()

    fun getJobbyId(jobId: String): Task<DocumentSnapshot> {
        return jobCollection.document(jobId).get()
    }

    private fun sendMail(job: String, context: Context, user: Users) {
        GlobalScope.launch(Dispatchers.IO) {
            val mail: String = "atishay.tca1909005@tmu.ac.in"
            val message: String = "Name: ${user.full_name}\nCurrent Position: ${user.job}\nOrganisation: ${user.college_name}\nMobile No.: ${user.mobileNo}"+
                    "\nHighest Qualification: ${user.education}\nResume: ${user.resume}\nEmail: ${user.email}\n has been applied for " + job
            val subject: String = "${user.full_name} has been applied for " + job

            //Send Mail
            val javaMailAPI = JavaMailAPI(context, mail, subject, message)
            javaMailAPI.sendMail()
        }

    }

    fun addbookmark(jobId: String) {
        if(jobId.isNotBlank()) {
            GlobalScope.launch(Dispatchers.IO) {
                val currentUserId = auth.currentUser!!.uid
                val job = getJobbyId(jobId).await().toObject(Job::class.java)!!
                val isBookmarked = job.bookmark.contains(currentUserId)
                if(isBookmarked) {
                    job.bookmark.remove(currentUserId)
                } else {
                    job.bookmark.add(currentUserId)
                }
                jobCollection.document(jobId).set(job)
            }
        }
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
                    user.appliedJobs?.set(jobId, "Job")
                    UsersDao().addUser(user, currentUserId)
                    withContext(Dispatchers.Main) {
                        sendMail(job.title, context, user)
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