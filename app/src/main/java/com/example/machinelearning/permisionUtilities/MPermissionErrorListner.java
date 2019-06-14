package com.example.machinelearning.permisionUtilities;

import android.util.Log;

import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;

public class MPermissionErrorListner implements PermissionRequestErrorListener {

    private PermissionCallbacks.Error permissionError;

    public MPermissionErrorListner(PermissionCallbacks.Error error) {
        this.permissionError = error;
    }

    @Override
    public void onError(DexterError error) {
        Log.e("Dexter", "There was an permissionError: " + error.toString());
        permissionError.onError(error);
    }
}

