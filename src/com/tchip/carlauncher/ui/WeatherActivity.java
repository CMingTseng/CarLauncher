package com.tchip.carlauncher.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.service.LocationService;
import com.tchip.carlauncher.service.WeatherService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity {
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_weather);

		sharedPreferences = getSharedPreferences("CarLauncher",
				getApplicationContext().MODE_PRIVATE);
		initialLayout();
	}

	private void initialLayout() {
		startLocationService();
		startWeatherService();

		LinearLayout layoutWeather = (LinearLayout) findViewById(R.id.layoutWeather);

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
		textWeek.setText("第" + weekRank + "周 · " + getWeekStr(weekToday));
		TextView textDate = (TextView) findViewById(R.id.textDate);
		textDate.setText(yearStr + "年" + monthStr + "月" + dayStr + "日");

		// Day 0 (Today) Weather and Time, Location Info

		TextView textLocation = (TextView) findViewById(R.id.textLocation);
		textLocation.setText(sharedPreferences.getString("cityName", "未定位"));

		String weatherToday = sharedPreferences.getString("day0weather", "未知");
		layoutWeather.setBackground(getResources().getDrawable(
				getWeatherackground(weatherToday))); // Background
		ImageView imageTodayWeather = (ImageView) findViewById(R.id.imageTodayWeather);
		imageTodayWeather.setImageResource(getWeatherDrawable(weatherToday));
		TextView textTodayWeather = (TextView) findViewById(R.id.textTodayWeather);
		textTodayWeather.setText(weatherToday);

		TextView textTempHigh = (TextView) findViewById(R.id.textTempHigh);
		textTempHigh.setText(sharedPreferences.getString("day0tmpHigh", "25℃"));
		TextView textTempLow = (TextView) findViewById(R.id.textTempLow);
		textTempLow.setText(sharedPreferences.getString("day0tmpLow", "15℃"));

		TextView textWetLevel = (TextView) findViewById(R.id.textWetLevel);
		textWetLevel.setText("湿度 "
				+ sharedPreferences.getString("humidity", "55.55%"));

		TextView textWind = (TextView) findViewById(R.id.textWind);
		textWind.setText(sharedPreferences.getString("day0wind", "东北风5") + "级");

		TextView textUpdateTime = (TextView) findViewById(R.id.textUpdateTime);
		textUpdateTime.setText("发布时间 "
				+ sharedPreferences.getString("postTime", "05:55"));

		// Day 1
		TextView day1week = (TextView) findViewById(R.id.day1week);
		day1week.setText(getWeekStr(weekToday + 1));
		TextView day1date = (TextView) findViewById(R.id.day1date);
		day1date.setText(sharedPreferences.getString("day1date", "2015-01-01")
				.substring(5, 10));
		ImageView day1image = (ImageView) findViewById(R.id.day1image);
		day1image.setImageResource(getWeatherDrawable(sharedPreferences
				.getString("day1weather", "未知")));
		TextView day1tmpHigh = (TextView) findViewById(R.id.day1tmpHigh);
		day1tmpHigh.setText(sharedPreferences.getString("day1tmpHigh", "35"));
		TextView day1tmpLow = (TextView) findViewById(R.id.day1tmpLow);
		day1tmpLow.setText(sharedPreferences.getString("day1tmpLow", "25"));
		TextView day1wind = (TextView) findViewById(R.id.day1wind);
		day1wind.setText(sharedPreferences.getString("day1wind", "东北风5") + "级");

		// Day 2
		TextView day2week = (TextView) findViewById(R.id.day2week);
		day2week.setText(getWeekStr(weekToday + 2));
		TextView day2date = (TextView) findViewById(R.id.day2date);
		day2date.setText(sharedPreferences.getString("day2date", "2015-01-01")
				.substring(5, 10));
		ImageView day2image = (ImageView) findViewById(R.id.day2image);
		day2image.setImageResource(getWeatherDrawable(sharedPreferences
				.getString("day2weather", "未知")));
		TextView day2tmpHigh = (TextView) findViewById(R.id.day2tmpHigh);
		day2tmpHigh.setText(sharedPreferences.getString("day2tmpHigh", "35"));
		TextView day2tmpLow = (TextView) findViewById(R.id.day2tmpLow);
		day2tmpLow.setText(sharedPreferences.getString("day2tmpLow", "25"));
		TextView day2wind = (TextView) findViewById(R.id.day2wind);
		day2wind.setText(sharedPreferences.getString("day2wind", "东北风5") + "级");

		// Day 3
		TextView day3week = (TextView) findViewById(R.id.day3week);
		day3week.setText(getWeekStr(weekToday + 3));
		TextView day3date = (TextView) findViewById(R.id.day3date);
		day3date.setText(sharedPreferences.getString("day3date", "2015-01-01")
				.substring(5, 10));
		ImageView day3image = (ImageView) findViewById(R.id.day3image);
		day3image.setImageResource(getWeatherDrawable(sharedPreferences
				.getString("day3weather", "未知")));
		TextView day3tmpHigh = (TextView) findViewById(R.id.day3tmpHigh);
		day3tmpHigh.setText(sharedPreferences.getString("day3tmpHigh", "35"));
		TextView day3tmpLow = (TextView) findViewById(R.id.day3tmpLow);
		day3tmpLow.setText(sharedPreferences.getString("day3tmpLow", "25"));
		TextView day3wind = (TextView) findViewById(R.id.day3wind);
		day3wind.setText(sharedPreferences.getString("day3wind", "东北风5") + "级");

		// Day 4
		TextView day4week = (TextView) findViewById(R.id.day4week);
		day4week.setText(getWeekStr(weekToday + 4));
		TextView day4date = (TextView) findViewById(R.id.day4date);
		day4date.setText(sharedPreferences.getString("day4date", "2015-01-01")
				.substring(5, 10));
		ImageView day4image = (ImageView) findViewById(R.id.day4image);
		day4image.setImageResource(getWeatherDrawable(sharedPreferences
				.getString("day4weather", "未知")));
		TextView day4tmpHigh = (TextView) findViewById(R.id.day4tmpHigh);
		day4tmpHigh.setText(sharedPreferences.getString("day4tmpHigh", "35"));
		TextView day4tmpLow = (TextView) findViewById(R.id.day4tmpLow);
		day4tmpLow.setText(sharedPreferences.getString("day4tmpLow", "25"));
		TextView day4wind = (TextView) findViewById(R.id.day4wind);
		day4wind.setText(sharedPreferences.getString("day4wind", "东北风5") + "级");

		// Day 5
		TextView day5week = (TextView) findViewById(R.id.day5week);
		day5week.setText(getWeekStr(weekToday + 5));
		TextView day5date = (TextView) findViewById(R.id.day5date);
		day5date.setText(sharedPreferences.getString("day5date", "2015-01-01")
				.substring(5, 10));
		ImageView day5image = (ImageView) findViewById(R.id.day5image);
		day5image.setImageResource(getWeatherDrawable(sharedPreferences
				.getString("day5weather", "未知")));
		TextView day5tmpHigh = (TextView) findViewById(R.id.day5tmpHigh);
		day5tmpHigh.setText(sharedPreferences.getString("day5tmpHigh", "35"));
		TextView day5tmpLow = (TextView) findViewById(R.id.day5tmpLow);
		day5tmpLow.setText(sharedPreferences.getString("day5tmpLow", "25"));
		TextView day5wind = (TextView) findViewById(R.id.day5wind);
		day5wind.setText(sharedPreferences.getString("day5wind", "东北风5") + "级");

		// Day 6
		TextView day6week = (TextView) findViewById(R.id.day6week);
		day6week.setText(getWeekStr(weekToday + 6));
		TextView day6date = (TextView) findViewById(R.id.day6date);
		day6date.setText(sharedPreferences.getString("day6date", "2015-01-01")
				.substring(5, 10));
		ImageView day6image = (ImageView) findViewById(R.id.day6image);
		day6image.setImageResource(getWeatherDrawable(sharedPreferences
				.getString("day6weather", "未知")));
		TextView day6tmpHigh = (TextView) findViewById(R.id.day6tmpHigh);
		day6tmpHigh.setText(sharedPreferences.getString("day6tmpHigh", "35"));
		TextView day6tmpLow = (TextView) findViewById(R.id.day6tmpLow);
		day6tmpLow.setText(sharedPreferences.getString("day6tmpLow", "25"));
		TextView day6wind = (TextView) findViewById(R.id.day6wind);
		day6wind.setText(sharedPreferences.getString("day6wind", "东北风5") + "级");

	}

	private int getWeatherackground(String weather) {
		if (weather.equals("未知")) {
			return isDay() ? (R.drawable.icon_weather_bg_default_day)
					: (R.drawable.icon_weather_bg_default_night);
		} else if (weather.equals("晴")) {
			return isDay() ? (R.drawable.icon_weather_sun_white)
					: (R.drawable.icon_weather_bg_sun_night);
		} else if (weather.equals("多云") || weather.equals("阴")) {
			return isDay() ? R.drawable.icon_weather_bg_cloud_day
					: R.drawable.icon_weather_bg_cloud_night;
		} else if (weather.equals("阵雨") || weather.equals("雷阵雨")
				|| weather.equals("小雨") || weather.equals("中雨")
				|| weather.equals("大雨") || weather.equals("暴雨")
				|| weather.equals("大暴雨") || weather.equals("特大暴雨")
				|| weather.equals("小到中雨") || weather.equals("中到大雨")
				|| weather.equals("大到暴雨") || weather.equals("暴雨到大暴雨")
				|| weather.equals("大暴雨到特大暴雨")) {
			return R.drawable.icon_weather_bg_rain;
		} else if (weather.equals("阵雪") || weather.equals("小雪")
				|| weather.equals("中雪") || weather.equals("大雪")
				|| weather.equals("暴雪") || weather.equals("小到中雪")
				|| weather.equals("中到大雪") || weather.equals("大到暴雪")) {
			return R.drawable.icon_weather_snow_white;
		} else if (weather.equals("雷阵雨伴有冰雹") || weather.equals("冻雨")) {
			return R.drawable.icon_weather_hail_white;
		} else if (weather.equals("雨夹雪")) {
			return R.drawable.icon_weather_rain_snow_white;
		} else if (weather.equals("雾") || weather.equals("霾")
				|| weather.equals("浮尘") || weather.equals("沙尘暴")
				|| weather.equals("强沙尘暴") || weather.equals("扬沙")) {
			return isDay() ? (R.drawable.weather_fog_day)
					: (R.drawable.weather_fog_night);
		} else if (weather.equals("多云转雪") || weather.equals("多云转大雪")
				|| weather.equals("雪转多云")) {
			return R.drawable.icon_weather_snow_cloud_white;
		} else if (weather.equals("多云转雨") || weather.equals("多云转大雨")
				|| weather.equals("雨转多云")) {
			return R.drawable.icon_weather_rain_cloud_white;
		} else if (weather.equals("晴转雪") || weather.equals("晴转大雪")
				|| weather.equals("雪转晴")) {
			return R.drawable.icon_weather_sun_snow_white;
		}
		return isDay() ? (R.drawable.icon_weather_bg_default_day)
				: (R.drawable.icon_weather_bg_default_night);

	}

	private String getWeekStr(int week) {
		if (week > 7)
			week = week % 7;
		switch (week) {
		case 1:
			return "星期日";
		case 2:
			return "星期一";
		case 3:
			return "星期二";
		case 4:
			return "星期三";
		case 5:
			return "星期四";
		case 6:
			return "星期五";
		case 7:
			return "星期六";
		default:
			return "星期日";
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

	private void setWeatherLogoOld(String weatherStr) {
		ImageView imgWeatherOne = (ImageView) findViewById(R.id.imgWeatherOne);
		ImageView imgWeatherTwo = (ImageView) findViewById(R.id.imgWeatherTwo);
		if (weatherStr.contains("转")) {
			String strWeatherOne = weatherStr.split("转")[0];
			String strWeatherTwo = weatherStr.split("转")[1];
			imgWeatherOne
					.setBackgroundResource(getWeatherDrawable(strWeatherOne));
			imgWeatherTwo.setVisibility(View.VISIBLE);
			imgWeatherTwo
					.setBackgroundResource(getWeatherDrawable(strWeatherTwo));

		} else {
			imgWeatherOne.setBackgroundResource(getWeatherDrawable(weatherStr));
			imgWeatherTwo.setVisibility(View.GONE);
		}
	}

	private int getWeatherDrawable(String weather) {
		if (weather.equals("未知")) {
			return R.drawable.icon_weather_na;
		} else if (weather.equals("晴")) {
			return isDay() ? (R.drawable.icon_weather_sun_white)
					: (R.drawable.icon_weather_moon_white);
		} else if (weather.equals("多云") || weather.equals("阴")) {
			return isDay() ? R.drawable.icon_weather_cloud_day_white
					: R.drawable.icon_weather_cloud_night_white;
		} else if (weather.equals("阵雨") || weather.equals("雷阵雨")
				|| weather.equals("小雨") || weather.equals("中雨")
				|| weather.equals("大雨") || weather.equals("暴雨")
				|| weather.equals("大暴雨") || weather.equals("特大暴雨")
				|| weather.equals("小到中雨") || weather.equals("中到大雨")
				|| weather.equals("大到暴雨") || weather.equals("暴雨到大暴雨")
				|| weather.equals("大暴雨到特大暴雨")) {
			return R.drawable.icon_weather_rain_white;
		} else if (weather.equals("阵雪") || weather.equals("小雪")
				|| weather.equals("中雪") || weather.equals("大雪")
				|| weather.equals("暴雪") || weather.equals("小到中雪")
				|| weather.equals("中到大雪") || weather.equals("大到暴雪")) {
			return R.drawable.icon_weather_snow_white;
		} else if (weather.equals("雷阵雨伴有冰雹") || weather.equals("冻雨")) {
			return R.drawable.icon_weather_hail_white;
		} else if (weather.equals("雨夹雪")) {
			return R.drawable.icon_weather_rain_snow_white;
		} else if (weather.equals("雾") || weather.equals("霾")
				|| weather.equals("浮尘") || weather.equals("沙尘暴")
				|| weather.equals("强沙尘暴") || weather.equals("扬沙")) {
			return isDay() ? (R.drawable.weather_fog_day)
					: (R.drawable.weather_fog_night);
		} else if (weather.equals("多云转雪") || weather.equals("多云转大雪")
				|| weather.equals("雪转多云")) {
			return R.drawable.icon_weather_snow_cloud_white;
		} else if (weather.equals("多云转雨") || weather.equals("多云转大雨")
				|| weather.equals("雨转多云")) {
			return R.drawable.icon_weather_rain_cloud_white;
		} else if (weather.equals("晴转雪") || weather.equals("晴转大雪")
				|| weather.equals("雪转晴")) {
			return R.drawable.icon_weather_sun_snow_white;
		}
		return isDay() ? (R.drawable.icon_weather_sun_white)
				: (R.drawable.icon_weather_moon_white);
	}

	private boolean isDay() {
		Date date = new Date();
		int hour = date.getHours();
		if (hour > 18 || hour < 6)
			return false;
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
	}

}
