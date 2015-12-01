package com.tchip.carlauncher.ui.fragment;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.ui.activity.BluetoothListActivity;
import com.tchip.carlauncher.ui.activity.SettingGravityActivity;
import com.tchip.carlauncher.ui.activity.SettingSystemDisplayActivity;
import com.tchip.carlauncher.ui.activity.SettingSystemUsbActivity;
import com.tchip.carlauncher.ui.activity.SettingSystemVolumeActivity;
import com.tchip.carlauncher.ui.activity.UserCenterActivity;
import com.tchip.carlauncher.ui.activity.WifiListActivity;
import com.tchip.carlauncher.util.OpenUtil;
import com.tchip.carlauncher.util.OpenUtil.MODULE_TYPE;
import com.tchip.carlauncher.util.SettingUtil;
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
	// private BluetoothAdapter bluetoothAdapter;
	private WifiManager wifiManager;

	private SwitchButton switchWifi, switchBluetooth, switchParking;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		systemSettingView = inflater.inflate(R.layout.fragment_setting_system,
				container, false);
		context = getActivity();

		preferences = getActivity().getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		editor = preferences.edit();

		// 开机自动录像(GONE)
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

		// 用户中心
		RelativeLayout layoutUserCenter = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutUserCenter);
		layoutUserCenter.setOnClickListener(new MyOnClickListener());
		View lineUserCenter = systemSettingView
				.findViewById(R.id.lineUserCenter);
		if (!Constant.Module.hasUserCenter) {
			layoutUserCenter.setVisibility(View.GONE);
			lineUserCenter.setVisibility(View.GONE);
		}

		// 亮度设置
		RelativeLayout layoutRippleDisplay = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleDisplay);
		layoutRippleDisplay.setOnClickListener(new MyOnClickListener());

		// 微密
		RelativeLayout layoutWeme = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutWeme);
		layoutWeme.setOnClickListener(new MyOnClickListener());

		// Wi-Fi
		RelativeLayout layoutRippleWifi = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleWifi);
		layoutRippleWifi.setOnClickListener(new MyOnClickListener());
		switchWifi = (SwitchButton) systemSettingView
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

		// 热点共享
		RelativeLayout layoutWifiAp = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutWifiAp);
		layoutWifiAp.setOnClickListener(new MyOnClickListener());

		// 流量使用情况
		RelativeLayout layoutRippleTraffic = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleTraffic);
		layoutRippleTraffic.setOnClickListener(new MyOnClickListener());

		// 碰撞侦测
		RelativeLayout layoutGravity = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutGravity);
		layoutGravity.setOnClickListener(new MyOnClickListener());

		// 停车侦测开关
		switchParking = (SwitchButton) systemSettingView
				.findViewById(R.id.switchParking);
		switchParking.setChecked(isParkingMonitorOn());
		switchParking.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SettingUtil.setParkingMonitor(context, isChecked);
			}
		});
		RelativeLayout layoutRippleParking = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleParking);
		layoutRippleParking.setOnClickListener(new MyOnClickListener());

		// 蓝牙(GONE)
		// RelativeLayout layoutRippleBluetooth = (RelativeLayout)
		// systemSettingView
		// .findViewById(R.id.layoutRippleBluetooth);
		// layoutRippleBluetooth.setOnClickListener(new MyOnClickListener());
		//
		// switchBluetooth = (SwitchButton) systemSettingView
		// .findViewById(R.id.switchBluetooth);
		// bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// switchBluetooth.setChecked(bluetoothAdapter.isEnabled());
		// switchBluetooth
		// .setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// if (isChecked)
		// bluetoothAdapter.enable();
		// else
		// bluetoothAdapter.disable();
		// }
		// });

		// 位置信息
		RelativeLayout layoutRippleLocation = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleLocation);
		layoutRippleLocation.setOnClickListener(new MyOnClickListener());

		// 存储
		RelativeLayout layoutRippleStorage = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleStorage);
		layoutRippleStorage.setOnClickListener(new MyOnClickListener());

		// USB连接设置
		RelativeLayout layoutRippleUsb = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleUsb);
		layoutRippleUsb.setOnClickListener(new MyOnClickListener());

		// 日期
		RelativeLayout layoutRippleDate = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleDate);
		layoutRippleDate.setOnClickListener(new MyOnClickListener());

		// 声音
		RelativeLayout layoutRippleSound = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleSound);
		layoutRippleSound.setOnClickListener(new MyOnClickListener());

		// FM(GONE)
		RelativeLayout layoutRippleFm = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleFm);
		layoutRippleFm.setOnClickListener(new MyOnClickListener());

		// 恢复出厂设置(GONE)
		RelativeLayout layoutRippleReset = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleReset);
		layoutRippleReset.setOnClickListener(new MyOnClickListener());

		// 关于设备
		RelativeLayout layoutRippleAbout = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleAbout);
		layoutRippleAbout.setOnClickListener(new MyOnClickListener());

		// 应用(GONE)
		RelativeLayout layoutRippleApp = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutRippleApp);
		layoutRippleApp.setOnClickListener(new MyOnClickListener());

		// 拷贝地图(GONE)
		RelativeLayout layoutCopyMap = (RelativeLayout) systemSettingView
				.findViewById(R.id.layoutCopyMap);
		layoutCopyMap.setOnClickListener(new MyOnClickListener());

		return systemSettingView;
	}

	/**
	 * 停车侦测是否打开
	 * 
	 * @return
	 */
	private boolean isParkingMonitorOn() {
		return preferences.getBoolean("parkingOn",
				Constant.Record.parkDefaultOn);
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layoutRippleParking:
				switchParking.setChecked(!isParkingMonitorOn());
				break;

			case R.id.layoutUserCenter:
				Intent intentUserCenter = new Intent(context,
						UserCenterActivity.class);
				startActivity(intentUserCenter);
				break;

			case R.id.layoutCopyMap:
				break;

			case R.id.layoutRippleDisplay:
				Intent intentDisplay = new Intent(context,
						SettingSystemDisplayActivity.class);
				startActivity(intentDisplay);
				break;

			case R.id.layoutWeme:
				// 微密
				OpenUtil.openModule(getActivity(), MODULE_TYPE.WEME);
				break;

			case R.id.layoutRippleWifi:
				OpenUtil.openModule(getActivity(), MODULE_TYPE.WIFI);
				break;

			case R.id.layoutWifiAp:
				// try {
				// Intent intent = new Intent();
				// ComponentName comp = new ComponentName(
				// "com.android.settings",
				// "com.android.settings.TetherSettings");
				// intent.setComponent(comp);
				// intent.setAction("android.intent.action.VIEW");
				// startActivityForResult(intent, 0);
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				try {
					startActivity(new Intent(
							"android.settings.TETHER_WIFI_SETTINGS"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case R.id.layoutRippleTraffic:
				// Intent intentTraffic = new Intent(context,
				// TrafficStatActivity.class);
				// startActivity(intentTraffic);
				try {
					startActivity(new Intent(
							"android.settings.DATA_USAGE_SETTINGS"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case R.id.layoutGravity:
				Intent intentGravity = new Intent(context,
						SettingGravityActivity.class);
				startActivity(intentGravity);
				break;

			case R.id.layoutRippleBluetooth:
				// startActivity(new Intent(
				// android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
				Intent intentBluetooth = new Intent(context,
						BluetoothListActivity.class);
				startActivity(intentBluetooth);
				break;

			case R.id.layoutRippleLocation:
				try {
					startActivity(new Intent(
							android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case R.id.layoutRippleStorage:
				try {
					startActivity(new Intent(
							android.provider.Settings.ACTION_MEMORY_CARD_SETTINGS));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case R.id.layoutRippleUsb:
				// startActivity(new Intent(
				// "android.settings.STORAGE_USB_SETTINGS"));
				Intent intentUsb = new Intent(context,
						SettingSystemUsbActivity.class);
				startActivity(intentUsb);
				break;

			case R.id.layoutRippleDate:
				try {
					startActivity(new Intent(
							android.provider.Settings.ACTION_DATE_SETTINGS));
				} catch (Exception e) {
					e.printStackTrace();
				}

				// ComponentName componentImage = new ComponentName(
				// "com.android.settings", "com.android.settings.Settings");
				// Intent intentImage = new Intent();
				// intentImage.setComponent(componentImage);
				// startActivity(intentImage);
				break;

			case R.id.layoutRippleSound:
				// startActivity(new Intent(
				// android.provider.Settings.ACTION_SOUND_SETTINGS));
				Intent intentVolume = new Intent(context,
						SettingSystemVolumeActivity.class);
				startActivity(intentVolume);
				break;

			case R.id.layoutRippleFm:
				try {
					startActivity(new Intent("android.settings.FM_SETTINGS"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case R.id.layoutRippleReset:
				try {
					startActivity(new Intent(
							"android.settings.BACKUP_AND_RESET_SETTINGS"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case R.id.layoutRippleAbout:
				try {
					startActivity(new Intent(
							android.provider.Settings.ACTION_DEVICE_INFO_SETTINGS));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case R.id.layoutRippleApp:
				try {
					startActivity(new Intent(
							android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS));
				} catch (Exception e) {
					e.printStackTrace();
				}
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

	@Override
	public void onResume() {
		super.onResume();
		// TODO:应该启动新线程
		switchWifi.setChecked(wifiManager.isWifiEnabled());
		// switchBluetooth.setChecked(bluetoothAdapter.isEnabled());
	}

}
