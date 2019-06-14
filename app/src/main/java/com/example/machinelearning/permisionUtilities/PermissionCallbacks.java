package com.example.machinelearning.permisionUtilities;

import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;

public interface PermissionCallbacks {

    public void onGranted(String permissionName);

    public void onDeneid(String permissionName, boolean permanentlyDenied);

    public void onError();

    void showPermissionRationale(PermissionToken token);

    public interface Granted{
        public void onGranted();
    }

    public interface Error{
        public void onError(DexterError error);
    }

}
