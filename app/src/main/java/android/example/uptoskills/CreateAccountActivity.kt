package android.example.uptoskills

import android.content.Intent
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.databinding.ActivityCreateAccountBinding
import android.example.uptoskills.models.Users
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class CreateAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAccountBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var referId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        referId = intent.getStringExtra("ReferId").toString()

        auth = FirebaseAuth.getInstance()

        binding.crtaccount.setOnClickListener {
            binding.createAccountProgressBar.visibility = View.VISIBLE
            if(binding.crtemail.text.toString().isNotBlank() && binding.crtpass.text.toString()
                    .isNotBlank() && binding.crtUsername.text.isNotBlank()
            ){
                auth.createUserWithEmailAndPassword(binding.crtemail.text.toString(), binding.crtpass.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful){
                        changeAuth()
                        binding.createAccountProgressBar.visibility = View.GONE

                        //adding user to db
                        val user: Users = Users(hashMapOf(), hashMapOf(),binding.crtUsername.text.toString(), binding.crtUsername.text.toString(),
                        binding.crtemail.text.toString(), "", "", "", "", "",
                            it.result.user?.uid!!, "", referId, 250)
                        val userDao = UsersDao()
                        userDao.addUser(user, it.result.user?.uid.toString())

                        userDao.ref.child(referId).addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val referer = snapshot.getValue(Users::class.java)
                                if(referer != null) {
                                    referer.coins += 500
                                    userDao.updateUser(referer, referId)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })


                        val id: String? = it.result.user?.uid
                        val intent = Intent(this, UserDetailsActivity::class.java)
                        intent.putExtra("username", binding.crtUsername.text.toString())
                        intent.putExtra("email", binding.crtemail.text.toString())
                        intent.putExtra("id", id)
                        intent.putExtra("userImage", "")
                        intent.putExtra("Activity", "NewUser")
                        startActivity(intent)
                        finish()
                    }
                    else {
                        binding.createAccountProgressBar.visibility = View.GONE
                        Toast.makeText(this, it.exception?.message.toString(),
                            Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                binding.createAccountProgressBar.visibility = View.GONE
                Toast.makeText(this, "Please Fill the required details",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun changeAuth() {
        val user = FirebaseAuth.getInstance().currentUser

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(binding.crtUsername.text.toString()).build()
        user!!.updateProfile(profileUpdates)
        }
}