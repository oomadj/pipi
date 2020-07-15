package com.miraclink.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.miraclink.model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAllUser();


    @Query("SELECT * FROM user WHERE ID = :ID")
    User queryUserByID(String ID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User... users);

    @Update
    void update(User... users);

    @Delete
    void delete(User... users);

    @Query("UPDATE user SET name = :name,age =:age,sex =:sex,height =:height,weight =:weight WHERE ID = :ID")
    void update(String name, int age, int sex, int height, int weight, String ID);

    @Query("UPDATE user SET time = :time,strong =:strong,rate =:rate,compose =:compose,mode =:mode WHERE ID = :ID")
    void update(int time,int strong,int rate,int compose,int mode,String ID);
}
