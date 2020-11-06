package com.miraclink.base;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miraclink.R;
import com.miraclink.content.ContentActivity;
import com.miraclink.networks.NetworkUtil;
import com.miraclink.utils.BroadCastAction;
import com.miraclink.utils.LogUtil;
import com.miraclink.utils.Utils;

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

    private BroadcastReceiver receiver;
    private TextView textDown;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        initParam();
        intiView();
        checkVersion();
    }

    @Override
    protected void initParam() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BroadCastAction.CHECK_VERSION_DATA)) {
                    downDialog(context, intent.getStringExtra("content"), intent.getIntExtra("flag", 0),
                            Utils.getVersionCode(context), intent.getIntExtra("version", 0), intent.getStringExtra("url"));
                } else if (intent.getAction().equals(BroadCastAction.DOWN_APK)) {
                    progressBar.setVisibility(View.VISIBLE);
                    textDown.setVisibility(View.VISIBLE);
                    progressBar.setProgress(intent.getIntExtra("progress", 0));
                } else if (intent.getAction().equals(BroadCastAction.DOWN_APK_FINISHED)) {
                    LogUtil.i(TAG, "down apk finished");
                    finish();
                } else if (intent.getAction().equals(BroadCastAction.CHECK_VERSION_FAILED)) {
                    LogUtil.i(TAG, "update_version_failed");
                    if (intent.getIntExtra("update_version_failed", 0) == 1) {
                        Toast.makeText(context, R.string.down_check_failed, Toast.LENGTH_SHORT).show();
                        checkLogin();
                    } else {
                        checkLoginDelay();
                    }
                }
            }
        };
    }

    @Override
    protected void intiView() {
        textDown = findViewById(R.id.textLauncherActivityDowning);
        progressBar = findViewById(R.id.progressLauncherActivity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadCastAction.CHECK_VERSION_DATA);
        filter.addAction(BroadCastAction.DOWN_APK);
        filter.addAction(BroadCastAction.DOWN_APK_FINISHED);
        filter.addAction(BroadCastAction.CHECK_VERSION_FAILED);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void checkVersion() {
        if (NetworkUtil.getConnectivityEnable(this)) {
            networkController.postCheckVersion(Utils.getVersionCode(this));
        } else {
            checkLoginDelay();
        }
    }

    private void checkLoginDelay() {
        LogUtil.i(TAG,"check login delay -"+Utils.getVersionCode(this));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkLogin();
            }
        }, 2000);
    }

    private void checkLogin() {
        LogUtil.i(TAG,"check login -");
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

    public void downDialog(final Context context, String content, final int isEnforce, final int thisVersion, final int updateVersion, final String url) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View v = inflater.inflate(R.layout.dialog_down, null);
        final TextView textTips = v.findViewById(R.id.textDialogDownTips);
        final TextView textThisVersion = v.findViewById(R.id.textThisVersion);
        final TextView textUpdateVersion = v.findViewById(R.id.textUpdateVersion);
        textThisVersion.setText(String.valueOf(thisVersion));
        textUpdateVersion.setText(String.valueOf(updateVersion));
        textTips.setText(content);
        TextView textEnforce = v.findViewById(R.id.textDialogDownEnforce);
        if (isEnforce == 1) {
            textEnforce.setVisibility(View.VISIBLE);
        }
        Button btCancel = v.findViewById(R.id.btDialogDownCancel);
        Button btOk = v.findViewById(R.id.btDialogDownOk);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setView(v);
        final AlertDialog mydialog = dialog.create();
        mydialog.show();
        mydialog.setCancelable(false);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydialog.dismiss();
                if (isEnforce == 1) {
                    finish();
                } else {
                    checkLogin();
                }
            }
        });

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkController.getDownApk(url);
                mydialog.dismiss();
            }
        });
    }

}
