package com.tchip.carlauncher.ui;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.util.SettingUtil;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class SettingSystemFragment extends Fragment {
	private View systemSettingView;
	private Button btnBrightness;
	private Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		systemSettingView = inflater.inflate(R.layout.fragment_setting_system,
				container, false);
		context = getActivity();

		// brightSeekBar = (NumberSeekBar) systemSettingView
		// .findViewById(R.id.brightSeekBar);
		btnBrightness = (Button) systemSettingView
				.findViewById(R.id.btnBrightness);
		btnBrightness.setOnClickListener(new MyOnClickListener());


		return systemSettingView;
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnBrightness:
				Intent intent = new Intent(context,
						SettingSystemDisplayActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}

		}
	}

}
