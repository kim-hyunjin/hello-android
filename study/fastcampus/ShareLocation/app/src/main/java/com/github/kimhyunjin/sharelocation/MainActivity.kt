package com.github.kimhyunjin.sharelocation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.kimhyunjin.sharelocation.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var googleMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val keyhash = Utility.getKeyHash(this)
        Log.i("keyhash", keyhash)

        // Kakao SDK 초기화
        KakaoSdk.init(this, "1fee26e3619c1a35f974d497df14516f")

//        startActivity(Intent(this, LoginActivity::class.java))

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        this.googleMap.addMarker(
            MarkerOptions()
            .position(sydney)
            .title("Marker in Sydney"))
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}