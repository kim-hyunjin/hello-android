package com.github.kimhyunjin.face_recognition_lib.recognition

import android.media.Image
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.lifecycle.Lifecycle
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

internal class FaceAnalyzer(
    lifecycle: Lifecycle,
    private val previewView: PreviewView,
    private val listener: FaceAnalyzerListener?
) : ImageAnalysis.Analyzer {

    private var widthScaleFactor = 1f
    private var heightScaleFactor = 1f

    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL) // 윤곽선
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL) // 표정도 가져올 수 있도록 설정
        .setMinFaceSize(0.4f)
        .build()

    private val detector = FaceDetection.getClient(options)

    private val successListener = OnSuccessListener<List<Face>> { faces -> }

    private val failureListener = OnFailureListener { e -> }

    override fun analyze(image: ImageProxy) {
        widthScaleFactor = previewView.width.toFloat() / image.width
        heightScaleFactor = previewView.height.toFloat() / image.height
        detectFace(image)
    }

    @OptIn(ExperimentalGetImage::class)
    private fun detectFace(imageProxy: ImageProxy) {
        val image = InputImage.fromMediaImage(
            imageProxy.image as Image,
            imageProxy.imageInfo.rotationDegrees
        )
        detector.process(image).addOnSuccessListener(successListener)
            .addOnFailureListener(failureListener).addOnCompleteListener {
                imageProxy.close()
            }
    }
}