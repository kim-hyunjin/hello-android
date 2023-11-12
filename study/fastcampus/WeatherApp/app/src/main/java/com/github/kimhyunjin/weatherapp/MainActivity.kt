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

        val baseDateTime = BaseDateTime.getCurrentBaseDateTime()
        val converter = GeoPointConverter()
        val point = converter.convert(lat = 37.5532, lon=127.1906)
        service.getForecast(
            baseDate = baseDateTime.baseDate,
            baseTime = baseDateTime.baseTime,
            nx = point.nx,
            ny = point.ny
        ).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                val weatherDateTimeMap = mutableMapOf<String, Forecast>()
                val weatherList = response.body()?.response?.body?.items?.entities

                weatherList?.forEach {
                    Log.i("Weather", it.toString())
                    val key = "${it.forecastDate}/${it.forecastTime}"
                    weatherDateTimeMap.putIfAbsent(key, Forecast(date = it.forecastDate, time = it.forecastTime))

                    weatherDateTimeMap[key].apply {
                        if (this != null && it.category != null) {
                            when(it.category) {
                                Category.POP -> {
                                    precipitation = it.forecastValue.toInt()
                                }
                                Category.PTY -> {
                                    precipitationType = PrecipitationType.fromCode(it.forecastValue.toInt())
                                }
                                Category.SKY -> {
                                    sky = SkyType.fromCode(it.forecastValue.toInt())
                                }
                                Category.TMP -> {
                                    temperature = it.forecastValue.toDouble()
                                }
                            }
                        }
                    }
                }

                Log.i("Forecast", weatherDateTimeMap.toString())
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }
}