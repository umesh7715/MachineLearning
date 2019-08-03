package com.example.machinelearning.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.machinelearning.R
import com.example.machinelearning.`interface`.OnFragmentInteractionListener
import com.example.machinelearning.adapter.ListAdapter
import com.example.machinelearning.model.MLFunctionality
import kotlinx.android.synthetic.main.fragment_main.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MainFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MainFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private val mlFunctionlityList = listOf(
            MLFunctionality("Text recognition", 1),
            MLFunctionality("Face detection", 2),
            MLFunctionality("Object detection and tracking", 3),
            MLFunctionality("Image labeling", 4),
            MLFunctionality("AutoML Vision Edge", 5),
            MLFunctionality("Barcode scanning", 6),
            MLFunctionality("Landmark recognition", 7),
            MLFunctionality("Language ID", 8),
            MLFunctionality("On-device translation", 9),
            MLFunctionality("Smart Reply", 10)
    )

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
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ListAdapter(mlFunctionlityList) { mlFunctionality: MLFunctionality, position: Int ->
                Log.e("MyActivity", "Clicked on item  ${mlFunctionality.title} at position $position")

                val action = when (position) {
                    0 -> MainFragmentDirections.actionMainFragmentToMLOCRFragment2()
                    1 -> MainFragmentDirections.actionMainFragmentToMLFaceDetactionFragment()
                    2 -> MainFragmentDirections.actionMainFragmentToMLObjectDetactionAndTrackingFragment()
                    3 -> MainFragmentDirections.actionMainFragmentToMLImageLabelingFragment()
                    4 -> MainFragmentDirections.actionMainFragmentToMLAutoMLVisionEdge()
                    5 -> MainFragmentDirections.actionMainFragmentToMLBarcodeScanning()
                    6 -> MainFragmentDirections.actionMainFragmentToMLLandmarkRecognitionFragment()
                    7 -> MainFragmentDirections.actionMainFragmentToMLLanguageIdFragment()
                    8 -> MainFragmentDirections.actionMainFragmentToMLOnDeviceTranslationFragment()
                    9 -> MainFragmentDirections.actionMainFragmentToMLSmartReplyFragment()
                    else -> MainFragmentDirections.actionMainFragmentToMLOCRFragment2()
                }

                findNavController().navigate(action)
            }
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
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MainFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
