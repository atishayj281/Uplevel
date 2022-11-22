package android.example.uptoskills

import android.example.uptoskills.daos.EventDao
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.databinding.ActivityEventViewBinding
import android.example.uptoskills.models.Events
import android.example.uptoskills.models.Users
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EventViewActivity : AppCompatActivity() {

    private lateinit var eventId: String
    private lateinit var event: Events
    private lateinit var eventDao: EventDao
    private lateinit var binding: ActivityEventViewBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var curUserId: String
    private lateinit var curUser: Users
    private lateinit var userDao: UsersDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        curUserId = auth.currentUser?.uid.toString()
        userDao = UsersDao()
        GlobalScope.launch(Dispatchers.IO) {
            curUser = userDao.getUserById(curUserId).await().toObject(Users::class.java)!!
        }


        binding.back.setOnClickListener {
            finish()
        }

        binding.enroll.setOnClickListener {
            if(curUserId.trim().isNotEmpty() && curUserId.trim() != "null") {
                if(!event.enrolled.contains(curUserId)) {
                    event.enrolled.add(curUserId)
                    curUser.events.add(eventId)
                    eventDao.enrollinEvent(event, eventId, this)
                    userDao.addUser(curUser, curUserId)
                } else {
                    Toast.makeText(this, "Already Enrolled...", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "Please Check Your Network Connection or Login Again...", Toast.LENGTH_SHORT).show()
            }

        }

        binding.blogViewProgressBar.visibility = View.VISIBLE
        eventDao = EventDao()
        eventId = intent.getStringExtra("eventId").toString()
        GlobalScope.launch(Dispatchers.IO) {
            event = eventDao.getBlogById(eventId.trim()).await().toObject(Events::class.java)!!
            withContext(Dispatchers.Main) {
                binding.blogHeading.text = event.title
                binding.blogView.text = event.description
                Glide.with(this@EventViewActivity).load(event.image).centerInside().into(binding.Image)
                binding.blogViewProgressBar.visibility = View.GONE
            }
        }
    }
}