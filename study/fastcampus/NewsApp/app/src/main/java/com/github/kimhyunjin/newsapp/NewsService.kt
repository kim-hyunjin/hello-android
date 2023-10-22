package com.github.kimhyunjin.newsapp

import retrofit2.Call
import retrofit2.http.GET

interface NewsService {

    @GET("browse/feed/")
    fun mainFeed(): Call<NewsRss>
}