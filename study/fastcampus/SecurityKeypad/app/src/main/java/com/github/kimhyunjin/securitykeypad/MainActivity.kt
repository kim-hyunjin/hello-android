package com.github.kimhyunjin.securitykeypad

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.kimhyunjin.securitykeypad.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.view = this
    }

    fun openShuffleKeypad() {

    }

    fun openVerifyOTP() {

    }

}