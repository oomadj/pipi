package com.miraclink.content.list;

import com.miraclink.database.IUserDatabaseManager;
import com.miraclink.networks.NetworkController;

public interface UserListContract {
    interface Presenter{
        void getUserList(NetworkController networkController);

        void queryAllUser(IUserDatabaseManager iUserDatabaseManager, IUserDatabaseManager.QueryAllUserCallback callback);

        void queryCheckHistoryByID(IUserDatabaseManager iUserDatabaseManager,IUserDatabaseManager.QueryCheckHistoryByIdCallback callback,String id);
    }
}
