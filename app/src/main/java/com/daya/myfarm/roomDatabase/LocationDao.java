package com.daya.myfarm.roomDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LocationDao {

    @Query("Select * from locationtask")
    LiveData<List<LocationTask>> getAllData();

    @Insert
    void insert(LocationTask locationTask);

    @Delete
    void delete(LocationTask locationTask);

    @Query("Delete from locationtask")
    void deleteAll();

    @Query("Delete from locationtask where farmName = :locname")
    void deletebyName(String locname);
}
