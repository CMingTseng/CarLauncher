package com.tchip.carlauncher.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.tchip.carlauncher.util.TrafficUtils;

public class TrafficFetchService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Log.i("TAG", "TrafficFetchService:onStartCommand() 正在获取流量数据");
				TrafficUtils.fetchTraffic(TrafficFetchService.this);
				Intent updateScreen = new Intent(
						TrafficUtils.ACTION_UPDATE_TRAFFIC);
				sendBroadcast(updateScreen);
			}
		}).start();

		return super.onStartCommand(intent, flags, startId);
	}
}
