package com.tchip.carlauncher.model;

import com.tchip.carlauncher.MyApplication;
import com.tchip.carlauncher.service.RouteRecordService;
import com.tchip.carlauncher.service.SpeakService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

public class PowerStateChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		// boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
		// || status == BatteryManager.BATTERY_STATUS_FULL;
		//
		// int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,
		// -1);
		// boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
		// boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
		// Toast.makeText(
		// context,
		// "isCharging:" + isCharging + "-usbCharge:" + usbCharge
		// + "-acCharge:" + acCharge, Toast.LENGTH_SHORT).show();

		// //当前剩余电量
		// int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,
		// -1);
		// //电量最大值
		// int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE,
		// -1);
		// //电量百分比
		// float batteryPct = level / (float)scale;

		if ("android.intent.action.ACTION_POWER_CONNECTED".equals(intent
				.getAction())) {
			// String strHintConnect = "";
			// Toast.makeText(context, "CONNECTED", Toast.LENGTH_SHORT).show();
			MyApplication.isPowerConnect = true;
			// 停止轨迹记录服务，保存轨迹
			Intent intentRoute = new Intent(context, RouteRecordService.class);
			context.startService(intentRoute);
		} else if ("android.intent.action.ACTION_POWER_DISCONNECTED"
				.equals(intent.getAction())) {
			MyApplication.isPowerConnect = false;
			String strHintDisconnect = "已断开电源，正在保存数据。";
			Toast.makeText(context, strHintDisconnect, Toast.LENGTH_SHORT)
					.show();
			Intent intentSpeak = new Intent(context, SpeakService.class);
			intentSpeak.putExtra("content", strHintDisconnect);
			context.startService(intentSpeak);

			// 停止轨迹记录服务，保存轨迹
			Intent intentRoute = new Intent(context, RouteRecordService.class);
			context.stopService(intentRoute);
		}
	}

}
