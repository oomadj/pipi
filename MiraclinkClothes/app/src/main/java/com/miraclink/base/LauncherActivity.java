package com.miraclink.base;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miraclink.R;
import com.miraclink.content.ContentActivity;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class LauncherActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = LauncherActivity.class.getSimpleName();

    private boolean isLogin = false; //TODO login
    private static final int PERMISSION_CODE_ON_CREATE = 632;
    private String[] perms = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        initParam();
        intiView();
    }

    @Override
    protected void initParam() {

    }

    @Override
    protected void intiView() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoginDelay();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (perms.size() == this.perms.length) {
            checkLogin();
        } else if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
        finish();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            Toast.makeText(this, "已更新權限設定", Toast.LENGTH_SHORT).show();
            if (EasyPermissions.hasPermissions(LauncherActivity.this, perms)) {
                checkLogin();
            } else {
                EasyPermissions.requestPermissions(LauncherActivity.this, getString(R.string.permission_rationale), PERMISSION_CODE_ON_CREATE, perms);
            }
        }
    }

    private void checkLoginDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkLogin();
            }
        }, 2000);
    }

    private void checkLogin() {
        if (EasyPermissions.hasPermissions(this, perms)) {
            if (isLogin) {

            } else {
                Intent intent = new Intent(this, ContentActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.permission_rationale),
                    PERMISSION_CODE_ON_CREATE,
                    perms);
        }

    }
}
