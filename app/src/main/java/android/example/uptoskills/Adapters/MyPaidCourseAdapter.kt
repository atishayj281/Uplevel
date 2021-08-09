package android.example.uptoskills.Adapters

import android.content.Context
import android.example.uptoskills.R
import android.example.uptoskills.models.PaidCourse
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView

class MyPaidCourseAdapter(val context: Context, val listener: CourseItemClicked): RecyclerView.Adapter<MyPaidCourseAdapter.CourseViewholder>() {

    private var courses: ArrayList<PaidCourse> = ArrayList()

    inner class CourseViewholder(itemview: View): RecyclerView.ViewHolder(itemview) {
        var courseImage: ImageView = itemview.findViewById(R.id.courseImage)
        var courseCategory: TextView = itemview.findViewById(R.id.CourseCategory)
        var courseName: TextView = itemview.findViewById(R.id.CourseName)
        var courseDescription: TextView = itemview.findViewById(R.id.CourseDescription)
        var course: MaterialCardView = itemview.findViewById(R.id.course)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewholder {
        val viewholder = CourseViewholder(LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false))
        return viewholder
    }

    override fun onBindViewHolder(holder: CourseViewholder, position: Int) {
        holder.courseCategory.text = courses[position].category
        holder.courseDescription.text = courses[position].course_description
        holder.courseName.text = courses[position].course_name
        Glide.with(holder.courseImage.context).load(courses[position].course_image).centerCrop().into(holder.courseImage)
    }

    override fun getItemCount(): Int {
        return courses.size
    }

    fun updateCourses(newCourses: ArrayList<PaidCourse>) {
        courses.clear()
        courses.addAll(newCourses)
        notifyDataSetChanged()
    }
}