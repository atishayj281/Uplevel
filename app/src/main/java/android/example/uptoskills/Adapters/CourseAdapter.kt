package android.example.uptoskills.Adapters

import android.content.Context;
import android.example.uptoskills.R
import android.example.uptoskills.models.FreeCourse
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.Query

class CourseAdapter(val context: Context, val listner: CourseItemClicked, val itemId: Int,
                    options: FirestoreRecyclerOptions<FreeCourse>
): FirestoreRecyclerAdapter<FreeCourse, CourseAdapter.CourseViewholder>(options) {
    inner class CourseViewholder(itemview: View): RecyclerView.ViewHolder(itemview) {
        var courseImage: ImageView = itemview.findViewById(R.id.courseImage)
        var courseCategory: TextView = itemview.findViewById(R.id.CourseCategory)
        var courseName: TextView = itemview.findViewById(R.id.CourseName)
        var courseDescription: TextView = itemview.findViewById(R.id.CourseDescription)
        var course: MaterialCardView = itemview.findViewById(R.id.course)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewholder {
        val viewholder = CourseViewholder(LayoutInflater.from(parent.context).inflate(itemId, parent, false))

        viewholder.course.setOnClickListener {
            listner.onCourseCLick(snapshots.getSnapshot(viewholder.adapterPosition).id)
        }

        return viewholder
    }

    override fun onBindViewHolder(holder: CourseViewholder, position: Int, model: FreeCourse) {
        holder.courseCategory.text = model.category
        holder.courseDescription.text = model.course_description
        holder.courseName.text = model.course_name
        Glide.with(holder.courseImage.context).load(model.course_image).centerCrop().into(holder.courseImage)
    }

    fun update(query: Query) {

        query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->

            if (firebaseFirestoreException != null) {
                return@addSnapshotListener
            }

            for (snapshot in querySnapshot!!.documents) {
                val item = snapshot?.toObject(FreeCourse::class.java)

            }
            notifyDataSetChanged()

            notifyDataSetChanged()
        }
    }
}

interface CourseItemClicked{
    fun onCourseCLick(paidCourse: String)
}