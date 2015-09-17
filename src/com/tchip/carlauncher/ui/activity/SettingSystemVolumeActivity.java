package com.tchip.carlauncher.ui.activity;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.model.Typefaces;
import com.tchip.carlauncher.view.NumberSeekBar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextClock;
import android.widget.RelativeLayout;

public class SettingSystemVolumeActivity extends Activity {

	private AudioManager audioManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_setting_system_volume);

		// 返回
		RelativeLayout layoutToSettingFromBright = (RelativeLayout) findViewById(R.id.layoutToSettingFromBright);
		layoutToSettingFromBright.setOnClickListener(new MyOnClickListener());

		// 时钟
		TextClock textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setTypeface(Typefaces.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf"));

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		// 媒体音量SeekBar
		SeekBar volumeMedia = (SeekBar) findViewById(R.id.volumeMedia);
		volumeMedia.setMax(audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		volumeMedia.setProgress(audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC));
		volumeMedia.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						seekBar.getProgress(), 0);

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub

			}
		});

		// 铃声音量SeekBar

		SeekBar volumeRing = (SeekBar) findViewById(R.id.volumeRing);

		volumeRing.setMax(audioManager
				.getStreamMaxVolume(AudioManager.STREAM_RING));
		volumeRing.setProgress(audioManager
				.getStreamVolume(AudioManager.STREAM_RING));
		volumeRing.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				audioManager.setStreamVolume(AudioManager.STREAM_RING,
						seekBar.getProgress(), 0);

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub

			}
		});
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layoutToSettingFromBright:
				finish();
				break;

			default:
				break;
			}
		}
	}

}
