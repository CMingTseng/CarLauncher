package com.tchip.carlauncher.model;

import com.tchip.carlauncher.service.NetworkChangeService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class NetworkChangeReceiver extends BroadcastReceiver {

	public static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (TextUtils.equals(action, CONNECTIVITY_CHANGE_ACTION)) {

			Intent serviceIntent = new Intent(context,
					NetworkChangeService.class);
			context.startService(serviceIntent);
			// Toast.makeText(context, "网络状态变化", Toast.LENGTH_SHORT).show();
		}
	}
}
