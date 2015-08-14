package com.tchip.carlauncher.util;

import java.io.File;

import com.tchip.carlauncher.Constant;

import android.os.StatFs;

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
			return false;
		}
		return true;
	}

}
