package android.example.uptoskills

import android.example.uptoskills.Adapters.CourseVideoAdapter
import android.example.uptoskills.Adapters.videoItemClicked
import android.example.uptoskills.daos.CourseDao
import android.example.uptoskills.databinding.ActivityCourseVideoBinding
import android.example.uptoskills.models.FreeCourse
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer
import com.pierfrancescosoffritti.youtubeplayer.ui.PlayerUIController
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
    private lateinit var uiController: PlayerUIController
    private lateinit var mInitializedYouTubePlayer: YouTubePlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.recyclerView

        uiController = binding.youtubePlayerView.playerUIController
        uiController.showCurrentTime(true)
        uiController.showYouTubeButton(false)
        uiController.showBufferingProgress(true)
        uiController.showFullscreenButton(false)
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
        binding.youtubePlayerView.initialize({ initializedYouTubePlayer ->
            initializedYouTubePlayer.addListener(object : AbstractYouTubePlayerListener() {
                override fun onReady() {
                    mInitializedYouTubePlayer = initializedYouTubePlayer
                    initializedYouTubePlayer.cueVideo(course.videos[0].link, 0f)
                }
            })
        }, true)

    }

    override fun onClicked(id: String, title: String) {
        mInitializedYouTubePlayer.cueVideo(id, 0f)
    }

}