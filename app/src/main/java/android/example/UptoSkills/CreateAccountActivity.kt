package android.example.UptoSkills

import android.content.Intent
import android.example.UptoSkills.databinding.ActivityCreateAccountBinding
import android.example.UptoSkills.models.Users
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAccountBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()


        binding.crtaccount.setOnClickListener {
            if(binding.crtemail.text.toString().length != 0 && binding.crtpass.text.toString().length != 0){
                auth.createUserWithEmailAndPassword(binding.crtemail.text.toString(), binding.crtpass.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful){
                        //var usr = Users("", binding.crtUsername.text.toString(), binding.crtemail.text.toString())
                        var id: String? = it.result?.user?.uid
                        var intent = Intent(this, UserDetailsActivity::class.java)
                        intent.putExtra("username", binding.crtUsername.text.toString())
                        intent.putExtra("email", binding.crtemail.text.toString())
                        intent.putExtra("id", id)

                        startActivity(intent)
                        finish()
                    }
                    else {
                        Toast.makeText(this, it.exception?.message.toString(),
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}