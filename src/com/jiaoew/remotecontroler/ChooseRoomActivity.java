package com.jiaoew.remotecontroler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.jiaoew.remotecontroler.model.RoomInfoModel;
import com.jiaoew.remotecontroler.util.JsonParser;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ChooseRoomActivity extends Activity {

	public static final String ROOM_INFO = "room_info";
	
	private LocationManager lm;
	private List<RoomInfoModel> mModelList;
	private ListAdapter mAdapter;
	
	private ListView mRoomList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_room);
		
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String provider = getProvider();
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			Toast.makeText(this, "", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
			startActivityForResult(intent, 0);
		}
		Location location = lm.getLastKnownLocation(provider);
		updateLocation(location);
		
		mRoomList = (ListView) findViewById(R.id.choose_room_select_list);
		mRoomList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				RoomInfoModel roomModel = mModelList.get(arg2);				
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable(ROOM_INFO, roomModel);
				intent.putExtras(bundle);
				intent.setClass(ChooseRoomActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
	}
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.arg1) {
			case Messages.NETWORK_FAILED:
				Toast.makeText(ChooseRoomActivity.this, "cao", Toast.LENGTH_LONG).show();
				break;
			case Messages.NETWORK_SUCCESS:
				updateRoomList();
				break;
			}
		}
		
	};
	private void updateRoomList() {
		final String ROOM_NAME = "name";
		final String ROOM_TEMPERATURE = "temperature";
		String cantigrade = getResources().getString(R.string.util_centigrade);
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		for (RoomInfoModel model: mModelList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put(ROOM_NAME, model.getName());
			map.put(ROOM_TEMPERATURE, model.getCurTemp() + cantigrade);
			data.add(map);
		}
		mAdapter = new SimpleAdapter(this, data, R.layout.location_linear_item, 
				new String[] { ROOM_NAME, ROOM_TEMPERATURE},
				new int[] { R.id.location_item_name_text, R.id.location_item_temperature_text});
		mRoomList.setAdapter(mAdapter);
	}
	private void updateLocation(final Location location) {
		new Thread() {

			@Override
			public void run() {
				super.run();
				JsonParser parser = new JsonParser(ChooseRoomActivity.this);
				try {
					mModelList = parser.getRoomInfo("http://" + RemoteApp.SERVER_IP + "/location", location);
				} catch (JSONException e) {
					mModelList = null;
					Log.e("Choose", "fetch error");
					e.printStackTrace();
				}
				Message msg = handler.obtainMessage();
				msg.arg1 = (mModelList == null) ? Messages.NETWORK_FAILED : Messages.NETWORK_SUCCESS;
				handler.sendMessage(msg);
			}
			
		}.start();
	}

	private String getProvider() {
		Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return lm.getBestProvider(criteria, true);  
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_choose_room, menu);
		return true;
	}
	class Messages {
		public static final int NETWORK_SUCCESS = 0;
		public static final int NETWORK_FAILED = 1;
	}
}
