package com.example.machinelearning.utilities

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.DisplayMetrics
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.Surface
import android.view.TextureView
import androidx.camera.core.*
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import java.io.File
import java.io.IOException


class CameraUtility(_activity: LifecycleOwner, _cameraTextureView: TextureView, var onCapture: (filePath: String, rotation: Float) -> Unit) {

    private var imageAnalysisUseCase: ImageAnalysis
    private var imageCaptureUseCase: ImageCapture
    private var preview: Preview
    private lateinit var capture: ImageCapture
    private var resolution: Size
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

    fun stopCamera() {
        CameraX.unbind(preview, imageCaptureUseCase, imageAnalysisUseCase)
    }


    fun buildPreviewUseCase(): Preview {
        val previewConfig = PreviewConfig.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setTargetRotation(cameraTextureView.display.rotation)
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
                .setTargetRotation(cameraTextureView.display.rotation)
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

                onCapture(file.absolutePath, FirebaseRotationToDegrees())

                stopCamera()
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
                .setTargetRotation(cameraTextureView.display.rotation)
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
            val mediaImage = image?.image

            val imageRotation = degreesToFirebaseRotation(rotationDegrees)

            val image = FirebaseVisionImage.fromMediaImage(mediaImage!!, imageRotation)
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

    private fun FirebaseRotationToDegrees(): Float = when (cameraTextureView.display.rotation) {

        Surface.ROTATION_0 -> 0f
        Surface.ROTATION_90 -> 90f
        Surface.ROTATION_180 -> 180f
        Surface.ROTATION_270 -> 270f
        else -> throw IllegalStateException()

    }

    companion object {
        fun a(): Int = 1

        fun rotateBitmap(src: String): Bitmap {
            val bitmap = BitmapFactory.decodeFile(src)
            try {
                val exif = ExifInterface(src)
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
                    ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                        matrix.setRotate(180f)
                        matrix.postScale(-1f, 1f)
                    }
                    ExifInterface.ORIENTATION_TRANSPOSE -> {
                        matrix.setRotate(90f)
                        matrix.postScale(-1f, 1f)
                    }
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
                    ExifInterface.ORIENTATION_TRANSVERSE -> {
                        matrix.setRotate(-90f)
                        matrix.postScale(-1f, 1f)
                    }
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
                    ExifInterface.ORIENTATION_NORMAL, ExifInterface.ORIENTATION_UNDEFINED -> return bitmap
                    else -> return bitmap
                }

                try {
                    val oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    bitmap.recycle()
                    return oriented
                } catch (e: OutOfMemoryError) {
                    e.printStackTrace()
                    return bitmap
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }

            return bitmap
        }
    }


}