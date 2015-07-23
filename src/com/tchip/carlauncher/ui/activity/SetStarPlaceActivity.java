package com.tchip.carlauncher.ui.activity;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SetStarPlaceActivity extends Activity {
	private MapView mapView;
	private BaiduMap baiduMap;
	private double mLatitude, mLongitude;

	private LatLng clickLatLng; // 点击的LatLng
	private LatLng locLatLng; // 定位的LatLng

	private SharedPreferences preference;
	private Editor editor;

	private TextView textHint;
	private Marker clickMarker;

	private RelativeLayout layoutConfirm, layoutBack;
	private Button btnConfirm, btnBack;

	private boolean isClick = false;

	/**
	 * 0-Work;1-Home
	 */
	private int starType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

		setContentView(R.layout.activity_set_star_place);

		// 接收搜索内容
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			starType = extras.getInt("starType");
		}

		preference = getSharedPreferences(Constant.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		editor = preference.edit();

		InitialLayout();
	}

	private void InitialLayout() {
		mLatitude = Double
				.parseDouble(preference.getString("latitude", "0.00"));
		mLongitude = Double.parseDouble(preference.getString("longitude",
				"0.00"));

		mapView = (MapView) findViewById(R.id.mapView);
		// 去掉百度Logo
		int count = mapView.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = mapView.getChildAt(i);
			if (child instanceof ImageView) {
				child.setVisibility(View.INVISIBLE);
			}
		}

		baiduMap = mapView.getMap();
		// 初始化地图位置
		locLatLng = new LatLng(mLatitude, mLongitude);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(locLatLng);
		baiduMap.animateMapStatus(u);

		baiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi mapPoi) {
				clickLatLng = mapPoi.getPosition();
				addMakerToMap(clickLatLng);
				isClick = true;
				return false;
			}

			@Override
			public void onMapClick(LatLng latLng) {
				clickLatLng = latLng;
				addMakerToMap(clickLatLng);
				isClick = true;
			}
		});

		layoutConfirm = (RelativeLayout) findViewById(R.id.layoutConfirm);
		layoutConfirm.setOnClickListener(new MyOnClickListener());
		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btnConfirm.setOnClickListener(new MyOnClickListener());

		layoutBack = (RelativeLayout) findViewById(R.id.layoutBack);
		layoutBack.setOnClickListener(new MyOnClickListener());
		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new MyOnClickListener());
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnConfirm:
			case R.id.layoutConfirm:
				if (isClick) {
					// 保存地址经纬度
					String strAddress = "";
					if (starType == 1) {
						// HOME
						strAddress = "家庭";
						editor.putBoolean("homeSet", true);
						editor.putString("homeLat", "" + clickLatLng.latitude);
						editor.putString("homeLng", "" + clickLatLng.longitude);
					} else {
						// WORK
						strAddress = "公司";
						editor.putBoolean("workSet", true);
						editor.putString("workLat", "" + clickLatLng.latitude);
						editor.putString("workLng", "" + clickLatLng.longitude);
					}
					editor.commit();
					Toast.makeText(getApplicationContext(),
							strAddress + "地址设置成功", Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(getApplicationContext(), "请选取位置",
							Toast.LENGTH_SHORT).show();
				}
				break;

			case R.id.layoutBack:
			case R.id.btnBack:
				finish();
				break;

			default:
				break;
			}
		}

	}

	private void addMakerToMap(LatLng latLng) {
		baiduMap.clear();
		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
				.fromResource(R.drawable.ui_marker_select);

		OverlayOptions ooA = new MarkerOptions().position(latLng)
				.icon(bitmapDescriptor).zIndex(9).draggable(true);
		baiduMap.addOverlay(ooA);
	}

}
