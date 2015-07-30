package com.tchip.carlauncher.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.MyApplication;
import com.tchip.carlauncher.adapter.RouteAdapter;
import com.tchip.carlauncher.model.RoutePoint;

public class RouteRecordService extends Service {
	private LocationClient mLocationClient;
	private final String ROUTE_PATH = Constant.Path.ROUTE_TRACK;
	private String startTime = "";
	private String stopTime = "";

	private List<RoutePoint> list = new ArrayList<RoutePoint>();
	private RouteAdapter adapter = new RouteAdapter();

	private int scanSpan = 1000; // 采集轨迹点间隔(ms)
	private final static double ERROR_CODE = 0.0;
	private double routeLng, routeLat;

	private SharedPreferences sharedPreferences;
	private Editor editor;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// Update Running State
		sharedPreferences = getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		InitLocation(LocationMode.Hight_Accuracy, "bd09ll", scanSpan, false);
		// 初始化路径
		File filestoreMusic = new File(ROUTE_PATH);
		if (!filestoreMusic.exists()) {
			filestoreMusic.mkdir();
		}
		startTime = getTimeStr();
		if (Constant.isDebug) {
			Log.v(Constant.TAG, "Start Record Route");
		}
		// 开启轨迹记录线程
		new Thread(new RouteRecordThread()).start();

		return super.onStartCommand(intent, flags, startId);
	}

	public class RouteRecordThread implements Runnable {

		@Override
		public void run() {
			synchronized (recordHandler) {
				do {
					try {
						Thread.sleep(scanSpan);
						Message message = new Message();
						message.what = 1;
						recordHandler.sendMessage(message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} while (MyApplication.isPowerConnect);
			}
		}
	}

	final Handler recordHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				startRecordRoute();
			}
			super.handleMessage(msg);
		}
	};

	private void startRecordRoute() {

		RoutePoint routePoint = new RoutePoint();
		if (routeLng != 0.0 && routeLat != 0.0 && routeLng != 4.9E-324
				&& routeLat != 4.9E-324) {
			if (list.size() > 0
					&& list.get(list.size() - 1).getLat() == routeLat
					&& (list.get(list.size() - 1).getLng() == routeLng)) {
				// 经纬度未改变
				if (Constant.isDebug) {
					Log.v(Constant.TAG, "Location is Not Change.");
				}
			} else {
				routePoint.setLng(routeLng);
				routePoint.setLat(routeLat);
				if (Constant.isDebug) {
					Log.v(Constant.TAG, "Lat:" + routeLat + "-Lng:" + routeLng);
				}
				list.add(routePoint);
			}
		}
		// 1K轨迹点保存为1个文件
		if (list.size() > 999 || !MyApplication.isPowerConnect) {
			saveRouteFile(list);
		}
	}

	/**
	 * 保存轨迹文件
	 * 
	 * @param list
	 */
	private void saveRouteFile(List<RoutePoint> list) {
		if (list.size() >= 2) {
			String saveString = adapter.setJsonString(list);
			writeFileSdcard(getFilePath(), saveString);
			Log.v(Constant.TAG,
					"Auto Route Track:save file with " + list.size()
							+ " points");
			list.clear();
		} else {
			Log.e(Constant.TAG, "Auto Route Track:list size is less than 2");
		}
	}

	public String readFileSdcard(String fileName) {
		String res = "";
		try {
			FileInputStream fin = new FileInputStream(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (res.equals("")) {
			res = " ";
		}
		return res;
	}

	public void writeFileSdcard(String fileName, String message) {
		if (message.equals("[]")) {
			message = "";
		}
		try {
			FileOutputStream fout = new FileOutputStream(fileName);
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			byte[] bytes = message.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	private String getTimeStr() {
		long nowTime = System.currentTimeMillis();
		Date date = new Date(nowTime);
		String strs = "" + ERROR_CODE;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
			strs = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strs;
	}

	/**
	 * 初始化轨迹文件路径和名称
	 * 
	 * @return String
	 */
	private String getFilePath() {
		stopTime = getTimeStr();
		String format = ".art"; // ART: Auto Route Track
		if (Constant.isDebug)
			format = ".txt";
		return ROUTE_PATH + startTime + "-" + stopTime + format;
	}

	class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// 读取百度加密经纬度
			routeLng = location.getLongitude();
			routeLat = location.getLatitude();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mLocationClient.stop();
		saveRouteFile(list); // 服务销毁时，保存轨迹点到文件

		// Update Running State
		MyApplication.isRouteRecord = false;
	}

}