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
import android.widget.Toast;

import com.time_waiting_counter.RoomInterfaces.DayDao;
import com.time_waiting_counter.RoomModels.Day;
import com.time_waiting_counter.SupportClasses.DataTimeFormatter;
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
    private LinearLayout rowWeekMovingLayout;
    private LinearLayout rowMonthMovingLayout;
    private TextView rowDayMovingTitle;
    private TextView rowWeekMovingTitle;
    private TextView rowMonthMovingTitle;
    private TextView rowDayMovingTime;
    private TextView rowWeekMovingTime;
    private TextView rowMonthMovingTime;
    private TextView rowDayMovingPercent;
    private TextView rowWeekMovingPercent;
    private TextView rowMonthMovingPercent;
    private LinearLayout rowDayStoppingLayout;
    private LinearLayout rowWeekStoppingLayout;
    private LinearLayout rowMonthStoppingLayout;
    private TextView rowDayStoppingTitle;
    private TextView rowWeekStoppingTitle;
    private TextView rowMonthStoppingTitle;
    private TextView rowDayStoppingTime;
    private TextView rowWeekStoppingTime;
    private TextView rowMonthStoppingTime;
    private TextView rowDayStoppingPercent;
    private TextView rowWeekStoppingPercent;
    private TextView rowMonthStoppingPercent;
    private ImageButton delete;
    private long weekMovingTime;
    private long weekStoppingTime;
    private long monthMovingTime;
    private long monthStoppingTime;
    private int selectedWeek;
    private int selectedMonth;
    private int selectedYear;
    private Day day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        dayHeader = (TextView) findViewById(R.id.day_header);
        rowDayMovingLayout = (LinearLayout) findViewById(R.id.list_row_moving_day);
        rowWeekMovingLayout = (LinearLayout) findViewById(R.id.list_row_moving_week);
        rowMonthMovingLayout = (LinearLayout) findViewById(R.id.list_row_moving_month);
        rowDayMovingTitle = (TextView) rowDayMovingLayout.findViewById(R.id.rowTitle);
        rowWeekMovingTitle = (TextView) rowWeekMovingLayout.findViewById(R.id.rowTitle);
        rowMonthMovingTitle = (TextView) rowMonthMovingLayout.findViewById(R.id.rowTitle);
        rowDayMovingTime = (TextView) rowDayMovingLayout.findViewById(R.id.rowTime);
        rowWeekMovingTime = (TextView) rowWeekMovingLayout.findViewById(R.id.rowTime);
        rowMonthMovingTime = (TextView) rowMonthMovingLayout.findViewById(R.id.rowTime);
        rowDayMovingPercent = (TextView) rowDayMovingLayout.findViewById(R.id.rowPercent);
        rowWeekMovingPercent = (TextView) rowWeekMovingLayout.findViewById(R.id.rowPercent);
        rowMonthMovingPercent = (TextView) rowMonthMovingLayout.findViewById(R.id.rowPercent);
        rowDayStoppingLayout = (LinearLayout) findViewById(R.id.list_row_stopping_day);
        rowWeekStoppingLayout = (LinearLayout) findViewById(R.id.list_row_stopping_week);
        rowMonthStoppingLayout = (LinearLayout) findViewById(R.id.list_row_stopping_month);
        rowDayStoppingTitle = (TextView) rowDayStoppingLayout.findViewById(R.id.rowTitle);
        rowWeekStoppingTitle = (TextView) rowWeekStoppingLayout.findViewById(R.id.rowTitle);
        rowMonthStoppingTitle = (TextView) rowMonthStoppingLayout.findViewById(R.id.rowTitle);
        rowDayStoppingTime = (TextView) rowDayStoppingLayout.findViewById(R.id.rowTime);
        rowWeekStoppingTime = (TextView) rowWeekStoppingLayout.findViewById(R.id.rowTime);
        rowMonthStoppingTime = (TextView) rowMonthStoppingLayout.findViewById(R.id.rowTime);
        rowDayStoppingPercent = (TextView) rowDayStoppingLayout.findViewById(R.id.rowPercent);
        rowWeekStoppingPercent = (TextView) rowWeekStoppingLayout.findViewById(R.id.rowPercent);
        rowMonthStoppingPercent = (TextView) rowMonthStoppingLayout.findViewById(R.id.rowPercent);
        delete = (ImageButton) findViewById(R.id.delete);

        Bundle historyBundle = getIntent().getExtras();

        dateAsString = historyBundle.getString("dayDate");
        selectedWeek = historyBundle.getInt("weekDate");
        selectedMonth = historyBundle.getInt("monthDate");
        selectedYear = historyBundle.getInt("yearDate");

        waitingCounterDatabase = Room.databaseBuilder(getApplicationContext(),
                WaitingCounterDatabase.class, "counter-database").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        counterDayDao = waitingCounterDatabase.dayDao();
        day = counterDayDao.getDayByDate(dateAsString);
        dayHeader.setText(dateAsString);
        rowDayMovingTitle.setText(getString(R.string.timeMove));
        rowWeekMovingTitle.setText(getString(R.string.timeMove));
        rowMonthMovingTitle.setText(getString(R.string.timeMove));
        rowDayStoppingTitle.setText(getString(R.string.timeStop));
        rowWeekStoppingTitle.setText(getString(R.string.timeStop));
        rowMonthStoppingTitle.setText(getString(R.string.timeStop));
        if (day != null) {
            rowDayMovingTime.setText(DataTimeFormatter.getFormattedTime(day.getDayMovingTime()));
            rowDayStoppingTime.setText(DataTimeFormatter.getFormattedTime(day.getDayStoppingTime()));
            rowDayMovingPercent.setText(DataTimeFormatter.getMovingPercent((int) day.getDayMovingTime(), (int) day.getDayStoppingTime()) + "%");
            rowDayStoppingPercent.setText(DataTimeFormatter.getStoppingPercent((int) day.getDayMovingTime(), (int) day.getDayStoppingTime()) + "%");
        }

        if (selectedWeek != 0 && selectedYear != 0) {
            weekMovingTime = counterDayDao.getWeekMovingTime(selectedWeek, selectedYear);
            weekStoppingTime = counterDayDao.getWeekStoppingTime(selectedWeek, selectedYear);
            rowWeekMovingTime.setText(DataTimeFormatter.getFormattedTime(weekMovingTime));
            rowWeekStoppingTime.setText(DataTimeFormatter.getFormattedTime(weekStoppingTime));
            rowWeekMovingPercent.setText(DataTimeFormatter.getMovingPercent((int) weekMovingTime, (int) weekStoppingTime) + "%");
            rowWeekStoppingPercent.setText(DataTimeFormatter.getStoppingPercent((int) weekMovingTime, (int) weekStoppingTime) + "%");
        }

        if (selectedMonth != 0 && selectedYear != 0) {
            monthMovingTime = counterDayDao.getMonthMovingTime(selectedMonth, selectedYear);
            monthStoppingTime = counterDayDao.getMonthStoppingTime(selectedMonth, selectedYear);
            rowMonthMovingTime.setText(DataTimeFormatter.getFormattedTime(monthMovingTime));
            rowMonthStoppingTime.setText(DataTimeFormatter.getFormattedTime(monthStoppingTime));
            rowMonthMovingPercent.setText(DataTimeFormatter.getMovingPercent((int) monthMovingTime, (int) monthStoppingTime) + "%");
            rowMonthStoppingPercent.setText(DataTimeFormatter.getStoppingPercent((int) monthMovingTime, (int) monthStoppingTime) + "%");
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (day != null) {
                    counterDayDao.delete(day);
                    Toast.makeText(getApplicationContext(), getString(R.string.deleteDay).replace("[[day]]", dateAsString), Toast.LENGTH_LONG).show();
                    recreate();
                }
            }
        });
    }

}

