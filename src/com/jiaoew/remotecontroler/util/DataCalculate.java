package com.jiaoew.remotecontroler.util;

import java.util.*;

import org.afree.data.general.Series;
import org.afree.data.time.Minute;
import org.afree.data.time.TimeSeries;

import android.text.TextUtils;

import com.jiaoew.remotecontroler.model.RoomInfoModel;
import com.jiaoew.remotecontroler.model.TemperatureRecordModel;

public class DataCalculate {
	static final double EARTH_RADIUS = 6378.137;
	public static final int MINUTE = 60 * 1000;
	public static final int HOUR = 60 * MINUTE;
	public static final int DAY = 24 * HOUR;
	public static final int VALUE_COUNT = 20;
	
	RoomInfoModel mModel = null;
	List<TemperatureRecordModel> mRecordList = null;
	Date startDate = null;
	
	private List<Date> xValue = new ArrayList<Date>();
	private List<Double> yValue = new ArrayList<Double>();
	
	public DataCalculate(Date startDate, RoomInfoModel model) {
		super();
		mModel = model;
		this.startDate = startDate;
		double step = 10;
		Random rand = new Random();
		for (int i = 0; i < VALUE_COUNT; i++) {
			Date date = new Date((long) (startDate.getTime() + step * i * MINUTE));
			xValue.add(date);
			double d = rand.nextDouble();
			yValue.add(d);
		}
	}
	public DataCalculate(List<TemperatureRecordModel> list) {
		mRecordList = list;
		Date now = new Date();
		for (TemperatureRecordModel model : mRecordList) {
			long time = model.getDate().getTime();
			Date date = new Date(time);
			xValue.add(date);
			yValue.add(model.getTemperature());
		}
	}
	public List<Date> getXValue() {
		return xValue;
	}
	public List<Double> getYValue() {
		return yValue;
	}
	public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lon1) - rad(lon2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + 
				Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s *= EARTH_RADIUS;
		return Math.round(s * 10000) / 10000;
	}
	public static void getSeriesByList(List<TemperatureRecordModel> recordList, TimeSeries series) {
//        Collections.reverse(recordList);
		Date now = new Date();
		for (TemperatureRecordModel model : recordList) {
			Date tDate = new Date(model.getDate().getTime());
			if ((now.getTime() - tDate.getTime()) < 5 * HOUR)
				series.addOrUpdate(new Minute(tDate), model.getTemperature());
		}
	}
	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}
}
