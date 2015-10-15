package com.tchip.carlauncher.ui.fragment;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.ui.activity.SettingSystemDisplayActivity;
import com.tchip.carlauncher.ui.activity.SettingSystemVolumeActivity;
import com.tchip.carlauncher.view.LayoutRipple;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
		RelativeLayout layoutRippleDisplay = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleDisplay);
		layoutRippleDisplay.setOnClickListener(new MyOnClickListener());

		// Wi-Fi
		RelativeLayout layoutRippleWifi = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleWifi);
		layoutRippleWifi.setOnClickListener(new MyOnClickListener());

		// 流量(GONE)
		RelativeLayout layoutRippleTraffic = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleTraffic);
		layoutRippleTraffic.setOnClickListener(new MyOnClickListener());

		// 蓝牙
		RelativeLayout layoutRippleBluetooth = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleBluetooth);
		layoutRippleBluetooth.setOnClickListener(new MyOnClickListener());

		// 位置信息
		RelativeLayout layoutRippleLocation = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleLocation);
		layoutRippleLocation.setOnClickListener(new MyOnClickListener());

		// 存储(GONE)
		RelativeLayout layoutRippleStorage = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleUsb);
		layoutRippleStorage.setOnClickListener(new MyOnClickListener());

		// 日期
		RelativeLayout layoutRippleDate = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleDate);
		layoutRippleDate.setOnClickListener(new MyOnClickListener());

		// 声音
		RelativeLayout layoutRippleSound = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleSound);
		layoutRippleSound.setOnClickListener(new MyOnClickListener());

		// FM
		RelativeLayout layoutRippleFm = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleFm);
		layoutRippleFm.setOnClickListener(new MyOnClickListener());

		// 恢复出厂设置
		RelativeLayout layoutRippleReset = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleReset);
		layoutRippleReset.setOnClickListener(new MyOnClickListener());

		// 关于(GONE)
		RelativeLayout layoutRippleAbout = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleAbout);
		layoutRippleAbout.setOnClickListener(new MyOnClickListener());

		// 应用(GONE)
		RelativeLayout layoutRippleApp = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleApp);
		layoutRippleApp.setOnClickListener(new MyOnClickListener());

		return systemSettingView;
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layoutRippleDisplay:
				Intent intentDisplay = new Intent(context,
						SettingSystemDisplayActivity.class);
				startActivity(intentDisplay);
				break;
			case R.id.layoutRippleWifi:
				// SETTINGS 设置主界面
				// WIRELESS_SETTINGS 更多网络
				// WIFI_DISPLAY_SETTINGS
				// LOCALE_SETTINGS 语言设置
				// INPUT_METHOD_SETTINGS
				// INPUT_METHOD_SUBTYPE_SETTINGS
				// USER_DICTIONARY_SETTINGS
				// DISPLAY_SETTINGS
				// SAFETY
				// SECURITY_SETTINGS
				// APPLICATION_SETTINGS
				// MANAGE_ALL_APPLICATIONS_SETTINGS
				// MANAGE_PACKAGE_STORAGE
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
			case R.id.layoutRippleTraffic:
				break;
			case R.id.layoutRippleBluetooth:
				startActivity(new Intent(
						android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
				break;
			case R.id.layoutRippleLocation:
				startActivity(new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				break;
			case R.id.layoutRippleUsb:
				startActivity(new Intent(
						android.provider.Settings.ACTION_MEMORY_CARD_SETTINGS));
				break;
			case R.id.layoutRippleDate:
				startActivity(new Intent(
						android.provider.Settings.ACTION_DATE_SETTINGS));
				break;
			case R.id.layoutRippleSound:
				// startActivity(new Intent(
				// android.provider.Settings.ACTION_SOUND_SETTINGS));
				Intent intentVolume = new Intent(context,
						SettingSystemVolumeActivity.class);
				startActivity(intentVolume);
				break;
			case R.id.layoutRippleFm:
				startActivity(new Intent("android.settings.FM_SETTINGS"));
				break;
			case R.id.layoutRippleReset:
				startActivity(new Intent(
						"android.settings.BACKUP_AND_RESET_SETTINGS"));
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
				layoutRipple.setRippleSpeed(Constant.SETTING_ITEM_RIPPLE_SPEED);
			}
		});
	}

}
