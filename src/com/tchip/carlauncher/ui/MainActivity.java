package com.tchip.carlauncher.ui;

import java.util.ArrayList;
import java.util.List;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.service.BrightAdjustService;
import com.tchip.carlauncher.service.LocationService;
import com.tchip.carlauncher.service.RouteRecordService;
import com.tchip.carlauncher.service.SpeakService;
import com.tchip.carlauncher.service.WeatherService;
import com.tchip.carlauncher.util.WeatherUtil;
import com.tchip.carlauncher.view.TransitionViewPager;
import com.tchip.carlauncher.view.TransitionViewPager.TransitionEffect;
import com.tchip.carlauncher.view.TransitionViewPagerContainer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

public class MainActivity extends Activity {
	private View viewMain, viewVice;
	private List<View> viewList;
	private TransitionViewPager viewPager;
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		preferences = getSharedPreferences("CarLauncher", Context.MODE_PRIVATE);

		LayoutInflater inflater = LayoutInflater.from(this);
		viewMain = inflater.inflate(R.layout.activity_main_one, null);
		viewVice = inflater.inflate(R.layout.activity_main_two, null);

		viewList = new ArrayList<View>(); // 将要分页显示的View装入数组中
		viewList.add(viewMain);
		viewList.add(viewVice);
		viewPager = (TransitionViewPager) findViewById(R.id.viewpager);
		viewPager.setTransitionEffect(TransitionEffect.CubeOut);

		viewPager.setPageMargin(10);
		viewPager.setAdapter(pagerAdapter);

		iniService();
	}

	private void iniService() {
		// 位置
		Intent intentLocation = new Intent(this, LocationService.class);
		startService(intentLocation);

		// 亮度自动调整服务
		Intent intentBrightness = new Intent(this, BrightAdjustService.class);
		startService(intentBrightness);

		// 轨迹记录服务
		Intent intentRoute = new Intent(this, RouteRecordService.class);
		startService(intentRoute);

		// 更新天气
		new Thread(new WeatherUpdateThread()).start(); // 计时线程
	}

	final Handler weatherUpdateHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// Update Weather
				Intent intentWeather = new Intent(getApplicationContext(),
						WeatherService.class);
				startService(intentWeather);
			}
			super.handleMessage(msg);
		}
	};

	public class WeatherUpdateThread implements Runnable {

		@Override
		public void run() {
			// while (true) {
			try {
				Thread.sleep(3000);
				Message message = new Message();
				message.what = 1;
				weatherUpdateHandler.sendMessage(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// }
		}
	}

	PagerAdapter pagerAdapter = new PagerAdapter() {

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			if (view instanceof TransitionViewPagerContainer) {
				return ((TransitionViewPagerContainer) view).getChildAt(0) == obj;
			} else {
				return view == obj;
			}
		}

		@Override
		public int getCount() {
			return viewList.size();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(viewList.get(position));
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(viewList.get(position));
			viewPager.setObjectForPosition(viewList.get(position), position); // 动画需要
			if (position == 0)
				updateMainLayout();
			else
				updateViceLayout();
			return viewList.get(position);
		}

	};

	private void startSpeak(String content) {
		Intent intent = new Intent(this, SpeakService.class);
		intent.putExtra("content", content);
		startService(intent);
	}

	private void updateMainLayout() {
		// 天气
		ImageView imgWeather = (ImageView) findViewById(R.id.imgWeather);
		imgWeather.setImageResource(WeatherUtil
				.getWeatherBackground(WeatherUtil.getTypeByStr(preferences
						.getString("day0weather", "晴"))));
		imgWeather.setOnClickListener(new MyOnClickListener());

		// 多媒体
		ImageView imgMultimedia = (ImageView) findViewById(R.id.imgMultimedia);
		imgMultimedia.setOnClickListener(new MyOnClickListener());

		// 蓝牙拨号
		ImageView imgBluetooth = (ImageView) findViewById(R.id.imgBluetooth);
		imgBluetooth.setOnClickListener(new MyOnClickListener());

		// 轨迹
		ImageView imgRoute = (ImageView) findViewById(R.id.imgRoute);
		imgRoute.setOnClickListener(new MyOnClickListener());

		// 文件管理
		ImageView imgFileExplorer = (ImageView) findViewById(R.id.imgFileExplorer);
		imgFileExplorer.setOnClickListener(new MyOnClickListener());
	}

	private void updateViceLayout() {

		ImageView imgNear = (ImageView) findViewById(R.id.imgNear);
		imgNear.setOnClickListener(new MyOnClickListener());

		ImageView imgRoutePlan = (ImageView) findViewById(R.id.imgRoutePlan);
		imgRoutePlan.setOnClickListener(new MyOnClickListener());

		ImageView imgAbout = (ImageView) findViewById(R.id.imgAbout);
		imgAbout.setOnClickListener(new MyOnClickListener());

		ImageView imgChat = (ImageView) findViewById(R.id.imgChat);
		imgChat.setOnClickListener(new MyOnClickListener());

		ImageView imgSetting = (ImageView) findViewById(R.id.imgSetting);
		imgSetting.setOnClickListener(new MyOnClickListener());

	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// Main Layout
			case R.id.imgMultimedia:
				Intent intent11 = new Intent(MainActivity.this,
						MultimediaActivity.class);
				startActivity(intent11);
				overridePendingTransition(R.anim.zms_translate_down_out,
						R.anim.zms_translate_down_in);
				break;
			case R.id.imgBluetooth:
				Intent intent13 = new Intent(MainActivity.this,
						BluetoothActivity.class);
				startActivity(intent13);

				break;
			case R.id.imgRoute:
				Intent intent14 = new Intent(MainActivity.this,
						RouteListActivity.class);
				startActivity(intent14);
				break;
			case R.id.imgWeather:
				Intent intent15 = new Intent(MainActivity.this,
						WeatherActivity.class);
				startActivity(intent15);
				break;
			case R.id.imgFileExplorer:
				Intent intent16 = new Intent(MainActivity.this,
						FileRemoteControlActivity.class);
				startActivity(intent16);
				break;
			// Vice Layout
			case R.id.imgNear:
				Intent intent21 = new Intent(MainActivity.this,
						NearActivity.class);
				startActivity(intent21);
				overridePendingTransition(R.anim.zms_translate_down_out,
						R.anim.zms_translate_down_in);
				break;
			case R.id.imgRoutePlan:
				Intent intent22 = new Intent(MainActivity.this,
						RoutePlanActivity.class);
				startActivity(intent22);
				overridePendingTransition(R.anim.zms_translate_down_out,
						R.anim.zms_translate_down_in);
				break;
			case R.id.imgAbout:
				Intent intent23 = new Intent(MainActivity.this,
						AboutActivity.class);
				startActivity(intent23);
				overridePendingTransition(R.anim.zms_translate_up_out,
						R.anim.zms_translate_up_in);
				break;
			case R.id.imgChat:
				Intent intent24 = new Intent(MainActivity.this,
						ChatActivity.class);
				startActivity(intent24);
				overridePendingTransition(R.anim.zms_translate_up_out,
						R.anim.zms_translate_up_in);
				break;
			case R.id.imgSetting:
				Intent intent25 = new Intent(MainActivity.this,
						SettingActivity.class);
				startActivity(intent25);
				overridePendingTransition(R.anim.zms_translate_up_out,
						R.anim.zms_translate_up_in);
				break;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
	}

}
