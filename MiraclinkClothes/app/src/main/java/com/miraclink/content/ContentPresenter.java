package com.miraclink.content;

import android.bluetooth.BluetoothGattCharacteristic;

import com.miraclink.base.BaseCallback;
import com.miraclink.bluetooth.MyBlueService;

public class ContentPresenter implements ContentContract.Presenter, BaseCallback {
    @Override
    public void writeRXCharacteristic(byte[] bytes, MyBlueService service) {

    }

    @Override
    public void onDeviceChange(BluetoothGattCharacteristic bluetoothGattCharacteristic) {

    }
}
