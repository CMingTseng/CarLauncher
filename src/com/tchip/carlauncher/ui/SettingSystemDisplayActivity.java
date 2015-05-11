package com.tchip.carlauncher.ui;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.util.SettingUtil;
import com.tchip.carlauncher.view.MaterialSwitch;
import com.tchip.carlauncher.view.NumberSeekBar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class SettingSystemDisplayActivity extends Activity {

	private MaterialSwitch nightSwitch;
	private Context context;
	private RadioGroup screenOffGroup;
	private RadioButton screenOff1min, screenOff2min, screenOff5min,
			screenOff10min, screenOffNone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_system_display);
		setTitle("显示设置");

		context = getApplicationContext();

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
		nightSwitch.setChecked(true);
		nightSwitch.setOnCheckListener(new MySwitchOnCheckListener());

		Toast.makeText(context,
				"time:" + SettingUtil.getScreenOffTime(context),
				Toast.LENGTH_SHORT).show();

		// 屏幕关闭RadioGroup
		iniRadioGroup();
	}

	private void iniRadioGroup() {
		screenOffGroup = (RadioGroup) findViewById(R.id.screenOffGroup);
		screenOffGroup
				.setOnCheckedChangeListener(new MyRadioOnCheckedListener());
		screenOff1min = (RadioButton) findViewById(R.id.screenOff1min);
		screenOff2min = (RadioButton) findViewById(R.id.screenOff2min);
		screenOff5min = (RadioButton) findViewById(R.id.screenOff5min);
		screenOff10min = (RadioButton) findViewById(R.id.screenOff10min);
		screenOffNone = (RadioButton) findViewById(R.id.screenOffNone);
		int nowScreenOffTime = SettingUtil.getScreenOffTime(context);
		switch (nowScreenOffTime) {
		case 60000:
			screenOff1min.setChecked(true);
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
			// TODO Auto-generated method stub

		}
	}

	class MySwitchOnCheckListener implements MaterialSwitch.OnCheckListener {

		@Override
		public void onCheck(boolean check) {
			Toast.makeText(context, "isCheck:" + check, Toast.LENGTH_SHORT)
					.show();
		}
	}

}
