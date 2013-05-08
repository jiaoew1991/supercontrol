package com.jiaoew.remotecontroler.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.afree.chart.ChartFactory;
import org.afree.chart.AFreeChart;
import org.afree.chart.axis.DateAxis;
import org.afree.chart.axis.ValueAxis;
import org.afree.chart.plot.Marker;
import org.afree.chart.plot.ValueMarker;
import org.afree.chart.plot.XYPlot;
import org.afree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.afree.chart.renderer.xy.XYSplineRenderer;
import org.afree.data.time.Minute;
import org.afree.data.time.TimeSeries;
import org.afree.data.time.TimeSeriesCollection;
import org.afree.data.xy.XYDataset;
import org.afree.graphics.SolidColor;
import org.afree.ui.Layer;

import android.content.Context;
import android.graphics.Color;

import com.jiaoew.remotecontroler.R;
import com.jiaoew.remotecontroler.RemoteApp;
import com.jiaoew.remotecontroler.util.DataCalculate;

public class ChartView extends DemoView {

	private Context mContext;
	private TimeSeriesCollection mDataset;
	public ChartView(Context context) {
        super(context);
        mContext = context;
        mDataset = (TimeSeriesCollection) createDataset();
        final AFreeChart chart = createChart();
        setChart(chart);
        this.setMaximumDrawHeight(400);
	}
    public ChartView(Context context, TimeSeries series) {
        super(context);
        mContext = context;
        mDataset = new TimeSeriesCollection();
        mDataset.addSeries(series);
        final AFreeChart chart = createChart();
        setChart(chart);
        this.setMaximumDrawHeight(400);
    }
    
	public void addDataset(TimeSeries series) {
		mDataset.addSeries(series);
	}

	private AFreeChart createChart() {
//        mSerierData = createDataset();
        AFreeChart chart = ChartFactory.createTimeSeriesChart(
        		mContext.getResources().getString(R.string.main_chart_title),
        		mContext.getResources().getString(R.string.main_chart_x_title),
        		mContext.getResources().getString(R.string.main_chart_y_title),
                mDataset,
                false,
                true,
                false);
        chart.setBackgroundPaintType(new SolidColor(Color.WHITE));
        
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaintType(new SolidColor(Color.WHITE));
        plot.setDomainGridlinesVisible(false);
        
        Date now = new Date();
        Date later = new Date(now.getTime() + 15 * DataCalculate.MINUTE);
        Date start = new Date(later.getTime() - 5 * DataCalculate.HOUR);
        ValueAxis valueAxis = plot.getRangeAxis();
        DateAxis dateAxis = (DateAxis) plot.getDomainAxis();
        dateAxis.setDateFormatOverride(new SimpleDateFormat("HH : mm", Locale.getDefault()));
        dateAxis.setRange(start, later);

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setRange(RemoteApp.MIN_VOTE_TEMPERATURE, RemoteApp.MAX_VOTE_TEMPERATURE);

        plot.setRenderer(new XYSplineRenderer());
        
        XYLineAndShapeRenderer itemRender = (XYLineAndShapeRenderer) plot.getRenderer();
        itemRender.setBaseShapesVisible(true);
        itemRender.setBaseShapesFilled(true);
        itemRender.setDrawSeriesLineAsPath(true);
        itemRender.setSeriesPaintType(0, new SolidColor(Color.BLUE));
        itemRender.setSeriesStroke(0, 2.0f);
        
        // add range marker
        Marker marker_V_Start = new ValueMarker(now.getTime(), Color.RED, 2.0f);
        Marker marker_V_End = new ValueMarker(now.getTime(), Color.RED, 2.0f);
        plot.addDomainMarker(marker_V_Start, Layer.BACKGROUND);
        plot.addDomainMarker(marker_V_End, Layer.BACKGROUND);
        
        return chart;
    }

    /**
     * Returns a sample dataset.
     * @return A sample dataset.
     */
    private XYDataset createDataset() {
        TimeSeriesCollection result = new TimeSeriesCollection();
        TimeSeries series1 = new TimeSeries("Value");

        Random rand = new Random();
        Date later = new Date(new Date().getTime() + 15 * DataCalculate.MINUTE);
        for (int i = 0; i < 40; i++) {
        	series1.add(new Minute(new Date(later.getTime() - 15 * DataCalculate.MINUTE * i - rand.nextInt(DataCalculate.MINUTE * 2))),
        			rand.nextDouble() + RemoteApp.MIN_VOTE_TEMPERATURE + 4);
        }

        result.addSeries(series1);
        return result;
    }

}
