package android.example.uptoskills.Adapters

import android.content.Context
import android.example.uptoskills.R
import android.example.uptoskills.models.Internship
import android.example.uptoskills.models.Job
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.card.MaterialCardView

class InternshipAdapter(val context: Context, options: FirestoreRecyclerOptions<Internship>, val listener: JobItemClicked, val itemId: Int) : FirestoreRecyclerAdapter<Internship, InternshipAdapter.JobViewholder>(
    options
){
    inner class JobViewholder(itemview: View): RecyclerView.ViewHolder(itemview) {
        var company_logo = itemview.findViewById<ImageView>(R.id.company_image)
        var job_type = itemView.findViewById<TextView>(R.id.jobtype)
        var jobtitle = itemview.findViewById<TextView>(R.id.jobname)
        var comapny_name = itemview.findViewById<TextView>(R.id.companyname)
        var jobLocation = itemview.findViewById<TextView>(R.id.jobLoc)
        var job_box = itemview.findViewById<MaterialCardView>(R.id.job_container)
        var salary: TextView = itemView.findViewById(R.id.salary)
        var salarySymbol: TextView = itemview.findViewById(R.id.rupee_symbol)
        val shareBtn: ImageView = itemview.findViewById(R.id.share_btn);
        val bookmarked = itemview.findViewById<ImageView>(R.id.bookmark_btn);
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewholder {
        val viewHolder = JobViewholder(LayoutInflater.from(parent.context)
            .inflate(itemId, parent, false))
        viewHolder.job_box.setOnClickListener {
            listener.onJobCLick(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        viewHolder.bookmarked.setOnClickListener {
            val result = listener.onbookmarkCLick(snapshots.getSnapshot(viewHolder.adapterPosition).id, "internship");
            if(result) {
                Toast.makeText(context, "Job Bookmarked Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Job Bookmark Removed", Toast.LENGTH_SHORT).show()
            }
        }
        viewHolder.shareBtn.setOnClickListener {
            listener.shareJob(snapshots.getSnapshot(viewHolder.adapterPosition).id, "internship")
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: JobViewholder, position: Int, model: Internship) {
        holder.comapny_name.text = model.company_name
        holder.jobLocation.text = model.candidate_required_location
//        holder.job_type.text = model.job_type
        holder.jobtitle.text = model.title
        if(model.salary.trim().isEmpty()) {
            holder.salary.isVisible=false
            holder.salarySymbol.isVisible=false
        } else {
            holder.salary.text = model.salary.trim()
        }
        Glide.with(holder.company_logo.context).load(model.company_logo_url)
            .circleCrop().into(holder.company_logo)
    }


}