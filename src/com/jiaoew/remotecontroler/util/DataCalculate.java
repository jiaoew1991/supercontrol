package com.jiaoew.remotecontroler.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.text.TextUtils;

import com.jiaoew.remotecontroler.model.RoomInfoModel;

public class DataCalculate {
	static final double EARTH_RADIUS = 6378.137;
	static final int MINUTE = 60 * 1000;
	static final int VALUE_COUNT = 20;
	RoomInfoModel mModel = null;
	Date startDate = null;
	public DataCalculate(Date startDate, RoomInfoModel model) {
		super();
		mModel = model;
		this.startDate = startDate;
	}
	public List<Date> getXValue() {
		List<Date> rst = new ArrayList<Date>();
		double step = ((double)mModel.getDelayMinute()) / VALUE_COUNT;
		for (int i = 0; i < VALUE_COUNT; i++) {
			Date date = new Date((long) (startDate.getTime() + step * i * MINUTE));
			rst.add(date);
		}
		return rst;
	}
	public List<Double> getYValue() {
		List<Double> rst = new ArrayList<Double>();
		Random rand = new Random();
		for (int i = 0; i < VALUE_COUNT; i++) {
			double d = rand.nextDouble() * (mModel.getCurTemp() - mModel.getTargetTemp()) +
					mModel.getTargetTemp();
			rst.add(d);
		}
		return rst;
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
	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}
}
