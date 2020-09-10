package com.miraclink.base;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public interface BaseCallback {
    void onDeviceChange(BluetoothGatt gatt ,BluetoothGattCharacteristic bluetoothGattCharacteristic);

    void onDisconnected();
}
