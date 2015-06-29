package com.tchip.carlauncher.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class EDatabaseHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "edrivecorder.db";
	private static final int VERSION = 1;

	public EDatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE video(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"name varchar(50)," + 
				"path varchar(100)," +
				"path_withoutname varchar(100)," +
				"protect INTEGER," + 
				"keep_save INTEGER," + 
				"resolution varchar(10)," +
				"file_size varchar(15)," +
				"duration varchar(10)," +
				"path_thumbnail varchar(100)," +
				"btime varchar(50)," +
				"btime_unix INTEGER," +
				"etime varchar(50)," + 
				"etime_unix INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
