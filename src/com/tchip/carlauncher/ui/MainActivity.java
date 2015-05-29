package com.tchip.carlauncher.ui;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.MapView;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.model.Typefaces;
import com.tchip.carlauncher.ui.MainActivityOld.MyOnClickListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.ZoomControls;

public class MainActivity extends Activity {
	
	private LocationClient mLocationClient;

	private SurfaceView surfaceCamera;
	private boolean isSurfaceLarge = false;
	private MapView mainMapView;
	
	private int scanSpan = 1000; // 采集轨迹点间隔(ms)

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		initialLayout();
	}

	/**
	 * 初始化布局
	 */
	private void initialLayout() {
		// 录像窗口
		surfaceCamera = (SurfaceView) findViewById(R.id.surfaceCamera);
		surfaceCamera.setOnClickListener(new MyOnClickListener());

		// 天气预报
		RelativeLayout layoutWeather = (RelativeLayout) findViewById(R.id.layoutWeather);
		layoutWeather.setOnClickListener(new MyOnClickListener());
		TextClock textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setTypeface(Typefaces.get(this, "Font-Roboto-Thin.ttf"));

		TextClock textDate = (TextClock) findViewById(R.id.textDate);
		textDate.setTypeface(Typefaces
				.get(this, "Font-Droid-Sans-Fallback.ttf"));

		TextClock textWeek = (TextClock) findViewById(R.id.textWeek);
		textWeek.setTypeface(Typefaces
				.get(this, "Font-Droid-Sans-Fallback.ttf"));

		// 定位地图
		mainMapView = (MapView) findViewById(R.id.mainMapView);
		// 去掉缩放控件和百度Logo
		int count = mainMapView.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = mainMapView.getChildAt(i);
			if (child instanceof ImageView || child instanceof ZoomControls) {
				child.setVisibility(View.INVISIBLE);
			}
		}
		InitLocation(LocationMode.Hight_Accuracy, "bd09ll", scanSpan, true);

		// 多媒体
		ImageView imageMultimedia = (ImageView) findViewById(R.id.imageMultimedia);
		imageMultimedia.setOnClickListener(new MyOnClickListener());

		// 文件管理
		ImageView imageFileExplore = (ImageView) findViewById(R.id.imageFileExplore);
		imageFileExplore.setOnClickListener(new MyOnClickListener());

		// 周边搜索
		ImageView imageNearSearch = (ImageView) findViewById(R.id.imageNearSearch);
		imageNearSearch.setOnClickListener(new MyOnClickListener());

		// 语音助手
		ImageView imageVoiceChat = (ImageView) findViewById(R.id.imageVoiceChat);
		imageVoiceChat.setOnClickListener(new MyOnClickListener());

		// 行驶轨迹
		ImageView imageRouteTrack = (ImageView) findViewById(R.id.imageRouteTrack);
		imageRouteTrack.setOnClickListener(new MyOnClickListener());

		// 路径规划（摄像头，红绿灯）
		ImageView imageRoutePlan = (ImageView) findViewById(R.id.imageRoutePlan);
		imageRoutePlan.setOnClickListener(new MyOnClickListener());

		// 设置
		ImageView imageSetting = (ImageView) findViewById(R.id.imageSetting);
		imageSetting.setOnClickListener(new MyOnClickListener());
	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.surfaceCamera:
				if (!isSurfaceLarge) {
					int widthFull = 854;
					int heightFull = 480;
					surfaceCamera
							.setLayoutParams(new RelativeLayout.LayoutParams(
									widthFull, heightFull));
					isSurfaceLarge = true;
				} else {
					int widthSmall = 506;
					int heightSmall = 285;
					surfaceCamera
							.setLayoutParams(new RelativeLayout.LayoutParams(
									widthSmall, heightSmall));
					isSurfaceLarge = false;
				}
				break;
			case R.id.layoutWeather:
				Intent intentWeather = new Intent(MainActivity.this,
						WeatherActivity.class);
				startActivity(intentWeather);
				overridePendingTransition(R.anim.zms_translate_down_out,
						R.anim.zms_translate_down_in);
				break;
			case R.id.imageMultimedia:
				Intent intentMultimedia = new Intent(MainActivity.this,
						MultimediaActivity.class);
				startActivity(intentMultimedia);
				overridePendingTransition(R.anim.zms_translate_up_out,
						R.anim.zms_translate_up_in);
				break;
			case R.id.imageRouteTrack:
				Intent intentRouteTrack = new Intent(MainActivity.this,
						RouteListActivity.class);
				startActivity(intentRouteTrack);
				overridePendingTransition(R.anim.zms_translate_up_out,
						R.anim.zms_translate_up_in);
				break;
			case R.id.imageRoutePlan:
				Intent intentRoutePlan = new Intent(MainActivity.this,
						RoutePlanActivity.class);
				startActivity(intentRoutePlan);
				overridePendingTransition(R.anim.zms_translate_up_out,
						R.anim.zms_translate_up_in);
				break;
			case R.id.imageFileExplore:
				Intent intentFileExplore = new Intent(MainActivity.this,
						FileRemoteControlActivity.class);
				startActivity(intentFileExplore);
				overridePendingTransition(R.anim.zms_translate_up_out,
						R.anim.zms_translate_up_in);
				break;
			case R.id.imageNearSearch:
				Intent intentNearSearch = new Intent(MainActivity.this,
						NearActivity.class);
				startActivity(intentNearSearch);
				overridePendingTransition(R.anim.zms_translate_up_out,
						R.anim.zms_translate_up_in);
				break;

			case R.id.imageVoiceChat:
				Intent intentVoiceChat = new Intent(MainActivity.this,
						ChatActivity.class);
				startActivity(intentVoiceChat);
				overridePendingTransition(R.anim.zms_translate_up_out,
						R.anim.zms_translate_up_in);
				break;

			case R.id.imageSetting:
				Intent intentSetting = new Intent(MainActivity.this,
						SettingActivity.class);
				startActivity(intentSetting);
				overridePendingTransition(R.anim.zms_translate_up_out,
						R.anim.zms_translate_up_in);
				break;

			default:
				break;
			}
		}
	}
	
	
	
	
	class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			//cityName = location.getCity();

				// editor.putLong("cityCode", cityCode);

				// new Thread(networkTask).start();
		}
	}
	
	
	/**
	 * 
	 * @param tempMode
	 *            LocationMode.Hight_Accuracy-高精度
	 *            LocationMode.Battery_Saving-低功耗
	 *            LocationMode.Device_Sensors-仅设备
	 * @param tempCoor
	 *            gcj02-国测局加密经纬度坐标 bd09ll-百度加密经纬度坐标 bd09-百度加密墨卡托坐标
	 * @param frequence
	 *            MIN_SCAN_SPAN = 1000; MIN_SCAN_SPAN_NETWORK = 3000;
	 * @param isNeedAddress
	 *            是否需要地址
	 */
	private void InitLocation(LocationMode tempMode, String tempCoor,
			int frequence, boolean isNeedAddress) {

		mLocationClient = new LocationClient(this.getApplicationContext());
		mLocationClient.registerLocationListener(new MyLocationListener());
		// mGeofenceClient = new GeofenceClient(getApplicationContext());

		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);
		option.setCoorType(tempCoor);
		option.setScanSpan(frequence);
		option.setIsNeedAddress(isNeedAddress);
		mLocationClient.setLocOption(option);

		mLocationClient.start();
	}


//	private View insertImage(Integer id) {
//		LinearLayout layout = new LinearLayout(getApplicationContext());
//		layout.setLayoutParams(new LayoutParams(320, 320));
//		layout.setGravity(Gravity.CENTER);
//
//		ImageView imageView = new ImageView(getApplicationContext());
//		imageView.setLayoutParams(new LayoutParams(300, 300));
//		imageView.setBackgroundResource(id);
//
//		layout.addView(imageView);
//		return layout;
//	}

	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mLocationClient.stop();
	}

}
