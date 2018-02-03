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
    boolean switchStartButton=true;
    private long lastPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //inizializzo componenti
        timerChronometerMove= (Chronometer) findViewById(R.id.timerChronometerMove);
        timerChronometerStop= (Chronometer) findViewById(R.id.timerChronometerStop);

        startChronometer= (Button) findViewById(R.id.startChronometer);
        resetChronometer= (Button) findViewById(R.id.resetChronometer);

        //imposto il format del timer mettendo la scritta "secondi"
        timerChronometerMove.setFormat("%s s");
        timerChronometerStop.setFormat("%s s");

        lastPause = SystemClock.elapsedRealtime();

        //reset timer dei cronometri dalla memoria
        timerChronometerMove.setBase(SystemClock.elapsedRealtime());
        timerChronometerStop.setBase(SystemClock.elapsedRealtime());



        startChronometer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(switchStartButton) {
                    startChronometer.setText(getString(R.string.stopChronometer));

                    timerChronometerMove.setBase(timerChronometerMove.getBase() + SystemClock.elapsedRealtime() - lastPause);
                    timerChronometerStop.setBase(timerChronometerStop.getBase() + SystemClock.elapsedRealtime() - lastPause);

                    timerChronometerMove.start();
                    timerChronometerStop.start();
                }
                else{
                    startChronometer.setText(getString(R.string.startChronometer));
                    lastPause = SystemClock.elapsedRealtime();
                    timerChronometerMove.stop();
                    timerChronometerStop.stop();
                }
                switchStartButton=!switchStartButton;
            }
        });

        resetChronometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //imposto il cronometro come Stop
                startChronometer.setText(getString(R.string.startChronometer));
                timerChronometerMove.stop();
                timerChronometerStop.stop();
                switchStartButton=true;

                //reset tempo
                timerChronometerMove.setBase(SystemClock.elapsedRealtime());
                timerChronometerStop.setBase(SystemClock.elapsedRealtime());


            }
        });

   }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //reset tempo
        timerChronometerMove.setBase(SystemClock.elapsedRealtime());
        timerChronometerStop.setBase(SystemClock.elapsedRealtime());

    }


}
