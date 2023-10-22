package com.github.kimhyunjin.githubrepofinder.network

import android.content.Context
import com.github.kimhyunjin.githubrepofinder.R
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GithubServiceSingleton private constructor() {
    companion object {
        private var githubService: GithubService? = null

        fun getInstance(context: Context): GithubService {
            return githubService ?: synchronized(this) {
                if (githubService != null) return githubService!!

                val client = OkHttpClient.Builder().addInterceptor { chain ->
                    val newReq =
                        chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer ${context.getString(R.string.GITHUB_API_KEY)}").build()
                    chain.proceed(newReq)
                }.build()

                val retrofit = Retrofit.Builder().baseUrl("https://api.github.com/").client(client)
                    .addConverterFactory(GsonConverterFactory.create()).build()

                githubService = retrofit.create(GithubService::class.java)
                return githubService!!
            }
        }
    }
}