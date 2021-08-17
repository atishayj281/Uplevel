package android.example.uptoskills

import android.content.Intent
import android.example.uptoskills.daos.InternshipDao
import android.example.uptoskills.daos.JobDao
import android.example.uptoskills.models.Job
import android.example.uptoskills.databinding.ActivityJobViewBinding
import android.example.uptoskills.models.Internship
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class JobViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobViewBinding
    private lateinit var jobId: String
    private var isJob: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        jobId = intent.getStringExtra("jobId").toString()
        isJob = intent.getStringExtra("category") == "job"

        fillActivity()

        binding.Applybtn.setOnClickListener {
            val intent = Intent(this, UserDetailsActivity::class.java)
            intent.putExtra("parent", "job")
            intent.putExtra("isJob", isJob)
            intent.putExtra("jobId", jobId)
            startActivity(intent)
        }

        binding.back.setOnClickListener {
            finish()
        }
    }

    private fun fillActivity(){
        GlobalScope.launch(Dispatchers.IO) {
            if(isJob) {
                val jobDao = JobDao()
                val job: Job? = jobDao.getJobbyId(jobId).await().toObject(Job::class.java)
                Log.e("job", jobId)
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
                    Log.e("job is null", "Job is null")
                }
            } else {
                val internshipDao = InternshipDao()
                val internship: Internship? = internshipDao.getJobbyId(jobId).await().toObject(Internship::class.java)

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
                }
            }
        }
    }
}