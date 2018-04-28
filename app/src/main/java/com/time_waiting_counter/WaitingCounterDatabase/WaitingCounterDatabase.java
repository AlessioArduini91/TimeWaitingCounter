package com.time_waiting_counter.WaitingCounterDatabase;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;

import com.time_waiting_counter.RoomInterfaces.DayDao;
import com.time_waiting_counter.RoomModels.Day;

/**
 * Created by alessio on 22/04/18.
 */

@Database(entities = {Day.class}, version = 2)
public abstract class WaitingCounterDatabase extends RoomDatabase {
    public abstract DayDao dayDao();
}