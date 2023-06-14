package com.example.happyplaces.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplaces.R
import com.example.happyplaces.databinding.ActivityHappyPlaceDetailBinding
import com.example.happyplaces.models.PlaceEntity

class HappyPlaceDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHappyPlaceDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarHappyPlaceDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var place: PlaceEntity? = null
        if (intent.hasExtra("place")) {
            place = intent.getParcelableExtra("place")
        }
        if (place != null) {
            supportActionBar?.title = place.title
            binding.ivPlaceImage.setImageURI(Uri.parse(place.image))
            binding.tvDescription.text = place.description
            binding.tvLocation.text = place.location
            binding.btnViewOnMap.setOnClickListener {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container_view, MapsFragment(place)).commit()
            }
        }

    }
}