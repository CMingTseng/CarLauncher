package com.tchip.carlauncher.model;

import com.tchip.carlauncher.MyApplication;
import com.tchip.carlauncher.service.RouteRecordService;
import com.tchip.carlauncher.service.SpeakService;
import com.tchip.carlauncher.util.MyLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PowerStateChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		// "android.intent.action.ACTION_POWER_UNCONNECTED".equals(action)
		MyLog.v("[PowerStateChangeReceiver]action:" + action);
		if ("com.tchip.POWER_OFF".equals(action)) {
			try {
				// MyApplication.isPowerConnect = false;
				//
				// // 熄灭屏幕
				// context.sendBroadcast(new Intent("com.tchip.powerKey")
				// .putExtra("value", "power"));
				//
				// String strHintDisconnect = "电源断开";
				// Toast.makeText(context, strHintDisconnect,
				// Toast.LENGTH_SHORT)
				// .show();
				// Intent intentSpeak = new Intent(context, SpeakService.class);
				// intentSpeak.putExtra("content", strHintDisconnect);
				// context.startService(intentSpeak);
				//
				// // 停止轨迹记录服务，保存轨迹
				// Intent intentRoute = new Intent(context,
				// RouteRecordService.class);
				// context.stopService(intentRoute);
				//
				// // 发送关机广播
				// context.sendBroadcast(new Intent(
				// "tchip.intent.action.ACTION_POWER_OFF"));
			} catch (Exception e) {
				MyLog.e("[PowerStateChangeReceiver]:Error occur when run com.tchip.POWER_OFF");
			}
		}
		// else if ("android.intent.action.ACTION_POWER_CONNECTED".equals(intent
		// .getAction())) {
		// MyApplication.isPowerConnect = true;
		// // 轨迹记录服务
		// Intent intentRoute = new Intent(context, RouteRecordService.class);
		// context.startService(intentRoute);
		// }
	}
}
