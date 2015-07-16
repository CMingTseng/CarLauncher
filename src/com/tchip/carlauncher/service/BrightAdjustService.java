package com.tchip.carlauncher.service;

import java.util.Date;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.util.SettingUtil;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class BrightAdjustService extends Service {
	private static final int HOUR_DAY_START = 7;
	private static final int HOUR_DAY_END = HOUR_DAY_START + 12;
	private static final int SCAN_SPAN = 30000; // 5 min

	private static final int BRIGHT_NIGHT = 80; // 0~255
	private static final int BRIGHT_DAY = 160;

	private SharedPreferences preferences;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		new Thread(new RouteRecordThread()).start();
	}

	public class RouteRecordThread implements Runnable {

		@Override
		public void run() {
			synchronized (brightHandler) {
				while (true) {
					try {
						Thread.sleep(SCAN_SPAN);
						Message message = new Message();
						message.what = 1;
						brightHandler.sendMessage(message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	final Handler brightHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				preferences = getSharedPreferences(
						Constant.SHARED_PREFERENCES_NAME,
						getApplicationContext().MODE_PRIVATE);
				boolean brightAdjust = preferences.getBoolean("brightAdjust",
						false);
				if (brightAdjust) {
					int hourNow = getDeviceHour();
					if (hourNow > HOUR_DAY_END || hourNow < HOUR_DAY_START) { // 夜间
						SettingUtil.setBrightness(getApplicationContext(),
								BRIGHT_NIGHT);
					} else { // 日间
						SettingUtil.setBrightness(getApplicationContext(),
								BRIGHT_DAY);
					}
				}
			}
			super.handleMessage(msg);
		}
	};

	public int getDeviceHour() {
		Date date = new Date();
		return date.getHours();
	}

}
