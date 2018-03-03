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

import com.github.anastr.speedviewlib.ProgressiveGauge;

public class MainActivity extends AppCompatActivity implements SpeedMeterManager.SpeedMeterInterface {

    Button goToResult;
    ProgressiveGauge speedView;
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
    long percentMoving=3, percentStopping=4;

    private static long lastPause = SystemClock.elapsedRealtime();

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

        toggleTextView(movingChrono, movingTextView);
        toggleTextView(stoppingChrono, stoppingTextView);
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

        speedView = (ProgressiveGauge) findViewById(R.id.speedView);
        speedView.setSpeedometerColor(getResources().getColor(R.color.customActionBarColor));
        //inizializzo componenti
        goToResult = findViewById(R.id.goToResult);

        final SpeedMeterManager speedMeterManager = new SpeedMeterManager(this);

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

        goToResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchStartButton=true;
                resetChronometer();
                Intent myIntent = new Intent(MainActivity.this, ResultsActivity.class);

                myIntent.putExtra("timeMoving", movingChrono.getBase());
                myIntent.putExtra("timeStopping", stoppingChrono.getBase());
                myIntent.putExtra("percentMoving", percentMoving);
                myIntent.putExtra("percentStopping", percentStopping);

                startActivity(myIntent);
            }
        });

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
            if (textView.getId() == R.id.text_view_moving){
                textView.setText(R.string.timeMove);
            }
            else {
                textView.setText(R.string.timeStop);
            }
        }
        else {
            if (textView.getId() == R.id.text_view_moving){
                textView.setText(R.string.percentMove);
            }
            else {
                textView.setText(R.string.percentStopping);
            }
        }
    }

    public static void toggleChronometer(Boolean command){

        if(command) {
            movingChrono.setBase(movingChrono.getBase() + SystemClock.elapsedRealtime() - lastPause);
            movingChrono.start();
            lastPause = SystemClock.elapsedRealtime();
            stoppingChrono.stop();
        }
        else {
            stoppingChrono.setBase(stoppingChrono.getBase() + SystemClock.elapsedRealtime() - lastPause);
            stoppingChrono.start();
            lastPause = SystemClock.elapsedRealtime();
            movingChrono.stop();
        }

    }

    public static void resetChronometer(){

        //imposto il cronometro come Stop
        lastPause = SystemClock.elapsedRealtime();
        movingChrono.stop();
        stoppingChrono.stop();


        //reset tempo
        movingChrono.setBase(SystemClock.elapsedRealtime());
        stoppingChrono.setBase(SystemClock.elapsedRealtime());

    }

    @Override
    public void setSpeedView(float speed) {
        speedView.speedTo(Math.round(speed * 360)/100);

    }
}