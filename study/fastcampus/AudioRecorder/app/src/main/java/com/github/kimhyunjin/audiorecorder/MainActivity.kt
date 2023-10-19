package com.github.kimhyunjin.audiorecorder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.kimhyunjin.audiorecorder.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var fileName: String = ""

    private enum class State {
        RELEASE, RECORDING, PLAYING, PAUSE
    }
    private var state: State = State.RELEASE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fileName = "${externalCacheDir?.absoluteFile}/record_test.3gp"

        binding.recordButton.setOnClickListener {
            when(state) {
                State.RELEASE -> {
                    record()
                }
                State.RECORDING -> {
                    stopRecording()
                }
                else -> {

                }
            }
        }

        binding.playButton.setOnClickListener {
            when(state) {
                State.RELEASE -> {
                    startPlaying()
                }
                State.RECORDING -> {

                }
                State.PLAYING -> {
                    pausePlaying()
                }
                State.PAUSE -> {
                    resumePlaying()
                }
            }
        }

        binding.stopButton.setOnClickListener {
            stopPlaying()
        }
    }

    private fun record() {
        when {
            ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                startRecording()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.RECORD_AUDIO
            ) -> {
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

    private fun startRecording() {
        state = State.RECORDING
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(this)
        } else {
            MediaRecorder()
        }
        recorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e("APP", "MediaRecorder prepare() failed $e")
            }

            start()
        }

        binding.recordButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_stop_24))
        binding.recordButton.imageTintList = ColorStateList.valueOf(Color.BLACK)
        binding.playButton.isEnabled = false
        binding.playButton.alpha = 0.3f
        binding.stopButton.isEnabled = false
        binding.stopButton.alpha = 0.3f
    }

    private fun stopRecording() {
        state = State.RELEASE
        recorder?.apply {
            stop()
            release()
        }

        binding.recordButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_fiber_manual_record_24))
        binding.recordButton.imageTintList = ColorStateList.valueOf(Color.RED)
        binding.playButton.isEnabled = true
        binding.playButton.alpha = 1.0f
        binding.stopButton.isEnabled = true
        binding.stopButton.alpha = 1.0f
    }

    private fun startPlaying() {
        state = State.PLAYING

        player = MediaPlayer().apply {
            setDataSource(fileName)
            try {
                prepare()
            } catch (e: IOException) {
                Log.e("APP", "MediaPlayer prepare() failed $e")
            }

            start()

            setOnCompletionListener {
                state = State.RELEASE
                binding.recordButton.isEnabled = true
                binding.recordButton.alpha = 1.0f
            }
        }

        binding.recordButton.isEnabled = false
        binding.recordButton.alpha = 0.3f
        binding.playButton.setImageResource(R.drawable.baseline_pause_24)
    }

    private fun pausePlaying() {
        player?.apply {
            state = State.PAUSE
            pause()
        }

        binding.playButton.setImageResource(R.drawable.baseline_play_arrow_24)
    }

    private fun resumePlaying() {
        player?.apply {
            state = State.PLAYING
            start()
        }

        binding.playButton.setImageResource(R.drawable.baseline_pause_24)
    }

    private fun stopPlaying() {
        state = State.RELEASE

        player?.release()
        player = null

        binding.recordButton.isEnabled = true
        binding.recordButton.alpha = 1.0f
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
            startRecording()
        } else {
            showPermissionSettingDialog()
        }
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO = 100
    }
}