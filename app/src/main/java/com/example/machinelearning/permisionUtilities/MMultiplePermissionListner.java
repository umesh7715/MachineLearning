package com.example.machinelearning.permisionUtilities;

import android.app.Activity;

import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MMultiplePermissionListner implements MultiplePermissionsListener {

    private final Activity activity;
    private PermissionRationnaleUtility permissionRationnaleUtility;
    private PermissionCallbacks permissionCallbacks;

    public MMultiplePermissionListner(Activity activity, PermissionCallbacks permissionCallbacks) {
        this.activity = activity;
        this.permissionRationnaleUtility=permissionRationnaleUtility;
        this.permissionCallbacks=permissionCallbacks;
    }

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport report) {
        for (PermissionGrantedResponse response : report.getGrantedPermissionResponses()) {
            permissionCallbacks.onGranted(response.getPermissionName());
        }

        for (PermissionDeniedResponse response : report.getDeniedPermissionResponses()) {
            permissionCallbacks.onDeneid(response.getPermissionName(), response.isPermanentlyDenied());
        }
    }

    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
        permissionCallbacks.showPermissionRationale(token);
    }
}