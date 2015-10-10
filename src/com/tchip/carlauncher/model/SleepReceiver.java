package com.tchip.carlauncher.model;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.MyApplication;
import com.tchip.carlauncher.service.SpeakService;
import com.tchip.carlauncher.ui.activity.MainActivity;
import com.tchip.carlauncher.util.MyLog;
import com.tchip.carlauncher.util.SettingUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.KeyEvent;

public class SleepReceiver extends BroadcastReceiver {

	private Context context;
	private SharedPreferences preferences;
	private Editor editor;
	private AudioManager audioManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		/*
		this.context = context;
		preferences = context.getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		editor = preferences.edit();
		audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);

		String action = intent.getAction();
		MyLog.v("[SleepReceiver]action:" + action);
		if (action.equals("com.tchip.SLEEP_ON")) {
			try {
				// 进入低功耗待机
				MyApplication.isSleeping = true;
				// 打开飞行模式
				context.sendBroadcast(new Intent("com.tchip.AIRPLANE_ON"));

				// 熄灭屏幕,判断当前屏幕是否关闭
				PowerManager pm = (PowerManager) context
						.getSystemService(Context.POWER_SERVICE);
				boolean isScreenOn = pm.isScreenOn();
				if (!isScreenOn) {
				} else {
					context.sendBroadcast(new Intent("com.tchip.powerKey")
							.putExtra("value", "power"));
				}

				// 关闭GPS
				context.sendBroadcast(new Intent(
						"tchip.intent.action.ACTION_GPS_OFF"));

				// 关闭FM发射，并保存休眠前状态
				boolean fmStateBeforeSleep = SettingUtil
						.isFmTransmitOn(context);
				editor.putBoolean("fmStateBeforeSleep", fmStateBeforeSleep);
				editor.commit();
				if (fmStateBeforeSleep) {
					MyLog.v("[SleepReceiver]Sleep:close FM");
					Settings.System.putString(context.getContentResolver(),
							Constant.FMTransmit.SETTING_ENABLE, "0");
					SettingUtil.SaveFileToNode(SettingUtil.nodeFmEnable, "0");
				}

				// 静音
				// 媒体声音
				if (Constant.Module.muteWhenSleep) {
					int volumeMusic = audioManager
							.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
					int volumeRing = audioManager
							.getStreamVolume(AudioManager.STREAM_RING);
					editor.putInt("volumeMusic", volumeMusic);
					editor.putInt("volumeRing", volumeRing);
					editor.commit();
				}
			} catch (Exception e) {
				MyLog.e("[SleepReceiver]Error when run com.tchip.SLEEP_ON");
			} finally {
				// 发送Home键，回到主界面
				context.sendBroadcast(new Intent("com.tchip.powerKey")
						.putExtra("value", "home"));
			}
		} else if (action.equals("com.tchip.SLEEP_OFF")) {
			try {
				// 取消低功耗待机
				MyApplication.isSleeping = false;

				// 发送Home键，回到主界面
				context.sendBroadcast(new Intent("com.tchip.powerKey")
						.putExtra("value", "home"));

				// 点亮屏幕
				SettingUtil.lightScreen(context);

				// 关闭飞行模式
				context.sendBroadcast(new Intent("com.tchip.AIRPLANE_OFF"));

				// 打开GPS
				context.sendBroadcast(new Intent(
						"tchip.intent.action.ACTION_GPS_ON"));

				// 重置FM发射状态
				boolean fmStateBeforeSleep = preferences.getBoolean(
						"fmStateBeforeSleep", false);
				if (fmStateBeforeSleep) {
					MyLog.v("[SleepReceiver]WakeUp:open FM Transmit");
					Settings.System.putString(context.getContentResolver(),
							Constant.FMTransmit.SETTING_ENABLE, "1");
					SettingUtil.SaveFileToNode(SettingUtil.nodeFmEnable, "1");
				}

				// 恢复音量设置
				if (Constant.Module.muteWhenSleep) {
					int volumeMusic = preferences.getInt("volumeMusic", 8);
					int volumeRing = preferences.getInt("volumeRing", 8);
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
							volumeMusic, 0);
					audioManager.setStreamVolume(AudioManager.STREAM_RING,
							volumeRing, 0);
				}

				// MainActivity,BackThread的Handler启动AutoThread,启动录像和服务
				MyApplication.shouldWakeRecord = true;

			} catch (Exception e) {
				MyLog.e("[SleepReceiver]Error when run com.tchip.SLEEP_OFF");
			}
		} else if (action.equals("com.tchip.GSENSOR_CRASH")) {
			// 休眠时碰撞侦测，接收到碰撞，亮屏录制一段视频，然后休眠

			// 发送Home键，回到主界面
			context.sendBroadcast(new Intent("com.tchip.powerKey").putExtra(
					"value", "home"));

			// 点亮屏幕
			SettingUtil.lightScreen(context);

			if (MyApplication.shouldCrashRecord
					|| MyApplication.shouldStopWhenCrashVideoSave) {
			} else {
				MyApplication.shouldCrashRecord = true;
				MyApplication.shouldStopWhenCrashVideoSave = true;
			}
		}
		*/
	}

	/**
	 * 当前是否开启飞行模式
	 */
	private boolean isAirplaneModeOn() {
		// 返回值是1时表示处于飞行模式
		int modeIdx = Settings.Global.getInt(context.getContentResolver(),
				Settings.Global.AIRPLANE_MODE_ON, 0);
		boolean isEnabled = (modeIdx == 1);
		MyLog.v("[SleepReceiver]isAirplaneModeOn:" + isEnabled);
		return isEnabled;
	}

	/**
	 * 设置飞行模式
	 */
	private void setAirplaneMode(boolean setAirPlane) {
		MyLog.v("[SleepReceiver]setAirplaneMode:" + setAirPlane);
		Settings.Global.putInt(context.getContentResolver(),
				Settings.Global.AIRPLANE_MODE_ON, setAirPlane ? 1 : 0);
		// 广播飞行模式的改变，让相应的程序可以处理。
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", setAirPlane);
		context.sendBroadcast(intent);
	}
}
