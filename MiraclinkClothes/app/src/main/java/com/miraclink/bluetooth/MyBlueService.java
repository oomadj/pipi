package com.miraclink.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.miraclink.base.BaseCallback;
import com.miraclink.utils.BroadCastAction;
import com.miraclink.utils.LogUtil;
import com.miraclink.utils.SharePreUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MyBlueService extends Service {
    private static final String TAG = MyBlueService.class.getSimpleName();
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private BluetoothGatt bluetoothGatt;
    private String bluetoothAddress;
    private int connectStatus;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public static UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    //public static UUID RX_SERVICE_UUID; // = UUID.fromString("8653000a-43e6-47b7-9cb0-5fc21d4ae340");
    //public static UUID RX_CHAR_UUID; // = UUID.fromString("8653000c-43e6-47b7-9cb0-5fc21d4ae340");
    //public static UUID TX_CHAR_UUID; // = UUID.fromString("8653000b-43e6-47b7-9cb0-5fc21d4ae340");

    public Map<String,BluetoothGatt> idAndGatt = new HashMap<>();
    private Map<String,UUID> RX_SERVICE_UUID_MAP = new HashMap<>();
    private Map<String,UUID> RX_CHAR_UUID_MAP = new HashMap<>();
    private Map<String,UUID> TX_CHAR_UUID_MAP = new HashMap<>();

    private BaseCallback baseCallback; // xzx add

    public void setCallback(BaseCallback callback) {
        baseCallback = callback;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public MyBlueService getService() {
            return MyBlueService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    public boolean initialize() {
        if (bluetoothManager == null) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager == null) {
                LogUtil.d(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        if (bluetoothAdapter == null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null) {
                LogUtil.d(TAG, "Unable to obtain a BluetoothAdapter.");
                return false;
            }
        }

        return true;
    }

    // Previously connected device.  Try to reconnect.
    public boolean connect(final String address) {
        //xzx add
        BluetoothGatt gatt = idAndGatt.get(address);  //如果没有更换用户 先断开之前的连接再连接新的设备
        if (gatt != null) {
            gatt.disconnect();
            gatt.close();
            idAndGatt.remove(address);
        }

        if (bluetoothAdapter == null || address == null) {
            LogUtil.i(TAG, "BluetoothAdapter not initialized or unspecified address");
            return false;
        }

        if (bluetoothAddress != null && address.equals(bluetoothAddress) && bluetoothGatt != null) {
            LogUtil.i(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (bluetoothGatt.connect()) {
                connectStatus = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            LogUtil.i(TAG, "device not found");
            return false;
        }
        // android 6.0 connectGatt
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothGatt = device.connectGatt(this, false, gattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            bluetoothGatt = device.connectGatt(this, false, gattCallback);
        }

        idAndGatt.put(SharePreUtils.getCurrentID(this),bluetoothGatt); //放在gattCallback中
        LogUtil.i(TAG, "Trying to create a new connection.");
        bluetoothAddress = address;
        connectStatus = STATE_CONNECTING;
        return true;
    }

    public void disconnect() {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            LogUtil.d(TAG, "bluetooth adapter not initialized");
            return;
        }
        bluetoothGatt.disconnect();
    }

    public void close() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothAddress = null;
        bluetoothGatt.close();
        bluetoothGatt = null;
    }

    public boolean enableTXNotification() {
        BluetoothGatt gatt = idAndGatt.get(SharePreUtils.getCurrentID(this));
        BluetoothGattService rxService = gatt.getService(RX_SERVICE_UUID_MAP.get(SharePreUtils.getCurrentID(this)));
        if (rxService == null) {
            LogUtil.d(TAG, "rxServce = null");
            broadcastUpdate(BroadCastAction.DEVICE_DOES_NOT_SUPPORT_UART);
            return false;
        }

        LogUtil.i(TAG,"tx uuid:"+TX_CHAR_UUID_MAP.get(SharePreUtils.getCurrentID(this)).toString()+"rx service uuid："+RX_SERVICE_UUID_MAP.get(SharePreUtils.getCurrentID(this)));
        BluetoothGattCharacteristic txCharacteristic = rxService.getCharacteristic(TX_CHAR_UUID_MAP.get(SharePreUtils.getCurrentID(this)));
        if (txCharacteristic == null) {
            LogUtil.d(TAG, "txChar = null");
            broadcastUpdate(BroadCastAction.DEVICE_DOES_NOT_SUPPORT_UART);
            return false;
        }

        boolean b = gatt.setCharacteristicNotification(txCharacteristic, true);
        LogUtil.i(TAG, "set notification :" + b);

        BluetoothGattDescriptor descriptor = txCharacteristic.getDescriptor(CCCD);
        if (descriptor == null) {
            LogUtil.d(TAG, "descriptor = null");
            descriptor = new BluetoothGattDescriptor(CCCD, BluetoothGattDescriptor.PERMISSION_READ);  //read or write
        }
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean isDescriptor = gatt.writeDescriptor(descriptor);
        LogUtil.i(TAG, "id descriptor:" + isDescriptor);
        return isDescriptor;
    }

    public void writeRXCharacteristic(byte[] values,String id) {
        BluetoothGatt gatt = idAndGatt.get(id);
        if (gatt == null) {
            LogUtil.i(TAG, "xzx blue gatt is null");
            return;
        }
        BluetoothGattService rxService = gatt.getService(RX_SERVICE_UUID_MAP.get(id));
        if (rxService == null) {
            Log.d(TAG, "xzx rx service not found");
            broadcastUpdate(BroadCastAction.DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }

        BluetoothGattCharacteristic rxCharacteristic = rxService.getCharacteristic(RX_CHAR_UUID_MAP.get(id));
        if (rxCharacteristic == null) {
            LogUtil.d(TAG, "xzx rx char not found");
            broadcastUpdate(BroadCastAction.DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
        rxCharacteristic.setValue(values);

        // xzx add test
        if ((rxCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) == 0
                && (rxCharacteristic.getProperties()
                & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) == 0) {
            Log.e(TAG, "xzx--fail 111");
        }

        if (rxService == null || rxCharacteristic.getValue() == null) {
            Log.e(TAG, "xzx--fail 222");
        }

        boolean status = gatt.writeCharacteristic(rxCharacteristic);
        LogUtil.i(TAG, "write rx char status:" + status);
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        //LocalBroadcastManager
        this.sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        if (TX_CHAR_UUID_MAP.get(SharePreUtils.getCheckID(this)).equals(characteristic.getUuid())) {
            intent.putExtra(BroadCastAction.EXTRA_DATA, characteristic.getValue());
        } else {
        }
        this.sendBroadcast(intent);
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            LogUtil.i(TAG, "onConnectionStateChange states:" + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = BroadCastAction.ACTION_GATT_CONNECTED;
                connectStatus = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                LogUtil.i(TAG, "Connected to GATT server | start service discovery:" + gatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = BroadCastAction.ACTION_GATT_DISCONNECTED;
                connectStatus = STATE_DISCONNECTED;
                broadcastUpdate(intentAction);
                //close();  // xzx add 建议：在onConnectionStateChange()回调中判断，若state非0(连接断开)，调用gatt.close()，手动释放掉gatt相关资源
                LogUtil.i(TAG, "disconnect form gatt server");
                baseCallback.onDisconnected();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> services = gatt.getServices();
                for (int i = 0; i < services.size(); i++) {
                    BluetoothGattService service = services.get(i);
                    LogUtil.i(TAG, "service id:" + service.getUuid().toString());
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                    for (int b = 0; b < characteristics.size(); b++) {
                        int charProp = characteristics.get(b).getProperties();
                        if ((charProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                            RX_SERVICE_UUID_MAP.put(SharePreUtils.getCurrentID(MyBlueService.this),services.get(i).getUuid());
                            RX_CHAR_UUID_MAP.put(SharePreUtils.getCurrentID(MyBlueService.this),characteristics.get(b).getUuid());
                            //LogUtil.i(TAG, "write uuid: service:" + RX_SERVICE_UUID + ": rx char:" + RX_CHAR_UUID);
                        }

                        if ((charProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
                            RX_SERVICE_UUID_MAP.put(SharePreUtils.getCurrentID(MyBlueService.this),services.get(i).getUuid());
                            RX_CHAR_UUID_MAP.put(SharePreUtils.getCurrentID(MyBlueService.this),characteristics.get(b).getUuid());
                            //Log.i(TAG, "xzx-write no response-uuid: service:" + RX_SERVICE_UUID + ":rx char:" + RX_CHAR_UUID);
                        }

                        if ((charProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            RX_SERVICE_UUID_MAP.put(SharePreUtils.getCurrentID(MyBlueService.this),services.get(i).getUuid());
                            TX_CHAR_UUID_MAP.put(SharePreUtils.getCurrentID(MyBlueService.this),characteristics.get(b).getUuid());
                            //Log.i(TAG, "xzx-notify-uuid: service" + RX_SERVICE_UUID + ":tx char:" + TX_CHAR_UUID);
                        }

                    }
                }
                broadcastUpdate(BroadCastAction.ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                LogUtil.e(TAG, "on service discovery failed");
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            LogUtil.i(TAG, "on ct read");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(BroadCastAction.ACTION_DATA_AVAILABLE, characteristic);

            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                String s = null;
                try {
                    s = new String(characteristic.getValue(), "UTF-8");
                    //LogUtil.i(TAG, "on ct write value:" + s + ":hex:");
                } catch (UnsupportedEncodingException e) {
                    LogUtil.e(TAG, "un encoding exception" + e.toString());
                    e.printStackTrace();
                }
            } else {
                LogUtil.e(TAG, "on ct write failed:" + status);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            LogUtil.i(TAG, "on char changed");
            broadcastUpdate(BroadCastAction.ACTION_DATA_AVAILABLE, characteristic);
            baseCallback.onDeviceChange(gatt,characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            LogUtil.i(TAG, "on descriptor:" + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                LogUtil.i(TAG, "on descriptor success");
            }
        }
    };


}











