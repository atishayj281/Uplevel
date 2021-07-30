package android.example.uptoskills

import android.annotation.SuppressLint
import android.example.uptoskills.databinding.ActivityPdfviewerBinding
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent

class PDFViewerActivity : AppCompatActivity() {

    private lateinit var pdfUrl: String
    private lateinit var binding: ActivityPdfviewerBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfviewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pdfUrl = intent.getStringExtra("pdfUrl").toString()
//        binding.progressBar.visibility = View.VISIBLE
//        binding.webView.settings.javaScriptEnabled = true
//        binding.webView.loadUrl(pdfUrl)
//        binding.progressBar.visibility = View.GONE

        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(pdfUrl))
    }
}