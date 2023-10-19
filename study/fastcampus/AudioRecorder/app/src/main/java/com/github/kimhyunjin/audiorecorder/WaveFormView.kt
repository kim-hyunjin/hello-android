package com.github.kimhyunjin.audiorecorder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class WaveFormView @JvmOverloads constructor (context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private val ampList = mutableListOf<Float>()
    private val rectList = mutableListOf<RectF>()
    private val redPaint = Paint().apply {
        color = Color.RED
    }
    private val rectWidth = 10f
    private var tick = 0

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (rectF in rectList) {
            canvas.drawRect(rectF, redPaint)
        }
    }

    fun addAmplitude(maxAmplitude: Float) {

        ampList.add(maxAmplitude)
        rectList.clear()

        val maxRectCnt = (this.width / rectWidth).toInt()

        val amps = ampList.takeLast(maxRectCnt)
        ampsToRectList(amps)

        invalidate()
    }

    fun replayAmplitude() {
        rectList.clear()

        val maxRectCnt = (this.width / rectWidth).toInt()

        val amps = ampList.take(tick).takeLast(maxRectCnt)
        ampsToRectList(amps)
        tick++

        invalidate()
    }

    fun clearData() {
        ampList.clear()
    }

    fun clearWave() {
        rectList.clear()
        invalidate()
        tick = 0
    }

    private fun ampsToRectList(amps: List<Float>) {
        for ((i, amp) in amps.withIndex()) {
            val rectF = RectF().apply {
                top = 0f
                bottom = amp
                left = i * rectWidth
                right = left + rectWidth
            }

            rectList.add(rectF)
        }
    }
}