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
    private TextView rowDayMovingTitle;
    private  TextView rowWeekMovingTitle;
    private TextView rowDayMovingTime;
    private TextView rowWeekMovingTime;
    private TextView rowDayMovingPercent;
    private TextView rowWeekMovingPercent;
    private LinearLayout rowDayStoppingLayout;
    private LinearLayout rowWeekStoppingLayout;
    private TextView rowDayStoppingTitle;
    private TextView rowWeekStoppingTitle;
    private TextView rowDayStoppingTime;
    private TextView rowWeekStoppingTime;
    private TextView rowDayStoppingPercent;
    private TextView rowWeekStoppingPercent;
    private ImageButton delete;
    private long weekMovingTime;
    private long weekStoppingTime;

    private Day day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        dayHeader = (TextView) findViewById(R.id.day_header);
        rowDayMovingLayout = (LinearLayout) findViewById(R.id.list_row_moving_day);
        rowWeekMovingLayout = (LinearLayout) findViewById(R.id.list_row_moving_week);
        rowDayMovingTitle = (TextView) rowDayMovingLayout.findViewById(R.id.rowTitle);
        rowWeekMovingTitle = (TextView) rowWeekMovingLayout.findViewById(R.id.rowTitle);
        rowDayMovingTime = (TextView) rowDayMovingLayout.findViewById(R.id.rowTime);
        rowWeekMovingTime = (TextView) rowWeekMovingLayout.findViewById(R.id.rowTime);
        rowDayMovingPercent = (TextView) rowDayMovingLayout.findViewById(R.id.rowPercent);
        rowWeekMovingPercent = (TextView) rowWeekMovingLayout.findViewById(R.id.rowPercent);
        rowDayStoppingLayout = (LinearLayout) findViewById(R.id.list_row_stopping_day);
        rowWeekStoppingLayout = (LinearLayout) findViewById(R.id.list_row_stopping_week);
        rowDayStoppingTitle = (TextView) rowDayStoppingLayout.findViewById(R.id.rowTitle);
        rowWeekStoppingTitle = (TextView) rowWeekStoppingLayout.findViewById(R.id.rowTitle);
        rowDayStoppingTime = (TextView) rowDayStoppingLayout.findViewById(R.id.rowTime);
        rowWeekStoppingTime = (TextView) rowWeekStoppingLayout.findViewById(R.id.rowTime);
        rowDayStoppingPercent = (TextView) rowDayStoppingLayout.findViewById(R.id.rowPercent);
        rowWeekStoppingPercent = (TextView) rowWeekStoppingLayout.findViewById(R.id.rowPercent);
        delete = (ImageButton) findViewById(R.id.delete);

        Bundle historyBundle = getIntent().getExtras();

        dateAsString = historyBundle.getString("dayDate");

        waitingCounterDatabase = Room.databaseBuilder(getApplicationContext(),
                WaitingCounterDatabase.class, "counter-database").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        counterDayDao = waitingCounterDatabase.dayDao();
        day = counterDayDao.getDayByDate(dateAsString);
        dayHeader.setText(dayHeader.getText() + " " + dateAsString);
        rowDayMovingTitle.setText(getString(R.string.timeMove));
        rowWeekMovingTitle.setText(getString(R.string.timeMove));
        rowDayStoppingTitle.setText(getString(R.string.timeStop));
        rowWeekStoppingTitle.setText(getString(R.string.timeStop));
        if (day != null) {
            weekMovingTime = counterDayDao.getWeekMovingTime(day.getWeek(), day.getYear());
            weekStoppingTime = counterDayDao.getWeekStoppingTime(day.getWeek(), day.getYear());
            rowDayMovingTime.setText(getFormattedTime(day.getDayMovingTime()));
            rowDayStoppingTime.setText(getFormattedTime(day.getDayStoppingTime()));
            rowDayMovingPercent.setText(day.getDayMovingPercent() + "%");
            rowDayStoppingPercent.setText(day.getDayStoppingPercent() + "%");
            rowWeekMovingTime.setText(getFormattedTime(weekMovingTime));
            rowWeekStoppingTime.setText(getFormattedTime(weekStoppingTime));
            rowWeekMovingPercent.setText(getWeekMovingPercent() + "%");
            rowWeekStoppingPercent.setText(getWeekStoppingPercent() + "%");
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

    private String getFormattedTime(long timerSeconds) {
        int seconds = (int) (timerSeconds / (1000)) % 60;
        int minutes = (int) (timerSeconds / (1000 * 60)) % 60;
        int hours = (int) (timerSeconds / (1000 * 60 * 60)) % 24;
        int days = (int) (timerSeconds / (1000 * 60 * 60 * 24));

        if (days != 0) {
            if (days == 1) {
                return String.format("%d giorno, %02d : %02d : %02d", days, hours, minutes, seconds);
            } else {
                return String.format("%d giorni, %02d : %02d : %02d", days, hours, minutes, seconds);
            }
        } else {
            return String.format("%02d : %02d : %02d", hours, minutes, seconds);
        }
    }

    public int getWeekMovingPercent() {
        if (getTotalWeekTime() == 0) {
            return 0;
        } else {
            return ((int) weekMovingTime * 100) / getTotalWeekTime();
        }
    }

    public int getWeekStoppingPercent() {
        if (getTotalWeekTime() == 0) {
            return 0;
        } else {
            return 100 - getWeekMovingPercent();
        }
    }

    public int getTotalWeekTime() {
        return (int) weekMovingTime + (int) weekStoppingTime;
    }
}

