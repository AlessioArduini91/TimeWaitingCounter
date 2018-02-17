package tacchella.arduini.com.time_waiting_counter;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Context;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.widget.Toast;
import android.support.annotation.NonNull;

public class MainActivity extends AppCompatActivity {

    Button startChronometer, goToResult;
    TextView speedView;
    TextView movingTextView;
    TextView stoppingTextView;
    TextView percentMovingTextView;
    TextView percentStoppingTextView;
    static Chronometer movingChrono;
    static Chronometer stoppingChrono;
    LinearLayout movingLayout;
    LinearLayout stoppingLayout;
    private int animationDuration;
    final int ACCESS_FINE_LOCATION_REQUEST_CODE = 5;
    //boolean usato per scambiare tra pulsante avvio e pulsante stop
    boolean switchStartButton=true;
    static boolean started=false;

    static ChronometerManager chronometerManager= null;

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
        startChronometer = findViewById(R.id.startChronometer);
        goToResult = findViewById(R.id.goToResult);


        chronometerManager= new ChronometerManager();

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



        chronometerManager.setLastPause1(SystemClock.elapsedRealtime());
        chronometerManager.setLastPause2(SystemClock.elapsedRealtime());


        startChronometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchStartButton) {

                    startChronometer.setText(getString(R.string.stopChronometer));
                    started=true;

                }
                else{
                    startChronometer.setText(getString(R.string.startChronometer));
                    started=false;
                }
                switchStartButton=!switchStartButton;
            }
        });

        goToResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startChronometer.setText(getString(R.string.startChronometer));
                switchStartButton=true;
                chronometerManager.resetChronometer();

                Intent myIntent = new Intent(MainActivity.this, ResultsActivity.class);
                MainActivity.this.startActivity(myIntent);

            }
        });

    }

    public static boolean getStarted(){
            return started;
    };

    public static Chronometer getChronometer1() {
       return movingChrono;
    }

    public static Chronometer getChronometer2() {
        return stoppingChrono;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //reset tempo
        movingChrono.setBase(SystemClock.elapsedRealtime());
        stoppingChrono.setBase(SystemClock.elapsedRealtime());

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
}