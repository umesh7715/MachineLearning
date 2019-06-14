package com.example.machinelearning.permisionUtilities;

import android.app.Activity;

import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MSinglePermissionListner implements PermissionListener {

    private final Activity activity;
    PermissionRationnaleUtility permissionRationnaleUtility;
    public PermissionCallbacks permissionCallbacks;

    public MSinglePermissionListner(Activity activity, PermissionCallbacks permissionCallbacks) {
        this.activity = activity;
        this.permissionRationnaleUtility = permissionRationnaleUtility;
        this.permissionCallbacks=permissionCallbacks;
    }

    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
       permissionCallbacks.onGranted(response.getPermissionName());
    }

    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
        permissionCallbacks.onDeneid(response.getPermissionName(), response.isPermanentlyDenied());
    }

    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
        //activity.showPermissionRationale(token);
        permissionCallbacks.showPermissionRationale(token);
    }
}