package android.example.uptoskills

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.example.uptoskills.Adapters.CertificateAdapter
import android.example.uptoskills.Adapters.onCertificateClicked
import android.example.uptoskills.daos.PaidCourseDao
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.databinding.ActivityCertificateBinding
import android.example.uptoskills.models.PaidCourse
import android.example.uptoskills.models.Users
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import android.example.uptoskills.mail.JavaMailAPI
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class CertificateActivity : AppCompatActivity(), onCertificateClicked {

    private lateinit var binding: ActivityCertificateBinding
    private lateinit var user: Users
    private lateinit var usersDao: UsersDao
    private lateinit var auth: FirebaseAuth
    private val completedCourseId = ArrayList<String>()
    private val completedCourse = ArrayList<PaidCourse>()
    private lateinit var paidCourseDao: PaidCourseDao
    private lateinit var adapter: CertificateAdapter
    private lateinit var storageReference: StorageReference
    private lateinit var downloadManager: DownloadManager

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertificateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.VISIBLE

        storageReference = FirebaseStorage.getInstance().reference
        adapter = CertificateAdapter(this, this)
        binding.certificateRecyclerView.adapter = adapter
        binding.certificateRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.back.setOnClickListener {
            finish()
        }

        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PackageManager.PERMISSION_GRANTED)

        paidCourseDao = PaidCourseDao()
        auth = FirebaseAuth.getInstance()
        usersDao = UsersDao()

        GlobalScope.launch(Dispatchers.IO) {
            val temp = auth.currentUser?.let {
                usersDao.getUserById(it.uid).await().toObject(Users::class.java)
            }
            if (temp != null) {
                user = temp
                user.paidcourses?.forEach { (s, s2) ->
                    if (s2.lowercase() != "no") {
                        completedCourseId.add(s)
                    }
                }
                if (completedCourseId.isNotEmpty()) {
                    GlobalScope.launch {
                        completedCourseId
                            .forEach {
                                val course = paidCourseDao.getCoursebyId(it).await()
                                    .toObject(PaidCourse::class.java)
                                if (course != null) {
                                    completedCourse.add(course)
                                }
                            }
                        withContext(Dispatchers.Main) {
                            adapter.updateCertificate(completedCourse)
                            binding.progressBar.visibility = View.GONE
                            if (completedCourse.size == 0) {
                                Toast.makeText(this@CertificateActivity,
                                    "No Certificate Found",
                                    Toast.LENGTH_SHORT).show()
                                binding.noCertificate.visibility = View.VISIBLE
                            } else {
                                binding.noCertificate.visibility = View.GONE
                            }
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClicked(course: PaidCourse) {
        binding.progressBar.visibility = View.VISIBLE
        val url: String? = user.paidcourses?.get(course.id.trim())
        if(url != null) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
        binding.progressBar.visibility = View.GONE
    }
    override fun onBackPressed() {
        if(parent == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        super.onBackPressed()
    }
}