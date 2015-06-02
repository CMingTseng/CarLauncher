package com.tchip.carlauncher.ui;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.service.SpeakService;
import com.tchip.carlauncher.view.AudioRecordDialog;
import com.tchip.carlauncher.view.ButtonFloat;

public class RoutePlanActivity extends Activity implements
		BaiduMap.OnMapClickListener, OnGetRoutePlanResultListener {
	Button mBtnPre = null; // 上一个节点
	Button mBtnNext = null; // 下一个节点
	int nodeIndex = -1; // 节点索引,供浏览节点时使用
	RouteLine route = null;
	OverlayManager routeOverlay = null;
	boolean useDefaultIcon = false;
	private TextView popupText = null;// 泡泡view

	private SharedPreferences preferences;
	private double startLatitude, startLongitude, endLatitude, endLongitude;
	private LatLng startLatLng, endLatLng;

	// 百度地图地址转经纬度
	private GeoCoder mEndSearch = null;

	// 地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
	// 如果不处理touch事件，则无需继承，直接使用MapView即可
	MapView mMapView = null;
	BaiduMap mBaiduMap = null;
	RoutePlanSearch mSearch = null;

	private EditText editDestination;
	private ImageView imgVoice;

	// 初始化全局 bitmap 信息，不用时及时 recycle
	BitmapDescriptor iconCamera = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_map_speed_camera);
	BitmapDescriptor iconLight = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_route_traffic_light);

	private AudioRecordDialog audioRecordDialog;

	// 语义理解对象（语音到语义）。
	private SpeechUnderstander mSpeechUnderstander;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_route_plan);

		audioRecordDialog = new AudioRecordDialog(RoutePlanActivity.this);
		preferences = getSharedPreferences("CarLauncher", Context.MODE_PRIVATE);

		iniLayout();
	}

	private void iniLayout() {
		// 初始化地图
		mMapView = (MapView) findViewById(R.id.map);
		// 去掉缩放控件和百度Logo
		int count = mMapView.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = mMapView.getChildAt(i);
			if (child instanceof ImageView || child instanceof ZoomControls) {
				child.setVisibility(View.INVISIBLE);
			}
		}

		mBaiduMap = mMapView.getMap();
		mBtnPre = (Button) findViewById(R.id.pre);
		mBtnNext = (Button) findViewById(R.id.next);
		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);
		// 地图点击事件处理
		mBaiduMap.setOnMapClickListener(this);
		// 获取当前经纬度
		startLatitude = Double.parseDouble(preferences.getString("latitude",
				"0.00"));
		startLongitude = Double.parseDouble(preferences.getString("longitude",
				"0.00"));
		// 初始化地图位置
		startLatLng = new LatLng(startLatitude, startLongitude);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(startLatLng);
		mBaiduMap.animateMapStatus(u);

		// 初始化搜索模块，注册事件监听
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(this);

		ButtonFloat btnToViceFromRoutePlan = (ButtonFloat) findViewById(R.id.btnToViceFromRoutePlan);
		btnToViceFromRoutePlan.setDrawableIcon(getResources().getDrawable(
				R.drawable.icon_arrow_up));
		btnToViceFromRoutePlan.setOnClickListener(new MyOnClickListener());

		// 语音按钮
		ButtonFloat btnVoice = (ButtonFloat) findViewById(R.id.btnVoice);
		btnVoice.setDrawableIcon(getResources().getDrawable(
				R.drawable.icon_route_microphone));
		btnVoice.hasAnimation(false);
		btnVoice.setOnClickListener(new MyOnClickListener());

		// 搜索按钮
		ButtonFloat btnSearch = (ButtonFloat) findViewById(R.id.btnSearch);
		btnSearch.setDrawableIcon(getResources().getDrawable(
				R.drawable.icon_route_search));
		btnSearch.hasAnimation(false);
		btnSearch.setOnClickListener(new MyOnClickListener());

		editDestination = (EditText) findViewById(R.id.editDestination);

	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnToViceFromRoutePlan:
				backToVice();
				break;
			case R.id.btnSearch:
				String destinationStr = editDestination.getText().toString();
				if (destinationStr != null & destinationStr.length() > 0) {
					startSearch(destinationStr);
				}
				break;
			case R.id.btnVoice:
				startVoiceUnderstand();
				break;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backToVice();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	private void backToVice() {
		finish();
		overridePendingTransition(R.anim.zms_translate_down_out,
				R.anim.zms_translate_down_in);
	}

	/**
	 * 发起路线规划搜索示例
	 * 
	 */
	public void startSearch(String destinationStr) {
		// 重置浏览节点的路线数据
		route = null;
		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);
		mBaiduMap.clear();
		// 处理搜索按钮响应
		// String routeStart = preferences.getString("street",
		// "紫竹横街")+preferences.getString("streetNum", "");
		// String cityName = preferences.getString("cityName", "中山");
		// PlanNode stNode = PlanNode.withCityNameAndPlaceName(cityName,
		// routeStart);

		//
		mEndSearch = GeoCoder.newInstance();
		mEndSearch
				.setOnGetGeoCodeResultListener(new MyOnGetGeoCoderResultListener());
		mEndSearch
				.geocode(new GeoCodeOption().city("").address(destinationStr));

	}

	/**
	 * 地址转经纬度监听
	 */
	class MyOnGetGeoCoderResultListener implements OnGetGeoCoderResultListener {

		@Override
		public void onGetGeoCodeResult(GeoCodeResult result) {
			// TODO Auto-generated method stub
			endLatLng = result.getLocation();
			if (endLatLng != null) {
				// 目的地经纬度
				endLatitude = endLatLng.latitude;
				endLongitude = endLatLng.longitude;
				endLatLng = new LatLng(endLatitude, endLongitude);

				// 路径规划
				PlanNode startNode = PlanNode.withLocation(startLatLng);
				PlanNode endNode = PlanNode.withLocation(endLatLng);
				// PlanNode enNode = PlanNode.withCityNameAndPlaceName(cityName,
				// destinationStr);
				// 公交：TransitRoutePlanOption 步行：WalkingRoutePlanOption
				mSearch.drivingSearch((new DrivingRoutePlanOption()).from(
						startNode).to(endNode));

				// 获取摄像头，红绿灯JSON线程
				new Thread(telematicsTask).start();
			}
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		}
	}

	/**
	 * 节点浏览示例
	 * 
	 * @param v
	 */
	public void nodeClick(View v) {
		if (route == null || route.getAllStep() == null) {
			return;
		}
		if (nodeIndex == -1 && v.getId() == R.id.pre) {
			return;
		}
		// 设置节点索引
		if (v.getId() == R.id.next) {
			if (nodeIndex < route.getAllStep().size() - 1) {
				nodeIndex++;
			} else {
				return;
			}
		} else if (v.getId() == R.id.pre) {
			if (nodeIndex > 0) {
				nodeIndex--;
			} else {
				return;
			}
		}
		// 获取节结果信息
		LatLng nodeLocation = null;
		String nodeTitle = null;
		Object step = route.getAllStep().get(nodeIndex);
		if (step instanceof DrivingRouteLine.DrivingStep) {
			nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrace()
					.getLocation();
			nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
		}

		if (nodeLocation == null || nodeTitle == null) {
			return;
		}
		// 移动节点至中心
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
		// show popup
		popupText = new TextView(RoutePlanActivity.this);
		popupText.setBackgroundResource(R.drawable.popup);
		popupText.setTextColor(0xFF000000);
		popupText.setText(nodeTitle);
		mBaiduMap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));

	}

	/**
	 * 切换路线图标，刷新地图使其生效 注意： 起终点图标使用中心对齐.
	 */
	public void changeRouteIcon(View v) {
		if (routeOverlay == null) {
			return;
		}
		if (useDefaultIcon) {
			((Button) v).setText("自定义起终点图标");
			// Toast.makeText(this, "将使用系统起终点图标", Toast.LENGTH_SHORT).show();

		} else {
			((Button) v).setText("系统起终点图标");
			// Toast.makeText(this, "将使用自定义起终点图标", Toast.LENGTH_SHORT).show();

		}
		useDefaultIcon = !useDefaultIcon;
		routeOverlay.removeFromMap();
		routeOverlay.addToMap();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	private void startSpeak(String content) {
		Intent intent = new Intent(this, SpeakService.class);
		intent.putExtra("content", content);
		startService(intent);
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult result) {

	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult result) {

	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			// 未找到结果
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			result.getSuggestAddrInfo();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			nodeIndex = -1;
			// mBtnPre.setVisibility(View.VISIBLE);
			// mBtnNext.setVisibility(View.VISIBLE);
			route = result.getRouteLines().get(0);
			DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
			routeOverlay = overlay;
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
		}
	}

	// 定制RouteOverly
	private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

		public MyDrivingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
			}
			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
			}
			return null;
		}
	}

	private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

		public MyWalkingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
			}
			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
			}
			return null;
		}
	}

	private class MyTransitRouteOverlay extends TransitRouteOverlay {

		public MyTransitRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
			}
			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
			}
			return null;
		}
	}

	@Override
	public void onMapClick(LatLng point) {
		mBaiduMap.hideInfoWindow();
	}

	@Override
	public boolean onMapPoiClick(MapPoi poi) {
		return false;
	}

	// ====================================================
	final Handler jsonHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String jsonStr = data.getString("json");
			// editDestination.setText(jsonStr);
			try {
				JSONObject jsonObject;
				jsonObject = new JSONObject(jsonStr);
				int errorNum = jsonObject.getInt("error");
				if (errorNum < 1) {
					JSONObject resultObject = jsonObject
							.getJSONObject("results");
					// mainRoad主要路段 entrance道路出入口 landMark途径地标 tollStation收费站
					// trafficLight红绿灯 serviceArea服务区 gasStation加油站 camera摄像头
					// other其他 carPark终点停车场

					JSONArray cameraArray, lightArray, parkArray;
					// 摄像头
					try {
						cameraArray = resultObject.getJSONArray("camera");
						int cameraCount = cameraArray.length();
						for (int i = 0; i < cameraCount; i++) {
							JSONObject cameraObject = cameraArray
									.getJSONObject(i);
							// speed:途经路段摄像头点限速，-1为无相关数据
							int speedLimit = cameraObject.getInt("speed");
							double cameraLng = cameraObject.getJSONObject(
									"location").getDouble("lng");
							double cameraLat = cameraObject.getJSONObject(
									"location").getDouble("lat");
							// 绘制摄像头Maker
							LatLng cameraLatLng = new LatLng(cameraLat,
									cameraLng);
							OverlayOptions ooCamera = new MarkerOptions()
									.position(cameraLatLng).icon(iconCamera)
									.zIndex(9).draggable(true);
							Marker cameraMaker = (Marker) (mBaiduMap
									.addOverlay(ooCamera));
						}
					} catch (JSONException e) {

					}

					// 红绿灯
					try {
						lightArray = resultObject.getJSONArray("trafficLight");
						int lightCount = lightArray.length();
						for (int i = 0; i < lightCount; i++) {
							JSONObject lightObject = lightArray
									.getJSONObject(i);
							double lightLng = lightObject.getJSONObject(
									"location").getDouble("lng");
							double lightLat = lightObject.getJSONObject(
									"location").getDouble("lat");
							// 绘制红绿灯Maker
							LatLng lightLatLng = new LatLng(lightLat, lightLng);
							OverlayOptions ooLight = new MarkerOptions()
									.position(lightLatLng).icon(iconLight)
									.zIndex(9).draggable(true);
							Marker lightMaker = (Marker) (mBaiduMap
									.addOverlay(ooLight));
						}
					} catch (JSONException e) {

					}

					// 停车场
					try {
						parkArray = resultObject.getJSONArray("carPark");
						int parkCount = parkArray.length();
					} catch (JSONException e) {

					}
				} else {

				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 网络操作相关的子线程
	 */
	Runnable telematicsTask = new Runnable() {

		@Override
		public void run() {
			// TODO
			// 在这里进行 http request.网络请求相关操作

			String jsonString = "get Failed";
			try {
				URL uri = new URL(
						"http://api.map.baidu.com/telematics/v3/viaPath?origin="
								+ startLongitude + "," + startLatitude
								+ "&destination=" + endLongitude + ","
								+ endLatitude + "&output=json&ak="
								+ Constant.BAIDU_API_KEY + "&mcode="
								+ Constant.BAIDU_MCODE);
				URLConnection ucon = uri.openConnection();
				InputStream is = ucon.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				ByteArrayBuffer baf = new ByteArrayBuffer(100);
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
				}
				jsonString = new String(baf.toByteArray(), "utf-8");

				Message message = new Message();
				message.what = 1;
				Bundle jsonData = new Bundle();
				jsonData.putString("json", jsonString);
				message.setData(jsonData);
				jsonHandler.sendMessage(message);

			} catch (Exception e) {
			}

		}
	};

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
	}

	@Override
	protected void onDestroy() {
		mSearch.destroy();
		mMapView.onDestroy();
		iconCamera.recycle();
		iconLight.recycle();
		super.onDestroy();
	}

	// ==========================
	int ret = 0;// 函数调用返回值

	public void startVoiceUnderstand() {
		// 初始化对象
		mSpeechUnderstander = SpeechUnderstander.createUnderstander(
				RoutePlanActivity.this, speechUnderstanderListener);
		setParam();

		if (mSpeechUnderstander.isUnderstanding()) { // 开始前检查状态
			mSpeechUnderstander.stopUnderstanding(); // 停止录音
		} else {
			ret = mSpeechUnderstander.startUnderstanding(mRecognizerListener);
			if (ret != 0) {
				// 语义理解失败,错误码:ret
			} else {
				// showTip(getString(R.string.text_begin));
			}
		}
	}

	/**
	 * 初始化监听器（语音到语义）。
	 */
	private InitListener speechUnderstanderListener = new InitListener() {
		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
				// 初始化失败,错误码：code
			}
		}
	};

	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	public void setParam() {
		String lag = preferences.getString("voiceAccent", "mandarin");
		if (lag.equals("en_us")) {
			// 设置语言
			mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mSpeechUnderstander.setParameter(SpeechConstant.ACCENT, lag);
		}
		// 设置语音前端点
		mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS,
				preferences.getString("voiceBos", "4000"));
		// 设置语音后端点
		mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS,
				preferences.getString("voiceEos", "1000"));
		// 设置标点符号
		mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT,
				preferences.getString("understander_punc_preference", "1"));
		// 设置音频保存路径
		mSpeechUnderstander.setParameter(
				SpeechConstant.ASR_AUDIO_PATH,
				preferences.getString("voicePath",
						Environment.getExternalStorageDirectory()
								+ "/iflytek/wavaudio.pcm"));
	}

	/**
	 * 识别回调。
	 */
	private SpeechUnderstanderListener mRecognizerListener = new SpeechUnderstanderListener() {

		@Override
		public void onResult(final UnderstanderResult result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (null != result) {
						// 显示
						String text = result.getResultString();

						try {
							JSONObject jsonObject;
							jsonObject = new JSONObject(text);
							String strContent = jsonObject.getString("text");
							if (!TextUtils.isEmpty(text)) {
								editDestination.setText(strContent);
								startSearch(strContent);
								// Intent intent1 = new Intent(
								// RoutePlanActivity.this,
								// NearResultActivity.class);
								// intent1.putExtra("findType", strContent);
								// startActivity(intent1);
							}
						} catch (JSONException e) {

						}
					} else {
						// 识别结果不正确
					}
				}
			});
		}

		@Override
		public void onVolumeChanged(int v) {
			Log.e("ZMS", "VOLUME:" + v);
			audioRecordDialog.updateVolumeLevel(v);

		}

		@Override
		public void onEndOfSpeech() {
			// showTip("onEndOfSpeech");
			audioRecordDialog.dismissDialog();

		}

		@Override
		public void onBeginOfSpeech() {
			// showTip("onBeginOfSpeech");
			audioRecordDialog.showDialog();
		}

		@Override
		public void onError(SpeechError error) {
			// showTip("onError Code：" + error.getErrorCode());
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// TODO Auto-generated method stub

		}
	};

}
