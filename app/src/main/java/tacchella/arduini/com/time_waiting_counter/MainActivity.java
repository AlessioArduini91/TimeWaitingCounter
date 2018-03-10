package tacchella.arduini.com.time_waiting_counter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.CountDownTimer;
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

    long percentMoving=0, percentStopping=0;
    Boolean test = false;
    private static long totalBaseTime;
    private static long lastPauseStart;
    private static long lastPauseStop;
    private static boolean stopAlreadyStarted = false;
    private static boolean startAlreadyStarted = false;

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
//        Button testButton = (Button) findViewById(R.id.test);
//
//        testButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                toggleChronometer(test);
//                test = !(test);
//            }
//        });
//
        toggleTextView(true, movingTextView);
        toggleTextView(true, stoppingTextView);
        movingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVisibility(movingChrono, movingTextView, percentMovingTextView);
            }
        });

        stoppingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVisibility(stoppingChrono, stoppingTextView, percentStoppingTextView);
            }
        });

        speedView = (ProgressiveGauge) findViewById(R.id.speedView);
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
//                resetChronometer();
                Intent myIntent = new Intent(MainActivity.this, ResultsActivity.class);

                myIntent.putExtra("timeMoving", movingChrono.getText());
                myIntent.putExtra("timeStopping", stoppingChrono.getText());
                myIntent.putExtra("percentMoving", percentMoving);
                myIntent.putExtra("percentStopping", 100-percentMoving);


                startActivity(myIntent);
            }
        });

        movingChrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (stopAlreadyStarted) {
                    percentMoving = (SystemClock.elapsedRealtime() - movingChrono.getBase()) * 100 /
                            ((SystemClock.elapsedRealtime() - totalBaseTime));
                    percentMovingTextView.setText(percentMoving + "%");
                    percentStoppingTextView.setText(100 - percentMoving + "%");
                } else {
                    percentMovingTextView.setText("100%");
                    percentMoving=100;
                    percentStoppingTextView.setText ("0%");
                    percentStopping=0;
                }
            }
        });

        stoppingChrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (startAlreadyStarted) {
                    percentStopping = (SystemClock.elapsedRealtime() - stoppingChrono.getBase()) * 100 /
                            ((SystemClock.elapsedRealtime() - totalBaseTime));
                    percentStoppingTextView.setText(percentStopping + "%");
                    percentMovingTextView.setText(100 - percentStopping + "%");
                } else {
                    percentStoppingTextView.setText("100%");
                    percentStopping=100;
                    percentMovingTextView.setText ("0%");
                    percentMoving=0;
                }
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

    private void toggleVisibility(final Chronometer chrono, final TextView label, final TextView percent){

        if (chrono.getVisibility() == View.VISIBLE){
            toggleTextView(false, label);
            percent.setVisibility(View.VISIBLE);
            chrono.setVisibility(View.GONE);
            percent.setAlpha(0f);
            percent.animate()
                    .alpha(1f)
                    .setDuration(animationDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {

                            new CountDownTimer(1500,500) {
                                @Override
                                public void onTick(long l) {

                                }

                                @Override
                                public void onFinish() {
                                    percent.setVisibility(View.GONE);
                                    chrono.setVisibility(View.VISIBLE);
                                    toggleTextView(true, label);
                                }
                            }.start();
                        }
                    });
        }

    }

    private void toggleTextView(Boolean mustBeChronometerVisible, TextView label) {
        if (label.getId() == R.id.text_view_moving){
            if (mustBeChronometerVisible) {
                label.setText(R.string.timeMove);
            }
            else {
                label.setText(R.string.percentMove);
            }

        }
        else {
            if (mustBeChronometerVisible) {
                label.setText(R.string.timeStop);
            }
            else {
                label.setText(R.string.percentStopping);
            }
        }
    }

    public static void toggleChronometer(Boolean command){

        if (command) {
            if (lastPauseStart == 0) {
                movingChrono.setBase(SystemClock.elapsedRealtime());
                if (stopAlreadyStarted) {
                    lastPauseStop = SystemClock.elapsedRealtime();
                }
                else {
                    totalBaseTime = SystemClock.elapsedRealtime();
                }
                startAlreadyStarted = true;
            }
            else {
                movingChrono.setBase(movingChrono.getBase() + SystemClock.elapsedRealtime() - lastPauseStart);
                lastPauseStop = SystemClock.elapsedRealtime();
            }
            movingChrono.start();
            stoppingChrono.stop();
        }
        else {
            if (lastPauseStop == 0) {
                stoppingChrono.setBase(SystemClock.elapsedRealtime());
                if (startAlreadyStarted) {
                    lastPauseStart = SystemClock.elapsedRealtime();
                }
                else {
                    totalBaseTime = SystemClock.elapsedRealtime();
                }
                stopAlreadyStarted = true;
            }
            else {
                stoppingChrono.setBase(stoppingChrono.getBase() + SystemClock.elapsedRealtime() - lastPauseStop);
                lastPauseStart = SystemClock.elapsedRealtime();
            }
            stoppingChrono.start();
            movingChrono.stop();
        }
    }

    public static void resetChronometer(){

       totalBaseTime=0;
       lastPauseStart=0;
       lastPauseStop=0;
       stopAlreadyStarted = false;
       startAlreadyStarted = false;
        //imposto il cronometro come Stop
        //lastPause = SystemClock.elapsedRealtime();
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