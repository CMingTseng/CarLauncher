package com.tchip.carlauncher;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.baidu.mapapi.SDKInitializer;
import com.iflytek.cloud.SpeechUtility;
import com.tchip.carlauncher.lib.filemanager.AppPreferences;
import com.tchip.carlauncher.lib.filemanager.FavouritesManager;
import com.tchip.carlauncher.lib.filemanager.FileIconResolver;
import com.tchip.carlauncher.service.MusicServiceManager;
import com.tchip.carlauncher.ui.activity.UserCenterActivity;
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

	// Music
	public static boolean mIsSleepClockSetting = false;
	public static MusicServiceManager mServiceManager = null;
	private static String rootPath = "/mymusic";
	public static String lrcPath = "/lrc";
	public static String nowPlayMusic = "";

	// Route Record
	public static boolean isRouteRecord = false;

	// Video Record
	public static boolean isVideoReording = false;
	public static boolean isPowerConnect = true;
	public static boolean isVideoLock = false;
	public static boolean isCrashed = false;
	public static boolean shouldVideoRecordWhenChangeSize = false;

	public static boolean shouldResetRecordWhenResume = false;
	public static boolean isFirstLaunch = true;
	public static boolean isMainForeground = true;

	/**
	 * SD卡取出
	 */
	public static boolean isVideoCardEject = false;

	/**
	 * 碰撞侦测开关和级别
	 */
	public static boolean isCrashOn = false;
	public static int crashSensitive = 5;
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

		// Music
		mServiceManager = new MusicServiceManager(this);
		initPath();
	}

	/**
	 * 初始化碰撞数据
	 */
	private void initialCrashData() {
		try {
			sharedPreferences = getSharedPreferences(
					Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

			isCrashOn = sharedPreferences.getBoolean("crashOn", false);
			crashSensitive = sharedPreferences.getInt("crashSensitive", 5);
		} catch (Exception e) {
			MyLog.e("[MyApplication]initialCrashData: Catch Exception!"
					+ e.getMessage());
		}
	}

	private void initPath() {
		try {
			String ROOT = "";
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				ROOT = Environment.getExternalStorageDirectory().getPath();
			}
			rootPath = ROOT + rootPath;
			lrcPath = rootPath + lrcPath;
			File lrcFile = new File(lrcPath);
			if (lrcFile.exists()) {
				lrcFile.mkdirs();
			}
		} catch (Exception e) {
			MyLog.e("[MyApplication]initPath: Catch Exception!");
		}
	}

	// File Manager
	AppPreferences appPreferences = null;
	FavouritesManager favouritesManager = null;
	FileIconResolver fileIconResolver = null;

	public AppPreferences getAppPreferences() {
		if (appPreferences == null)
			appPreferences = AppPreferences
					.loadPreferences(getApplicationContext());

		return appPreferences;
	}

	public FavouritesManager getFavouritesManager() {
		if (favouritesManager == null)
			favouritesManager = new FavouritesManager(getApplicationContext());
		return favouritesManager;
	}

	public FileIconResolver getFileIconResolver() {
		if (fileIconResolver == null)
			fileIconResolver = new FileIconResolver(getApplicationContext());
		return fileIconResolver;
	}

}
