package com.tchip.carlauncher.ui;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.model.Typefaces;
import com.tchip.carlauncher.service.WeatherService;
import com.tchip.carlauncher.ui.WeatherActivity.UpdateWeatherThread;
import com.tchip.carlauncher.util.WeatherUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

public class MainActivity extends Activity {

	private SharedPreferences sharedPreferences;
	private LocationClient mLocationClient;

	private SurfaceView surfaceCamera;
	private boolean isSurfaceLarge = false;
	private MapView mainMapView;
	private BaiduMap baiduMap;
	private com.baidu.mapapi.map.MyLocationConfiguration.LocationMode currentMode;
	boolean isFirstLoc = true;// 是否首次定位

	private int scanSpan = 1000; // 采集轨迹点间隔(ms)

	private ImageView smallVideoRecord, smallVideoLock;
	private RelativeLayout layoutLargeButton;
	private TextView textTemp, textLocation, textTodayWeather;
	private ImageView imageTodayWeather;

	private ProgressBar updateProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		sharedPreferences = getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		initialLayout();
		initialCameraButton();
	}

	/**
	 * 初始化布局
	 */
	private void initialLayout() {
		// 录像窗口
		surfaceCamera = (SurfaceView) findViewById(R.id.surfaceCamera);
		surfaceCamera.setOnClickListener(new MyOnClickListener());

		// 天气预报和时钟
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

		textTemp = (TextView) findViewById(R.id.textTemp);
		imageTodayWeather = (ImageView) findViewById(R.id.imageTodayWeather);
		textTodayWeather = (TextView) findViewById(R.id.textTodayWeather);
		textLocation = (TextView) findViewById(R.id.textLocation);
		updateProgress = (ProgressBar) findViewById(R.id.updateProgress);
		updateLocationAndWeather();
		// 更新天气与位置信息
		updateProgress.setVisibility(View.VISIBLE);
		new Thread(new UpdateWeatherThread()).start();

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
		baiduMap = mainMapView.getMap();
		// 开启定位图层
		baiduMap.setMyLocationEnabled(true);

		// 自定义Maker
		BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_arrow_up);

		// LocationMode 跟随：FOLLOWING 普通：NORMAL 罗盘：COMPASS
		currentMode = com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.COMPASS;
		baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				currentMode, true, null));
		InitLocation(
				com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy,
				"bd09ll", scanSpan, true);
		// 设置地图缩放级别 0-19：数值越大，比例尺单位越小
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15);
		baiduMap.animateMapStatus(msu);

		View mapHideView = findViewById(R.id.mapHideView);
		mapHideView.setOnClickListener(new MyOnClickListener());

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

	/**
	 * 初始化录像按钮
	 */
	private void initialCameraButton() {
		// ********** 小视图 **********
		// 录制
		smallVideoRecord = (ImageView) findViewById(R.id.smallVideoRecord);
		smallVideoRecord.setOnClickListener(new MyOnClickListener());

		// 锁定
		smallVideoLock = (ImageView) findViewById(R.id.smallVideoLock);
		smallVideoLock.setOnClickListener(new MyOnClickListener());

		// ********** 大视图 **********
		layoutLargeButton = (RelativeLayout) findViewById(R.id.layoutLargeButton);

		// 视频尺寸
		ImageView largeVideoSize = (ImageView) findViewById(R.id.largeVideoSize);
		largeVideoSize.setOnClickListener(new MyOnClickListener());

		// 视频分段长度
		ImageView largeVideoTime = (ImageView) findViewById(R.id.largeVideoTime);
		largeVideoTime.setOnClickListener(new MyOnClickListener());

		// 锁定
		ImageView largeVideoLock = (ImageView) findViewById(R.id.largeVideoLock);
		largeVideoLock.setOnClickListener(new MyOnClickListener());

		// 视频文件
		ImageView largeVideoFile = (ImageView) findViewById(R.id.largeVideoFile);
		largeVideoFile.setOnClickListener(new MyOnClickListener());

		// 录制
		ImageView largeVideoRecord = (ImageView) findViewById(R.id.largeVideoRecord);
		largeVideoRecord.setOnClickListener(new MyOnClickListener());

		// 拍照
		ImageView largeVideoCamera = (ImageView) findViewById(R.id.largeVideoCamera);
		largeVideoCamera.setOnClickListener(new MyOnClickListener());

		updateButtonState(isSurfaceLarge());
	}

	/**
	 * 更新录像按钮状态
	 * 
	 * @param isSurfaceLarge
	 */
	private void updateButtonState(boolean isSurfaceLarge) {
		if (isSurfaceLarge) {
			smallVideoRecord.setVisibility(View.GONE);
			smallVideoLock.setVisibility(View.GONE);
			layoutLargeButton.setVisibility(View.VISIBLE);
		} else {
			smallVideoRecord.setVisibility(View.VISIBLE);
			smallVideoLock.setVisibility(View.VISIBLE);
			layoutLargeButton.setVisibility(View.GONE);
		}

	}

	private boolean isSurfaceLarge() {
		return isSurfaceLarge;
	}

	/**
	 * 更新位置和天气
	 */
	private void updateLocationAndWeather() {
		textLocation.setText(sharedPreferences.getString("cityName", "未定位"));
		String weatherToday = sharedPreferences.getString("day0weather", "未知");
		textTodayWeather.setText(weatherToday);
		imageTodayWeather.setImageResource(WeatherUtil
				.getWeatherDrawable(WeatherUtil.getTypeByStr(weatherToday)));
		String day0tmpLow = sharedPreferences.getString("day0tmpLow", "15℃");
		String day0tmpHigh = sharedPreferences.getString("day0tmpHigh", "25℃");
		day0tmpLow = day0tmpLow.split("℃")[0];
		textTemp.setText(day0tmpLow + "~" + day0tmpHigh);
	}

	public class UpdateWeatherThread implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(2000);
				startWeatherService();
				Thread.sleep(3000);
				Message message = new Message();
				message.what = 1;
				updateWeatherHandler.sendMessage(message);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	final Handler updateWeatherHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				updateProgress.setVisibility(View.GONE);
				updateLocationAndWeather();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

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
					updateButtonState(true);
				} else {
					int widthSmall = 480;
					int heightSmall = 270;
					surfaceCamera
							.setLayoutParams(new RelativeLayout.LayoutParams(
									widthSmall, heightSmall));
					isSurfaceLarge = false;
					updateButtonState(false);
				}
				break;
			case R.id.smallVideoRecord:
			case R.id.largeVideoRecord:
				break;
			case R.id.smallVideoLock:
			case R.id.largeVideoLock:
				break;
			case R.id.largeVideoSize:
				break;
			case R.id.largeVideoTime:
				break;
			case R.id.largeVideoFile:
				break;
			case R.id.largeVideoCamera:
				break;

			case R.id.layoutWeather:
				Intent intentWeather = new Intent(MainActivity.this,
						WeatherActivity.class);
				startActivity(intentWeather);
				overridePendingTransition(R.anim.zms_translate_down_out,
						R.anim.zms_translate_down_in);
				break;
			case R.id.mapHideView:
				Toast.makeText(getApplicationContext(), "启动地图",
						Toast.LENGTH_SHORT).show();
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
			// map view 销毁后不在处理新接收的位置
			if (location == null || mainMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			baiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				baiduMap.animateMapStatus(u);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
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
	private void InitLocation(
			com.baidu.location.LocationClientOption.LocationMode tempMode,
			String tempCoor, int frequence, boolean isNeedAddress) {

		mLocationClient = new LocationClient(this.getApplicationContext());
		mLocationClient.registerLocationListener(new MyLocationListener());
		// mGeofenceClient = new GeofenceClient(getApplicationContext());

		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);
		option.setCoorType(tempCoor);
		option.setScanSpan(frequence);
		option.setOpenGps(true);// 打开gps
		option.setIsNeedAddress(isNeedAddress);
		mLocationClient.setLocOption(option);

		mLocationClient.start();
	}

	/**
	 * 更新天气
	 */
	private void startWeatherService() {
		Intent intent = new Intent(this, WeatherService.class);
		startService(intent);
	}

	@Override
	protected void onPause() {
		mainMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mainMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocationClient.stop();
		// 关闭定位图层
		baiduMap.setMyLocationEnabled(false);
		mainMapView.onDestroy();
		mainMapView = null;
		super.onDestroy();
	}
}
