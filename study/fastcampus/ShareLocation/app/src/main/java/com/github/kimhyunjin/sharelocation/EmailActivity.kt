package com.github.kimhyunjin.sharelocation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.kimhyunjin.sharelocation.databinding.ActivityEmailBinding

class EmailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.doneButton.setOnClickListener {
            if (binding.emailEditText.text.isNotEmpty()) {
                val data = Intent().apply {
                    putExtra("email", binding.emailEditText.text.toString())
                }
                setResult(RESULT_OK, data)
                finish()
            } else {
                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}