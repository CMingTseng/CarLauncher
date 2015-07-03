package com.tchip.carlauncher.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.adapter.WiFiInfoAdapter;
import com.tchip.carlauncher.model.WifiInfo;
import com.tchip.carlauncher.ui.activity.WeatherActivity.MyOnClickListener;
import com.tchip.carlauncher.util.WifiAdmin;
import com.tchip.carlauncher.view.SwitchButton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class WifiListActivity extends Activity {

	private ArrayList<WifiInfo> wifiArray;
	private WiFiInfoAdapter wifiInfoAdapter;
	private ListView listWifi;

	private ProgressBar updateProgress;
	private Button updateButton;

	private WifiManager wifiManager;
	private WifiAdmin wiFiAdmin;
	private List<ScanResult> list;
	private ScanResult mScanResult;
	private StringBuffer sb = new StringBuffer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_wifi_list);

		wiFiAdmin = new WifiAdmin(WifiListActivity.this);
		initLayout();
		// if(wifi.isOn)
		getAllNetWorkList();
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

			listWifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(android.widget.AdapterView<?> parent,
						android.view.View view, int position, long id) {
					Toast.makeText(getApplicationContext(),
							"Mac:" + list.get(position).BSSID,
							Toast.LENGTH_SHORT).show();
					// focusItemPos = position;
					// Intent intent = new Intent(RouteListActivity.this,
					// RouteShowActivity.class);
					// intent.putExtra("filePath",
					// fileNameList.get(position));
					// startActivity(intent);
					// overridePendingTransition(
					// R.anim.zms_translate_left_out,
					// R.anim.zms_translate_left_in);
				}
			});
		}
	}
}
