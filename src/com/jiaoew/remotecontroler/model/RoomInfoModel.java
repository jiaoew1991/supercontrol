package com.jiaoew.remotecontroler.model;

import java.io.Serializable;

public class RoomInfoModel implements  Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1151192489492085795L;
	
	public static final String ID = "id";
	public static final String LATITUDE = "latitude";
	public static final String LONGTITUDE = "longitude";
	public static String CURRENT_TEMPERATURE = "curTemp";
	public static String TARGET_TEMPERATURE = "targetTemp";
	public static String DELAY_MINUTE = "delayMinute";
	public static final String NAME = "name";
	private long id;
	private double latitude;
	private double longitude;
	private double curTemp;
	private double targetTemp;
	private int delayMinute;
	private String name;
	public double getCurTemp() {
		return curTemp;
	}
	public void setCurTemp(double curTemp) {
		this.curTemp = curTemp;
	}
	public double getTargetTemp() {
		return targetTemp;
	}
	public void setTargetTemp(double targetTemp) {
		this.targetTemp = targetTemp;
	}
	public int getDelayMinute() {
		return delayMinute;
	}
	public void setDelayMinute(int delayMinute) {
		this.delayMinute = delayMinute;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
