package com.tchip.carlauncher.ui.activity;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tchip.carlauncher.model.TrafficAppModel;
import com.tchip.carlauncher.model.TrafficViewHolder;
import com.tchip.carlauncher.service.TrafficFetchService;
import com.tchip.carlauncher.service.TrafficFloatBarService;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.util.TrafficAndroidUtils;
import com.tchip.carlauncher.util.TrafficPreferencesUtils;
import com.tchip.carlauncher.util.TrafficProgressDialogUtils;
import com.tchip.carlauncher.util.TrafficUtils;

public class TrafficStatActivity extends Activity {

	private TextView cur_type;
	private TextView cur_interval;
	private TextView total_mobile;
	private TextView day_mobile;
	private TextView total_wifi;
	private TextView day_wifi;

	private ListView app_list;
	private List<TrafficAppModel> dataList;
	private boolean isFirstIn;

	public static final String TAG_FLOAT = "tag_float";
	private CheckBox float_check;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trafftic_main);

		/**
		 * 控制加载对话框
		 */
		isFirstIn = true;

		initView();

		setCurNetType();

		new InitTotalInterfaceTask().execute();

		TrafficUtils.startRepeatingService(this, TrafficUtils.INTERVAL,
				TrafficFetchService.class, "");

		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mFilter.addAction(TrafficUtils.ACTION_UPDATE_TRAFFIC);
		registerReceiver(mReceiver, mFilter);

		/**
		 * 获得流量数据
		 */
		new FetchAllAppDataTask().execute();
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

		app_list = TrafficAndroidUtils.findViewById(this, R.id.app_list);
		app_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Bundle b = new Bundle();
				b.putString(TrafficChartActivity.TAG_TITLE,
						dataList.get(position).getAppName());
				b.putInt(TrafficChartActivity.TAG_UID, dataList.get(position)
						.getUid());
				TrafficAndroidUtils.startActivity(TrafficStatActivity.this,
						TrafficChartActivity.class, b);
			}
		});

		float_check = TrafficAndroidUtils.findViewById(this, R.id.float_check);

		float_check.setChecked(TrafficPreferencesUtils.getInstance().isChecked(
				this, TAG_FLOAT));

		setListener();

	}

	private void setListener() {
		float_check
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						TrafficPreferencesUtils.getInstance().setChecked(
								TrafficStatActivity.this, TAG_FLOAT, isChecked);
						if (isChecked) {
							Intent serviceIntent = new Intent(
									TrafficStatActivity.this,
									TrafficFloatBarService.class);
							startService(serviceIntent);
						} else {
							Intent brodcastIntent = new Intent(
									TrafficFloatBarService.ACTION_SERVICE);
							sendBroadcast(brodcastIntent);
						}
					}
				});
	}

	private void setCurNetType() {
		String type = TrafficUtils.netWorkTypeToString(this);
		cur_type.setText(String.format(getString(R.string.cur_type), type));
	}

	public void onViewClick(View view) {
		switch (view.getId()) {
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

			// TODO:zms
			new FetchAllAppDataTask().execute();
		}
	};

	private class InitTotalInterfaceTask extends AsyncTask<Void, Void, long[]> {

		@Override
		protected long[] doInBackground(Void... params) {
			return TrafficUtils.getMobileAndWifiData(TrafficStatActivity.this);
		}

		@Override
		protected void onPostExecute(long[] datas) {
			Log.i("TAG", "InitTotalInterfaceTask:onPostExecute()");
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

	// TODO:zms
	private class FetchAllAppDataTask extends
			AsyncTask<Void, Void, List<TrafficAppModel>> {

		TrafficProgressDialogUtils pDlgUtl;

		@Override
		protected void onPreExecute() {
			if (pDlgUtl == null && isFirstIn) {
				isFirstIn = false;
				pDlgUtl = new TrafficProgressDialogUtils(
						TrafficStatActivity.this);
				pDlgUtl.show();
			}
		}

		@Override
		protected List<TrafficAppModel> doInBackground(Void... params) {
			return TrafficUtils.getAllAppTraffic(TrafficStatActivity.this);
		}

		@Override
		protected void onPostExecute(List<TrafficAppModel> datas) {
			dataList = datas;
			ListAdapter adapter = app_list.getAdapter();
			if (adapter != null
					&& app_list.getAdapter() instanceof AllAppAdapter) {
				((AllAppAdapter) adapter).notifyDataSetChanged();
			} else {
				app_list.setAdapter(new AllAppAdapter());
			}
			if (pDlgUtl != null) {
				pDlgUtl.hide();
			}
			Log.i("TAG", "FetchAllAppDataTask():onPostExecute() 界面更新");
		}
	}

	private class AllAppAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public TrafficAppModel getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater()
						.from(TrafficStatActivity.this).inflate(
								R.layout.trafftic_allapp_item, null);
			}
			TextView app_name = TrafficViewHolder.get(convertView,
					R.id.app_name);
			TextView app_traffic = TrafficViewHolder.get(convertView,
					R.id.app_traffic);
			ImageView app_icon = TrafficViewHolder.get(convertView,
					R.id.app_icon);

			TrafficAppModel item = getItem(position);
			app_name.setText(item.getAppName());
			app_traffic.setText(TrafficUtils.dataSizeFormat(item.getTraffic()));

			try {
				PackageManager pManager = getPackageManager();
				Drawable icon = pManager.getApplicationIcon(item.getPkgName());
				app_icon.setImageDrawable(icon);
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}

			return convertView;
		}
	}

}
