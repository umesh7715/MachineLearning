package com.example.machinelearning.utilities

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.DisplayMetrics
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.Surface
import android.view.TextureView
import androidx.camera.core.*
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import java.io.File


class CameraUtility(_activity: LifecycleOwner, _cameraTextureView: TextureView, var onCapture: (bitmap: Bitmap) -> Unit) {

    private var imageAnalysisUseCase: ImageAnalysis
    private var imageCaptureUseCase: ImageCapture
    private var preview: Preview
    private lateinit var capture: ImageCapture
    private var resolution: Size
    private var rotation: Int = 0
    private var aspectRatio: Rational
    private var lensFacing = CameraX.LensFacing.BACK
    private var cameraTextureView: TextureView = _cameraTextureView
    private var activity: LifecycleOwner = _activity;

    var listener: ((file: File) -> Unit)? = null

    init {
        // Initialize a DisplayMetrics object that receives the TextureView's real display size
        val metrics = DisplayMetrics().also { cameraTextureView.display.getRealMetrics(it) }

        aspectRatio = Rational(metrics.widthPixels, metrics.heightPixels)
        resolution = Size(metrics.widthPixels, metrics.heightPixels)

        preview = buildPreviewUseCase()
        imageCaptureUseCase = buildImageCaptureUseCase()
        imageAnalysisUseCase = buildImageAnalysisUseCase()


    }

    fun startCamera() {

        CameraX.bindToLifecycle(
                activity, // Can be an Activity, Fragment or a custom Lifecycle
                preview, imageCaptureUseCase, imageAnalysisUseCase)
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

                bitmap = rotateImage(bitmap)

                onCapture(bitmap)

                cameraTextureView.lockCanvas()

               // CameraX.unbind(preview)
            }

            override fun onError(useCaseError: ImageCapture.UseCaseError, message: String, cause: Throwable?) {
                // Display error message
                Log.e(ContentValues.TAG, "Path error")
            }
        })

        capture.takePicture(object : ImageCapture.OnImageCapturedListener() {
            override fun onCaptureSuccess(image: ImageProxy, rotationDegrees: Int) {
                // Handle image captured
            }

            override fun onError(useCaseError: ImageCapture.UseCaseError?, message: String?, cause: Throwable?) {
                // Handle image capture error
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

    fun rotateImage(source: Bitmap): Bitmap {
        val matrix = Matrix()

        var rotationFloat = when (rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> throw IllegalStateException()
        }

        matrix.postRotate(rotationFloat.toFloat())
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height,
                matrix, true)
    }

}