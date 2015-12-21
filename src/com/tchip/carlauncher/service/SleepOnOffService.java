package com.tchip.carlauncher.service;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.MyApplication;
import com.tchip.carlauncher.util.HintUtil;
import com.tchip.carlauncher.util.MyLog;
import com.tchip.carlauncher.util.SettingUtil;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;

public class SleepOnOffService extends Service {
	private Context context;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private PowerManager powerManager;
	private WakeLock wakeLock;

	/** ACC断开进入预备模式的时间:秒 **/
	private int preSleepCount = 0;

	/** 预备模式的时间:秒 **/
	private final int TIME_SLEEP_CONFIRM = 2;

	/** ACC断开的时间:秒 **/
	private int accOffCount = 0;

	/** ACC断开进入深度休眠之前的时间:秒 **/
	private final int TIME_SLEEP_GOING = 70;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		context = getApplicationContext();

		sharedPreferences = getSharedPreferences(Constant.MySP.NAME,
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();

		powerManager = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);

		// 动态注册监听函数
		sleepOnOffReceiver = new SleepOnOffReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.Broadcast.ACC_ON);
		filter.addAction(Constant.Broadcast.ACC_OFF);
		filter.addAction(Constant.Broadcast.GSENSOR_CRASH);
		filter.addAction(Constant.Broadcast.SPEECH_COMMAND);
		filter.addAction(Constant.Broadcast.BT_MUSIC_PLAYING);
		filter.addAction(Constant.Broadcast.BT_MUSIC_STOPED);
		filter.addAction(Constant.Broadcast.SETTING_SYNC);
		registerReceiver(sleepOnOffReceiver, filter);

	}

	/**
	 * 获取休眠锁
	 * 
	 * PARTIAL_WAKE_LOCK
	 * 
	 * SCREEN_DIM_WAKE_LOCK
	 * 
	 * FULL_WAKE_LOCK
	 * 
	 * ON_AFTER_RELEASE
	 */
	private void acquireWakeLock() {
		if (wakeLock == null) {
			wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					this.getClass().getCanonicalName());
		}
		wakeLock.acquire();

	}

	/**
	 * 释放休眠锁
	 */
	private void releaseWakeLock() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}
	}

	private SleepOnOffReceiver sleepOnOffReceiver;

	public class SleepOnOffReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			MyLog.v("[SleepOnOffReceiver]action:" + action);
			if (action.equals(Constant.Broadcast.ACC_OFF)) {
				MyApplication.isAccOn = false;

				MyApplication.isSleepConfirm = true;
				preSleepCount = 0;
				new Thread(new PreSleepThread()).start();

			} else if (action.equals(Constant.Broadcast.ACC_ON)) {
				MyApplication.isAccOn = true;

				preSleepCount = 0;
				MyApplication.isSleepConfirm = false;

				deviceWake();
				startExternalService();

			} else if (action.equals(Constant.Broadcast.GSENSOR_CRASH)) { // 停车守卫:侦测到碰撞广播触发
				if (MyApplication.isSleeping) {
					MyLog.v("[GSENSOR_CRASH]Before State->shouldCrashRecord:"
							+ MyApplication.shouldCrashRecord
							+ ",shouldStopWhenCrashVideoSave:"
							+ MyApplication.shouldStopWhenCrashVideoSave);

					// context.sendBroadcast(new Intent("com.tchip.powerKey")
					// .putExtra("value", "home")); // 发送Home键，回到主界面
					// SettingUtil.lightScreen(context); // 点亮屏幕

					if (MyApplication.shouldCrashRecord
							|| MyApplication.shouldStopWhenCrashVideoSave) {
					} else {
						MyApplication.shouldCrashRecord = true;
						MyApplication.shouldStopWhenCrashVideoSave = true;
					}
				}

			} else if (action.equals(Constant.Broadcast.SPEECH_COMMAND)) {
				String command = intent.getExtras().getString("command");
				if ("take_photo".equals(command)) {
					MyApplication.shouldTakeVoicePhoto = true; // 语言拍照

					context.sendBroadcast(new Intent("com.tchip.powerKey")
							.putExtra("value", "home")); // 发送Home键，回到主界面

					if (!powerManager.isScreenOn()) { // 确保屏幕点亮
						SettingUtil.lightScreen(getApplicationContext());
					}

				}
			} else if (action.equals(Constant.Broadcast.BT_MUSIC_PLAYING)) {
				MyApplication.isBTPlayMusic = true;

			} else if (action.equals(Constant.Broadcast.BT_MUSIC_STOPED)) {
				MyApplication.isBTPlayMusic = false;

			} else if (action.equals(Constant.Broadcast.SETTING_SYNC)) {
				String content = intent.getExtras().getString("content");
				if ("parkOn".equals(content)) { // 停车守卫:开
					editor.putBoolean(Constant.MySP.STR_PARKING_ON, true);
					editor.commit();

				} else if ("parkOff".equals(content)) { // 停车守卫:关
					editor.putBoolean(Constant.MySP.STR_PARKING_ON, false);
					editor.commit();

				} else if ("crashOn".equals(content)) { // 碰撞侦测:开
					editor.putBoolean("crashOn", true);
					editor.commit();

				} else if ("crashOff".equals(content)) { // 碰撞侦测:关
					editor.putBoolean("crashOn", false);
					editor.commit();

				} else if ("crashLow".equals(content)) { // 碰撞侦测灵敏度:低
					MyApplication.crashSensitive = 0;
					editor.putInt("crashSensitive", 0);
					editor.commit();

				} else if ("crashMiddle".equals(content)) { // 碰撞侦测灵敏度:中
					MyApplication.crashSensitive = 1;
					editor.putInt("crashSensitive", 1);
					editor.commit();

				} else if ("crashHigh".equals(content)) { // 碰撞侦测灵敏度:高
					MyApplication.crashSensitive = 2;
					editor.putInt("crashSensitive", 2);
					editor.commit();

				}
			}
		}
	}

	/**
	 * 预备休眠线程
	 */
	public class PreSleepThread implements Runnable {

		@Override
		public void run() {
			synchronized (preSleepHandler) {
				/** 激发条件:1.ACC下电 2.未进入休眠 **/
				while (MyApplication.isSleepConfirm && !MyApplication.isAccOn
						&& !MyApplication.isSleeping) {
					try {
						Thread.sleep(1000);
						Message message = new Message();
						message.what = 1;
						preSleepHandler.sendMessage(message);

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	final Handler preSleepHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (!MyApplication.isAccOn) {
					preSleepCount++;
				} else {
					preSleepCount = 0;
				}
				MyLog.v("[ParkingMonitor]preSleepCount:" + preSleepCount);

				if (preSleepCount == TIME_SLEEP_CONFIRM
						&& !MyApplication.isAccOn && !MyApplication.isSleeping) {
					preSleepCount = 0;

					MyApplication.isSleepConfirm = false;
					deviceAccOff();
				}
				break;

			default:
				break;
			}
		}

	};

	/**
	 * 90s后进入停车侦测守卫模式，期间如果ACC上电则取消
	 */
	public class GoingParkMonitorThread implements Runnable {

		@Override
		public void run() {
			synchronized (goingParkMonitorHandler) {
				/** 激发条件:1.ACC下电 2.未进入休眠 **/
				while (!MyApplication.isAccOn && !MyApplication.isSleeping) {
					try {
						Thread.sleep(1000);
						Message message = new Message();
						message.what = 1;
						goingParkMonitorHandler.sendMessage(message);

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	final Handler goingParkMonitorHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (!MyApplication.isAccOn) {
					accOffCount++;
				} else {
					accOffCount = 0;
				}
				MyLog.v("[ParkingMonitor]accOffCount:" + accOffCount);

				if (accOffCount >= TIME_SLEEP_GOING && !MyApplication.isAccOn
						&& !MyApplication.isSleeping) {
					deviceSleep();
				}
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 执行90秒任务
	 */
	private void deviceAccOff() {
		if (sharedPreferences.getBoolean(Constant.MySP.STR_PARKING_ON, true)) {
			HintUtil.speakVoice(context, "九十秒后启动停车守卫");
		}

		if (!MyApplication.isMainForeground) {
			// 发送Home键，回到主界面
			context.sendBroadcast(new Intent("com.tchip.powerKey").putExtra(
					"value", "home"));
			if (!powerManager.isScreenOn()) { // 确保屏幕点亮
				SettingUtil.lightScreen(getApplicationContext());
			}
		}

		MyApplication.shouldTakePhotoWhenAccOff = true;

		acquireWakeLock();
		new Thread(new GoingParkMonitorThread()).start();

		stopExternalService();
		accOffCount = 0; // TODO:Why?

		context.sendBroadcast(new Intent(Constant.Broadcast.GPS_OFF)); // 关闭GPS
		SettingUtil.setEDogEnable(false); // 关闭电子狗电源

		// 关闭FM发射，并保存休眠前状态
		boolean fmStateBeforeSleep = SettingUtil.isFmTransmitOn(context);
		editor.putBoolean("fmStateBeforeSleep", fmStateBeforeSleep);
		editor.commit();
		if (fmStateBeforeSleep) {
			MyLog.v("[SleepReceiver]Sleep: close FM");
			Settings.System.putString(context.getContentResolver(),
					Constant.FMTransmit.SETTING_ENABLE, "0");
			SettingUtil.SaveFileToNode(SettingUtil.nodeFmEnable, "0");
			sendBroadcast(new Intent("com.tchip.FM_CLOSE_CARLAUNCHER")); // 通知状态栏同步图标
		}
	}

	/**
	 * 休眠广播触发
	 */
	private void deviceSleep() {
		try {
			MyLog.e("[SleepOnOffService]deviceSleep.");
			MyApplication.isSleeping = true; // 进入低功耗待机

			context.sendBroadcast(new Intent("com.tchip.KILL_APP").putExtra(
					"value", "com.tchip.route")); // 退出轨迹

		} catch (Exception e) {
			MyLog.e("[SleepReceiver]Error when run deviceSleep");
		} finally {
			MyApplication.isAccOffPhotoTaking = false; // 重置ACC下电拍照标志
			context.sendBroadcast(new Intent(Constant.Broadcast.AIRPLANE_ON)); // 打开飞行模式
			context.sendBroadcast(new Intent(Constant.Broadcast.SLEEP_ON)); // 通知其他应用进入休眠

			releaseWakeLock();
		}
	}

	/**
	 * 唤醒广播触发
	 */
	private void deviceWake() {
		try {
			// 取消低功耗待机
			MyApplication.isSleeping = false;
			// 通知其他应用取消休眠
			context.sendBroadcast(new Intent(Constant.Broadcast.SLEEP_OFF));

			// 如果当前正在停车侦测录像，录满30S后不停止
			MyApplication.shouldStopWhenCrashVideoSave = false;

			// MainActivity,BackThread的Handler启动AutoThread,启动录像和服务
			MyApplication.shouldWakeRecord = true;

			context.sendBroadcast(new Intent("com.tchip.powerKey").putExtra(
					"value", "home")); // 发送Home键，回到主界面
			context.sendBroadcast(new Intent(Constant.Broadcast.AIRPLANE_OFF)); // 关闭飞行模式
			context.sendBroadcast(new Intent(Constant.Broadcast.GPS_ON)); // 打开GPS

			// SettingUtil.setEDogEnable(true); // 打开电子狗电源

			// 重置FM发射状态
			boolean fmStateBeforeSleep = sharedPreferences.getBoolean(
					"fmStateBeforeSleep", false);
			if (fmStateBeforeSleep) {
				MyLog.v("[SleepReceiver]WakeUp:open FM Transmit");
				Settings.System.putString(context.getContentResolver(),
						Constant.FMTransmit.SETTING_ENABLE, "1");
				SettingUtil.SaveFileToNode(SettingUtil.nodeFmEnable, "1");

				// 通知状态栏同步图标
				sendBroadcast(new Intent("com.tchip.FM_OPEN_CARLAUNCHER"));
			}
		} catch (Exception e) {
			MyLog.e("[SleepReceiver]Error when run deviceWake");
		}
	}

	/**
	 * 开启外部服务：
	 * 
	 * 1.轨迹记录
	 * 
	 * 2.天气播报
	 */
	private void startExternalService() {
		try {
			// 轨迹记录
			Intent intentRoute = new Intent();
			intentRoute.setClassName("com.tchip.route",
					"com.tchip.route.service.RouteRecordService");
			startService(intentRoute);

			// 天气播报
			Intent intentWeather = new Intent();
			intentWeather.setClassName("com.tchip.weather",
					"com.tchip.weather.service.TimeTickService");
			startService(intentWeather);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 关闭外部应用与服务：
	 * 
	 * 1.轨迹记录服务
	 * 
	 * 2.天气播报服务
	 * 
	 * 3.酷我音乐
	 */
	private void stopExternalService() {
		try {
			// 轨迹记录服务
			Intent intentRoute = new Intent();
			intentRoute.setClassName("com.tchip.route",
					"com.tchip.route.service.RouteRecordService");
			stopService(intentRoute);

			// 天气播报
			Intent intentWeather = new Intent();
			intentWeather.setClassName("com.tchip.weather",
					"com.tchip.weather.service.TimeTickService");
			stopService(intentWeather);

			// 酷我音乐
			context.sendBroadcast(new Intent("com.tchip.KILL_APP").putExtra(
					"value", "cn.kuwo.kwmusiccar"));

			// 高德地图
			context.sendBroadcast(new Intent("com.tchip.KILL_APP").putExtra(
					"value", "com.autonavi.minimap"));

			// 图库
			context.sendBroadcast(new Intent("com.tchip.KILL_APP").putExtra(
					"value", "com.android.gallery3d"));

			// 天气
			context.sendBroadcast(new Intent("com.tchip.KILL_APP").putExtra(
					"value", "com.tchip.weather"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		if (sleepOnOffReceiver != null) {
			unregisterReceiver(sleepOnOffReceiver);
		}

		super.onDestroy();
	}

}