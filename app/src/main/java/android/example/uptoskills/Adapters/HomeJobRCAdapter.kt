package android.example.uptoskills.Adapters

import android.example.uptoskills.R
import android.example.uptoskills.models.Job
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.card.MaterialCardView

class HomeJobRCAdapter(options: FirestoreRecyclerOptions<Job>, val listener: JobItemClicked, val itemId: Int) : FirestoreRecyclerAdapter<Job, HomeJobRCAdapter.JobViewholder>(
    options
){
    inner class JobViewholder(itemview: View): RecyclerView.ViewHolder(itemview) {
        var company_logo = itemview.findViewById<ImageView>(R.id.company_image)
        var job_type = itemView.findViewById<TextView>(R.id.jobtype)
        var jobtitle = itemview.findViewById<TextView>(R.id.jobname)
        var comapny_name = itemview.findViewById<TextView>(R.id.companyname)
        var jobLocation = itemview.findViewById<TextView>(R.id.jobLoc)
        var job_box = itemview.findViewById<MaterialCardView>(R.id.job_container)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewholder {
        val viewHolder = JobViewholder(LayoutInflater.from(parent.context)
            .inflate(itemId, parent, false))
        viewHolder.job_box.setOnClickListener {
            listener.onJobCLick(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: JobViewholder, position: Int, model: Job) {
        holder.comapny_name.text = model.company_name
        holder.jobLocation.text = model.candidate_required_location
        holder.job_type.text = model.job_type
        holder.jobtitle.text = model.title
        Glide.with(holder.company_logo.context).load(model.company_logo_url)
            .circleCrop().into(holder.company_logo)
    }


}