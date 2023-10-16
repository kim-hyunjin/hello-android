package com.github.kimhyunjin.musicplayer

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.kimhyunjin.musicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPlay.setOnClickListener {
            play()
        }
        binding.btnPause.setOnClickListener {
            pause()
        }
        binding.btnStop.setOnClickListener {
            stop()
        }
    }

    private fun play() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.sample).apply {
                isLooping = true
            }
        }
        mediaPlayer?.start()
    }

    private fun pause() {
        mediaPlayer?.pause()
    }

    private fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}