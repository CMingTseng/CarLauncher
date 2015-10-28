package com.tchip.carlauncher.service;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.MyApplication;
import com.tchip.carlauncher.ui.activity.MainActivity;
import com.tchip.carlauncher.util.MyLog;
import com.tchip.carlauncher.util.SettingUtil;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.Toast;

public class SleepOnOffService extends Service {
	private Context context;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private PowerManager powerManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		context = getApplicationContext();

		sharedPreferences = getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();

		powerManager = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);

		// 动态注册监听函数
		sleepOnOffReceiver = new SleepOnOffReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.tchip.SLEEP_ON");
		filter.addAction("com.tchip.SLEEP_OFF");
		filter.addAction("com.tchip.GSENSOR_CRASH");
		registerReceiver(sleepOnOffReceiver, filter);

	}

	private SleepOnOffReceiver sleepOnOffReceiver;

	public class SleepOnOffReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			MyLog.v("[SleepOnOffReceiver]action:" + action);
			if (action.equals("com.tchip.SLEEP_ON")) {
				if (MyApplication.isSleeping) {
				} else {
					try {
						// 进入低功耗待机
						MyApplication.isSleeping = true;

						// 打开飞行模式
						context.sendBroadcast(new Intent(
								"com.tchip.AIRPLANE_ON"));

						// 关闭GPS
						context.sendBroadcast(new Intent(
								"tchip.intent.action.ACTION_GPS_OFF"));

						// 关闭电子狗电源
						SettingUtil.setEDogEnable(false);

						// 关闭FM发射，并保存休眠前状态
						boolean fmStateBeforeSleep = SettingUtil
								.isFmTransmitOn(context);
						editor.putBoolean("fmStateBeforeSleep",
								fmStateBeforeSleep);
						editor.commit();
						if (fmStateBeforeSleep) {
							MyLog.v("[SleepReceiver]Sleep:close FM");
							Settings.System.putString(
									context.getContentResolver(),
									Constant.FMTransmit.SETTING_ENABLE, "0");
							SettingUtil.SaveFileToNode(
									SettingUtil.nodeFmEnable, "0");

							// 通知状态栏同步图标
							sendBroadcast(new Intent(
									"com.tchip.FM_CLOSE_CARLAUNCHER"));
						}

					} catch (Exception e) {
						MyLog.e("[SleepReceiver]Error when run com.tchip.SLEEP_ON");
					}
				}
			} else if (action.equals("com.tchip.SLEEP_OFF")) {
				if (MyApplication.isSleeping) {
					try {
						// 取消低功耗待机
						MyApplication.isSleeping = false;

						// 如果当前正在停车侦测录像，录满30S后不停止
						MyApplication.shouldStopWhenCrashVideoSave = false;

						// MainActivity,BackThread的Handler启动AutoThread,启动录像和服务
						MyApplication.shouldWakeRecord = true;

						// 发送Home键，回到主界面
						context.sendBroadcast(new Intent("com.tchip.powerKey")
								.putExtra("value", "home"));

						// 关闭飞行模式
						context.sendBroadcast(new Intent(
								"com.tchip.AIRPLANE_OFF"));

						// 打开GPS
						context.sendBroadcast(new Intent(
								"tchip.intent.action.ACTION_GPS_ON"));

						// 打开电子狗电源
						// SettingUtil.setEDogEnable(true);

						// 重置FM发射状态
						boolean fmStateBeforeSleep = sharedPreferences
								.getBoolean("fmStateBeforeSleep", false);
						if (fmStateBeforeSleep) {
							MyLog.v("[SleepReceiver]WakeUp:open FM Transmit");
							Settings.System.putString(
									context.getContentResolver(),
									Constant.FMTransmit.SETTING_ENABLE, "1");
							SettingUtil.SaveFileToNode(
									SettingUtil.nodeFmEnable, "1");

							// 通知状态栏同步图标
							sendBroadcast(new Intent(
									"com.tchip.FM_OPEN_CARLAUNCHER"));
						}
					} catch (Exception e) {
						MyLog.e("[SleepReceiver]Error when run com.tchip.SLEEP_OFF");
					}
				}
			} else if (action.equals("com.tchip.GSENSOR_CRASH")) {
				// 休眠时碰撞侦测，接收到碰撞，亮屏录制一段视频，然后休眠
				MyLog.v("[GSENSOR_CRASH]Before State->shouldCrashRecord:"
						+ MyApplication.shouldCrashRecord
						+ ",shouldStopWhenCrashVideoSave:"
						+ MyApplication.shouldStopWhenCrashVideoSave);

				// 发送Home键，回到主界面
				// context.sendBroadcast(new Intent("com.tchip.powerKey")
				// .putExtra("value", "home"));

				// 点亮屏幕
				// SettingUtil.lightScreen(context);

				if (MyApplication.shouldCrashRecord
						|| MyApplication.shouldStopWhenCrashVideoSave) {
				} else {
					MyApplication.shouldCrashRecord = true;
					MyApplication.shouldStopWhenCrashVideoSave = true;
				}
			}
		}

	}

	private void startSpeak(String content) {
		Intent intent = new Intent(getApplicationContext(), SpeakService.class);
		intent.putExtra("content", content);
		startService(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (sleepOnOffReceiver != null) {
			unregisterReceiver(sleepOnOffReceiver);
		}
	}

}