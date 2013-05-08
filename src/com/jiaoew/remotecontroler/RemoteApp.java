package com.jiaoew.remotecontroler;

import android.app.Application;

public class RemoteApp extends Application{

	public static final String SERVER_IP = "59.78.35.22:8887";
	public static final int MAX_VOTE_TEMPERATURE = 28;
	public static final int MIN_VOTE_TEMPERATURE = 22;
	public static final int MIN_VOTE_DELAY_TIME = 15 * 60 * 1000;
}
