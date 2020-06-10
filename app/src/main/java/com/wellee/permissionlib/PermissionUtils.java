package com.wellee.permissionlib;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PermissionUtils {

    public static void onRequestSuccess(Object object, int requestCode) {
        Method[] methods = object.getClass().getDeclaredMethods();
        for (Method method : methods) {
            PermissionGranted permissionGranted = method.getAnnotation(PermissionGranted.class);
            if (permissionGranted != null) {
                int code = permissionGranted.requestCode();
                if (code == requestCode) {
                    invokeMethod(object, method);
                }
            }
        }
    }

    public static void onRequestFailure(Object object, int requestCode) {
        Method[] methods = object.getClass().getDeclaredMethods();
        for (Method method : methods) {
            PermissionDenied permissionDenied = method.getAnnotation(PermissionDenied.class);
            if (permissionDenied != null) {
                int code = permissionDenied.requestCode();
                if (code == requestCode) {
                    invokeMethod(object, method);
                }
            }
        }
    }

    private static void invokeMethod(Object object, Method method) {
        try {
            method.setAccessible(true);
            method.invoke(object, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static List<String> getDeniedPermissions(Object object, String... permissions) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            Activity activity = Objects.requireNonNull(getActivity(object));
            int result = ContextCompat.checkSelfPermission(activity, permission);
            if (result == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }

    public static Activity getActivity(Object object) {
        if (object instanceof Activity) {
            return (Activity) object;
        }
        if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        }
        return null;
    }

}
