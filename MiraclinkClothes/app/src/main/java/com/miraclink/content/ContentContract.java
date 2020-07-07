package com.miraclink.content;

import com.miraclink.bluetooth.MyBlueService;
import com.miraclink.content.check.UserCheckFragment;

public interface ContentContract {
    interface Presenter {
        void getCheckFragment(UserCheckFragment fragment);
        void getBlueService(MyBlueService service);
    }

    interface IView {

    }
}
