package com.miraclink.content.check;

import com.miraclink.bluetooth.MyBlueService;
import com.miraclink.database.IUserDatabaseManager;
import com.miraclink.model.CheckHistory;
import com.miraclink.model.User;

public interface UserCheckContract {
    interface Presenter{
        void getBlueService(MyBlueService service);
        void getBleAddress(String string);
        void onDestroy();
        void getUserInfo(String id);

        void onCheckStartClick();

        void onCheckRateAdd();

        void onCheckRateCut();

        void onCheckLegClick();

        void onCheckLegZeroClick();

        void onCheckArmClick();

        void onCheckArmZeroClick();

        void onCheckChestClick();

        void onCheckStomachClick();

        void onCheckNeckClick();

        void onCheckBackClick();

        void onCheckRearClick();

        void onUserChanged();

        void queryAllUser(IUserDatabaseManager iUserDatabaseManager, IUserDatabaseManager.QueryAllUserCallback callback);

        void onInit(int time,int rate,int strong);

        void onInsertCheckHistory(IUserDatabaseManager iUserDatabaseManager,CheckHistory history);

    }

    interface IView{
        void setButtonBackground(int i,boolean isCheck);

        void setTimeText(String text);

        void setStartText(String text);

        void refreshCheckButtonText(int armIo,int chestIo,int stomachIo,int legIo,int neckIo,int backIo,int rearIo);

        void refreshStartButtonText(int status);

        void setInfoText(User user);
    }
}
