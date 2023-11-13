package com.github.kimhyunjin.weatherapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationRequestPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    updateLocation()
                }

                else -> {
                    Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequestPermission.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    private fun updateLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationRequestPermission.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                Log.i("Location", "${it.latitude}, ${it.longitude}")
                fetchForecast(it)
            }
        }
    }

    private fun fetchForecast(location: Location) {
        val gson = GsonBuilder().setLenient().create()

        val retrofit =
            Retrofit.Builder().baseUrl("https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/")
                .addConverterFactory(
                    GsonConverterFactory.create(gson)
                ).build()

        val service = retrofit.create(WeatherService::class.java)

        val baseDateTime = BaseDateTime.getCurrentBaseDateTime()
        val converter = GeoPointConverter()
        val point = converter.convert(lat = location.latitude, lon = location.longitude)
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
                    weatherDateTimeMap.putIfAbsent(
                        key,
                        Forecast(date = it.forecastDate, time = it.forecastTime)
                    )

                    weatherDateTimeMap[key].apply {
                        if (this != null && it.category != null) {
                            when (it.category) {
                                Category.POP -> {
                                    precipitation = it.forecastValue.toInt()
                                }

                                Category.PTY -> {
                                    precipitationType =
                                        PrecipitationType.fromCode(it.forecastValue.toInt())
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