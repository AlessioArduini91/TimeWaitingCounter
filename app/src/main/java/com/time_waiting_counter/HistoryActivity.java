package com.time_waiting_counter;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.time_waiting_counter.RoomInterfaces.DayDao;
import com.time_waiting_counter.RoomModels.Day;
import com.time_waiting_counter.WaitingCounterDatabase.WaitingCounterDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alessio on 24/04/18.
 */

public class HistoryActivity extends AppCompatActivity {

    private WaitingCounterDatabase waitingCounterDatabase;
    private DayDao counterDayDao;
    private String dateAsString;
    private long initialMovingTime;
    private long initialStoppingTime;
    private long lastPauseStart;
    private long lastPauseStop;
    private TextView dayHeader;
    private LinearLayout rowDayMovingLayout;
    private TextView rowDayMovingTitle;
    private TextView rowDayMovingTime;
    private TextView rowDayMovingPercent;
    private LinearLayout rowDayStoppingLayout;
    private TextView rowDayStoppingTitle;
    private TextView rowDayStoppingTime;
    private TextView rowDayStoppingPercent;

    private Day day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dayHeader = (TextView) findViewById(R.id.day_header);
        rowDayMovingLayout = (LinearLayout) findViewById(R.id.list_row_moving_day);
        rowDayMovingTitle = (TextView) rowDayMovingLayout.findViewById(R.id.rowTitle);
        rowDayMovingTime = (TextView) rowDayMovingLayout.findViewById(R.id.rowTime);
        rowDayMovingPercent = (TextView) rowDayMovingLayout.findViewById(R.id.rowPercent);
        rowDayStoppingLayout = (LinearLayout) findViewById(R.id.list_row_stopping_day);
        rowDayStoppingTitle = (TextView) rowDayStoppingLayout.findViewById(R.id.rowTitle);
        rowDayStoppingTime = (TextView) rowDayStoppingLayout.findViewById(R.id.rowTime);
        rowDayStoppingPercent = (TextView) rowDayStoppingLayout.findViewById(R.id.rowPercent);

        Bundle historyBundle = getIntent().getExtras();

        dateAsString = historyBundle.getString("dayDate");

        waitingCounterDatabase = Room.databaseBuilder(getApplicationContext(),
                WaitingCounterDatabase.class, "counter-database").allowMainThreadQueries().build();
        counterDayDao = waitingCounterDatabase.dayDao();
        day = counterDayDao.getDayByDate(dateAsString);
        dayHeader.setText(dateAsString);
        if (day != null) {
            rowDayMovingTitle.setText(getString(R.string.timeMove));
            rowDayMovingTime.setText(day.getFormattedMoveTime());
            rowDayStoppingTitle.setText(getString(R.string.timeStop));
            rowDayStoppingTime.setText(day.getFormattedStopTime());
        }
    }
}

