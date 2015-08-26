package com.tchip.carlauncher.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.tchip.carlauncher.Constant;

import android.content.Context;
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

}
