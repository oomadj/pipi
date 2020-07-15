package com.miraclink.database;

import com.miraclink.model.User;

import java.util.List;

public interface IUserDatabaseManager {
    interface QueryAllUserCallback {
        void onQueried(List<User> userList);
    }

    interface QueryUserByIDCallback {
        void onQueried(User user);
    }

    void queryAllUser(QueryAllUserCallback callback);

    void insertUserList(List<User> userList);

    void insertUser(User user);

    void updateUser(User user);

    void updateUser(String name, int age, int sex, int height, int weight, String id);

    void updateUserSettings(int time,int strong,int rate,int compose,int mode,String id);

    void queryUserByID(QueryUserByIDCallback callback, String ID);
    //User queryUserByID(String ID);
}
