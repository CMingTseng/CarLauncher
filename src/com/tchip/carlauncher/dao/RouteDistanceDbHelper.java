package com.tchip.carlauncher.dao;

import java.util.ArrayList;
import java.util.List;

import com.tchip.carlauncher.bean.RouteDistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RouteDistanceDbHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "routes_db";

	private static final String ROUTE_TABLE_NAME = "route";
	private static final String ROUTE_COL_ID = "_id";
	private static final String ROUTE_COL_NAME = "name";
	private static final String ROUTE_COL_LINEAR = "linear";
	private static final String ROUTE_COL_DRIVE = "drive";

	private static final String[] ROUTE_COL_PROJECTION = new String[] {
			ROUTE_COL_ID, ROUTE_COL_NAME, ROUTE_COL_LINEAR, ROUTE_COL_DRIVE, };

	public RouteDistanceDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Create table
	@Override
	public void onCreate(SQLiteDatabase db) {
		String createRouteTableSql = "CREATE TABLE " + ROUTE_TABLE_NAME + " ("
				+ ROUTE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ ROUTE_COL_NAME + " TEXT," + ROUTE_COL_LINEAR + " TEXT,"
				+ ROUTE_COL_DRIVE + " INTEGER" + ");";
		db.execSQL(createRouteTableSql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + ROUTE_TABLE_NAME);

		// Create tables again
		onCreate(db);
	}

	// Add new RouteDistance
	public int addRouteDistance(RouteDistance route) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(ROUTE_COL_NAME, route.getName());
		values.put(ROUTE_COL_LINEAR, route.getLinear());
		values.put(ROUTE_COL_DRIVE, route.getDrive());

		// Insert to database
		long rowId = db.insert(ROUTE_TABLE_NAME, null, values);

		// Close the database
		db.close();

		return (int) rowId;
	}

	// Get RouteDistance By ID
	public RouteDistance getRouteDistanceById(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(ROUTE_TABLE_NAME, ROUTE_COL_PROJECTION,
				ROUTE_COL_ID + "=?", new String[] { String.valueOf(id) }, null,
				null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		RouteDistance routeDistance = new RouteDistance(cursor.getInt(0),
				cursor.getString(1), cursor.getDouble(2), cursor.getDouble(3));

		return routeDistance;
	}

	// Get RouteDistance By Name
	public RouteDistance getRouteDistanceByName(String name) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(ROUTE_TABLE_NAME, ROUTE_COL_PROJECTION,
				ROUTE_COL_NAME + "=?", new String[] { name }, null, null, null,
				null);

		if (cursor != null)
			cursor.moveToFirst();

		RouteDistance routeDistance = new RouteDistance(cursor.getInt(0),
				cursor.getString(1), cursor.getDouble(2), cursor.getDouble(3));

		return routeDistance;
	}

	/**
	 * 获取所有的轨迹距离信息
	 * 
	 * @return
	 */
	public List<RouteDistance> getAllRouteDistance() {
		List<RouteDistance> routeDistanceList = new ArrayList<RouteDistance>();
		String selectQuery = "SELECT * FROM " + ROUTE_TABLE_NAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				RouteDistance routeDistance = new RouteDistance(
						cursor.getInt(0), cursor.getString(1),
						cursor.getDouble(2), cursor.getDouble(3));
				routeDistanceList.add(routeDistance);
			} while (cursor.moveToNext());
		}

		// return contact list
		return routeDistanceList;
	}

	public Cursor getAllRouteDistanceCursor() {
		String selectQuery = "SELECT * FROM " + ROUTE_TABLE_NAME;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;
	}

	public int updateRouteDistance(RouteDistance routeDistance) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(ROUTE_COL_NAME, routeDistance.getName());
		values.put(ROUTE_COL_LINEAR, routeDistance.getLinear());
		values.put(ROUTE_COL_DRIVE, routeDistance.getDrive());

		return db.update(ROUTE_TABLE_NAME, values, ROUTE_COL_ID + "=?",
				new String[] { String.valueOf(routeDistance.getId()) });
	}

	public void deleteRouteDistanceById(int routeDistanceId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(ROUTE_TABLE_NAME, ROUTE_COL_ID + "=?",
				new String[] { String.valueOf(routeDistanceId) });
		db.close();
	}
	
	public void deleteRouteDistanceByName(String name) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(ROUTE_TABLE_NAME, ROUTE_COL_NAME + "=?",
				new String[] { name });
		db.close();
	}

	public void deleteAllRouteDistance() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(ROUTE_TABLE_NAME, null, null);
		db.close();
	}

}
