package com.tchip.carlauncher.model;

import java.io.File;

import cn.kuwo.autosdk.api.KWAPI;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.MyApp;
import com.tchip.carlauncher.util.MyLog;
import com.tchip.carlauncher.util.StorageUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

public class CardEjectReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_MEDIA_EJECT)
				|| action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)
				|| action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
			if (!StorageUtil.isVideoCardExists()) {
				MyApp.isVideoCardEject = true;
			}

			// 规避播放音乐时拔SD,media-server died,从而导致主界面录像预览卡死问题
			// 但会导致播放网络音乐拔SD卡,同样关掉酷我
			KWAPI.createKWAPI(context, "auto").exitAPP(context);
			context.sendBroadcast(new Intent("com.tchip.KILL_APP").putExtra(
					"value", "music_kuwo"));

		} else if (action.equals(Intent.ACTION_MEDIA_NOFS)) {
			MyLog.e("CardEjectReceiver:ACTION_MEDIA_NOFS !!");

		} else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
			// 插入录像卡自动录像
			if ("/storage/sdcard2".equals(intent.getData().getPath())
					&& MyApp.isAccOn) {
				MyApp.shouldMountRecord = true;
			}

			if (StorageUtil.isVideoCardExists()) {
				MyApp.isVideoCardEject = false;
				MyApp.isVideoCardFormat = false;

				SharedPreferences sharedPreferences = context
						.getSharedPreferences(Constant.MySP.NAME,
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
			String sdcardPath = Constant.Path.SDCARD_2 + File.separator; // "/storage/sdcard2/";
			File file = new File(sdcardPath + "tachograph/");
			StorageUtil.RecursionDeleteFile(file);
		}
	}

}
