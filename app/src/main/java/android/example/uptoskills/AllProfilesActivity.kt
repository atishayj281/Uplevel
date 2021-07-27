package android.example.uptoskills

import android.example.uptoskills.Adapters.IProfileAdapter
import android.example.uptoskills.Adapters.ProfileAdapter
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.models.Users
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class AllProfilesActivity : AppCompatActivity(), IProfileAdapter {


    private lateinit var progressBar: ProgressBar
    private lateinit var userDao: UsersDao
    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var profileRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_profiles)
        progressBar = findViewById(R.id.ProfileProgressBar)
        userDao = UsersDao()
        var profiles: ArrayList<Users> = ArrayList()
        profileRecyclerView = findViewById(R.id.Profilerecyclerview)
        profileAdapter = ProfileAdapter(this, this)
        profileRecyclerView.layoutManager = LinearLayoutManager(this)
        profileRecyclerView.adapter = profileAdapter

        userDao.ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                children.forEach {
                    var user = it.getValue(Users::class.java)
                    if (user != null) {
                        profiles.add(user)
                    }
                }
                profileAdapter.updateProfiles(profiles)
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
            }

        })
    }

    override fun onProfileClicked(uid: String) {

    }
}