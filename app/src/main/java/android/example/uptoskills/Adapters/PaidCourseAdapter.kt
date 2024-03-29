package android.example.uptoskills.Adapters

import android.content.Context
import android.example.uptoskills.R
import android.example.uptoskills.models.FreeCourse
import android.example.uptoskills.models.PaidCourse
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.card.MaterialCardView

class PaidCourseAdapter(val context: Context, val listner: paidCourseclicked, val itemId: Int,
                        options: FirestoreRecyclerOptions<PaidCourse>
): FirestoreRecyclerAdapter<PaidCourse, PaidCourseAdapter.CourseViewholder>(options) {
    inner class CourseViewholder(itemview: View): RecyclerView.ViewHolder(itemview) {
        var courseImage: ImageView = itemview.findViewById(R.id.courseImage)
        var courseName: TextView = itemview.findViewById(R.id.CourseName)
        var course: MaterialCardView = itemview.findViewById(R.id.course)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewholder {
        val viewholder = CourseViewholder(LayoutInflater.from(parent.context).inflate(itemId, parent, false))

        viewholder.course.setOnClickListener {
            listner.onpaidCourseClicked(snapshots.getSnapshot(viewholder.adapterPosition).id)
        }
        return viewholder
    }

    override fun onBindViewHolder(holder: CourseViewholder, position: Int, model: PaidCourse) {
        holder.courseName.text = model.course_name
        Glide.with(holder.courseImage.context).load(model.course_image).centerInside().into(holder.courseImage)

    }
}

interface paidCourseclicked{
    fun onpaidCourseClicked(courseId: String)
}

