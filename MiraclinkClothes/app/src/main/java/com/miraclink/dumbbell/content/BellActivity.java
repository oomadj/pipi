package com.miraclink.dumbbell.content;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.miraclink.R;
import com.miraclink.base.BaseActivity;
import com.miraclink.bluetooth.MyBlueService;
import com.miraclink.dumbbell.content.course.CourseFragment;
import com.miraclink.dumbbell.content.homepage.HomeFragment;
import com.miraclink.dumbbell.content.mine.MineFragment;
import com.miraclink.utils.BroadCastAction;
import com.miraclink.utils.LogUtil;

public class BellActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = BellActivity.class.getSimpleName();
    private ImageView ivHome, ivCourse, ivMine;
    private TextView tvHome, tvCourse, tvMine;
    private LinearLayout layoutHome, layoutCourse, layoutMine;
    private HomeFragment homeFragment;
    private CourseFragment courseFragment;
    private MineFragment mineFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private BroadcastReceiver receiver;
    private BluetoothAdapter bluetoothAdapter;
    private ServiceConnection serviceConnection;
    private MyBlueService blueService;
    private Intent bindIntent;

    public static final int REQUEST_SELECT_DEVICE = 10;
    public static final int REQUEST_ENABLE_BT = 2;

    private boolean isCount=false;
    private short prex,prey,prez;
    public short totalCounts = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bell_activity_main);
        initParam();
        intiView();
        setTabSelect(0);
    }

    @Override
    protected void initParam() {
        fragmentManager = getSupportFragmentManager();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bindIntent = new Intent(this, MyBlueService.class);
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

            }
        };
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BroadCastAction.ACTION_GATT_CONNECTED)) {

                }

                if (action.equals(BroadCastAction.ACTION_GATT_DISCONNECTED)) {
                    blueService.close();
                }

                if (action.equals(BroadCastAction.ACTION_GATT_SERVICES_DISCOVERED)) {
                    blueService.enableTXNotification();
                }

                if (action.equals(BroadCastAction.ACTION_DATA_AVAILABLE)) {
                    final byte[] txValue = intent.getByteArrayExtra(BroadCastAction.EXTRA_DATA);
                    int size = 0;
                    size = txValue.length;
                    if (size > 5) {
                        short x = getXYZValue(txValue[2], txValue[3]);
                        short y = getXYZValue(txValue[4], txValue[5]);
                        short z = getXYZValue(txValue[6], txValue[7]);

                        if (isCount) {
                            short dx = (short) Math.pow(Math.abs(x - prex), 2);
                            short dy = (short) Math.pow(Math.abs(y - prey), 2);
                            short dz = (short) Math.pow(Math.abs(z - prez), 2);
                            short distance = (short) Math.sqrt(dx + dy + dz);
                            isCount = false;
                            if (distance >= 165) {
                                totalCounts = (short) (totalCounts + 1);
                                homeFragment.tvCount.setText(totalCounts + "");
                            }
                        }
                        if (!isCount) {
                            prex = x;
                            prey = y;
                            prez = z;
                            isCount = true;
                        }
                    }
                    if (size == 3) {
                        String showKeySuccess = byteToHex(txValue[2]);
                        homeFragment.tvKey.setText(showKeySuccess);
                    }
                }

                if (action.equals(BroadCastAction.DEVICE_DOES_NOT_SUPPORT_UART)) {
                    blueService.disconnect();
                }
            }
        };
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void intiView() {
        ivHome = findViewById(R.id.imgBellActivityHome);
        ivCourse = findViewById(R.id.imgBellActivityCourse);
        ivMine = findViewById(R.id.imgBellActivityMine);
        tvHome = findViewById(R.id.textBellActivityHome);
        tvCourse = findViewById(R.id.textBellActivityCourse);
        tvMine = findViewById(R.id.textBellActivityMine);
        layoutHome = findViewById(R.id.layoutHome);
        layoutCourse = findViewById(R.id.layoutCourse);
        layoutMine = findViewById(R.id.layoutMine);
        layoutHome.setOnClickListener(this);
        layoutMine.setOnClickListener(this);
        layoutCourse.setOnClickListener(this);
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
        getPermission();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        blueService.stopSelf();
        blueService = null;
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
                }
                break;
            default:
                LogUtil.e(TAG, "wrong request code");
                break;
        }
    }

    public static String byteToHex(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    public static short getXYZValue(byte high, byte low) {

        short absb = (short) Math.abs(high);
        if (high < 0) {
            absb = (short) (absb | 0x80);
        }
        return (short) (absb << 8 | low & 0xff);

    }

    private void setTabSelect(int i) {
        transaction = fragmentManager.beginTransaction();
        hideFragments();
        resetImageViewAndTextView();
        switch (i) {
            case 0:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    transaction.add(R.id.layoutBellActivity, homeFragment);
                }
                transaction.show(homeFragment);
                ivHome.setImageResource(R.drawable.homepager_click);
                tvHome.setTextColor(getResources().getColor(R.color.check_text_blue));
                break;
            case 1:
                if (courseFragment == null) {
                    courseFragment = new CourseFragment();
                    transaction.add(R.id.layoutBellActivity, courseFragment);
                }
                transaction.show(courseFragment);
                ivCourse.setImageResource(R.drawable.course_click);
                tvCourse.setTextColor(getResources().getColor(R.color.check_text_blue));

                break;
            case 2:
                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                    transaction.add(R.id.layoutBellActivity, mineFragment);
                }
                transaction.show(mineFragment);
                ivMine.setImageResource(R.drawable.mine_click);
                tvMine.setTextColor(getResources().getColor(R.color.check_text_blue));

                break;
        }
        transaction.commit();
    }


    private void resetImageViewAndTextView() {
        ivHome.setImageResource(R.drawable.homepager_unclick);
        ivCourse.setImageResource(R.drawable.course_unclick);
        ivMine.setImageResource(R.drawable.mine_unclick);
        tvHome.setTextColor(getResources().getColor(R.color.black));
        tvCourse.setTextColor(getResources().getColor(R.color.black));
        tvMine.setTextColor(getResources().getColor(R.color.black));
    }

    private void hideFragments() {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (courseFragment != null) {
            transaction.hide(courseFragment);
        }
        if (mineFragment != null) {
            transaction.hide(mineFragment);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutHome:
                setTabSelect(0);
                break;
            case R.id.layoutCourse:
                setTabSelect(1);
                break;
            case R.id.layoutMine:
                setTabSelect(2);
                break;
            default:
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
