package tacchella.arduini.com.time_waiting_counter.SupportClasses;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

/**
 * Created by alessio on 27/03/18.
 */

public class ChartFormatter {
    public static LegendEntry formatLegendEntry(int legendColor, String label) {
        LegendEntry legendEntry = new LegendEntry();
        legendEntry.formColor = legendColor;
        legendEntry.label = label;
        legendEntry.form = Legend.LegendForm.CIRCLE;
        return legendEntry;
    }

    public static LineDataSet formatLineDataSet(List<Entry> chartEntries, String label, int dataSetColor, float width) {
        LineDataSet lineDataSet = new LineDataSet(chartEntries, label);
        lineDataSet.setColor(dataSetColor);
        lineDataSet.setLineWidth(width);
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawCircles(false);
        return lineDataSet;
    }

    public static void formatXAxis(XAxis xAxis) {
        xAxis.setAxisMinimum(0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(8f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
    }

    public static void formatYAxis(YAxis yAxis) {
        yAxis.setAxisMinimum(0f);
        yAxis.setTextSize(8f);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);
    }

    public static void formatChart(LineChart lineChart, int backgroundColor, LineData lineData) {
        lineChart.getDescription().setText("");
        lineChart.setBackgroundColor(backgroundColor);
        lineChart.setHighlightPerDragEnabled(false);
        lineChart.setHighlightPerTapEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
}
