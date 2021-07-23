package android.example.UptoSkills.daos

import android.example.UptoSkills.models.GoogleUser
import android.example.UptoSkills.models.Users
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GoogleUsersDao {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref = database.getReference("users")

    fun addUser(user: GoogleUser?, id: String) {
        user?.let {

            GlobalScope.launch(Dispatchers.IO) {
                ref.child(id).setValue(it)
            }
        }
    }


    fun updateUser(user: Users, id: String){
        GlobalScope.launch {
            ref.child(id).setValue(user)

        }
    }
}