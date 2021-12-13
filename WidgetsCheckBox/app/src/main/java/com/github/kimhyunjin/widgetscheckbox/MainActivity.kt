package com.github.kimhyunjin.widgetscheckbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import com.github.kimhyunjin.widgetscheckbox.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var fruits: MutableList<String> = mutableListOf()
    private val listener by lazy {CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        when(buttonView.id) {
            R.id.checkApple -> {
                binding.selected.text = "사과"
                if(isChecked) fruits.add("사과") else fruits.remove("사과")
            }
            R.id.checkBanana -> {
                binding.selected.text = "바나나"
                if(isChecked) fruits.add("바나나") else fruits.remove("바나나")
            }
            R.id.checkOrange -> {
                binding.selected.text = "오렌지"
                if(isChecked) fruits.add("오렌지") else fruits.remove("오렌지")
            }
        }

        var msg = "selected: "
        fruits.forEachIndexed { index, item ->
            msg += if (index != fruits.size - 1) "$item, " else item
        }
        Log.d("CheckBox", msg)
    }}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.checkApple.setOnCheckedChangeListener(listener)
        binding.checkBanana.setOnCheckedChangeListener(listener)
        binding.checkOrange.setOnCheckedChangeListener(listener)

    }
}