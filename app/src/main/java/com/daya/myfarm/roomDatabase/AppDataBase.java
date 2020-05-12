package com.daya.myfarm.roomDatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {LocationTask.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract LocationDao locationDao();

}
