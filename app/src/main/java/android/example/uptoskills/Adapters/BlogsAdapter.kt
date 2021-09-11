package android.example.uptoskills.Adapters

import android.example.uptoskills.models.Blog
import android.example.uptoskills.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.squareup.okhttp.internal.DiskLruCache


class BlogsAdapter(options: FirestorePagingOptions<Blog>, val listener: IBlogAdapter, val itemId: Int) : FirestorePagingAdapter<Blog, BlogsAdapter.BLogViewHolder>(
    options
) {

    inner class BLogViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val blogImage: ImageView = itemView.findViewById(R.id.blogImage)
        val blogHeading: TextView = itemView.findViewById(R.id.blogHeading)
        val description: TextView = itemView.findViewById(R.id.blogDescription)
        val blog: CardView = itemView.findViewById(R.id.blog)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BLogViewHolder {
        val viewHolder =  BLogViewHolder(LayoutInflater.from(parent.context).inflate(itemId, parent, false))
        viewHolder.blog.setOnClickListener {
            getItem(viewHolder.adapterPosition)?.toObject(Blog::class.java)?.let { listener.onBlogClicked(it) }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: BLogViewHolder, position: Int, model: Blog) {
        holder.blogHeading.text = model.heading
        holder.description.text = model.description
        Glide.with(holder.blogImage.context).load(model.image).centerCrop().into(holder.blogImage)
    }
}

interface IBlogAdapter {
    fun onBlogClicked(blog: Blog)
    fun onBookmarkClicked(blogId: String, item: String)
}