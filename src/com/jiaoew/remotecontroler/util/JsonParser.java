package com.jiaoew.remotecontroler.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.telephony.TelephonyManager;

import com.jiaoew.remotecontroler.MainActivity;
import com.jiaoew.remotecontroler.RemoteApp;
import com.jiaoew.remotecontroler.model.RoomInfoModel;

public class JsonParser {

	private Context mContext;
	public JsonParser(Context context) {
		mContext = context;
	}
	public List<RoomInfoModel> getRoomInfo(String url, Location location) throws JSONException {
		HttpRequestHelper request = new HttpRequestHelper();
		request.setRequestUri(url);
		String serverRst = request.postJsonResult(makePostRoomData(location));
		
		List<RoomInfoModel> list = new ArrayList<RoomInfoModel>();
		JSONArray json = new JSONArray(serverRst);
		for (int i = 0; i < json.length(); i++) {
			JSONObject jo = json.getJSONObject(i);
			RoomInfoModel model = new RoomInfoModel();
			model.setId(jo.getLong(RoomInfoModel.ID));
			model.setName(jo.getString(RoomInfoModel.NAME));
			model.setLatitude(jo.getDouble(RoomInfoModel.LATITUDE));
			model.setLongitude(jo.getDouble(RoomInfoModel.LONGTITUDE));
			model.setCurTemp(jo.getDouble(RoomInfoModel.CURRENT_TEMPERATURE));
			model.setTargetTemp(jo.getDouble(RoomInfoModel.TARGET_TEMPERATURE));
			model.setDelayMinute(jo.getInt(RoomInfoModel.DELAY_MINUTE));
			list.add(model);
		}
		return list;
	}
	private List<NameValuePair> makePostRoomData(Location location) {
		List<NameValuePair> data = new ArrayList<NameValuePair>();
		data.add(new BasicNameValuePair("latitude", location.getLatitude() + ""));
		data.add(new BasicNameValuePair("longitude", location.getLongitude() + ""));
		return data;
	}
//	public TemperatureModel getTeperature(String url) throws JSONException {
//		TemperatureModel model = new TemperatureModel();
//		HttpRequestHelper request = new HttpRequestHelper();
//		request.setRequestUri(url);
//		String serverResult = request.postJsonResult(makePostData());
//		JSONObject json = new JSONObject(serverResult);
//		model.setCurTemp(json.getDouble(TemperatureModel.CURRENT_TEMPERATURE));
//		model.setTargetTemp(json.getDouble(TemperatureModel.TARGET_TEMPERATURE));
//		model.setDelayMinute(json.getInt(TemperatureModel.DELAY_MINUTE));
//		return model;
//	}
//	private List<NameValuePair> makePostData() {
//		List<NameValuePair> data = new ArrayList<NameValuePair>();
//		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
//		NameValuePair phoneId = new BasicNameValuePair(RemoteApp.PHONE_ID, tm.getDeviceId());
////		NameValuePair deviceId = new BasicNameValuePair(RemoteApp.DEVEICE_ID, ((MainActivity) mContext).getRemoteDeviceId() + "");
////		NameValuePair targetTemp = new BasicNameValuePair("targetTemp", 23 + "");
//		data.add(phoneId);
////		data.add(deviceId);
////		data.add(targetTemp);
//		return data;
//	}
}
