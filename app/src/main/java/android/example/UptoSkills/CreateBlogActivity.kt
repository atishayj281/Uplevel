package android.example.UptoSkills

import android.content.Intent
import android.example.UptoSkills.Fragment.BlogFragment
import android.example.UptoSkills.daos.BlogDao
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class CreateBlogActivity : AppCompatActivity() {

    private lateinit var postDao: BlogDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_blog)

        var postButton = findViewById<Button>(R.id.postButton)
        var postInput = findViewById<EditText>(R.id.postInput)
        var postheading = findViewById<EditText>(R.id.postHeading)
        postDao = BlogDao()

        postButton.setOnClickListener {
            val input = postInput.text.toString().trim()
            val heading = postheading.text.toString().trim()
            if(input.isNotEmpty() && heading.isNotEmpty()) {
                postDao.addPost(input, heading)
                Toast.makeText(this, "Post Uploaded Successfully...", Toast.LENGTH_SHORT).show()
                postInput.text.clear()
                postheading.text.clear()

            }
        }

    }
}