package tacchella.arduini.com.time_waiting_counter;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    String speed = "0.0";

    static final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            location.getSpeed();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView speedView = (TextView) findViewById(R.id.speedView);
        speedView.setText(speed);
    }
}
