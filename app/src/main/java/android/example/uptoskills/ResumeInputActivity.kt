package android.example.uptoskills

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import com.google.android.material.button.MaterialButton

class ResumeInputActivity : AppCompatActivity() {

    private var layoutIndex = 0
    private lateinit var add: MaterialButton
    private lateinit var layout: LinearLayout
    private lateinit var dynamicProjectView: DynamicProjectView
    private lateinit var remove: MaterialButton
    private val projects: HashMap<String, String> = hashMapOf()
    private lateinit var submit: MaterialButton
    private lateinit var name: EditText
    private lateinit var organization: EditText
    private lateinit var job: EditText
    private lateinit var skills: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resume_input)

        add = findViewById(R.id.add)
        remove = findViewById(R.id.remove)
        layout = findViewById(R.id.layout)
        submit = findViewById(R.id.submit)
        name = findViewById(R.id.name)
        organization = findViewById(R.id.Organisation)
        job = findViewById(R.id.job)
        skills = findViewById(R.id.skills)
        addView()

        // Adding project ViewGroup
        add.setOnClickListener {
            addView()
        }

        // removing project viewGroup
        remove.setOnClickListener {
            removeView()
        }

        // Submit the form
        submit.setOnClickListener {

        }
    }

    private fun removeView() {

        if(layoutIndex >= 4) {

            val titleEditText: EditText = layout.getChildAt(layoutIndex-1) as EditText

            layout.removeViewAt(--layoutIndex)
            layout.removeViewAt(--layoutIndex)
        }
    }

    private fun addView() {
        dynamicProjectView = DynamicProjectView(this)
        layout.addView(dynamicProjectView.titleEditText(application), layoutIndex++)
        layout.addView(dynamicProjectView.descriptionEditText(applicationContext), layoutIndex++)
    }
}