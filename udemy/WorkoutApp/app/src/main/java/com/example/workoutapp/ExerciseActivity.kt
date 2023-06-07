package com.example.workoutapp

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.workoutapp.databinding.ActivityExerciseBinding

const val MAX_REST_PROGRESS = 10
class ExerciseActivity: AppCompatActivity() {
    private var binding: ActivityExerciseBinding? = null
    private var restTimer: CountDownTimer? = null
    private var restProgress = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarExercise)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setRestProgressBar()
    }

    private fun setRestProgressBar() {
        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }

        binding?.progressBar?.progress = restProgress
        restTimer = object: CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.progressBar?.progress = MAX_REST_PROGRESS - restProgress
                binding?.tvTimer?.text = (MAX_REST_PROGRESS - restProgress).toString()
            }

            override fun onFinish() {
                Toast.makeText(this@ExerciseActivity, "we will start the exercise.", Toast.LENGTH_LONG).show()
            }

        }.start()

    }

    override fun onDestroy() {
        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }
        binding = null
        super.onDestroy()
    }
}