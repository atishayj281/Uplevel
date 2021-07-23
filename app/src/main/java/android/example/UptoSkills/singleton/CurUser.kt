package android.example.UptoSkills.singleton


import android.content.Context
import android.example.UptoSkills.daos.UsersDao
import android.example.UptoSkills.models.Users
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class CurUser(val context: Context) {
    private var usersDao: UsersDao = UsersDao()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object{
        var INSTANCE: CurUser? = null
        fun getInstance(context: Context?) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: context?.let {
                    CurUser(it).also {
                        INSTANCE = it
                    }
                }
            }
    }
    fun getUser(): Users? {
        var user: Users? = null
        usersDao.ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.child(auth.uid.toString()).getValue(Users::class.java)!!

            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "User not created", Toast.LENGTH_SHORT).show()
            }

        })
        return user
    }



}