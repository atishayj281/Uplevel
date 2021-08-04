package android.example.uptoskills.singleton


import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.models.Users
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
    fun getUser(): Users? {

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