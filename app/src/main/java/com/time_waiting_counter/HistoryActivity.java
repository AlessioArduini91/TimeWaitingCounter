package com.time_waiting_counter;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
    private TextView dayHeader;
    private LinearLayout rowDayMovingLayout;
    private TextView rowDayMovingTitle;
    private TextView rowDayMovingTime;
    private TextView rowDayMovingPercent;
    private LinearLayout rowDayStoppingLayout;
    private TextView rowDayStoppingTitle;
    private TextView rowDayStoppingTime;
    private TextView rowDayStoppingPercent;
    private ImageButton delete;

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
        delete = (ImageButton) findViewById(R.id.delete);

        Bundle historyBundle = getIntent().getExtras();

        dateAsString = historyBundle.getString("dayDate");

        waitingCounterDatabase = Room.databaseBuilder(getApplicationContext(),
                WaitingCounterDatabase.class, "counter-database").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        counterDayDao = waitingCounterDatabase.dayDao();
        day = counterDayDao.getDayByDate(dateAsString);
        dayHeader.setText(dayHeader.getText() + " " + dateAsString);
        rowDayMovingTitle.setText(getString(R.string.timeMove));
        rowDayStoppingTitle.setText(getString(R.string.timeStop));
        if (day != null) {
            rowDayMovingTime.setText(day.getFormattedMoveTime());
            rowDayStoppingTime.setText(day.getFormattedStopTime());
            rowDayMovingPercent.setText(day.getDayMovingPercent() + "%");
            rowDayStoppingPercent.setText(day.getDayStoppingPercent() + "%");
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (day != null) {
                    counterDayDao.delete(day);
                    recreate();
                }
            }
        });
    }

}

