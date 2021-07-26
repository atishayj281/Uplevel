package android.example.UptoSkills.Adapters

import android.example.UptoSkills.models.Blog
import android.example.UptoSkills.R
import android.example.UptoSkills.Utils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView


class BlogsAdapter(options: FirestoreRecyclerOptions<Blog>, val listener: IBlogAdapter) : FirestoreRecyclerAdapter<Blog, BlogsAdapter.BLogViewHolder>(
    options
) {

    class BLogViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val posttitle: TextView = itemView.findViewById(R.id.postTitle)
        val postText: TextView = itemView.findViewById(R.id.post)
        val userText: MaterialTextView = itemView.findViewById(R.id.userName)
        val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val blog: MaterialCardView = itemView.findViewById(R.id.blog)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BLogViewHolder {
        val viewHolder =  BLogViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.blog_item, parent, false))
        viewHolder.blog.setOnClickListener {
            listener.onBlogClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: BLogViewHolder, position: Int, model: Blog) {
        holder.posttitle.text = model.title
        holder.postText.text = model.text
        holder.userText.text = model.createdBy.displayName
        if(model.createdBy.userImage.isNotEmpty() && model.createdBy.userImage != "null"){
            Glide.with(holder.userImage.context).load(model.createdBy.userImage).circleCrop().into(holder.userImage)
        }

        holder.createdAt.text = Utils.getTimeAgo(model.createdAt)
    }
}

interface IBlogAdapter {
    fun onBlogClicked(postId: String)
}