package com.github.kimhyunjin.sharelocation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.github.kimhyunjin.sharelocation.databinding.ActivityMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    getCurrentLocation()
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    getCurrentLocation()
                }

                else -> {
                    // TODO 설정으로 보내기 or 교육용 팝업을 띄워 권한 요청하기
                }
            }
        }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // 새로 요청된 위치 정보
            for (location in locationResult.locations) {
                Log.i(
                    "MapActivity",
                    "onLocationResult: ${location.latitude}, ${location.longitude}"
                )

                val uid = Firebase.auth.currentUser?.uid.orEmpty()

                val locationMap = mutableMapOf<String, Any>()
                locationMap["latitude"] = location.latitude
                locationMap["longitude"] = location.longitude
                Firebase.database.reference.child("Person").child(uid).updateChildren(locationMap)
                // 지도 마커 움직이기

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5 * 1000).build()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        it.latitude,
                        it.longitude
                    ), 16f
                )
            )
        }
    }

    private fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        googleMap.setMaxZoomPreference(20.0f)
        googleMap.setMinZoomPreference(10.0f)

    }
}