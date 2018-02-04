package tacchella.arduini.com.time_waiting_counter;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

public class MainActivity extends AppCompatActivity {

    Chronometer timerChronometerMove, timerChronometerStop;
    Button startChronometer, resetChronometer;

    //boolean usato per scambiare tra pulsante avvio e pulsante stop
    boolean switchStartButton=true;
    //long usato per far si che dopo la pausa il pulsante ritorni a contare dal punto in cui si era fermato
    private long lastPause;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);


        //inizializzo componenti
        timerChronometerMove= findViewById(R.id.timerChronometerMove);
        timerChronometerStop= findViewById(R.id.timerChronometerStop);

        startChronometer= findViewById(R.id.startChronometer);
        resetChronometer= findViewById(R.id.resetChronometer);

        lastPause = SystemClock.elapsedRealtime();


            startChronometer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(switchStartButton) {

                    startChronometer.setText(getString(R.string.stopChronometer));
                    startChronometer();

                }
                else{
                    startChronometer.setText(getString(R.string.startChronometer));
                    stopChronometer();
                }
                switchStartButton=!switchStartButton;
            }
        });

        resetChronometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resetChronometer();

            }
        });

   }

    private void startChronometer(){

        timerChronometerMove.setBase(timerChronometerMove.getBase() + SystemClock.elapsedRealtime() - lastPause);
        timerChronometerStop.setBase(timerChronometerStop.getBase() + SystemClock.elapsedRealtime() - lastPause);

        timerChronometerMove.start();
        timerChronometerStop.start();

    }

    private void stopChronometer(){

        lastPause = SystemClock.elapsedRealtime();

        timerChronometerMove.stop();
        timerChronometerStop.stop();

    }

    private void resetChronometer(){

        //imposto il cronometro come Stop
        startChronometer.setText(getString(R.string.startChronometer));
        lastPause = SystemClock.elapsedRealtime();
        timerChronometerMove.stop();
        timerChronometerStop.stop();
        switchStartButton=true;

        //reset tempo
        timerChronometerMove.setBase(SystemClock.elapsedRealtime());
        timerChronometerStop.setBase(SystemClock.elapsedRealtime());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //reset tempo
        timerChronometerMove.setBase(SystemClock.elapsedRealtime());
        timerChronometerStop.setBase(SystemClock.elapsedRealtime());

    }

}