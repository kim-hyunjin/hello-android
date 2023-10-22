package com.github.kimhyunjin.githubrepofinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kimhyunjin.githubrepofinder.adapter.RepoAdapter
import com.github.kimhyunjin.githubrepofinder.databinding.ActivityRepoBinding
import com.github.kimhyunjin.githubrepofinder.model.Repo
import com.github.kimhyunjin.githubrepofinder.network.GithubService
import com.github.kimhyunjin.githubrepofinder.network.GithubServiceSingleton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRepoBinding
    private lateinit var repoAdapter: RepoAdapter
    private lateinit var githubService: GithubService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repoAdapter = RepoAdapter()
        githubService = GithubServiceSingleton.getInstance(this)

        binding.repoRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RepoActivity)
            adapter = repoAdapter
        }

        val username = intent.getStringExtra("username") ?: return
        binding.usernameTextView.text = username
        searchRepo(username)
    }

    private fun searchRepo(username: String) {
        githubService.listRepos(username).enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                Log.i("listRepos", response.body().toString())
                repoAdapter.submitList(response?.body())
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                Log.e("listRepos", t.toString())
            }

        })
    }
}