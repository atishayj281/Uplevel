package android.example.uptoskills.Adapters

import android.content.Context
import android.example.uptoskills.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CategoriesRVAdapter(val context: Context, val categoryList: ArrayList<String>,
                          val background: ArrayList<Int>, val listener: CategoriesRVListener)
    : RecyclerView.Adapter<CategoriesRVAdapter.CategoriesViewHolder>() {

    inner class CategoriesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val layout: FrameLayout = itemView.findViewById(R.id.bg)
        val text: TextView = itemView.findViewById(R.id.category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val viewHolder = CategoriesViewHolder(LayoutInflater.from(context).inflate(R.layout.categories_banner, parent, false))
        viewHolder.layout.setOnClickListener {
            listener.onCategorySelect(categoryList[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        holder.text.text = categoryList[position]
        holder.layout.background = ContextCompat.getDrawable(context, background[position])
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

}

interface CategoriesRVListener {
    fun onCategorySelect(category: String)
}