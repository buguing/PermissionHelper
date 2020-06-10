package com.wellee.permissionlib;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.List;

public class PermissionHelper {

    private PermissionHelper() {
    }

    private Object mObject;
    private String[] mPermissions;
    private int mRequestCode;

    public PermissionHelper(Object object) {
        this.mObject = object;
    }

    public static void requestPermission(Activity activity, int requestCode, String... permissions) {
        PermissionHelper.with(activity).requestCode(requestCode).requestPermission(permissions).request();
    }

    public static void requestPermission(Fragment fragment, int requestCode, String... permissions) {
        PermissionHelper.with(fragment).requestCode(requestCode).requestPermission(permissions).request();
    }

    public static PermissionHelper with(Activity activity) {
        return new PermissionHelper(activity);
    }

    public static PermissionHelper with(Fragment fragment) {
        return new PermissionHelper(fragment);
    }

    public PermissionHelper requestPermission(String... permissions) {
        this.mPermissions = permissions;
        return this;
    }

    public PermissionHelper requestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    public void request() {
        if (isBelowVersionM()) {
            PermissionUtils.onRequestSuccess(mObject, mRequestCode);
            return;
        }
        List<String> deniedPermissions = PermissionUtils.getDeniedPermissions(mObject, mPermissions);
        if (deniedPermissions.size() == 0) {
            PermissionUtils.onRequestSuccess(mObject, mRequestCode);
            return;
        }
        ActivityCompat.requestPermissions(PermissionUtils.getActivity(mObject),
                deniedPermissions.toArray(new String[deniedPermissions.size()]), mRequestCode);
    }

    private boolean isBelowVersionM() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void onRequestPermissionsResult(Object object, int requestCode, String[] permissions) {
        List<String> deniedPermissions = PermissionUtils.getDeniedPermissions(object, permissions);

        Activity activity = PermissionUtils.getActivity(object);
        boolean showSettings = false;
        for (String permission : deniedPermissions) {
            boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
            if (!showRationale) {
                showSettings = true;
                break;
            }
        }

        if (deniedPermissions.size() == 0) {
            PermissionUtils.onRequestSuccess(object, requestCode);
        } else {
            if (showSettings) {
                showSettings(object, deniedPermissions, requestCode);
            } else {
                PermissionUtils.onRequestFailure(object, requestCode);
            }
        }
    }

    private static void showSettings(Object object, List<String> deniedPermissions, final int requestCode) {
        final Activity activity = PermissionUtils.getActivity(object);
        new AlertDialog.Builder(activity).setCancelable(false)
                .setTitle("提示")
                .setMessage("请前往设置中允许权限" + deniedPermissions.toString())
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SettingPage setting = new SettingPage(activity);
                        setting.start(requestCode);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(activity, "已取消", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
}
