package com.miraclink.content.info;

import com.miraclink.database.IUserDatabaseManager;
import com.miraclink.model.User;

public interface UserInfoContract {
    interface Presenter{
        void queryUser(IUserDatabaseManager iUserDatabaseManager,String id);
        void updateUser(IUserDatabaseManager iUserDatabaseManager,String name,int age,int sex,int height,int weight,String id);
    }

    interface IView{
        void setUserInfoView(User user);
    }
}
