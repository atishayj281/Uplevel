package android.example.uptoskills.Adapters

import android.content.Context
import android.example.uptoskills.R
import android.example.uptoskills.models.Events
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView

class MyEventsAdapter(val context: Context, val listener: IEventClickListener, val events: ArrayList<Events>): RecyclerView.Adapter<MyEventsAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val event: MaterialCardView = itemView.findViewById(R.id.event)
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val image: ImageView = itemView.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): EventViewHolder {
        val viewHolder = EventViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.event_item, parent, false))
        viewHolder.event.setOnClickListener {
            listener.onClick(events[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.title.text = event.title
        holder.description.text = event.description
        Glide.with(holder.image.context).load(event.image)
            .centerCrop()
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return events.size
    }


}