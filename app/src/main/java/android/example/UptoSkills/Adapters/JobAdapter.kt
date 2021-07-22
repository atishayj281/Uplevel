package android.example.UptoSkills.Adapters

import android.content.Context;
import android.example.UptoSkills.Fragment.JobFragment
import android.example.UptoSkills.R
import android.example.UptoSkills.models.Job
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide
import java.util.ArrayList;

class JobAdapter(val context: Context,val listner: JobItemClicked): RecyclerView.Adapter<JobAdapter.JobViewholder>(){

    private var items = arrayListOf<Job>(Job("1437137", "https://www.amazon.jobs/en/jobs/1437137/software-development-engineer-ii",
    "Software Development Engineer II", "Amazon", "Software Development",
    "Full Time", "", "Chennai, India",
    "We are looking for passionate, hard-working, and talented software engineers who have experience building innovative, mission critical, high volume applications that customers love. This is a high visibility team where you will get a chance to make a positive impact on customer experience.\n" +
            "\n" +
            "Amazon FireTV products are revolutionary consumer devices that have become the #1 best-selling Amazon products. They are among the most innovative and fastest growing businesses at both Amazon and the entire consumer electronics industry. We are looking for talented software engineers to join our team in building application software. This is an enormous opportunity to work on products used every day by people you know.\n" +
            "\n" +
            "As a software development engineer on this team, you will play a pivotal role in shaping the definition, vision, design, roadmap and development of product features from beginning to end. You will:\n" +
            "· Work with the team to help solve business problems.\n" +
            "· Design, implement, test, deploy and maintain innovative software solutions to transform service performance, durability, cost, and security.\n" +
            "· Use software engineering best practices to ensure a high standard of quality for all of the team deliverables.\n" +
            "· Write high quality distributed system software.\n" +
            "· Work in an agile, startup-like development environment, where you are always working on the most important stuff.",
    "https://merivis.org/wp-content/uploads/2018/02/Amazon-Logo-Transparent-PNG.png",
    ), Job("1437137", "https://www.amazon.jobs/en/jobs/1437137/software-development-engineer-ii",
        "Software Development Engineer II", "Amazon", "Software Development",
        "Full Time", "", "Chennai, India",
        "We are looking for passionate, hard-working, and talented software engineers who have experience building innovative, mission critical, high volume applications that customers love. This is a high visibility team where you will get a chance to make a positive impact on customer experience.\n" +
                "\n" +
                "Amazon FireTV products are revolutionary consumer devices that have become the #1 best-selling Amazon products. They are among the most innovative and fastest growing businesses at both Amazon and the entire consumer electronics industry. We are looking for talented software engineers to join our team in building application software. This is an enormous opportunity to work on products used every day by people you know.\n" +
                "\n" +
                "As a software development engineer on this team, you will play a pivotal role in shaping the definition, vision, design, roadmap and development of product features from beginning to end. You will:\n" +
                "· Work with the team to help solve business problems.\n" +
                "· Design, implement, test, deploy and maintain innovative software solutions to transform service performance, durability, cost, and security.\n" +
                "· Use software engineering best practices to ensure a high standard of quality for all of the team deliverables.\n" +
                "· Write high quality distributed system software.\n" +
                "· Work in an agile, startup-like development environment, where you are always working on the most important stuff.",
        "https://merivis.org/wp-content/uploads/2018/02/Amazon-Logo-Transparent-PNG.png",
    ), Job("1437137", "https://www.amazon.jobs/en/jobs/1437137/software-development-engineer-ii",
        "Software Development Engineer II", "Amazon", "Software Development",
        "Full Time", "", "Chennai, India",
        "We are looking for passionate, hard-working, and talented software engineers who have experience building innovative, mission critical, high volume applications that customers love. This is a high visibility team where you will get a chance to make a positive impact on customer experience.\n" +
                "\n" +
                "Amazon FireTV products are revolutionary consumer devices that have become the #1 best-selling Amazon products. They are among the most innovative and fastest growing businesses at both Amazon and the entire consumer electronics industry. We are looking for talented software engineers to join our team in building application software. This is an enormous opportunity to work on products used every day by people you know.\n" +
                "\n" +
                "As a software development engineer on this team, you will play a pivotal role in shaping the definition, vision, design, roadmap and development of product features from beginning to end. You will:\n" +
                "· Work with the team to help solve business problems.\n" +
                "· Design, implement, test, deploy and maintain innovative software solutions to transform service performance, durability, cost, and security.\n" +
                "· Use software engineering best practices to ensure a high standard of quality for all of the team deliverables.\n" +
                "· Write high quality distributed system software.\n" +
                "· Work in an agile, startup-like development environment, where you are always working on the most important stuff.",
        "https://merivis.org/wp-content/uploads/2018/02/Amazon-Logo-Transparent-PNG.png",
    ), Job("1437137", "https://www.amazon.jobs/en/jobs/1437137/software-development-engineer-ii",
        "Software Development Engineer II", "Amazon", "Software Development",
        "Full Time", "", "Chennai, India",
        "We are looking for passionate, hard-working, and talented software engineers who have experience building innovative, mission critical, high volume applications that customers love. This is a high visibility team where you will get a chance to make a positive impact on customer experience.\n" +
                "\n" +
                "Amazon FireTV products are revolutionary consumer devices that have become the #1 best-selling Amazon products. They are among the most innovative and fastest growing businesses at both Amazon and the entire consumer electronics industry. We are looking for talented software engineers to join our team in building application software. This is an enormous opportunity to work on products used every day by people you know.\n" +
                "\n" +
                "As a software development engineer on this team, you will play a pivotal role in shaping the definition, vision, design, roadmap and development of product features from beginning to end. You will:\n" +
                "· Work with the team to help solve business problems.\n" +
                "· Design, implement, test, deploy and maintain innovative software solutions to transform service performance, durability, cost, and security.\n" +
                "· Use software engineering best practices to ensure a high standard of quality for all of the team deliverables.\n" +
                "· Write high quality distributed system software.\n" +
                "· Work in an agile, startup-like development environment, where you are always working on the most important stuff.",
        "https://merivis.org/wp-content/uploads/2018/02/Amazon-Logo-Transparent-PNG.png",
    ), Job("1437137", "https://www.amazon.jobs/en/jobs/1437137/software-development-engineer-ii",
        "Software Development Engineer II", "Amazon", "Software Development",
        "Full Time", "", "Chennai, India",
        "We are looking for passionate, hard-working, and talented software engineers who have experience building innovative, mission critical, high volume applications that customers love. This is a high visibility team where you will get a chance to make a positive impact on customer experience.\n" +
                "\n" +
                "Amazon FireTV products are revolutionary consumer devices that have become the #1 best-selling Amazon products. They are among the most innovative and fastest growing businesses at both Amazon and the entire consumer electronics industry. We are looking for talented software engineers to join our team in building application software. This is an enormous opportunity to work on products used every day by people you know.\n" +
                "\n" +
                "As a software development engineer on this team, you will play a pivotal role in shaping the definition, vision, design, roadmap and development of product features from beginning to end. You will:\n" +
                "· Work with the team to help solve business problems.\n" +
                "· Design, implement, test, deploy and maintain innovative software solutions to transform service performance, durability, cost, and security.\n" +
                "· Use software engineering best practices to ensure a high standard of quality for all of the team deliverables.\n" +
                "· Write high quality distributed system software.\n" +
                "· Work in an agile, startup-like development environment, where you are always working on the most important stuff.",
        "https://merivis.org/wp-content/uploads/2018/02/Amazon-Logo-Transparent-PNG.png",
    ), Job("1437137", "https://www.amazon.jobs/en/jobs/1437137/software-development-engineer-ii",
        "Software Development Engineer II", "Amazon", "Software Development",
        "Full Time", "", "Chennai, India",
        "We are looking for passionate, hard-working, and talented software engineers who have experience building innovative, mission critical, high volume applications that customers love. This is a high visibility team where you will get a chance to make a positive impact on customer experience.\n" +
                "\n" +
                "Amazon FireTV products are revolutionary consumer devices that have become the #1 best-selling Amazon products. They are among the most innovative and fastest growing businesses at both Amazon and the entire consumer electronics industry. We are looking for talented software engineers to join our team in building application software. This is an enormous opportunity to work on products used every day by people you know.\n" +
                "\n" +
                "As a software development engineer on this team, you will play a pivotal role in shaping the definition, vision, design, roadmap and development of product features from beginning to end. You will:\n" +
                "· Work with the team to help solve business problems.\n" +
                "· Design, implement, test, deploy and maintain innovative software solutions to transform service performance, durability, cost, and security.\n" +
                "· Use software engineering best practices to ensure a high standard of quality for all of the team deliverables.\n" +
                "· Write high quality distributed system software.\n" +
                "· Work in an agile, startup-like development environment, where you are always working on the most important stuff.",
        "https://merivis.org/wp-content/uploads/2018/02/Amazon-Logo-Transparent-PNG.png",
    ))


    inner class JobViewholder(itemview: View): RecyclerView.ViewHolder(itemview) {
        var company_logo = itemview.findViewById<ImageView>(R.id.company_image)
        var job_type = itemView.findViewById<TextView>(R.id.jobtype)
        var jobtitle = itemview.findViewById<TextView>(R.id.jobname)
        var comapny_name = itemview.findViewById<TextView>(R.id.companyname)
        var jobLocation = itemview.findViewById<TextView>(R.id.jobLoc)
        var job_box = itemview.findViewById<CardView>(R.id.job_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewholder {
        var viewHolder = JobViewholder(LayoutInflater.from(parent.context).inflate(R.layout.job_item, parent, false))

        viewHolder.job_box.setOnClickListener{
            listner.onJobCLick(items[viewHolder.adapterPosition])
        }

        return viewHolder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateJob(updatedJobs: ArrayList<Job>) {
        items.clear()
        items.addAll(updatedJobs)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: JobViewholder, position: Int) {

        holder.comapny_name.text = items[position].company_name
        holder.job_type.text = items[position].job_type
        holder.jobtitle.text = items[position].title
        holder.jobLocation.text = items[position].candidate_required_location
        Glide.with(context).load(items[position].company_logo_url).circleCrop().into(holder.company_logo)
    }


}

interface JobItemClicked{
    fun onJobCLick(job: Job)
}