package com.miraclink.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.miraclink.model.CheckHistory;
import com.miraclink.model.User;

@Database(entities = {User.class, CheckHistory.class},version = 1,exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {
    private static final String DB_NAME = "UserDatabase.db";
    private static volatile UserDatabase INSTANCE;

    static UserDatabase getInstance(final Context context){
        if (INSTANCE == null){
            synchronized (UserDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),UserDatabase.class,DB_NAME).build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract UserDao getUserDao();

    public abstract CheckHistoryDao getHistoryDao();

}
