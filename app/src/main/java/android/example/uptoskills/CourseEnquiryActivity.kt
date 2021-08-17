package android.example.uptoskills

import android.content.Intent
import android.content.pm.PackageManager
import android.example.uptoskills.databinding.ActivityCourseEnquiryBinding
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class CourseEnquiryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCourseEnquiryBinding
    private val CALL: Int = 1
    private var firstCall: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseEnquiryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        binding.call1.setOnClickListener{
            // checking the permission
            firstCall = true
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                call(binding.contact1.text as String)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE), CALL)
            }
        }
        binding.call2.setOnClickListener{
            // checking the permission
            firstCall = false
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                call(binding.contact2.text as String)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE), CALL)
            }
        }

        binding.whatsapp.setOnClickListener {
            openWhatsappContact(binding.wContact.text.toString())
        }


    }

    private fun openWhatsappContact(number: String) {
        val uri = Uri.parse("smsto:$number")
        val i = Intent(Intent.ACTION_SENDTO, uri)
        i.setPackage("com.whatsapp")
        startActivity(Intent.createChooser(i, ""))
    }

    private fun call(no: String) {
        val uri = "tel:" + no
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse(uri)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if(requestCode == CALL && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(firstCall) {
                call(binding.contact1.text.toString())
            } else {
                call(binding.contact2.text.toString())
            }
        }
    }
}