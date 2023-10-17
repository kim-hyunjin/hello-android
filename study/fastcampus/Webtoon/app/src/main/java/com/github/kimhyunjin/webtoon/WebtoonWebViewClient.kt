package com.github.kimhyunjin.webtoon

import android.graphics.Bitmap
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.github.kimhyunjin.webtoon.databinding.FragmentWebViewBinding

class WebtoonWebViewClient(private val binding: FragmentWebViewBinding): WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

        binding.progressBar.visibility = View.GONE
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)

        binding.progressBar.visibility = View.VISIBLE
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if (request != null && request.url.toString().contains("comic.naver.com")) return false

        return true
    }
}