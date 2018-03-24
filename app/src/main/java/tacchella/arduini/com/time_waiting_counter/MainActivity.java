package tacchella.arduini.com.time_waiting_counter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
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
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    Boolean testStart = false;
    private static Boolean isStart = false;
    private static Boolean isStop = false;
    private static long totalBaseTime;
    private static long lastPauseStart;
    private static long lastPauseStop;
    private static boolean stopAlreadyStarted = false;
    private static boolean startAlreadyStarted = false;
    private static SpeedMeterManager speedMeterManager;
    static List<Entry> chartEntries = new ArrayList<Entry>();

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

        //Button testStartButton = (Button) findViewById(R.id.starttest);
//        Button testResetButton = (Button) findViewById(R.id.resettest);

//        testStartButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                toggleChronometer(testStart);
//                testStart = !(testStart);
//            }
//        });
//
//        testResetButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                resetChronometer();
//            }
//        });

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

        speedMeterManager = new SpeedMeterManager(this);

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

                Intent openResultActivity = new Intent(MainActivity.this, ResultsActivity.class);

                openResultActivity.putExtra("timeMoving", movingChrono.getText());
                openResultActivity.putExtra("timeStopping", stoppingChrono.getText());
                openResultActivity.putExtra("percentMoving", percentMoving);
                openResultActivity.putExtra("percentStopping", 100-percentMoving);

                startActivity(openResultActivity);
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
            isStart = true;
            isStop = false;
            if (lastPauseStart == 0) {
                movingChrono.setBase(SystemClock.elapsedRealtime());
                if (stopAlreadyStarted) {
                    lastPauseStop = SystemClock.elapsedRealtime();
                    stoppingChrono.stop();
                }
                else {
                    totalBaseTime = SystemClock.elapsedRealtime();
                }
                startAlreadyStarted = true;
            }
            else {
                movingChrono.setBase(movingChrono.getBase() + SystemClock.elapsedRealtime() - lastPauseStart);
                lastPauseStop = SystemClock.elapsedRealtime();
                stoppingChrono.stop();
            }
            movingChrono.start();
        }
        else {
            isStart = false;
            isStop = true;
            if (lastPauseStop == 0) {
                stoppingChrono.setBase(SystemClock.elapsedRealtime());
                if (startAlreadyStarted) {
                    lastPauseStart = SystemClock.elapsedRealtime();
                    movingChrono.stop();
                }
                else {
                    totalBaseTime = SystemClock.elapsedRealtime();
                }
                stopAlreadyStarted = true;
            }
            else {
                stoppingChrono.setBase(stoppingChrono.getBase() + SystemClock.elapsedRealtime() - lastPauseStop);
                lastPauseStart = SystemClock.elapsedRealtime();
                movingChrono.stop();
            }
            stoppingChrono.start();
        }
    }

    public static void resetChronometer(){
        movingChrono.stop();
        stoppingChrono.stop();
        totalBaseTime=0;
        lastPauseStart=0;
        lastPauseStop=0;
        stopAlreadyStarted = false;
        startAlreadyStarted = false;
        speedMeterManager.setTimerSeconds(0f);
        chartEntries.clear();
        movingChrono.setBase(SystemClock.elapsedRealtime());
        stoppingChrono.setBase(SystemClock.elapsedRealtime());
        speedMeterManager.createNewTimer();
        speedMeterManager.setStopTime(true);
        speedMeterManager.setMoveTime(true);

    }

    public static void stopChronometers() {
        if (isStop) {
            lastPauseStop = SystemClock.elapsedRealtime();
            stoppingChrono.stop();

        }
        else if (isStart) {
            lastPauseStart = SystemClock.elapsedRealtime();
            movingChrono.stop();
        }
        speedMeterManager.setStopTime(true);
        speedMeterManager.setMoveTime(true);
    }
//
//    public static void reStartChronometers() {
//        if (isStop) {
//            toggleChronometer(false);
//        }
//        else if (isStart) {
//            toggleChronometer(true);
//        }
//    }

    @Override
    public void setSpeedView(float speed) {
        speedView.speedTo(Math.round(speed * 360)/100);
    }

    @Override
    public void setGraphEntry(float time, float speed) {
        chartEntries.add(new Entry(time/1000, Math.round(speed * 360)/100));
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public static List<Entry> getChartEntries() {
        return chartEntries;
    }
}