package android.example.UptoSkills

import android.app.DownloadManager
import android.content.Intent
import android.example.UptoSkills.daos.UsersDao
import android.example.UptoSkills.databinding.ActivityUserDetailsBinding
import android.example.UptoSkills.models.Users
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetailsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var user: Users
    private lateinit var userDao: UsersDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(Build.VERSION.SDK_INT >= 21) {
            var window: Window = this.window
            window.statusBarColor = ContextCompat.getColor(this, R.color.LightningYellow)
        }

        auth = FirebaseAuth.getInstance()
        var id = intent.getStringExtra("id")
        userDao = UsersDao()

        GlobalScope.launch(Dispatchers.IO) {
            var full_name: String=""
            var mobile: String=""
            var college: String=""
            var job: String=""
            var education: String=""
            var displayName: String=""

            var checker: Query = userDao.ref.orderByChild("id").equalTo(id.toString())
            checker.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        var ref = snapshot.child(id.toString())
                        full_name = ref.child("full_name").value as String
                        mobile = ref.child("mobileNo").value as String
                        college = ref.child("college_name").value as String
                        job = ref.child("job").value as String
                        education = ref.child("education").value as String
                        displayName = ref.child("displayName").value as String
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

            withContext(Dispatchers.Main) {
                binding.fullName.setText(full_name)
                binding.mobile.setText(mobile)
                binding.college.setText(college)
                binding.currentJob.setText(job)
                binding.education.setText(education)
                binding.username.text = displayName
            }
        }

        binding.submit.setOnClickListener {

            var usr = Users(binding.fullName.text.toString(), intent.getStringExtra("username"),
            intent.getStringExtra("email").toString(), binding.college.text.toString(), binding.education.text.toString(),
                binding.currentJob.text.toString(), "", binding.mobile.text.toString(), id.toString())
            userDao.updateUser(usr, id.toString())
            Toast.makeText(this, id, Toast.LENGTH_SHORT).show()
            startMainActivity()

        }
    }

    fun startMainActivity(){
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}