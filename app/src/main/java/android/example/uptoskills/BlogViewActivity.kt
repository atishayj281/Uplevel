package android.example.uptoskills

import android.content.Intent
import android.example.uptoskills.daos.BlogDao
import android.example.uptoskills.databinding.ActivityBlogViewBinding
import android.example.uptoskills.models.Blog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BlogViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBlogViewBinding
    private lateinit var blog: Blog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlogViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var postId: String? = intent.getStringExtra("123")
        binding.blogViewProgressBar.visibility = View.VISIBLE
        updateBlog(postId)

        binding.back.setOnClickListener {
            finish()
        }
    }

    // Method to show blog in the activity
    private fun updateBlog(BlogId: String?) {
        var blogDao = BlogDao()
        GlobalScope.launch(Dispatchers.IO) {
            blog = blogDao.getBlogById(BlogId.toString().trim()).await().toObject(Blog::class.java)!!
            withContext(Dispatchers.Main) {
                binding.blogHeading.text = blog.heading
                binding.blogView.text = blog.description
                binding.creatorName.text = blog.createdBy
                binding.blogViewProgressBar.visibility = View.GONE
                Glide.with(this@BlogViewActivity).load(blog.image).into(binding.creatorImage)
            }
        }

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