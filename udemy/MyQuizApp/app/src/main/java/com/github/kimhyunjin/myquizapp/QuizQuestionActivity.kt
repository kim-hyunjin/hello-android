package com.github.kimhyunjin.myquizapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.setMargins

class QuizQuestionActivity : AppCompatActivity() {
    lateinit var tvQuestion: TextView
    lateinit var progressBar: ProgressBar
    lateinit var tvProgress: TextView
    lateinit var ivImage: ImageView
    lateinit var optionContainer: LinearLayout

    private val optionViewLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    private var currentQuestionIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_question)

        tvQuestion = findViewById(R.id.tv_question)
        progressBar = findViewById(R.id.progressBar)
        tvProgress = findViewById(R.id.tv_progress)
        ivImage = findViewById(R.id.iv_image)
        optionContainer = findViewById(R.id.optionContainer)

        val questionList = Constants.getQuestions()
        Log.i("QuestionList size is", "${questionList.size}")
        progressBar.max = questionList.size

        createQuestionView(questionList[currentQuestionIndex])
    }

    private fun createQuestionView(question: Question) {
        tvQuestion.text = question.question
        progressBar.progress = currentQuestionIndex + 1
        tvProgress.text = getString(R.string.tv_progress, currentQuestionIndex + 1, progressBar.max)
        ivImage.setImageResource(question.image)
        createOptionsView(question.options)
    }

    private fun createOptionsView(options: List<String>) {
        optionContainer.removeAllViews()
        for (i in 0 until options.count()) {
            val tv = TextView(this)
            tv.id = i
            tv.text = options[i]
            tv.textSize = 18f
            tv.gravity = Gravity.CENTER
            tv.setPadding(15, 15,15, 15)
            tv.setBackgroundResource(R.drawable.default_option_border_bg)
            tv.setTextColor(Color.parseColor("#7A8089"))
            optionViewLayoutParam.setMargins(10)
            optionContainer.addView(tv, optionViewLayoutParam)
        }
    }
}