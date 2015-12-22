package com.tchip.carlauncher.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import net.sourceforge.jheader.App1Header;
import net.sourceforge.jheader.ExifFormatException;
import net.sourceforge.jheader.JpegFormatException;
import net.sourceforge.jheader.JpegHeaders;
import net.sourceforge.jheader.TagFormatException;
import net.sourceforge.jheader.App1Header.Tag;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.MyApp;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.model.DriveVideoDbHelper;
import com.tchip.carlauncher.service.SpeakService;
import com.tchip.carlauncher.ui.activity.MainActivity;
import com.tchip.carlauncher.view.AudioRecordDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.ExifInterface;
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

	/** 录像SD卡是否存在 **/
	public static boolean isVideoCardExists() {
		try {
			String pathVideo = Constant.Path.SDCARD_2 + "/tachograph/";
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

	/** 地图SD卡是否存在 **/
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
				if (!file.getName().startsWith(".")) { // 不删除正在录制的视频
					file.delete();
				}
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

	/** 删除空视频文件夹 **/
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
	 * 2.文件保存回调{@link MainActivity#onFileSave}
	 */
	public static boolean deleteOldestUnlockVideo(Context context) {
		try {
			// 视频数据库
			DriveVideoDbHelper videoDb = new DriveVideoDbHelper(context);
			AudioRecordDialog audioRecordDialog = new AudioRecordDialog(context);

			StorageUtil.deleteEmptyVideoDirectory();
			String sdcardPath = Constant.Path.SDCARD_2 + File.separator;// "/storage/sdcard2/";

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

						if (MyApp.isVideoReording) {
							// stopRecorder(); // TODO:停止录像
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
							HintUtil.speakVoice(context, strNoStorage);

							return false;
						}
					} else {
						// 提示用户清理空间，删除较旧的视频（加锁）
						String strStorageFull = context.getResources()
								.getString(
										R.string.storage_full_and_delete_lock);
						HintUtil.speakVoice(context, strStorageFull);
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
						videoDb.deleteDriveVideoById(oldestVideoId); // 删除数据库记录
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

	/**
	 * 删除数据库中不存在的错误视频文件
	 * 
	 * @param file
	 */
	public static void RecursionCheckFile(Context context, File file) {
		DriveVideoDbHelper videoDb = new DriveVideoDbHelper(context); // 视频数据库
		try {
			if (file.isFile() && !file.getName().endsWith(".jpg")
					&& !file.getName().startsWith(".")) {
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

	/**
	 * 写入EXIF信息
	 * 
	 * @param context
	 * @param imagePath
	 */
	public static void writeImageExif(Context context, String imagePath) {
		// Android Way
		try {
			SharedPreferences sharedPreferences = context.getSharedPreferences(
					Constant.MySP.NAME, Context.MODE_PRIVATE);

			ExifInterface exif = new ExifInterface(imagePath);
			// 经度
			String strLongitude = sharedPreferences.getString("longitude",
					"0.00");
			double intLongitude = Double.parseDouble(strLongitude);
			exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, strLongitude);
			exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF,
					intLongitude > 0.0f ? "E" : "W");
			// 纬度
			String strLatitude = sharedPreferences
					.getString("latitude", "0.00");
			double intLatitude = Double.parseDouble(strLatitude);
			exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, strLongitude);
			exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,
					intLatitude > 0.0f ? "N" : "S");
			exif.setAttribute(ExifInterface.TAG_ORIENTATION, ""
					+ ExifInterface.ORIENTATION_NORMAL);

			exif.setAttribute(ExifInterface.TAG_MAKE, "zenlane"); // 品牌
			exif.setAttribute(ExifInterface.TAG_MODEL, "X755"); // 型号/机型

			// exif.setAttribute(ExifInterface.TAG_FLASH, "1/30"); // 闪光灯
			// exif.setAttribute(ExifInterface.TAG_FOCAL_LENGTH, "5/1"); // 焦距
			// exif.setAttribute(ExifInterface.TAG_WHITE_BALANCE,
			// ExifInterface.WHITEBALANCE_AUTO+"/1"); // 白平衡
			// exif.setAttribute(ExifInterface.TAG_EXPOSURE_TIME, "1/30"); //
			// // 曝光时间
			// exif.setAttribute(ExifInterface.TAG_ISO, "100"); // 感光度
			// exif.setAttribute(ExifInterface.TAG_APERTURE, "2/1"); // 光圈

			exif.saveAttributes();
		} catch (Exception e) {
			MyLog.e("[Android]Set Attribute Catch Exception:" + e.toString());
			e.printStackTrace();
		}

		// JpegHeaders Way
		try {
			JpegHeaders jpegHeaders = new JpegHeaders(imagePath);
			App1Header exifHeader = jpegHeaders.getApp1Header();

			// 遍历显示EXIF
			// SortedMap tags = exifHeader.getTags();
			// for (Map.Entry entry : tags.entrySet()) {
			// System.out.println(entry.getKey() + "[" + entry.getKey().name
			// + "]:" + entry.getValue());
			// }

			// 修改EXIF
			// exifHeader.setValue(Tag.DATETIMEORIGINAL, "2015:05:55 05:55:55");
			exifHeader.setValue(Tag.ORIENTATION, "1"); // 浏览模式/方向:上/左
			exifHeader.setValue(Tag.APERTUREVALUE, "22/10"); // 光圈：2.2
			exifHeader.setValue(Tag.FOCALLENGTH, "7/2"); // 焦距：3.5mm
			exifHeader.setValue(Tag.WHITEBALANCE, "0"); // 白平衡：自动
			exifHeader.setValue(Tag.ISOSPEEDRATINGS, "100"); // ISO感光度：100
			exifHeader.setValue(Tag.EXPOSURETIME, "1/30"); // 曝光时间：1/30
			// 曝光补偿:EV值每增加1.0，相当于摄入的光线量增加一倍，如果照片过亮，要减小EV值，EV值每减小1.0，相当于摄入的光线量减小一倍
			exifHeader.setValue(Tag.EXPOSUREBIASVALUE,
					(1 + new Random().nextInt(10)) + "/10");
			exifHeader.setValue(Tag.METERINGMODE, "1"); // 测光模式：平均
			exifHeader.setValue(Tag.SATURATION,
					"" + (5 + new Random().nextInt(10))); // 饱和度：5-15

			exifHeader.setValue(Tag.FLASH, "0"); // 闪光灯：未使用

			// 保存,参数：是否保存原文件为.old
			jpegHeaders.save(false);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			MyLog.e("[JpegHeaders]Set Attribute Error,FileNotFoundException:"
					+ e.toString());
		} catch (ExifFormatException e) {
			e.printStackTrace();
			MyLog.e("[JpegHeaders]Set Attribute Error,ExifFormatException:"
					+ e.toString());
		} catch (TagFormatException e) {
			e.printStackTrace();
			MyLog.e("[JpegHeaders]Set Attribute Error,TagFormatException:"
					+ e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			MyLog.e("[JpegHeaders]Set Attribute Error,IOException:"
					+ e.toString());
		} catch (JpegFormatException e) {
			e.printStackTrace();
			MyLog.e("[JpegHeaders]Set Attribute Error,JpegFormatException:"
					+ e.toString());
		}

	}

}
