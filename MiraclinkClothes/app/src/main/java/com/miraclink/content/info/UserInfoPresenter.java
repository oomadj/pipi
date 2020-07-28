package com.miraclink.content.info;

import com.miraclink.database.IUserDatabaseManager;
import com.miraclink.model.User;

public class UserInfoPresenter implements UserInfoContract.Presenter {
    private UserInfoContract.IView iView;

    public UserInfoPresenter(UserInfoContract.IView iView) {
        this.iView = iView;
    }

    @Override
    public void queryUser(IUserDatabaseManager iUserDatabaseManager, String id) {
        iUserDatabaseManager.queryUserByID(user -> {
            iView.setUserInfoView(user);
        }, id);
    }

    @Override
    public void updateUser(IUserDatabaseManager iUserDatabaseManager, String name, int age, int sex, int height, int weight, String id) {
        iUserDatabaseManager.updateUser(name, age, sex, height, weight, id);
    }

    @Override
    public void insertUser(IUserDatabaseManager iUserDatabaseManager, User user) {
        iUserDatabaseManager.insertUser(user);
    }
}
