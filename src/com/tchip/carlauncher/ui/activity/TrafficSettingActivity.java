package com.tchip.carlauncher.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.tchip.carlauncher.service.TrafficFloatBarService;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.util.TrafficAndroidUtils;
import com.tchip.carlauncher.util.TrafficPreferencesUtils;

public class TrafficSettingActivity extends Activity {

	public static final String TAG_FLOAT = "tag_float";

	private CheckBox float_check;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trafftic_setting_activity);

		initView();
	}

	private void initView() {
		float_check = TrafficAndroidUtils.findViewById(this, R.id.float_check);

		float_check.setChecked(TrafficPreferencesUtils.getInstance().isChecked(this,
				TAG_FLOAT));

		setListener();
	}

	private void setListener() {
		float_check
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						TrafficPreferencesUtils.getInstance().setChecked(
								TrafficSettingActivity.this, TAG_FLOAT, isChecked);
						if (isChecked) {
							Intent serviceIntent = new Intent(
									TrafficSettingActivity.this, TrafficFloatBarService.class);
							startService(serviceIntent);
						} else {
							Intent brodcastIntent = new Intent(
									TrafficFloatBarService.ACTION_SERVICE);
							sendBroadcast(brodcastIntent);
						}
					}
				});
	}
}
