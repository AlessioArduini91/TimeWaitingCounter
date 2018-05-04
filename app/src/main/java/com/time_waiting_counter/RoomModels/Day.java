package com.time_waiting_counter.RoomModels;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.util.Calendar;

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

    @ColumnInfo(name = "dayDate")
    private String dayDate;

    @ColumnInfo(name = "dayMovingTime")
    private long dayMovingTime;

    @ColumnInfo(name = "dayStoppingTime")
    private long dayStoppingTime;

    @ColumnInfo(name = "dayMovingPercent")
    private int dayMovingPercent;

    @ColumnInfo(name = "dayStoppingPercent")
    private int dayStoppingPercent;

    @ColumnInfo(name = "day")
    private int day;

    @ColumnInfo(name = "week")
    private int week;

    @ColumnInfo(name = "month")
    private int month;

    @ColumnInfo(name = "year")
    private int year;

    public Day() {

    }

    public int getDayId() {
        return this.dayId;
    }

    public void setDayId(int dayId) {
        this.dayId = dayId;
    }

    public String getDayDate() {
        return this.dayDate;
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

    public int getDayMovingPercent() {
        return this.dayMovingPercent;
    }

    public int getDayStoppingPercent() {
        return this.dayStoppingPercent;
    }

    public void setDayMovingPercent(int dayMovingPercent) {
        this.dayMovingPercent = dayMovingPercent;
    }

    public void setDayStoppingPercent(int dayStoppingPercent) {
        this.dayStoppingPercent = dayStoppingPercent;
    }

    public int getDay() {
        return this.day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getWeek() {
        return this.week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getMonth() {
        return this.month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
