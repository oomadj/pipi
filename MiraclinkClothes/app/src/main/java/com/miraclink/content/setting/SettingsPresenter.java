package com.miraclink.content.setting;

import com.miraclink.database.IUserDatabaseManager;

public class SettingsPresenter implements SettingsContract.Presenter {
    private SettingsContract.IView iView;

    public SettingsPresenter(SettingsContract.IView iView) {
        this.iView = iView;
    }

    @Override
    public void queryUser(IUserDatabaseManager iUserDatabaseManager, String id) {
        iUserDatabaseManager.queryUserByID(user -> {
            iView.setUserView(user);
        }, id);
    }
}
