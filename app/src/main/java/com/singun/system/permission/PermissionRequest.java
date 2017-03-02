package com.singun.system.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by singun on 2017/3/1 0001.
 */

public class PermissionRequest {
    private Activity mActivity;
    private String[] mPermissions;
    private int mRequestCode;

    private PermissionRequestListener mListener;

    public PermissionRequest(Activity activity, String[] permissions, int requestCode) {
        mActivity = activity;
        mPermissions = permissions;
        mRequestCode = requestCode;
    }

    public void setPermissionRequestListener(PermissionRequestListener listener) {
        mListener = listener;
    }

    public void checkPermission() {
        String[] permissionArr = getNeedRequestPermissions();
        if (permissionArr != null) {
            ActivityCompat.requestPermissions(mActivity,
                    permissionArr,
                    mRequestCode);
        } else {
            if (mListener != null) {
                mListener.onPermissionAllGranted();
            }
        }
    }

    private String[] getNeedRequestPermissions() {
        List<String> needRequestList = null;
        for (String permission : mPermissions) {
            if (ContextCompat.checkSelfPermission(mActivity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                if (needRequestList == null) {
                    needRequestList = new ArrayList<>();
                }
                needRequestList.add(permission);
            }
        }
        if (needRequestList == null || needRequestList.isEmpty()) {
            return null;
        }
        String[] permissionArr = new String[needRequestList.size()];
        return needRequestList.toArray(permissionArr);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode != mRequestCode || mListener == null) {
            return;
        }
        if (grantResults.length > 0) {
            int grantedCount = 0;
            for (int i= 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    mListener.onPermissionGranted(permissions[i]);
                    grantedCount++;
                } else {
                    mListener.onPermissionDenied(permissions[i]);
                }
            }
            if (grantedCount == permissions.length) {
                mListener.onPermissionAllGranted();
            } else if (grantedCount == 0) {
                mListener.onPermissionAllDenied();
            } else {
                mListener.onPermissionPartDenied();
            }
        } else {
            for (String permission : permissions) {
                mListener.onPermissionDenied(permission);
            }
            mListener.onPermissionAllDenied();
        }
    }

    public interface PermissionRequestListener {
        void onPermissionAllGranted();
        void onPermissionAllDenied();
        void onPermissionPartDenied();
        void onPermissionGranted(String permission);
        void onPermissionDenied(String permission);
    }
}
