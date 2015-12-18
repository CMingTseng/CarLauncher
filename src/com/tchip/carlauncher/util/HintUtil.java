package com.tchip.carlauncher.util;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.service.FloatWindowService;
import com.tchip.carlauncher.service.SpeakService;
import com.tchip.carlauncher.ui.activity.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;

public class HintUtil {

	public static void playAudio(Context context, int type) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		MediaPlayer mediaPlayer;

		if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) > 0) {
			if (type == com.tchip.tachograph.TachographCallback.FILE_TYPE_IMAGE) {
				mediaPlayer = MediaPlayer.create(context, R.raw.camera_image);
			} else {
				mediaPlayer = MediaPlayer.create(context, R.raw.camera_video);
			}
			mediaPlayer.start();
		}

	}

	public static void speakVoice(Context context, String content) {
		Intent intent = new Intent(context, SpeakService.class);
		intent.putExtra("content", content);
		context.startService(intent);
	}

	/**
	 * 显示或隐藏录像提示悬浮窗
	 */
	public static void setRecordHintFloatWindowVisible(Context context,
			boolean isVisible) {
		MyLog.v("[HintUtil]setRecordHintFloatWindowVisible:" + isVisible);
		if (isVisible) {
			Intent intentFloatWindow = new Intent(context,
					FloatWindowService.class);
			context.startService(intentFloatWindow);
		} else {
			Intent intentFloatWindow = new Intent(context,
					FloatWindowService.class);
			context.stopService(intentFloatWindow);
		}
	}

}
