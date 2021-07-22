package android.example.UptoSkills.daos

import android.example.UptoSkills.models.Blog
import android.example.UptoSkills.models.GoogleUser
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BlogDao {

    val db = FirebaseFirestore.getInstance()
    val postCollections = db.collection("posts")
    val auth = Firebase.auth

    fun addPost(text: String, title: String="") {
        GlobalScope.launch {
            val currentUserId = auth.currentUser!!.uid
            val userDao = GoogleUsersDao()
            val user = userDao.getUserById(currentUserId).await().toObject(GoogleUser::class.java)!!

            val currentTime = System.currentTimeMillis()
            val post = Blog(text,title, user, currentTime)
            postCollections.document().set(post)
        }
    }

    fun getBlogById(postId: String): Task<DocumentSnapshot> {
        return postCollections.document(postId).get()
    }


}