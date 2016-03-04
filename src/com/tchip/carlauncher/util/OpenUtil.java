package com.tchip.carlauncher.util;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.ui.activity.MultimediaActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

public class OpenUtil {

	public enum MODULE_TYPE {
		/** 语音助手 */
		CHAT,

		/** 云中心 */
		CLOUD_CENTER,

		/** 云中心-拨号 */
		CLOUD_DIALER,

		/** 云中心-一键接人 */
		CLOUD_PICK,

		/** 设备测试 */
		DEVICE_TEST,

		/** 拨号 */
		DIALER,

		/** 电子狗 */
		EDOG,

		/** 工程模式 */
		ENGINEER_MODE,

		/** 文件管理 */
		FILE_EXPLORER,

		/** 文件管理(MTK) */
		FILE_MANAGER_MTK,

		/** FM发射 */
		FMTRANSMIT,

		/** 图库 */
		GALLERY,

		/** 短信 */
		MMS,

		/** 多媒体 */
		MULTIMEDIA,

		/** 在线音乐 */
		MUSIC,

		/** 导航:百度SDK */
		NAVI_BAIDU_SDK,

		/** 导航:高德地图 */
		NAVI_GAODE,

		/** 导航:高德地图车机版 */
		NAVI_GAODE_CAR,

		/** 导航：图吧 */
		NAVI_TUBA,

		/** 轨迹 */
		ROUTE,

		/** 设置 */
		SETTING,

		/** 关于 */
		SETTING_ABOUT,

		/** 应用 */
		SETTING_APP,

		/** 流量使用情况 */
		SETTING_DATA_USAGE,

		/** 日期和时间 */
		SETTING_DATE,

		/** 显示设置 */
		SETTING_DISPLAY,

		/** FM发射设置 */
		SETTING_FM,

		/** 位置 */
		SETTING_LOCATION,

		/** 音量设置 */
		SETTING_VOLUME,

		/** 备份和重置 */
		SETTING_RESET,

		/** 存储设置 */
		SETTING_STORAGE,

		/** 系统设置 */
		SETTING_SYSTEM,

		/** 用户中心 */
		SETTING_USER_CENTER,

		/** 视频 */
		VIDEO,

		/** 天气 */
		WEATHER,

		/** 微信助手 */
		WECHAT,

		/** 微密 */
		WEME,

		/** Wi-Fi */
		WIFI,

		/** Wi-Fi热点 */
		WIFI_AP,

		/** 喜马拉雅 */
		XIMALAYA
	}

	public static void openModule(Activity activity, MODULE_TYPE moduleTye) {
		switch (moduleTye) {
		case CHAT:
			break;

		case CLOUD_CENTER:
			try {
				activity.sendBroadcast(new Intent(
						"tchip.intent.action.ACTION_GPS_ON")); // 打开GPS
				Intent intentCloudCenter = new Intent(Intent.ACTION_VIEW);
				intentCloudCenter
						.setClassName("com.hdsc.monitor.heart.monitorvoice",
								"com.hdsc.monitor.heart.monitorvoice.CloudCenterActivity");
				intentCloudCenter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activity.startActivity(intentCloudCenter);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case CLOUD_DIALER:
			try {
				activity.sendBroadcast(new Intent(
						"tchip.intent.action.ACTION_GPS_ON")); // 打开GPS
				Intent intentCloudDialer = new Intent(Intent.ACTION_VIEW);
				intentCloudDialer.setClassName(
						"com.hdsc.monitor.heart.monitorvoice",
						"com.hdsc.monitor.heart.monitorvoice.MainActivity");
				intentCloudDialer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activity.startActivity(intentCloudDialer);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case CLOUD_PICK:
			try {
				activity.sendBroadcast(new Intent(
						"tchip.intent.action.ACTION_GPS_ON")); // 打开GPS
				Intent intenCloudPick = new Intent(Intent.ACTION_VIEW);
				intenCloudPick.setClassName(
						"com.hdsc.monitor.heart.monitorvoice",
						"com.hdsc.monitor.heart.monitorvoice.JJJRActivity");
				intenCloudPick.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activity.startActivity(intenCloudPick);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case DEVICE_TEST:
			try {
				Intent intentDeviceTest = new Intent(Intent.ACTION_VIEW);
				intentDeviceTest.setClassName("com.DeviceTest",
						"com.DeviceTest.DeviceTest");
				intentDeviceTest.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activity.startActivity(intentDeviceTest);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case DIALER:
			try {
				ComponentName componentDialer = new ComponentName(
						"com.goodocom.gocsdk", "com.tchip.call.MainActivity");
				Intent intentDialer = new Intent();
				intentDialer.setComponent(componentDialer);
				activity.startActivity(intentDialer);
				activity.overridePendingTransition(R.anim.zms_translate_up_out,
						R.anim.zms_translate_up_in);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case ENGINEER_MODE:
			try {
				Intent intentEngineerMode = new Intent(Intent.ACTION_VIEW);
				intentEngineerMode.setClassName("com.mediatek.engineermode",
						"com.mediatek.engineermode.EngineerMode");
				intentEngineerMode.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activity.startActivity(intentEngineerMode);
			} catch (Exception e) {

			}
			break;

		case EDOG:
			if (!ClickUtil.isQuickClick(800)) {
				activity.sendBroadcast(new Intent(
						"tchip.intent.action.ACTION_GPS_ON")); // 打开GPS

				// SettingUtil.setEDogEnable(true);
				try {
					ComponentName componentEDog = new ComponentName(
							"entry.dsa2014", "entry.dsa2014.MainActivity");
					Intent intentEDog = new Intent();
					intentEDog.setComponent(componentEDog);
					activity.startActivity(intentEDog);
				} catch (Exception e) {
					e.printStackTrace();
				}
				activity.overridePendingTransition(R.anim.zms_translate_up_out,
						R.anim.zms_translate_up_in);
			}
			break;

		case FILE_EXPLORER:
			if (!ClickUtil.isQuickClick(800)) {
				try {
					ComponentName componentFile = new ComponentName(
							"com.tchip.filemanager",
							"com.tchip.filemanager.ui.activity.MainActivity");
					Intent intentFile = new Intent();
					intentFile.setComponent(componentFile);
					activity.startActivity(intentFile);
					activity.overridePendingTransition(
							R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		case FILE_MANAGER_MTK:
			if (!ClickUtil.isQuickClick(800)) {
				try {
					ComponentName componentFile = new ComponentName(
							"com.mediatek.filemanager",
							"com.mediatek.filemanager.FileManagerOperationActivity");
					Intent intentFile = new Intent();
					intentFile.setComponent(componentFile);
					activity.startActivity(intentFile);
					activity.overridePendingTransition(
							R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		case FMTRANSMIT:
			if (!ClickUtil.isQuickClick(800)) {
				try {
					ComponentName componentFM = new ComponentName(
							"com.tchip.fmtransmit",
							"com.tchip.fmtransmit.ui.activity.MainActivity");
					Intent intentFM = new Intent();
					intentFM.setComponent(componentFM);
					activity.startActivity(intentFM);
					activity.overridePendingTransition(
							R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		case GALLERY:
			try {
				ComponentName componentImage = new ComponentName(
						"com.android.gallery3d",
						"com.android.gallery3d.app.GalleryActivity");
				Intent intentImage = new Intent();
				intentImage.setComponent(componentImage);
				intentImage.addCategory(Intent.CATEGORY_LAUNCHER);
				intentImage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activity.startActivity(intentImage);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case MMS:
			if (!ClickUtil.isQuickClick(800)) {
				try {
					ComponentName componentMessage = new ComponentName(
							"com.android.mms",
							"com.android.mms.ui.BootActivity");
					Intent intentMessage = new Intent();
					intentMessage.setComponent(componentMessage);
					activity.startActivity(intentMessage);
					activity.overridePendingTransition(
							R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		case MULTIMEDIA:
			if (!ClickUtil.isQuickClick(800)) {
				Intent intentMultimedia = new Intent(activity,
						MultimediaActivity.class);
				activity.startActivity(intentMultimedia);
				activity.overridePendingTransition(R.anim.zms_translate_up_out,
						R.anim.zms_translate_up_in);
			}
			break;

		case MUSIC:
			if (!ClickUtil.isQuickClick(800)) {
				try {
					ComponentName componentMusic;
					// 普通HD版："cn.kuwo.kwmusichd","cn.kuwo.kwmusichd.WelcomeActivity"
					// 车载HD版："cn.kuwo.kwmusiccar","cn.kuwo.kwmusiccar.WelcomeActivity"
					componentMusic = new ComponentName("cn.kuwo.kwmusiccar",
							"cn.kuwo.kwmusiccar.WelcomeActivity");
					Intent intentMusic = new Intent();
					intentMusic.setComponent(componentMusic);
					activity.startActivity(intentMusic);
					activity.overridePendingTransition(
							R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		case NAVI_BAIDU_SDK:
			if (!ClickUtil.isQuickClick(800)) {
				if (NetworkUtil.isNetworkConnected(activity)) {
					// 打开GPS
					activity.sendBroadcast(new Intent(
							"tchip.intent.action.ACTION_GPS_ON"));

					try {
						ComponentName componentBaiduNavi;
						componentBaiduNavi = new ComponentName(
								"com.tchip.baidunavi",
								"com.tchip.baidunavi.ui.activity.MainActivity");
						Intent intentBaiduNavi = new Intent();
						intentBaiduNavi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intentBaiduNavi.setComponent(componentBaiduNavi);
						activity.startActivity(intentBaiduNavi);
						activity.overridePendingTransition(
								R.anim.zms_translate_up_out,
								R.anim.zms_translate_up_in);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					NetworkUtil.noNetworkHint(activity);
				}
			}
			break;

		case NAVI_GAODE:
			if (!ClickUtil.isQuickClick(800)) {
				// 打开GPS
				activity.sendBroadcast(new Intent(
						"tchip.intent.action.ACTION_GPS_ON"));
				try {
					ComponentName componentGaode;
					componentGaode = new ComponentName("com.autonavi.minimap",
							"com.autonavi.map.activity.SplashActivity");
					Intent intentGaode = new Intent();
					intentGaode.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intentGaode.setComponent(componentGaode);
					activity.startActivity(intentGaode);
					activity.overridePendingTransition(
							R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		case NAVI_GAODE_CAR:
			if (!ClickUtil.isQuickClick(800)) {
				// 打开GPS
				activity.sendBroadcast(new Intent(
						"tchip.intent.action.ACTION_GPS_ON"));
				try {
					ComponentName componentGaode;
					componentGaode = new ComponentName("com.autonavi.amapauto",
							"com.autonavi.auto.MainMapActivity");
					Intent intentGaode = new Intent();
					intentGaode.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intentGaode.setComponent(componentGaode);
					activity.startActivity(intentGaode);
					activity.overridePendingTransition(
							R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		case NAVI_TUBA:
			if (!ClickUtil.isQuickClick(800)) {
				// 打开GPS
				activity.sendBroadcast(new Intent(
						"tchip.intent.action.ACTION_GPS_ON"));
				try {
					ComponentName componentTuba;
					componentTuba = new ComponentName(
							"com.mapbar.android.carnavi",
							"com.mapbar.android.carnavi.activity.LoadingActivity");
					Intent intentTubaNavi = new Intent();
					intentTubaNavi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intentTubaNavi.setComponent(componentTuba);
					activity.startActivity(intentTubaNavi);
					activity.overridePendingTransition(
							R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		case ROUTE:
			if (!ClickUtil.isQuickClick(800)) {
				try {
					ComponentName componentDialer = new ComponentName(
							"com.tchip.route",
							"com.tchip.route.ui.activity.MainActivity");
					Intent intentDialer = new Intent();
					intentDialer.setComponent(componentDialer);
					activity.startActivity(intentDialer);
					activity.overridePendingTransition(
							R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		case SETTING:
			if (!ClickUtil.isQuickClick(800)) {
				ComponentName componentDialer = new ComponentName(
						"com.tchip.carsetting",
						"com.tchip.carsetting.ui.activity.MainActivity");
				Intent intentDialer = new Intent();
				intentDialer.setComponent(componentDialer);
				activity.startActivity(intentDialer);
			}
			break;

		case SETTING_ABOUT:
			try {
				activity.startActivity(new Intent(
						android.provider.Settings.ACTION_DEVICE_INFO_SETTINGS));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case SETTING_APP:
			try {
				activity.startActivity(new Intent(
						android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case SETTING_DATA_USAGE:
			try {
				activity.startActivity(new Intent(
						"android.settings.DATA_USAGE_SETTINGS"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case SETTING_DATE:
			try {
				activity.startActivity(new Intent(
						android.provider.Settings.ACTION_DATE_SETTINGS));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case SETTING_FM:
			try {
				activity.startActivity(new Intent(
						"android.settings.FM_SETTINGS"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case SETTING_LOCATION:
			try {
				activity.startActivity(new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case SETTING_RESET:
			try {
				activity.startActivity(new Intent(
						"android.settings.BACKUP_AND_RESET_SETTINGS"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case SETTING_STORAGE:
			try {
				activity.startActivity(new Intent(
						android.provider.Settings.ACTION_MEMORY_CARD_SETTINGS));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case SETTING_SYSTEM:
			try {
				ComponentName componentSetting = new ComponentName(
						"com.android.settings", "com.android.settings.Settings");
				Intent intentSetting = new Intent();
				intentSetting.setComponent(componentSetting);
				activity.startActivity(intentSetting);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case SETTING_USER_CENTER:
			if (!ClickUtil.isQuickClick(800)) {
				ComponentName componentDialer = new ComponentName(
						"com.tchip.usercenter",
						"com.tchip.usercenter.ui.activity.MainActivity");
				Intent intentDialer = new Intent();
				intentDialer.setComponent(componentDialer);
				activity.startActivity(intentDialer);
			}
			break;

		case VIDEO:
			try {
				ComponentName componentVideo = new ComponentName(
						"com.mediatek.videoplayer",
						"com.mediatek.videoplayer.MovieListActivity");
				Intent intentVideo = new Intent();
				intentVideo.setComponent(componentVideo);
				intentVideo.addCategory(Intent.CATEGORY_DEFAULT);
				intentVideo.addCategory(Intent.CATEGORY_LAUNCHER);
				intentVideo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				activity.startActivity(intentVideo);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case WEATHER:
			if (!ClickUtil.isQuickClick(800)) {
				try {
					ComponentName componentWeather;
					componentWeather = new ComponentName("com.tchip.weather",
							"com.tchip.weather.ui.activity.MainActivity");
					Intent intentWeather = new Intent();
					intentWeather.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intentWeather.setComponent(componentWeather);
					activity.startActivity(intentWeather);
					activity.overridePendingTransition(
							R.anim.zms_translate_down_out,
							R.anim.zms_translate_down_in);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		case WECHAT:
			if (!ClickUtil.isQuickClick(800)) {
				try {
					ComponentName componentWechat;
					componentWechat = new ComponentName("com.txznet.webchat",
							"com.txznet.webchat.ui.AppStartActivity");
					Intent intentWechat = new Intent();
					intentWechat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intentWechat.setComponent(componentWechat);
					activity.startActivity(intentWechat);
					activity.overridePendingTransition(
							R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		case WEME:
			try {
				activity.sendBroadcast(new Intent(
						"tchip.intent.action.ACTION_GPS_ON")); // 打开GPS

				Intent intent = new Intent();
				ComponentName comp = new ComponentName("com.mirrtalk.app",
						"com.mirrtalk.app.MtLoginActivity");
				intent.setComponent(comp);
				intent.setAction("android.intent.action.VIEW");
				activity.startActivity(intent);
				activity.overridePendingTransition(R.anim.zms_translate_up_out,
						R.anim.zms_translate_up_in);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case WIFI:
			if (!ClickUtil.isQuickClick(800)) {
				try {
					activity.startActivity(new Intent(
							android.provider.Settings.ACTION_WIFI_SETTINGS));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		case WIFI_AP:
			try {
				activity.startActivity(new Intent(
						"android.settings.TETHER_WIFI_SETTINGS"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case XIMALAYA:
			try {
				Intent intent = new Intent();
				// ComponentName comp = new ComponentName(
				// "com.ximalaya.ting.android",
				// "com.ximalaya.ting.android.activity.login.WelcomeActivity");
				ComponentName comp = new ComponentName(
						"com.ximalaya.ting.android.car",
						"com.ximalaya.ting.android.car.activity.WelcomeActivity");
				intent.setComponent(comp);
				intent.setAction("android.intent.action.VIEW");
				activity.startActivity(intent);
				activity.overridePendingTransition(R.anim.zms_translate_up_out,
						R.anim.zms_translate_up_in);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}

}
