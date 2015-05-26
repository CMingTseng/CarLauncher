package com.tchip.carlauncher.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class MusicFolderInfoDao {

	private static final String TABLE_FOLDER = "folder_info";
	private Context mContext;

	public MusicFolderInfoDao(Context context) {
		this.mContext = context;
	}

	public void saveFolderInfo(List<MusicFolderInfo> list) {
		SQLiteDatabase db = MusicDatabaseHelper.getInstance(mContext);
		for (MusicFolderInfo info : list) {
			ContentValues cv = new ContentValues();
			cv.put("folder_name", info.folder_name);
			cv.put("folder_path", info.folder_path);
			db.insert(TABLE_FOLDER, null, cv);
		}
	}

	public List<MusicFolderInfo> getFolderInfo() {
		SQLiteDatabase db = MusicDatabaseHelper.getInstance(mContext);
		List<MusicFolderInfo> list = new ArrayList<MusicFolderInfo>();
		String sql = "select * from " + TABLE_FOLDER;
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			MusicFolderInfo info = new MusicFolderInfo();
			info.folder_name = cursor.getString(cursor
					.getColumnIndex("folder_name"));
			info.folder_path = cursor.getString(cursor
					.getColumnIndex("folder_path"));
			list.add(info);
		}
		cursor.close();
		return list;
	}

	/**
	 * 数据库中是否有数据
	 * 
	 * @return
	 */
	public boolean hasData() {
		SQLiteDatabase db = MusicDatabaseHelper.getInstance(mContext);
		String sql = "select count(*) from " + TABLE_FOLDER;
		Cursor cursor = db.rawQuery(sql, null);
		boolean has = false;
		if (cursor.moveToFirst()) {
			int count = cursor.getInt(0);
			if (count > 0) {
				has = true;
			}
		}
		cursor.close();
		return has;
	}

	public int getDataCount() {
		SQLiteDatabase db = MusicDatabaseHelper.getInstance(mContext);
		String sql = "select count(*) from " + TABLE_FOLDER;
		Cursor cursor = db.rawQuery(sql, null);
		int count = 0;
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}
		return count;
	}
}
