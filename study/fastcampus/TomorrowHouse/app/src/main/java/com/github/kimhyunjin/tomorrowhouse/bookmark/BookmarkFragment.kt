package com.github.kimhyunjin.tomorrowhouse.bookmark

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.github.kimhyunjin.tomorrowhouse.R
import com.github.kimhyunjin.tomorrowhouse.databinding.FragmentBookmarkBinding


class BookmarkFragment : Fragment(R.layout.fragment_bookmark) {

    private lateinit var binding: FragmentBookmarkBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBookmarkBinding.bind(view)
    }
}