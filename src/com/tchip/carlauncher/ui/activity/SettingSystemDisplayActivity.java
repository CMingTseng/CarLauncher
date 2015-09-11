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
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextClock;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

public class SettingSystemDisplayActivity extends Activity {

	private MaterialSwitch nightSwitch;
	private Context context;
	private RadioGroup screenOffGroup;
	private RadioButton screenOff30Second, screenOff2min, screenOff5min,
			screenOff10min, screenOffNone;

	private SharedPreferences sharedPreferences;
	private Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_setting_system_display);

		context = getApplicationContext();

		sharedPreferences = getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();

		// 返回
		RelativeLayout layoutToSettingFromBright = (RelativeLayout) findViewById(R.id.layoutToSettingFromBright);
		layoutToSettingFromBright.setOnClickListener(new MyOnClickListener());

		// 时钟
		TextClock textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setTypeface(Typefaces.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf"));

		// 亮度SeekBar
		int color = Color.parseColor("#1E88E5");
		NumberSeekBar brightSeekBar = (NumberSeekBar) findViewById(R.id.brightSeekBar);
		brightSeekBar.setBackgroundColor(color);
		brightSeekBar.setShowNumberIndicator(true);
		brightSeekBar.setMin(0);
		brightSeekBar.setMax(255);
		brightSeekBar.setValue(SettingUtil.getBrightness(context));
		brightSeekBar
				.setOnValueChangedListener(new NumberSeekBar.OnValueChangedListener() {

					@Override
					public void onValueChanged(int value) {
						SettingUtil.setBrightness(context, value);
					}
				});

		// 夜晚亮度自动降低Switch
		nightSwitch = (MaterialSwitch) findViewById(R.id.nightSwitch);
		nightSwitch.setChecked(sharedPreferences.getBoolean("brightAdjust",
				false));
		nightSwitch.setOnCheckListener(new MySwitchOnCheckListener());

		// 屏幕关闭RadioGroup
		iniRadioGroup();
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

	private void iniRadioGroup() {
		screenOffGroup = (RadioGroup) findViewById(R.id.screenOffGroup);
		screenOffGroup
				.setOnCheckedChangeListener(new MyRadioOnCheckedListener());
		screenOff30Second = (RadioButton) findViewById(R.id.screenOff30Second);
		screenOff2min = (RadioButton) findViewById(R.id.screenOff2min);
		screenOff5min = (RadioButton) findViewById(R.id.screenOff5min);
		screenOff10min = (RadioButton) findViewById(R.id.screenOff10min);
		screenOffNone = (RadioButton) findViewById(R.id.screenOffNone);
		int nowScreenOffTime = SettingUtil.getScreenOffTime(context);
		switch (nowScreenOffTime) {
		case 30000:
			screenOff30Second.setChecked(true);
			break;
		case 120000:
			screenOff2min.setChecked(true);
			break;
		case 300000:
			screenOff5min.setChecked(true);
			break;
		case 600000:
			screenOff10min.setChecked(true);
			break;
		default:
			screenOffNone.setChecked(true);
			break;
		}
	}

	class MyRadioOnCheckedListener implements OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.screenOff30Second:
				SettingUtil.setScreenOffTime(context, 30000);
				break;
			case R.id.screenOff2min:
				SettingUtil.setScreenOffTime(context, 120000);
				break;
			case R.id.screenOff5min:
				SettingUtil.setScreenOffTime(context, 300000);
				break;
			case R.id.screenOff10min:
				SettingUtil.setScreenOffTime(context, 600000);
				break;
			case R.id.screenOffNone:
				// 240小时
				SettingUtil.setScreenOffTime(context, 864000000);
				break;

			default:
				break;
			}
		}
	}

	class MySwitchOnCheckListener implements MaterialSwitch.OnCheckListener {

		@Override
		public void onCheck(boolean check) {
			editor.putBoolean("brightAdjust", check);
			editor.commit();
		}
	}

}
