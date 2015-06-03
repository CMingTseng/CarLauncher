package com.tchip.carlauncher.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tchip.carlauncher.service.TrafficFetchService;
import com.tchip.carlauncher.util.TrafficUtils;

public class TrafficBootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		TrafficDbManager.getInstance(context).setTrafficTotal(0L);
		/**
		 * 定时更新流量
		 */
		TrafficUtils.startRepeatingService(context, TrafficUtils.INTERVAL,
				TrafficFetchService.class, "");
	}
}
