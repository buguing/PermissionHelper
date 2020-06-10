package com.wellee.permissionlib;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @PermissionGranted(requestCode = 1000)
    public void permissionGrant() {
        Toast.makeText(this, "permissionGrant", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied(requestCode = 1000)
    public void permissionDenied() {
        Toast.makeText(this, "permissionDenied", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.onRequestPermissionsResult(this, requestCode, permissions);
    }

    public void onClick(View view) {
        PermissionHelper.with(this).requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).requestCode(1000).request();
    }
}
