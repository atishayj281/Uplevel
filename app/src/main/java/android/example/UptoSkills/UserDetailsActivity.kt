package android.example.UptoSkills

import android.app.DownloadManager
import android.content.Intent
import android.example.UptoSkills.daos.UsersDao
import android.example.UptoSkills.databinding.ActivityUserDetailsBinding
import android.example.UptoSkills.models.Users
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetailsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var user: Users
    private lateinit var userDao: UsersDao
    private val imageRequestCode = 123
    private lateinit var ProfileimageUrl: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.userDetailsProgressBar.visibility = View.VISIBLE

        if (Build.VERSION.SDK_INT >= 21) {
            var window: Window = this.window
            window.statusBarColor = ContextCompat.getColor(this, R.color.LightningYellow)
        }

        auth = FirebaseAuth.getInstance()
        var id = intent.getStringExtra("id")
        userDao = UsersDao()

        userDao.ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var ref = snapshot.child(auth.uid.toString())
                binding.fullName.editText?.setText(ref.child("full_name").value.toString())
                binding.mobile.setText(ref.child("mobileNo").value.toString())
                binding.college.setText(ref.child("college_name").value.toString())
                binding.currentJob.setText(ref.child("job").value.toString())
                binding.education.setText(ref.child("education").value.toString())
                binding.username.setText(ref.child("displayName").value.toString())
                binding.userDetailsProgressBar.visibility = View.GONE
                binding.email.setText(auth.currentUser?.email)
                var image: String = ref.child("userImage").value.toString()
                if(image.isNotEmpty() && image != "null") {
                    Glide.with(binding.profileImage.context).load(image).circleCrop().into(binding.profileImage)
                }

            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.submit.setOnClickListener {
            binding.userDetailsProgressBar.visibility = View.VISIBLE
            var usr =
                Users(binding.fullName.editText?.text.toString(),
                    intent.getStringExtra("username"),
                    binding.email.text.toString(),
                    binding.college.text.toString(),
                    binding.education.text.toString(),
                    binding.currentJob.text.toString(),
                    intent.getStringExtra("userImage").toString(),
                    binding.mobile.text.toString(),
                    id.toString())
            userDao.uploadProfileImage(ProfileimageUrl, usr, this, id.toString())
            Toast.makeText(this, id, Toast.LENGTH_SHORT).show()
            binding.userDetailsProgressBar.visibility
            startMainActivity()
        }

        //choose and set Profile Image
        binding.addProfileImage.setOnClickListener{
            filechoser()
        }
    }

    // Select the image file
    private fun filechoser() {
        var intent = Intent();
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, imageRequestCode)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == imageRequestCode && resultCode == RESULT_OK && data?.data != null) {
            ProfileimageUrl = data.data!!
            binding.profileImage.setImageURI(ProfileimageUrl)
        }
    }

    fun startMainActivity() {
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}