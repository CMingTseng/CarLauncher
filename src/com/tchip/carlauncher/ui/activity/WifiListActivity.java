package com.tchip.carlauncher.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.adapter.WiFiInfoAdapter;
import com.tchip.carlauncher.model.WifiInfo;
import com.tchip.carlauncher.ui.dialog.WifiPswDialog;
import com.tchip.carlauncher.ui.dialog.WifiPswDialog.OnCustomDialogListener;
import com.tchip.carlauncher.util.WifiAdmin;
import com.tchip.carlauncher.view.SwitchButton;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class WifiListActivity extends Activity {

	private ArrayList<WifiInfo> wifiArray;
	private WiFiInfoAdapter wifiInfoAdapter;
	private ListView listWifi;

	private ProgressBar updateProgress;
	private Button updateButton, btnWifiAp;
	private String wifiPassword = null;

	private WifiManager wifiManager;
	private WifiAdmin wiFiAdmin;
	private List<ScanResult> list;
	private ScanResult mScanResult;
	private StringBuffer sb = new StringBuffer();

	private SharedPreferences sharedPreferences;
	private Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_wifi_list);

		sharedPreferences = getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();

		wiFiAdmin = new WifiAdmin(WifiListActivity.this);
		initLayout();

		if (wifiManager.isWifiEnabled()) {
			getAllNetWorkList();
		} else {
			// TODO:提示打开WiFi
		}
	}

	public void initLayout() {
		listWifi = (ListView) findViewById(R.id.listWiFi);
		RelativeLayout btnToSettingFromWiFi = (RelativeLayout) findViewById(R.id.btnToSettingFromWiFi);
		btnToSettingFromWiFi.setOnClickListener(new MyOnClickListener());

		// 刷新按钮和进度条
		updateProgress = (ProgressBar) findViewById(R.id.updateProgress);
		updateProgress.setVisibility(View.INVISIBLE);
		updateButton = (Button) findViewById(R.id.updateButton);
		updateButton.setVisibility(View.VISIBLE);
		updateButton.setOnClickListener(new MyOnClickListener());

		SwitchButton switchWifi = (SwitchButton) findViewById(R.id.switchWifi);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		switchWifi.setChecked(wifiManager.isWifiEnabled());
		switchWifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				wifiManager.setWifiEnabled(isChecked);
				// 更新WiFi列表
				if (isChecked) {
					listWifi.setVisibility(View.VISIBLE);
					updateProgress.setVisibility(View.VISIBLE);
					updateButton.setVisibility(View.INVISIBLE);
					new Thread(new refreshWifiThread()).start();
				} else {
					listWifi.setVisibility(View.GONE);
				}
			}
		});

		btnWifiAp = (Button) findViewById(R.id.btnWifiAp);
		btnWifiAp.setOnClickListener(new MyOnClickListener());

	}

	final Handler refreshWifiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				getAllNetWorkList();
				updateProgress.setVisibility(View.INVISIBLE);
				updateButton.setVisibility(View.VISIBLE);
				break;

			default:
				break;
			}
		}
	};

	public class refreshWifiThread implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(3000);
				Message message = new Message();
				message.what = 1;
				refreshWifiHandler.sendMessage(message);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnToSettingFromWiFi:
				finish();
				break;

			case R.id.updateButton:
				updateButton.setVisibility(View.INVISIBLE);
				updateProgress.setVisibility(View.VISIBLE);
				new Thread(new refreshWifiThread()).start();
				break;

			case R.id.btnWifiAp:
				Intent intent = new Intent();
				ComponentName comp = new ComponentName("com.android.settings",
						"com.android.settings.TetherSettings");
				intent.setComponent(comp);
				intent.setAction("android.intent.action.VIEW");
				startActivityForResult(intent, 0);
				break;

			default:
				break;
			}
		}

	}

	public void getAllNetWorkList() {

		wifiArray = new ArrayList<WifiInfo>();
		if (sb != null) {
			sb = new StringBuffer();
		}
		wiFiAdmin.startScan();
		list = wiFiAdmin.getWifiList();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				mScanResult = list.get(i);
				WifiInfo wifiInfo = new WifiInfo(mScanResult.BSSID,
						mScanResult.SSID, mScanResult.capabilities,
						mScanResult.level);
				wifiArray.add(wifiInfo);
			}

			wifiInfoAdapter = new WiFiInfoAdapter(getApplicationContext(),
					wifiArray);
			listWifi.setAdapter(wifiInfoAdapter);

			//
			wiFiAdmin.getConfiguration();

			listWifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				String wifiItemSSID = null;

				public void onItemClick(android.widget.AdapterView<?> parent,
						android.view.View view, int position, long id) {

					Log.d(Constant.TAG, "BSSID:" + list.get(position).BSSID);

					// 连接WiFi
					wifiItemSSID = list.get(position).SSID;
					int wifiItemId = wiFiAdmin.IsConfiguration("\""
							+ list.get(position).SSID + "\"");
					if (wifiItemId != -1) {
						if (wiFiAdmin.ConnectWifi(wifiItemId)) {
							// 连接已保存密码的WiFi
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.wifi_connecting),
									Toast.LENGTH_SHORT).show();
							updateButton.setVisibility(View.INVISIBLE);
							updateProgress.setVisibility(View.VISIBLE);
							new Thread(new refreshWifiThread()).start();
						}
					} else {
						// 没有配置好信息，配置
						WifiPswDialog pswDialog = new WifiPswDialog(
								WifiListActivity.this,
								new OnCustomDialogListener() {
									@Override
									public void back(String str) {
										wifiPassword = str;
										if (wifiPassword != null) {
											int netId = wiFiAdmin
													.AddWifiConfig(list,
															wifiItemSSID,
															wifiPassword);
											if (netId != -1) {
												wiFiAdmin.getConfiguration();// 添加了配置信息，要重新得到配置信息
												if (wiFiAdmin
														.ConnectWifi(netId)) {
													// 保存最后连接的wifi信息
													editor.putString(
															"wifiName",
															wifiItemSSID);
													editor.putString(
															"wifiPass",
															wifiPassword);
													editor.commit();

													// 连接成功，刷新UI
													updateProgress
															.setVisibility(View.VISIBLE);
													updateButton
															.setVisibility(View.INVISIBLE);
													new Thread(
															new refreshWifiThread())
															.start();
												}
											} else {
												// 网络连接错误
											}
										} else {
										}
									}
								});
						pswDialog.show();
					}
				}
			});
		}
	}

}
