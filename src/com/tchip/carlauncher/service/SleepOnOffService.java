package com.tchip.carlauncher.service;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.MyApplication;
import com.tchip.carlauncher.R;
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
import android.widget.Toast;

public class SleepOnOffService extends Service {
	private Context context;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private PowerManager powerManager;
	private WakeLock wakeLock;

	/** ACC断开的时间:秒 **/
	private int accOffCount = 0;

	/** ACC断开进入深度休眠之前的时间:秒 **/
	private final int TIME_BEFORE_SLEEP = 70;

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
		filter.addAction(Constant.Broadcast.ACC_ON);
		filter.addAction(Constant.Broadcast.ACC_OFF);
		filter.addAction(Constant.Broadcast.GSENSOR_CRASH);
		filter.addAction(Constant.Broadcast.SPEECH_COMMAND);
		filter.addAction(Constant.Broadcast.BT_MUSIC_PLAYING);
		filter.addAction(Constant.Broadcast.BT_MUSIC_STOPED);
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
				deviceAccOff();

			} else if (action.equals(Constant.Broadcast.ACC_ON)) {
				MyApplication.isAccOn = true;
				deviceWake();
				startExternalService();

			} else if (action.equals(Constant.Broadcast.GSENSOR_CRASH)) {
				deviceCrash();

			} else if (action.equals(Constant.Broadcast.SPEECH_COMMAND)) {
				String command = intent.getExtras().getString("command");
				if ("take_photo".equals(command)) {
					// 语言拍照
					MyApplication.shouldTakeVoicePhoto = true;

					// 发送Home键，回到主界面
					context.sendBroadcast(new Intent("com.tchip.powerKey")
							.putExtra("value", "home"));

					// 确保屏幕点亮
					if (!powerManager.isScreenOn()) {
						SettingUtil.lightScreen(getApplicationContext());
					}

				}
			} else if (action.equals(Constant.Broadcast.BT_MUSIC_PLAYING)) {

				MyApplication.isBTPlayMusic = true;

			} else if (action.equals(Constant.Broadcast.BT_MUSIC_STOPED)) {

				MyApplication.isBTPlayMusic = false;
			}
		}
	}

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

				// TODO: Delete below lines
				MyLog.v("[ParkingMonitor]accOffCount:" + accOffCount);

				if (accOffCount >= TIME_BEFORE_SLEEP && !MyApplication.isAccOn
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
	 * ACC下电广播触发
	 */
	private void deviceAccOff() {
		MyApplication.isAccOn = false;
		startSpeak("九十秒后启动停车守卫");

		// if (MyApplication.shouldTakePhotoWhenAccOff
		// || MyApplication.shouldSendPathToDSA) {
		// // 已经在走拍照流程，不需要再次激活,Bug:不会熄屏
		// } else {

		if (!MyApplication.isMainForeground) {
			// 发送Home键，回到主界面
			context.sendBroadcast(new Intent("com.tchip.powerKey").putExtra(
					"value", "home"));

			// 确保屏幕点亮
			if (!powerManager.isScreenOn()) {
				SettingUtil.lightScreen(getApplicationContext());
			}
		}

		MyApplication.shouldTakePhotoWhenAccOff = true;

		acquireWakeLock();
		new Thread(new GoingParkMonitorThread()).start();

		stopExternalService();
		// }
		accOffCount = 0;

		// 关闭GPS
		context.sendBroadcast(new Intent(Constant.Broadcast.GPS_OFF));

		// 关闭电子狗电源
		SettingUtil.setEDogEnable(false);

		// 关闭FM发射，并保存休眠前状态
		boolean fmStateBeforeSleep = SettingUtil.isFmTransmitOn(context);
		editor.putBoolean("fmStateBeforeSleep", fmStateBeforeSleep);
		editor.commit();
		if (fmStateBeforeSleep) {
			MyLog.v("[SleepReceiver]Sleep: close FM");
			Settings.System.putString(context.getContentResolver(),
					Constant.FMTransmit.SETTING_ENABLE, "0");
			SettingUtil.SaveFileToNode(SettingUtil.nodeFmEnable, "0");

			// 通知状态栏同步图标
			sendBroadcast(new Intent("com.tchip.FM_CLOSE_CARLAUNCHER"));
		}
	}

	/**
	 * 休眠广播触发
	 */
	private void deviceSleep() {
		try { 
			String strSleepOn = getResources().getString(
					R.string.device_going_sleep);
			MyLog.e("[SleepOnOffService]deviceSleep.");
			// startSpeak(strSleepOn);

			// 进入低功耗待机
			MyApplication.isSleeping = true;

			// 退出轨迹
			context.sendBroadcast(new Intent("com.tchip.KILL_APP").putExtra(
					"value", "com.tchip.route"));

		} catch (Exception e) {
			MyLog.e("[SleepReceiver]Error when run deviceSleep");
		} finally {
			// 重置ACC下电拍照标志
			MyApplication.isAccOffPhotoTaking = false;

			// 打开飞行模式
			context.sendBroadcast(new Intent(Constant.Broadcast.AIRPLANE_ON));

			// 通知其他应用进入休眠
			context.sendBroadcast(new Intent(Constant.Broadcast.SLEEP_ON));

			releaseWakeLock();
		}
	}

	/**
	 * 唤醒广播触发
	 */
	private void deviceWake() {
		// if (MyApplication.isSleeping) {
		try {
			// 取消低功耗待机
			MyApplication.isSleeping = false;
			// 通知其他应用取消休眠
			context.sendBroadcast(new Intent(Constant.Broadcast.SLEEP_OFF));

			// 如果当前正在停车侦测录像，录满30S后不停止
			MyApplication.shouldStopWhenCrashVideoSave = false;

			// MainActivity,BackThread的Handler启动AutoThread,启动录像和服务
			MyApplication.shouldWakeRecord = true;

			// 发送Home键，回到主界面
			context.sendBroadcast(new Intent("com.tchip.powerKey").putExtra(
					"value", "home"));

			// 关闭飞行模式
			context.sendBroadcast(new Intent(Constant.Broadcast.AIRPLANE_OFF));

			// 打开GPS
			context.sendBroadcast(new Intent(Constant.Broadcast.GPS_ON));

			// 打开电子狗电源
			// SettingUtil.setEDogEnable(true);

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
		// }
	}

	/**
	 * 停车守卫:侦测到碰撞广播触发
	 */
	private void deviceCrash() {
		if (MyApplication.isSleeping) {
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