package com.tchip.carlauncher.ui;

import java.util.Calendar;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.bean.Titanic;
// import com.tchip.carlauncher.bean.Typefaces;
import com.tchip.carlauncher.service.LocationService;
import com.tchip.carlauncher.service.SpeakService;
import com.tchip.carlauncher.service.WeatherService;
import com.tchip.carlauncher.util.DateUtil;
import com.tchip.carlauncher.util.WeatherUtil;
import com.tchip.carlauncher.util.WeatherUtil.WEATHER_TYPE;
import com.tchip.carlauncher.view.TitanicTextView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity {
	private SharedPreferences sharedPreferences;
	private FrameLayout frameLayout;
	private String[] weatherArray;
	private boolean isLocated = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_weather);

		sharedPreferences = getSharedPreferences("CarLauncher",
				Context.MODE_PRIVATE);

		initialLayout();

		speakWeather(0);
	}

	private void showWeatherAnimation(WEATHER_TYPE type) {
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
			WeatherUtil.cloudAnimation(getApplicationContext(), frameLayout);
			break;
		}
	}

	private void initialLayout() {
		weatherArray = new String[6];

		RelativeLayout layoutWeather = (RelativeLayout) findViewById(R.id.layoutWeather);

		// 刷新按钮
		ImageView imageRefresh = (ImageView) findViewById(R.id.imageRefresh);
		imageRefresh.setOnClickListener(new MyOnClickListener());

		// 时钟信息
		int weekToday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		String weekRank = String.valueOf(Calendar.getInstance().get(
				Calendar.WEEK_OF_YEAR));
		String yearStr = String.valueOf(Calendar.getInstance().get(
				Calendar.YEAR));
		String monthStr = String.valueOf((Calendar.getInstance().get(
				Calendar.MONTH) + 1));
		String dayStr = String.valueOf(Calendar.getInstance().get(
				Calendar.DAY_OF_MONTH));
		TextView textWeek = (TextView) findViewById(R.id.textWeek);
		textWeek.setText("第" + weekRank + "周 · "
				+ DateUtil.getWeekStrByInt(weekToday));
		TextView textDate = (TextView) findViewById(R.id.textDate);
		textDate.setText(yearStr + "年" + monthStr + "月" + dayStr + "日");

		// Day 0 (Today) Weather and Time, Location Info

		TextView textLocation = (TextView) findViewById(R.id.textLocation);
		String cityName = sharedPreferences.getString("cityName", "未定位");
		textLocation.setText(cityName);

		String weatherToday = sharedPreferences.getString("day0weather", "未知");

		showWeatherAnimation(WeatherUtil.getTypeByStr(weatherToday));

		layoutWeather.setBackground(getResources().getDrawable(
				WeatherUtil.getWeatherBackground(WeatherUtil
						.getTypeByStr(weatherToday)))); // Background
		ImageView imageTodayWeather = (ImageView) findViewById(R.id.imageTodayWeather);
		imageTodayWeather.setImageResource(WeatherUtil
				.getWeatherDrawable(WeatherUtil.getTypeByStr(weatherToday)));
		TextView textTodayWeather = (TextView) findViewById(R.id.textTodayWeather);
		textTodayWeather.setText(weatherToday);

		TitanicTextView textTempHigh = (TitanicTextView) findViewById(R.id.textTempHigh);
		String day0tmpHigh = sharedPreferences.getString("day0tmpHigh", "25℃");
		textTempHigh.setText(day0tmpHigh);

		// textTempHigh.setTypeface(Typefaces.get(this, "Satisfy-Regular.ttf")); // 设置字体
		new Titanic().start(textTempHigh);

		TitanicTextView textTempLow = (TitanicTextView) findViewById(R.id.textTempLow);
		String day0tmpLow = sharedPreferences.getString("day0tmpLow", "15℃");
		textTempLow.setText(day0tmpLow);
		new Titanic().start(textTempLow);

		TextView textWetLevel = (TextView) findViewById(R.id.textWetLevel);
		textWetLevel.setText("湿度 "
				+ sharedPreferences.getString("humidity", "55.55%"));

		TextView textWind = (TextView) findViewById(R.id.textWind);
		String day0windStr = sharedPreferences.getString("day0wind", "东北风5");
		textWind.setText(day0windStr);

		TextView textUpdateTime = (TextView) findViewById(R.id.textUpdateTime);
		textUpdateTime.setText("发布时间 "
				+ sharedPreferences.getString("postTime", "05:55"));

		if (!"未定位".equals(cityName)) {
			weatherArray[0] = cityName + "今日天气:" + weatherToday + ","
					+ day0tmpLow + "到" + day0tmpHigh + "," + day0windStr;
			isLocated = true;
		} else {
			weatherArray[0] = "定位失败，无法获取天气信息";
			isLocated = false;
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
		day1image.setImageResource(WeatherUtil.getWeatherDrawable(WeatherUtil
				.getTypeByStr(day1weatherStr)));

		TextView day1tmpHigh = (TextView) findViewById(R.id.day1tmpHigh);
		String day1tmpHighStr = sharedPreferences
				.getString("day1tmpHigh", "35");
		day1tmpHigh.setText(day1tmpHighStr);

		TextView day1tmpLow = (TextView) findViewById(R.id.day1tmpLow);
		String day1tmpLowStr = sharedPreferences.getString("day1tmpLow", "25");
		day1tmpLow.setText(day1tmpLowStr);

		TextView day1wind = (TextView) findViewById(R.id.day1wind);
		String day1windStr = sharedPreferences.getString("day1wind", "东北风5");
		day1wind.setText(day1windStr);

		if (isLocated) {
			weatherArray[1] = day1weekStr + "天气：" + day1weatherStr + ","
					+ day1tmpLowStr + "到" + day1tmpHighStr + "," + day1windStr;
		} else {
			weatherArray[1] = "定位失败，无法获取天气信息";
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

		TextView day2tmpHigh = (TextView) findViewById(R.id.day2tmpHigh);
		String day2tmpHighStr = sharedPreferences
				.getString("day2tmpHigh", "35");
		day2tmpHigh.setText(day2tmpHighStr);

		TextView day2tmpLow = (TextView) findViewById(R.id.day2tmpLow);
		String day2tmpLowStr = sharedPreferences.getString("day2tmpLow", "25");
		day2tmpLow.setText(day2tmpLowStr);

		TextView day2wind = (TextView) findViewById(R.id.day2wind);
		String day2windStr = sharedPreferences.getString("day2wind", "东北风5");
		day2wind.setText(day2windStr);

		if (isLocated) {
			weatherArray[2] = day2WeekStr + "天气：" + day2WeatherStr + ","
					+ day2tmpLowStr + "到" + day2tmpHighStr + "," + day2windStr;
		} else {
			weatherArray[2] = "定位失败，无法获取天气信息";
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

		TextView day3tmpHigh = (TextView) findViewById(R.id.day3tmpHigh);
		String day3tmpHighStr = sharedPreferences
				.getString("day3tmpHigh", "35");
		day3tmpHigh.setText(day3tmpHighStr);

		TextView day3tmpLow = (TextView) findViewById(R.id.day3tmpLow);
		String day3tmpLowStr = sharedPreferences.getString("day3tmpLow", "25");
		day3tmpLow.setText(day3tmpLowStr);

		TextView day3wind = (TextView) findViewById(R.id.day3wind);
		String day3windStr = sharedPreferences.getString("day3wind", "东北风5");
		day3wind.setText(day3windStr);

		if (isLocated) {
			weatherArray[3] = day3WeekStr + "天气：" + day3WeatherStr + ","
					+ day3tmpLowStr + "到" + day3tmpHighStr + "," + day3windStr;
		} else {
			weatherArray[3] = "定位失败，无法获取天气信息";
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

		TextView day4tmpHigh = (TextView) findViewById(R.id.day4tmpHigh);
		String day4tmpHighStr = sharedPreferences
				.getString("day4tmpHigh", "35");
		day4tmpHigh.setText(day4tmpHighStr);

		TextView day4tmpLow = (TextView) findViewById(R.id.day4tmpLow);
		String day4tmpLowStr = sharedPreferences.getString("day4tmpLow", "25");
		day4tmpLow.setText(day4tmpLowStr);

		TextView day4wind = (TextView) findViewById(R.id.day4wind);
		String day4windStr = sharedPreferences.getString("day4wind", "东北风5");
		day4wind.setText(day4windStr);

		if (isLocated) {
			weatherArray[4] = day4WeekStr + "天气：" + day4WeatherStr + ","
					+ day4tmpLowStr + "到" + day4tmpHighStr + "," + day4windStr;
		} else {
			weatherArray[4] = "定位失败，无法获取天气信息";
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

		TextView day5tmpHigh = (TextView) findViewById(R.id.day5tmpHigh);
		String day5tmpHighStr = sharedPreferences
				.getString("day5tmpHigh", "35");
		day5tmpHigh.setText(day5tmpHighStr);

		TextView day5tmpLow = (TextView) findViewById(R.id.day5tmpLow);
		String day5tmpLowStr = sharedPreferences.getString("day5tmpLow", "25");
		day5tmpLow.setText(day5tmpLowStr);

		TextView day5wind = (TextView) findViewById(R.id.day5wind);
		String day5windStr = sharedPreferences.getString("day5wind", "东北风5");
		day5wind.setText(day5windStr);

		if (isLocated) {
			weatherArray[5] = day5WeekStr + "天气：" + day5WeatherStr + ","
					+ day5tmpLowStr + "到" + day5tmpHighStr + "," + day5windStr;
		} else {
			weatherArray[5] = "定位失败，无法获取天气信息";
		}

		// Day 6
		// TextView day6week = (TextView) findViewById(R.id.day6week);
		// String day6WeekStr = DateUtil.getWeekStrByInt(weekToday + 6);
		// day6week.setText(day6WeekStr);
		//
		// TextView day6date = (TextView) findViewById(R.id.day6date);
		// day6date.setText(sharedPreferences.getString("day6date",
		// "2015-01-01")
		// .substring(5, 10));
		//
		// ImageView day6image = (ImageView) findViewById(R.id.day6image);
		// String day6WeatherStr = sharedPreferences
		// .getString("day6weather", "未知");
		// day6image.setImageResource(WeatherUtil.getWeatherDrawable(WeatherUtil
		// .getTypeByStr(day6WeatherStr)));
		//
		// TextView day6tmpHigh = (TextView) findViewById(R.id.day6tmpHigh);
		// String day6tmpHighStr = sharedPreferences
		// .getString("day6tmpHigh", "35");
		// day6tmpHigh.setText(day6tmpHighStr);
		//
		// TextView day6tmpLow = (TextView) findViewById(R.id.day6tmpLow);
		// String day6tmpLowStr = sharedPreferences.getString("day6tmpLow",
		// "25");
		// day6tmpLow.setText(day6tmpLowStr);
		//
		// TextView day6wind = (TextView) findViewById(R.id.day6wind);
		// String day6windStr = sharedPreferences.getString("day6wind", "东北风5");
		// day6wind.setText(day6windStr);
		//
		// if (isLocated) {
		// weatherArray[6] = day6WeekStr + "天气：" + day6WeatherStr + ","
		// + day6tmpLowStr + "到" + day6tmpHighStr + "," + day6windStr;
		// } else {
		// weatherArray[6] = "定位失败，无法获取天气信息";
		// }

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

		// LinearLayout layoutDay6 = (LinearLayout)
		// findViewById(R.id.layoutDay6);
		// layoutDay6.setOnClickListener(new MyOnClickListener());

	}

	private void speakWeather(int day) {
		Intent intent = new Intent(this, SpeakService.class);
		intent.putExtra("content", weatherArray[day]);
		startService(intent);
	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
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

			case R.id.imageRefresh:
				// new Thread
				startLocationService();
				startWeatherService();
				break;
			}
		}
	}

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
		// TODO Auto-generated method stub
		super.onStop();
		exitWeather();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (KeyEvent.KEYCODE_BACK == keyCode)
			exitWeather();
		return super.onKeyDown(keyCode, event);
	}

	private void exitWeather() {
		finish();
	}

}
