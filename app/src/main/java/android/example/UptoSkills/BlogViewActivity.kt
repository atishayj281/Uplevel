package android.example.UptoSkills

import android.example.UptoSkills.daos.BlogDao
import android.example.UptoSkills.databinding.ActivityBlogViewBinding
import android.example.UptoSkills.models.Blog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    }

    // Method to show blog in the activity
    private fun updateBlog(BlogId: String?) {
        var blogDao = BlogDao()
        GlobalScope.launch(Dispatchers.IO) {
            blog = blogDao.getBlogById(BlogId.toString()).await().toObject(Blog::class.java)!!
            withContext(Dispatchers.Main) {
                binding.blogHeading.text = blog.title
                binding.blogView.text = blog.text
                binding.creatorName.text = blog.createdBy.displayName
                binding.blogViewProgressBar.visibility = View.GONE
                Glide.with(this@BlogViewActivity).load(blog.createdBy.userImage).circleCrop().into(binding.creatorImage)
            }
        }

    }


}