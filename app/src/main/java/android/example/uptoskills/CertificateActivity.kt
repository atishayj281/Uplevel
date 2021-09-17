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




class CertificateActivity : AppCompatActivity(), onCertificateClicked {

    private lateinit var binding: ActivityCertificateBinding
    private lateinit var user: Users
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
    private lateinit var bitmap: Bitmap
    private lateinit var scaleBitmap: Bitmap

    @RequiresApi(Build.VERSION_CODES.M)
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
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.certificate)
        scaleBitmap = Bitmap.createScaledBitmap(bitmap, 1280, 720, false)

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
                    if (s2.lowercase() == "yes") {
                        completedCourseId.add(s)
                    }
                    Log.e("crtificate", s2)
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
                }
            }
        }

    }



    @RequiresApi(Build.VERSION_CODES.M)
    private fun createCertificate(user: Users, course: PaidCourse) {
        binding.progressBar.visibility = View.VISIBLE
        pdfDocument = PdfDocument()
        paint = Paint()

        val pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(1280, 720, 1).create()
        val page1 = pdfDocument.startPage(pageInfo)
        canvas = page1.canvas

        canvas.drawBitmap(scaleBitmap, 0f, 0f, paint)
        paint.textSize = 60f
        paint.color = ContextCompat.getColor(this, R.color.orange)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(user.full_name, 140f, 380f, paint)
        Toast.makeText(this, user.full_name, Toast.LENGTH_SHORT).show()

        paint.color = ContextCompat.getColor(this, R.color.green)

        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 45f
        canvas.drawText(course.course_name,
            1280f - (pageInfo.pageWidth / 3) - 2 * course.course_name.length,
            590f,
            paint)

        val college: String = user.college_name
        val a = college.length
        when {
            a < 10 -> {
                paint.textSize = 60f
                paint.color = ContextCompat.getColor(this, R.color.orange)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(college, 140f, 460f, paint)
            }
            a < 20 -> {
                paint.textSize = 40f
                paint.color = ContextCompat.getColor(this, R.color.orange)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(college, 140f, 460f, paint)
            }
            a < 30 -> {
                paint.textSize = 40f
                paint.color = ContextCompat.getColor(this, R.color.orange)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(college, 100f, 460f, paint)
            }
            else -> {
                paint.textSize = 35f
                paint.color = ContextCompat.getColor(this, R.color.orange)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(college, 80f, 460f, paint)
            }
        }

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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClicked(course: PaidCourse) {
        createCertificate(user, course)
    }
}