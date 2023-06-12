package com.example.happyplaces

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddHappyPlaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddHappyPlaceBinding
    private var calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var imageSourceChooseDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        binding.etDate.setOnClickListener {
            DatePickerDialog(
                this@AddHappyPlaceActivity,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        imageSourceChooseDialog =
            AlertDialog.Builder(this@AddHappyPlaceActivity).setTitle("장소 사진 추가하기").setItems(
                arrayOf("갤러리", "카메라")
            ) { dialog, which ->
                when (which) {
                    0 -> {
                        requestOpenGallery()
                    }
                    1 -> {
                        requestCamera()
                    }
                }
            }.create()

        binding.tvAddImage.setOnClickListener {
            imageSourceChooseDialog.show()
        }
    }

    private fun requestOpenGallery() {
        if (checkSelfPermission(READ_IMAGE_PERMISSION) == PackageManager.PERMISSION_DENIED) {
            requestPermissionsLauncher.launch(arrayOf(READ_IMAGE_PERMISSION))
        } else {
            openGallery()
        }
    }

    private fun requestCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            requestPermissionsLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        } else {
            openCamera()
        }
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                if (!isGranted) {
                    Toast.makeText(
                        this,
                        "Oops, you just denied the permission.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@forEach
                }
                when (permissionName) {
                    READ_IMAGE_PERMISSION -> {
                        openGallery()
                    }
                    Manifest.permission.CAMERA -> {
                        openCamera()
                    }
                }

            }
        }

    private fun openGallery() {
        val imagePickIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        openGalleryLauncher.launch(imagePickIntent)
    }

    private val openGalleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            binding.ivPlaceImage.setImageURI(result.data?.data)
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        openCameraLauncher.launch(intent)
    }

    private val openCameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val thumbnail = result.data!!.extras!!.get("data") as Bitmap
            binding.ivPlaceImage.setImageBitmap(thumbnail)
        }
    }

    private fun updateDateInView() {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        binding.etDate.setText(sdf.format(calendar.time).toString())
    }
}