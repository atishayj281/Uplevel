package android.example.uptoskills.Adapters

import android.content.Context
import android.example.uptoskills.R
import android.example.uptoskills.models.PaidCourse
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CertificateAdapter(val context: Context,val listener: onCertificateClicked):
    RecyclerView.Adapter<CertificateAdapter.certificateViewHolder>() {

    private val course = ArrayList<PaidCourse>()


    inner class certificateViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val download: ImageView = itemView.findViewById(R.id.download)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): certificateViewHolder {
        val viewHolder = certificateViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.certificate_item, parent, false))
        viewHolder.download.setOnClickListener {
            listener.onClicked(course[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: certificateViewHolder, position: Int) {
        holder.title.text = course[position].course_name
    }

    override fun getItemCount(): Int {
        return course.size
    }

    fun updateCertificate(newCourse: ArrayList<PaidCourse>) {
        course.clear()
        course.addAll(newCourse)
        notifyDataSetChanged()
    }


}

interface onCertificateClicked{
    fun onClicked(course: PaidCourse)
}