package com.miraclink.content;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.miraclink.utils.AppExecutors;
import com.miraclink.utils.BroadCastAction;
import com.miraclink.utils.LogUtil;
import com.miraclink.utils.SharePreUtils;

public class ContentActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = ContentActivity.class.getSimpleName();
    private FrameLayout frameLayout;
    private UserCheckFragment checkFragment;
    private UserListFragment listFragment;
    private UserInfoFragment infoFragment;
    private SettingsFragment settingsFragment;
    private BroadcastReceiver receiver;
    private BluetoothAdapter bluetoothAdapter;
    private IUserDatabaseManager iUserDatabaseManager;

    private Button btList;
    private Button btInfo;
    private Button btCheck;
    private Button btSettings;

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

    public int ioRate = 1; // io 功率
    public int strong = 1; //strong

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        initParam();
        intiView();
        setTabSelection(0);
    }

    public MyBlueService getBlueService() {
        return blueService;
    }

    @Override
    protected void initParam() {
        iUserDatabaseManager = UserDatabaseManager.getInstance(this, AppExecutors.getInstance());
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
        btInfo = findViewById(R.id.btbtContentActivityUserInfo);
        btInfo.setOnClickListener(this);
        btCheck = findViewById(R.id.btContentActivityUserCheck);
        btCheck.setOnClickListener(this);
        btSettings = findViewById(R.id.btContentActivitySettings);
        btSettings.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", i);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        i = savedInstanceState.getInt("position");
        setTabSelection(i);
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
    protected void onResume() {
        super.onResume();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        blueService.stopSelf();
        blueService = null;
        unregisterReceiver(receiver);
        presenter.onDestroy();
        //remove saved user id
        SharePreUtils.removeCurrentID(this);
    }

    private void setTabSelection(int page) {
        transaction = fragmentManager.beginTransaction();
        hideFragment(transaction);
        if (listFragment == null) {
            listFragment = new UserListFragment();
            transaction.add(R.id.layoutContentActivityContent, listFragment);
        }

        switch (page) {
            case 0:
                if (listFragment == null) {
                    listFragment = new UserListFragment();
                } else {
                    transaction.show(listFragment);
                }
                break;
            case 1:
                if (infoFragment == null) {
                    infoFragment = new UserInfoFragment();
                    transaction.add(R.id.layoutContentActivityContent, infoFragment);
                } else {
                    transaction.show(infoFragment);
                }
                break;
            case 2:
                if (checkFragment == null) {
                    checkFragment = new UserCheckFragment();
                    transaction.add(R.id.layoutContentActivityContent, checkFragment);
                } else {
                    transaction.show(checkFragment);
                }
                break;
            case 3:
                if (settingsFragment == null) {
                    settingsFragment = new SettingsFragment();
                    transaction.add(R.id.layoutContentActivityContent, settingsFragment);
                } else {
                    transaction.show(settingsFragment);
                }
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
                setTabSelection(0);
                break;
            case R.id.btbtContentActivityUserInfo:
                sendBroadcast(new Intent(BroadCastAction.USER_CHANGED));  //refresh page data
                setTabSelection(1);
                break;
            case R.id.btContentActivityUserCheck:
                sendBroadcast(new Intent(BroadCastAction.USER_CHANGED));  //refresh page data
                setTabSelection(2);
                break;
            case R.id.btContentActivitySettings:
                sendBroadcast(new Intent(BroadCastAction.USER_CHANGED));  //refresh page data
                setTabSelection(3);
                break;
            default:
                break;
        }
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


}
