package com.tchip.carlauncher;

import java.util.Calendar;
import java.util.Date;

import com.tchip.carlauncher.service.LocationService;
import com.tchip.carlauncher.service.SpeakService;
import com.tchip.carlauncher.view.ButtonFloat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

		setContentView(R.layout.activity_main);
		// startSpeak("欢迎使用天启行车记录仪");
		updateLayout();
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
		textWeek.setText("星期" + weekStr + " · 第" + weekRank + "周");
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

		// 多媒体
		ImageView imgMultimedia = (ImageView) findViewById(R.id.imgMultimedia);
		imgMultimedia.setOnClickListener(new MyOnClickListener());

		// 副界面
		ButtonFloat btnToVice = (ButtonFloat) findViewById(R.id.btnToVice);
		btnToVice.setDrawableIcon(getResources().getDrawable(
				R.drawable.icon_arrow_right));
		btnToVice.setOnClickListener(new MyOnClickListener());

		// 蓝牙拨号
		ImageView imgBluetooth = (ImageView) findViewById(R.id.imgBluetooth);
		imgBluetooth.setOnClickListener(new MyOnClickListener());
	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			int version = Integer.valueOf(android.os.Build.VERSION.SDK);
			switch (v.getId()) {
			case R.id.imgMultimedia:
				Intent intent1 = new Intent(MainActivity.this,
						MultimediaActivity.class);
				startActivity(intent1);
				// add for animation start

				if (version > 5) {
					overridePendingTransition(R.anim.zms_translate_down_out,
							R.anim.zms_translate_down_in);
				}
				// add for animation end
				break;
			case R.id.btnToVice:
				Intent intent2 = new Intent(MainActivity.this,
						ViceActivity.class);
				startActivity(intent2);
				// add for animation start
				if (version > 5) {
					overridePendingTransition(R.anim.zms_translate_left_out,
							R.anim.zms_translate_left_in);
				}
				// add for animation end
				break;
			case R.id.imgBluetooth:
				Intent intent3 = new Intent(MainActivity.this,
						BluetoothActivity.class);
				startActivity(intent3);

			}
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
		if (weather.equals("晴")) {
			return isDay() ? (R.drawable.weather_sun)
					: (R.drawable.weather_night);
		} else if (weather.equals("多云")) {
			return isDay() ? R.drawable.weather_sun_cloud_day
					: R.drawable.weather_sun_cloud_night;
		} else if (weather.equals("阴")) {
			return R.drawable.weather_cloud;
		} else if (weather.equals("阵雨") || weather.equals("雷阵雨")
				|| weather.equals("小雨") || weather.equals("中雨")
				|| weather.equals("大雨") || weather.equals("暴雨")
				|| weather.equals("大暴雨") || weather.equals("特大暴雨")
				|| weather.equals("小到中雨") || weather.equals("中到大雨")
				|| weather.equals("大到暴雨") || weather.equals("暴雨到大暴雨")
				|| weather.equals("大暴雨到特大暴雨")) {
			return R.drawable.weather_rain;
		} else if (weather.equals("阵雪") || weather.equals("小雪")
				|| weather.equals("中雪") || weather.equals("大雪")
				|| weather.equals("暴雪") || weather.equals("小到中雪")
				|| weather.equals("中到大雪") || weather.equals("大到暴雪")) {
			return R.drawable.weather_snow;
		} else if (weather.equals("雷阵雨伴有冰雹")) {
			return R.drawable.weather_hail;
		} else if (weather.equals("雨夹雪")) {
			return R.drawable.weather_rain_snow;
		} else if (weather.equals("雾") || weather.equals("霾")
				|| weather.equals("浮尘")) {
			return isDay() ? (R.drawable.weather_fog_day)
					: (R.drawable.weather_fog_night);
		} else if (weather.equals("冻雨")) {
			return R.drawable.weather_sun;
		} else if (weather.equals("沙尘暴") || weather.equals("强沙尘暴")
				|| weather.equals("扬沙")) {
			return R.drawable.weather_dust;
		}
		return isDay() ? (R.drawable.weather_sun) : (R.drawable.weather_night);
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
		// btnLogo.setBackgroundResource(getLogo(getBrand()));
	}
}
