package com.miraclink.content.list;

import com.miraclink.database.IUserDatabaseManager;
import com.miraclink.networks.NetworkController;

public class UserListPresenter implements UserListContract.Presenter {
    @Override
    public void getUserList(NetworkController networkController) {
        //networkController get user list
        networkController.getUserList();
    }

    @Override
    public void queryAllUser(IUserDatabaseManager iUserDatabaseManager, IUserDatabaseManager.QueryAllUserCallback callback) {
        iUserDatabaseManager.queryAllUser(callback);
    }

    @Override
    public void queryCheckHistoryByID(IUserDatabaseManager iUserDatabaseManager, IUserDatabaseManager.QueryCheckHistoryByIdCallback callback, String id) {
        iUserDatabaseManager.queryCheckHistoryById(callback,id);
    }
}
