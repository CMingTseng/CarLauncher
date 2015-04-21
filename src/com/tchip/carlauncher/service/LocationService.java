package com.tchip.carlauncher.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service {
	private LocationClient mLocationClient;

	private int scanSpan = 1000; // 采集轨迹点间隔(ms)
	private String cityName = "未定位";
	private long cityCode = 123456789;
	private SharedPreferences sharedPreferences;
	private Editor editor;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		InitLocation(LocationMode.Hight_Accuracy, "bd09ll", scanSpan, true);
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
			InputStream inputStream = assetManager.open("city_code");
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
			// 读取百度加密经纬度
			// routeLng = location.getLongitude();
			// routeLat = location.getLatitude();
			cityName = location.getCity();
			cityCode = getCityCodeByName(cityName);

			if ((!cityName.equals("未定位")) && (cityCode != 123456789)) {
				sharedPreferences = getSharedPreferences("CarLauncher",
						getApplicationContext().MODE_PRIVATE);
				editor = sharedPreferences.edit();
				editor.putLong("cityCode", cityCode);

				//
				new Thread(networkTask).start();  
				
				
				// stopSelf();
			}
		}
	}
	
	
	
	Handler handler = new Handler() {  
	    @Override  
	    public void handleMessage(Message msg) {  
	        super.handleMessage(msg);  
	        Bundle data = msg.getData();  
	        String val = data.getString("value");  
	        Log.i("mylog", "请求结果为-->" + val);  
	        // TODO  
	        // UI界面的更新等相关操作  '
	        Toast.makeText(getApplicationContext(), val, Toast.LENGTH_SHORT).show();
	        
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
			try {
				// 介绍：http://blog.csdn.net/qq43599939/article/details/12278641/
				// http://m.weather.com.cn/data/101050101.html
				// http://www.weather.com.cn/data/sk/101010100.html 
				// http://www.weather.com.cn/data/cityinfo/101010100.html
				URL uri = new URL("http://m.weather.com.cn/data/"
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
				
			} catch (Exception e) {
			}
			JSONObject jsonObject;

			try {
				jsonObject = new JSONObject(jsonString)
						.getJSONObject("weatherinfo");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	    	
	        Message msg = new Message();  
	        Bundle data = new Bundle();  
	        data.putString("value", jsonString);  
	        msg.setData(data);  
	        handler.sendMessage(msg);  
	        stopSelf();
	    }  
	};  

	@Override
	public void onDestroy() {
		super.onDestroy();
		mLocationClient.stop();
	}

}