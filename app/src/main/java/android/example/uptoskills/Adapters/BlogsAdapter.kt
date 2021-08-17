package android.example.uptoskills.Adapters

import android.example.uptoskills.models.Blog
import android.example.uptoskills.R
import android.example.uptoskills.Utils
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.models.Users
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class BlogsAdapter(options: FirestoreRecyclerOptions<Blog>, val listener: IBlogAdapter, val itemId: Int) : FirestoreRecyclerAdapter<Blog, BlogsAdapter.BLogViewHolder>(
    options
) {

    class BLogViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val blogImage: ImageView = itemView.findViewById(R.id.blogImage)
        val blogHeading: TextView = itemView.findViewById(R.id.blogHeading)
        val description: TextView = itemView.findViewById(R.id.blogDescription)
        val blog: CardView = itemView.findViewById(R.id.blog)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BLogViewHolder {
        val viewHolder =  BLogViewHolder(LayoutInflater.from(parent.context).inflate(itemId, parent, false))
        viewHolder.blog.setOnClickListener {
            listener.onBlogClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
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
    fun onBlogClicked(postId: String)
    fun onBookmarkClicked(blogId: String, item: String)
}