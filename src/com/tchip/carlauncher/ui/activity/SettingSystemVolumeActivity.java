package com.tchip.carlauncher.ui.activity;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.model.Typefaces;
import com.tchip.carlauncher.util.SettingUtil;
import com.tchip.carlauncher.view.MaterialSwitch;
import com.tchip.carlauncher.view.NumberSeekBar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextClock;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SettingSystemVolumeActivity extends Activity {

	private Context context;

	private SharedPreferences sharedPreferences;
	private Editor editor;
	private AudioManager audioManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_setting_system_volume);

		context = getApplicationContext();

		sharedPreferences = getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();

		// 返回
		RelativeLayout layoutToSettingFromBright = (RelativeLayout) findViewById(R.id.layoutToSettingFromBright);
		layoutToSettingFromBright.setOnClickListener(new MyOnClickListener());

		// 时钟
		TextClock textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setTypeface(Typefaces.get(this, Constant.FONT_PATH
				+ "Font-Helvetica-Neue-LT-Pro.otf"));

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		// 媒体音量SeekBar
		int color = Color.parseColor("#1E88E5");
		NumberSeekBar volumeMedia = (NumberSeekBar) findViewById(R.id.volumeMedia);
		volumeMedia.setBackgroundColor(color);
		volumeMedia.setShowNumberIndicator(true);
		volumeMedia.setMin(0);
		volumeMedia.setMax(audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		volumeMedia.setValue(audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC));
		volumeMedia
				.setOnValueChangedListener(new NumberSeekBar.OnValueChangedListener() {

					@Override
					public void onValueChanged(int value) {
						audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
								value, 0);
					}
				});

		// 铃声音量SeekBar

		NumberSeekBar volumeRing = (NumberSeekBar) findViewById(R.id.volumeRing);
		volumeRing.setBackgroundColor(color);
		volumeRing.setShowNumberIndicator(true);
		volumeRing.setMin(0);
		volumeRing.setMax(audioManager
				.getStreamMaxVolume(AudioManager.STREAM_RING));
		volumeRing.setValue(audioManager
				.getStreamVolume(AudioManager.STREAM_RING));
		volumeRing
				.setOnValueChangedListener(new NumberSeekBar.OnValueChangedListener() {

					@Override
					public void onValueChanged(int value) {
						audioManager.setStreamVolume(AudioManager.STREAM_RING,
								value, 0);
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
