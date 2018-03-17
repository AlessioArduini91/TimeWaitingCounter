package tacchella.arduini.com.time_waiting_counter;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by alessio on 04/02/18.
 */

public class SpeedMeterManager {

    private float speed = (float) 0.0;
    boolean moveTime=true;
    boolean stopTime=true;
    SpeedMeterInterface speedMeter;
    Timer timer = new Timer();
    float timerSeconds;
    final long TIMER_INTERVAL = 10000;

    interface SpeedMeterInterface {
        void setSpeedView(float speed);
        void setGraphEntry(float time, float speed);
    }

    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

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

    public SpeedMeterManager(Context context){
        speedMeter = (SpeedMeterInterface) context;
        speedMeter.setSpeedView(speed);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                speedMeter.setGraphEntry(timerSeconds, speed);
                timerSeconds += 10000;
            }
        },0, TIMER_INTERVAL);
    }
}
