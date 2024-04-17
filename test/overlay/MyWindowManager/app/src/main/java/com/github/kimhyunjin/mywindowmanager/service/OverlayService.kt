package com.github.kimhyunjin.mywindowmanager.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.github.kimhyunjin.mywindowmanager.R

class OverlayService: Service(), OnTouchListener {
    private lateinit var windowManager: WindowManager
    private lateinit var params: WindowManager.LayoutParams
    private var initialX: Int = 0;
    private var initialY: Int = 0;
    private var initialTouchX: Float = 0f;
    private var initialTouchY: Float = 0f;
    private var overlayView: View? = null;

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.START or Gravity.TOP
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showOverlay()
        return START_STICKY
    }

    private fun showOverlay() {
        // 오버레이 뷰 생성
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as? LayoutInflater
        overlayView = inflater?.inflate(R.layout.content_main, null);
        val webView = overlayView?.findViewById<WebView>(R.id.webview)
        webView?.let {
            it.webViewClient = WebViewClient()
            it.loadUrl("https://google.com")
            it.settings.javaScriptEnabled = true
        }

        // 오버레이 뷰를 윈도우 매니저에 추가
        windowManager.addView(overlayView, params)

        overlayView?.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        Log.i("onTouch", "${event?.x} ${event?.y}")
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = params.x
                initialY = params.y
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                params.x = initialX + (event.rawX - initialTouchX).toInt()
                params.y = initialY + (event.rawY - initialTouchY).toInt()
                windowManager.updateViewLayout(overlayView, params)
                return true
            }

            MotionEvent.ACTION_UP -> {
                v?.performClick()
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (overlayView != null) {
            val webView = overlayView!!.findViewById<WebView>(R.id.webview)
            webView.destroy()
            windowManager.removeView(overlayView);
            overlayView = null
        }
    }
}