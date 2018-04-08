package tacchella.arduini.com.time_waiting_counter.SupportClasses;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tacchella.arduini.com.time_waiting_counter.MainActivity;

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
    SpeedMeterInterface speedMeter;
    TimerTask timerTask;
    TimerTask checkTimerTask;
    Timer timer;
    Timer checkTimer;
    List<Timer> timerList = new ArrayList<Timer>();
    List<TimerTask> timerTaskList = new ArrayList<TimerTask>();
    float timerSeconds;
    final long TIMER_INTERVAL = 10000;
    final long TIMER_INTERVAL_GPS = 1000;
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
                latitudeAfterGpsRestart = location.getLatitude();
                longitudeAfterGpsRestart = location.getLongitude();
                timeAfterGpsRestart = timerSeconds;
                setAverageSpeed();
                setAvgGraphEntries();
                checkGps = true;
            }
            latitudeBeforeGpsLoss = location.getLatitude();
            longitudeBeforeGpsLoss = location.getLongitude();
            noSignal = false;
            speed = location.getSpeed();
            speedMeter.setSpeedView(speed);
                // false = stop
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

    public void createNewTimer() {
        checkTimerTask.cancel();
        checkTimer.cancel();
        timerTask.cancel();
        timer.cancel();
        timerSeconds = 0f;
        stopTime = true;
        moveTime = true;
        checkGps = true;
        noSignal = false;
        initTimer();
    }

    private void initTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (checkGps) {
                    speedMeter.setGraphEntry(timerSeconds, speed);
                    timerSeconds += 10000;
                    timeBeforeGpsLoss = timerSeconds;
                } else {
                    timerSeconds += 10000;
                }
            }
        };

        timer = new Timer();
        timer.schedule(timerTask, 0, TIMER_INTERVAL);

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

        timerList.add(timer);
        timerList.add(checkTimer);
        timerTaskList.add(timerTask);
        timerTaskList.add(checkTimerTask);

        speedMeter.setTimers(timerList);
        speedMeter.setTimerTasks(timerTaskList);
    }

    private void setAverageSpeed() {
        if (latitudeBeforeGpsLoss == 0 || longitudeBeforeGpsLoss == 0) {
            avgSpeed = 0; //valore fittizio nel caso in cui non ci sia campo all'avvio
        } else {
            Location locationBeforeGpsLoss = new Location("beforeLoss");
            locationBeforeGpsLoss.setLatitude(latitudeBeforeGpsLoss);
            locationBeforeGpsLoss.setLongitude(longitudeBeforeGpsLoss);

            Location locationAfterGpsRestart = new Location("afterRestart");
            locationAfterGpsRestart.setLatitude(latitudeAfterGpsRestart);
            locationAfterGpsRestart.setLongitude(longitudeAfterGpsRestart);

            float distance = locationBeforeGpsLoss.distanceTo(locationAfterGpsRestart);
            avgSpeed = (distance * 1000) / (timeAfterGpsRestart - timeBeforeGpsLoss);
        }
    }

    private void setAvgGraphEntries() {
        for (float time = timeBeforeGpsLoss; time < timeAfterGpsRestart; time = time + 10000) {
            speedMeter.setGraphEntry(time, avgSpeed);
        }
    }

    public void setMoveTime(Boolean moveTime) {
        this.moveTime = moveTime;
    }

    public void setStopTime(Boolean stopTime) {
        this.stopTime = stopTime;
    }
}
