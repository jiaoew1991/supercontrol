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
	public static final String NAME = "name";
	public static final String TEMPERATURE = "temperature";
	public static final String OLD_TEMP = "old_temp";
	public static final String VOTE_NUM = "vote_num";
	private long id;
	private double latitude;
	private double longitude;
	private String name;
	private double temperature;
	private double oldTemperature;
	private int voteNumber;
	public double getTemperature() {
		return temperature;
	}
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	public int getVoteNumber() {
		return voteNumber;
	}
	public void setVoteNumber(int voteNumber) {
		this.voteNumber = voteNumber;
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
	public double getOldTemperature() {
		return oldTemperature;
	}
	public void setOldTemperature(double oldTemperature) {
		this.oldTemperature = oldTemperature;
	}
}
