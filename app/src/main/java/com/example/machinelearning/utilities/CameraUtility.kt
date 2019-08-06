package com.example.machinelearning.utilities

import android.app.Activity
import android.content.ContentValues
import android.graphics.Bitmap
import android.util.DisplayMetrics
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.TextureView
import android.widget.Button
import androidx.camera.core.*
import androidx.lifecycle.LifecycleOwner
import kotlinx.android.synthetic.main.fragment_mlocr.*
import java.io.File
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.graphics.BitmapFactory
import android.view.Surface
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata


class CameraUtility(activity: LifecycleOwner, _cameraTextureView: TextureView, var onCapture: (bitmap: Bitmap) -> Unit) {

    private lateinit var capture: ImageCapture
    private lateinit var resolution: Size
    private var rotation: Int = 0
    private lateinit var aspectRatio: Rational
    private var lensFacing = CameraX.LensFacing.BACK
    private var cameraTextureView: TextureView = _cameraTextureView

    var listener: ((file: File) -> Unit)? = null

    init {
        // Initialize a DisplayMetrics object that receives the TextureView's real display size
        val metrics = DisplayMetrics().also { cameraTextureView.display.getRealMetrics(it) }

        aspectRatio = Rational(metrics.widthPixels, metrics.heightPixels)
        rotation = cameraTextureView.display.rotation
        resolution = Size(metrics.widthPixels, metrics.heightPixels)

        CameraX.bindToLifecycle(
                activity, // Can be an Activity, Fragment or a custom Lifecycle
                buildPreviewUseCase(),
                buildImageCaptureUseCase(),
                buildImageAnalysisUseCase())
    }

    fun buildPreviewUseCase(): Preview {
        val previewConfig = PreviewConfig.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setTargetRotation(rotation)
                .setTargetResolution(resolution)
                .setLensFacing(lensFacing)
                .build()

        val preview = Preview(previewConfig)
        preview.setOnPreviewOutputUpdateListener { previewOutput ->
            cameraTextureView.surfaceTexture = previewOutput.surfaceTexture
            rotation = when (cameraTextureView.display.rotation) {
                Surface.ROTATION_0 -> 0
                Surface.ROTATION_90 -> 90
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_270 -> 270
                else -> throw IllegalStateException()
            }
        }

        return preview
    }

    fun buildImageCaptureUseCase(): ImageCapture {
        val captureConfig = ImageCaptureConfig.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setTargetRotation(rotation)
                .setTargetResolution(resolution)
                .setFlashMode(FlashMode.OFF)
                .setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
                .build()

        capture = ImageCapture(captureConfig)

        return capture
    }

    fun captureImage() {

        // Create temporary file
        val fileName = System.currentTimeMillis().toString()
        val fileFormat = ".jpg"
        val imageFile = createTempFile(fileName, fileFormat)

        // Store captured image in the temporary file
        capture.takePicture(imageFile, object : ImageCapture.OnImageSavedListener {
            override fun onImageSaved(file: File) {
                // You may display the image for example using its path file.absolutePath
                Log.e(ContentValues.TAG, "Path " + file.absoluteFile)
                listener?.invoke(file)

                val bmOptions = BitmapFactory.Options()
                var bitmap = BitmapFactory.decodeFile(file.absolutePath, bmOptions)

                onCapture(bitmap)

            }

            override fun onError(useCaseError: ImageCapture.UseCaseError, message: String, cause: Throwable?) {
                // Display error message
                Log.e(ContentValues.TAG, "Path error")
            }
        })
    }


    fun buildImageAnalysisUseCase(): ImageAnalysis {
        val analysisConfig = ImageAnalysisConfig.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setTargetRotation(rotation)
                .setTargetResolution(resolution)
                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                .build()

        val analysis = ImageAnalysis(analysisConfig)
        analysis.setAnalyzer { image, rotationDegrees ->
            val rect = image.cropRect
            val format = image.format
            val width = image.width
            val height = image.height
            val planes = image.planes
        }

        return analysis
    }

    private fun degreesToFirebaseRotation(degrees: Int): Int = when (degrees) {
        0 -> FirebaseVisionImageMetadata.ROTATION_0
        90 -> FirebaseVisionImageMetadata.ROTATION_90
        180 -> FirebaseVisionImageMetadata.ROTATION_180
        270 -> FirebaseVisionImageMetadata.ROTATION_270
        else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
    }

}