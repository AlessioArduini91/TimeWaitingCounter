package tacchella.arduini.com.time_waiting_counter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.widget.Toast;
import android.support.annotation.NonNull;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Chronometer timerChronometerMove, timerChronometerStop;
    Button startChronometer, resetChronometer;
    TextView speedView;
    TextView movingTextView;
    TextView stoppingTextView;
    TextView percentMovingTextView;
    TextView percentStoppingTextView;
    Chronometer movingChrono;
    Chronometer stoppingChrono;
    LinearLayout movingLayout;
    LinearLayout stoppingLayout;
    final int ACCESS_FINE_LOCATION_REQUEST_CODE = 5;
    //boolean usato per scambiare tra pulsante avvio e pulsante stop
    boolean switchStartButton=true;
    //long usato per far si che dopo la pausa il pulsante ritorni a contare dal punto in cui si era fermato
    private long lastPause;
    private int animationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        animationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);
        movingLayout = (LinearLayout) findViewById(R.id.chronoMoveLayout);
        stoppingLayout = (LinearLayout) findViewById(R.id.chronoStopLayout);
        movingTextView = movingLayout.findViewById(R.id.timeText);
        stoppingTextView = stoppingLayout.findViewById(R.id.timeText);
        movingTextView.setId(R.id.text_view_moving);
        stoppingTextView.setId(R.id.text_view_stopping);
        movingChrono = movingLayout.findViewById(R.id.timeChrono);
        stoppingChrono = stoppingLayout.findViewById(R.id.timeChrono);
        percentMovingTextView = movingLayout.findViewById(R.id.timePercent);
        percentStoppingTextView = stoppingLayout.findViewById(R.id.timePercent);

        movingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVisibility(movingChrono, percentMovingTextView);
                toggleTextView(movingChrono, movingTextView);
            }
        });

        stoppingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVisibility(stoppingChrono, percentStoppingTextView);
                toggleTextView(stoppingChrono, stoppingTextView);
            }
        });

        speedView = (TextView) findViewById(R.id.speedView);
        //inizializzo componenti
        //timerChronometerMove = findViewById(R.id.timerChronometerMove);
        //timerChronometerStop = findViewById(R.id.timerChronometerStop);
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

    private void toggleVisibility(Chronometer chrono, TextView percent){

        if (chrono.getVisibility() == View.VISIBLE){
            chrono.setVisibility(View.GONE);
            percent.setVisibility(View.VISIBLE);
            percent.setAlpha(0f);
            percent.animate()
                    .alpha(1f)
                    .setDuration(animationDuration)
                    .setListener(null);
        }
        else{
            percent.setVisibility(View.GONE);
            chrono.setVisibility(View.VISIBLE);
            chrono.setAlpha(0f);
            chrono.animate()
                    .alpha(1f)
                    .setDuration(animationDuration)
                    .setListener(null);
        }

    }

    private void toggleTextView(Chronometer chrono, TextView textView) {
        if (chrono.getVisibility() == View.VISIBLE){
            textView.setText(R.string.timeMove);
        }
        else {
            textView.setText(R.string.percentMove);
        }
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