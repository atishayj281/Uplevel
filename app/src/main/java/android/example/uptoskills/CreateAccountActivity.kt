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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


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

        }
    }

    private fun changeAuth() {
        val user = FirebaseAuth.getInstance().currentUser

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(binding.crtUsername.text.toString()).build()
        user!!.updateProfile(profileUpdates)
        }
}