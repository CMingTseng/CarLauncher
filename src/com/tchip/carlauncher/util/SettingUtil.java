package com.tchip.carlauncher.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
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
		if (brightness < 255 && brightness > 0)
			Settings.System.putInt(context.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS, brightness);
	}

	public static int getBrightness(Context context) {
		try {
			return Settings.System.getInt(context.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 155;
		}
	}

	public static void setScreenOffTime(Context context, int time) {
		Settings.System.putInt(context.getContentResolver(),
				android.provider.Settings.System.SCREEN_OFF_TIMEOUT, -1);
	}

	public static int getScreenOffTime(Context context) {
		try {
			return Settings.System.getInt(context.getContentResolver(),
					Settings.System.SCREEN_OFF_TIMEOUT);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 155;
		}
	}

}
