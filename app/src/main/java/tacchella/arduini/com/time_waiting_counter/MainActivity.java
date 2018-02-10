package tacchella.arduini.com.time_waiting_counter;

import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.widget.Toast;
import android.support.annotation.NonNull;

public class MainActivity extends AppCompatActivity implements ChronometerFragmentList.OnFragmentEventListener {

    Chronometer timerChronometerMove, timerChronometerStop;
    Button startChronometer, resetChronometer;
    TextView speedView;
    TextView chronoView;
    final int ACCESS_FINE_LOCATION_REQUEST_CODE = 5;
    //boolean usato per scambiare tra pulsante avvio e pulsante stop
    boolean switchStartButton=true;
    //long usato per far si che dopo la pausa il pulsante ritorni a contare dal punto in cui si era fermato
    private long lastPause;

    @Override
    public void toggleChronoInfo(int toggle) {
        Log.d("index", "ciao");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speedView = (TextView) findViewById(R.id.speedView);
        //inizializzo componenti
        timerChronometerMove = findViewById(R.id.timerChronometerMove);
        timerChronometerStop = findViewById(R.id.timerChronometerStop);
        startChronometer = findViewById(R.id.startChronometer);
        resetChronometer = findViewById(R.id.resetChronometer);

        final SpeedMeterManager speedMeterManager = new SpeedMeterManager(speedView, getString(R.string.speed_unit_of_measure));

        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_REQUEST_CODE);
        }
        else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, speedMeterManager.locationListener);
        }


        lastPause = SystemClock.elapsedRealtime();

        startChronometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchStartButton) {

                    startChronometer.setText(getString(R.string.stopChronometer));
                    startChronometer();

                }
                else{
                    startChronometer.setText(getString(R.string.startChronometer));
                    stopChronometer();
                }
                switchStartButton=!switchStartButton;
            }
        });

        resetChronometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resetChronometer();

            }
        });

    }

    private void startChronometer(){

        timerChronometerMove.setBase(timerChronometerMove.getBase() + SystemClock.elapsedRealtime() - lastPause);
        timerChronometerStop.setBase(timerChronometerStop.getBase() + SystemClock.elapsedRealtime() - lastPause);

        timerChronometerMove.start();
        timerChronometerStop.start();

    }

    private void stopChronometer(){

        lastPause = SystemClock.elapsedRealtime();

        timerChronometerMove.stop();
        timerChronometerStop.stop();

    }

    private void resetChronometer(){

        //imposto il cronometro come Stop
        startChronometer.setText(getString(R.string.startChronometer));
        lastPause = SystemClock.elapsedRealtime();
        timerChronometerMove.stop();
        timerChronometerStop.stop();
        switchStartButton=true;

        //reset tempo
        timerChronometerMove.setBase(SystemClock.elapsedRealtime());
        timerChronometerStop.setBase(SystemClock.elapsedRealtime());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //reset tempo
        timerChronometerMove.setBase(SystemClock.elapsedRealtime());
        timerChronometerStop.setBase(SystemClock.elapsedRealtime());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_REQUEST_CODE:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), getString(R.string.access_fine_location_granted), Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), getString(R.string.access_fine_location_denied), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

}