package com.tchip.carlauncher.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.R.anim;
import com.tchip.carlauncher.R.drawable;
import com.tchip.carlauncher.R.id;
import com.tchip.carlauncher.R.layout;
import com.tchip.carlauncher.service.LocationService;
import com.tchip.carlauncher.service.SpeakService;
import com.tchip.carlauncher.service.WeatherService;
import com.tchip.carlauncher.view.MyViewPager;
import com.tchip.carlauncher.view.MyViewPager.TransitionEffect;
import com.tchip.carlauncher.view.MyViewPagerContainer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class Main extends Activity {
	private View viewMain, viewVice;
	private List<View> viewList;
	private MyViewPager viewPager; // viewpager
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		sharedPreferences = getSharedPreferences("CarLauncher",
				getApplicationContext().MODE_PRIVATE);

		LayoutInflater inflater = getLayoutInflater().from(this);
		viewMain = inflater.inflate(R.layout.activity_main, null);
		viewVice = inflater.inflate(R.layout.activity_vice, null);

		viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
		viewList.add(viewMain);
		viewList.add(viewVice);
		viewPager = (MyViewPager) findViewById(R.id.viewpager);
		viewPager.setTransitionEffect(TransitionEffect.CubeOut);

		viewPager.setPageMargin(10);
		viewPager.setAdapter(pagerAdapter);
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

	private void startLocationService() {
		Intent intent = new Intent(this, LocationService.class);
		startService(intent);
	}

	private void updateMainLayout() {
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
		// ButtonFloat btnToVice = (ButtonFloat) findViewById(R.id.btnToVice);
		// btnToVice.setDrawableIcon(getResources().getDrawable(
		// R.drawable.icon_arrow_right));
		// btnToVice.setOnClickListener(new MyOnClickListener());

		// 蓝牙拨号
		ImageView imgBluetooth = (ImageView) findViewById(R.id.imgBluetooth);
		imgBluetooth.setOnClickListener(new MyOnClickListener());
	}

	private void updateViceLayout() {
		// ButtonFloat btnToMainFromVice = (ButtonFloat)
		// findViewById(R.id.btnToMainFromVice);
		// btnToMainFromVice.setDrawableIcon(getResources().getDrawable(
		// R.drawable.icon_arrow_left));
		// btnToMainFromVice.setOnClickListener(new MyOnClickListener());

		ImageView imgNear = (ImageView) findViewById(R.id.imgNear);
		imgNear.setOnClickListener(new MyOnClickListener());

		ImageView imgRoutePlan = (ImageView) findViewById(R.id.imgRoutePlan);
		imgRoutePlan.setOnClickListener(new MyOnClickListener());

		ImageView imgAbout = (ImageView) findViewById(R.id.imgAbout);
		imgAbout.setOnClickListener(new MyOnClickListener());

		ImageView imgChat = (ImageView) findViewById(R.id.imgChat);
		imgChat.setOnClickListener(new MyOnClickListener());

	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			int version = Integer.valueOf(android.os.Build.VERSION.SDK);
			switch (v.getId()) {
			case R.id.imgMultimedia:
				Intent intent11 = new Intent(Main.this,
						MultimediaActivity.class);
				startActivity(intent11);
				// add for animation start

				if (version > 5) {
					overridePendingTransition(R.anim.zms_translate_down_out,
							R.anim.zms_translate_down_in);
				}
				// add for animation end
				break;
			case R.id.btnToVice:
				Intent intent12 = new Intent(Main.this, ViceActivity.class);
				startActivity(intent12);
				// add for animation start
				if (version > 5) {
					overridePendingTransition(R.anim.zms_translate_left_out,
							R.anim.zms_translate_left_in);
				}
				// add for animation end
				break;
			case R.id.imgBluetooth:
				Intent intent13 = new Intent(Main.this, BluetoothActivity.class);
				startActivity(intent13);

				break;
			case R.id.imgNear:
				Intent intent21 = new Intent(Main.this, NearActivity.class);
				startActivity(intent21);
				// add for animation start

				if (version > 5) {
					overridePendingTransition(R.anim.zms_translate_down_out,
							R.anim.zms_translate_down_in);
				}
				// add for animation end
				break;
			case R.id.imgRoutePlan:
				Intent intent22 = new Intent(Main.this, RoutePlanActivity.class);
				startActivity(intent22);
				// add for animation start

				if (version > 5) {
					overridePendingTransition(R.anim.zms_translate_down_out,
							R.anim.zms_translate_down_in);
				}
				// add for animation end
				break;
			case R.id.imgAbout:
				Intent intent23 = new Intent(Main.this, AboutActivity.class);
				startActivity(intent23);

				if (version > 5) {
					overridePendingTransition(R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				}
				break;
			case R.id.imgChat:
				Intent intent24 = new Intent(Main.this, ChatActivity.class);
				startActivity(intent24);

				if (version > 5) {
					overridePendingTransition(R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				}
				break;
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
		} else if (weather.equals("雷阵雨伴有冰雹") || weather.equals("冻雨")) {
			return R.drawable.weather_hail;
		} else if (weather.equals("雨夹雪")) {
			return R.drawable.weather_rain_snow;
		} else if (weather.equals("雾") || weather.equals("霾")
				|| weather.equals("浮尘")) {
			return isDay() ? (R.drawable.weather_fog_day)
					: (R.drawable.weather_fog_night);
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

		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
	}

}
