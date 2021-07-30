package android.example.uptoskills.daos

import android.example.uptoskills.models.Blog
import android.example.uptoskills.models.Users
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BlogDao {

    val db = FirebaseFirestore.getInstance()
    val postCollections = db.collection("Blogs")
    val auth = Firebase.auth


    fun getBlogById(postId: String): Task<DocumentSnapshot> {
        return postCollections.document(postId).get()
    }


}