package tacchella.arduini.com.time_waiting_counter;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by alessio on 04/02/18.
 */

public class SpeedMeterManager {
    Context activityContext;
    private float speed = (float) 0.0;
    private boolean moveTime=true;
    private boolean stopTime=true;
    private boolean checkGps=true;
    SpeedMeterInterface speedMeter;
    TimerTask timerTask;
    TimerTask checkTimerTask;
    Timer timer;
    Timer checkTimer;
    float timerSeconds;
    final long TIMER_INTERVAL = 10000;
    final long TIMER_INTERVAL_GPS = 5000;
    Boolean noSignal = false;

    interface SpeedMeterInterface {
        void setSpeedView(float speed);
        void setGraphEntry(float time, float speed);
    }

    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (!checkGps) {
                checkGps = true;
            }
            noSignal = false;
            speed = location.getSpeed();
            speedMeter.setSpeedView(speed);
                // false = stop
            if (speed < 3.0 && moveTime) {
                MainActivity.toggleChronometer(false);
                moveTime = false;
                stopTime = true;
            } else if (speed >= 3.0 && stopTime) {
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
        initTimer();
    }

    private void initTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                speedMeter.setGraphEntry(timerSeconds, speed);
                timerSeconds += 10000;
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
    }

    public void setMoveTime(Boolean moveTime) {
        this.moveTime = moveTime;
    }

    public void setStopTime(Boolean stopTime) {
        this.stopTime = stopTime;
    }

    public void setTimerSeconds(float timerSeconds) {
        this.timerSeconds = timerSeconds;
    }
}
