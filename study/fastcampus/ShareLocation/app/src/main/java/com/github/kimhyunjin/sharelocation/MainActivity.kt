package com.github.kimhyunjin.sharelocation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.kimhyunjin.sharelocation.databinding.ActivityMainBinding
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val keyhash = Utility.getKeyHash(this)
        Log.i("keyhash", keyhash)

        // Kakao SDK 초기화
        KakaoSdk.init(this, "1fee26e3619c1a35f974d497df14516f")

        startActivity(Intent(this, LoginActivity::class.java))
    }
}