package com.example.machinelearning.permisionUtilities;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.example.machinelearning.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;

import java.util.List;

public class PermissionsUtility {

    private CompositeMultiplePermissionsListener allPermissionsListener;
    private CompositePermissionListener compositeSinglePermissionListenerWithSnackbar;
    private CompositePermissionListener compositeSinglePermissionListenerWithDialog;
    private MPermissionErrorListner errorListener;
    private PermissionRationnaleUtility permissionRationnaleUtility;

    private PermissionCallbacks.Granted grantedPermissionCallbacks;
    private PermissionCallbacks.Error errorPermissionCallback;

    private Activity activity;
    private View rootView;

    public PermissionsUtility(Activity activity, View rootView, PermissionCallbacks.Granted permissionCallbacks, PermissionCallbacks.Error error) {
        this.activity = activity;
        this.rootView = rootView;
        this.grantedPermissionCallbacks = permissionCallbacks;
        this.errorPermissionCallback = error;
    }

    public PermissionsUtility(Activity activity, View rootView) {
        this.activity = activity;
        this.rootView = rootView;
    }

    public PermissionsUtility addGrantedPermissionCallbacks(PermissionCallbacks.Granted permissionCallbacks) {
        this.grantedPermissionCallbacks = permissionCallbacks;
        return this;
    }

    public PermissionsUtility addErrorPermissionCallbacks(PermissionCallbacks.Error error) {
        this.errorPermissionCallback = error;
        return this;
    }

    public PermissionsUtility getAllPermissions(List<String> permissions) {

        Dexter.withActivity(activity)
                .withPermissions(permissions)
                .withListener(allPermissionsListener)
                .withErrorListener(errorListener)
                .check();

        return this;
    }

    public PermissionsUtility getSinglePermission(String permission, boolean shouldDisplaySnackbar, boolean shouldDisplayDialog) {

        if (shouldDisplayDialog) {

            Dexter.withActivity(activity)
                    .withPermission(permission)
                    .withListener(compositeSinglePermissionListenerWithDialog)
                    .withErrorListener(errorListener)
                    .check();

        } else if (shouldDisplaySnackbar) {

            Dexter.withActivity(activity)
                    .withPermission(permission)
                    .withListener(compositeSinglePermissionListenerWithSnackbar)
                    .withErrorListener(errorListener)
                    .check();

        } else {

            Dexter.withActivity(activity)
                    .withPermissions(permission)
                    .withListener(allPermissionsListener)
                    .withErrorListener(errorListener)
                    .check();

        }

        return this;
    }


    public PermissionsUtility createSinglePermissionListener(boolean shouldShowRationale, String permissionMessage) {

        permissionRationnaleUtility = new PermissionRationnaleUtility(activity);

        /**
         * Single permission listener
         */
        PermissionListener mSinglePermissionListner = new MSinglePermissionListner(activity, new PermissionCallbacks() {
            @Override
            public void onGranted(String permissionName) {
                grantedPermissionCallbacks.onGranted();
            }

            @Override
            public void onDeneid(String permissionName, boolean permanentlyDenied) {

            }

            @Override
            public void onError() {

            }

            @Override
            public void showPermissionRationale(PermissionToken token) {
                if (shouldShowRationale) {
                    permissionRationnaleUtility.showPermissionRationale(token);
                }
            }
        });

        /**
         * Snackbar to display at bottom on permission denial
         */
        SnackbarOnDeniedPermissionListener snackbarOnDeniedPermissionListener = SnackbarOnDeniedPermissionListener.Builder.with(rootView,
                permissionMessage)
                .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                .withCallback(new Snackbar.Callback() {
                    @Override
                    public void onShown(Snackbar snackbar) {
                        super.onShown(snackbar);
                    }

                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                    }
                })
                .build();

        compositeSinglePermissionListenerWithSnackbar = new CompositePermissionListener(mSinglePermissionListner, snackbarOnDeniedPermissionListener);


        /**
         * Dialog to display on permission denial
         */
        DialogOnDeniedPermissionListener dialogOnDeniedPermissionListener = DialogOnDeniedPermissionListener.Builder.withContext(activity)
                .withTitle(R.string.permission_needed_titile)
                .withMessage(permissionMessage)
                .withButtonText(android.R.string.ok)
                .build();

        compositeSinglePermissionListenerWithDialog = new CompositePermissionListener(mSinglePermissionListner, dialogOnDeniedPermissionListener);


        return this;
    }

    public PermissionsUtility createAllPermissionsListener() {


        permissionRationnaleUtility = new PermissionRationnaleUtility(activity);

        /**
         * Multiple permission listener
         */
        MultiplePermissionsListener mMultiplePermissionListner = new MMultiplePermissionListner(activity, new PermissionCallbacks() {
            @Override
            public void onGranted(String permissionName) {
                grantedPermissionCallbacks.onGranted();
            }

            @Override
            public void onDeneid(String permissionName, boolean permanentlyDenied) {
                //grantedPermissionCallbacks.onDeneid(permissionName, permanentlyDenied);
            }

            @Override
            public void onError() {

            }

            @Override
            public void showPermissionRationale(PermissionToken token) {
                //permissionRationnaleUtility.showPermissionRationale(token);
            }
        });

        SnackbarOnAnyDeniedMultiplePermissionsListener allPermissionSnackBarBuilder = SnackbarOnAnyDeniedMultiplePermissionsListener.Builder
                .with(rootView, R.string.all_permissions_denied_feedback)
                .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                .build();

        allPermissionsListener = new CompositeMultiplePermissionsListener(mMultiplePermissionListner, allPermissionSnackBarBuilder);


        /**
         * Error lisetner
         */
        errorListener = new MPermissionErrorListner(error -> {
            errorPermissionCallback.onError(error);
        });

        return this;
    }
}
