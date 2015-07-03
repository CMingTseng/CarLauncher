package com.tchip.carlauncher;

import java.io.File;

import android.app.Application;
import android.os.Environment;

import com.baidu.mapapi.SDKInitializer;
import com.iflytek.cloud.SpeechUtility;
import com.tchip.carlauncher.lib.filemanager.AppPreferences;
import com.tchip.carlauncher.lib.filemanager.FavouritesManager;
import com.tchip.carlauncher.lib.filemanager.FileIconResolver;
import com.tchip.carlauncher.model.TrafficDbManager;
import com.tchip.carlauncher.service.MusicServiceManager;
import com.tchip.carlauncher.service.TrafficFetchService;
import com.tchip.carlauncher.util.TrafficUtils;

public class MyApplication extends Application {
	// Music
	public static boolean mIsSleepClockSetting = false;
	public static MusicServiceManager mServiceManager = null;
	private static String rootPath = "/mymusic";
	public static String lrcPath = "/lrc";
	public static String nowPlayMusic = "";

	@Override
	public void onCreate() {

		// 应用程序入口处调用,避免手机内存过小,杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
		// 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
		// 参数间使用“,”分隔。
		SpeechUtility.createUtility(MyApplication.this, "appid="
				+ Constant.XUNFEI_APP_ID);
		super.onCreate();

		// 百度地图SDK初始化
		SDKInitializer.initialize(getApplicationContext());

		// Music
		mServiceManager = new MusicServiceManager(this);
		initPath();

		// 流量
		TrafficDbManager.getInstance(MyApplication.this).setTrafficTotal(0L);
		TrafficUtils.startRepeatingService(MyApplication.this,
				TrafficUtils.INTERVAL, TrafficFetchService.class, "");
	}

	private void initPath() {
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

	// Video Record
	public static boolean isVideoReording = false;
	public static boolean isPowerConnect = true;
	public static boolean isFirstLaunch = true;
	public static boolean isVideoLock = false;

	// Recent Play Music
	// public void savePlayList(String nowPlayMusic) {
	// SharedPreferences preferences = getSharedPreferences(
	// Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
	// }
}
