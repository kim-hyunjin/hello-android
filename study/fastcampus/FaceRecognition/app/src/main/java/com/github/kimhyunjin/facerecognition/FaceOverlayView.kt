package com.github.kimhyunjin.facerecognition

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.util.SizeF
import android.view.View

class FaceOverlayView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAtt: Int = 0
) : View(context, attributeSet, defStyleAtt) {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        alpha = 90
        style = Paint.Style.FILL
    }

    // 얼굴형 회색 스트로크
    private val facePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        style = Paint.Style.STROKE
        strokeWidth = 10f
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

    // 가운데 구멍내기
    private val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    }

    private val facePath = Path()

    // 얼굴형 custom UI 그리기
    fun setSize(rectF: RectF, sizeF: SizeF, centerF: PointF) {
        val topOffset = sizeF.width / 2
        val bottomOffset = sizeF.width / 5 // 아래쪽이 더 갸름

        with(facePath) {
            reset()
            // 오른쪽 반원
            moveTo(centerF.x, rectF.top)
            cubicTo(
                rectF.right + topOffset,
                rectF.top,
                rectF.right + bottomOffset,
                rectF.bottom,
                centerF.x,
                rectF.bottom
            )
            // 왼쪽 반원
            cubicTo(
                rectF.left - bottomOffset,
                rectF.bottom,
                rectF.left - topOffset,
                rectF.top,
                centerF.x,
                rectF.top
            )
            close()
        }
        postInvalidate()
    }

    fun reset() {
        facePath.reset()
        invalidate()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawOverlay(canvas)
    }

    private fun drawOverlay(canvas: Canvas) {
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), backgroundPaint)
        canvas.drawPath(facePath, maskPaint)
        canvas.drawPath(facePath, facePaint)
    }
}