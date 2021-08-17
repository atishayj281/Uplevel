package android.example.uptoskills

import android.example.uptoskills.databinding.ActivityCertificateBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class CertificateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCertificateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertificateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }
    }
}