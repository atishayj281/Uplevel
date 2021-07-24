package android.example.UptoSkills.daos

import android.content.Context
import android.example.UptoSkills.models.Blog
import android.example.UptoSkills.models.GoogleUser
import android.view.View
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

class BlogDao {

    val db = FirebaseFirestore.getInstance()
    val postCollections = db.collection("posts")
    val auth = Firebase.auth

    fun addPost(text: String, title: String="") {
        GlobalScope.launch {
            val currentUserId = auth.currentUser!!.uid
            val userDao = GoogleUsersDao()
            var user: GoogleUser = GoogleUser()
            userDao.ref.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.child(currentUserId).getValue(GoogleUser::class.java)!!
                    val currentTime = System.currentTimeMillis()
                    val post = Blog(text,title, user, currentTime)
                    post.let { postCollections.document().set(it) }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }
    }

    fun getBlogById(postId: String): Task<DocumentSnapshot> {
        return postCollections.document(postId).get()
    }


}