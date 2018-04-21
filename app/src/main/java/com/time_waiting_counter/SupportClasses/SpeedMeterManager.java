package com.time_waiting_counter.SupportClasses;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.time_waiting_counter.MainActivity;

/**
 * Created by alessio on 04/02/18.
 */

public class SpeedMeterManager {
    Context activityContext;
    private float speed = (float) 0.0;
    private float avgSpeed = (float) 0.0;
    private boolean moveTime=true;
    private boolean stopTime=true;
    private boolean checkGps=true;
    private double latitudeBeforeGpsLoss;
    private double longitudeBeforeGpsLoss;
    private double latitudeAfterGpsRestart;
    private double longitudeAfterGpsRestart;
    private float timeBeforeGpsLoss;
    private float timeAfterGpsRestart;
    private float realTimeBeforeGpsLoss;
    private float realTimeAfterGpsRestart;
    SpeedMeterInterface speedMeter;
    TimerTask timerTask;
    TimerTask checkTimerTask;
    Timer timer;
    Timer checkTimer;
    List<Timer> timerList = new ArrayList<Timer>();
    List<TimerTask> timerTaskList = new ArrayList<TimerTask>();
    float timerSeconds;
    final long TIMER_INTERVAL = 6000;
    final long TIMER_INTERVAL_GPS = 3000;
    Boolean noSignal = false;

    public interface SpeedMeterInterface {
        void setSpeedView(float speed);
        void setGraphEntry(float time, float speed);
        void setTimers(List<Timer> timers);
        void setTimerTasks(List<TimerTask> timerTasks);
    }

    public final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (!checkGps) {
                checkGps = true;
                latitudeAfterGpsRestart = location.getLatitude();
                longitudeAfterGpsRestart = location.getLongitude();
                realTimeAfterGpsRestart = timerSeconds;
                setAverageSpeed();
                setAvgGraphEntries();
            }
            latitudeBeforeGpsLoss = location.getLatitude();
            longitudeBeforeGpsLoss = location.getLongitude();
            realTimeBeforeGpsLoss = timerSeconds;
            noSignal = false;
            speed = location.getSpeed();
            speedMeter.setSpeedView(speed);
            if (speed < 1.0 && moveTime) {
                MainActivity.toggleChronometer(false);
                moveTime = false;
                stopTime = true;
            } else if (speed >= 1.0 && stopTime) {
                MainActivity.toggleChronometer(true);
                moveTime = true;
                stopTime = false;
            }

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    public SpeedMeterManager(final Context context){
        activityContext = context;
        speedMeter = (SpeedMeterInterface) context;
        speedMeter.setSpeedView(speed);
        initTimer();
    }

    private void initTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (checkGps) {
                    if (timerSeconds % TIMER_INTERVAL == 0) {
                        speedMeter.setGraphEntry(timerSeconds, speed);
                        timeBeforeGpsLoss = timerSeconds;
                    }
                    timerSeconds += 1000;
                } else {
                    if (timerSeconds % TIMER_INTERVAL == 0) {
                        timeAfterGpsRestart = timerSeconds;
                    }
                    timerSeconds += 1000;
                }
            }
        };

        timer = new Timer();

        checkTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (noSignal && checkGps) {
                    MainActivity.stopChronometers();
                    checkGps = false;
                }
                noSignal = true;
            }
        };

        checkTimer = new Timer();

        checkTimer.schedule(checkTimerTask, 0, TIMER_INTERVAL_GPS);
        timer.schedule(timerTask, 0, 1000);

        timerList.add(timer);
        timerList.add(checkTimer);
        timerTaskList.add(timerTask);
        timerTaskList.add(checkTimerTask);

        speedMeter.setTimers(timerList);
        speedMeter.setTimerTasks(timerTaskList);
    }

    private void setAverageSpeed() {
        if (latitudeBeforeGpsLoss == 0.0 || longitudeBeforeGpsLoss == 0.0) {
            avgSpeed = 0;
        } else {
            Location locationBeforeGpsLoss = new Location("beforeLoss");
            locationBeforeGpsLoss.setLatitude(latitudeBeforeGpsLoss);
            locationBeforeGpsLoss.setLongitude(longitudeBeforeGpsLoss);

            Location locationAfterGpsRestart = new Location("afterRestart");
            locationAfterGpsRestart.setLatitude(latitudeAfterGpsRestart);
            locationAfterGpsRestart.setLongitude(longitudeAfterGpsRestart);

            float distance = locationBeforeGpsLoss.distanceTo(locationAfterGpsRestart);
            avgSpeed = (distance * 1000) / (realTimeAfterGpsRestart - realTimeBeforeGpsLoss);
        }
    }

    private void setAvgGraphEntries() {
        for (float time = timeBeforeGpsLoss + TIMER_INTERVAL; time <= timeAfterGpsRestart; time = time + TIMER_INTERVAL) {
            speedMeter.setGraphEntry(time, avgSpeed);
        }
    }

    public String getTotalParsedTime() {
        int seconds = (int) (timerSeconds / (1000)) % 60;
        int minutes = (int) (timerSeconds / (1000 * 60)) % 60;
        int hours = (int) (timerSeconds / (1000 * 60 * 60)) % 24;

        return String.format("%02d : %02d : %02d", hours, minutes, seconds);
    }

    public void setMoveTime(Boolean moveTime) {
        this.moveTime = moveTime;
    }

    public void setStopTime(Boolean stopTime) {
        this.stopTime = stopTime;
    }
}
