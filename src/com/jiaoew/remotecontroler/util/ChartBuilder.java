package com.jiaoew.remotecontroler.util;

import java.util.Date;
import java.util.List;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.graphics.Paint.Align;

public class ChartBuilder {

	public static XYMultipleSeriesRenderer buildDefaultRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 20, 30, 15, 20 });
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(Color.GREEN);
		r.setPointStyle(PointStyle.CIRCLE);
		r.setFillPoints(true);
		renderer.addSeriesRenderer(r);
		
		renderer.setXLabels(12);
		renderer.setYLabels(10);
		renderer.setShowGrid(true);
		renderer.setXLabelsAlign(Align.RIGHT);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setPanLimits(new double[] { 0, 20, 0, 35 });
		renderer.setZoomLimits(new double[] { 0, 20, 0, 35 });
		return renderer;
	}
	public static void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
			String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
	}
	public static XYMultipleSeriesDataset buildDefaultDataset(String title, List<Date> xValue, List<Double> yValue) {
		XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();
		TimeSeries xy = new TimeSeries(title);
		for (int i = 0; i < xValue.size(); i++) {
			xy.add(xValue.get(i), yValue.get(i));
		}
		dataSet.addSeries(xy);
		return dataSet;
	}
}
