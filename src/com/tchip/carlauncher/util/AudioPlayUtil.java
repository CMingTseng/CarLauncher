package com.tchip.carlauncher.util;

import com.tchip.carlauncher.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

public class AudioPlayUtil {

	private static MediaPlayer mediaPlayer;

	public static void playAudio(Context context, int type) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		int volume = audioManager
				.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

		if (volume > 0) {
			if (type == com.tchip.tachograph.TachographCallback.FILE_TYPE_IMAGE) {
				mediaPlayer = MediaPlayer.create(context, R.raw.camera_image);
			} else {
				mediaPlayer = MediaPlayer.create(context, R.raw.camera_video);
			}
			mediaPlayer.start();
		}

	}

}
