package com.miraclink.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.miraclink.model.CheckHistory;

import java.util.List;

@Dao
public interface CheckHistoryDao {

    @Query("SELECT * FROM checkhistory WHERE ID= :ID")
    List<CheckHistory> getUserCheckHistory(String ID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CheckHistory... history);

    @Update
    void update(CheckHistory... history);

    @Delete
    void delete(CheckHistory... history);

}
