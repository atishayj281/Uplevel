package android.example.uptoskills.Adapters

import android.content.Context
import android.example.uptoskills.R
import android.example.uptoskills.interviewQuestions.Questions
import android.example.uptoskills.models.Job
import android.gesture.GestureLibraries
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView

class InterviewQuesAdapter(val context: Context, val listener: IQueslistener): RecyclerView.Adapter<InterviewQuesAdapter.QuesViewHolder>() {

    private val ques = Questions()
    private var images = ques.images
    private val question = arrayListOf(ques.python, ques.java, ques.cpp)


    inner class QuesViewHolder(itemview: View): RecyclerView.ViewHolder(itemview) {
        val topic_image: ImageView = itemview.findViewById(R.id.topic_image)
        val ques: MaterialCardView = itemview.findViewById(R.id.question)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuesViewHolder {
        val viewholder = QuesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ques_item, parent, false))
        viewholder.ques.setOnClickListener {
            listener.onClick(question[viewholder.adapterPosition])
        }
        return viewholder
    }

    override fun onBindViewHolder(holder: QuesViewHolder, position: Int) {
        Glide.with(holder.topic_image.context).load(images[position].trim())
            .placeholder(R.drawable.ic__uptoskillslogo)

            .into(holder.topic_image)
        Log.e("image", images[position])
    }

    override fun getItemCount(): Int {
        Log.e("imageSize", images.size.toString())
        return images.size
    }

    fun updateQues() {
        //notifyDataSetChanged()
    }

}

interface IQueslistener{
    fun onClick(ques: ArrayList<String>)
}