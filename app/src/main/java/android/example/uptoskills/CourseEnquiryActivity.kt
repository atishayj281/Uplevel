package android.example.uptoskills

import android.content.Intent
import android.content.pm.PackageManager
import android.example.uptoskills.databinding.ActivityCourseEnquiryBinding
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import java.lang.Exception

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

        binding.submit.setOnClickListener {
            val emailsend: String = "info@uptoskills.com"
            val emailsubject: String = "App Query"
            val emailbody: String = binding.query.text.toString() + "\nContact No: " + binding.contactNo.text.toString() + "\nMail Id: " + FirebaseAuth.getInstance().currentUser?.email

            // define Intent object
            // with action attribute as ACTION_SEND

            // define Intent object
            // with action attribute as ACTION_SEND
            val intent = Intent(Intent.ACTION_SEND)

            // add three fiels to intent using putExtra function

            // add three fiels to intent using putExtra function
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailsend))
            intent.putExtra(Intent.EXTRA_SUBJECT, emailsubject)
            intent.putExtra(Intent.EXTRA_TEXT, emailbody)

            // set type of intent

            // set type of intent
            intent.type = "message/rfc822"

            // startActivity with intent with chooser
            // as Email client using createChooser function

            // startActivity with intent with chooser
            // as Email client using createChooser function
            startActivity(
                Intent
                    .createChooser(intent,
                        "Choose an Email client :"))
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