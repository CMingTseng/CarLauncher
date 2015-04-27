package com.tchip.carlauncher.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class LocationService extends Service {
	private LocationClient mLocationClient;

	private int scanSpan = 1000; // 采集轨迹点间隔(ms)
	private String cityName = "未定位";
	private long cityCode = 123456789;
	private SharedPreferences preferences;
	private Editor editor;
	private final String WEATHER_PREFIX = "{\"weatherinfo\"";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		InitLocation(LocationMode.Hight_Accuracy, "bd09ll", scanSpan, true);

		preferences = getSharedPreferences("CarLauncher",
				getApplicationContext().MODE_PRIVATE);
		editor = preferences.edit();
	}

	/**
	 * 根据城市名（来自百度）获取城市码（中央气象台）
	 * 
	 * @param cityName
	 * @return cityCode 城市码
	 */
	private long getCityCodeByName(String cityName) {
		// 获取assets中测试json文本
		AssetManager assetManager = getAssets();
		String text;
		try {
			InputStream inputStream = assetManager.open("zms_city_code");
			byte[] buffer = new byte[inputStream.available()];
			inputStream.read(buffer);
			text = new String(buffer, "utf-8");
			try {
				JSONArray jArray = new JSONArray(text);
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject item = jArray.getJSONObject(i);
					if (item.optString("name").equals(cityName))
						return item.getLong("code");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cityCode;
	}

	/**
	 * 
	 * @param tempMode
	 *            LocationMode.Hight_Accuracy-高精度
	 *            LocationMode.Battery_Saving-低功耗
	 *            LocationMode.Device_Sensors-仅设备
	 * @param tempCoor
	 *            gcj02-国测局加密经纬度坐标 bd09ll-百度加密经纬度坐标 bd09-百度加密墨卡托坐标
	 * @param frequence
	 *            MIN_SCAN_SPAN = 1000; MIN_SCAN_SPAN_NETWORK = 3000;
	 * @param isNeedAddress
	 *            是否需要地址
	 */
	private void InitLocation(LocationMode tempMode, String tempCoor,
			int frequence, boolean isNeedAddress) {

		mLocationClient = new LocationClient(this.getApplicationContext());
		mLocationClient.registerLocationListener(new MyLocationListener());
		// mGeofenceClient = new GeofenceClient(getApplicationContext());

		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);
		option.setCoorType(tempCoor);
		option.setScanSpan(frequence);
		option.setIsNeedAddress(isNeedAddress);
		mLocationClient.setLocOption(option);

		mLocationClient.start();
	}

	class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			cityName = location.getCity();
			cityCode = getCityCodeByName(cityName);

			if ((cityName != null) && (!cityName.equals("未定位"))
					&& (cityCode != 123456789)) {

				editor.putLong("cityCode", cityCode);
				editor.putString("cityName", cityName);
				editor.putString("latitude", "" + location.getLatitude());
				editor.putString("longitude", "" + location.getLongitude());
				editor.putString("district", location.getDistrict());
				editor.putString("floor", location.getFloor());
				editor.putString("addrStr", location.getAddrStr());
				editor.putString("street", location.getStreet());
				editor.putString("streetNum", location.getStreetNumber());
				editor.putFloat("speed", location.getSpeed());
				editor.putString("altitude", "" + location.getAltitude());
				editor.putString("lbsTime", location.getTime());
				editor.commit();

				new Thread(networkTask).start();
			}
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// Bundle data = msg.getData();
			// String val = data.getString("value");
			// // UI界面的更新等相关操作
			// Toast.makeText(getApplicationContext(), val, Toast.LENGTH_SHORT)
			// .show();

		}
	};

	/**
	 * 网络操作相关的子线程
	 */
	Runnable networkTask = new Runnable() {

		@Override
		public void run() {
			// TODO
			// 在这里进行 http request.网络请求相关操作

			String jsonString = "get Failed";
			// 1. cityinfo part
			try {

				URL uri = new URL("http://www.weather.com.cn/data/cityinfo/"
						+ cityCode + ".html");
				URLConnection ucon = uri.openConnection();
				InputStream is = ucon.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				ByteArrayBuffer baf = new ByteArrayBuffer(100);
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
				}
				jsonString = new String(baf.toByteArray(), "utf-8");

				if (jsonString.startsWith(WEATHER_PREFIX)) {
					try {
						JSONObject jsonObject;
						jsonObject = new JSONObject(jsonString)
								.getJSONObject("weatherinfo");
						editor.putString("tempHigh",
								jsonObject.getString("temp1"));
						editor.putString("tempLow",
								jsonObject.getString("temp2"));
						editor.putString("weather",
								jsonObject.getString("weather"));
						editor.putString("postTime",
								jsonObject.getString("ptime"));
						editor.commit();

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
			}

			// 2. sk part
			jsonString = "get Failed";
			try {
				URL uri = new URL("http://www.weather.com.cn/data/sk/"
						+ cityCode + ".html");
				URLConnection ucon = uri.openConnection();
				InputStream is = ucon.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				ByteArrayBuffer baf = new ByteArrayBuffer(100);
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
				}
				jsonString = new String(baf.toByteArray(), "utf-8");
				if (jsonString.startsWith(WEATHER_PREFIX)) {
					try {
						JSONObject jsonObject;
						jsonObject = new JSONObject(jsonString)
								.getJSONObject("weatherinfo");
						editor.putString("tempNow",
								jsonObject.getString("temp"));
						editor.putString("windDir", jsonObject.getString("WD"));
						editor.putString("windSpeed",
								jsonObject.getString("WS"));
						editor.putString("wetLevel", jsonObject.getString("SD"));
						editor.commit();

						// stopSelf();

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
			}

		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		mLocationClient.stop();
	}

}