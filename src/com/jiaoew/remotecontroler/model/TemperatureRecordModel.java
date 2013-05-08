package com.jiaoew.remotecontroler.model;

import java.sql.Timestamp;

public class TemperatureRecordModel {

	public static final String TEMPERATURE = "temperature";
	public static final String RECORD_TIME = "time";
	private double temperature;
	private Timestamp date;
	public Timestamp getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = Timestamp.valueOf(date);
	}
	public double getTemperature() {
		return temperature;
	}
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
}
