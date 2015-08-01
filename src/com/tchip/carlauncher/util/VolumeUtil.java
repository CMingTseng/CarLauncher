package com.tchip.carlauncher.util;

import android.content.Context;
import android.media.AudioManager;

public class VolumeUtil {

	/**
	 * 
	 * @param context
	 * @param type
	 *            AudioManager.STREAM_MUSIC;STREAM_RING
	 * @param step
	 *            增加音量
	 */
	public static void plusVolume(Context context, int type, int step) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);

		int nowVolume = audioManager.getStreamVolume(type);
		int toVolume = nowVolume + step;
		if (toVolume <= 15)
			audioManager.setStreamVolume(type, toVolume, 0);
		else
			audioManager.setStreamVolume(type, 15, 0);
	}

	/**
	 * 
	 * @param context
	 * @param type
	 *            AudioManager.STREAM_MUSIC;STREAM_RING
	 * @param step
	 */
	public static void minusVolume(Context context, int type, int step) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		int nowVolume = audioManager.getStreamVolume(type);
		int toVolume = nowVolume - step;
		if (toVolume > 0)
			audioManager.setStreamVolume(type, toVolume, 0);
		else
			audioManager.setStreamVolume(type, 0, 0);

	}

	/**
	 * 设置最大音量
	 * 
	 * @param context
	 *            AudioManager.STREAM_MUSIC;STREAM_RING
	 * @param type
	 */
	public static void setMaxVolume(Context context, int type) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(type, 15, 0);
	}

	/**
	 * 设置最小音量
	 * 
	 * @param context
	 *            AudioManager.STREAM_MUSIC;STREAM_RING
	 * @param type
	 */
	public static void setMinVolume(Context context, int type) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(type, 0, 0);
	}

	/**
	 * 静音
	 * 
	 * @param context
	 */
	public static void setMute(Context context) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setRingerMode(audioManager.RINGER_MODE_SILENT);
	}
	
	/**
	 * 关闭静音
	 */
	public static void setUnmute(Context context, int type) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(type, 8, 0);
	}
	
}
