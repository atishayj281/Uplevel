package android.example.UptoSkills.singleton


import android.content.Context
import android.example.UptoSkills.daos.UsersDao
import android.example.UptoSkills.models.Users
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.lang.NullPointerException

object CurUser {
    private var usersDao: UsersDao = UsersDao()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var exists: Users? = null
    fun getUser(user: FirebaseUser?): Users? {

        try {
            usersDao.ref.orderByChild("id").equalTo(auth.currentUser?.uid).addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    exists = snapshot.getValue(Users::class.java)
                }
                override fun onCancelled(error: DatabaseError) {

                }

            })
        } catch (e: NullPointerException) {
            return null
        }


        return exists
    }



}