package com.tchip.carlauncher.model;

import com.tchip.carlauncher.MyApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class SleepReceiver extends BroadcastReceiver {
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;

		String action = intent.getAction();
		if (action.equals("com.tchip.SLEEP_ON")) {
			// 进入低功耗待机
			MyApplication.isSleeping = true;
			if (!isAirplaneModeOn()) {
				setAirplaneMode(true);
			}

		} else if (action.equals("com.tchip.SLEEP_OFF")) {
			// 取消低功耗待机
			MyApplication.isSleeping = false;
			if (isAirplaneModeOn()) {
				setAirplaneMode(false);
			}
		}
	}

	/**
	 * 当前是否开启飞行模式
	 */
	private boolean isAirplaneModeOn() {
		// 返回值是1时表示处于飞行模式
		int modeIdx = Settings.Global.getInt(context.getContentResolver(),
				Settings.Global.AIRPLANE_MODE_ON, 0);
		boolean isEnabled = (modeIdx == 1);
		return isEnabled;
	}

	/**
	 * 设置飞行模式
	 */
	private void setAirplaneMode(boolean setAirPlane) {
		Settings.Global.putInt(context.getContentResolver(),
				Settings.Global.AIRPLANE_MODE_ON, setAirPlane ? 1 : 0);
		// 广播飞行模式的改变，让相应的程序可以处理。
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", setAirPlane);
		context.sendBroadcast(intent);
	}
}
