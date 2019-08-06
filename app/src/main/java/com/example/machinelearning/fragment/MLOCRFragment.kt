package com.example.machinelearning.fragment

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Rational
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import com.example.machinelearning.R
import com.example.machinelearning.`interface`.OnFragmentInteractionListener
import com.example.machinelearning.permisionUtilities.PermissionCallbacks
import com.example.machinelearning.permisionUtilities.PermissionsUtility
import com.example.machinelearning.utilities.CameraUtility
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.karumi.dexter.listener.DexterError
import kotlinx.android.synthetic.main.fragment_mlocr.*
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.EasyImageConfig
import java.io.File


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MLOCRFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MLOCRFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MLOCRFragment : Fragment() {

    private lateinit var resolution: Size
    private var rotation: Int = 0
    private lateinit var aspectRatio: Rational
    private var lensFacing = CameraX.LensFacing.BACK

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private var easyImage: EasyImage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mlocr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /*easyImage = EasyImage.ImageSource()
                .setChooserTitle("Pick media")
                .setCopyImagesToPublicGalleryFolder(false)
                .setChooserType(EasyImageConfig.REQ_TAKE_PICTURE)
                .setFolderName("EasyImage sample")
                .allowMultiple(true)
                .build()*/

        getPermissions(permissionCallbacks = PermissionCallbacks.Granted {
            easyImage.runCatching { this }
        })

        val cameraUtility = activity?.let {
            fun onCapture(bitmap: @ParameterName(name = "bitmap") Bitmap) {

                val image = FirebaseVisionImage.fromBitmap(bitmap)
                val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

                detector.processImage(image)
                        .addOnSuccessListener { result ->

                            val resultText = result.text
                            Log.e(TAG, " Text $resultText")
                            for (block in result.textBlocks) {
                                val blockText = block.text
                                val blockConfidence = block.confidence
                                val blockLanguages = block.recognizedLanguages
                                val blockCornerPoints = block.cornerPoints
                                val blockFrame = block.boundingBox
                                for (line in block.lines) {
                                    val lineText = line.text
                                    val lineConfidence = line.confidence
                                    val lineLanguages = line.recognizedLanguages
                                    val lineCornerPoints = line.cornerPoints
                                    val lineFrame = line.boundingBox
                                    for (element in line.elements) {
                                        val elementText = element.text
                                        val elementConfidence = element.confidence
                                        val elementLanguages = element.recognizedLanguages
                                        val elementCornerPoints = element.cornerPoints
                                        val elementFrame = element.boundingBox
                                    }
                                }
                            }


                        }
                        .addOnFailureListener {

                        }

            }
            CameraUtility(it, cameraTextureView, ::onCapture)
        }

        cameraCaptureImageButton.setOnClickListener {
            cameraUtility?.captureImage()
        }


    }


    private fun getPermissions(permissionCallbacks: PermissionCallbacks.Granted) {

        /**
         * For Single permission
         */

        PermissionsUtility(activity, contentView)
                .addGrantedPermissionCallbacks { permissionCallbacks.onGranted() }
                .addErrorPermissionCallbacks { this.showError(it) }
                .createSinglePermissionListener(false, activity!!.getString(R.string.storage_permission_required))
                .getSinglePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, true, false)

        /**
         * For multiple permissions
         */
        /*List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        new PermissionsUtility(activity, contentView)
                .addGrantedPermissionCallbacks(this::downlaodFIle)
                .addErrorPermissionCallbacks(this::showError)
                .createAllPermissionsListener()
                .getAllPermissions(permissions);*/
    }

    private fun showError(it: DexterError?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MLOCRFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MLOCRFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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

    fun buildImageCaptureUseCase(): ImageCapture {
        val captureConfig = ImageCaptureConfig.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setTargetRotation(rotation)
                .setTargetResolution(resolution)
                .setFlashMode(FlashMode.OFF)
                .setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
                .build()

        val capture = ImageCapture(captureConfig)
        cameraCaptureImageButton.setOnClickListener {
            // Create temporary file
            val fileName = System.currentTimeMillis().toString()
            val fileFormat = ".jpg"
            val imageFile = createTempFile(fileName, fileFormat)

            // Store captured image in the temporary file
            capture.takePicture(imageFile, object : ImageCapture.OnImageSavedListener {
                override fun onImageSaved(file: File) {
                    // You may display the image for example using its path file.absolutePath
                    Log.e(TAG, "Path " + file.absoluteFile)
                }

                override fun onError(useCaseError: ImageCapture.UseCaseError, message: String, cause: Throwable?) {
                    // Display error message
                    Log.e(TAG, "Path error")
                }
            })
        }

        return capture
    }

}
