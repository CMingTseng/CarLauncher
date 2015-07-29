package com.tchip.carlauncher.ui.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;

/**
 * 安装后搜索仍需联网，但会节约达90%的流量
 * 
 * 低分屏：L 高分屏：H (854x480 5' 属于低分屏)
 */
public class OfflineBaiduMapActivity extends Activity implements
		MKOfflineMapListener {

	private MKOfflineMap mOffline = null;
	private TextView cidView;
	private TextView stateView;
	private EditText cityNameView;

	private Button btnSearch;
	private RelativeLayout layoutBack, layoutDownload, layoutCity,
			layoutImport;

	private LinearLayout layoutSingleDownload;

	private boolean isSingleDownShow = false;

	/**
	 * 已下载的离线地图信息列表
	 */
	private ArrayList<MKOLUpdateElement> localMapList = null;
	private LocalMapAdapter lAdapter = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_offline_baidumap);
		mOffline = new MKOfflineMap();
		mOffline.init(this);
		initialLayout();
	}

	private void initialLayout() {

		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new MyOnClickListener());

		layoutBack = (RelativeLayout) findViewById(R.id.layoutBack);
		layoutBack.setOnClickListener(new MyOnClickListener());

		layoutDownload = (RelativeLayout) findViewById(R.id.layoutDownload);
		layoutDownload.setOnClickListener(new MyOnClickListener());

		layoutCity = (RelativeLayout) findViewById(R.id.layoutCity);
		layoutCity.setOnClickListener(new MyOnClickListener());

		layoutImport = (RelativeLayout) findViewById(R.id.layoutImport);
		layoutImport.setOnClickListener(new MyOnClickListener());

		layoutSingleDownload = (LinearLayout) findViewById(R.id.layoutSingleDownload);
		layoutSingleDownload.setVisibility(View.GONE);

		cidView = (TextView) findViewById(R.id.cityid);
		cityNameView = (EditText) findViewById(R.id.textCity);
		stateView = (TextView) findViewById(R.id.state);

		// ListView hotCityList = (ListView) findViewById(R.id.hotcitylist);
		// ArrayList<String> hotCities = new ArrayList<String>();
		// // 获取热闹城市列表
		ArrayList<MKOLSearchRecord> records1 = mOffline.getHotCityList();
		// if (records1 != null) {
		// for (MKOLSearchRecord r : records1) {
		// hotCities.add(r.cityName + "(" + r.cityID + ")" + "   --"
		// + this.formatDataSize(r.size));
		// }
		// }
		// ListAdapter hAdapter = (ListAdapter) new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, hotCities);
		// hotCityList.setAdapter(hAdapter);

		ListView allCityList = (ListView) findViewById(R.id.allcitylist);
		// 获取所有支持离线地图的城市
		final ArrayList<String> allCities = new ArrayList<String>();
		ArrayList<MKOLSearchRecord> records2 = mOffline.getOfflineCityList();
		if (records1 != null) {
			for (MKOLSearchRecord r : records2) {
				allCities.add(r.cityName + "(" + r.cityID + ")" + "   --"
						+ this.formatDataSize(r.size));
			}
		}
		ListAdapter aAdapter = (ListAdapter) new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, allCities);
		allCityList.setAdapter(aAdapter);
		allCityList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int startIndex = allCities.get(position).indexOf("(") + 1;
				int endIndex = allCities.get(position).indexOf(")");
				String clickId = allCities.get(position).substring(startIndex,
						endIndex);
				Log.v(Constant.TAG, "Offline Map id:" + clickId);

				mOffline.start(Integer.parseInt(clickId));
				Toast.makeText(
						getApplicationContext(),
						"开始下载:"
								+ allCities.get(position).substring(0,
										startIndex - 1), Toast.LENGTH_SHORT)
						.show();
			}
		});

		LinearLayout cl = (LinearLayout) findViewById(R.id.citylist_layout);
		LinearLayout lm = (LinearLayout) findViewById(R.id.localmap_layout);
		lm.setVisibility(View.GONE);
		cl.setVisibility(View.VISIBLE);

		// 获取已下过的离线地图信息
		localMapList = mOffline.getAllUpdateInfo();
		if (localMapList == null) {
			localMapList = new ArrayList<MKOLUpdateElement>();
		}

		ListView localMapListView = (ListView) findViewById(R.id.localmaplist);
		lAdapter = new LocalMapAdapter();
		localMapListView.setAdapter(lAdapter);

	}

	/**
	 * 设置下载单个城市离线地图是否可见
	 * 
	 * @param isShow
	 */
	private void setSingleDownShow(boolean isShow) {
		if (!isShow) {
			layoutSingleDownload.setVisibility(View.GONE);
			isSingleDownShow = false;
		} else {
			layoutSingleDownload.setVisibility(View.VISIBLE);
			isSingleDownShow = true;
		}
	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnSearch:
				// 搜索离线城市
				ArrayList<MKOLSearchRecord> records = mOffline
						.searchCity(cityNameView.getText().toString());
				if (records == null || records.size() != 1)
					return;
				setSingleDownShow(true);
				cidView.setText(String.valueOf(records.get(0).cityID));
				break;

			case R.id.layoutBack:
				if (isSingleDownShow) {
					setSingleDownShow(false);
				} else {
					finish();
				}
				break;

			case R.id.layoutDownload:
				// 下载管理列表
				LinearLayout cl = (LinearLayout) findViewById(R.id.citylist_layout);
				LinearLayout lm = (LinearLayout) findViewById(R.id.localmap_layout);
				lm.setVisibility(View.VISIBLE);
				cl.setVisibility(View.GONE);
				break;

			case R.id.layoutCity:
				// 城市列表
				LinearLayout cls = (LinearLayout) findViewById(R.id.citylist_layout);
				LinearLayout lms = (LinearLayout) findViewById(R.id.localmap_layout);
				lms.setVisibility(View.GONE);
				cls.setVisibility(View.VISIBLE);
				break;

			case R.id.layoutImport:
				importFromSDCard();
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 开始下载
	 * 
	 * @param view
	 */
	public void start(View view) {
		int cityid = Integer.parseInt(cidView.getText().toString());
		mOffline.start(cityid);
		Toast.makeText(this, "开始下载离线地图. cityid: " + cityid, Toast.LENGTH_SHORT)
				.show();
		updateView();
	}

	/**
	 * 暂停下载
	 * 
	 * @param view
	 */
	public void stop(View view) {
		int cityid = Integer.parseInt(cidView.getText().toString());
		mOffline.pause(cityid);
		Toast.makeText(this, "暂停下载离线地图. cityid: " + cityid, Toast.LENGTH_SHORT)
				.show();
		updateView();
	}

	/**
	 * 删除离线地图
	 * 
	 * @param view
	 */
	public void deleteMapByCityId(int cityId) {
		mOffline.remove(cityId);
		Toast.makeText(this, "删除离线地图. cityid: " + cityId, Toast.LENGTH_SHORT)
				.show();
		updateView();
	}

	/**
	 * 从SD卡导入离线地图安装包
	 * 
	 * 存放位置：USB存储器/BaiduMapSDK/vmp/l/zhongshan_187.dat
	 * 
	 */
	public void importFromSDCard() {
		int num = mOffline.importOfflineData();
		String msg = "";
		if (num == 0) {
			msg = "没有导入离线包，这可能是离线包放置位置不正确，或离线包已经导入过";
		} else {
			msg = String.format("成功导入 %d 个离线包，可以在下载管理查看", num);
		}
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		updateView();
	}

	/**
	 * 更新状态显示
	 */
	public void updateView() {
		localMapList = mOffline.getAllUpdateInfo();
		if (localMapList == null) {
			localMapList = new ArrayList<MKOLUpdateElement>();
		}
		lAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onPause() {
		int cityid = Integer.parseInt(cidView.getText().toString());
		MKOLUpdateElement temp = mOffline.getUpdateInfo(cityid);
		if (temp != null && temp.status == MKOLUpdateElement.DOWNLOADING) {
			mOffline.pause(cityid);
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public String formatDataSize(int size) {
		String ret = "";
		if (size < (1024 * 1024)) {
			ret = String.format("%dK", size / 1024);
		} else {
			ret = String.format("%.1fM", size / (1024 * 1024.0));
		}
		return ret;
	}

	@Override
	protected void onDestroy() {
		/**
		 * 退出时，销毁离线地图模块
		 */
		mOffline.destroy();
		super.onDestroy();
	}

	@Override
	public void onGetOfflineMapState(int type, int state) {
		switch (type) {
		case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
			MKOLUpdateElement update = mOffline.getUpdateInfo(state);
			// 处理下载进度更新提示
			if (update != null) {
				stateView.setText(String.format("%s : %d%%", update.cityName,
						update.ratio));
				updateView();
			}
		}
			break;
		case MKOfflineMap.TYPE_NEW_OFFLINE:
			// 有新离线地图安装
			Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
			break;
		case MKOfflineMap.TYPE_VER_UPDATE:
			// 版本更新提示
			// MKOLUpdateElement e = mOffline.getUpdateInfo(state);

			break;
		}

	}

	/**
	 * 离线地图管理列表适配器
	 */
	public class LocalMapAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return localMapList.size();
		}

		@Override
		public Object getItem(int index) {
			return localMapList.get(index);
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

		@Override
		public View getView(int index, View view, ViewGroup arg2) {
			MKOLUpdateElement e = (MKOLUpdateElement) getItem(index);
			view = View.inflate(OfflineBaiduMapActivity.this,
					R.layout.activity_offline_baidumap_locallist, null);
			initViewItem(view, e);
			return view;
		}

		void initViewItem(View view, final MKOLUpdateElement e) {
			Button display = (Button) view.findViewById(R.id.btnDisplay);
			Button remove = (Button) view.findViewById(R.id.remove);
			Button btnUpdate = (Button) view.findViewById(R.id.btnUpdate);

			TextView title = (TextView) view.findViewById(R.id.title);
			TextView update = (TextView) view.findViewById(R.id.update);
			TextView ratio = (TextView) view.findViewById(R.id.ratio);
			ratio.setText(e.ratio + "%");
			title.setText(e.cityName);
			if (e.update) {
				update.setText("可更新");
				update.setTextColor(Color.RED);
				btnUpdate.setText("更新");
			} else {
				update.setText("最新");
				update.setTextColor(Color.BLUE);
				btnUpdate.setText("重新下载");
			}
			if (e.ratio != 100) {
				display.setEnabled(false);
			} else {
				display.setEnabled(true);
			}
			remove.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mOffline.remove(e.cityID);
					updateView();
				}
			});
			display.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("x", e.geoPt.longitude);
					intent.putExtra("y", e.geoPt.latitude);
					intent.setClass(OfflineBaiduMapActivity.this,
							OfflineBaiduMapShowActivity.class);
					startActivity(intent);
				}
			});

			btnUpdate.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mOffline.update(e.cityID);
					updateView();
				}
			});
		}

	}

}