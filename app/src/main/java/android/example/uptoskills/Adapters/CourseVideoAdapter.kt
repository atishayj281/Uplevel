package android.example.uptoskills.Adapters

import android.content.Context
import android.example.uptoskills.R
import android.example.uptoskills.models.Video
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView

class CourseVideoAdapter(val context: Context, val listener: videoItemClicked): RecyclerView.Adapter<CourseVideoAdapter.CourseViewholder>() {

    private var videos: ArrayList<Video> = ArrayList()

    inner class CourseViewholder(itemview: View): RecyclerView.ViewHolder(itemview) {
        val video_image: ImageView = itemview.findViewById(R.id.video_image)
        val title: TextView = itemview.findViewById(R.id.title)
        val video: MaterialCardView = itemview.findViewById(R.id.video)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewholder {
        val viewholder = CourseViewholder(LayoutInflater.from(parent.context).inflate(R.layout.video, parent, false))
        viewholder.video.setOnClickListener {
            listener.onClicked(videos[viewholder.adapterPosition].link,videos[viewholder.adapterPosition].title)
        }
        return viewholder
    }

    override fun onBindViewHolder(holder: CourseViewholder, position: Int) {
        holder.title.text = videos[position].title
        Glide.with(holder.video_image.context).load(videos[position].image).centerInside().into(holder.video_image)
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    fun updateVideos(newVideos: ArrayList<Video>) {
        videos.clear()
        videos.addAll(newVideos)
        notifyDataSetChanged()
    }
}

interface videoItemClicked{
    fun onClicked(id: String, title: String)
}
