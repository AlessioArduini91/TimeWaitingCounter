package com.time_waiting_counter.RoomModels;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by alessio on 22/04/18.
 */

@Entity
public class Week {
    @PrimaryKey(autoGenerate = true)
    private int weekId;

    @ColumnInfo(name = "week")
    private String week;

    public int getWeekId() {
        return this.weekId;
    }

    public void setWeekId(int weekId) {
        this.weekId = weekId;
    }

    public String getWeek() {
        return this.week;
    }

    public void setWeek(String week) {
        this.week = week;
    }
}
