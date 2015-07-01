package com.tchip.carlauncher.model;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.tchip.carlauncher.Constant;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LZSVideoTableDao {

	private LZSDatabaseHelper mDatabaseHelper;
	// private SQLiteDatabase dbRead, dbWrite;

	private static String TABLE_NAME = "video";

	public LZSVideoTableDao(Context context) {
		mDatabaseHelper = new LZSDatabaseHelper(context);
	}

	public void addVideo(String name, String path_withoutname, long btime,
			long etime, boolean protect, String resolution, String file_size,
			String path_thumbnail) {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
		String bTimeString = sDateFormat.format(new Date(btime));
		Integer bTimeInteger = (int) (btime / 1000);
		String eTimeString = sDateFormat.format(new Date(etime));
		Integer eTimeInteger = (int) (etime / 1000);

		LZSVideoTable videoTable = new LZSVideoTable();
		videoTable.setName(name);
		videoTable.setPath(path_withoutname + name);
		videoTable.setPath_withoutname(path_withoutname);
		videoTable.setResolution(resolution);
		videoTable.setFile_size(file_size);
		videoTable.setDuration(getDurationTime((int) ((etime - btime) / 1000)));
		videoTable.setPath_thumbnail(path_thumbnail);

		if (protect) {
			videoTable.setProtect(1);
		} else {
			videoTable.setProtect(0);
		}
		videoTable.setKeep_save(0);
		videoTable.setBtime(bTimeString);
		videoTable.setBtime_unix(bTimeInteger);
		videoTable.setEtime(eTimeString);
		videoTable.setEtime_unix(eTimeInteger);
		addVideo(videoTable);
	}

	public void addVideo(LZSVideoTable videoTable) {
		SQLiteDatabase dbWrite = mDatabaseHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("name", videoTable.getName());
		cv.put("path", videoTable.getPath());
		cv.put("path_withoutname", videoTable.getPath_withoutname());
		cv.put("protect", videoTable.getProtect());
		cv.put("keep_save", videoTable.getKeep_save());
		cv.put("resolution", videoTable.getResolution());
		cv.put("file_size", videoTable.getFile_size());
		cv.put("duration", videoTable.getDuration());
		cv.put("path_thumbnail", videoTable.getPath_thumbnail());
		cv.put("btime", videoTable.getBtime());
		cv.put("btime_unix", videoTable.getBtime_unix());
		cv.put("etime", videoTable.getEtime());
		cv.put("etime_unix", videoTable.getEtime_unix());
		dbWrite.insert(TABLE_NAME, null, cv);
		dbWrite.close();

	}

	public Cursor selectAll() {
		SQLiteDatabase dbRead = mDatabaseHelper.getReadableDatabase();
		Cursor cursor = dbRead.rawQuery("SELECT * FROM video", null);
		return cursor;
	}

	public Cursor selectByFolderPath(String folderPath) {
		SQLiteDatabase dbRead = mDatabaseHelper.getReadableDatabase();
		Cursor cursor = dbRead.rawQuery(
				"SELECT * FROM video WHERE path_withoutname=?",
				new String[] { folderPath });
		return cursor;
	}

	public Cursor secectNormal() {
		SQLiteDatabase dbRead = mDatabaseHelper.getReadableDatabase();
		Cursor cursor = dbRead.rawQuery(
				"SELECT * FROM video WHERE protect=? and keep_save=?",
				new String[] { "0", "0" });
		return cursor;
	}

	public LZSVideoTable secectNormalFirst() {
		SQLiteDatabase dbRead = mDatabaseHelper.getReadableDatabase();
		Cursor cursor = dbRead.rawQuery(
				"SELECT * FROM video WHERE protect=? and keep_save=?",
				new String[] { "0", "0" });
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			LZSVideoTable videoTable = new LZSVideoTable();
			videoTable.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			videoTable.setName(cursor.getString(cursor.getColumnIndex("name")));
			videoTable.setPath(cursor.getString(cursor.getColumnIndex("path")));
			videoTable.setPath_thumbnail(cursor.getString(cursor
					.getColumnIndex("path_thumbnail")));
			videoTable.setDuration(cursor.getString(cursor
					.getColumnIndex("duration")));
			videoTable
					.setBtime(cursor.getString(cursor.getColumnIndex("btime")));
			videoTable.setFile_size(cursor.getString(cursor
					.getColumnIndex("file_size")) + "MB");
			return videoTable;
		} else {
			return null;
		}
	}

	public Cursor selectProtect() {
		SQLiteDatabase dbRead = mDatabaseHelper.getReadableDatabase();
		Cursor cursor = dbRead.rawQuery("SELECT * FROM video WHERE protect=?",
				new String[] { "1" });
		return cursor;
	}

	public Cursor selectKeepSave() {
		SQLiteDatabase dbRead = mDatabaseHelper.getReadableDatabase();
		Cursor cursor = dbRead.rawQuery(
				"SELECT * FROM video WHERE keep_save=?", new String[] { "1" });
		return cursor;
	}

	public String selectLast() {
		SQLiteDatabase dbRead = mDatabaseHelper.getReadableDatabase();
		Cursor cursor = dbRead.rawQuery("SELECT * FROM video", null);
		if (cursor.getCount() > 0) {
			cursor.moveToLast();
			String name = cursor.getString(cursor.getColumnIndex("name"));
			return name;
		} else {
			return null;
		}
	}

	public LZSVideoTable selectOneByPath(String path) {
		SQLiteDatabase dbRead = mDatabaseHelper.getReadableDatabase();
		Cursor cursor = dbRead.rawQuery("SELECT * FROM video WHERE path=?",
				new String[] { path });
		if (cursor.getCount() > 0) {
			cursor.moveToNext();
			LZSVideoTable videoTable = new LZSVideoTable();
			videoTable.setName(cursor.getString(cursor.getColumnIndex("name")));
			videoTable.setPath_thumbnail(cursor.getString(cursor
					.getColumnIndex("path_thumbnail")));
			videoTable.setDuration(cursor.getString(cursor
					.getColumnIndex("duration")));
			videoTable
					.setBtime(cursor.getString(cursor.getColumnIndex("btime")));
			videoTable.setFile_size(cursor.getString(cursor
					.getColumnIndex("file_size")) + "MB");
			return videoTable;
		} else {
			return null;
		}
	}

	public int selectLast_protect() {
		SQLiteDatabase dbRead = mDatabaseHelper.getReadableDatabase();
		Cursor cursor = dbRead.rawQuery("SELECT * FROM video", null);
		if (cursor.getCount() > 0) {
			cursor.moveToLast();
			int protect = cursor.getInt(cursor.getColumnIndex("protect"));
			return protect;
		} else {
			return 0;
		}
	}

	public void delById(String id) {
		SQLiteDatabase dbWrite = mDatabaseHelper.getWritableDatabase();
		dbWrite.delete(TABLE_NAME, "_id=?", new String[] { id });
		dbWrite.close();
	}

	public void delByPath(String path) {
		SQLiteDatabase dbWrite = mDatabaseHelper.getWritableDatabase();
		dbWrite.delete(TABLE_NAME, "path=?", new String[] { path });
		dbWrite.close();
	}

	public void delByFoldPath(String path) {
		SQLiteDatabase dbWrite = mDatabaseHelper.getWritableDatabase();
		dbWrite.delete(TABLE_NAME, "path_withoutname=?", new String[] { path });
		dbWrite.close();
	}

	public void updateByName(String name) {
		String protectName = "PROTECT_" + name;
		String protectPath = "sdcard/ECarRecorder/" + protectName;
		ContentValues cv = new ContentValues();
		cv.put("protect", 1);
		cv.put("name", protectName);
		cv.put("path", protectPath);
		SQLiteDatabase dbWrite = mDatabaseHelper.getWritableDatabase();
		dbWrite.update(TABLE_NAME, cv, "name=?", new String[] { name });
	}

	public void updateToKeepSave(String name) {
		String keepSavePath = Constant.EROOTPATH.SAVEPATH + name;
		String keepThumbPath = Constant.EROOTPATH.SAVEPATH + ".Thumb"
				+ File.separator + name.substring(0, name.lastIndexOf('.'))
				+ ".png";
		ContentValues cv = new ContentValues();
		cv.put("keep_save", 1);
		cv.put("path", keepSavePath);
		cv.put("path_withoutname", Constant.EROOTPATH.SAVEPATH);
		cv.put("path_thumbnail", keepThumbPath);
		SQLiteDatabase dbWrite = mDatabaseHelper.getWritableDatabase();
		dbWrite.update(TABLE_NAME, cv, "name=?", new String[] { name });
	}

	private String getDurationTime(int time_value) {
		String timeString = null;
		if (time_value < 60) {
			if (time_value < 10) {
				timeString = "00:0" + time_value;
			} else {
				timeString = "00:" + time_value;
			}
		} else {
			String time_min = null;
			String time_sec = null;
			if ((time_value / 60) < 10) {
				time_min = "0" + time_value / 60;
			} else {
				time_min = "" + time_value / 60;
			}

			if ((time_value % 60) < 10) {
				time_sec = "0" + time_value % 60;
			} else {
				time_sec = "" + time_value % 60;
			}
			timeString = time_min + ":" + time_sec;
		}
		return "视频时间:" + timeString;
	}

}
