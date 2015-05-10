package com.tchip.carlauncher.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.util.SettingUtil;
import com.tchip.carlauncher.view.NumberSeekBar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
