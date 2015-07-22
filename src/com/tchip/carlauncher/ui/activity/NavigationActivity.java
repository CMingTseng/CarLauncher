package com.tchip.carlauncher.ui.activity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.utils.DistanceUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.MyApplication;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.adapter.NaviHistoryAdapter;
import com.tchip.carlauncher.adapter.NaviResultAdapter;
import com.tchip.carlauncher.model.NaviHistory;
import com.tchip.carlauncher.model.NaviHistoryDbHelper;
import com.tchip.carlauncher.model.NaviResultInfo;
import com.tchip.carlauncher.util.NetworkUtil;
import com.tchip.carlauncher.view.AudioRecordDialog;

import com.baidu.navisdk.BNaviEngineManager;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.BaiduNaviManager.OnStartNavigationListener;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;

public class NavigationActivity extends FragmentActivity implements
		OnGetPoiSearchResultListener, OnGetSuggestionResultListener {

	private PoiSearch mPoiSearch = null;
	private SuggestionSearch mSuggestionSearch = null;
	private BaiduMap mBaiduMap = null;

	private ArrayAdapter<String> sugAdapter = null;

	private double mLatitude, mLongitude;
	private LatLng mLatLng;

	private EditText etHistoryWhere;
	private LinearLayout layoutNearAdvice, layoutShowHistory;
	private RelativeLayout layoutNaviVoice, layoutNear, layoutHistory,
			layoutHistoryBack, layoutRoadCondition;

	private AudioRecordDialog audioRecordDialog;

	// 语义理解对象（语音到语义）。
	private SpeechUnderstander mSpeechUnderstander;
	private SharedPreferences preference;

	private ListView listResult, listHistory;
	private ArrayList<NaviResultInfo> naviArray;
	private NaviResultAdapter naviResultAdapter;

	private boolean isResultListShow = false;
	private boolean isNearLayoutShow = false;
	private boolean isHistoryLayoutShow = false;

	private boolean isRoadConditionOn = false;

	private Button btnHistoryNavi, btnCloseHistory;

	private NaviHistoryDbHelper naviDb;
	private NaviHistoryAdapter naviHistoryAdapter;
	private ArrayList<NaviHistory> naviHistoryArray;

	private ImageView imageRoadCondition, imgVoiceSearch;

	private ProgressBar progressVoice;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		setContentView(R.layout.activity_navigation);

		naviDb = new NaviHistoryDbHelper(getApplicationContext());

		audioRecordDialog = new AudioRecordDialog(NavigationActivity.this);

		// 接收搜索内容
		// Bundle extras = getIntent().getExtras();
		// if (extras != null) {
		// findContent = extras.getString("findType");
		// }

		// 获取当前经纬度
		preference = getSharedPreferences(Constant.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		mLatitude = Double
				.parseDouble(preference.getString("latitude", "0.00"));
		mLongitude = Double.parseDouble(preference.getString("longitude",
				"0.00"));

		// 初始化搜索模块，注册搜索事件监听
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);

		Button btnToNearFromResult = (Button) findViewById(R.id.btnToNearFromResult);
		btnToNearFromResult.setOnClickListener(new MyOnClickListener());

		initialLayout();
	}

	private void initialLayout() {

		layoutShowHistory = (LinearLayout) findViewById(R.id.layoutShowHistory);
		layoutShowHistory.setOnClickListener(new MyOnClickListener());

		layoutNaviVoice = (RelativeLayout) findViewById(R.id.layoutNaviVoice);
		layoutNaviVoice.setOnClickListener(new MyOnClickListener());

		layoutNear = (RelativeLayout) findViewById(R.id.layoutNear);
		layoutNear.setOnClickListener(new MyOnClickListener());

		// 周边搜索
		layoutNearAdvice = (LinearLayout) findViewById(R.id.layoutNearAdvice);
		RelativeLayout layoutNearOilStation = (RelativeLayout) findViewById(R.id.layoutNearOilStation);
		layoutNearOilStation.setOnClickListener(new MyOnClickListener());

		RelativeLayout layoutNearParking = (RelativeLayout) findViewById(R.id.layoutNearParking);
		layoutNearParking.setOnClickListener(new MyOnClickListener());

		RelativeLayout layoutNear4s = (RelativeLayout) findViewById(R.id.layoutNear4s);
		layoutNear4s.setOnClickListener(new MyOnClickListener());

		RelativeLayout layoutNearBank = (RelativeLayout) findViewById(R.id.layoutNearBank);
		layoutNearBank.setOnClickListener(new MyOnClickListener());

		RelativeLayout layoutShop = (RelativeLayout) findViewById(R.id.layoutShop);
		layoutShop.setOnClickListener(new MyOnClickListener());

		RelativeLayout layoutNearHotel = (RelativeLayout) findViewById(R.id.layoutNearHotel);
		layoutNearHotel.setOnClickListener(new MyOnClickListener());

		listResult = (ListView) findViewById(R.id.listResult);

		MapView mMapView = (MapView) findViewById(R.id.map);

		// 去掉百度Logo
		int count = mMapView.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = mMapView.getChildAt(i);
			if (child instanceof ImageView) {
				child.setVisibility(View.INVISIBLE);
			}
		}
		mBaiduMap = mMapView.getMap();

		// 初始化地图位置
		mLatLng = new LatLng(mLatitude, mLongitude);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(mLatLng);
		mBaiduMap.animateMapStatus(u);

		// 导航搜索历史记录
		layoutHistory = (RelativeLayout) findViewById(R.id.layoutHistory);
		layoutHistoryBack = (RelativeLayout) findViewById(R.id.layoutHistoryBack);
		layoutHistoryBack.setOnClickListener(new MyOnClickListener());
		btnCloseHistory = (Button) findViewById(R.id.btnCloseHistory);
		btnCloseHistory.setOnClickListener(new MyOnClickListener());

		etHistoryWhere = (EditText) findViewById(R.id.etHistoryWhere);
		btnHistoryNavi = (Button) findViewById(R.id.btnHistoryNavi);
		btnHistoryNavi.setOnClickListener(new MyOnClickListener());

		listHistory = (ListView) findViewById(R.id.listHistory);

		// 路况
		layoutRoadCondition = (RelativeLayout) findViewById(R.id.layoutRoadCondition);
		layoutRoadCondition.setOnClickListener(new MyOnClickListener());

		imageRoadCondition = (ImageView) findViewById(R.id.imageRoadCondition);

		// 语音识别进度
		progressVoice = (ProgressBar) findViewById(R.id.progressVoice);
		imgVoiceSearch = (ImageView) findViewById(R.id.imgVoiceSearch);

	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnToNearFromResult:
				if (isNearLayoutShow) {
					isNearLayoutShow = false;
					layoutNearAdvice.setVisibility(View.GONE);
				} else if (isResultListShow) {
					isResultListShow = false;
					listResult.setVisibility(View.GONE);
				} else {
					finish();
				}
				break;

			case R.id.layoutNaviVoice:
				isNearLayoutShow = false;
				layoutNearAdvice.setVisibility(View.GONE);

				startVoiceUnderstand();
				break;

			case R.id.layoutShowHistory:
				showOrHideLayoutHistory();
				break;

			// 周边搜索
			case R.id.layoutNear:
				showOrHideLayoutNear();
				break;

			case R.id.layoutNearOilStation:
				searchNear("加油站");
				break;

			case R.id.layoutNearParking:
				searchNear("停车场");
				break;

			case R.id.layoutNear4s:
				searchNear("4S");
				break;

			case R.id.layoutNearBank:
				searchNear("ATM");
				break;

			case R.id.layoutShop:
				searchNear("超市");
				break;

			case R.id.layoutNearHotel:
				searchNear("酒店");
				break;

			// 历史记录
			case R.id.layoutHistoryBack:
			case R.id.btnCloseHistory:
				showOrHideLayoutHistory();
				break;

			case R.id.btnHistoryNavi:
				String strContent = etHistoryWhere.getText().toString();
				if (strContent.trim().length() > 0 && strContent != null) {
					if (Constant.START_TEST_APK.equals(strContent)) {
						Log.d(Constant.TAG, "Start Test App");
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setClassName("com.DeviceTest",
								"com.DeviceTest.DeviceTest");
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					} else {
						isNearLayoutShow = false;
						layoutNearAdvice.setVisibility(View.GONE);

						startSearchPlace(strContent);
					}
				}
				break;

			case R.id.layoutRoadCondition:
				openOrCloseRoadCondition();
				break;

			default:
				break;
			}
		}
	}

	private void searchNear(String content) {
		isNearLayoutShow = false;
		layoutNearAdvice.setVisibility(View.GONE);

		startSearchPlace(content);
	}

	/**
	 * 打开或关闭路况显示
	 */
	private void openOrCloseRoadCondition() {
		if (isRoadConditionOn) {
			mBaiduMap.setTrafficEnabled(false);
			isRoadConditionOn = false;
			imageRoadCondition.setImageDrawable(getResources().getDrawable(
					R.drawable.main_icon_roadcondition_off));
		} else {
			mBaiduMap.setTrafficEnabled(true);
			isRoadConditionOn = true;
			imageRoadCondition.setImageDrawable(getResources().getDrawable(
					R.drawable.main_icon_roadcondition_on));
		}
	}

	/**
	 * 显示或隐藏历史记录
	 */
	private void showOrHideLayoutHistory() {
		if (isHistoryLayoutShow) {
			isHistoryLayoutShow = false;
			isResultListShow = false;
			layoutHistory.setVisibility(View.GONE);
			listResult.setVisibility(View.GONE);
		} else {
			isHistoryLayoutShow = true;
			layoutHistory.setVisibility(View.VISIBLE);
			naviHistoryArray = naviDb.getAllNaviHistory();

			naviHistoryAdapter = new NaviHistoryAdapter(
					getApplicationContext(), naviHistoryArray);
			listHistory.setAdapter(naviHistoryAdapter);

			listHistory
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							// 将内容更新到EditText
							String strHistory = naviHistoryArray.get(position)
									.getKey();
							etHistoryWhere.setText(strHistory);
						}
					});
		}
	}

	/**
	 * 显示或隐藏周边搜索
	 */
	private void showOrHideLayoutNear() {
		if (isNearLayoutShow) {
			isNearLayoutShow = false;
			layoutNearAdvice.setVisibility(View.GONE);
		} else {
			layoutNearAdvice.setVisibility(View.VISIBLE);
			isNearLayoutShow = true;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mPoiSearch.destroy();
		mSuggestionSearch.destroy();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	public void startSearchPlace(String where) {
		if (where != null && where.trim().length() > 0) {
			if (-1 == NetworkUtil.getNetworkType(getApplicationContext())) {
				NetworkUtil.noNetworkHint(getApplicationContext());
			} else {

				Toast.makeText(getApplicationContext(), "正在查找" + where,
						Toast.LENGTH_SHORT).show();
				// mPoiSearch.searchInCity((new
				// PoiCitySearchOption()).city(findCity)
				// .keyword(findContent).pageNum(load_Index));

				PoiNearbySearchOption poiOption = new PoiNearbySearchOption();
				poiOption.keyword(where);
				poiOption.location(mLatLng);
				poiOption.radius(15 * 1000);
				poiOption.pageNum(0);
				try {
					mPoiSearch.searchNearby(poiOption); // mPoiSearch.searchInCity(arg0);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 存储搜索历史到数据库
				int existId = naviDb.getNaviIdByKey(where);
				if (existId != -1) {
					naviDb.deleteNaviHistoryById(existId);
				}
				NaviHistory naviHistory = new NaviHistory(where);
				naviDb.addNaviHistory(naviHistory);
				naviHistoryAdapter.notifyDataSetChanged();
			}
		}
	}

	public void onGetPoiResult(PoiResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(NavigationActivity.this, "未找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			// 添加结果
			LatLng llStart = mLatLng; // 当前位置
			naviArray = new ArrayList<NaviResultInfo>();
			for (int i = 1; i < result.getAllPoi().size(); i++) {
				PoiInfo poiInfo = result.getAllPoi().get(i);

				double distance = DistanceUtil.getDistance(llStart,
						poiInfo.location);

				NaviResultInfo naviResultInfo = new NaviResultInfo(i,
						poiInfo.name, poiInfo.address,
						poiInfo.location.longitude, poiInfo.location.latitude,
						distance);
				naviArray.add(naviResultInfo);
			}

			naviResultAdapter = new NaviResultAdapter(getApplicationContext(),
					naviArray);
			listResult.setVisibility(View.VISIBLE);
			isResultListShow = true;
			listResult.setAdapter(naviResultAdapter);

			listResult
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						public void onItemClick(
								android.widget.AdapterView<?> parent,
								android.view.View view, int position, long id) {
							// 开始导航
							listResult.setVisibility(View.GONE);
							isResultListShow = false;

							listHistory.setVisibility(View.GONE);
							isHistoryLayoutShow = false;

							if (MyApplication.isNaviInitialSuccess) {
								launchNavigator(mLatLng.latitude,
										mLatLng.longitude, "当前位置", naviArray
												.get(position).getLatitude(),
										naviArray.get(position).getLongitude(),
										naviArray.get(position).getName());
							} else {
								// 未成功初始化
								Log.e(Constant.TAG, "Initlal fail");
							}
						}
					});

			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			Toast.makeText(NavigationActivity.this, strInfo, Toast.LENGTH_LONG)
					.show();
		}
	}

	/**
	 * 启动GPS导航. 前置条件：导航引擎初始化成功
	 */
	private void launchNavigator(double startLatitude, double startLongitude,
			String startName, double endLatitude, double endLongitude,
			String endName) {
		BaiduNaviManager.getInstance().launchNavigator(this, startLatitude,
				startLongitude, startName, endLatitude, endLongitude, endName,
				NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME, // 算路方式
				true, // 真实导航
				BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, // 在离线策略
				new OnStartNavigationListener() { // 跳转监听

					@Override
					public void onJumpToNavigator(Bundle configParams) {
						Intent intent = new Intent(NavigationActivity.this,
								BNavigatorActivity.class);
						intent.putExtras(configParams);
						startActivity(intent);
					}

					@Override
					public void onJumpToDownloader() {
					}
				});
	}

	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(NavigationActivity.this, "抱歉，未找到结果",
					Toast.LENGTH_SHORT).show();
		} else {
			// 点击地图上搜索结果气球进入导航

			if (MyApplication.isNaviInitialSuccess) {
				launchNavigator(mLatLng.latitude, mLatLng.longitude, "当前位置",
						result.getLocation().latitude,
						result.getLocation().longitude, result.getAddress());
			} else {
				// 未成功初始化
				Log.e(Constant.TAG, "点击气球：未成功初始化");
			}

		}
	}

	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		if (res == null || res.getAllSuggestions() == null) {
			return;
		}
		sugAdapter.clear();
		for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
			if (info.key != null)
				sugAdapter.add(info.key);
		}
		sugAdapter.notifyDataSetChanged();
	}

	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
					.poiUid(poi.uid));
			// }
			return true;
		}
	}

	// @Override
	// protected void onResumeFragments() {
	// super.onResumeFragments();
	// View decorView = getWindow().getDecorView();
	// decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
	// }

	// ==========================
	int ret = 0;// 函数调用返回值

	public void startVoiceUnderstand() {
		if (-1 == NetworkUtil.getNetworkType(getApplicationContext())) {
			NetworkUtil.noNetworkHint(getApplicationContext());
		} else {
			// 初始化对象
			mSpeechUnderstander = SpeechUnderstander.createUnderstander(
					NavigationActivity.this, speechUnderstanderListener);
			setParam();

			if (mSpeechUnderstander.isUnderstanding()) { // 开始前检查状态
				mSpeechUnderstander.stopUnderstanding(); // 停止录音
			} else {
				ret = mSpeechUnderstander
						.startUnderstanding(mRecognizerListener);
				if (ret != 0) {
					// 语义理解失败,错误码:ret
				} else {
					// showTip(getString(R.string.text_begin));
				}
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
		String lag = preference.getString("voiceAccent", "mandarin");
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
				preference.getString("voiceBos", "4000"));
		// 设置语音后端点
		mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS,
				preference.getString("voiceEos", "1000"));
		// 设置标点符号
		mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT,
				preference.getString("understander_punc_preference", "1"));
		// 设置音频保存路径
		mSpeechUnderstander.setParameter(
				SpeechConstant.ASR_AUDIO_PATH,
				preference.getString("voicePath",
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
								progressVoice.setVisibility(View.GONE);
								imgVoiceSearch.setVisibility(View.VISIBLE);

								etHistoryWhere.setText(strContent);
								// startSearchPlace(strContent);
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

			// 开始识别
			imgVoiceSearch.setVisibility(View.GONE);
			progressVoice.setVisibility(View.VISIBLE);

		}

		@Override
		public void onBeginOfSpeech() {
			// showTip("onBeginOfSpeech");
			audioRecordDialog.showDialog();
		}

		@Override
		public void onError(SpeechError error) {
			// showTip("onError Code：" + error.getErrorCode());

			imgVoiceSearch.setVisibility(View.VISIBLE);
			progressVoice.setVisibility(View.GONE);
			Log.e(Constant.TAG, "SpeechError:" + error.getErrorCode());
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

		}
	};

}
