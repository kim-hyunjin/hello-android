package com.github.kimhyunjin.newsapp

import retrofit2.Call
import retrofit2.http.GET

interface NewsService {

    @GET("rss?hl=en-US&gl=US&ceid=US:en")
    fun mainFeed(): Call<NewsRss>
}