package com.miraclink.database;

import android.content.Context;

import com.miraclink.model.User;
import com.miraclink.utils.AppExecutors;

import java.util.List;

public class UserDatabaseManager implements IUserDatabaseManager {
    private static UserDatabaseManager INSTANCE;
    private Context context;
    private AppExecutors executors;
    private UserDatabase database;

    public static UserDatabaseManager getInstance(Context context, AppExecutors appExecutors) {
        if (INSTANCE == null) {
            synchronized (UserDatabaseManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserDatabaseManager(context, appExecutors);
                }
            }
        }
        return INSTANCE;
    }

    private UserDatabaseManager(Context context, AppExecutors executors) {
        this.context = context;
        this.executors = executors;
        database = UserDatabase.getInstance(context);
    }

    @Override
    public void queryAllUser(final QueryAllUserCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<User> users = database.getUserDao().getAllUser();
                executors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onQueried(users);
                    }
                });
            }
        };
        executors.diskIO().execute(runnable);
    }

    @Override
    public void insertUserList(final List<User> userList) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                database.getUserDao().insert(userList.toArray(new User[0]));
            }
        };
        executors.diskIO().execute(runnable);
    }

    @Override
    public void insertUser(final User user) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                database.getUserDao().insert(user);
            }
        };
        executors.diskIO().execute(runnable);
    }

    @Override
    public void updateUser(final User user) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                database.getUserDao().update(user);
            }
        };
        executors.diskIO().execute(runnable);
    }

    @Override
    public void updateUser(String name, int age, int sex, int height, int weight, String id) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                database.getUserDao().update(name, age, sex, height, weight, id);
            }
        };
        executors.diskIO().execute(runnable);
    }

    @Override
    public void queryUserByID(QueryUserByIDCallback callback, String ID) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                User user = database.getUserDao().queryUserByID(ID);
                callback.onQueried(user);
            }
        };
        executors.diskIO().execute(runnable);
    }

}
