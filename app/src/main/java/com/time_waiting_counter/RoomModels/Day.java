package com.time_waiting_counter.RoomModels;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by alessio on 22/04/18.
 */
//
//@Entity(foreignKeys = @ForeignKey(entity = Day.class,
//        parentColumns = "weekId",
//        childColumns = "dayId",
//        onDelete = CASCADE))
    @Entity
public class Day {
    @PrimaryKey(autoGenerate = true)
    private int dayId;

//    @ColumnInfo(name = "weekId")
//    private int weekId;

    @ColumnInfo(name = "dayDate")
    private String dayDate;

    @ColumnInfo(name = "dayMovingTime")
    private long dayMovingTime;

    @ColumnInfo(name = "dayStoppingTime")
    private long dayStoppingTime;

    public Day() {

    }

    public int getDayId() {
        return this.dayId;
    }

    public void setDayId(int dayId) {
        this.dayId = dayId;
    }

//    public int getWeekId() {
//        return this.weekId;
//    }
//
//    public void setWeekId(int weekId) {
//        this.weekId = weekId;
//    }

    public String getDayDate() {
        return this.dayDate;
    }

    public String getFormattedMoveTime() {
        return getFormattedTime(this.dayMovingTime);
    }

    public String getFormattedStopTime() {
        return getFormattedTime(this.dayStoppingTime);
    }

    private String getFormattedTime(long timerSeconds) {
        int seconds = (int) (timerSeconds / (1000)) % 60;
        int minutes = (int) (timerSeconds / (1000 * 60)) % 60;
        int hours = (int) (timerSeconds / (1000 * 60 * 60)) % 24;

        return String.format("%02d : %02d : %02d", hours, minutes, seconds);
    }

    public void setDayDate(String dayDate) {
        this.dayDate = dayDate;
    }

    public long getDayMovingTime() {
        return this.dayMovingTime;
    }

    public void setDayMovingTime(long dayMovingTime) {
        this.dayMovingTime = dayMovingTime;
    }

    public long getDayStoppingTime() {
        return this.dayStoppingTime;
    }

    public void setDayStoppingTime(long dayStoppingTime) {
        this.dayStoppingTime = dayStoppingTime;
    }
}
