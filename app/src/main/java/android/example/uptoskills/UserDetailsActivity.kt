package android.example.uptoskills

import android.content.Intent
import android.content.pm.PackageManager
import android.example.uptoskills.daos.*
import android.example.uptoskills.databinding.ActivityUserDetailsBinding
import android.example.uptoskills.models.Users
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
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
//import com.razorpay.Checkout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class UserDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetailsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userDao: UsersDao
    private val imageRequestCode = 123
    private var ProfileimageUrl: Uri = Uri.EMPTY
    private var isImageChoose: Boolean = false
    private var isResumeSelected: Boolean = false
    private val resumeRequestCode: Int = 2
    private val READ_EXTERNAL_STORAGE_CODE: Int = 9
    private var resumeUri: Uri = Uri.EMPTY
    private lateinit var usr: Users
    private lateinit var userDetailsLayout: LinearLayout

    override fun onResume() {
        super.onResume()
        GlobalScope.launch(Dispatchers.IO) {
            usr = auth.currentUser?.let { userDao.getUserById(it.uid).await().toObject(Users::class.java) }!!
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        CONSTANTS.getInstance(this)
        binding.userDetailsProgressBar.visibility = View.VISIBLE
        if(intent.getStringExtra("parent") == "jobActivity") {
            Toast.makeText(this, "Please Verify your Details...", Toast.LENGTH_SHORT).show()
        }

        userDetailsLayout = binding.userDetailsLayout

//        Checkout.preload(applicationContext)
//        Checkout.clearUserData(this)
        auth = FirebaseAuth.getInstance()

        userDao = UsersDao()

        updateProfile()

        binding.submit.setOnClickListener {
            val id = auth.currentUser?.uid
            var displayName: String = binding.username.text.toString()
            binding.userDetailsProgressBar.visibility = View.VISIBLE

            // Updating Resume and ProfileImage

            GlobalScope.launch(Dispatchers.IO) {
                val curUser = auth.currentUser?.let { it1 -> userDao.getUserById(it1.uid).await().toObject(Users::class.java) }
                if(curUser != null) {
                    usr = Users(binding.achievements.editText?.text.toString().trim(),
                        binding.projecttitle.editText?.text.toString().trim(),
                        binding.Projectdesc.editText?.text.toString().trim(),
                        binding.skills.editText?.text.toString().trim(),
                        binding.address.editText?.text.toString().trim(),
                        binding.exptitle.editText?.text.toString().trim(),
                        binding.expdesc.editText?.text.toString().trim(),
                        curUser.freecourses,
                        curUser.paidcourses,
                        binding.fullName.editText?.text.toString().trim(),
                        displayName.trim(),
                        binding.email.text.toString().trim(),
                        binding.college.text.toString().trim(),
                        binding.education.text.toString().trim(),
                        binding.currentJob.text.toString().trim(),
                        curUser.userImage,
                        binding.mobile.text.toString().trim(),
                        id.toString().trim(),
                        curUser.resume,
                        curUser.referCode,
                        curUser.coins,
                        curUser.bookmarks,
                        curUser.appliedJobs,
                        curUser.events)
                    withContext(Dispatchers.Main) {

                    /*    if(curUser.resume.trim().isNotBlank()) {
                            binding.resumeImage.setImageResource(R.drawable.ic_resume)
                        }*/

                        if(isImageChoose && isResumeSelected) {
                            if(ProfileimageUrl.toString().isNotEmpty() && resumeUri.toString().isNotEmpty()){
                                userDao.uploadResumeWithImage(resumeUri, ProfileimageUrl, usr, this@UserDetailsActivity, id.toString())
                                isImageChoose = false
                                isResumeSelected = false
                            }
                        }
                        else if(isResumeSelected) {

                            userDao.uploadResume(resumeUri, usr, this@UserDetailsActivity, id.toString())
                            isResumeSelected = false

                        }
                        else if(isImageChoose){

                            userDao.uploadProfileImage(ProfileimageUrl, usr, this@UserDetailsActivity, id.toString())
                            isImageChoose = false

                        } else {
                            userDao.addUser(usr, id.toString())
                        }

                        binding.userDetailsProgressBar.visibility = View.GONE
                        val isNewUser:String = intent.getStringExtra("Activity").toString()
                        if(isNewUser == "NewUser") {
                            startMainActivity()
                        }
                        else if(intent.getStringExtra("parent").toString().trim() == "jobActivity") {
                            if(usr.full_name.trim().isEmpty()  || usr.mobileNo.trim().isEmpty()
                                || usr.education.trim().isEmpty() || usr.job.trim().isEmpty()
                                || usr.college_name.trim().isEmpty() || usr.resume.trim().isEmpty()) {

                                Toast.makeText(this@UserDetailsActivity, "Please Provide your full Details", Toast.LENGTH_SHORT).show()
                                // checking job or internship

                            } else {
                                if(intent.getBooleanExtra("isJob", true) ) {
                                    val jobDao = JobDao()
                                    jobDao.applyJob(intent.getStringExtra("jobId").toString(), this@UserDetailsActivity, usr)
                                } else {
                                    val internDao = InternshipDao()
                                    internDao.applyJob(intent.getStringExtra("jobId").toString(), this@UserDetailsActivity, usr)
                                }
//                                finish()
                            }
                        }
                        else if(intent.getStringExtra("parent") == "course") {
                            CONSTANTS.setUsername(usr.full_name)
                            CONSTANTS.setEmail(usr.email)
                            Toast.makeText(this@UserDetailsActivity, "Profile Updated Successfully, Now Enroll...", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            CONSTANTS.setUsername(usr.full_name)
                            CONSTANTS.setEmail(usr.email)
                            Toast.makeText(this@UserDetailsActivity, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                        }
                    }

                }

            }

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


        // Download Resume
        binding.downloadProfile.setOnClickListener {
            val resume = Resume(usr, this)
            resume.createResume()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_EXTERNAL_STORAGE_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            resumeChooser()
        }
    }

    private fun resumeChooser() {
        isResumeSelected = true
        val intent: Intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, resumeRequestCode)
    }

    // Select the image file
    private fun filechoser() {
        isImageChoose = true
        val intent = Intent();
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
            /*binding.resumeImage.setImageResource(R.drawable.ic_resume)*/
        }
    }

    fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // After Update in the database update the user Profile in the activity
    fun updateProfile(){
        binding.userDetailsProgressBar.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {
            val usr = auth.currentUser?.uid?.let { userDao.getUserById(it).await().toObject(Users::class.java) }
            withContext(Dispatchers.Main) {
                if(usr != null) {
                    binding.projecttitle.editText?.setText(usr.projectTitle.trim())
                    binding.Projectdesc.editText?.setText(usr.projectDesc.trim())
                    binding.address.editText?.setText(usr.address.trim())
                    binding.skills.editText?.setText(usr.skills.trim())
                    binding.exptitle.editText?.setText(usr.experiencetitle.trim())
                    binding.expdesc.editText?.setText(usr.experienceDesc.trim())
                    binding.fullName.editText?.setText(usr.full_name.trim())
                    binding.mobile.setText(usr.mobileNo.trim())
                    binding.college.setText(usr.college_name.trim())
                    binding.currentJob.setText(usr.job.trim())
                    binding.education.setText(usr.education.trim())
                    binding.username.setText(usr.displayName?.trim())
                    binding.email.setText(usr.email.trim())
                    binding.achievements.editText?.setText(usr.achievements.trim())
                    var image: String = usr.userImage.trim()
                    if(image.isNotEmpty() && image != "null") {
                        binding.profileImage.setImageResource(R.drawable.image_circle)
                        Glide.with(binding.profileImage.context).load(image).circleCrop().into(binding.profileImage)
                    }
                    binding.userDetailsProgressBar.visibility = View.GONE

                }
            }
        }
    }

    override fun onBackPressed() {
        if (parent == null) {
            startMainActivity()
        }
        super.onBackPressed()
    }
}