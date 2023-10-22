package com.github.kimhyunjin.githubrepofinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kimhyunjin.githubrepofinder.adapter.UserAdapter
import com.github.kimhyunjin.githubrepofinder.databinding.ActivityMainBinding
import com.github.kimhyunjin.githubrepofinder.model.Repo
import com.github.kimhyunjin.githubrepofinder.model.UserDto
import com.github.kimhyunjin.githubrepofinder.network.GithubService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var githubService: GithubService

    private val handler = Handler(Looper.getMainLooper())
    private var searchFor: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userAdapter = UserAdapter {
            val intent = Intent(this@MainActivity, RepoActivity::class.java)
            intent.putExtra("username", it.username)
            startActivity(intent)
        }
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val newReq =
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${this@MainActivity.getString(R.string.GITHUB_API_KEY)}").build()
            chain.proceed(newReq)
        }.build()

        val retrofit = Retrofit.Builder().baseUrl("https://api.github.com/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()

        githubService = retrofit.create(GithubService::class.java)


        binding.userRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }

        val runnable = Runnable {
            searchUser()
        }

        binding.searchEditText.addTextChangedListener {
            searchFor = it.toString()
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, 300)
        }

    }

    private fun searchUser() {
        githubService.searchUsers(searchFor).enqueue(object : Callback<UserDto> {
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                Log.i("searchUsers", response.body().toString())
                userAdapter.submitList(response.body()?.items)
            }

            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                Log.e("searchUsers", t.toString())
                Toast.makeText(this@MainActivity, "에러 발생", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun searchRepo() {
        githubService.listRepos("kim-hyunjin").enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                Log.i("listRepos", response.body().toString())
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                Log.e("listRepos", t.toString())
            }

        })
    }
}