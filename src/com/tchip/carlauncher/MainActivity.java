package com.tchip.carlauncher;

import java.util.Calendar;

import com.tchip.carlauncher.service.LocationService;
import com.tchip.carlauncher.service.SpeakService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		sharedPreferences = getSharedPreferences("CarLauncher",
				getApplicationContext().MODE_PRIVATE);

		setContentView(R.layout.activity_main_metro);
		// startSpeak("欢迎使用天启行车记录仪");

		updateLayout();
	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			}
		}
	}

	private void startSpeak(String content) {
		Intent intent = new Intent(this, SpeakService.class);
		intent.putExtra("content", content);
		startService(intent);
	}

	private void startLocationService() {
		Intent intent = new Intent(this, LocationService.class);
		startService(intent);
	}

	private void updateLayout() {
		startLocationService();

		// 时钟信息
		String weekStr = String.valueOf(Calendar.getInstance().get(
				Calendar.DAY_OF_WEEK));
		String weekRank = String.valueOf(Calendar.getInstance().get(
				Calendar.WEEK_OF_YEAR));
		String yearStr = String.valueOf(Calendar.getInstance().get(
				Calendar.YEAR));
		String monthStr = String.valueOf((Calendar.getInstance().get(
				Calendar.MONTH) + 1));
		String dayStr = String.valueOf(Calendar.getInstance().get(
				Calendar.DAY_OF_MONTH));
		if ("1".equals(weekStr)) {
			weekStr = "天";
		} else if ("2".equals(weekStr)) {
			weekStr = "一";
		} else if ("3".equals(weekStr)) {
			weekStr = "二";
		} else if ("4".equals(weekStr)) {
			weekStr = "三";
		} else if ("5".equals(weekStr)) {
			weekStr = "四";
		} else if ("6".equals(weekStr)) {
			weekStr = "五";
		} else if ("7".equals(weekStr)) {
			weekStr = "六";
		}
		TextView textWeek = (TextView) findViewById(R.id.textWeek);
		textWeek.setText("星期" + weekStr + "·第" + weekRank + "周");
		TextView textDate = (TextView) findViewById(R.id.textDate);
		textDate.setText(yearStr + "年" + monthStr + "月" + dayStr + "日");
		// 天气
		TextView textWeather = (TextView) findViewById(R.id.textWeather);
		String weatherStr = sharedPreferences.getString("weather", "晴朗");
		if (!weatherStr.equals("晴朗")) {
			textWeather.setText(weatherStr);

			// 城市
			TextView textLocation = (TextView) findViewById(R.id.textLocation);
			textLocation
					.setText(sharedPreferences.getString("cityName", "北京市"));

			// 天气图标

			setWeatherLogo(weatherStr);

			// 当前温度
			TextView textTempNow = (TextView) findViewById(R.id.textTempNow);
			textTempNow.setText(sharedPreferences.getString("tempNow", "25")
					+ "°");

			// 当日最高温度
			TextView textTempHigh = (TextView) findViewById(R.id.textTempHigh);
			textTempHigh
					.setText(sharedPreferences.getString("tempHigh", "25℃"));

			// 当日最低温度
			TextView textTempLow = (TextView) findViewById(R.id.textTempLow);
			textTempLow.setText(sharedPreferences.getString("tempLow", "15℃"));

			// 湿度
			TextView textWetLevel = (TextView) findViewById(R.id.textWetLevel);
			textWetLevel.setText(sharedPreferences.getString("wetLevel",
					"55.55%"));

			// 风向和风速
			TextView textWind = (TextView) findViewById(R.id.textWind);
			textWind.setText(sharedPreferences.getString("windDir", "东北风")
					+ sharedPreferences.getString("windSpeed", "5级"));

			// 发布时间
			TextView textUpdateTime = (TextView) findViewById(R.id.textUpdateTime);
			textUpdateTime.setText("发布时间"
					+ sharedPreferences.getString("postTime", "05:55"));
		} else {
			// 未获取到天气信息
		}
	}

	private void setWeatherLogo(String weatherStr) {
		// weatherStr = "多云转晴"; // Debug
		ImageView imgWeatherOne = (ImageView) findViewById(R.id.imgWeatherOne);
		ImageView imgWeatherTwo = (ImageView) findViewById(R.id.imgWeatherTwo);
		if (weatherStr.contains("转")) {
			String strWeatherOne = weatherStr.split("转")[0];
			String strWeatherTwo = weatherStr.split("转")[1];
			imgWeatherOne
					.setBackgroundResource(getSingleWeatherDrawable(strWeatherOne));
			imgWeatherTwo.setVisibility(View.VISIBLE);
			imgWeatherTwo
					.setBackgroundResource(getSingleWeatherDrawable(strWeatherTwo));

		} else {
			imgWeatherOne
					.setBackgroundResource(getSingleWeatherDrawable(weatherStr));
			imgWeatherTwo.setVisibility(View.GONE);
		}
	}

	private int getSingleWeatherDrawable(String weather) {
		if (weather.equals("晴")) {
			return R.drawable.weather_00;
		} else if (weather.equals("多云")) {
			return R.drawable.weather_01;
		} else if (weather.equals("晴")) {
			return R.drawable.weather_00;
		} else if (weather.equals("晴")) {
			return R.drawable.weather_00;
		} else if (weather.equals("晴")) {
			return R.drawable.weather_00;
		} else if (weather.equals("晴")) {
			return R.drawable.weather_00;
		} else if (weather.equals("晴")) {
			return R.drawable.weather_00;
		} else if (weather.equals("晴")) {
			return R.drawable.weather_00;
		} else if (weather.equals("晴")) {
			return R.drawable.weather_00;
		} else if (weather.equals("晴")) {
			return R.drawable.weather_00;
		} else if (weather.equals("晴")) {
			return R.drawable.weather_00;
		} else if (weather.equals("晴")) {
			return R.drawable.weather_00;
		} else if (weather.equals("晴")) {
			return R.drawable.weather_00;
		}
		return R.drawable.weather_00;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// btnLogo.setBackgroundResource(getLogo(getBrand()));
	}
}
