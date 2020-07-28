package com.miraclink.content.info;

import com.miraclink.database.IUserDatabaseManager;
import com.miraclink.model.User;

public interface UserInfoContract {
    interface Presenter{
        void queryUser(IUserDatabaseManager iUserDatabaseManager,String id);
        void updateUser(IUserDatabaseManager iUserDatabaseManager,String name,int age,int sex,int height,int weight,String id);
        void insertUser(IUserDatabaseManager iUserDatabaseManager,User user);
    }

    interface IView{
        void setUserInfoView(User user);
    }
}
