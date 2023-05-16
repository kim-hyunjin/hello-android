package com.example.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context): View(context) {
    private lateinit var mDrawPath: CustomPath
    private lateinit var mCanvasBitmap: Bitmap
    private lateinit var mDrawPaint: Paint
    private lateinit var mCanvasPaint: Paint
    private lateinit var mCanvas: Canvas
    private var mBrushSize: Float = 0f
    private var mColor: Int = Color.BLACK

    init {
        setupDrawing()
    }

    private fun setupDrawing() {
        mDrawPath = CustomPath(mColor, mBrushSize)
        mDrawPaint = Paint()
        mDrawPaint.color = mColor
        mDrawPaint.style = Paint.Style.STROKE
        mDrawPaint.strokeJoin = Paint.Join.ROUND
        mDrawPaint.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        mBrushSize = 20f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mCanvasBitmap)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(mCanvasBitmap, 0f, 0f, mCanvasPaint)
        mDrawPaint.strokeWidth = mDrawPath.brushThickness
        mDrawPaint.color = mDrawPath.color
        canvas?.drawPath(mDrawPath, mDrawPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawPath.brushThickness = mBrushSize
                mDrawPath.color = mColor
                mDrawPath.reset()
                mDrawPath.moveTo(touchX!!, touchY!!)
            }
            MotionEvent.ACTION_MOVE -> {
                mDrawPath.lineTo(touchX!!, touchY!!)
            }
            MotionEvent.ACTION_UP -> {
                mDrawPath = CustomPath(mColor, mBrushSize)
            } else -> {
                return false
            }
        }

        invalidate()
        return true
    }
    internal inner class CustomPath(var color: Int, var brushThickness: Float): Path() {

    }
}