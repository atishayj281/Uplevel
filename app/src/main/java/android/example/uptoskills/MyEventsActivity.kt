package android.example.uptoskills

import android.content.Intent
import android.example.uptoskills.Adapters.IEventClickListener
import android.example.uptoskills.Adapters.MyEventsAdapter
import android.example.uptoskills.daos.EventDao
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.models.Events
import android.example.uptoskills.models.Users
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.sun.mail.imap.protocol.UID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MyEventsActivity : AppCompatActivity(), IEventClickListener {

    private var user: Users = Users()
    private val userDao = UsersDao()
    private val eventDao: EventDao = EventDao()
    private lateinit var auth: FirebaseAuth
    private lateinit var UID: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var myEventsAdapter: MyEventsAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var noEventView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_events)

        auth = FirebaseAuth.getInstance()
        UID = auth.currentUser?.uid.toString()
        recyclerView = findViewById(R.id.recycler_view)
        progressBar = findViewById(R.id.progress_bar)
        noEventView = findViewById(R.id.noEventView)
        getUser()

    }

    private fun getUser() {
        GlobalScope.launch(Dispatchers.IO) {
            val temp = auth.currentUser?.let { userDao.getUserById(it.uid).await().toObject(Users::class.java) }
            if(temp != null) {
                user = temp;
                setUpEvents()
            }
        }
    }

    private fun setUpEvents() {
        val events: ArrayList<Events> = ArrayList()
        GlobalScope.launch(Dispatchers.IO) {
            user.events.forEach {
                val tEvent = eventDao.getBlogById(it).await().toObject(Events::class.java)
                if(tEvent != null) {
                    events.add(tEvent)
                }
            }
            withContext(Dispatchers.Main) {
                myEventsAdapter = MyEventsAdapter(this@MyEventsActivity, this@MyEventsActivity, events)
                recyclerView.adapter = myEventsAdapter
                recyclerView.layoutManager = LinearLayoutManager(this@MyEventsActivity)
                progressBar.visibility = View.GONE
                if(events.isEmpty()) {
                    noEventView.visibility = View.VISIBLE
                } else {
                    noEventView.visibility = View.GONE
                }
            }
        }
    }

    override fun onClick(event: Events) {
        val intent = Intent(this, EventViewActivity::class.java)
        intent.putExtra("eventId", event.id)
        startActivity(intent)
    }
}