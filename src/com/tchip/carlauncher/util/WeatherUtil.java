package com.tchip.carlauncher.util;

import java.util.Date;

import com.tchip.carlauncher.R;

public class WeatherUtil {

	public static int getWeatherBackground(String weather) {
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

	public static int getWeatherDrawable(String weather) {
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

	public static boolean isDay() {
		Date date = new Date();
		int hour = date.getHours();
		if (hour > 18 || hour < 6)
			return false;
		return true;
	}

}
