package com.miraclink.content;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.miraclink.R;
import com.miraclink.base.BaseActivity;
import com.miraclink.bluetooth.MyBlueService;
import com.miraclink.content.check.UserCheckFragment;
import com.miraclink.content.info.UserInfoFragment;
import com.miraclink.content.list.UserListFragment;
import com.miraclink.content.setting.SettingsFragment;
import com.miraclink.database.IUserDatabaseManager;
import com.miraclink.database.UserDatabaseManager;
import com.miraclink.model.User;
import com.miraclink.utils.AppExecutors;
import com.miraclink.utils.BroadCastAction;
import com.miraclink.utils.LogUtil;
import com.miraclink.utils.SharePreUtils;

import java.util.ArrayList;

public class ContentActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = ContentActivity.class.getSimpleName();
    private FrameLayout frameLayout;
    private UserCheckFragment checkFragment;
    private UserListFragment listFragment;
    private UserInfoFragment infoFragment;
    private SettingsFragment settingsFragment;
    private BroadcastReceiver receiver;
    private BluetoothAdapter bluetoothAdapter;

    private ConstraintLayout btList;
    private ConstraintLayout btInfo;
    private ConstraintLayout btCheck;
    private ConstraintLayout btSettings;

    private ImageView ivInfo, ivSetting, ivCheck, ivList;
    private TextView textInfo, textSetting, textCheck, textList;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private int i = 0;  //fragment status

    private ServiceConnection serviceConnection;
    private MyBlueService blueService;
    private Intent bindIntent;
    private ContentContract.Presenter presenter;

    public static int checkStatus = 0;   // 0 stop // 1 starting
    public static String bleAddress;

    public static final int REQUEST_SELECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;
    public static final int UART_PROFILE_CONNECTED = 20;
    public static final int UART_PROFILE_DISCONNECTED = 21;

    public static int mState = UART_PROFILE_DISCONNECTED;

    public boolean isNewBuild = false;

    public ArrayList<String> checkUserIds = new ArrayList<>(); //训练界面用户id集合

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideBottom();
        setContentView(R.layout.activity_content);
        initParam();
        intiView();
        setTabSelection(0, false);
    }

    public MyBlueService getBlueService() {
        return blueService;
    }

    public String getBleAddress() {
        return bleAddress;
    }

    @Override
    protected void initParam() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bindIntent = new Intent(this, MyBlueService.class);
        fragmentManager = getSupportFragmentManager();
        frameLayout = findViewById(R.id.layoutContentActivityContent);
        presenter = new ContentPresenter();
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                blueService = ((MyBlueService.LocalBinder) service).getService();
                if (!blueService.initialize()) {
                    LogUtil.d(TAG, "Unable to initialize Bluetooth");
                    finish();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                checkStatus = 0;
            }
        };
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                {
                    String action = intent.getAction();
                    final Intent mIntent = intent;
                    if (action.equals(BroadCastAction.ACTION_GATT_CONNECTED)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mState = UART_PROFILE_CONNECTED;
                            }
                        });
                    }

                    if (action.equals(BroadCastAction.ACTION_GATT_DISCONNECTED)) {
                        mState = UART_PROFILE_DISCONNECTED;
                        blueService.close();
                    }

                    if (action.equals(BroadCastAction.ACTION_GATT_SERVICES_DISCOVERED)) {
                        blueService.enableTXNotification();
                    }

                    if (action.equals(BroadCastAction.ACTION_DATA_AVAILABLE)) {
                        final byte[] values = intent.getByteArrayExtra(BroadCastAction.EXTRA_DATA);
                    }

                    if (action.equals(BroadCastAction.DEVICE_DOES_NOT_SUPPORT_UART)) {
                        blueService.disconnect();
                    }
                }
            }
        };
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void intiView() {
        btList = findViewById(R.id.btContentActivityUserList);
        btList.setOnClickListener(this);
        btInfo = findViewById(R.id.btContentActivityUserInfo);
        btInfo.setOnClickListener(this);
        btCheck = findViewById(R.id.btContentActivityUserCheck);
        btCheck.setOnClickListener(this);
        btSettings = findViewById(R.id.btContentActivitySettings);
        btSettings.setOnClickListener(this);
        ivInfo = findViewById(R.id.imageContentActivityUserInfo);
        ivSetting = findViewById(R.id.imageContentActivitySettings);
        ivCheck = findViewById(R.id.imageContentActivityUserCheck);
        ivList = findViewById(R.id.imageContentActivityUserList);
        textInfo = findViewById(R.id.textContentActivityUserInfo);
        textSetting = findViewById(R.id.textContentActivityUserSetting);
        textCheck = findViewById(R.id.textContentActivityUserCheck);
        textList = findViewById(R.id.textContentActivityUserList);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", i);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        i = savedInstanceState.getInt("position");
        setTabSelection(i, false);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadCastAction.ACTION_GATT_SERVICES_DISCOVERED);
        filter.addAction(BroadCastAction.ACTION_DATA_AVAILABLE);
        filter.addAction(BroadCastAction.ACTION_GATT_CONNECTED);
        filter.addAction(BroadCastAction.ACTION_GATT_DISCONNECTED);
        filter.addAction(BroadCastAction.DEVICE_DOES_NOT_SUPPORT_UART);
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
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        getPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        blueService.stopSelf();
        blueService = null;
        presenter.onDestroy();
        //remove saved user id
        SharePreUtils.removeCurrentID(this);
    }

    public void setTabSelection(int page, boolean isJump) {
        transaction = fragmentManager.beginTransaction();
        hideFragment(transaction);
        resetImageViewAndTextView();
        //if (infoFragment == null) {
         //   infoFragment = new UserInfoFragment();
         //   transaction.add(R.id.layoutContentActivityContent, infoFragment);
       // }

        switch (page) {
            case 3:
                if (listFragment == null) {
                    listFragment = new UserListFragment();
                    transaction.add(R.id.layoutContentActivityContent, listFragment);
                } else {
                    transaction.show(listFragment);
                }
                ivList.setImageResource(R.drawable.icon_user_list_check);
                textList.setTextColor(getResources().getColor(R.color.check_text_blue));
                break;
            case 0:
                if (infoFragment == null) {
                    infoFragment = new UserInfoFragment();
                    infoFragment.addValues(isJump);
                    transaction.add(R.id.layoutContentActivityContent, infoFragment);
                } else {
                    infoFragment.addValues(isJump);
                    transaction.show(infoFragment);
                }
                ivInfo.setImageResource(R.drawable.icon_user_info_check);
                textInfo.setTextColor(getResources().getColor(R.color.check_text_blue));
                break;
            case 2:
                if (checkFragment == null) {
                    checkFragment = new UserCheckFragment();
                    transaction.add(R.id.layoutContentActivityContent, checkFragment);
                } else {
                    transaction.show(checkFragment);
                }
                ivCheck.setImageResource(R.drawable.icon_user_check_check);
                textCheck.setTextColor(getResources().getColor(R.color.check_text_blue));
                break;
            case 1:
                if (settingsFragment == null) {
                    settingsFragment = new SettingsFragment();
                    transaction.add(R.id.layoutContentActivityContent, settingsFragment);
                } else {
                    transaction.show(settingsFragment);
                }
                ivSetting.setImageResource(R.drawable.icon_setting_check);
                textSetting.setTextColor(getResources().getColor(R.color.check_text_blue));
                break;
            default:
                break;
        }

        transaction.commit();
    }

    private void hideFragment(FragmentTransaction trans) {
        if (listFragment != null) {
            trans.hide(listFragment);
        }
        if (checkFragment != null) {
            trans.hide(checkFragment);
        }
        if (infoFragment != null) {
            trans.hide(infoFragment);
        }
        if (settingsFragment != null) {
            trans.hide(settingsFragment);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btContentActivityUserList:
                sendBroadcast(new Intent(BroadCastAction.USER_CHANGED));  //refresh page data
                setTabSelection(3, false);
                break;
            case R.id.btContentActivityUserInfo:
                sendBroadcast(new Intent(BroadCastAction.USER_CHANGED));  //refresh page data
                setTabSelection(0, false);
                break;
            case R.id.btContentActivityUserCheck:
                sendBroadcast(new Intent(BroadCastAction.USER_CHANGED));  //refresh page data
                setTabSelection(2, false);
                break;
            case R.id.btContentActivitySettings:
                sendBroadcast(new Intent(BroadCastAction.USER_CHANGED));  //refresh page data
                setTabSelection(1, false);
                break;
            default:
                break;
        }
    }

    private void hideBottom() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        window.setAttributes(params);
    }

    private void resetImageViewAndTextView() {
        ivInfo.setImageResource(R.drawable.icon_user_info);
        ivSetting.setImageResource(R.drawable.icon_setting);
        ivCheck.setImageResource(R.drawable.icon_user_check);
        ivList.setImageResource(R.drawable.icon_user_list);
        textInfo.setTextColor(getResources().getColor(R.color.bottom_text_color));
        textSetting.setTextColor(getResources().getColor(R.color.bottom_text_color));
        textCheck.setTextColor(getResources().getColor(R.color.bottom_text_color));
        textList.setTextColor(getResources().getColor(R.color.bottom_text_color));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SELECT_DEVICE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String address = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    LogUtil.i(TAG, "on activity result address:" + address);
                    blueService.connect(address);
                    bleAddress = address;
                }
                break;
            default:
                LogUtil.e(TAG, "wrong request code");
                break;
        }
    }

    private final int ACCESS_LOCATION = 1;

    private void getPermission() {
        LogUtil.i(TAG, "sdk version:" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = 0;
            permissionCheck = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionCheck += this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                //未获得权限
                this.requestPermissions( // 请求授权
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        ACCESS_LOCATION);// 自定义常量,任意整型
            }
        }
    }

    private boolean hasAllPermissionGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case ACCESS_LOCATION:
                if (hasAllPermissionGranted(grantResults)) {
                    Toast.makeText(this, "onRequestPermissions success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "onRequestPermissions failed", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
