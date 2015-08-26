package com.tchip.carlauncher.model;

import com.tchip.carlauncher.MyApplication;
import com.tchip.carlauncher.util.StorageUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CardEjectReceiver extends BroadcastReceiver {

	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;

		String action = intent.getAction();
		if (action.equals(Intent.ACTION_MEDIA_EJECT)
				|| action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)
				|| action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
			if (!StorageUtil.isVideoCardExists()) {
				MyApplication.isVideoCardEject = true;
			}
		} else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
			if (StorageUtil.isVideoCardExists()) {
				MyApplication.isVideoCardEject = false;
			}
		}
	}
}
