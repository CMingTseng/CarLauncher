package com.tchip.carlauncher.util;

import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.view.WeatherDynamicCloudyView;
import com.tchip.carlauncher.view.WeatherDynamicRainView;

public class WeatherUtil {

	public static enum WEATHER_TYPE {
		CLOUD, SUN, RAIN, SNOW, FOG, RAIN_SNOW, HAIL
	}

	/**
	 * 多云动画
	 */
	public static void cloudAnimation(Context context, FrameLayout flLayout) {
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.weather_cloud_1);
		flLayout.removeAllViews();
		WeatherDynamicCloudyView view1 = new WeatherDynamicCloudyView(context,
				bitmap, -150, 50, 30);
		flLayout.addView(view1);
		view1.move();

		WeatherDynamicCloudyView view2 = new WeatherDynamicCloudyView(context,
				bitmap, 280, 150, 60);
		flLayout.addView(view2);
		view2.move();

		WeatherDynamicCloudyView view3 = new WeatherDynamicCloudyView(context,
				bitmap, 140, 220, 40);
		flLayout.addView(view3);
		view3.move();
	}

	/**
	 * 下雨动画
	 */
	public static void rainAnimation(Context context, FrameLayout flLayout) {
		flLayout.removeAllViews();

		DisplayMetrics dm = new DisplayMetrics();
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		display.getMetrics(dm);
		int screenMax = Math.max(dm.widthPixels, dm.heightPixels);

		int rainSpanX = screenMax / 10;
		int rainSpanY = 150;

		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.weather_rain_drop);
		WeatherDynamicRainView view1 = new WeatherDynamicRainView(context,
				bitmap, 100 + rainSpanX * 0, 50 + rainSpanY * 0, 30);
		flLayout.addView(view1);
		view1.move();

		WeatherDynamicRainView view2 = new WeatherDynamicRainView(context,
				bitmap, 100 + rainSpanX * 1, 50 + rainSpanY * 1, 20);
		flLayout.addView(view2);
		view2.move();

		WeatherDynamicRainView view3 = new WeatherDynamicRainView(context,
				bitmap, 100 + rainSpanX * 2, 50 + rainSpanY * 3, 40);
		flLayout.addView(view3);
		view3.move();

		WeatherDynamicRainView view4 = new WeatherDynamicRainView(context,
				bitmap, 100 + rainSpanX * 3, 50 + rainSpanY * 2, 10);
		flLayout.addView(view4);
		view4.move();

		WeatherDynamicRainView view5 = new WeatherDynamicRainView(context,
				bitmap, 100 + rainSpanX * 4, 50 + rainSpanY * 1, 30);
		flLayout.addView(view5);
		view5.move();

		WeatherDynamicRainView view6 = new WeatherDynamicRainView(context,
				bitmap, 100 + rainSpanX * 5, 50 + rainSpanY * 2, 20);
		flLayout.addView(view6);
		view6.move();

		WeatherDynamicRainView view7 = new WeatherDynamicRainView(context,
				bitmap, 100 + rainSpanX * 6, 50 + rainSpanY * 0, 40);
		flLayout.addView(view7);
		view7.move();

		WeatherDynamicRainView view8 = new WeatherDynamicRainView(context,
				bitmap, 100 + rainSpanX * 7, 50 + rainSpanY * 1, 30);
		flLayout.addView(view8);
		view8.move();

		WeatherDynamicRainView view9 = new WeatherDynamicRainView(context,
				bitmap, 100 + rainSpanX * 8, 50 + rainSpanY * 2, 20);
		flLayout.addView(view9);
		view9.move();

	}

	/**
	 * 下雪动画
	 */
	public static void snowAnimation(Context context, FrameLayout flLayout) {
		flLayout.removeAllViews();

		DisplayMetrics dm = new DisplayMetrics();
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		display.getMetrics(dm);
		int screenMax = Math.max(dm.widthPixels, dm.heightPixels);

		int rainSpanX = screenMax / 10;
		int rainSpanY = 150;

		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.weather_snow_flake);
		WeatherDynamicRainView view1 = new WeatherDynamicRainView(context,
				bitmap, 100 + rainSpanX * 0, 50 + rainSpanY * 0, 60);
		flLayout.addView(view1);
		view1.move();

		WeatherDynamicRainView view2 = new WeatherDynamicRainView(context,
				bitmap, 100 + rainSpanX * 1, 50 + rainSpanY * 1, 40);
		flLayout.addView(view2);
		view2.move();

		WeatherDynamicRainView view3 = new WeatherDynamicRainView(context,
				bitmap, 100 + rainSpanX * 2, 50 + rainSpanY * 3, 80);
		flLayout.addView(view3);
		view3.move();

		WeatherDynamicRainView view4 = new WeatherDynamicRainView(context,
				bitmap, 100 + rainSpanX * 3, 50 + rainSpanY * 2, 60);
		flLayout.addView(view4);
		view4.move();

		WeatherDynamicRainView view5 = new WeatherDynamicRainView(context,
				bitmap, 100 + rainSpanX * 4, 50 + rainSpanY * 1, 50);
		flLayout.addView(view5);
		view5.move();

		WeatherDynamicRainView view6 = new WeatherDynamicRainView(context,
				bitmap, 100 + rainSpanX * 5, 50 + rainSpanY * 2, 70);
		flLayout.addView(view6);
		view6.move();

		WeatherDynamicRainView view7 = new WeatherDynamicRainView(context,
				bitmap, 100 + rainSpanX * 6, 50 + rainSpanY * 0, 40);
		flLayout.addView(view7);
		view7.move();

		WeatherDynamicRainView view8 = new WeatherDynamicRainView(context,
				bitmap, 100 + rainSpanX * 7, 50 + rainSpanY * 1, 90);
		flLayout.addView(view8);
		view8.move();

		WeatherDynamicRainView view9 = new WeatherDynamicRainView(context,
				bitmap, 100 + rainSpanX * 8, 50 + rainSpanY * 2, 50);
		flLayout.addView(view9);
		view9.move();

	}

	public static WEATHER_TYPE getTypeByStr(String weather) {

		if (weather.equals("晴")) {
			return WEATHER_TYPE.SUN;
		} else if (weather.equals("多云") || weather.equals("阴")) {
			return WEATHER_TYPE.CLOUD;
		} else if (weather.equals("雨") || weather.equals("阵雨")
				|| weather.equals("雷阵雨") || weather.equals("小雨")
				|| weather.equals("中雨") || weather.equals("大雨")
				|| weather.equals("暴雨") || weather.equals("大暴雨")
				|| weather.equals("特大暴雨") || weather.equals("小到中雨")
				|| weather.equals("中到大雨") || weather.equals("大到暴雨")
				|| weather.equals("暴雨到大暴雨") || weather.equals("大暴雨到特大暴雨")
				|| weather.equals("多云转雨") || weather.equals("多云转大雨")
				|| weather.equals("雨转多云")) {
			return WEATHER_TYPE.RAIN;
		} else if (weather.equals("雪") || weather.equals("阵雪")
				|| weather.equals("小雪") || weather.equals("中雪")
				|| weather.equals("大雪") || weather.equals("暴雪")
				|| weather.equals("小到中雪") || weather.equals("中到大雪")
				|| weather.equals("大到暴雪") || weather.equals("多云转雪")
				|| weather.equals("多云转大雪") || weather.equals("雪转多云")
				|| weather.equals("晴转雪") || weather.equals("晴转大雪")
				|| weather.equals("雪转晴")) {
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
			return isDay() ? (R.drawable.icon_weather_bg_sun_day)
					: (R.drawable.icon_weather_bg_sun_night);
		case CLOUD:
			return isDay() ? R.drawable.icon_weather_bg_cloud_day
					: R.drawable.icon_weather_bg_cloud_night;
		case RAIN:
			return R.drawable.icon_weather_bg_rain;
		case SNOW:
			return R.drawable.icon_weather_bg_snow;
		case HAIL:
			return R.drawable.icon_weather_hail_white;
		case RAIN_SNOW:
			return R.drawable.icon_weather_bg_rain_snow;
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
