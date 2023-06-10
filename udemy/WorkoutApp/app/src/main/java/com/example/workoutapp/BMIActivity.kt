package com.example.workoutapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.workoutapp.databinding.ActivityBmiBinding
import com.example.workoutapp.models.BMI

class BMIActivity : AppCompatActivity() {

    private var binding: ActivityBmiBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarBmiActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "CALCULATE BMI"

        binding?.btnCalculateUnits?.setOnClickListener {
            if (validateMetricUnits()) {
                // The height value is converted to a float value and divided by 100 to convert it to meter.
                val heightValue: Float = binding?.etMetricUnitHeight?.text.toString().toFloat() / 100

                // The weight value is converted to a float value
                val weightValue: Float = binding?.etMetricUnitWeight?.text.toString().toFloat()

                displayBMIResult(BMI(heightValue, weightValue))

            } else {
                Toast.makeText(this@BMIActivity, "Please enter valid values.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun validateMetricUnits(): Boolean {
        if (binding?.etMetricUnitWeight?.text.toString().isEmpty()) {
            return false
        } else if (binding?.etMetricUnitHeight?.text.toString().isEmpty()) {
            return false
        }

        return true
    }

    private fun displayBMIResult(bmi: BMI) {
        //Use to set the result layout visible
        binding?.llDiplayBMIResult?.visibility = View.VISIBLE

        binding?.tvBMIValue?.text = bmi.displayBmiValue // Value is set to TextView
        binding?.tvBMIType?.text = bmi.bmiLabel // Label is set to TextView
        binding?.tvBMIDescription?.text = bmi.bmiDescription // Description is set to TextView
    }
}