package android.example.uptoskills

import android.content.Intent
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.databinding.ActivityUserDetailsBinding
import android.example.uptoskills.models.Users
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class UserDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetailsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userDao: UsersDao
    private val imageRequestCode = 123
    private lateinit var ProfileimageUrl: Uri
    private var isImageChoose: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.userDetailsProgressBar.visibility = View.VISIBLE


        auth = FirebaseAuth.getInstance()
        val id = intent.getStringExtra("id")
        userDao = UsersDao()

        updateProfile()

        binding.submit.setOnClickListener {
            var displayName: String = ""
            binding.userDetailsProgressBar.visibility = View.VISIBLE
            if(intent.getStringExtra("parent").toString().equals("MoreFragment")){
                displayName = binding.username.text.toString()
            }
            else{
                    displayName = intent.getStringExtra("username").toString()
            }
            var usr =
                Users(binding.fullName.editText?.text.toString(),
                    displayName,
                    binding.email.text.toString(),
                    binding.college.text.toString(),
                    binding.education.text.toString(),
                    binding.currentJob.text.toString(),
                    intent.getStringExtra("userImage").toString(),
                    binding.mobile.text.toString(),
                    id.toString())

            if(isImageChoose){
                userDao.uploadProfileImage(ProfileimageUrl, usr, this, id.toString())
                isImageChoose = false
            } else {
                userDao.ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var ref = snapshot.child(auth.uid.toString())
                        if(ref != null) {
                            usr.userImage = ref.child("userImage").getValue(String::class.java).toString()
                            userDao.addUser(usr, id.toString())
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
            Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
            binding.userDetailsProgressBar.visibility = View.GONE
            updateProfile()

            var isNewUser:String = intent.getStringExtra("Activity").toString()
            if(isNewUser.equals("NewUser")) {
                startMainActivity()
            }

        }

        //choose and set Profile Image
        binding.addProfileImage.setOnClickListener{
            filechoser()
        }
    }

    // Select the image file
    private fun filechoser() {
        isImageChoose = true
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

    // After Update in the database update the user Profile in the activity
    fun updateProfile(){
        binding.userDetailsProgressBar.visibility = View.VISIBLE
        userDao.ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var ref = snapshot.child(auth.uid.toString())
                binding.fullName.editText?.setText(ref.child("full_name").value.toString())
                binding.mobile.setText(ref.child("mobileNo").value.toString())
                binding.college.setText(ref.child("college_name").value.toString())
                binding.currentJob.setText(ref.child("job").value.toString())
                binding.education.setText(ref.child("education").value.toString())
                binding.username.setText(ref.child("displayName").value.toString())
                binding.email.setText(auth.currentUser?.email)
                var image: String = ref.child("userImage").value.toString()
                if(image.isNotEmpty() && image != "null") {
                    Glide.with(binding.profileImage.context).load(image).circleCrop().into(binding.profileImage)
                }
                binding.userDetailsProgressBar.visibility = View.GONE

            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}