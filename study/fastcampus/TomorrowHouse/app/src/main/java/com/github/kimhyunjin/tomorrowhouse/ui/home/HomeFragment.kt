package com.github.kimhyunjin.tomorrowhouse.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.github.kimhyunjin.tomorrowhouse.R
import com.github.kimhyunjin.tomorrowhouse.data.ArticleModel
import com.github.kimhyunjin.tomorrowhouse.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var articleAdapter: HomeArticleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        setupWriteButton(view)
        setupBookmarkButton()
        setupRecyclerView()

        Firebase.firestore.collection("articles").get().addOnSuccessListener { result ->
            val list = result.map {
                it.toObject<ArticleModel>()
            }
            articleAdapter.submitList(list)
        }

    }

    private fun setupRecyclerView() {
        articleAdapter = HomeArticleAdapter {
            it.articleId?.let { articleId ->
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToArticleFragment(
                        articleId
                    )
                )
            }
        }

        binding.homeRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = articleAdapter
        }
    }

    private fun setupBookmarkButton() {
        binding.bookmarkButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToBookmarkFragment())
        }
    }

    private fun setupWriteButton(view: View) {
        binding.writeButton.setOnClickListener {
            if (Firebase.auth.currentUser != null) {
                val action = HomeFragmentDirections.actionHomeFragmentToWriteArticleFragment()
                findNavController().navigate(action)
            } else {
                Snackbar.make(view, "로그인 후 사용해주세요.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}