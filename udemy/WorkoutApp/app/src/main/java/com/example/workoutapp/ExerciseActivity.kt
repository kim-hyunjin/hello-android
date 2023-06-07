package com.example.workoutapp

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.workoutapp.databinding.ActivityExerciseBinding
import com.example.workoutapp.models.ExerciseModel

const val MAX_REST_PROGRESS = 10L
const val MAX_EXERCISE_PROGRESS = 30L
class ExerciseActivity: AppCompatActivity() {
    private var binding: ActivityExerciseBinding? = null
    private var restTimer: CountDownTimer? = null
    private var restProgress = 0
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0

    private var exerciseList: ArrayList<ExerciseModel>? = null // We will initialize the list later.
    private var currentExercisePosition = -1 // Current Position of Exercise.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        exerciseList = Constants.defaultExerciseList()

        setRestView()
    }

    private fun setRestView() {
        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE

        setRestProgressBar()
    }

    private fun setRestProgressBar() {
        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }

        binding?.progressBar?.progress = restProgress
        restTimer = object: CountDownTimer(MAX_REST_PROGRESS * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.progressBar?.progress = (MAX_REST_PROGRESS - restProgress).toInt()
                binding?.tvTimer?.text = (MAX_REST_PROGRESS - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++
                setExerciseView()
            }

        }.start()

    }

    private fun setExerciseView() {
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE

        val currentExercise = exerciseList!![currentExercisePosition]
        binding?.ivImage?.setImageResource(currentExercise.image)
        binding?.tvExerciseName?.text = currentExercise.name
        setExerciseProgressBar()
    }

    private fun setExerciseProgressBar() {
        if (exerciseTimer != null) {
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }

        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object : CountDownTimer(MAX_EXERCISE_PROGRESS * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = (MAX_EXERCISE_PROGRESS - exerciseProgress).toInt()
                binding?.tvTimerExercise?.text = (MAX_EXERCISE_PROGRESS - exerciseProgress).toString()
            }

            override fun onFinish() {
                if (currentExercisePosition < exerciseList!!.size - 1) {
                    setRestView()
                } else {
                    Toast.makeText(
                        this@ExerciseActivity,
                        "Congratulations! You have completed the 7 minutes workout.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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