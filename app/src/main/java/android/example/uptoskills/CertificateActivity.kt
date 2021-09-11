package android.example.uptoskills

import android.Manifest
import android.content.pm.PackageManager
import android.example.uptoskills.Adapters.CertificateAdapter
import android.example.uptoskills.Adapters.onCertificateClicked
import android.example.uptoskills.daos.PaidCourseDao
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.databinding.ActivityCertificateBinding
import android.example.uptoskills.models.PaidCourse
import android.example.uptoskills.models.Users
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.time.temporal.ValueRange

class CertificateActivity : AppCompatActivity(), onCertificateClicked {

    private lateinit var binding: ActivityCertificateBinding
    private var user = Users()
    private lateinit var usersDao: UsersDao
    private lateinit var auth: FirebaseAuth
    private val completedCourseId = ArrayList<String>()
    private val completedCourse = ArrayList<PaidCourse>()
    private lateinit var paidCourseDao: PaidCourseDao
    private lateinit var pdfDocument: PdfDocument
    private lateinit var canvas: Canvas
    private lateinit var paint: Paint
    private lateinit var file: File
    private lateinit var adapter: CertificateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertificateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = CertificateAdapter(this, this)
        binding.certificateRecyclerView.adapter = adapter
        binding.certificateRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.back.setOnClickListener {
            finish()
        }

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PackageManager.PERMISSION_GRANTED)

        paidCourseDao = PaidCourseDao()
        auth = FirebaseAuth.getInstance()
        usersDao = UsersDao()

        GlobalScope.launch(Dispatchers.IO) {
            val temp = auth.currentUser?.let { usersDao.getUserById(it.uid).await().toObject(Users::class.java) }
            if(temp != null) {
                user = temp;
                user.paidcourses?.forEach { (s, s2) ->
                    if(s2.lowercase() == "yes") {
                        completedCourseId.add(s)
                    }
                    Log.e("crtificate", s2)
                }
                if(completedCourseId.isNotEmpty()) {
                    GlobalScope.launch {
                        completedCourseId
                            .forEach {
                                val course = paidCourseDao.getCoursebyId(it).await().toObject(PaidCourse::class.java)
                                if(course != null) {
                                    completedCourse.add(course)
                                }
                            }
                        withContext(Dispatchers.Main) {
                            adapter.updateCertificate(completedCourse)
                            if(completedCourse.size == 0) {
                                Toast.makeText(this@CertificateActivity, "No Certificate Found", Toast.LENGTH_SHORT).show()
                                binding.noCertificate.visibility = View.VISIBLE
                            }
                            else {
                                binding.noCertificate.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }


    private fun createCertificate(user: Users, course: PaidCourse) {
        binding.progressBar.visibility = View.VISIBLE
        pdfDocument = PdfDocument()
        paint = Paint()

        val pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(900, 695, 1).create()
        val page1 = pdfDocument.startPage(pageInfo)
        canvas = page1.canvas
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 35f
        paint.color = ContextCompat.getColor(this, R.color.darkgoldenRod)
        canvas.drawText("CERTIFICATE", (pageInfo.pageWidth/2).toFloat(), 40f, paint)
        paint.textSize = 28f
        canvas.drawText("OF COMPLETION", (pageInfo.pageWidth/2).toFloat(), 80f, paint)
        paint.textSize = 20f
        paint.color = ContextCompat.getColor(this, R.color.black)
        canvas.drawText("This Certificate is Proudly Presented to:",
            (pageInfo.pageWidth/2).toFloat(), 112f, paint)
        paint.textSize = 35f
        paint.color = ContextCompat.getColor(this, R.color.darkgoldenRod)
        canvas.drawText(user.full_name.uppercase(), (pageInfo.pageWidth/2).toFloat(), 150f, paint)

        paint.color = ContextCompat.getColor(this, R.color.black)
        canvas.drawLine(45f, 185f, 855f, 185f, paint)



        pdfDocument.finishPage(page1)
        file = File(this.externalCacheDir!!.absolutePath, "/${course.id}.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(this, file.absolutePath, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.message?.let { Log.e("Certificate Error", it) }
        }
        binding.progressBar.visibility = View.GONE
        pdfDocument.close()
    }

    override fun onClicked(course: PaidCourse) {
        createCertificate(user, course)
    }
}