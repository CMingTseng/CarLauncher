package com.tchip.carlauncher.util;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.service.SpeakService;
import com.tchip.carlauncher.ui.activity.ChatActivity;
import com.tchip.carlauncher.ui.activity.MultimediaActivity;
import com.tchip.carlauncher.ui.activity.SettingActivity;
import com.tchip.carlauncher.ui.activity.WifiListActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OpenUtil {

	public enum MODULE_TYPE {
		/** 导航 **/
		BAIDUNAVI,

		/** 语音助手 **/
		CHAT,

		/** 拨号 **/
		DIALER,

		/** 电子狗 **/
		EDOG,

		/** 文件管理 **/
		FILEEXPLORER,

		/** FM发射 **/
		FMTRANSMIT,

		/** 短信 **/
		MMS,

		/** 多媒体 **/
		MULTIMEDIA,

		/** 在线音乐 **/
		MUSIC,

		/** 轨迹 **/
		ROUTE,

		/** 设置 **/
		SETTING,

		/** 天气 **/
		WEATHER,

		/** 微密 **/
		WEME,

		/** Wi-Fi **/
		WIFI
	}

	public static void openModule(Activity activity, MODULE_TYPE moduleTye) {
		switch (moduleTye) {
		case BAIDUNAVI:
			if (!ClickUtil.isQuickClick(800)) {
				if (NetworkUtil.isNetworkConnected(activity)) {
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

		case CHAT:
			if (!ClickUtil.isQuickClick(800)) {
				Intent intentVoiceChat;
				if (Constant.Module.isVoiceXunfei) {
					// 讯飞语音
					intentVoiceChat = new Intent(activity, ChatActivity.class);
				} else {
				}
				activity.startActivity(intentVoiceChat);
				activity.overridePendingTransition(R.anim.zms_translate_up_out,
						R.anim.zms_translate_up_in);
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

		case EDOG:
			if (!ClickUtil.isQuickClick(800)) {
				if (1 == SettingUtil.getAccStatus()) {
					// 打开GPS
					activity.sendBroadcast(new Intent(
							"tchip.intent.action.ACTION_GPS_ON"));

					SettingUtil.setEDogEnable(true);
					try {
						ComponentName componentEDog = new ComponentName(
								"entry.dsa2014", "entry.dsa2014.MainActivity");
						Intent intentEDog = new Intent();
						intentEDog.setComponent(componentEDog);
						activity.startActivity(intentEDog);
					} catch (Exception e) {
						e.printStackTrace();
					}
					activity.overridePendingTransition(
							R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				} else {
					SettingUtil.setEDogEnable(false);
					String strNoAcc = "正在休眠";
					Toast.makeText(activity, strNoAcc, Toast.LENGTH_SHORT)
							.show();
					startSpeak(activity, strNoAcc);
				}
			}
			break;

		case FILEEXPLORER:
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
				Intent intentSetting = new Intent(activity,
						SettingActivity.class);
				activity.startActivity(intentSetting);
				activity.overridePendingTransition(R.anim.zms_translate_up_out,
						R.anim.zms_translate_up_in);
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

		case WEME:
			try {
				// 打开GPS
				activity.sendBroadcast(new Intent(
						"tchip.intent.action.ACTION_GPS_ON"));

				Intent intent = new Intent();
				ComponentName comp = new ComponentName("com.mirrtalk.app",
						"com.mirrtalk.app.MainActivity");
				intent.setComponent(comp);
				intent.setAction("android.intent.action.VIEW");
				activity.startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case WIFI:
			if (!ClickUtil.isQuickClick(800)) {
				if (Constant.Module.isWifiSystem) {
					try {
						activity.startActivity(new Intent(
								android.provider.Settings.ACTION_WIFI_SETTINGS));
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Intent intentWiFi = new Intent(activity,
							WifiListActivity.class);
					activity.startActivity(intentWiFi);
				}

			}
			break;

		default:
			break;
		}
	}

	private static void startSpeak(Context context, String content) {
		Intent intent = new Intent(context, SpeakService.class);
		intent.putExtra("content", content);
		context.startService(intent);
	}

}
