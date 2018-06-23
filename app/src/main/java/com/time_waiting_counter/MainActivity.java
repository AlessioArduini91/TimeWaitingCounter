package com.time_waiting_counter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Context;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.FrameLayout;
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
import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.github.anastr.speedviewlib.ProgressiveGauge;
import com.github.mikephil.charting.data.Entry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.time_waiting_counter.R;

import com.time_waiting_counter.RoomInterfaces.DayDao;
import com.time_waiting_counter.RoomModels.Day;
import com.time_waiting_counter.SupportClasses.SpeedMeterManager;
import com.time_waiting_counter.WaitingCounterDatabase.WaitingCounterDatabase;

public class MainActivity extends AppCompatActivity implements SpeedMeterManager.SpeedMeterInterface {

    static ProgressiveGauge speedView;
    static TextView noGpsText;
    FrameLayout gaugeFrame;
    TextView movingTextView;
    TextView stoppingTextView;
    TextView percentMovingTextView;
    TextView percentStoppingTextView;
    TextView totalTimeTextView;
    Toolbar mainToolbar;
    Drawable stopDrawable;
    Drawable reloadDrawable;
    MenuItem historyDrawable;
    MenuItem graphDrawable;
    Button saveDay;
    ImageButton play;
    ImageButton stop;
    ImageButton refresh;
    LocationManager locationManager;
    static AnimationDrawable gpsAnimation;
    static ImageView gpsImage;
    static Chronometer movingChrono;
    static Chronometer stoppingChrono;
    List<Timer> timers;
    List<TimerTask> timerTasks;
    LinearLayout movingLayout;
    LinearLayout stoppingLayout;
    private static int animationDuration;
    final int ACCESS_FINE_LOCATION_REQUEST_CODE = 5;

    long percentMoving=0, percentStopping=0;
    private static Boolean isStart = false;
    private static Boolean isStop = false;
    private static Boolean gpsSearching = false;
    private static long lastPauseStart;
    private static long lastPauseStop;
    private static boolean stopAlreadyStarted = false;
    private static boolean startAlreadyStarted = false;
    private static SpeedMeterManager speedMeterManager;
    private static boolean activityPaused = false;
    private static boolean noGps = false;
    private static boolean isStopped = false;
    private DatePickerDialog datePickerDialog;
    private WaitingCounterDatabase waitingCounterDatabase;
    private DayDao counterDayDao;
    private String dateAsString;
    private String today;
    static List<Entry> chartEntries = new ArrayList<Entry>();
    String format;
    SimpleDateFormat dateFormat;
    Date setDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play = (ImageButton) findViewById(R.id.play);
        stop = (ImageButton) findViewById(R.id.stop);
        refresh = (ImageButton) findViewById(R.id.refresh);
        noGpsText = (TextView) findViewById (R.id.noGpsText);
        gpsImage = (ImageView) findViewById(R.id.gps);
        gpsImage.setImageDrawable(getDrawable(R.drawable.animation_gps));
        gpsAnimation = (AnimationDrawable) gpsImage.getDrawable();
        animationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);
        movingLayout = (LinearLayout) findViewById(R.id.chronoMoveLayout);
        stoppingLayout = (LinearLayout) findViewById(R.id.chronoStopLayout);
        movingTextView = movingLayout.findViewById(R.id.timeText);
        stoppingTextView = stoppingLayout.findViewById(R.id.timeText);
        totalTimeTextView = (TextView) findViewById(R.id.totalTime);
        movingTextView.setId(R.id.text_view_moving);
        stoppingTextView.setId(R.id.text_view_stopping);
        movingChrono = movingLayout.findViewById(R.id.timeChrono);
        stoppingChrono = stoppingLayout.findViewById(R.id.timeChrono);
        percentMovingTextView = movingLayout.findViewById(R.id.timePercent);
        percentStoppingTextView = stoppingLayout.findViewById(R.id.timePercent);
        gaugeFrame = findViewById(R.id.gaugeFrame);
        speedView = (ProgressiveGauge) findViewById(R.id.speedView);
        speedMeterManager = new SpeedMeterManager(this);
        mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        saveDay = (Button) findViewById(R.id.save);
        waitingCounterDatabase = Room.databaseBuilder(getApplicationContext(),
                WaitingCounterDatabase.class, "counter-database").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        counterDayDao = waitingCounterDatabase.dayDao();

        movingLayout.setBackground(getDrawable(R.drawable.chronometer_shape_moving));
        stoppingLayout.setBackground(getDrawable(R.drawable.chronometer_shape_stopping));
        format  = "ddMMyyyy";
        dateFormat = new SimpleDateFormat(format);

        setSupportActionBar(mainToolbar);
        toggleTextView(movingTextView);
        toggleTextView(stoppingTextView);

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        today = String.format("%02d-%02d-%02d",
                day,
                month + 1,
                year);

        datePickerDialog = new DatePickerDialog(this,
                R.style.datepicker,
                new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int selectedYear,
                                  int selectedMonth, int selectedDay) {
                dateAsString = String.format("%02d-%02d-%02d",
                        selectedDay,
                        selectedMonth + 1,
                        selectedYear);

                try {
                    setDate = dateFormat.parse(dateAsString.replace("-",""));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar setDateCalendar = Calendar.getInstance();
                setDateCalendar.setTime(setDate);
                int setWeek = setDateCalendar.get(Calendar.WEEK_OF_YEAR);
                int setMonth = setDateCalendar.get(Calendar.MONTH);
                int setYear = setDateCalendar.get(Calendar.YEAR);

                Bundle historyBundle = new Bundle();
                historyBundle.putString("dayDate", dateAsString);
                historyBundle.putInt("weekDate", setWeek);
                historyBundle.putInt("monthDate", setMonth);
                historyBundle.putInt("yearDate", setYear);
                Intent historyActivityIntent = new Intent(MainActivity.this, HistoryActivity.class);
                historyActivityIntent.putExtras(historyBundle);
                startActivity(historyActivityIntent);
            }
        }, year, month, day);

        saveDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long movingForTime;
                long stoppingForTime;

                if (startAlreadyStarted) {
                    movingForTime = lastPauseStart - movingChrono.getBase();
                } else {
                    movingForTime = 0;
                }

                if (stopAlreadyStarted) {
                    stoppingForTime = lastPauseStop - stoppingChrono.getBase();
                } else {
                    stoppingForTime = 0;
                }

                Day day = counterDayDao.getDayByDate(today);
                if (counterDayDao.getDayByDate(today) == null) {
                    Day newDay = new Day();
                    newDay.setDayDate(today);
                    newDay.setDayMovingTime(movingForTime);
                    newDay.setDayStoppingTime(stoppingForTime);
                    newDay.setDay(calendar.get(Calendar.DAY_OF_WEEK));
                    newDay.setWeek(calendar.get(Calendar.WEEK_OF_YEAR));
                    newDay.setMonth(calendar.get(Calendar.MONTH));
                    newDay.setYear(calendar.get(Calendar.YEAR));
                    counterDayDao.insert(newDay);
                } else {
                    long oldPercentMoving = day.getDayMovingTime();
                    long oldPercentStopping = day.getDayStoppingTime();
                    day.setDayMovingTime(oldPercentMoving + movingForTime);
                    day.setDayStoppingTime(oldPercentStopping + stoppingForTime);
                    counterDayDao.update(day);
                }

                Toast.makeText(getApplicationContext(), getString(R.string.queryCorrect), Toast.LENGTH_LONG).show();
                reload();
            }
        });

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

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheckPlay = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheckPlay != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            ACCESS_FINE_LOCATION_REQUEST_CODE);
                }
                else {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, speedMeterManager.locationListener);
                    speedMeterManager.initTimer();
                    play.setVisibility(View.GONE);
                    stop.setVisibility(View.VISIBLE);
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movingChrono.stop();
                stoppingChrono.stop();
                stop();
                totalTimeTextView.setText(getString(R.string.totalTime) + "\n" + speedMeterManager.getTotalParsedTime());
                totalTimeTextView.setVisibility(View.VISIBLE);
                stop.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
                saveDay.setVisibility(View.VISIBLE);
                historyDrawable.setVisible(true);
                graphDrawable.setVisible(true);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noGps = false;
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
                    percentMovingTextView.setText(R.string.maxPercent);
                    percentMoving=100;
                    percentStoppingTextView.setText (R.string.minPercent);
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
                    percentStoppingTextView.setText(R.string.maxPercent);
                    percentStopping=100;
                    percentMovingTextView.setText(R.string.minPercent);
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
    protected void onPause() {
        activityPaused = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        activityPaused = false;
        super.onResume();
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

    public static void toggleNoGpsVisibility() {
        if (!activityPaused) {
            if (noGps) {
                gpsAnimation.start();
                speedView.setAlpha(0f);
                noGpsText.setAlpha(1f);
            } else {
                gpsAnimation.stop();
                gpsAnimation.selectDrawable(0);
                noGpsText.setAlpha(0f);
                speedView.setAlpha(1f);
            }
        }
    }

    private void toggleVisibility(final Chronometer chrono, final TextView label, final TextView percent){

        if (chrono.getVisibility() == View.VISIBLE){
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
                                }
                            }.start();
                        }
                    });
        }

    }

    private void toggleTextView( TextView label) {
        if (label.getId() == R.id.text_view_moving){
            label.setText(R.string.timeMove);
        }
        else {
            label.setText(R.string.timeStop);
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
        noGps = false;
        toggleNoGpsVisibility();
    }

    public static void resetChronometers(){
        isStopped = false;
        movingChrono.stop();
        stoppingChrono.stop();
        lastPauseStart=0;
        lastPauseStop=0;
        stopAlreadyStarted = false;
        startAlreadyStarted = false;
        chartEntries.clear();
        movingChrono.setBase(SystemClock.elapsedRealtime());
        stoppingChrono.setBase(SystemClock.elapsedRealtime());
    }

    public void stop() {
        isStopped = true;
        for (Timer timer : timers) {
            timer.cancel();
        }

        for (TimerTask timerTask : timerTasks) {
            timerTask.cancel();
        }

        if (isStop) {
            lastPauseStop = SystemClock.elapsedRealtime();
        }
        else if (isStart) {
            lastPauseStart = SystemClock.elapsedRealtime();
        }

        isStop = false;
        isStart = false;
        locationManager.removeUpdates(speedMeterManager.locationListener);
        speedView.setAlpha(0f);
        noGpsText.setAlpha(0f);
        gpsImage.setVisibility(View.GONE);
    }

    public void reload() {
        stop();
        resetChronometers();
        recreate();
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
        isStart = false;
        isStop = false;
        noGps = true;
        toggleNoGpsVisibility();
    }

    private static void unlockChronometersForNextLoop() {
        gpsSearching = true;
        speedMeterManager.setStopTime(true);
        speedMeterManager.setMoveTime(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        historyDrawable = menu.findItem(R.id.action_history);
        graphDrawable = menu.findItem(R.id.action_graph);
        historyDrawable.getIcon().setColorFilter(getResources().getColor(android.R.color.white),  PorterDuff.Mode.SRC_ATOP);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_graph:
                Intent openResultActivity = new Intent(MainActivity.this, ResultsActivity.class);
                startActivity(openResultActivity);
                return true;

            case R.id.action_history:
                datePickerDialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);

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