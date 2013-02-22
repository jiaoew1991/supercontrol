package com.jiaoew.remotecontroler;

import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import com.jiaoew.remotecontroler.model.RoomInfoModel;
import com.jiaoew.remotecontroler.util.ChartBuilder;
import com.jiaoew.remotecontroler.util.DataCalculate;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.res.Resources;
import android.view.Menu;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

//	private SensorManager mSensor = null;
//	private Sensor temperatureSensor = null;
	private TextView currentTemprature = null;
	private GraphicalView mChartView = null;
//	private boolean isSupport;
	private RoomInfoModel mRoomInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mRoomInfo = (RoomInfoModel) getIntent().getSerializableExtra(ChooseRoomActivity.ROOM_INFO);
		currentTemprature = (TextView) findViewById(R.id.main_current_temperature_text);
		currentTemprature.setText(mRoomInfo.getCurTemp() + getResources().getString(R.string.util_centigrade));
		paintTemperatureChart();
//		mSensor = (SensorManager) getSystemService(SENSOR_SERVICE);
//		temperatureSensor = mSensor.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.arg1) {
			case Messages.RESPONES_FAILED:
				Toast.makeText(MainActivity.this, R.string.connect_internet_error, Toast.LENGTH_LONG).show();
				break;
			case Messages.RESPONSE_SUCCESS:
//				if (!isSupport) {
					currentTemprature.setText(mRoomInfo.getCurTemp() + getResources().getString(R.string.util_centigrade));
					paintTemperatureChart();
//				}
				break;
			}
		}
		
	};
	@Override
	protected void onResume() {
		super.onResume();
//		isSupport = mSensor.registerListener(mSensorListener, temperatureSensor, SensorManager.SENSOR_DELAY_UI);
//		new Thread() {
//
//			@Override
//			public void run() {
//				super.run();
//				try {
//					JsonParser jsonParser = new JsonParser(MainActivity.this);
//					model = jsonParser.getTeperature("http://" + RemoteApp.SERVER_IP + "/temperature/get");
//				} catch (JSONException e) {
//					model = null;
//					e.printStackTrace();
//				}
//				Message msg = handler.obtainMessage();
//				msg.arg1 = (model == null) ? Messages.RESPONES_FAILED : Messages.RESPONSE_SUCCESS;
//				handler.sendMessage(msg);
//			}
//			
//		}.start();
	}

	private void paintTemperatureChart() {
		LinearLayout container = (LinearLayout) findViewById(R.id.main_chart_container);
		if (mChartView == null) {
			DataCalculate calc = new DataCalculate(new Date(), mRoomInfo);
			List<Date> x = calc.getXValue();
			List<Double> y = calc.getYValue();
			XYMultipleSeriesRenderer renderer = ChartBuilder.buildDefaultRenderer();
			renderer.setZoomButtonsVisible(false);
			renderer.setZoomEnabled(false, true);
			ChartBuilder.setChartSettings(renderer, getResources().getString(R.string.main_chart_title), 
					getResources().getString(R.string.main_chart_x_title), getResources().getString(R.string.main_chart_y_title),
					x.get(0).getTime(), x.get(x.size() - 1).getTime(), 10, 30, Color.LTGRAY, Color.BLACK);
			XYMultipleSeriesDataset dataset = ChartBuilder.buildDefaultDataset("test", x, y);
//			mChartView = ChartFactory.getLineChartView(this, dataset, renderer);
			mChartView = ChartFactory.getTimeChartView(this, dataset, renderer, "hh : mm");
			container.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		} else {
			mChartView.repaint();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	class Messages {
		public static final int RESPONSE_SUCCESS = 0;
		public static final int RESPONES_FAILED = 1;
	}

	public long getRemoteDeviceId() {
		return 0l;
	}
}
