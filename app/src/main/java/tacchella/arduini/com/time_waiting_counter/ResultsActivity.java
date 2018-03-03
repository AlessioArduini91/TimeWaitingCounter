package tacchella.arduini.com.time_waiting_counter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultsActivity  extends AppCompatActivity {

    Button backToHome;

    TextView percentageStop;
    TextView percentageMove;
    TextView timeStop;
    TextView timeMove;
    TextView totalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        percentageStop=findViewById(R.id.percentageStop);
        percentageMove=findViewById(R.id.percentageMove);
        timeStop=findViewById(R.id.timeStop);
        timeMove=findViewById(R.id.timeMove);
        totalTime=findViewById(R.id.totalTime);

        //inizializzo gli oggetti
        backToHome= findViewById(R.id.backToHome);

        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ResultsActivity.this, MainActivity.class);
                ResultsActivity.this.startActivity(myIntent);
            }
        });
    }
}
