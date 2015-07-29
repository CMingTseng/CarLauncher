package com.tchip.carlauncher.ui.activity;

import java.util.Calendar;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.model.Titanic;
import com.tchip.carlauncher.model.Typefaces;
// import com.tchip.carlauncher.bean.Typefaces;
import com.tchip.carlauncher.service.LocationService;
import com.tchip.carlauncher.service.SpeakService;
import com.tchip.carlauncher.service.WeatherService;
import com.tchip.carlauncher.util.DateUtil;
import com.tchip.carlauncher.util.NetworkUtil;
import com.tchip.carlauncher.util.WeatherUtil;
import com.tchip.carlauncher.util.WeatherUtil.WEATHER_TYPE;
import com.tchip.carlauncher.view.TitanicTextView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

public class WeatherActivity extends Activity {
	private SharedPreferences sharedPreferences;
	private FrameLayout frameLayout;
	private String[] weatherArray;
	
	/**
	 * 是否定位
	 */
	private boolean isLocated = false;

	/**
	 * 获取天气信息是否成功
	 */
	private boolean isGetSuccess = false;

	private String strNoLoction = "定位失败，无法获取天气信息";
	private String strNoWeather = "天气获取失败，请稍候重试";

	private ProgressBar updateProgress;
	private Button updateButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_weather);

		sharedPreferences = getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

		// 刷新按钮和进度条
		updateProgress = (ProgressBar) findViewById(R.id.updateProgress);
		updateProgress.setVisibility(View.INVISIBLE);
		updateButton = (Button) findViewById(R.id.updateButton);
		updateButton.setVisibility(View.VISIBLE);
		updateButton.setOnClickListener(new MyOnClickListener());

		initialLayout();

		if (-1 == NetworkUtil.getNetworkType(getApplicationContext())) {
			NetworkUtil.noNetworkHint(getApplicationContext());
		} else {
			// 数据是否自动更新
			if (sharedPreferences.getBoolean("voiceUpdateWeather", true)) {
				updateWeather(); // 会再次调用initialLayout
			} else {
				speakWeather(0);
			}
		}
	}

	private void showWeatherAnimation(WEATHER_TYPE type) {
		if (Constant.hasWeatherAnimation) {
			frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
			switch (type) {
			case RAIN:
				WeatherUtil.rainAnimation(getApplicationContext(), frameLayout);
				break;

			case SNOW:
				WeatherUtil.snowAnimation(getApplicationContext(), frameLayout);
				break;

			case SUN:
			case CLOUD:
			default:
				WeatherUtil
						.cloudAnimation(getApplicationContext(), frameLayout);
				break;
			}
		}
	}

	private void initialLayout() {
		weatherArray = new String[6];

		// 返回
		RelativeLayout layoutBack = (RelativeLayout) findViewById(R.id.layoutBack);
		layoutBack.setOnClickListener(new MyOnClickListener());
		Button btnToMainFromWeather = (Button) findViewById(R.id.btnToMainFromWeather);
		btnToMainFromWeather.setOnClickListener(new MyOnClickListener());

		// 时钟信息
		int weekToday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

		TextClock textClock = (TextClock) findViewById(R.id.textClock);
		TextClock textWeek = (TextClock) findViewById(R.id.textWeek);
		TextClock textDate = (TextClock) findViewById(R.id.textDate);

		textClock.setTypeface(Typefaces.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf"));
		textDate.setTypeface(Typefaces.get(this, Constant.Path.FONT
				+ "Font-Droid-Sans-Fallback.ttf"));
		textWeek.setTypeface(Typefaces.get(this, Constant.Path.FONT
				+ "Font-Droid-Sans-Fallback.ttf"));
		// Day 0 (Today) Weather and Time, Location Info
		String cityName = "";
		if ("未定位".equals(sharedPreferences.getString("cityName", "未定位"))) {
			cityName = sharedPreferences.getString("cityNameRealButOld", "未定位");
		} else {
			cityName = sharedPreferences.getString("cityName", "未定位");
		}
		TextView textLocation = (TextView) findViewById(R.id.textLocation);
		textLocation.setText(cityName);

		String weatherToday = sharedPreferences.getString("day0weather", "未知");

		showWeatherAnimation(WeatherUtil.getTypeByStr(weatherToday));

		// 背景
		RelativeLayout layoutWeather = (RelativeLayout) findViewById(R.id.layoutWeather);
		layoutWeather.setBackground(getResources().getDrawable(
				WeatherUtil.getWeatherBackground(WeatherUtil
						.getTypeByStr(weatherToday)))); // Background

		ImageView imageTodayWeather = (ImageView) findViewById(R.id.imageTodayWeather);
		imageTodayWeather.setImageResource(WeatherUtil
				.getWeatherDrawable(WeatherUtil.getTypeByStr(weatherToday)));
		TextView textTodayWeather = (TextView) findViewById(R.id.textTodayWeather);
		textTodayWeather.setText(weatherToday);

		TitanicTextView textTempRange = (TitanicTextView) findViewById(R.id.textTempRange);
		String day0tmpLow = sharedPreferences.getString("day0tmpLow", "15℃");
		String day0tmpHigh = sharedPreferences.getString("day0tmpHigh", "25℃");
		day0tmpLow = day0tmpLow.split("℃")[0];
		day0tmpHigh = day0tmpHigh.split("℃")[0];
		textTempRange.setText(day0tmpLow + "~" + day0tmpHigh);
		textTempRange.setTypeface(Typefaces.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf"));
		if (Constant.hasWeatherAnimation) {
			new Titanic().start(textTempRange);
		} else {
			textTempRange.setTextColor(Color.WHITE);
		}
		TextView textWetLevel = (TextView) findViewById(R.id.textWetLevel);
		textWetLevel.setText("湿度 "
				+ sharedPreferences.getString("humidity", "55.55%"));

		TextView textWind = (TextView) findViewById(R.id.textWind);
		String day0windStr = sharedPreferences.getString("day0wind", "东北风5");
		textWind.setText(day0windStr);

		TextView textUpdateTime = (TextView) findViewById(R.id.textUpdateTime);
		textUpdateTime.setText("发布时间 "
				+ sharedPreferences.getString("postTime", "2015 05:55").split(
						" ")[1]);

		if ("未定位".equals(cityName)) {
			weatherArray[0] = strNoLoction;
			isLocated = false;
		} else if ("未知".equals(weatherToday)) {
			weatherArray[0] = strNoWeather;
			isGetSuccess = false;
		} else {
			weatherArray[0] = cityName + "今日天气:" + weatherToday + ","
					+ day0tmpLow + "到" + day0tmpHigh + "℃," + day0windStr;
			isLocated = true;
			isGetSuccess = true;
		}

		// Day 1
		TextView day1Week = (TextView) findViewById(R.id.day1week);
		String day1weekStr = DateUtil.getWeekStrByInt(weekToday + 1);
		day1Week.setText(day1weekStr);

		TextView day1date = (TextView) findViewById(R.id.day1date);
		day1date.setText(sharedPreferences.getString("day1date", "2015-01-01")
				.substring(5, 10));

		ImageView day1image = (ImageView) findViewById(R.id.day1image);
		String day1weatherStr = sharedPreferences
				.getString("day1weather", "未知");
		TextView day1weather = (TextView) findViewById(R.id.day1weather);
		day1weather.setText(day1weatherStr);
		day1image.setImageResource(WeatherUtil.getWeatherDrawable(WeatherUtil
				.getTypeByStr(day1weatherStr)));

		TextView day1tmpRange = (TextView) findViewById(R.id.day1tmpRange);
		String day1tmpHighStr = sharedPreferences
				.getString("day1tmpHigh", "35");
		String day1tmpLowStr = sharedPreferences.getString("day1tmpLow", "25")
				.split("℃")[0];
		day1tmpRange.setText(day1tmpLowStr + "~" + day1tmpHighStr);
		day1tmpRange.setTypeface(Typefaces.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf"));

		TextView day1wind = (TextView) findViewById(R.id.day1wind);
		String day1windStr = sharedPreferences.getString("day1wind", "东北风5");
		day1wind.setText(day1windStr);

		if (!isLocated) {
			weatherArray[1] = strNoLoction;
		} else if (!isGetSuccess) {
			weatherArray[1] = strNoWeather;
		} else {
			weatherArray[1] = day1weekStr + "天气：" + day1weatherStr + ","
					+ day1tmpLowStr + "~" + day1tmpHighStr + "," + day1windStr;
		}

		// Day 2
		TextView day2week = (TextView) findViewById(R.id.day2week);
		String day2WeekStr = DateUtil.getWeekStrByInt(weekToday + 2);
		day2week.setText(day2WeekStr);

		TextView day2date = (TextView) findViewById(R.id.day2date);
		day2date.setText(sharedPreferences.getString("day2date", "2015-01-01")
				.substring(5, 10));

		ImageView day2image = (ImageView) findViewById(R.id.day2image);
		String day2WeatherStr = sharedPreferences
				.getString("day2weather", "未知");
		day2image.setImageResource(WeatherUtil.getWeatherDrawable(WeatherUtil
				.getTypeByStr(day2WeatherStr)));

		TextView day2tmpRange = (TextView) findViewById(R.id.day2tmpRange);
		String day2tmpHighStr = sharedPreferences
				.getString("day2tmpHigh", "35");
		String day2tmpLowStr = sharedPreferences.getString("day2tmpLow", "25")
				.split("℃")[0];
		day2tmpRange.setText(day2tmpLowStr + "~" + day2tmpHighStr);
		day2tmpRange.setTypeface(Typefaces.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf"));

		TextView day2wind = (TextView) findViewById(R.id.day2wind);
		String day2windStr = sharedPreferences.getString("day2wind", "东北风5");
		day2wind.setText(day2windStr);

		if (!isLocated) {
			weatherArray[2] = strNoLoction;
		} else if (!isGetSuccess) {
			weatherArray[2] = strNoWeather;
		} else {
			weatherArray[2] = day2WeekStr + "天气：" + day2WeatherStr + ","
					+ day2tmpLowStr + "~" + day2tmpHighStr + "," + day2windStr;
		}

		// Day 3
		TextView day3week = (TextView) findViewById(R.id.day3week);
		String day3WeekStr = DateUtil.getWeekStrByInt(weekToday + 3);
		day3week.setText(day3WeekStr);

		TextView day3date = (TextView) findViewById(R.id.day3date);
		day3date.setText(sharedPreferences.getString("day3date", "2015-01-01")
				.substring(5, 10));

		ImageView day3image = (ImageView) findViewById(R.id.day3image);
		String day3WeatherStr = sharedPreferences
				.getString("day3weather", "未知");
		day3image.setImageResource(WeatherUtil.getWeatherDrawable(WeatherUtil
				.getTypeByStr(day3WeatherStr)));

		TextView day3tmpRange = (TextView) findViewById(R.id.day3tmpRange);
		String day3tmpHighStr = sharedPreferences
				.getString("day3tmpHigh", "35");
		String day3tmpLowStr = sharedPreferences.getString("day3tmpLow", "25")
				.split("℃")[0];
		day3tmpRange.setText(day3tmpLowStr + "~" + day3tmpHighStr);
		day3tmpRange.setTypeface(Typefaces.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf"));

		TextView day3wind = (TextView) findViewById(R.id.day3wind);
		String day3windStr = sharedPreferences.getString("day3wind", "东北风5");
		day3wind.setText(day3windStr);
		
		if (!isLocated) {
			weatherArray[3] = strNoLoction;
		} else if (!isGetSuccess) {
			weatherArray[3] = strNoWeather;
		} else {
			weatherArray[3] = day3WeekStr + "天气：" + day3WeatherStr + ","
					+ day3tmpLowStr + "~" + day3tmpHighStr + "," + day3windStr;
		}

		// Day 4
		TextView day4week = (TextView) findViewById(R.id.day4week);
		String day4WeekStr = DateUtil.getWeekStrByInt(weekToday + 4);
		day4week.setText(day4WeekStr);

		TextView day4date = (TextView) findViewById(R.id.day4date);
		day4date.setText(sharedPreferences.getString("day4date", "2015-01-01")
				.substring(5, 10));

		ImageView day4image = (ImageView) findViewById(R.id.day4image);
		String day4WeatherStr = sharedPreferences
				.getString("day4weather", "未知");
		day4image.setImageResource(WeatherUtil.getWeatherDrawable(WeatherUtil
				.getTypeByStr(day4WeatherStr)));

		TextView day4tmpRange = (TextView) findViewById(R.id.day4tmpRange);
		String day4tmpHighStr = sharedPreferences
				.getString("day4tmpHigh", "35");
		String day4tmpLowStr = sharedPreferences.getString("day4tmpLow", "25")
				.split("℃")[0];
		day4tmpRange.setText(day4tmpLowStr + "~" + day4tmpHighStr);
		day4tmpRange.setTypeface(Typefaces.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf"));

		TextView day4wind = (TextView) findViewById(R.id.day4wind);
		String day4windStr = sharedPreferences.getString("day4wind", "东北风5");
		day4wind.setText(day4windStr);
		
		if (!isLocated) {
			weatherArray[4] = strNoLoction;
		} else if (!isGetSuccess) {
			weatherArray[4] = strNoWeather;
		} else {
			weatherArray[4] = day4WeekStr + "天气：" + day4WeatherStr + ","
					+ day4tmpLowStr + "~" + day4tmpHighStr + "," + day4windStr;
		}
		

		// Day 5
		TextView day5week = (TextView) findViewById(R.id.day5week);
		String day5WeekStr = DateUtil.getWeekStrByInt(weekToday + 5);
		day5week.setText(day5WeekStr);

		TextView day5date = (TextView) findViewById(R.id.day5date);
		day5date.setText(sharedPreferences.getString("day5date", "2015-01-01")
				.substring(5, 10));

		ImageView day5image = (ImageView) findViewById(R.id.day5image);
		String day5WeatherStr = sharedPreferences
				.getString("day5weather", "未知");
		day5image.setImageResource(WeatherUtil.getWeatherDrawable(WeatherUtil
				.getTypeByStr(day5WeatherStr)));

		TextView day5tmpRange = (TextView) findViewById(R.id.day5tmpRange);
		String day5tmpHighStr = sharedPreferences
				.getString("day5tmpHigh", "35");
		String day5tmpLowStr = sharedPreferences.getString("day5tmpLow", "25")
				.split("℃")[0];
		day5tmpRange.setText(day5tmpLowStr + "~" + day5tmpHighStr);
		day5tmpRange.setTypeface(Typefaces.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf"));

		TextView day5wind = (TextView) findViewById(R.id.day5wind);
		String day5windStr = sharedPreferences.getString("day5wind", "东北风5");
		day5wind.setText(day5windStr);
		
		if (!isLocated) {
			weatherArray[5] = strNoLoction;
		} else if (!isGetSuccess) {
			weatherArray[5] = strNoWeather;
		} else {
			weatherArray[5] = day5WeekStr + "天气：" + day5WeatherStr + ","
					+ day5tmpLowStr + "~" + day5tmpHighStr + "," + day5windStr;
		}

		LinearLayout layoutDay1 = (LinearLayout) findViewById(R.id.layoutDay1);
		layoutDay1.setOnClickListener(new MyOnClickListener());

		LinearLayout layoutDay2 = (LinearLayout) findViewById(R.id.layoutDay2);
		layoutDay2.setOnClickListener(new MyOnClickListener());

		LinearLayout layoutDay3 = (LinearLayout) findViewById(R.id.layoutDay3);
		layoutDay3.setOnClickListener(new MyOnClickListener());

		LinearLayout layoutDay4 = (LinearLayout) findViewById(R.id.layoutDay4);
		layoutDay4.setOnClickListener(new MyOnClickListener());

		LinearLayout layoutDay5 = (LinearLayout) findViewById(R.id.layoutDay5);
		layoutDay5.setOnClickListener(new MyOnClickListener());

	}

	private void speakWeather(int day) {
		Intent intent = new Intent(this, SpeakService.class);
		intent.putExtra("content", weatherArray[day]);
		startService(intent);
	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.layoutDay1:
				speakWeather(1);
				break;

			case R.id.layoutDay2:
				speakWeather(2);
				break;

			case R.id.layoutDay3:
				speakWeather(3);
				break;

			case R.id.layoutDay4:
				speakWeather(4);
				break;
			case R.id.layoutDay5:
				speakWeather(5);
				break;

			case R.id.layoutDay6:
				speakWeather(6);
				break;

			case R.id.updateButton:
				updateWeather();
				break;

			case R.id.layoutBack:
			case R.id.btnToMainFromWeather:
				backToMain();
				break;
			}
		}
	}

	private void backToMain() {
		finish();
	}

	private void updateWeather() {
		if (-1 == NetworkUtil.getNetworkType(getApplicationContext())) {
			NetworkUtil.noNetworkHint(getApplicationContext());
		} else {
			startLocationService();
			updateButton.setVisibility(View.INVISIBLE);
			updateProgress.setVisibility(View.VISIBLE);
			new Thread(new UpdateWeatherThread()).start();
		}
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
				updateButton.setVisibility(View.VISIBLE);
				updateProgress.setVisibility(View.INVISIBLE);
				initialLayout();
				speakWeather(0);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void startLocationService() {
		Intent intent = new Intent(this, LocationService.class);
		startService(intent);
	}

	private void startWeatherService() {
		Intent intent = new Intent(this, WeatherService.class);
		startService(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
	}

	@Override
	protected void onStop() {
		super.onStop();
		exitWeather();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode)
			exitWeather();
		return super.onKeyDown(keyCode, event);
	}

	private void exitWeather() {
		finish();
		overridePendingTransition(R.anim.zms_translate_down_out,
				R.anim.zms_translate_down_in);
	}

}
