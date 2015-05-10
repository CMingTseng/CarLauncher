package com.tchip.carlauncher.ui;

import com.tchip.carlauncher.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SettingSystemFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View systemSettingView = inflater.inflate(
				R.layout.fragment_setting_system, container, false);

		// brightSeekBar = (NumberSeekBar) systemSettingView
		// .findViewById(R.id.brightSeekBar);
		Button btnBrightness = (Button) systemSettingView
				.findViewById(R.id.btnBrightness);
		btnBrightness.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						SettingSystemBrightActivity.class);
				startActivity(intent);
			}
		});

		return systemSettingView;
	}
}
