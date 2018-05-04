package com.time_waiting_counter.SupportClasses;

/**
 * Created by alessio on 04/05/18.
 */

public class DataTimeFormatter {

    public static String getFormattedTime(long timerSeconds) {
        int seconds = (int) (timerSeconds / (1000)) % 60;
        int minutes = (int) (timerSeconds / (1000 * 60)) % 60;
        int hours = (int) (timerSeconds / (1000 * 60 * 60)) % 24;
        int days = (int) (timerSeconds / (1000 * 60 * 60 * 24));

        if (days != 0) {
            if (days == 1) {
                return String.format("%d giorno, %02d : %02d : %02d", days, hours, minutes, seconds);
            } else {
                return String.format("%d giorni, %02d : %02d : %02d", days, hours, minutes, seconds);
            }
        } else {
            return String.format("%02d : %02d : %02d", hours, minutes, seconds);
        }
    }

    public static int getMovingPercent(int movingTime, int stoppingTime) {
        if (getTotalTime(movingTime, stoppingTime) == 0) {
            return 0;
        } else {
            return ((int) movingTime * 100) / getTotalTime(movingTime, stoppingTime);
        }
    }

    public static int getStoppingPercent(int movingTime, int stoppingTime) {
        if (getTotalTime(movingTime, stoppingTime) == 0) {
            return 0;
        } else {
            return 100 - getMovingPercent(movingTime, stoppingTime);
        }
    }

    public static int getTotalTime(int movingTime, int stoppingTime) {
        return (int) movingTime + (int) stoppingTime;
    }

}
