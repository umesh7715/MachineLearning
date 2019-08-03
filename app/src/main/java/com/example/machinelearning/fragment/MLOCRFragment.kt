package com.example.machinelearning.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.machinelearning.R
import com.example.machinelearning.`interface`.OnFragmentInteractionListener
import com.example.machinelearning.permisionUtilities.PermissionCallbacks
import com.example.machinelearning.permisionUtilities.PermissionsUtility
import com.karumi.dexter.listener.DexterError
import kotlinx.android.synthetic.main.fragment_mlocr.*
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.EasyImageConfig






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
}
