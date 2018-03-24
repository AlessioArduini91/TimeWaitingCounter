package tacchella.arduini.com.time_waiting_counter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alessio on 17/03/18.
 */

public class ResultsActivity extends AppCompatActivity {
    private List<Entry> chartEntries = new ArrayList<Entry>();
    private List<LegendEntry> legendEntries = new ArrayList<LegendEntry>();
    private LegendEntry unitOfMeasureLegend = new LegendEntry();
    private LegendEntry noValueLegend = new LegendEntry();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        unitOfMeasureLegend.formColor = getResources().getColor(R.color.customActionBarColor);
        unitOfMeasureLegend.label = getString(R.string.legendLabel);
        unitOfMeasureLegend.form = Legend.LegendForm.CIRCLE;
        noValueLegend.formColor = getResources().getColor(R.color.noValueColor);
        noValueLegend.label = getString(R.string.noValueLabel);
        noValueLegend.form = Legend.LegendForm.CIRCLE;
        legendEntries.add(unitOfMeasureLegend);
        legendEntries.add(noValueLegend);
        LineChart chart = (LineChart) findViewById(R.id.chart);
        chartEntries = MainActivity.getChartEntries();
        ImageButton backToHome = findViewById(R.id.home);
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.resetChronometer();
                finish();
            }
        });
        LineDataSet dataSet = new LineDataSet(chartEntries, getString(R.string.legendLabel));
        dataSet.setColor(getResources().getColor(R.color.customActionBarColor));
        dataSet.setLineWidth(2f);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);
        LineData lineData = new LineData(dataSet);
        Legend legend = chart.getLegend();
//        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setCustom(legendEntries);

        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMinimum(0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(8f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setTextSize(8f);
        yAxis.setAxisMinimum(0f); // start at zero
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false); // no grid lines
        chart.getDescription().setText("");
        chart.setBackgroundColor(getResources().getColor(R.color.gaugeBackGround));
        chart.setHighlightPerDragEnabled(false);
        chart.setHighlightPerTapEnabled(false);
        chart.getAxisRight().setEnabled(false); // no right axis
        chart.setData(lineData);
        chart.invalidate();

    }
}
