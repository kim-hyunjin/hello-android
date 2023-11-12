package com.github.kimhyunjin.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gson = GsonBuilder().setLenient().create()

        val retrofit =
            Retrofit.Builder().baseUrl("https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/")
                .addConverterFactory(
                    GsonConverterFactory.create(gson)
                ).build()

        val service = retrofit.create(WeatherService::class.java)

        service.getForecast(
            baseDate = "20231112",
            baseTime = "0500",
            nx = 55,
            ny = 127
        ).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                response.body()?.response?.body?.items?.entities?.get(0)?.toString()
                    ?.let { Log.i("Weather", it) }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }
}