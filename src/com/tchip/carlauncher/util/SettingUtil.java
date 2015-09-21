package com.tchip.carlauncher.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.tchip.carlauncher.Constant;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

public class SettingUtil {

	/**
	 * 调整系统亮度
	 * 
	 * @param brightness
	 */
	public static void setBrightness(Context context, int brightness) {
		if (brightness < 256 && brightness > -1) {
			boolean setSuccess = Settings.System.putInt(
					context.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS, brightness);
			MyLog.v("[SettingUtil]setBrightness: " + brightness + ", "
					+ setSuccess);

			SharedPreferences sharedPreferences = context.getSharedPreferences(
					Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
			Editor editor = sharedPreferences.edit();

			editor.putInt("manulLightValue", brightness);
			editor.commit();
		}
	}

	public static int getBrightness(Context context) {
		try {
			int nowBrightness = Settings.System.getInt(
					context.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS);
			MyLog.v("[SettingUtil]nowBrightness:" + nowBrightness);
			return nowBrightness;
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
			return 155;
		}
	}

	public static void setScreenOffTime(Context context, int time) {
		Settings.System.putInt(context.getContentResolver(),
				android.provider.Settings.System.SCREEN_OFF_TIMEOUT, time);
	}

	public static int getScreenOffTime(Context context) {
		try {
			return Settings.System.getInt(context.getContentResolver(),
					Settings.System.SCREEN_OFF_TIMEOUT);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
			return 155;
		}
	}

	/**
	 * FM发射开关节点
	 * 
	 * 1：开 0：关
	 */
	public static File nodeFmEnable = new File(
			"/sys/devices/platform/mt-i2c.1/i2c-1/1-002c/enable_qn8027");

	/**
	 * FM发射频率节点
	 * 
	 * 频率范围：7600~10800:8750-10800
	 */
	public static File nodeFmChannel = new File(
			"/sys/devices/platform/mt-i2c.1/i2c-1/1-002c/setch_qn8027");

	public static boolean isFmTransmitOn(Context context) {
		boolean isFmTransmitOpen = false;
		String fmEnable = Settings.System.getString(
				context.getContentResolver(),
				Constant.FMTransmit.SETTING_ENABLE);
		if (fmEnable.trim().length() > 0) {
			if ("1".equals(fmEnable)) {
				isFmTransmitOpen = true;
			} else {
				isFmTransmitOpen = false;
			}
		}
		return isFmTransmitOpen;
	}

	/**
	 * 获取设置中存取的频率
	 * 
	 * @return 8750-10800
	 */
	public static int getFmFrequceny(Context context) {
		String fmChannel = Settings.System.getString(
				context.getContentResolver(),
				Constant.FMTransmit.SETTING_CHANNEL);

		return Integer.parseInt(fmChannel);
	}

	/**
	 * 设置FM发射频率:8750-10800
	 * 
	 * @param frequency
	 */
	public static void setFmFrequency(Context context, int frequency) {
		if (frequency >= 8750 || frequency <= 10800) {
			Settings.System.putString(context.getContentResolver(),
					Constant.FMTransmit.SETTING_CHANNEL, "" + frequency);

			SaveFileToNode(nodeFmChannel, String.valueOf(frequency));
			Log.v(Constant.TAG, "FM Transmit:Set FM Frequency success:"
					+ frequency / 100.0f + "MHz");
		}
	}

	public static void SaveFileToNode(File file, String value) {
		if (file.exists()) {
			try {
				StringBuffer strbuf = new StringBuffer("");
				strbuf.append(value);
				OutputStream output = null;
				OutputStreamWriter outputWrite = null;
				PrintWriter print = null;

				try {
					output = new FileOutputStream(file);
					outputWrite = new OutputStreamWriter(output);
					print = new PrintWriter(outputWrite);
					print.print(strbuf.toString());
					print.flush();
					output.close();

				} catch (FileNotFoundException e) {
					e.printStackTrace();
					Log.e(Constant.TAG, "FM Transmit:output error");
				}
			} catch (IOException e) {
				Log.e(Constant.TAG, "FM Transmit:IO Exception");
			}
		} else {
			Log.e(Constant.TAG, "FM Transmit:File:" + file + "not exists");
		}
	}

	/**
	 * 点亮屏幕
	 * 
	 * @param context
	 */
	public static void lightScreen(Context context) {
		// 获取电源管理器对象
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);

		// 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.ACQUIRE_CAUSES_WAKEUP
						| PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");

		wl.acquire(); // 点亮屏幕
		wl.release(); // 释放

		// 得到键盘锁管理器对象
		KeyguardManager km = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE);

		// 参数是LogCat里用的Tag
		KeyguardLock kl = km.newKeyguardLock("ZMS");

		kl.disableKeyguard();
	}

	/**
	 * Camera自动调节亮度节点
	 * 
	 * 1：开 0：关;默认打开
	 */
	public static File fileAutoLightSwitch = new File(
			"sys/devices/platform/mt-i2c.1/i2c-1/1-007f/back_car_status");

	/**
	 * 设置Camera自动调节亮度开关
	 */
	public static void setAutoLight(Context context, boolean isAutoLightOn) {
		if (isAutoLightOn) {
			SaveFileToNode(fileAutoLightSwitch, "1");
		} else {
			SaveFileToNode(fileAutoLightSwitch, "0");
		}
		MyLog.v("[SettingUtil]setAutoLight:" + isAutoLightOn);
	}

}
