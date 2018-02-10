package tacchella.arduini.com.time_waiting_counter;


import android.os.SystemClock;
import android.widget.Chronometer;

public class ChronometerManager {


    static Chronometer timerChronometerMove, timerChronometerStop;

    //long usato per far si che dopo la pausa il pulsante ritorni a contare dal punto in cui si era fermato
    static private long lastPause1, lastPause2;

    public ChronometerManager(){
        timerChronometerMove=MainActivity.getChronometer1();
        timerChronometerStop=MainActivity.getChronometer2();
    }


    public static void Chronometer1(){

        timerChronometerMove.setBase(timerChronometerMove.getBase() + SystemClock.elapsedRealtime() - lastPause1);
        timerChronometerMove.start();
        lastPause2 = SystemClock.elapsedRealtime();
        timerChronometerStop.stop();


    }

    public static void Chronometer2(){

        timerChronometerStop.setBase(timerChronometerStop.getBase() + SystemClock.elapsedRealtime() - lastPause2);
        timerChronometerStop.start();

        lastPause1 = SystemClock.elapsedRealtime();
        timerChronometerMove.stop();


    }

    public static void resetChronometer(){

        //imposto il cronometro come Stop
        lastPause1 = SystemClock.elapsedRealtime();
        lastPause2 = SystemClock.elapsedRealtime();
        timerChronometerMove.stop();
        timerChronometerStop.stop();


        //reset tempo
        timerChronometerMove.setBase(SystemClock.elapsedRealtime());
        timerChronometerStop.setBase(SystemClock.elapsedRealtime());

    }

    public static void setLastPause1(long a){
        lastPause1=a;
    }
    public static void setLastPause2(long a){
        lastPause2=a;
    }

    public static void stopAll(){

        //imposto il cronometro come Stop
        lastPause1 = SystemClock.elapsedRealtime();
        lastPause2 = SystemClock.elapsedRealtime();
        timerChronometerMove.stop();
        timerChronometerStop.stop();

    }

}
