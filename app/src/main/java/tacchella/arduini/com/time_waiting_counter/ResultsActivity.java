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

    TextView timeMovingTV, timeStoppingTV;
    TextView percentageMovingTV, percentageStoppingTV;
    String timeMoving, timeStopping;
    long percentageMoving, percentageStopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        //inizializzo componenti
        percentageStoppingTV=findViewById(R.id.percentageStop);
        percentageMovingTV=findViewById(R.id.percentageMove);
        timeStoppingTV=findViewById(R.id.timeStop);
        timeMovingTV=findViewById(R.id.timeMove);

        //riempio valori
        timeMoving=(String)getIntent().getExtras().get("timeMoving");
        timeStopping=(String)getIntent().getExtras().get("timeStopping");
        percentageMoving=(long)getIntent().getExtras().get("percentMoving");
        percentageStopping=(long)getIntent().getExtras().get("percentStopping");

        //stampo su schermo valori

        percentageMovingTV.setText(String.valueOf(percentageMoving)+'%');
        percentageStoppingTV.setText(String.valueOf(percentageStopping)+'%');

        timeStoppingTV.setText(String.valueOf(timeStopping));
        timeMovingTV.setText(String.valueOf(timeMoving));

        //inizializzo gli oggetti
        backToHome= findViewById(R.id.backToHome);

        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.resetChronometer();
                Intent myIntent = new Intent(ResultsActivity.this, MainActivity.class);
                startActivity(myIntent);
            }
        });
    }
}
