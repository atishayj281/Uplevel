package android.example.uptoskills.daos

import android.content.Context
import android.example.uptoskills.models.Events
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class EventDao {
    val db = FirebaseFirestore.getInstance()
    val postCollections = db.collection("events")
    val auth = Firebase.auth

    fun getBlogById(postId: String): Task<DocumentSnapshot> {
        return postCollections.document(postId).get()
    }

    fun enrollinEvent(event: Events, id: String, context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            postCollections.document(id).set(event)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Successfully Enrolled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}