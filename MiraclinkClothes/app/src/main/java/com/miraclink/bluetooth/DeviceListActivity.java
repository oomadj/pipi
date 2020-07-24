package com.miraclink.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.miraclink.R;
import com.miraclink.base.BaseActivity;
import com.miraclink.utils.AnimationFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> bluetoothDevices;
    Map<String, Integer> devRssiValues;
    private TextView emptyList;
    private Button btCancel;
    private ListView listView;
    private ImageView imageLoading;
    public static final String TAG = DeviceListActivity.class.getSimpleName();
    private static final long SCAN_PERIOD = 10000;
    private Handler handler;
    private boolean isScanning;
    private DeviceListAdapter listAdapter;

    private WindowManager.LayoutParams layoutParams;
    private View view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        initParam();
        intiView();
        scanLeDevice(true);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            showToastMessage("ble not support 1");
            finish();
        }
        if (bluetoothAdapter == null) {
            showToastMessage("ble not support 2");
            finish();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //WindowManager.LayoutParams layoutParams = this.getWindow().getAttributes();
        //layoutParams.gravity = Gravity.CENTER;
        //layoutParams.y = 200;

        view = getWindow().getDecorView();
        layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.height = getResources().getDisplayMetrics().heightPixels * 3 / 5;
        layoutParams.width = getResources().getDisplayMetrics().widthPixels * 3 / 5;
        getWindowManager().updateViewLayout(view, layoutParams);
    }

    @Override
    protected void initParam() {
        handler = new Handler();
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothDevices = new ArrayList<BluetoothDevice>();
        devRssiValues = new HashMap<String, Integer>();
        listAdapter = new DeviceListAdapter(this, bluetoothDevices);
    }

    @Override
    protected void intiView() {
        emptyList = findViewById(R.id.empty);
        btCancel = findViewById(R.id.btn_cancel);
        btCancel.setOnClickListener(this);
        listView = findViewById(R.id.new_devices);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        imageLoading = findViewById(R.id.imageDeviceListActivityLoading);
        imageLoading.setImageResource(R.drawable.icon_devicelist_loading);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isScanning = false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    btCancel.setText(R.string.scan);
                    stopLoadAnima();
                }
            }, SCAN_PERIOD);
            startLoadAnima();
            isScanning = true;
            bluetoothAdapter.startLeScan(leScanCallback);
            btCancel.setText(R.string.cancel);
        } else {
            isScanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
            btCancel.setText(R.string.scan);
        }
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //if (device.getName() != null && device.getName().contains("SMC")) { // get SMC(Miraclink) name ble
                        addDevice(device, rssi);
                    //}
                }
            });
        }
    };

    private void addDevice(BluetoothDevice device, int rssi) {
        boolean deviceFound = false;
        for (BluetoothDevice listDec : bluetoothDevices) {
            if (listDec.getAddress().equals(device.getAddress())) {
                deviceFound = true;
                break;
            }
        }

        devRssiValues.put(device.getAddress(), rssi);
        if (!deviceFound) {
            bluetoothDevices.add(device);
            emptyList.setVisibility(View.GONE);
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetoothAdapter.stopLeScan(leScanCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothAdapter.stopLeScan(leScanCallback);
    }

    @Override
    public void onClick(View v) {
        if (isScanning == false) scanLeDevice(true);
        else finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        bluetoothAdapter.stopLeScan(leScanCallback);
        Bundle b = new Bundle();
        b.putString(BluetoothDevice.EXTRA_DEVICE, bluetoothDevices.get(position).getAddress());
        Intent intent = new Intent();
        intent.putExtras(b);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    class DeviceListAdapter extends BaseAdapter {
        private Context context;
        private List<BluetoothDevice> devices;
        LayoutInflater layoutInflater;

        public DeviceListAdapter(Context context, List<BluetoothDevice> devices) {
            this.context = context;
            this.devices = devices;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup vg;
            if (convertView != null) {
                vg = (ViewGroup) convertView;
            } else {
                vg = (ViewGroup) layoutInflater.inflate(R.layout.item_device_list, null);
            }
            BluetoothDevice device = devices.get(position);
            final TextView tvadd = vg.findViewById(R.id.address);
            final TextView tvname = vg.findViewById(R.id.name);
            final TextView tvpaired = vg.findViewById(R.id.paired);
            final TextView tvrssi = vg.findViewById(R.id.rssi);
            tvrssi.setVisibility(View.VISIBLE);
            byte rssival = (byte) devRssiValues.get(device.getAddress()).intValue();
            if (rssival != 0) {
                tvrssi.setText("rssi =" + String.valueOf(rssival));
            }

            tvname.setText(device.getName());
            tvadd.setText(device.getAddress());

            tvname.setText(device.getName());
            tvadd.setText(device.getAddress());
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                tvname.setTextColor(Color.BLACK);
                tvadd.setTextColor(Color.BLACK);
                tvpaired.setTextColor(Color.GRAY);
                tvpaired.setVisibility(View.VISIBLE);
                tvpaired.setText(R.string.paired);
                tvrssi.setVisibility(View.VISIBLE);
                tvrssi.setTextColor(Color.BLACK);

            } else {
                tvname.setTextColor(Color.BLACK);
                tvadd.setTextColor(Color.BLACK);
                tvpaired.setVisibility(View.GONE);
                tvrssi.setVisibility(View.VISIBLE);
                tvrssi.setTextColor(Color.BLACK);
            }
            return vg;
        }
    }

    private void showToastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startLoadAnima() {
        imageLoading.setVisibility(View.VISIBLE);
        imageLoading.startAnimation(AnimationFactory.rotate2Self());
    }

    private void stopLoadAnima() {
        imageLoading.setVisibility(View.INVISIBLE);
        imageLoading.clearAnimation();
    }

}
