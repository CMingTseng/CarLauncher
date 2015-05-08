package com.tchip.carlauncher.util;

import java.util.Date;

import android.content.Context;
import android.widget.FrameLayout;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.view.DynamicWeatherCloudyView;

public class WeatherUtil {

	public static enum WEATHER_TYPE {
		CLOUD, SUN, RAIN, SNOW, FOG, RAIN_SNOW, HAIL
	}

	/**
	 * 多云动画
	 */
	public static void cloudAnimation(Context context, FrameLayout flLayout) {

		DynamicWeatherCloudyView view1 = new DynamicWeatherCloudyView(context,
				R.drawable.weather_cloud_1, -150, 40, 30);
		DynamicWeatherCloudyView view2 = new DynamicWeatherCloudyView(context,
				R.drawable.weather_cloud_2, 280, 100, 60);
		DynamicWeatherCloudyView view3 = new DynamicWeatherCloudyView(context,
				R.drawable.weather_cloud_3, 140, 130, 40);
		DynamicWeatherCloudyView view4 = new DynamicWeatherCloudyView(context,
				R.drawable.weather_cloud_4, 0, 60, 40);

		flLayout.addView(view1);
		flLayout.addView(view2);
		flLayout.addView(view3);
		flLayout.addView(view4);
		view1.move();
		view2.move();
		view3.move();
		view4.move();
	}

	public static WEATHER_TYPE getTypeByStr(String weather) {

		if (weather.equals("晴")) {
			return WEATHER_TYPE.SUN;
		} else if (weather.equals("多云") || weather.equals("阴")) {
			return WEATHER_TYPE.CLOUD;
		} else if (weather.equals("阵雨") || weather.equals("雷阵雨")
				|| weather.equals("小雨") || weather.equals("中雨")
				|| weather.equals("大雨") || weather.equals("暴雨")
				|| weather.equals("大暴雨") || weather.equals("特大暴雨")
				|| weather.equals("小到中雨") || weather.equals("中到大雨")
				|| weather.equals("大到暴雨") || weather.equals("暴雨到大暴雨")
				|| weather.equals("大暴雨到特大暴雨") || weather.equals("多云转雨")
				|| weather.equals("多云转大雨") || weather.equals("雨转多云")) {
			return WEATHER_TYPE.RAIN;
		} else if (weather.equals("阵雪") || weather.equals("小雪")
				|| weather.equals("中雪") || weather.equals("大雪")
				|| weather.equals("暴雪") || weather.equals("小到中雪")
				|| weather.equals("中到大雪") || weather.equals("大到暴雪")
				|| weather.equals("多云转雪") || weather.equals("多云转大雪")
				|| weather.equals("雪转多云") || weather.equals("晴转雪")
				|| weather.equals("晴转大雪") || weather.equals("雪转晴")) {
			return WEATHER_TYPE.SNOW;
		} else if (weather.equals("雷阵雨伴有冰雹") || weather.equals("冻雨")) {
			return WEATHER_TYPE.HAIL;
		} else if (weather.equals("雨夹雪")) {
			return WEATHER_TYPE.RAIN_SNOW;
		} else if (weather.equals("雾") || weather.equals("霾")
				|| weather.equals("浮尘") || weather.equals("沙尘暴")
				|| weather.equals("强沙尘暴") || weather.equals("扬沙")) {
			return WEATHER_TYPE.FOG;
		} else
			return WEATHER_TYPE.SUN;
	}

	public static int getWeatherBackground(WEATHER_TYPE type) {

		switch (type) {
		case SUN:
			return isDay() ? (R.drawable.icon_weather_sun_white)
					: (R.drawable.icon_weather_bg_sun_night);
		case CLOUD:
			return isDay() ? R.drawable.icon_weather_bg_cloud_day
					: R.drawable.icon_weather_bg_cloud_night;
		case RAIN:
			return R.drawable.icon_weather_bg_rain;
		case SNOW:
			return R.drawable.icon_weather_snow_white;
		case HAIL:
			return R.drawable.icon_weather_hail_white;
		case RAIN_SNOW:
			return R.drawable.icon_weather_rain_snow_white;
		case FOG:
			return isDay() ? (R.drawable.weather_fog_day)
					: (R.drawable.weather_fog_night);
		default:
			return isDay() ? (R.drawable.icon_weather_bg_default_day)
					: (R.drawable.icon_weather_bg_default_night);
		}
	}

	public static int getWeatherDrawable(WEATHER_TYPE type) {

		switch (type) {
		case SUN:
			return isDay() ? (R.drawable.icon_weather_sun_white)
					: (R.drawable.icon_weather_moon_white);
		case CLOUD:
			return isDay() ? R.drawable.icon_weather_cloud_day_white
					: R.drawable.icon_weather_cloud_night_white;
		case RAIN:
			return R.drawable.icon_weather_rain_white;
		case SNOW:
			return R.drawable.icon_weather_snow_white;
		case HAIL:
			return R.drawable.icon_weather_hail_white;
		case RAIN_SNOW:
			return R.drawable.icon_weather_rain_snow_white;
		case FOG:
			return isDay() ? (R.drawable.weather_fog_day)
					: (R.drawable.weather_fog_night);
		default:
			return R.drawable.icon_weather_na;
		}
	}

	public static boolean isDay() {
		Date date = new Date();
		int hour = date.getHours();
		if (hour > 18 || hour < 6)
			return false;
		return true;
	}

}
