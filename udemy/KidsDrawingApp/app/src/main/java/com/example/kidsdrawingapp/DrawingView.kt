package com.example.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.util.TypedValue
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
    private var mPaths = ArrayList<CustomPath>()

    init {
        setupDrawing()
    }

    fun setSizeForBrush(newSize: Float) {
        // 화면 크기를 고려
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, resources.displayMetrics)
        mDrawPaint.strokeWidth = mBrushSize
    }

    fun setColorForBrush(color: Int) {
        mColor = color
    }

    private fun setupDrawing() {
        mDrawPath = CustomPath(mColor, mBrushSize)

        mDrawPaint = Paint()
        mDrawPaint.color = mColor
        mDrawPaint.style = Paint.Style.STROKE
        mDrawPaint.strokeJoin = Paint.Join.ROUND
        mDrawPaint.strokeCap = Paint.Cap.ROUND

        mCanvasPaint = Paint(Paint.DITHER_FLAG)
//        mBrushSize = 20f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mCanvasBitmap)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(mCanvasBitmap, 0f, 0f, mCanvasPaint)

        drawPreviousPaths(canvas)

        mDrawPaint.strokeWidth = mDrawPath.brushThickness
        mDrawPaint.color = mDrawPath.color
        canvas?.drawPath(mDrawPath, mDrawPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                setPathWithUserSetting()
                mDrawPath.moveTo(touchX!!, touchY!!)
            }
            MotionEvent.ACTION_MOVE -> {
                mDrawPath.lineTo(touchX!!, touchY!!)
            }
            MotionEvent.ACTION_UP -> {
                savePathAndPrepareNewPath()
            } else -> {
                return false
            }
        }

        invalidate()
        return true
    }

    private fun drawPreviousPaths(canvas: Canvas?) {
        if (canvas == null) return

        for (path in mPaths) {
            mDrawPaint.strokeWidth = path.brushThickness
            mDrawPaint.color = path.color
            canvas.drawPath(path, mDrawPaint)
        }
    }
    private fun setPathWithUserSetting() {
        mDrawPath.brushThickness = mBrushSize
        mDrawPath.color = mColor
        // mDrawPath.reset() // why should I call this?
    }
    private fun savePathAndPrepareNewPath() {
        mPaths.add(mDrawPath)
        mDrawPath = CustomPath(mColor, mBrushSize)
    }

    internal inner class CustomPath(var color: Int, var brushThickness: Float): Path() {

    }
}