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
import com.jiaoew.remotecontroler.model.TemperatureRecordModel;

public class JsonParser {

	private Context mContext;
	public JsonParser(Context context) {
		mContext = context;
	}
	public List<RoomInfoModel> getRoomInfo(String str) throws JSONException {
		List<RoomInfoModel> list = new ArrayList<RoomInfoModel>();
		JSONArray json = new JSONArray(str);
		for (int i = 0; i < json.length(); i++) {
			JSONObject jo = json.getJSONObject(i);
			RoomInfoModel model = new RoomInfoModel();
			model.setId(jo.getLong(RoomInfoModel.ID));
			model.setName(jo.getString(RoomInfoModel.NAME));
			model.setLatitude(jo.getDouble(RoomInfoModel.LATITUDE));
			model.setLongitude(jo.getDouble(RoomInfoModel.LONGTITUDE));
			model.setTemperature(jo.getDouble(RoomInfoModel.TEMPERATURE));
			model.setVoteNumber(jo.getInt(RoomInfoModel.VOTE_NUM));
			if (jo.has(RoomInfoModel.OLD_TEMP))
				model.setOldTemperature(jo.getDouble(RoomInfoModel.OLD_TEMP));
			list.add(model);
		}
		return list;
	}
	public List<TemperatureRecordModel> getRecordInfo(String str) throws JSONException {
		List<TemperatureRecordModel> list = new ArrayList<TemperatureRecordModel>();
		JSONArray json = new JSONArray(str);
		for (int i = 0; i < json.length(); i++) {
			JSONObject jo = json.getJSONObject(i);
			TemperatureRecordModel model = new TemperatureRecordModel();
			model.setDate(jo.getString(TemperatureRecordModel.RECORD_TIME));
			model.setTemperature(jo.getDouble(TemperatureRecordModel.TEMPERATURE));
			list.add(model);
		}
		return list;
	}
}
