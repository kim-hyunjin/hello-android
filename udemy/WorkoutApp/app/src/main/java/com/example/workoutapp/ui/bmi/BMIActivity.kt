package com.example.workoutapp.ui.bmi

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.workoutapp.databinding.ActivityBmiBinding
import com.example.workoutapp.data.bmi.BMI
import androidx.databinding.DataBindingUtil
import com.example.workoutapp.R
import com.example.workoutapp.data.bmi.UNIT_TYPE

class BMIActivity : AppCompatActivity() {

    private var binding: ActivityBmiBinding? = null
    private var currentUnit: UNIT_TYPE = UNIT_TYPE.METRIC

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bmi)
        binding?.isMetric = true

        setSupportActionBar(binding?.toolbarBmiActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "CALCULATE BMI"

        binding?.rgUnits?.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.rbMetricUnits) {
                currentUnit = UNIT_TYPE.METRIC
                binding?.etMetricUnitHeight?.text!!.clear()
                binding?.etMetricUnitWeight?.text!!.clear()
            } else {
                currentUnit = UNIT_TYPE.US
                binding?.etUsMetricUnitWeight?.text!!.clear()
                binding?.etUsMetricUnitHeightFeet?.text!!.clear()
                binding?.etUsMetricUnitHeightInch?.text!!.clear()
            }
            binding?.isMetric = currentUnit == UNIT_TYPE.METRIC
            binding?.llDiplayBMIResult?.visibility = View.INVISIBLE
        }

        binding?.btnCalculateUnits?.setOnClickListener {
            calculateUnits()
        }
    }

    private fun calculateUnits() {
        if (validateUnits()) {
            val weightValue: Float
            val heightValue: Float
            if (currentUnit == UNIT_TYPE.METRIC) {
                weightValue = binding?.etMetricUnitWeight?.text.toString().toFloat()
                heightValue = binding?.etMetricUnitHeight?.text.toString().toFloat() / 100 // cm to meter
            } else {
                weightValue = binding?.etUsMetricUnitWeight?.text.toString()
                    .toFloat()
                val usUnitHeightValueFeet: String = binding?.etUsMetricUnitHeightFeet?.text.toString()
                val usUnitHeightValueInch: String = binding?.etUsMetricUnitHeightInch?.text.toString()
                // Here the Height Feet and Inch values are merged and multiplied by 12 for converting it to inches.
                heightValue =
                    usUnitHeightValueInch.toFloat() + usUnitHeightValueFeet.toFloat() * 12
            }
            binding?.bmi = BMI(height = heightValue, weight = weightValue, currentUnit)
            binding?.llDiplayBMIResult?.visibility = View.VISIBLE
        } else {
            Toast.makeText(this@BMIActivity, "Please enter valid values.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun validateUnits(): Boolean {
        if (currentUnit == UNIT_TYPE.METRIC) {
            if (binding?.etMetricUnitWeight?.text.toString().isEmpty()) {
                return false
            } else if (binding?.etMetricUnitHeight?.text.toString().isEmpty()) {
                return false
            }

            return true
        } else {
            var isValid = true

            when {
                binding?.etMetricUnitWeight?.text.toString().isEmpty() -> {
                    isValid = false
                }
                binding?.etUsMetricUnitHeightFeet?.text.toString().isEmpty() -> {
                    isValid = false
                }
                binding?.etUsMetricUnitHeightInch?.text.toString().isEmpty() -> {
                    isValid = false
                }
            }

            return isValid
        }
    }
}