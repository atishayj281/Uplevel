package android.example.uptoskills

import android.content.Intent
import android.example.uptoskills.Adapters.JobItemClicked
import android.example.uptoskills.Adapters.MyJobAdapter
import android.example.uptoskills.daos.JobDao
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.models.Job
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BookmarkActivity : AppCompatActivity(), JobItemClicked {

    private lateinit var recyclerView: RecyclerView
    private lateinit var usersDao: UsersDao
    private lateinit var auth: FirebaseAuth
    private lateinit var jobDao: JobDao
    private lateinit var adapter: MyJobAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var noBookmark: TextView
    private lateinit var backbtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

        recyclerView = findViewById(R.id.recycler_view)
        usersDao = UsersDao()
        auth = FirebaseAuth.getInstance()
        jobDao = JobDao()
        progressBar = findViewById(R.id.progress_bar)
        noBookmark = findViewById(R.id.noBookmark)
        backbtn = findViewById(R.id.back)

        progressBar.visibility = View.VISIBLE
        adapter = MyJobAdapter(this@BookmarkActivity, this@BookmarkActivity)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this@BookmarkActivity)

        setUpBokmarksView()

        backbtn.setOnClickListener {
            finish()
        }
    }

    private fun setUpBokmarksView() {
        usersDao.ref.child(auth.currentUser?.uid.toString()).addValueEventListener(object : ValueEventListener{
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookmark = snapshot.child("bookmarks").children

                        GlobalScope.launch(Dispatchers.IO) {
                            val bookmarks = ArrayList<Job>()
                            bookmark.forEach {
                                val jobId: String = it.key.toString()
                                Log.e("job", jobId)
                                val job: Job = jobDao.getJobbyId(jobId).await().toObject(Job::class.java)!!
                                bookmarks.add(job)
                            }
                            withContext(Dispatchers.Main) {
                                adapter.updateJobs(bookmarks)
                                if(bookmarks.isEmpty()) {
                                    noBookmark.visibility = View.VISIBLE
                                } else {
                                    noBookmark.visibility = View.GONE
                                }
                                progressBar.visibility = View.GONE
                            }
                    }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onJobCLick(jobId: String) {
        Log.e("job", jobId)
        val intent = Intent(this, JobViewActivity::class.java)
        intent.putExtra("jobId", jobId)
        intent.putExtra("category", "job")
        startActivity(intent)
    }

    override fun onbookmarkCLick(itemId: String, itemtype: String) {

    }
}