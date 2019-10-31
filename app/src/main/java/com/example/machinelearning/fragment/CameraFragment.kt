package com.example.machinelearning.fragment

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.machinelearning.R
import com.example.machinelearning.permisionUtilities.PermissionCallbacks
import com.example.machinelearning.permisionUtilities.PermissionsUtility
import com.example.machinelearning.utilities.CameraUtility
import com.karumi.dexter.listener.DexterError
import kotlinx.android.synthetic.main.fragment_camera.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CameraFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CameraFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CameraFragment : Fragment() {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private var position: Int? = 0
    private var cameraUtility: CameraUtility? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        arguments?.let {
            position = CameraFragmentArgs.fromBundle(it).position
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPermissions(permissionCallbacks = PermissionCallbacks.Granted {
            // easyImage.runCatching { this }
        })

        cameraUtility = activity?.let {
            fun onCapture(filePath: @ParameterName(name = "filePath") String, rotation: @ParameterName(name = "rotation") Float) {

                val action = when (position) {
                    0 -> CameraFragmentDirections.actionCameraFragmentToMLOCRFragment(filePath, rotation)
                    1 -> CameraFragmentDirections.actionCameraFragmentToMLFaceDetactionFragment()
                    2 -> CameraFragmentDirections.actionCameraFragmentToMLObjectDetactionAndTrackingFragment(filePath, rotation)
                    3 -> CameraFragmentDirections.actionCameraFragmentToMLImageLabelingFragment()
                    4 -> CameraFragmentDirections.actionCameraFragmentToMLAutoMLVisionEdge()
                    5 -> CameraFragmentDirections.actionCameraFragmentToMLBarcodeScanning()
                    6 -> CameraFragmentDirections.actionCameraFragmentToMLLandmarkRecognitionFragment()
                    7 -> CameraFragmentDirections.actionCameraFragmentToMLLanguageIdFragment()
                    8 -> CameraFragmentDirections.actionCameraFragmentToMLOnDeviceTranslationFragment()
                    9 -> CameraFragmentDirections.actionCameraFragmentToMLSmartReplyFragment()
                    else -> CameraFragmentDirections.actionCameraFragmentToMLOCRFragment(filePath, rotation)
                }

                findNavController().navigate(action)

            }
            CameraUtility(it, cameraTextureView, ::onCapture)
        }
        cameraUtility?.startCamera()

        cameraCaptureImageButton.setOnClickListener {
            cameraUtility?.captureImage()
        }
    }


    private fun getPermissions(permissionCallbacks: PermissionCallbacks.Granted) {

        /**
         * For Single permission
         */

        PermissionsUtility(activity, contentView as View?)
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


    override fun onDetach() {
        super.onDetach()
        listener = null
        cameraUtility?.stopCamera()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CameraFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                CameraFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
