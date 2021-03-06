package android.example.uptoskills.daos

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
class BlogDao {

    val db = FirebaseFirestore.getInstance()
    val postCollections = db.collection("Blogs")
    val auth = Firebase.auth

    fun getBlogById(postId: String): Task<DocumentSnapshot> {
        return postCollections.document(postId).get()
    }
}