package android.example.uptoskills

import android.content.Intent
import android.example.uptoskills.daos.InternshipDao
import android.example.uptoskills.daos.JobDao
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.models.Job
import android.example.uptoskills.databinding.ActivityJobViewBinding
import android.example.uptoskills.models.Internship
import android.example.uptoskills.models.Users
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class JobViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobViewBinding
    private lateinit var jobId: String
    private var isJob: Boolean = true
    private var call: String = ""
    private var curUser: Users = Users()
    private lateinit var userDao: UsersDao
    private lateinit var auth: FirebaseAuth
    private lateinit var jobDao: JobDao

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        call = intent.getStringExtra("parent").toString()
        jobId = intent.getStringExtra("jobId").toString()
        isJob = intent.getStringExtra("category") == "job"

        userDao = UsersDao()
        auth = FirebaseAuth.getInstance()
        jobDao = JobDao()
//        if(!isJob) {
//            binding.isBookmarked.visibility = View.GONE
//        }
        GlobalScope.launch(Dispatchers.IO) {
            curUser = auth.currentUser?.let { userDao.getUserById(it.uid).await().toObject(Users::class.java) }!!

            withContext(Dispatchers.Main) {
                if(call.trim().lowercase() == "applied" || curUser.appliedJobs?.containsKey(jobId) == true) {
                    binding.Applybtn.visibility = View.GONE
                    binding.alreadyApplied.visibility = View.VISIBLE
                } else {
                    binding.alreadyApplied.visibility = View.GONE
                    binding.Applybtn.visibility = View.VISIBLE
                }
                if(curUser.bookmarks?.containsKey(jobId) == true && curUser.bookmarks!![jobId] == intent.getStringExtra("category")) {
                    binding.isBookmarked.setImageDrawable(ContextCompat.getDrawable(binding.isBookmarked.context, R.drawable.ic_bookmarked))
                } else {
                    binding.isBookmarked.setImageDrawable(ContextCompat.getDrawable(binding.isBookmarked.context, R.drawable.ic_unbookmark))
                }
            }
        }


        fillActivity()



        binding.Applybtn.setOnClickListener {
            val intent = Intent(this, UserDetailsActivity::class.java)
            intent.putExtra("parent", "job")
            intent.putExtra("isJob", isJob)
            intent.putExtra("jobId", jobId)
            intent.putExtra("parent", "jobActivity")
            startActivity(intent)
            finish()
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.isBookmarked.setOnClickListener {
            Log.e(jobId, isJob.toString())
            Log.e("isJob", isJob.toString())
            if(isJob) {
                if (curUser.bookmarks?.containsKey(jobId) == true && curUser.bookmarks!![jobId] == intent.getStringExtra("category")) {
                    binding.isBookmarked.setImageDrawable(ContextCompat.getDrawable(binding.isBookmarked.context,
                        R.drawable.ic_unbookmark))
                    curUser.bookmarks?.remove(jobId,
                        intent.getStringExtra("category").toString().trim())
                } else {
                    binding.isBookmarked.setImageDrawable(ContextCompat.getDrawable(binding.isBookmarked.context,
                        R.drawable.ic_bookmarked))

                    curUser.bookmarks?.set(jobId,
                        intent.getStringExtra("category").toString().trim())
                }

                jobDao.addbookmark(jobId)
            } else {
                if (curUser.bookmarks?.containsKey(jobId) == true && curUser.bookmarks!![jobId] == intent.getStringExtra("category")) {
                    binding.isBookmarked.setImageDrawable(ContextCompat.getDrawable(binding.isBookmarked.context,
                        R.drawable.ic_unbookmark))
                    curUser.bookmarks?.remove(jobId,
                        "internship")
                } else {
                    binding.isBookmarked.setImageDrawable(ContextCompat.getDrawable(binding.isBookmarked.context,
                        R.drawable.ic_bookmarked))

                    curUser.bookmarks?.set(jobId,
                        "internship")
                }
                InternshipDao().addbookmark(jobId)
            }
            auth.currentUser?.let { userDao.updateUser(curUser, it.uid) }
        }

    }

    private fun fillActivity(){
        GlobalScope.launch(Dispatchers.IO) {
            if(isJob) {
                val jobDao = JobDao()
                val job: Job? = jobDao.getJobbyId(jobId.trim()).await().toObject(Job::class.java)

                if(job != null) {
                    withContext(Dispatchers.Main) {
                        binding.basicQualification.text = job.basic_requirements
                        binding.preferredQualification.text = job.preferred_requirements
                        binding.companyname.text = job.company_name
                        binding.description.text = job.description
                        binding.location.text = job.candidate_required_location
                        binding.title.text = job.title
                        binding.totalApplied.text = job.applied.size.toString()
                        Glide.with(this@JobViewActivity).load(job.company_logo_url).circleCrop().into(binding.logo)

                    }
                } else {

                }
            } else {
                val internshipDao = InternshipDao()
                val internship: Internship? = internshipDao.getJobbyId(jobId.trim()).await().toObject(Internship::class.java)

                if(internship != null) {
                    withContext(Dispatchers.Main) {
                        binding.title.text = internship.title
                        binding.location.text = internship.candidate_required_location
                        binding.description.text = internship.description
                        binding.preferredQualification.text = internship.preferred_requirements
                        binding.basicQualification.text = internship.basic_requirements
                        binding.companyname.text = internship.company_name
                        binding.totalApplied.text = internship.applied.size.toString()
                        Glide.with(this@JobViewActivity).load(internship
                            .company_logo_url).circleCrop().into(binding.logo)
                    }
                } else {
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@JobViewActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if(parent == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        super.onBackPressed()
    }
}