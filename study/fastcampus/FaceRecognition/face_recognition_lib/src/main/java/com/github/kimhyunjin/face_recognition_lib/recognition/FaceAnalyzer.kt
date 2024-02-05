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

    private var detectStatus = FaceAnalyzerStatus.UnDetect

    private val successListener = OnSuccessListener<List<Face>> { faces ->
        val face = faces.firstOrNull()
        // 얼굴 인식 -> 왼쪽 윙크 -> 오른쪽 윙크 -> 스마일 -> 종료
        if (face != null) {
            if (detectStatus == FaceAnalyzerStatus.UnDetect) {
                detectStatus = FaceAnalyzerStatus.Detect
                listener?.detect()
                listener?.detectProgress(25f, "얼굴을 인식했습니다. \n왼쪽 눈만 깜빡여주세요.")
            } else if (detectStatus == FaceAnalyzerStatus.Detect && (face.leftEyeOpenProbability
                    ?: 0f) > EYE_SUCCESS_VALUE && (face.rightEyeOpenProbability
                    ?: 0f) < EYE_SUCCESS_VALUE
            ) {
                detectStatus = FaceAnalyzerStatus.LeftWink
                listener?.detectProgress(50f, "오른쪽 눈만 깜빡여주세요.")
            } else if (detectStatus == FaceAnalyzerStatus.LeftWink && (face.rightEyeOpenProbability
                    ?: 0f) > EYE_SUCCESS_VALUE && (face.leftEyeOpenProbability
                    ?: 0f) < EYE_SUCCESS_VALUE
            ) {
                detectStatus = FaceAnalyzerStatus.RightWink
                listener?.detectProgress(75f, "활짝 웃어보세요.")
            } else if (detectStatus == FaceAnalyzerStatus.RightWink && (face.smilingProbability ?: 0f) > SMILE_SUCCESS_VALUE) {
                detectStatus = FaceAnalyzerStatus.Smile
                listener?.detectProgress(100f, "얼굴 인식이 완료되었습니다.")
                listener?.stopDetect()
                detector.close()
            }
            // 중간단계에서 얼굴인식 실패시 처음단계로 돌아간다.
        } else if (detectStatus != FaceAnalyzerStatus.UnDetect && detectStatus != FaceAnalyzerStatus.Smile) {
            detectStatus = FaceAnalyzerStatus.UnDetect
            listener?.notDetect()
            listener?.detectProgress(0f, "얼굴을 인식하지 못했습니다. \n처음 단계로 돌아갑니다.")
        }
    }

    private val failureListener = OnFailureListener { e ->
        detectStatus = FaceAnalyzerStatus.UnDetect
    }

    init {
        lifecycle.addObserver(detector)
    }

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

    companion object {
        private const val EYE_SUCCESS_VALUE = 0.1f
        private const val SMILE_SUCCESS_VALUE = 0.8f
    }
}