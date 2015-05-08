package com.tchip.carlauncher.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.service.LocationService;
import com.tchip.carlauncher.service.SpeakService;
import com.tchip.carlauncher.service.WeatherService;
import com.tchip.carlauncher.util.WeatherUtil;
import com.tchip.carlauncher.view.MyViewPager;
import com.tchip.carlauncher.view.MyViewPager.TransitionEffect;
import com.tchip.carlauncher.view.MyViewPagerContainer;

import android.app.Activity;
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
import android.widget.Toast;

public class MainActivity extends Activity {
	private View viewMain, viewVice;
	private List<View> viewList;
	private MyViewPager viewPager;
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		preferences = getSharedPreferences("CarLauncher",
				getApplicationContext().MODE_PRIVATE);

		// Update Location
		Intent intent = new Intent(this, LocationService.class);
		startService(intent);

		LayoutInflater inflater = getLayoutInflater().from(this);
		viewMain = inflater.inflate(R.layout.activity_main_one, null);
		viewVice = inflater.inflate(R.layout.activity_main_two, null);

		viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
		viewList.add(viewMain);
		viewList.add(viewVice);
		viewPager = (MyViewPager) findViewById(R.id.viewpager);
		viewPager.setTransitionEffect(TransitionEffect.CubeOut);

		viewPager.setPageMargin(10);
		viewPager.setAdapter(pagerAdapter);

		new Thread(new StartWeatherServiceThread()).start(); // 计时线程
	}

	final Handler timeHandler = new Handler() {
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

	public class StartWeatherServiceThread implements Runnable {

		@Override
		public void run() {
			// while (true) {
			try {
				Thread.sleep(3000);
				Message message = new Message();
				message.what = 1;
				timeHandler.sendMessage(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// }
		}
	}

	PagerAdapter pagerAdapter = new PagerAdapter() {

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			if (view instanceof MyViewPagerContainer) {
				return ((MyViewPagerContainer) view).getChildAt(0) == obj;
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

	private void startWeatherService() {
		Intent intent = new Intent(this, WeatherService.class);
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
			int version = Integer.valueOf(android.os.Build.VERSION.SDK);
			switch (v.getId()) {
			// Main Layout
			case R.id.imgMultimedia:
				Intent intent11 = new Intent(MainActivity.this,
						MultimediaActivity.class);
				startActivity(intent11);
				if (version > 5) {
					overridePendingTransition(R.anim.zms_translate_down_out,
							R.anim.zms_translate_down_in);
				}
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
			// Vice Layout
			case R.id.imgNear:
				Intent intent21 = new Intent(MainActivity.this,
						NearActivity.class);
				startActivity(intent21);
				if (version > 5) {
					overridePendingTransition(R.anim.zms_translate_down_out,
							R.anim.zms_translate_down_in);
				}
				break;
			case R.id.imgRoutePlan:
				Intent intent22 = new Intent(MainActivity.this,
						RoutePlanActivity.class);
				startActivity(intent22);
				if (version > 5) {
					overridePendingTransition(R.anim.zms_translate_down_out,
							R.anim.zms_translate_down_in);
				}
				break;
			case R.id.imgAbout:
				Intent intent23 = new Intent(MainActivity.this,
						AboutActivity.class);
				startActivity(intent23);
				if (version > 5) {
					overridePendingTransition(R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				}
				break;
			case R.id.imgChat:
				Intent intent24 = new Intent(MainActivity.this,
						ChatActivity.class);
				startActivity(intent24);
				if (version > 5) {
					overridePendingTransition(R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				}
				break;
			case R.id.imgSetting:
				Intent intent25 = new Intent(MainActivity.this,
						SettingActivity.class);
				startActivity(intent25);
				if (version > 5) {
					overridePendingTransition(R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				}
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
