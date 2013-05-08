package com.jiaoew.remotecontroler.view;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.jiaoew.remotecontroler.MainActivity;
import com.jiaoew.remotecontroler.R;
import com.jiaoew.remotecontroler.model.RoomInfoModel;
import com.jiaoew.remotecontroler.util.DataCalculate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public abstract class PostListViewFragment extends Fragment {

	public static final String ROOM_INFO = "room_info";
	
	protected ListView mRoomListView;
	protected Context mContext;
	private LocationManager lm;
	protected List<RoomInfoModel> mModelList;
	protected Location location;
	private ListAdapter mAdapter;

	protected Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.arg1) {
			case Messages.NETWORK_FAILED:
				Toast.makeText(mContext, R.string.connect_internet_error, Toast.LENGTH_LONG).show();
				break;
			case Messages.NETWORK_SUCCESS:
				updateRoomList();
				break;
			}
		}
		
	};
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
		lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		String provider = getProvider();
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			Toast.makeText(mContext, "", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
			startActivityForResult(intent, 0);
		}
		location = lm.getLastKnownLocation(provider);
		updateLocation(location);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_room, container, false);
        mRoomListView = (ListView) rootView.findViewById(R.id.choose_room_select_list);
        return rootView;
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
	protected void updateRoomList() {
		final String ROOM_NAME = "name";
		final String ROOM_TEMPERATURE = "temperature";
		String cantigrade = getResources().getString(R.string.util_centigrade);
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		for (RoomInfoModel model: mModelList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put(ROOM_NAME, model.getName());
			int dis = (int) DataCalculate.getDistance(location.getLatitude(), location.getLongitude(), model.getLatitude(), model.getLongitude());
			String s = mContext.getResources().getString(R.string.choose_room_distance);
			String s2 = String.format(s, dis+"");
			DecimalFormat format = new DecimalFormat("########0.00");
			if (dis == 0)
				map.put(ROOM_TEMPERATURE, format.format(model.getOldTemperature()) + cantigrade + "(" + mContext.getResources().getString(R.string.choose_room_distance_near) + ")");
			else 
				map.put(ROOM_TEMPERATURE, format.format(model.getOldTemperature()) + cantigrade + "(" + s2 + ")");
			data.add(map);
		}
		mAdapter = new SimpleAdapter(mContext, data, R.layout.location_linear_item, 
				new String[] { ROOM_NAME, ROOM_TEMPERATURE},
				new int[] { R.id.location_item_name_text, R.id.location_item_temperature_text});
		mRoomListView.setAdapter(mAdapter);
	}
	protected abstract void updateLocation(final Location location); 
//	{
//	}
	protected List<NameValuePair> makePostRoomData(Location location) {
		List<NameValuePair> data = new ArrayList<NameValuePair>();
		data.add(new BasicNameValuePair("latitude", location.getLatitude() + ""));
		data.add(new BasicNameValuePair("longitude", location.getLongitude() + ""));
		return data;
	}
	public static class Messages {
		public static final int NETWORK_SUCCESS = 0;
		public static final int NETWORK_FAILED = 1;
	}

}
