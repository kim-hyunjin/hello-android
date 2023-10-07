package com.github.kimhyunjin.emergencymedicalinfoapp

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import com.github.kimhyunjin.emergencymedicalinfoapp.databinding.ActivityEditBinding

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bloodTypeSpinner.adapter = ArrayAdapter.createFromResource(this, R.array.blood_types, android.R.layout.simple_list_item_1)

        binding.birthLayer.setOnClickListener {
            val listener = OnDateSetListener { date, year, month, dayOfMonth ->
                binding.birthValueTextView.text = "$year-${month.inc()}-$dayOfMonth"
            }
            DatePickerDialog(this, listener, 2000, 0, 1).show()
        }

        binding.warningCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.warningValueEditText.isVisible = isChecked
        }

        binding.warningValueEditText.isVisible = binding.warningCheckBox.isChecked
    }
}