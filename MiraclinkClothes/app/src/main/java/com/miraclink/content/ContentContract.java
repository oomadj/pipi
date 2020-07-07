package com.miraclink.content;

import com.miraclink.bluetooth.MyBlueService;

public interface ContentContract {
    interface Presenter {
        void writeRXCharacteristic(byte[] bytes, MyBlueService service);
    }

    interface IView {

    }
}
