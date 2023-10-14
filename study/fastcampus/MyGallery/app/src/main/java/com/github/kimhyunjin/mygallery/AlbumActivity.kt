package com.github.kimhyunjin.mygallery

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.kimhyunjin.mygallery.databinding.ActivityAlbumBinding

class AlbumActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlbumBinding
    private lateinit var albumAdapter: AlbumAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val images = (intent.getStringArrayExtra("images") ?: emptyArray()).map {
            AlbumItem(Uri.parse(it))
        }

        albumAdapter = AlbumAdapter(images)

        binding.viewpager.apply {
            adapter = albumAdapter
        }
    }
}