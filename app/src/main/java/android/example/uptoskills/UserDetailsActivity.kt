package android.example.uptoskills

import android.content.Intent
import android.content.pm.PackageManager
import android.example.uptoskills.daos.*
import android.example.uptoskills.databinding.ActivityUserDetailsBinding
import android.example.uptoskills.models.Users
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.razorpay.Checkout


class UserDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetailsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userDao: UsersDao
    private val imageRequestCode = 123
    private lateinit var ProfileimageUrl: Uri
    private var isImageChoose: Boolean = false
    private var isResumeSelected: Boolean = false
    private val resumeRequestCode: Int = 2
    private val READ_EXTERNAL_STORAGE_CODE: Int = 9
    private lateinit var resumeUri: Uri
    private lateinit var usr: Users


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.userDetailsProgressBar.visibility = View.VISIBLE

        Checkout.preload(applicationContext)
        Checkout.clearUserData(this)
        auth = FirebaseAuth.getInstance()

        userDao = UsersDao()

        updateProfile()

        binding.submit.setOnClickListener {
            val id = auth.currentUser?.uid
            var displayName: String = ""
            binding.userDetailsProgressBar.visibility = View.VISIBLE
            if(intent.getStringExtra("parent").toString().equals("MoreFragment") || intent.getStringExtra("parent").toString().equals("course")
                || intent.getStringExtra("parent") == "job"){
                displayName = binding.username.text.toString()
            }

            // Updating Resume and ProfileImage
            userDao.ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var ref = snapshot.child(auth.currentUser?.uid.toString())
                    if(ref.exists()) {
                        val freecourses: HashMap<String, String>? = ref.child("freecourses").getValue(object: GenericTypeIndicator<HashMap<String, String>>(){})
                        val paidcourses: HashMap<String, String>? = ref.child("paidcourses").getValue(object: GenericTypeIndicator<HashMap<String, String>>(){})
                        val bookmarks: HashMap<String, String>? = ref.child("bookmarks").getValue(object: GenericTypeIndicator<HashMap<String, String>>(){})
                        val appliedJobs: HashMap<String, String>? = ref.child("appliedJobs").getValue(object: GenericTypeIndicator<HashMap<String, String>>(){})

                        usr =
                            ref.child("coins").getValue(Int::class.java)?.let { it1 ->
                                Users(freecourses,paidcourses,
                                    binding.fullName.editText?.text.toString(),
                                    displayName,
                                    binding.email.text.toString(),
                                    binding.college.text.toString(),
                                    binding.education.text.toString(),
                                    binding.currentJob.text.toString(),
                                    ref.child("userImage").getValue(String::class.java).toString(),
                                    binding.mobile.text.toString(),
                                    id.toString(), ref.child("resume").getValue(String::class.java).toString(),
                                    ref.child("referCode").getValue(String::class.java).toString(),
                                    it1, bookmarks, appliedJobs)
                            }!!
                        if(ref.child("resume").getValue(String::class.java).toString().isNotEmpty()){
                            binding.resumeImage.setImageResource(R.drawable.ic_resume)
                        }

                        if(isImageChoose && isResumeSelected) {
                            if(ProfileimageUrl.toString().isNotEmpty() && resumeUri.toString().isNotEmpty()){
                                userDao.uploadResumeWithImage(resumeUri, ProfileimageUrl, usr, this@UserDetailsActivity, id.toString())
                                isImageChoose = false
                                isResumeSelected = false
                            }
                        }
                        else if(isResumeSelected) {

                            if (usr != null) {
                                userDao.uploadResume(resumeUri, usr, this@UserDetailsActivity, id.toString())
                            }
                            isResumeSelected = false

                        }
                        else if(isImageChoose){

                            if (usr != null) {
                                userDao.uploadProfileImage(ProfileimageUrl, usr, this@UserDetailsActivity, id.toString())
                            }
                            isImageChoose = false

                        } else {
                            userDao.addUser(usr, id.toString())
                        }

                        binding.userDetailsProgressBar.visibility = View.GONE
                        var isNewUser:String = intent.getStringExtra("Activity").toString()
                        if(isNewUser == "NewUser") {
                            startMainActivity()
                        }
                        else if(intent.getStringExtra("parent") == "job") {
                            if(usr.full_name.trim().isEmpty()  || usr.mobileNo.trim().isEmpty()
                                || usr.education.trim().isEmpty() || usr.job.trim().isEmpty()
                                || usr.college_name.trim().isEmpty() || usr.resume.trim().isEmpty()) {

                                Toast.makeText(this@UserDetailsActivity, "Please Provide your full Details", Toast.LENGTH_SHORT).show()
                                // checking job or internship

                            } else {
                                if(intent.getBooleanExtra("isJob", true) ) {
                                    val jobDao = JobDao()
                                    jobDao.applyJob(intent.getStringExtra("jobId")!!, this@UserDetailsActivity)
                                } else {
                                    val internDao = InternshipDao()
                                    internDao.applyJob(intent.getStringExtra("jobId")!!, this@UserDetailsActivity)
                                }
                            }
                        }
                        else if(intent.getStringExtra("parent") == "course") {
                            Toast.makeText(this@UserDetailsActivity, "Profile Updated Successfully, Now Enroll...", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@UserDetailsActivity, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

        }


        //choose and set Profile Image
        binding.addProfileImage.setOnClickListener{
            filechoser()
        }

        // choose and upload the resume
        binding.uploadResume.setOnClickListener {
            // Checking the Permissions
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                resumeChooser()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE_CODE)
            }

        }

        // download resume
        binding.resumeImage.setOnClickListener {
            binding.userDetailsProgressBar.visibility = View.VISIBLE
            userDao.ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var ref = snapshot.child(auth.currentUser?.uid.toString())
                    if(ref.exists()) {
                        var resumeUrl = ref.child("resume").getValue(String::class.java).toString()
                        if(!resumeUrl.isBlank()) {
                            val builder = CustomTabsIntent.Builder()
                            val customTabsIntent = builder.build()
                            customTabsIntent.launchUrl(this@UserDetailsActivity, Uri.parse(resumeUrl))
                            binding.userDetailsProgressBar.visibility = View.GONE
                        } else {
                            Toast.makeText(this@UserDetailsActivity, "Please upload the Resume", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if(requestCode == READ_EXTERNAL_STORAGE_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            resumeChooser()
        }
    }

    private fun resumeChooser() {
        isResumeSelected = true
        var intent: Intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, resumeRequestCode)
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
        } else if(requestCode == resumeRequestCode && resultCode == RESULT_OK && data?.data != null) {
            resumeUri = data.data!! // Return the pdf uri
            binding.resumeImage.setImageResource(R.drawable.ic_resume)
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
                binding.fullName.editText?.setText(if(ref.child("full_name").value.toString() != "null") ref.child("full_name").value.toString() else "")
                binding.mobile.setText(if(ref.child("mobileNo").value.toString() != "null") ref.child("mobileNo").value.toString() else "")
                binding.college.setText(if(ref.child("college_name").value.toString() != "null") ref.child("college_name").value.toString() else "")
                binding.currentJob.setText(if(ref.child("job").value.toString() != "null") ref.child("job").value.toString() else "")
                binding.education.setText(if(ref.child("education").value.toString() != "null") ref.child("education").value.toString() else "")
                binding.username.setText(if(ref.child("displayName").value.toString() != "null") ref.child("displayName").value.toString() else "")
                binding.email.setText(if(ref.child("email").value.toString() != "null") ref.child("email").value.toString() else "")
                var image: String = ref.child("userImage").value.toString()
                if(image.isNotEmpty() && image != "null") {
                    binding.profileImage.setImageResource(R.drawable.image_circle)
                    Glide.with(binding.profileImage.context).load(image).circleCrop().into(binding.profileImage)
                }
                if(ref.child("resume").value.toString().isNotBlank()) {
                    binding.resumeImage.setImageResource(R.drawable.ic_resume)
                }
                binding.userDetailsProgressBar.visibility = View.GONE

            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}