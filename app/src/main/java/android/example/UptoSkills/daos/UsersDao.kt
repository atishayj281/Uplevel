package android.example.UptoSkills.daos

import android.example.UptoSkills.models.GoogleUser
import android.example.UptoSkills.models.Users
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UsersDao {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref = database.getReference("users")

    fun addUser(user: Users?, id: String) {
        user?.let {

            GlobalScope.launch(Dispatchers.IO) {
                //usersCollection.document(id).set(it)
                ref.child(id).setValue(it)
            }
        }
    }

    fun getUserById(uId: String): Task<DataSnapshot> {
        return ref.child(uId).get()
    }

    fun updateUser(user: Users, id: String){
        GlobalScope.launch {
            ref.child(id).setValue(user)

        }
    }
}