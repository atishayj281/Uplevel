package android.example.uptoskills

import android.example.uptoskills.Adapters.CourseVideoAdapter
import android.example.uptoskills.Adapters.videoItemClicked
import android.example.uptoskills.daos.CourseDao
import android.example.uptoskills.databinding.ActivityCourseVideoBinding
import android.example.uptoskills.models.FreeCourse
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CourseVideoActivity : AppCompatActivity(), videoItemClicked {

    private lateinit var binding: ActivityCourseVideoBinding
    private var course = FreeCourse()
    private lateinit var courseDao: CourseDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CourseVideoAdapter
    private lateinit var videoView: VideoView

    private fun startVideo(url: String){
        val videoUrl = "https://www.youtube.com/watch?v=${url}"
        // Uri object to refer the
        // resource from the videoUrl
        // Uri object to refer the
        // resource from the videoUrl
        val uri: Uri = Uri.parse(videoUrl)

        // sets the resource from the
        // videoUrl to the videoView

        // sets the resource from the
        // videoUrl to the videoView
        videoView.setVideoURI(uri)

        // creating object of
        // media controller class

        // creating object of
        // media controller class
        val mediaController = MediaController(this)

        // sets the anchor view
        // anchor view for the videoView

        // sets the anchor view
        // anchor view for the videoView
        mediaController.setAnchorView(videoView)

        // sets the media player to the videoView

        // sets the media player to the videoView
        mediaController.setMediaPlayer(videoView)

        // sets the media controller to the videoView

        // sets the media controller to the videoView
        videoView.setMediaController(mediaController)

        // starts the video

        // starts the video
        videoView.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        videoView = binding.youtubePlayerView
        recyclerView = binding.recyclerView
        courseDao = CourseDao()
        adapter = CourseVideoAdapter(this, this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)



        val courseId: String = intent.getStringExtra("courseId").toString()
        if(courseId != "null") {
            GlobalScope.launch(Dispatchers.IO) {
                course = courseDao.getCoursebyId(courseId).await().toObject(FreeCourse::class.java)!!
                withContext(Dispatchers.Main) {
                    adapter.updateVideos(course.videos)
                }
            }
        }
        startVideo(course.videos[0].link)

    }

    override fun onClicked(id: String, title: String) {
        startVideo(id)
    }

}