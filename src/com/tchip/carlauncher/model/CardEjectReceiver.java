package com.tchip.carlauncher.model;

import java.io.File;

import com.baidu.mapapi.map.Stroke;
import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.MyApplication;
import com.tchip.carlauncher.util.StorageUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class CardEjectReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
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

				SharedPreferences sharedPreferences = context
						.getSharedPreferences(Constant.SHARED_PREFERENCES_NAME,
								Context.MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();

				if (sharedPreferences.getBoolean("isFirstLaunch", true)) {

					new Thread(new DeleteVideoDirThread()).start();

					Log.e(Constant.TAG, "Delete video directory:tachograph !!!");

					editor.putBoolean("isFirstLaunch", false);
					editor.commit();
				} else {
					Log.e(Constant.TAG, "App isn't first launch");
				}
			}
		}
	}

	private class DeleteVideoDirThread implements Runnable {

		@Override
		public void run() {
			// 初次启动清空录像文件夹
			String sdcardPath = Constant.Path.SDCARD_1 + File.separator; // "/storage/sdcard1/";
			if (Constant.Record.saveVideoToSD2) {
				sdcardPath = Constant.Path.SDCARD_2 + File.separator; // "/storage/sdcard2/";
			}
			File file = new File(sdcardPath + "tachograph/");
			StorageUtil.RecursionDeleteFile(file);
		}

	}
}
