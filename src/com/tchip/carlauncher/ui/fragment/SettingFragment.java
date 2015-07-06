package com.tchip.carlauncher.ui.fragment;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.bluetooth.DiscoveryActivity;
import com.tchip.carlauncher.ui.activity.SettingSystemDisplayActivity;
import com.tchip.carlauncher.ui.activity.SettingSystemVolumeActivity;
import com.tchip.carlauncher.ui.activity.TrafficStatActivity;
import com.tchip.carlauncher.ui.activity.WifiListActivity;
import com.tchip.carlauncher.view.LayoutRipple;
import com.tchip.carlauncher.view.SwitchButton;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingFragment extends Fragment {
	private View systemSettingView;
	private Context context;
	private SharedPreferences preferences;
	private Editor editor;
	private BluetoothAdapter bluetoothAdapter;
	private WifiManager wifiManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		systemSettingView = inflater.inflate(R.layout.fragment_setting_system,
				container, false);
		context = getActivity();

		preferences = getActivity().getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		editor = preferences.edit();

		// 开机自动录像流量(GONE)
		SwitchButton switchAutoRecord = (SwitchButton) systemSettingView
				.findViewById(R.id.switchAutoRecord);
		switchAutoRecord
				.setChecked(preferences.getBoolean("autoRecord", false));
		switchAutoRecord
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						editor.putBoolean("autoRecord", isChecked);
						editor.commit();
					}
				});

		// 亮度设置
		RelativeLayout layoutRippleDisplay = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleDisplay);
		layoutRippleDisplay.setOnClickListener(new MyOnClickListener());

		// Wi-Fi
		RelativeLayout layoutRippleWifi = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleWifi);
		layoutRippleWifi.setOnClickListener(new MyOnClickListener());
		SwitchButton switchWifi = (SwitchButton) systemSettingView
				.findViewById(R.id.switchWifi);
		wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		switchWifi.setChecked(wifiManager.isWifiEnabled());
		switchWifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				wifiManager.setWifiEnabled(isChecked);
			}
		});

		// 流量(GONE)
		RelativeLayout layoutRippleTraffic = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleTraffic);
		layoutRippleTraffic.setOnClickListener(new MyOnClickListener());

		// 蓝牙
		RelativeLayout layoutRippleBluetooth = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleBluetooth);
		layoutRippleBluetooth.setOnClickListener(new MyOnClickListener());

		SwitchButton switchBluetooth = (SwitchButton) systemSettingView
				.findViewById(R.id.switchBluetooth);
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		switchBluetooth.setChecked(bluetoothAdapter.isEnabled());
		switchBluetooth
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked)
							bluetoothAdapter.enable();
						else
							bluetoothAdapter.disable();
					}
				});

		// 位置信息
		RelativeLayout layoutRippleLocation = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleLocation);
		layoutRippleLocation.setOnClickListener(new MyOnClickListener());

		// 存储(GONE)
		RelativeLayout layoutRippleStorage = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleStorage);
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

		// 恢复出厂设置(GONE)
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
//				startActivity(new Intent(
//						android.provider.Settings.ACTION_WIFI_SETTINGS));
				Intent intentWifi = new Intent(context,
						WifiListActivity.class);
				startActivity(intentWifi);
				break;

			case R.id.layoutRippleTraffic:
				Intent intentTraffic = new Intent(context,
						TrafficStatActivity.class);
				startActivity(intentTraffic);
				break;

			case R.id.layoutRippleBluetooth:
				startActivity(new Intent(
						android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
				// Intent intentBluetooth = new Intent(context,
				// DiscoveryActivity.class);
				// startActivity(intentBluetooth);
				break;

			case R.id.layoutRippleLocation:
				startActivity(new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				break;

			case R.id.layoutRippleStorage:
				startActivity(new Intent(
						android.provider.Settings.ACTION_MEMORY_CARD_SETTINGS));
				break;

			case R.id.layoutRippleDate:
//				startActivity(new Intent(
//						android.provider.Settings.ACTION_DATE_SETTINGS));
				
				ComponentName componentImage = new ComponentName(
						"com.mediatek.oobe",
						"com.mediatek.oobe.basic.MainActivity");
				Intent intentImage = new Intent();
				intentImage.setComponent(componentImage);
				startActivity(intentImage);
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
