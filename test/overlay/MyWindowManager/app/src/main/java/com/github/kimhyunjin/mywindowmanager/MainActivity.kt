package com.github.kimhyunjin.mywindowmanager

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.kimhyunjin.mywindowmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
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
            runOverlay()
        }
        binding.destroyBtn.setOnClickListener {
            destroyOverlay()
        }
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
        Log.i("TEST", "runOverlay")
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