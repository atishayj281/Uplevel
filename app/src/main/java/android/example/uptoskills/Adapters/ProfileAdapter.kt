package android.example.uptoskills.Adapters

import android.content.Context
import android.example.uptoskills.R
import android.example.uptoskills.models.Users
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProfileAdapter(val context: Context, val listener: IProfileAdapter): RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    var profiles: ArrayList<Users> = ArrayList()

    class ProfileViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var profileImage: ImageView = itemView.findViewById(R.id.userImage)
        var name: TextView = itemView.findViewById(R.id.userName)
        var currJob: TextView = itemView.findViewById(R.id.jobtitle)
        var education: TextView = itemView.findViewById(R.id.education)
        var organisation: TextView = itemView.findViewById(R.id.organisation)
        var profile: ConstraintLayout = itemView.findViewById(R.id.profile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        var viewHolder = ProfileViewHolder(LayoutInflater.from(context).inflate(R.layout.profile, parent, false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        var user = profiles[position]
        holder.name.text = user.full_name
        holder.education.text = user.education
        holder.currJob.text = user.job
        holder.organisation.text = user.college_name

        if(user.userImage.isNotEmpty() && user.userImage != "null"){
            Glide.with(holder.profileImage.context).load(user.userImage).circleCrop().into(holder.profileImage)
        }
    }

    override fun getItemCount(): Int {
        return profiles.size
    }

    fun updateProfiles(updatedProfiles: ArrayList<Users>){
        profiles.clear()
        profiles.addAll(updatedProfiles)
        notifyDataSetChanged()
    }


}


interface IProfileAdapter {
    fun onProfileClicked(uid: String)
}