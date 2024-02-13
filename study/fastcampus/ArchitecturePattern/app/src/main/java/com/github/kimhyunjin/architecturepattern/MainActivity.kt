package com.github.kimhyunjin.architecturepattern

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.kimhyunjin.architecturepattern.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            it.view = this
        }
    }

    fun openMvc() {
    }

    fun openMvp() {
    }

    fun openMvvm() {
    }

    fun openMvi() {
    }
}