package com.github.kimhyunjin.architecturepattern

import retrofit2.Call
import retrofit2.http.GET

interface ImageService {

    @GET("v2/list")
    fun getRandomImage(): Call<List<ImageResponse>>
}