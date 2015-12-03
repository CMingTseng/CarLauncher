package com.tchip.carlauncher.util;

import java.io.File;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.MyApplication;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.model.DriveVideoDbHelper;
import com.tchip.carlauncher.service.SpeakService;
import com.tchip.carlauncher.ui.activity.MainActivity;
import com.tchip.carlauncher.view.AudioRecordDialog;

import android.content.Context;
import android.content.Intent;
import android.os.StatFs;
import android.widget.Toast;

public class StorageUtil {

	/**
	 * 获得SD卡总大小
	 * 
	 * @return 总大小，单位：字节B
	 */
	public static long getSDTotalSize(String SDCardPath) {
		StatFs stat = new StatFs(SDCardPath);
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return blockSize * totalBlocks;
	}

	/**
	 * 获得sd卡剩余容量，即可用大小
	 * 
	 * @return 剩余空间，单位：字节B
	 */
	public static long getSDAvailableSize(String SDCardPath) {
		// StatFs stat = new StatFs("/storage/sdcard1");
		StatFs stat = new StatFs(SDCardPath);
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return blockSize * availableBlocks;
	}

	/**
	 * 录像SD卡是否存在
	 * 
	 * @return
	 */
	public static boolean isVideoCardExists() {
		try {
			String pathVideo = Constant.Path.SDCARD_1 + "/tachograph/";
			if (Constant.Record.saveVideoToSD2) {
				pathVideo = Constant.Path.SDCARD_2 + "/tachograph/";
			}
			File fileVideo = new File(pathVideo);
			fileVideo.mkdirs();
			File file = new File(pathVideo);
			if (!file.exists()) {
				return false;
			}
		} catch (Exception e) {
			MyLog.e("[StorageUtil]isVideoCardExists:Catch Exception!");
			return false;
		}
		return true;
	}

	/**
	 * 地图SD卡是否存在
	 * 
	 * @return
	 */
	public boolean isMapSDExists() {
		try {
			String pathVideo = Constant.Path.SD_CARD_MAP + "/BaiduMapSDK/";
			File fileVideo = new File(pathVideo);
			fileVideo.mkdirs();
			File file = new File(pathVideo);
			if (!file.exists()) {
				return false;
			}
		} catch (Exception e) {
			MyLog.e("[StorageUtil]isMapSDExists:Catch Exception!");
			return false;
		}
		return true;
	}

	/**
	 * 递归删除文件和文件夹
	 * 
	 * @param file
	 *            要删除的根目录
	 */
	public static void RecursionDeleteFile(File file) {
		try {
			if (file.isFile()) {
				file.delete();
				return;
			}
			if (file.isDirectory()) {
				File[] childFile = file.listFiles();
				if (childFile == null || childFile.length == 0) {
					file.delete();
					return;
				}
				for (File f : childFile) {
					RecursionDeleteFile(f);
				}
				file.delete();
			}
		} catch (Exception e) {
			MyLog.e("[StorageUtil]RecursionDeleteFile:Catch Exception!");
		}
	}

	/**
	 * 删除空视频文件夹
	 */
	public static void deleteEmptyVideoDirectory() {
		File fileRoot = new File(Constant.Path.SDCARD_2 + File.separator
				+ "tachograph/");
		File[] listFileDate = fileRoot.listFiles();
		for (File file : listFileDate) {
			if (file.isDirectory()) {
				int numberChild = file.listFiles().length;
				if (numberChild == 0) {
					file.delete();
					MyLog.v("[StorageUtil]Delete Empty Video Directory:"
							+ file.getName() + ",Length:" + numberChild);
				}
			}
		}
	}

	/**
	 * 删除最旧视频，调用此函数的地方：
	 * 
	 * 1.开启录像 {@link MainActivity#startRecordTask}
	 * 
	 * 2.文件保存回调{@link #onFileSave}
	 */
	public static boolean deleteOldestUnlockVideo(Context context) {
		try {
			// 视频数据库
			DriveVideoDbHelper videoDb = new DriveVideoDbHelper(context);
			AudioRecordDialog audioRecordDialog = new AudioRecordDialog(context);

			StorageUtil.deleteEmptyVideoDirectory();
			String sdcardPath = Constant.Path.SDCARD_1 + File.separator;// "/storage/sdcard1/";
			if (Constant.Record.saveVideoToSD2) {
				sdcardPath = Constant.Path.SDCARD_2 + File.separator;// "/storage/sdcard2/";
			}
			// sharedPreferences.getString("sdcardPath","/mnt/sdcard2");

			float sdFree = StorageUtil.getSDAvailableSize(sdcardPath);
			// float sdTotal = StorageUtil.getSDTotalSize(sdcardPath);
			int intSdFree = (int) sdFree;
			MyLog.v("[StorageUtil]deleteOldestUnlockVideo, sdFree:" + intSdFree);
			while (intSdFree < Constant.Record.SD_MIN_FREE_STORAGE) {
				int oldestUnlockVideoId = videoDb.getOldestUnlockVideoId();
				// 删除较旧未加锁视频文件
				if (oldestUnlockVideoId != -1) {
					String oldestUnlockVideoName = videoDb
							.getVideNameById(oldestUnlockVideoId);
					File f = new File(sdcardPath + "tachograph/"
							+ oldestUnlockVideoName.split("_")[0]
							+ File.separator + oldestUnlockVideoName);
					if (f.exists() && f.isFile()) {
						MyLog.d("[StorageUtil]Delete Old Unlock Video:"
								+ f.getName());
						int i = 0;
						while (!f.delete() && i < 5) {
							i++;
							MyLog.d("[StorageUtil]Delete Old Unlock Video:"
									+ f.getName() + " Filed!!! Try:" + i);
						}
					}
					// 删除数据库记录
					videoDb.deleteDriveVideoById(oldestUnlockVideoId);
				} else {
					int oldestVideoId = videoDb.getOldestVideoId();
					if (oldestVideoId == -1) {

						if (MyApplication.isVideoReording) {
							// TODO:停止录像
							// stopRecorder();
						}
						/**
						 * 有一种情况：数据库中无视频信息。导致的原因：
						 * 1：升级时选Download的话，不会清理USB存储空间，应用数据库被删除； 2：应用被清除数据
						 * 这种情况下旧视频无法直接删除， 此时如果满存储，需要直接删除
						 */
						File file = new File(sdcardPath + "tachograph/");
						StorageUtil.RecursionDeleteFile(file);
						MyLog.e("[StorageUtil]!!! Delete tachograph/ Directory");

						sdFree = StorageUtil.getSDAvailableSize(sdcardPath);
						intSdFree = (int) sdFree;
						if (intSdFree < Constant.Record.SD_MIN_FREE_STORAGE) {
							// 此时若空间依然不足,提示用户清理存储（已不是行车视频的原因）
							MyLog.e("[StorageUtil]Storage is full...");

							String strNoStorage = context
									.getResources()
									.getString(
											R.string.storage_full_cause_by_other);

							audioRecordDialog.showErrorDialog(strNoStorage);
							// new Thread(new dismissDialogThread()).start();
							startSpeak(context, strNoStorage);

							return false;
						}
					} else {
						// 提示用户清理空间，删除较旧的视频（加锁）
						String strStorageFull = context.getResources()
								.getString(
										R.string.storage_full_and_delete_lock);
						startSpeak(context, strStorageFull);
						Toast.makeText(context, strStorageFull,
								Toast.LENGTH_SHORT).show();

						String oldestVideoName = videoDb
								.getVideNameById(oldestVideoId);
						File f = new File(sdcardPath + "tachograph/"
								+ oldestVideoName.split("_")[0]
								+ File.separator + oldestVideoName);
						if (f.exists() && f.isFile()) {
							MyLog.d("[StorageUtil]Delete Old lock Video:"
									+ f.getName());
							int i = 0;
							while (!f.delete() && i < 5) {
								i++;
								MyLog.d("[StorageUtil]Delete Old lock Video:"
										+ f.getName() + " Filed!!! Try:" + i);
							}
						}
						// 删除数据库记录
						videoDb.deleteDriveVideoById(oldestVideoId);
					}
				}
				// 更新剩余空间
				sdFree = StorageUtil.getSDAvailableSize(sdcardPath);
				intSdFree = (int) sdFree;
			}
			return true;
		} catch (Exception e) {
			/*
			 * 异常原因：1.文件由用户手动删除
			 */
			MyLog.e("[StorageUtil]deleteOldestUnlockVideo:Catch Exception:"
					+ e.toString());
			e.printStackTrace();
			return true;
		}
	}

	private static void startSpeak(Context context, String content) {
		Intent intent = new Intent(context, SpeakService.class);
		intent.putExtra("content", content);
		context.startService(intent);
	}

	/**
	 * 删除数据库中不存在的错误视频文件
	 * 
	 * @param file
	 */
	public static void RecursionCheckFile(Context context, File file) {
		if (MyApplication.isVideoReording) {
			// 开始录像，终止删除
			MyLog.v("[StorageUtil]RecursionCheckFile-Stop in case of isVideoReording == true");
			return;
		} else {
			// 视频数据库
			DriveVideoDbHelper videoDb = new DriveVideoDbHelper(context);
			try {
				if (file.isFile() && !file.getName().endsWith(".jpg")) {
					if (!videoDb.isVideoExist(file.getName())) {
						file.delete();
						MyLog.v("[StorageUtil]RecursionCheckFile-Delete Error File:"
								+ file.getName());
					}
					return;
				}
				if (file.isDirectory()) {
					File[] childFile = file.listFiles();
					if (childFile == null || childFile.length == 0) {
						// file.delete();
						return;
					}
					for (File f : childFile) {
						RecursionCheckFile(context, f);
					}
					// file.delete();
				}
			} catch (Exception e) {
				MyLog.e("[StorageUtil]RecursionCheckFile-Catch Exception:"
						+ e.toString());
			}
		}
	}

}
