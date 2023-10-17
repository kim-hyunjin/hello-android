package com.github.kimhyunjin.webtoon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.github.kimhyunjin.webtoon.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().apply {
            replace(
                binding.frameLayout.id,
                WebViewFragment.newInstance("https://comic.naver.com/webtoon/detail?titleId=814543&no=9&week=tue")
            )
            commit()
        }
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.fragments.first()
        if (currentFragment is WebViewFragment) {
            if (currentFragment.canGoBack()) {
                currentFragment.goBack()
            } else {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }
}