package android.example.uptoskills.daos

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class BannerDao {
    val db = FirebaseFirestore.getInstance()
    val postCollections = db.collection("Banner")
    val auth = Firebase.auth

    fun getBannerById(id: String): Task<DocumentSnapshot> {
        return postCollections.document(id).get()
    }
}