package com.tchip.carlauncher;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.baidu.mapapi.SDKInitializer;
import com.iflytek.cloud.SpeechUtility;
import com.tchip.carlauncher.util.MyLog;

public class MyApplication extends Application {
	/**
	 * 是否进入低功耗待机状态
	 */
	public static boolean isSleeping = false;

	/**
	 * 休眠唤醒，需要启动录像
	 */
	public static boolean shouldWakeRecord = false;

	/**
	 * 底层碰撞，需要启动录像
	 */
	public static boolean shouldCrashRecord = false;

	/**
	 * 录制底层碰撞视频后是否需要停止录像
	 */
	public static boolean shouldStopWhenCrashVideoSave = false;

	// Music
	// public static boolean mIsSleepClockSetting = false;
	// private static String rootPath = "/mymusic";
	// public static String lrcPath = "/lrc";
	// public static String nowPlayMusic = "";

	// Route Record
	public static boolean isRouteRecord = false;

	// Video Record
	public static boolean isVideoReording = false;
	public static boolean isPowerConnect = true;
	public static boolean isVideoLock = false;
	// 第二段视频加锁
	public static boolean isVideoLockSecond = false;
	public static boolean isCrashed = false;

	// 存储更改视频分辨率前的录像状态
	public static boolean shouldVideoRecordWhenChangeSize = false;

	public static boolean shouldResetRecordWhenResume = false;

	public static boolean isFirstLaunch = true;

	// 录像界面是否可见
	public static boolean isMainForeground = true;

	/**
	 * SD卡取出
	 */
	public static boolean isVideoCardEject = false;

	/**
	 * 碰撞侦测开关和级别
	 */
	public static boolean isCrashOn = Constant.GravitySensor.DEFAULT_ON; // 默认打开
	public static int crashSensitive = Constant.GravitySensor.SENSITIVE_DEFAULT;
	private SharedPreferences sharedPreferences;

	@Override
	public void onCreate() {

		initialCrashData();

		// 应用程序入口处调用,避免手机内存过小,杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
		// 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
		// 参数间使用“,”分隔。
		try {
			SpeechUtility
					.createUtility(this, "appid=" + Constant.XUNFEI_APP_ID);
		} catch (Exception e) {
			MyLog.e("[MyApplication]SpeechUtility.createUtility: Catch Exception!");
		}
		super.onCreate();

		/*
		 * 百度地图SDK初始化
		 * 
		 * 初始化全局 context，指定 sdcard 路径，若采用默认路径，请使用initialize(Context context)
		 * 重载函数 参数:
		 * 
		 * sdcardPath - sd 卡路径，请确保该路径能正常使用 context - 必须是 application context，SDK
		 * 各组件中需要用到。
		 */
		// if (isMapSDExists()) {
		// SDKInitializer.initialize(Constant.Path.SD_CARD_MAP,
		// getApplicationContext());
		// } else {
		try {
			SDKInitializer.initialize(getApplicationContext());
		} catch (Exception e) {
			MyLog.e("[MyApplication]SDKInitializer.initialize: Catch Exception!");
		}
		// }
	}

	/**
	 * 初始化碰撞数据
	 */
	private void initialCrashData() {
		try {
			sharedPreferences = getSharedPreferences(
					Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

			isCrashOn = sharedPreferences.getBoolean("crashOn",
					Constant.GravitySensor.DEFAULT_ON);
			crashSensitive = sharedPreferences.getInt("crashSensitive",
					Constant.GravitySensor.SENSITIVE_DEFAULT);
		} catch (Exception e) {
			MyLog.e("[MyApplication]initialCrashData: Catch Exception!"
					+ e.getMessage());
		}
	}

}
