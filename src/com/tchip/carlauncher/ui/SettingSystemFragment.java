package com.tchip.carlauncher.ui;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.view.LayoutRipple;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingSystemFragment extends Fragment {
	private View systemSettingView;
	private Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		systemSettingView = inflater.inflate(R.layout.fragment_setting_system,
				container, false);
		context = getActivity();

		// 亮度设置
		LayoutRipple layoutRippleDisplay = (LayoutRipple) systemSettingView
				.findViewById(R.id.layoutRippleDisplay);
		iniRipple(layoutRippleDisplay);
		layoutRippleDisplay.setOnClickListener(new MyOnClickListener());

		// Wi-Fi
		LayoutRipple layoutRippleWifi = (LayoutRipple) systemSettingView
				.findViewById(R.id.layoutRippleWifi);
		iniRipple(layoutRippleWifi);
		layoutRippleWifi.setOnClickListener(new MyOnClickListener());

		// 蓝牙
		LayoutRipple layoutRippleBluetooth = (LayoutRipple) systemSettingView
				.findViewById(R.id.layoutRippleBluetooth);
		iniRipple(layoutRippleBluetooth);
		layoutRippleBluetooth.setOnClickListener(new MyOnClickListener());

		// 日期
		LayoutRipple layoutRippleDate = (LayoutRipple) systemSettingView
				.findViewById(R.id.layoutRippleDate);
		iniRipple(layoutRippleDate);
		layoutRippleDate.setOnClickListener(new MyOnClickListener());

		// 声音
		LayoutRipple layoutRippleSound = (LayoutRipple) systemSettingView
				.findViewById(R.id.layoutRippleSound);
		iniRipple(layoutRippleSound);
		layoutRippleSound.setOnClickListener(new MyOnClickListener());

		// 关于
		LayoutRipple layoutRippleAbout = (LayoutRipple) systemSettingView
				.findViewById(R.id.layoutRippleAbout);
		iniRipple(layoutRippleAbout);
		layoutRippleAbout.setOnClickListener(new MyOnClickListener());

		// 应用
		LayoutRipple layoutRippleApp = (LayoutRipple) systemSettingView
				.findViewById(R.id.layoutRippleApp);
		iniRipple(layoutRippleApp);
		layoutRippleApp.setOnClickListener(new MyOnClickListener());

		return systemSettingView;
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layoutRippleDisplay:
				Intent intent = new Intent(context,
						SettingSystemDisplayActivity.class);
				startActivity(intent);
				break;
			case R.id.layoutRippleWifi:
				// SETTINGS 设置主界面
				// WIRELESS_SETTINGS 更多网络
				// WIFI_DISPLAY_SETTINGS
				// LOCALE_SETTINGS
				// INPUT_METHOD_SETTINGS
				// INPUT_METHOD_SUBTYPE_SETTINGS
				// USER_DICTIONARY_SETTINGS
				// DISPLAY_SETTINGS
				// SAFETY
				// SECURITY_SETTINGS
				// APPLICATION_SETTINGS
				// MANAGE_ALL_APPLICATIONS_SETTINGS
				// MANAGE_PACKAGE_STORAGE
				// LOCATION_SOURCE_SETTINGS
				// PRIVACY_SETTINGS
				// BACKUP_AND_RESET_SETTINGS
				// ACCESSIBILITY_SETTINGS
				// QUICK_LAUNCH_SETTINGS
				// APPLICATION_DEVELOPMENT_SETTINGS
				// STORAGE_USB_SETTINGS
				// POWER_USAGE_SUMMARY
				// ACCOUNT_SYNC_SETTINGS
				// SYNC_SETTINGS
				// USER_SETTINGS
				// SIM_MANAGEMENT_ACTIVITY
				// SIM_LIST_ENTRANCE_ACTIVITY

				// PICK_WIFI_NETWORK

				startActivity(new Intent(
						android.provider.Settings.ACTION_WIFI_SETTINGS));
				break;
			case R.id.layoutRippleBluetooth:
				startActivity(new Intent(
						android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
				break;
			case R.id.layoutRippleDate:
				startActivity(new Intent(
						android.provider.Settings.ACTION_DATE_SETTINGS));
				break;
			case R.id.layoutRippleSound:
				startActivity(new Intent(
						android.provider.Settings.ACTION_SOUND_SETTINGS));
				break;
			case R.id.layoutRippleAbout:
				startActivity(new Intent(
						android.provider.Settings.ACTION_DEVICE_INFO_SETTINGS));
				break;
			case R.id.layoutRippleApp:
				startActivity(new Intent(
						android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS));
				break;
			default:
				break;
			}
		}
	}

	private void iniRipple(final LayoutRipple layoutRipple) {

		layoutRipple.post(new Runnable() {
			@Override
			public void run() {
				// 让Ripple的圆心在Image处
				// View v = layoutRipple.getChildAt(0);
				// layoutRipple.setxRippleOrigin(ViewHelper.getX(v) +
				// v.getWidth()
				// / 2);
				// layoutRipple.setyRippleOrigin(ViewHelper.getY(v)
				// + v.getHeight() / 2);
				layoutRipple.setRippleColor(Color.parseColor("#1E88E5"));
				layoutRipple.setRippleSpeed(50);
			}
		});
	}

}
