package android.example.UptoSkills

import android.content.Intent
import android.example.UptoSkills.databinding.ActivityCreateAccountBinding
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest


class CreateAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAccountBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()


        binding.crtaccount.setOnClickListener {
            binding.createAccountProgressBar.visibility = View.VISIBLE
            if(binding.crtemail.text.toString().isNotEmpty() && binding.crtpass.text.toString()
                    .isNotEmpty()
            ){
                auth.createUserWithEmailAndPassword(binding.crtemail.text.toString(), binding.crtpass.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful){

                        changeAuth()

                        binding.createAccountProgressBar.visibility = View.GONE
                        //var usr = Users("", binding.crtUsername.text.toString(), binding.crtemail.text.toString())
                        val id: String? = it.result?.user?.uid
                        val intent = Intent(this, UserDetailsActivity::class.java)
                        intent.putExtra("username", binding.crtUsername.text.toString())
                        intent.putExtra("email", binding.crtemail.text.toString())
                        intent.putExtra("id", id)
                        intent.putExtra("userImage", "")

                        startActivity(intent)
                        finish()
                    }
                    else {
                        binding.createAccountProgressBar.visibility = View.GONE
                        Toast.makeText(this, it.exception?.message.toString(),
                            Toast.LENGTH_SHORT).show()
                    }
                }
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