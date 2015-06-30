package com.tchip.carlauncher.util;

import com.tchip.carlauncher.service.SpeakService;
import com.tchip.carlauncher.ui.activity.ChatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkUtil {

	public static int getNetworkType(Context context) {
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		if (info != null) {
			return info.getType();
		} else {
			return -1;
		}
	}

	public static void noNetworkHint(Context context) {
		String strNoNetwork = "无网络链接";

		Intent intent = new Intent(context, SpeakService.class);
		intent.putExtra("content", strNoNetwork);
		context.startService(intent);

		Toast.makeText(context, strNoNetwork, Toast.LENGTH_SHORT).show();
	}
}
