package android.example.uptoskills.Adapters

import android.content.Context
import android.example.uptoskills.R
import android.example.uptoskills.models.Job
import android.example.uptoskills.models.PaidCourse
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView

class MyJobAdapter(val context: Context, val listener: JobItemClicked, val itemId: Int): RecyclerView.Adapter<MyJobAdapter.JobViewHolder>() {

    private var jobs: ArrayList<Job> = ArrayList()

    inner class JobViewHolder(itemview: View): RecyclerView.ViewHolder(itemview) {
        var company_logo = itemview.findViewById<ImageView>(R.id.company_image)
        var job_type = itemView.findViewById<TextView>(R.id.jobtype)
        var jobtitle = itemview.findViewById<TextView>(R.id.jobname)
        var comapny_name = itemview.findViewById<TextView>(R.id.companyname)
        var jobLocation = itemview.findViewById<TextView>(R.id.jobLoc)
        var job_box = itemview.findViewById<MaterialCardView>(R.id.job_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val viewholder = JobViewHolder(LayoutInflater.from(parent.context).inflate(itemId, parent, false))
        viewholder.job_box.setOnClickListener {
            listener.onJobCLick(jobs[viewholder.adapterPosition].id)
        }
        return viewholder
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.comapny_name.text = jobs[position].company_name
        holder.jobLocation.text = jobs[position].candidate_required_location
        holder.job_type.text = jobs[position].job_type
        holder.jobtitle.text = jobs[position].title
        Glide.with(holder.company_logo.context).load(jobs[position].company_logo_url)
            .circleCrop().into(holder.company_logo)

    }

    override fun getItemCount(): Int {
        return jobs.size
    }

    fun updateJobs(job: ArrayList<Job>) {
        jobs.clear()
        jobs.addAll(job)
        notifyDataSetChanged()
    }
}
