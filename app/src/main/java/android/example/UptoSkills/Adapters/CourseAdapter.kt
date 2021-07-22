package android.example.UptoSkills.Adapters

import android.content.Context;
import android.example.UptoSkills.R
import android.example.UptoSkills.models.Course
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

class CourseAdapter(val context: Context,val listner: CourseItemClicked): RecyclerView.Adapter<CourseAdapter.CourseViewholder>(){

    private var items: ArrayList<Course> = arrayListOf(Course("Advanced Process Equipment Design in Oil & Gas Industry",
        "The Certification course in Advance Process Equipment Design Course is the Benchmark of competency in the Process engineering Industry and is recognized as the leading credential in corporate treasury worldwide. Whether participants are looking to safeguard their careers or seeking a professional career, promotion, earning this program can open a variety of doors.\n" +
                "\n" +
                "Professional Competency :\n" +
                "Earn up to 20 % more than your non- certified peers\n" +
                "\n" +
                "Improve Marketability :\n" +
                "Stand out against other applicants in a tough job market\n" +
                "\n" +
                "Boost Relevancy :\n" +
                "Stay current in the profession with continuing education requirement\n" +
                "\n" +
                "Increasing Job Security :\n" +
                "Validates your competency in liquidity, capital and risk management functions\n" +
                "\n" +
                "Better career Flexibility :\n" +
                "Prepares you for greater on-the-job responsibilities",
        "10 days", "https://www.uptoskills.com/panel/admin-panel/types_of_courses/855715890blog-05-2-1170x728.jpg",
        "Prabhat Kr Jha", "design", "English", 24, true,
        "https://www.uptoskills.com/course-details.php?course_id=22", "", "499",
    ), Course("Advanced Process Equipment Design in Oil & Gas Industry",
        "The Certification course in Advance Process Equipment Design Course is the Benchmark of competency in the Process engineering Industry and is recognized as the leading credential in corporate treasury worldwide. Whether participants are looking to safeguard their careers or seeking a professional career, promotion, earning this program can open a variety of doors.\n" +
                "\n" +
                "Professional Competency :\n" +
                "Earn up to 20 % more than your non- certified peers\n" +
                "\n" +
                "Improve Marketability :\n" +
                "Stand out against other applicants in a tough job market\n" +
                "\n" +
                "Boost Relevancy :\n" +
                "Stay current in the profession with continuing education requirement\n" +
                "\n" +
                "Increasing Job Security :\n" +
                "Validates your competency in liquidity, capital and risk management functions\n" +
                "\n" +
                "Better career Flexibility :\n" +
                "Prepares you for greater on-the-job responsibilities",
        "10 days", "https://www.uptoskills.com/panel/admin-panel/types_of_courses/855715890blog-05-2-1170x728.jpg",
        "Prabhat Kr Jha", "design", "English", 24, true,
        "https://www.uptoskills.com/course-details.php?course_id=22", "", "499",
    ), Course("Advanced Process Equipment Design in Oil & Gas Industry",
        "The Certification course in Advance Process Equipment Design Course is the Benchmark of competency in the Process engineering Industry and is recognized as the leading credential in corporate treasury worldwide. Whether participants are looking to safeguard their careers or seeking a professional career, promotion, earning this program can open a variety of doors.\n" +
                "\n" +
                "Professional Competency :\n" +
                "Earn up to 20 % more than your non- certified peers\n" +
                "\n" +
                "Improve Marketability :\n" +
                "Stand out against other applicants in a tough job market\n" +
                "\n" +
                "Boost Relevancy :\n" +
                "Stay current in the profession with continuing education requirement\n" +
                "\n" +
                "Increasing Job Security :\n" +
                "Validates your competency in liquidity, capital and risk management functions\n" +
                "\n" +
                "Better career Flexibility :\n" +
                "Prepares you for greater on-the-job responsibilities",
        "10 days", "https://www.uptoskills.com/panel/admin-panel/types_of_courses/855715890blog-05-2-1170x728.jpg",
        "Prabhat Kr Jha", "design", "English", 24, true,
        "https://www.uptoskills.com/course-details.php?course_id=22", "", "499",
    ))


    inner class CourseViewholder(itemview: View): RecyclerView.ViewHolder(itemview) {
        var courseImage: ImageView = itemview.findViewById(R.id.courseImage)
        var courseCategory: TextView = itemview.findViewById(R.id.CourseCategory)
        var courseName: TextView = itemview.findViewById(R.id.CourseName)
        var courseDescription: TextView = itemview.findViewById(R.id.CourseDescription)
        var course: CardView = itemview.findViewById(R.id.course)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewholder {
        var courseViewHolder = CourseViewholder(LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false))
        courseViewHolder.course.setOnClickListener{
            listner.onCourseCLick(items[courseViewHolder.adapterPosition])
        }
        return courseViewHolder
    }

    override fun onBindViewHolder(holder: CourseViewholder, position: Int) {
        holder.courseName.text = items[position].course_name
        holder.courseCategory.text = items[position].category
        holder.courseDescription.text = items[position].course_description
        Glide.with(holder.courseImage.context).load(items[position].course_image).centerCrop().into(holder.courseImage)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateCourse(updatedCourse: ArrayList<Course>) {
        items.clear()
        items.addAll(updatedCourse)
        notifyDataSetChanged()
    }
}

interface CourseItemClicked{
    fun onCourseCLick(course: Course)
}