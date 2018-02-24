package tacchella.arduini.com.time_waiting_counter;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by alessio on 04/02/18.
 */

public class SpeedMeterManager {

    private float speed = (float) 0.0;
    boolean moveTime=true;
    boolean stopTime=true;
    SpeedMeterInterface speedMeter;

    interface SpeedMeterInterface {
        void setSpeedView(float speed);
    }

    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            speed = location.getSpeed();
            speedMeter.setSpeedView(speed);

            //if(MainActivity.getStarted()) {
                if (speed < 3.0 && moveTime) {
                    ChronometerManager.Chronometer1();
                    moveTime = false;
                    stopTime = true;
                } else if (speed >= 3.0 && stopTime) {
                    ChronometerManager.Chronometer2();
                    moveTime = true;
                    stopTime = false;
                }
           /* }
            else{
                ChronometerManager.stopAll();
            }*/

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
    }
}
