package android.example.uptoskills.Adapters

import android.example.uptoskills.R
import android.content.Context


import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import android.example.uptoskills.models.MessageModal

import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup

class MessageRVAdapter(private val messageModalArrayList: ArrayList<MessageModal>, context: Context):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val userTV: TextView = itemView.findViewById(R.id.idTVUser)
    }
    inner class BotViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val botTV: TextView = itemView.findViewById(R.id.idTVBot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        when (viewType) {
            0 -> {
                // below line we are inflating user message layout.
                view = LayoutInflater.from(parent.context).inflate(R.layout.user_msg, parent, false)
                return UserViewHolder(view)
            }
            1 -> {
                // below line we are inflating bot message layout.
                view = LayoutInflater.from(parent.context).inflate(R.layout.bot_msg, parent, false)
                return BotViewHolder(view)
            }
            else -> {
                val viewholder = UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false))
                return viewholder
            }
        }
    }

    override fun getItemCount(): Int {
        return messageModalArrayList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val (message, sender) = messageModalArrayList[position]
        when (sender) {
            "user" ->                 // below line is to set the text to our text view of user layout
                (holder as UserViewHolder).userTV.text = message
            "bot" ->                 // below line is to set the text to our text view of bot layout
                (holder as BotViewHolder).botTV.text = message
        }
    }

    override fun getItemViewType(position: Int): Int {
        // below line of code is to set position.
        return when (messageModalArrayList[position].sender) {
            "user" -> 0
            "bot" -> 1
            else -> -1
        }
    }
}
