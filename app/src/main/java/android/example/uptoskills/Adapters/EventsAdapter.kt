package android.example.uptoskills.Adapters

import android.example.uptoskills.R
import android.example.uptoskills.models.Blog
import android.example.uptoskills.models.Events
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.android.material.card.MaterialCardView

class EventsAdapter(options: FirestorePagingOptions<Events>, private val listener: IEventClickListener, val itemId: Int) : FirestorePagingAdapter<Events, EventsAdapter.EventViewHolder>(
    options
) {

    inner class EventViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val event: MaterialCardView = itemView.findViewById(R.id.event)
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val image: ImageView = itemView.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val viewHolder =  EventViewHolder(LayoutInflater.from(parent.context).inflate(itemId, parent, false))
        viewHolder.event.setOnClickListener {
            getItem(viewHolder.adapterPosition)?.toObject(Events::class.java)?.let { listener.onClick(it) }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int, model: Events) {
        holder.title.text = model.title
        holder.description.text = model.description
        Glide.with(holder.image.context).load(model.image)
            .centerCrop()
            .into(holder.image)
    }
}

interface IEventClickListener{
    fun onClick(event: Events)
}
