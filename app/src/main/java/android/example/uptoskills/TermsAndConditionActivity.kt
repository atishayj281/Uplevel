package android.example.uptoskills

import android.example.uptoskills.databinding.ActivityTermsAndConditionBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class TermsAndConditionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTermsAndConditionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsAndConditionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.termsOfUse.setText(R.string.terms_of_use)
        binding.ContentOfferings.setText(R.string.content_offering)
        binding.desclaimers.setText(R.string.disclaimers)
        binding.otherPaidServices.setText(R.string.other_paid_services)
        binding.ourLicence.setText(R.string.licence)
        binding.paidCourses.setText(R.string.paid_services)
        binding.thirdPatyMarketPlaces.setText(R.string.third_party_marketPlaces)
        binding.usingUptoSkills.setText(R.string.using_uptoskills)
        binding.whoMayuse.setText(R.string.who_may_use)

        binding.back.setOnClickListener {
            finish()
        }

    }
}