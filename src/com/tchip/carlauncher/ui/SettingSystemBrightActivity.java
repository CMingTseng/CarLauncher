package com.tchip.carlauncher.ui;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.util.SettingUtil;
import com.tchip.carlauncher.view.NumberSeekBar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

public class SettingSystemBrightActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_system_bright);
		
		
		int color = Color.parseColor("#1E88E5");

		NumberSeekBar brightSeekBar = (NumberSeekBar)findViewById(R.id.brightSeekBar);
		brightSeekBar.setBackgroundColor(color);
		brightSeekBar.setShowNumberIndicator(true);
		brightSeekBar.setMin(0);
		brightSeekBar.setMax(255);
		brightSeekBar.setValue(SettingUtil
				.getBrightness(getApplicationContext()));
		brightSeekBar
				.setOnValueChangedListener(new NumberSeekBar.OnValueChangedListener() {

					@Override
					public void onValueChanged(int value) {
						SettingUtil.setBrightness(
								getApplicationContext(), value);
					}
				});
	}

}
