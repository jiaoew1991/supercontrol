package com.jiaoew.remotecontroler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.afree.data.time.Minute;
import org.afree.data.time.TimeSeries;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.jiaoew.remotecontroler.model.RoomInfoModel;
import com.jiaoew.remotecontroler.model.TemperatureRecordModel;
import com.jiaoew.remotecontroler.util.DataCalculate;
import com.jiaoew.remotecontroler.util.HttpRequestHelper;
import com.jiaoew.remotecontroler.util.JsonParser;
import com.jiaoew.remotecontroler.view.ChartView;
import com.jiaoew.remotecontroler.view.PostListViewFragment;

public class MainActivity extends Activity {

	private static String VOTE_INFO_SP = "vote_info";
	private static String VOTE_INFO_LAST_TIME_ITEM = "last_vote_time";
	
//	private TextView currentTemprature = null;
	private ChartView mChartView = null;
	private NumberPicker mNumberPicker = null;
	private Button mVoteButton = null;
	
	private RoomInfoModel mRoomInfo;
	private String[] mNumberValues = null;
	private List<TemperatureRecordModel> mRecordList = null;
	
	private TelephonyManager mTelphonyManager = null;
	private SharedPreferences mPreference = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mRoomInfo = (RoomInfoModel) getIntent().getSerializableExtra(PostListViewFragment.ROOM_INFO);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(mRoomInfo.getName());
		mNumberPicker = (NumberPicker) findViewById(R.id.main_vote_number_picker);
		mVoteButton = (Button) findViewById(R.id.main_vote_submit);
		buildUpNumberPicker();
		initVoteButton();
		postTemperatureRecord(mRoomInfo.getId());
		
		mTelphonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mPreference = getSharedPreferences(VOTE_INFO_SP, Context.MODE_PRIVATE);
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.arg1) {
			case Messages.VOTE_SUCCESS:
				Toast.makeText(MainActivity.this, R.string.main_vote_success, Toast.LENGTH_LONG).show();
				SharedPreferences.Editor editor = mPreference.edit();
				editor.putLong(VOTE_INFO_LAST_TIME_ITEM + mRoomInfo.getId(), new Date().getTime());
				editor.commit();
				break;
			case Messages.VOTE_FAILED:
				Toast.makeText(MainActivity.this, R.string.main_vote_failed, Toast.LENGTH_LONG).show();
				break;
			case Messages.RECORD_POST_SUCCESS:
				paintTemperatureChart();
				break;
			case Messages.RECORD_POST_FAILED:
				Toast.makeText(MainActivity.this, R.string.main_temp_record_failed, Toast.LENGTH_LONG).show();
				break;
			}
		}
		
	};

	private void initVoteButton() {
		mVoteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				long str = mPreference.getLong(VOTE_INFO_LAST_TIME_ITEM + mRoomInfo.getId(), 0L);
				Date lastTime = new Date(str);
				Date now = new Date();
				if (now.getTime() - lastTime.getTime() > RemoteApp.MIN_VOTE_DELAY_TIME) {
					int index = mNumberPicker.getValue();
					uploadVoteTemprature(Double.parseDouble(mNumberValues[index]));
				} else {
					String failStr = getResources().getString(R.string.main_vote_too_short);
					long time =  now.getTime() - lastTime.getTime();
					time = time > RemoteApp.MIN_VOTE_DELAY_TIME ? 0 : time;
					time = (RemoteApp.MIN_VOTE_DELAY_TIME - time) / 1000 / 60;
					String s = String.format(failStr, time + 1);
					Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	private void buildUpNumberPicker() {
		mNumberValues = new String[RemoteApp.MAX_VOTE_TEMPERATURE * 10 - RemoteApp.MIN_VOTE_TEMPERATURE * 10 + 1];
		for (int i = 0; i < mNumberValues.length; i++) {
			mNumberValues[i] = (double)i / 10.0 + RemoteApp.MIN_VOTE_TEMPERATURE + "";
		}
		mNumberPicker.setMaxValue(mNumberValues.length - 1);
		mNumberPicker.setMinValue(0);
		mNumberPicker.setDisplayedValues(mNumberValues);
		double temp = mRoomInfo.getOldTemperature();
		if (temp > RemoteApp.MAX_VOTE_TEMPERATURE) {
			temp = RemoteApp.MAX_VOTE_TEMPERATURE;
		} else if (temp < RemoteApp.MIN_VOTE_TEMPERATURE) {
			temp = RemoteApp.MIN_VOTE_TEMPERATURE;
		}
		mNumberPicker.setValue((int) ((temp - RemoteApp.MIN_VOTE_TEMPERATURE) * 10));
		mNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
	}
	private void paintTemperatureChart() {
		LinearLayout container = (LinearLayout) findViewById(R.id.main_chart_container);
		if (mChartView == null) {
			TimeSeries series = new TimeSeries("Temperature");
			DataCalculate.getSeriesByList(mRecordList, series);
			Date now = new Date();
			Date later = new Date(now.getTime() + 15 * DataCalculate.MINUTE);
			series.addOrUpdate(new Minute(now), mRoomInfo.getOldTemperature());
			series.addOrUpdate(new Minute(later), mRoomInfo.getTemperature());
			mChartView = new ChartView(this, series);
			container.addView(mChartView);
		} else {
			mChartView.repaint();
		}
	}

	private void uploadVoteTemprature(final double temp) {
		new Thread(){

			@Override
			public void run() {
				super.run();
				HttpRequestHelper request = new HttpRequestHelper("http://" + RemoteApp.SERVER_IP + "/vote");
				List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
				data.add(new BasicNameValuePair("roomId", mRoomInfo.getId() + ""));
				data.add(new BasicNameValuePair("temperature", temp + ""));
				data.add(new BasicNameValuePair("phoneId", mTelphonyManager.getDeviceId()));
				String result = request.doPostJSONResult(data);
				Message msg = handler.obtainMessage();
				msg.arg1 = result.startsWith("success") ? Messages.VOTE_SUCCESS : Messages.VOTE_FAILED;
				handler.sendMessage(msg);
			}
			
		}.start();
	}
	private void postTemperatureRecord(final long l) {
		new Thread() {


			@Override
			public void run() {
				super.run();
				HttpRequestHelper request = new HttpRequestHelper("http://" + RemoteApp.SERVER_IP + "/record");
				List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
				data.add(new BasicNameValuePair("roomId", l + ""));
				String result = request.doPostJSONResult(data);
				JsonParser parser = new JsonParser(MainActivity.this);
				Message msg = handler.obtainMessage();
				msg.arg1 = Messages.RECORD_POST_FAILED;
				try {
					mRecordList = parser.getRecordInfo(result);
					msg.arg1 = Messages.RECORD_POST_SUCCESS;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}
			
		}.start();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, ChooseRoomActivity.class);  
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
			startActivity(intent);  
			return true;  
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	class Messages {
		public static final int VOTE_SUCCESS = 2;
		public static final int VOTE_FAILED = 4;
		public static final int RECORD_POST_SUCCESS = 5;
		public static final int RECORD_POST_FAILED = 6;
	}

}
