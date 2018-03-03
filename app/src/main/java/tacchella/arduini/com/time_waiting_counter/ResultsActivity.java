package tacchella.arduini.com.time_waiting_counter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

public class ResultsActivity  extends AppCompatActivity {

    Button backToHome;

    Chronometer timeMovingCM, timeStoppingCM;
    TextView percentageMovingTV, percentageStoppingTV, totalTimeTV;
    long timeMoving, timeStopping, percentageMoving, percentageStopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        //inizializzo componenti
        percentageStoppingTV=findViewById(R.id.percentageStop);
        percentageMovingTV=findViewById(R.id.percentageMove);
        timeStoppingCM=findViewById(R.id.timeStop);
        timeMovingCM=findViewById(R.id.timeMove);
        totalTimeTV=findViewById(R.id.totalTime);

        //riempio valori
        timeMoving=(long)getIntent().getExtras().get("timeMoving");
        timeStopping=(long)getIntent().getExtras().get("timeStopping");
        percentageMoving=(long)getIntent().getExtras().get("percentMoving");
        percentageStopping=(long)getIntent().getExtras().get("percentStopping");

        //stampo su schermo valori

        percentageMovingTV.setText(String.valueOf(percentageMoving));
        percentageStoppingTV.setText(String.valueOf(percentageStopping));
        timeStoppingCM.setBase(timeStopping);
        timeMovingCM.setBase(timeMoving);


        //inizializzo gli oggetti
        backToHome= findViewById(R.id.backToHome);

        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ResultsActivity.this, MainActivity.class);
                startActivity(myIntent);
            }
        });
    }
}
