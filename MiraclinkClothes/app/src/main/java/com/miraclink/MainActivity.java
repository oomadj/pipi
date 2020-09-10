package com.miraclink;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.miraclink.adapter.MainAdapter;
import com.miraclink.base.BaseActivity;
import com.miraclink.base.BaseCallback;
import com.miraclink.bluetooth.DeviceListActivity;
import com.miraclink.bluetooth.MyBlueService;
import com.miraclink.content.ContentActivity;
import com.miraclink.utils.BroadCastAction;
import com.miraclink.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, BaseCallback {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private MainAdapter mainAdapter;
    private Button btConnect;
    private Button btSend;
    private Button btTest;
    private EditText editMessage;
    private TextView textStatus;
    private BroadcastReceiver receiver;
    private BluetoothDevice bluetoothDevice;
    private BluetoothAdapter bluetoothAdapter;
    private ServiceConnection serviceConnection;
    Intent bindIntent;
    private MyBlueService blueService;
    private ArrayList<String> stringList;

    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int UART_PROFILE_READY = 10;
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;

    private int mState = UART_PROFILE_DISCONNECTED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initParam();
        intiView();
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
        //registerReceiver(receiver, filter);
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
        //unregisterReceiver(receiver);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void initParam() {
        stringList = new ArrayList<String>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mainAdapter = new MainAdapter();
        mainAdapter.setData(stringList);
        bindIntent = new Intent(this, MyBlueService.class);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                blueService = ((MyBlueService.LocalBinder) service).getService();
                if (!blueService.initialize()) {
                    LogUtil.d(TAG, "Unable to initialize Bluetooth");
                    finish();
                }
                blueService.setCallback(MainActivity.this);
                LogUtil.i(TAG, "");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                blueService = null;
            }
        };

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                final Intent mIntent = intent;
                if (action.equals(BroadCastAction.ACTION_GATT_CONNECTED)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                            btConnect.setText("disconnect");
                            editMessage.setEnabled(true);
                            btSend.setEnabled(true);
                            textStatus.setText(bluetoothDevice.getName() + " - ready");
                            stringList.add("[" + currentDateTimeString + "] connected to: " + bluetoothDevice.getName());
                            mainAdapter.setData(stringList);
                            mState = UART_PROFILE_CONNECTED;
                        }
                    });
                }

                if (action.equals(BroadCastAction.ACTION_GATT_DISCONNECTED)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                            LogUtil.d(TAG, "uart disconnect msg");
                            btConnect.setText(R.string.connect);
                            editMessage.setEnabled(false);
                            btSend.setEnabled(false);
                            textStatus.setText("not connected");
                            stringList.add("[" + currentDateTimeString + "] disconnected to" + bluetoothDevice.getName());
                            mainAdapter.setData(stringList);
                            mState = UART_PROFILE_DISCONNECTED;
                            blueService.close();
                        }
                    });
                }

                if (action.equals(BroadCastAction.ACTION_GATT_SERVICES_DISCOVERED)) {
                    blueService.enableTXNotification();
                }

                if (action.equals(BroadCastAction.ACTION_DATA_AVAILABLE)) {
                    final byte[] values = intent.getByteArrayExtra(BroadCastAction.EXTRA_DATA);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String text = null;
                            try {
                                text = new String(values, "UTF-8");
                                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                                stringList.add("[" + currentDateTimeString + "] RX: " + text);
                                mainAdapter.setData(stringList);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recyclerMainActivity);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mainAdapter);
        btConnect = findViewById(R.id.btMainActivityConnect);
        btConnect.setOnClickListener(this);
        btSend = findViewById(R.id.btMainActivitySend);
        btSend.setOnClickListener(this);
        editMessage = findViewById(R.id.editMainActivityHex);
        textStatus = findViewById(R.id.textMainActivityStatus);
        btTest = findViewById(R.id.btMainActivityTestToContent);
        btTest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btMainActivityConnect:
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                } else {
                    if (btConnect.getText().equals("connect")) {
                        Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                        startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                    } else {
                        if (bluetoothDevice != null) {
                            blueService.disconnect();
                        }
                    }
                }
                break;
            case R.id.btMainActivitySend:
                String message = null;
                if (editMessage.getText().toString().isEmpty()) {
                    message = "0xAE 0x03 0xE1 0xE4";
                } else {
                    message = editMessage.getText().toString();
                }
                byte[] values;
                byte[] values2 = new byte[]{(byte) 0xAE, 0x03, (byte) 0xE1, (byte) 0xE4};
                try {
                    values = message.getBytes("UTF-8");
                    blueService.writeRXCharacteristic(values2,"test");
                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                    stringList.add("[" + currentDateTimeString + "]" + message);
                    mainAdapter.setData(stringList);
                    editMessage.setText("");
                } catch (UnsupportedEncodingException e) {
                    LogUtil.e(TAG, "edcoding exception" + e.toString());
                    e.printStackTrace();
                }

                break;
            case R.id.btMainActivityTestToContent:
                Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                startActivity(intent);
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
                    bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
                    textStatus.setText(bluetoothDevice.getName() + "- connecting");
                    blueService.connect(address);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "bt has turned on", Toast.LENGTH_SHORT).show();
                } else {
                    LogUtil.d(TAG, "bt not enable");
                    Toast.makeText(this, "problem in bt turning on", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                LogUtil.e(TAG, "wrong request code");
                break;
        }
    }

    @Override
    public void onDeviceChange(BluetoothGatt gatt,BluetoothGattCharacteristic bluetoothGattCharacteristic) {

    }

    @Override
    public void onDisconnected() {

    }

}
