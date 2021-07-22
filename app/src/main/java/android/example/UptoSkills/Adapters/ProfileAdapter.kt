package android.example.UptoSkills.Adapters

import android.example.UptoSkills.R
import android.example.UptoSkills.models.Users
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ProfileAdapter(options: FirestoreRecyclerOptions<Users>, val listener: IProfileAdapter) : FirestoreRecyclerAdapter<Users, ProfileAdapter.ProfileViewHolder>(options) {

    class ProfileViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.userImage)
        var name: TextView = itemView.findViewById(R.id.userName)
        var currJob: TextView = itemView.findViewById(R.id.jobtitle)
        var education: TextView = itemView.findViewById(R.id.education)
        var profile: ConstraintLayout = itemView.findViewById(R.id.profile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val viewHolder = ProfileViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.profile, parent, false))
        viewHolder.profile.setOnClickListener{
            listener.onProfileClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int, model: Users) {
        holder.name.text = model.displayName
        holder.currJob.text = model.job
        holder.education.text = model.education
    }
}


interface IProfileAdapter {
    fun onProfileClicked(uid: String)
}