package com.example.happyplaces.activities

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.happyplaces.HappyPlaceApp
import com.example.happyplaces.IMAGE_DIRECTORY
import com.example.happyplaces.READ_IMAGE_PERMISSION
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.example.happyplaces.models.PlaceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddHappyPlaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddHappyPlaceBinding
    private var calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var imageSourceChooseDialog: AlertDialog
    private var placeImage: Uri? = null
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

        binding.btnSave.setOnClickListener {
            when {
                binding.etTitle.text.isNullOrEmpty() -> {
                    toast("제목을 입력해주세요.")
                }
                binding.etDescription.text.isNullOrEmpty() -> {
                    toast("설명을 입력해주세요.")
                }
                binding.etDate.text.isNullOrEmpty() -> {
                    toast("날짜를 입력해주세요.")
                }
                binding.etLocation.text.isNullOrEmpty() -> {
                    toast("위치를 입력해주세요.")
                }
                placeImage == null -> {
                    toast("사진을 추가해주세요.")
                } else -> {
                    val entity = PlaceEntity(
                        id = 0,
                        title = binding.etTitle.text.toString(),
                        description = binding.etDescription.text.toString(),
                        image = placeImage.toString(),
                        date = binding.etDate.text.toString(),
                        location = binding.etLocation.text.toString(),
                        latitude = 0.0,
                        longitude = 0.0
                    )
                    val historyDao = (application as HappyPlaceApp).db.placeDao()
                    lifecycleScope.launch(Dispatchers.IO) {
                        historyDao.insert(entity)
                        finish()
                    }
                }
            }
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
            val contentUri = result.data!!.data!!

            contentUri.let {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(contentResolver, contentUri)
                } else {
                    val source = ImageDecoder.createSource(contentResolver, contentUri)
                    ImageDecoder.decodeBitmap(source)
                }
                binding.ivPlaceImage.setImageBitmap(bitmap)
                lifecycleScope.launch(Dispatchers.IO) {
                    placeImage = saveImageToInternalStorage(bitmap)
                }
            }
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
            lifecycleScope.launch(Dispatchers.IO) {
                saveImageToInternalStorage(thumbnail)
            }
        }
    }

    private suspend fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        var result: Uri
        withContext(Dispatchers.IO) {
            val wrapper = ContextWrapper(applicationContext)
            var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
            file = File(file, "${UUID.randomUUID()}.jpg")

            try {
                val stream: OutputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
            } catch (e: IOException) {
                Log.e("saveImageToInternalStorage: ERROR", e.message.toString())
            }

            Log.i("saveImageToInternalStorage: SUCCESS", file.absolutePath)
            result =  Uri.parse(file.absolutePath)
        }
        return result
    }

    private fun updateDateInView() {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        binding.etDate.setText(sdf.format(calendar.time).toString())
    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}