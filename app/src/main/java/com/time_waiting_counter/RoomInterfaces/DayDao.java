package com.time_waiting_counter.RoomInterfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.time_waiting_counter.RoomModels.Day;

import java.util.List;

/**
 * Created by alessio on 22/04/18.
 */
@Dao
public interface DayDao {
    @Query("select * from day where dayId=:dayId")
    Day getDay(int dayId);

    @Query("select * from day where dayDate=:date")
    Day getDayByDate(String date);

    @Insert
    void insert(Day day);

    @Update
    void update(Day day);

    @Delete
    void delete(Day day);
}
