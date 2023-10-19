package com.github.kimhyunjin.audiorecorder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.kimhyunjin.audiorecorder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.recordButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // TODO 녹음 시작
                }

                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) -> {
                    showPermissionRationalDialog()
                }

                else -> {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        REQUEST_RECORD_AUDIO
                    )
                }
            }
        }
    }

    private fun showPermissionRationalDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("녹음을 위해 권한이 필요합니다.")
            setPositiveButton("권한 허용하기") { _, _ ->
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO
                )
            }
            setNegativeButton("거절하기") { dialog, _ ->
                dialog.cancel()
            }
        }.show()
    }

    private fun showPermissionSettingDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("녹음권한이 필요합니다. 앱 설정 화면에서 권한을 켜주세요.")
            setPositiveButton("권한 변경하러 가기") { _, _ ->
                navigateToSetting()
            }
            setNegativeButton("거절하기") { dialog, _ ->
                dialog.cancel()
            }
        }.show()
    }

    private fun navigateToSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val audioRecordPermissionGranted =
            requestCode == REQUEST_RECORD_AUDIO && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        if (audioRecordPermissionGranted) {
            // TODO 녹음 시작
        } else {
            showPermissionSettingDialog()
        }
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO = 100
    }
}