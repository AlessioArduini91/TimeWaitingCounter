package tacchella.arduini.com.time_waiting_counter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Context;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import tacchella.arduini.com.time_waiting_counter.SupportClasses.SpeedMeterManager;

public class MainActivity extends AppCompatActivity implements SpeedMeterManager.SpeedMeterInterface {

    Button goToResult;
    ImageButton stop;
    ImageButton reload;
    ProgressiveGauge speedView;
    TextView movingTextView;
    TextView stoppingTextView;
    TextView percentMovingTextView;
    TextView percentStoppingTextView;
    LocationManager locationManager;
    static AnimationDrawable gpsAnimation;
    static ImageView gpsImage;
    static Chronometer movingChrono;
    static Chronometer stoppingChrono;
    List<Timer> timers;
    List<TimerTask> timerTasks;

    LinearLayout movingLayout;
    LinearLayout stoppingLayout;
    private int animationDuration;
    final int ACCESS_FINE_LOCATION_REQUEST_CODE = 5;

    long percentMoving=0, percentStopping=0;
    Boolean testStart = false;
    private static Boolean isStart = false;
    private static Boolean isStop = false;
    private static Boolean gpsSearching = false;
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
        gpsImage = (ImageView) findViewById(R.id.gps);
        gpsImage.setImageDrawable(getDrawable(R.drawable.animation_gps));
        gpsAnimation = (AnimationDrawable) gpsImage.getDrawable();
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

//        Button testStartButton = (Button) findViewById(R.id.starttest);
//        Button testResetButton = (Button) findViewById(R.id.resettest);

        speedView = (ProgressiveGauge) findViewById(R.id.speedView);
        goToResult = findViewById(R.id.goToResult);
        stop = findViewById(R.id.stop);
        reload = findViewById(R.id.reload);
        speedMeterManager = new SpeedMeterManager(this);


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
//                stopChronometers();
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

        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_REQUEST_CODE);
        }
        else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, speedMeterManager.locationListener);
        }

        goToResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openResultActivity = new Intent(MainActivity.this, ResultsActivity.class);

                openResultActivity.putExtra("timeMoving", movingChrono.getText());
                openResultActivity.putExtra("timeStopping", stoppingChrono.getText());
                openResultActivity.putExtra("percentMoving", percentMoving);
                openResultActivity.putExtra("percentStopping", 100-percentMoving);

                startActivity(openResultActivity);
            }
        });

        stop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        reload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                reload();
            }
        });

        movingChrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (stopAlreadyStarted) {
                    percentMoving = (SystemClock.elapsedRealtime() - movingChrono.getBase()) * 100 /
                            (SystemClock.elapsedRealtime() - movingChrono.getBase() + lastPauseStop - stoppingChrono.getBase());
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
                            (SystemClock.elapsedRealtime() - stoppingChrono.getBase() + lastPauseStart - movingChrono.getBase());
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
            if (lastPauseStart == 0) {
                movingChrono.setBase(SystemClock.elapsedRealtime());
                if (stopAlreadyStarted) {
                    if (!gpsSearching) {
                        lastPauseStop = SystemClock.elapsedRealtime();
                        stoppingChrono.stop();
                    }
                    gpsSearching = false;
                }
                startAlreadyStarted = true;
            }
            else {
                movingChrono.setBase(movingChrono.getBase() + SystemClock.elapsedRealtime() - lastPauseStart);
                if (!gpsSearching) {
                    lastPauseStop = SystemClock.elapsedRealtime();
                    stoppingChrono.stop();
                }
                gpsSearching = false;
            }
            isStart = true;
            isStop = false;
            movingChrono.start();
        }
        else {
            if (lastPauseStop == 0) {
                stoppingChrono.setBase(SystemClock.elapsedRealtime());
                if (startAlreadyStarted) {
                    if (!gpsSearching) {
                        lastPauseStart = SystemClock.elapsedRealtime();
                        movingChrono.stop();
                    }
                    gpsSearching = false;
                }
                stopAlreadyStarted = true;
            }
            else {
                stoppingChrono.setBase(stoppingChrono.getBase() + SystemClock.elapsedRealtime() - lastPauseStop);
                if (!gpsSearching) {
                    lastPauseStart = SystemClock.elapsedRealtime();
                    movingChrono.stop();
                }
                gpsSearching = false;
            }
            isStart = false;
            isStop = true;
            stoppingChrono.start();
        }
        gpsAnimation.stop();
        gpsAnimation.selectDrawable(0);
    }

    public static void resetChronometers(){
        movingChrono.stop();
        stoppingChrono.stop();
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

    public void stop() {
        movingChrono.stop();
        stoppingChrono.stop();

        for (Timer timer : timers) {
            timer.cancel();
        }

        for (TimerTask timerTask : timerTasks) {
            timerTask.cancel();
        }

        locationManager.removeUpdates(speedMeterManager.locationListener);
        toggleIconVisibility(false);
    }

    public void reload() {
        resetChronometers();
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_REQUEST_CODE);
        }
        else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, speedMeterManager.locationListener);
        }

        toggleIconVisibility(true);
    }

    public static void stopChronometers() {
        if (isStop) {
            lastPauseStop = SystemClock.elapsedRealtime();
            stoppingChrono.stop();
            unlockChronometersForNextLoop();
        }
        else if (isStart) {
            lastPauseStart = SystemClock.elapsedRealtime();
            movingChrono.stop();
            unlockChronometersForNextLoop();
        }
        gpsAnimation.start();
    }

    private static void unlockChronometersForNextLoop() {
        gpsSearching = true;
        speedMeterManager.setStopTime(true);
        speedMeterManager.setMoveTime(true);
    }

    private void toggleIconVisibility(Boolean canStop) {
        if (canStop) {
            stop.setVisibility(View.VISIBLE);
            reload.setVisibility(View.GONE);
            gpsImage.setVisibility(View.VISIBLE);
        } else {
            stop.setVisibility(View.GONE);
            reload.setVisibility(View.VISIBLE);
            gpsImage.setVisibility(View.GONE);
        }
    }

    @Override
    public void setSpeedView(float speed) {
        speedView.speedTo(Math.round(speed * 360)/100);
    }

    @Override
    public void setGraphEntry(float time, float speed) {
        chartEntries.add(new Entry(time/(1000 * 60), Math.round(speed * 360)/100));
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void setTimers(List<Timer> timers) {
        this.timers = timers;
    }

    @Override
    public void setTimerTasks(List<TimerTask> timerTasks) {
        this.timerTasks = timerTasks;
    }

    public static List<Entry> getChartEntries() {
        return chartEntries;
    }
}