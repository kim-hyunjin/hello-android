package com.github.kimhyunjin.mygallery

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.kimhyunjin.mygallery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLoadImg.setOnClickListener {
            checkPermission()
        }
    }

    private fun checkPermission() {
        Log.d("test", "let's check permission")
        when {
            ContextCompat.checkSelfPermission(
                this,
                PERMISSION_FOR_READ_IMAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                loadImage()
            }

            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                showPermissionInfoDialog()
            }

            else -> {
                requestReadExternalStorage()
            }
        }

    }

    private fun showPermissionInfoDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("이미지를 가져오기 권한이 필요합니다.")
            setNegativeButton("취소", null)
            setPositiveButton("동의") { _, _ ->
                requestReadExternalStorage()
            }
        }.show()
    }

    private fun requestReadExternalStorage() {
        Log.d("test", "requestReadExternalStorage")
        ActivityCompat.requestPermissions(
            this,
            arrayOf(PERMISSION_FOR_READ_IMAGE),
            REQUEST_EXTERNAL_STORAGE
        )
    }

    private fun loadImage() {
        Toast.makeText(this, "이미지 가져오기", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val PERMISSION_FOR_READ_IMAGE =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                android.Manifest.permission.READ_MEDIA_IMAGES
            } else {
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            }
        const val REQUEST_EXTERNAL_STORAGE = 100
    }
}