package com.example.machinelearning.fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.machinelearning.R
import com.example.machinelearning.`interface`.OnFragmentInteractionListener
import com.example.machinelearning.utilities.CameraUtility
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions
import kotlinx.android.synthetic.main.fragment_mlocr.*
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MLObjectDetactionAndTrackingFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MLObjectDetactionAndTrackingFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MLObjectDetactionAndTrackingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private var filePath: String? = ""
    private var rotation: Float? = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        arguments?.let {
            filePath = MLOCRFragmentArgs.fromBundle(it).filePath
            rotation = 90f
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mlobject_detaction_and_tracking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Multiple object detection in static images
        val options = FirebaseVisionObjectDetectorOptions.Builder()
                .setDetectorMode(FirebaseVisionObjectDetectorOptions.SINGLE_IMAGE_MODE)
                .enableMultipleObjects()
                .enableClassification()  // Optional
                .build()

        val objectDetector = FirebaseVision.getInstance().getOnDeviceObjectDetector(options)

        var bitmap = filePath?.let { CameraUtility.rotateBitmap(it) }

        val image = FirebaseVisionImage.fromBitmap(bitmap!!)

        val canvas = Canvas(bitmap)

        val p = Paint()
        p.style = Paint.Style.STROKE
        p.isAntiAlias = true
        p.isFilterBitmap = true
        p.isDither = true
        p.color = Color.RED

        objectDetector.processImage(image)
                .addOnSuccessListener { detectedObjects ->

                    for (obj in detectedObjects) {
                        val id = obj.trackingId       // A number that identifies the object across images
                        val bounds = obj.boundingBox  // The object's position in the image

                        canvas.drawRect(bounds, p)

                        // If classification was enabled:
                        val category = obj.classificationCategory
                        val confidence = obj.classificationConfidence

                        Log.e(TAG, " Category $category Confidence $confidence")
                    }
                    IVmlocr.setImageBitmap(bitmap)
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    // ...
                }
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
         * @return A new instance of fragment MLObjectDetactionAndTrackingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MLObjectDetactionAndTrackingFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

}
