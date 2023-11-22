package com.github.kimhyunjin.foodmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.github.kimhyunjin.foodmap.databinding.ActivityMainBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding
    private var naverMap: NaverMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.isNullOrEmpty()) return false

                searchWithQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                return false
            }

        })


    }

    private fun searchWithQuery(query: String) {
        SearchRepository.getGoodRestaurant(query).enqueue(object : Callback<SearchResult> {
            override fun onResponse(call: Call<SearchResult>, response: Response<SearchResult>) {
                Log.i("search", response.body().toString())

                val searchItemList = response.body()?.items.orEmpty()
                if (searchItemList.isEmpty()) {
                    Toast.makeText(this@MainActivity, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                    return
                } else if (naverMap == null) {
                    Toast.makeText(this@MainActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    return
                }

                val markerList = searchItemList.map {
                    Marker(
                        LatLng(
                            it.mapy.toDouble() / 10000000,
                            it.mapx.toDouble() / 10000000
                        )
                    ).apply {
                        captionText = it.title
                        map = naverMap
                    }
                }

                Log.i("search", markerList.map { it.position }.toString())


                val cameraUpdate = CameraUpdate.scrollTo(markerList.first().position)
                    .animate(CameraAnimation.Easing)
                naverMap!!.moveCamera(cameraUpdate)
            }

            override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                Toast.makeText(this@MainActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                return
            }

        })
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.5666102, 126.9783881))
            .animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)
    }

}