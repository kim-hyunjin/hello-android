package com.github.kimhyunjin.tomorrowhouse.ui.article

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.github.kimhyunjin.tomorrowhouse.R
import com.github.kimhyunjin.tomorrowhouse.databinding.FragmentWriteArticleBinding

class WriteArticleFragment : Fragment(R.layout.fragment_write_article) {
    private lateinit var binding: FragmentWriteArticleBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWriteArticleBinding.bind(view)
    }
}