package com.github.kimhyunjin.mywindowmanager

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.kimhyunjin.mywindowmanager.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (Settings.canDrawOverlays(this)) {
            runOverlay()
        } else {
            Toast.makeText(this, "오버레이를 띄우려면 설정이 필요합니다.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.overlayBtn.setOnClickListener {
            checkPermission()
        }
        binding.destroyBtn.setOnClickListener {
            destroyOverlay()
        }
    }

    private fun checkPermission() {
        if (Settings.canDrawOverlays(this)) {
            runOverlay()
            return;
        }

        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            "package:$packageName".toUri()
        )
        overlayPermissionLauncher.launch(intent)
    }

    private fun runOverlay() {
        Log.i("TEST", "runOverlay")
        Intent().also { intent ->
            intent.setComponent(
                ComponentName(
                    "com.github.kimhyunjin.mywindowmanager",
                    "com.github.kimhyunjin.mywindowmanager.receiver.MyBroadcastReceiver"
                )
            )
            intent.setAction("com.test.MY_BROADCAST")
            intent.putExtra("data", "runOverlay")
            sendBroadcast(intent)
        }
    }

    private fun destroyOverlay() {
        Log.i("TEST", "destroyOverlay")
        Intent().also { intent ->
            intent.setComponent(
                ComponentName(
                    "com.github.kimhyunjin.mywindowmanager",
                    "com.github.kimhyunjin.mywindowmanager.receiver.MyBroadcastReceiver"
                )
            )
            intent.setAction("com.test.MY_BROADCAST")
            intent.putExtra("data", "destroyOverlay")
            sendBroadcast(intent)
        }
    }
}