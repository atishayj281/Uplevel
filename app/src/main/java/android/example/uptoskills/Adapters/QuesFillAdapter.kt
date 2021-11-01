package android.example.uptoskills.Adapters

import android.content.Context
import android.example.uptoskills.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuesFillAdapter(val context: Context):
    RecyclerView.Adapter<QuesFillAdapter.QuesViewHolder>() {

    private val questions = arrayListOf<String>()

    class QuesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.ques)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuesViewHolder {
        val viewHolder = QuesViewHolder(LayoutInflater.from(context).inflate(R.layout.qfill_item, parent, false))
        viewHolder.text.setOnClickListener {
            if(viewHolder.text.maxLines < 5) {
                viewHolder.text.maxLines = 1000
            } else {
                viewHolder.text.maxLines = 3
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: QuesViewHolder, position: Int) {
        holder.text.text = questions[position]
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    fun updateQues(question: ArrayList<String>) {
        questions.clear()
        questions.addAll(question)
        notifyDataSetChanged()
    }

}