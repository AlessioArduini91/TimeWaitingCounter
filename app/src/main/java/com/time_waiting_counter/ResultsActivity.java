package com.time_waiting_counter;

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

import com.time_waiting_counter.R;

import com.time_waiting_counter.SupportClasses.ChartFormatter;

/**
 * Created by alessio on 17/03/18.
 */

public class ResultsActivity extends AppCompatActivity {
    private List<Entry> chartEntries = new ArrayList<Entry>();
    private List<LegendEntry> legendEntries = new ArrayList<LegendEntry>();
    private LegendEntry unitOfMeasureLegend = new LegendEntry();
    private LegendEntry noValueLegend = new LegendEntry();
    private LegendEntry lowValuesLegend = new LegendEntry();
    private LegendEntry middleValuesLegend = new LegendEntry();
    private LegendEntry highValuesLegend = new LegendEntry();
    private int dataSetColor;
    private int lowValuesColor;
    private int middleValuesColor;
    private int highValuesColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        dataSetColor = getResources().getColor(R.color.lineChartColor);
        lowValuesColor = getResources().getColor(R.color.lowValuesColor);
        middleValuesColor = getResources().getColor(R.color.middleValuesColor);
        highValuesColor = getResources().getColor(R.color.highValuesColor);

        unitOfMeasureLegend =
                ChartFormatter.formatLegendEntry(getResources().getColor(R.color.customActionBarColor), getString(R.string.legendLabel));
        noValueLegend =
                ChartFormatter.formatLegendEntry(getResources().getColor(R.color.noValueColor), getString(R.string.noValueLabel));
        lowValuesLegend =
                ChartFormatter.formatLegendEntry(getResources().getColor(R.color.lowValuesColor), getString(R.string.lowValuesLabel));
        middleValuesLegend =
                ChartFormatter.formatLegendEntry(getResources().getColor(R.color.middleValuesColor), getString(R.string.middleValuesLabel));
        highValuesLegend =
                ChartFormatter.formatLegendEntry(getResources().getColor(R.color.highValuesColor), getString(R.string.highValuesLabel));

        legendEntries.add(lowValuesLegend);
        legendEntries.add(middleValuesLegend);
        legendEntries.add(highValuesLegend);
        //legendEntries.add(unitOfMeasureLegend);
        //(legendEntries.add(noValueLegend);

        LineChart chart = (LineChart) findViewById(R.id.chart);
        chartEntries = MainActivity.getChartEntries();
        Legend legend = chart.getLegend();
        legend.setCustom(legendEntries);

        ImageButton backToHome = findViewById(R.id.home);
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LineDataSet formattedDataSet =
                ChartFormatter.formatLineDataSet(chartEntries, null, dataSetColor, lowValuesColor, middleValuesColor, highValuesColor, 1f );
        formattedDataSet.setCircleRadius(2f);
        LineData lineData = new LineData(formattedDataSet);
        XAxis xAxis = chart.getXAxis();
        YAxis yAxis = chart.getAxisLeft();
        ChartFormatter.formatXAxis(xAxis);
        ChartFormatter.formatYAxis(yAxis);

        ChartFormatter.formatChart(chart, getResources().getColor(R.color.chartBackground), lineData);
    }
}
