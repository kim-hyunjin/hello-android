package com.github.kimhyunjin.youtube

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kimhyunjin.youtube.databinding.ActivityMainBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val videoList: VideoList by lazy {
        readData("video.json", VideoList::class.java) ?: VideoList(emptyList())
    }

    private var player: ExoPlayer? = null

    private lateinit var videoAdapter: VideoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        videoAdapter = VideoAdapter(context = this) { videoItem ->
            binding.motionLayout.setTransition(R.id.collapse, R.id.expand)
            binding.motionLayout.transitionToEnd()

            play(videoItem)
        }
        binding.motionLayout.jumpToState(R.id.collapse)

        binding.videoListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = videoAdapter
        }

        Log.i("videos", videoList.videos.toString())
        videoAdapter.submitList(videoList.videos)
    }

    private fun play(videoItem: VideoItem) {
        player?.setMediaItem(MediaItem.fromUri(Uri.parse(videoItem.videoUrl)))
        player?.prepare()
        player?.play()
    }

    private fun initExoPlayer() {
        player = ExoPlayer.Builder(this).build().also {
            binding.playerView.player = it
        }
    }

    override fun onStart() {
        super.onStart()

        if (player == null) {
            initExoPlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onResume() {
        super.onResume()
        if (player == null) {
            initExoPlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}