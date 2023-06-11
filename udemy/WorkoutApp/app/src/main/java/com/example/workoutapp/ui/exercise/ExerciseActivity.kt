package com.example.workoutapp.ui.exercise

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workoutapp.Constants
import com.example.workoutapp.ui.finish.FinishActivity
import com.example.workoutapp.R
import com.example.workoutapp.databinding.ActivityExerciseBinding
import com.example.workoutapp.databinding.DialogCustomBackConfirmationBinding
import com.example.workoutapp.data.exercise.ExerciseModel
import java.util.Locale

const val MAX_REST_PROGRESS = 1L
const val MAX_EXERCISE_PROGRESS = 3L
class ExerciseActivity: AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding: ActivityExerciseBinding? = null
    private var restTimer: CountDownTimer? = null
    private var restProgress = 0
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0
    private var tts: TextToSpeech? = null
    private var mediaPlayer: MediaPlayer? = null
    private var exerciseAdapter: ExerciseStatusAdapter? = null


    private var exerciseList: ArrayList<ExerciseModel>? = null // We will initialize the list later.
    private var currentExercisePosition = -1 // Current Position of Exercise.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarExercise?.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        exerciseList = Constants.defaultExerciseList()

        tts = TextToSpeech(this, this)

        val soundURI =
            Uri.parse("android.resource://com.example.workoutapp/" + R.raw.press_start)
        mediaPlayer = MediaPlayer.create(applicationContext, soundURI)
        mediaPlayer?.isLooping = false

        setRecyclerView()
        setRestView()

    }

    private fun customDialogForBackButton() {
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        dialogBinding.tvYes.setOnClickListener {
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.tvNo.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun setRecyclerView() {
        binding?.rvExerciseStatus?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)

        // Adapter class is attached to recycler view
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }

    private fun setRestView() {
        setRestViewVisibility(View.VISIBLE)
        setExerciseVIewVisibility(View.INVISIBLE)
        mediaPlayer?.start()
        binding?.tvUpcomingExerciseName?.text = exerciseList!![currentExercisePosition + 1].name
        setRestProgressBar()
    }

    private fun setExerciseView() {
        setRestViewVisibility(View.INVISIBLE)
        setExerciseVIewVisibility(View.VISIBLE)

        val currentExercise = exerciseList!![currentExercisePosition]
        binding?.ivImage?.setImageResource(currentExercise.image)
        binding?.tvExerciseName?.text = currentExercise.name
        tts?.speak(currentExercise.name, TextToSpeech.QUEUE_FLUSH, null, "")
        setExerciseProgressBar()
    }

    private fun setRestViewVisibility(visibility: Int) {
        binding?.flRestView?.visibility = visibility
        binding?.tvTitle?.visibility = visibility
        binding?.tvUpcomingExerciseName?.visibility = visibility
        binding?.upcomingLabel?.visibility = visibility
    }
    private fun setExerciseVIewVisibility(visibility: Int) {
        binding?.tvExerciseName?.visibility = visibility
        binding?.flExerciseView?.visibility = visibility
        binding?.ivImage?.visibility = visibility
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
                exerciseList!![currentExercisePosition].isSelected = true
                exerciseAdapter?.notifyItemChanged(currentExercisePosition)
                setExerciseView()
            }

        }.start()

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
                    exerciseList!![currentExercisePosition].isSelected = false
                    exerciseList!![currentExercisePosition].isCompleted = true
                    exerciseAdapter?.notifyItemChanged(currentExercisePosition)
                    setRestView()
                } else {
                    finish()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                }
            }
        }.start()
    }

    override fun onDestroy() {
        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }
        tts?.stop()
        tts?.shutdown()
        mediaPlayer?.stop()
        binding = null
        super.onDestroy()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US

        }
    }
}