package com.tchip.carlauncher.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tchip.carlauncher.service.TrafficFetchService;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.util.TrafficAndroidUtils;
import com.tchip.carlauncher.util.TrafficUtils;

public class TrafficStatActivity extends Activity {

	private TextView cur_type;
	private TextView cur_interval;
	private TextView total_mobile;
	private TextView day_mobile;
	private TextView total_wifi;
	private TextView day_wifi;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trafftic_main);

		initView();

		setCurNetType();

		new InitTotalInterfaceTask().execute();

		TrafficUtils.startRepeatingService(this, TrafficUtils.INTERVAL,
				TrafficFetchService.class, "");

		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mFilter.addAction(TrafficUtils.ACTION_UPDATE_TRAFFIC);
		registerReceiver(mReceiver, mFilter);
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	private void initView() {
		cur_type = TrafficAndroidUtils.findViewById(this, R.id.cur_type);
		cur_interval = TrafficAndroidUtils
				.findViewById(this, R.id.cur_interval);
		total_mobile = TrafficAndroidUtils
				.findViewById(this, R.id.total_mobile);
		day_mobile = TrafficAndroidUtils.findViewById(this, R.id.day_mobile);
		total_wifi = TrafficAndroidUtils.findViewById(this, R.id.total_wifi);
		day_wifi = TrafficAndroidUtils.findViewById(this, R.id.day_wifi);

		cur_interval.setText(String.format(getString(R.string.cur_interval),
				TrafficUtils.INTERVAL));
	}

	private void setCurNetType() {
		String type = TrafficUtils.netWorkTypeToString(this);
		cur_type.setText(String.format(getString(R.string.cur_type), type));
	}

	public void onViewClick(View view) {
		switch (view.getId()) {
		case R.id.setting:
			TrafficAndroidUtils.startActivity(this,
					TrafficSettingActivity.class);
			break;
		case R.id.allapp_detail:
			TrafficAndroidUtils
					.startActivity(this, TrafficAllAppActivity.class);
			break;
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (TrafficUtils.ACTION_UPDATE_TRAFFIC.equals(intent.getAction())) {
				new InitTotalInterfaceTask().execute();
			} else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent
					.getAction())) {
				setCurNetType();
			}
		}
	};

	private class InitTotalInterfaceTask extends AsyncTask<Void, Void, long[]> {

		@Override
		protected long[] doInBackground(Void... params) {
			return TrafficUtils.getMobileAndWifiData(TrafficStatActivity.this);
		}

		@Override
		protected void onPostExecute(long[] datas) {
			Log.i("TAG", "InitTotalInterfaceTask:onPostExecute() �������");
			total_mobile.setText(TrafficUtils
					.dataSizeFormat(datas[TrafficUtils.INDEX_TOTAL_MOBILE]));
			day_mobile.setText(TrafficUtils
					.dataSizeFormat(datas[TrafficUtils.INDEX_DAY_MOBILE]));
			total_wifi.setText(TrafficUtils
					.dataSizeFormat(datas[TrafficUtils.INDEX_TOTAL_WIFI]));
			day_wifi.setText(TrafficUtils
					.dataSizeFormat(datas[TrafficUtils.INDEX_DAY_WIFI]));
		}
	}

}
