package com.tchip.carlauncher.service;

import java.util.List;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.ui.activity.MainActivity;
import com.tchip.carlauncher.util.WifiAdmin;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

public class ConnectWifiService extends Service {
	private SharedPreferences sharedPreferences;
	private Editor editor;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sharedPreferences = getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();

		connectWifi();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	private void connectWifi() {
		try {
			WifiManager wifiManager = (WifiManager) this
					.getSystemService(Context.WIFI_SERVICE);
			String strErr = "SuiYueJingHao&AlexZhou";
			String wifiName = sharedPreferences.getString("wifiName", strErr);
			String wifiPass = sharedPreferences.getString("wifiPass", strErr);
			if (wifiManager.isWifiEnabled() && (!strErr.equals(wifiName))
					&& wifiName.trim().length() > 0 && wifiName != null) {
				// 连接WiFi
				WifiAdmin wiFiAdmin = new WifiAdmin(this);
				wiFiAdmin.startScan();
				List<ScanResult> list = wiFiAdmin.getWifiList();

				int netId = wiFiAdmin.AddWifiConfig(list, wifiName, wifiPass);
				if (netId != -1) {
					wiFiAdmin.getConfiguration();// 添加了配置信息，要重新得到配置信息
					if (wiFiAdmin.ConnectWifi(netId)) {
					}
				} else {
					// 网络连接错误
				}
			}
			Log.v(Constant.TAG, "wifiName:" + wifiName + " - wifiPass:"
					+ wifiPass);
		} catch (Exception e) {
			Log.e(Constant.TAG, e.toString());
			e.printStackTrace();
		}
	}

}
