package android.example.uptoskills.daos

import android.example.uptoskills.models.GoogleUser
import android.example.uptoskills.models.Users
import com.google.firebase.database.FirebaseDatabase
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