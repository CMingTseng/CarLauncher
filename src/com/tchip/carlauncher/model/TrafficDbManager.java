package com.tchip.carlauncher.model;

import android.content.Context;

import com.tchip.carlauncher.util.TrafficUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

import java.util.*;

public class TrafficDbManager {

	public static final int UID_TOTAL_MOBILE = -1;
	public static final int UID_TOTAL_WIFI = -2;

	private static final String COLUMN_ID = "id";
	private static final String COLUMN_TYPE = "type";
	private static final String COLUMN_UID = "uid";
	private static final String COLUMN_RX = "rx";
	private static final String COLUMN_TX = "tx";
	private static final String COLUMN_TIME = "time";

	private static HashMap<String, TrafficDbManager> dbList;

	public enum TrafficDate {
		DAY, MONTH, SEC
	}

	private DbUtils trafficDbUtils;
	private Context ctx;

	private TrafficDbManager() {
	}

	public static TrafficDbManager getInstance(Context ctx) {
		if (dbList == null) {
			dbList = new HashMap<String, TrafficDbManager>();
		}
		TrafficDbManager dbManager = dbList.get(ctx.getClass().getName());
		if (dbManager == null) {
			dbManager = new TrafficDbManager();
			dbManager.init(ctx);
			dbList.put(ctx.getClass().getName(), dbManager);
		}

		if (dbManager.getContext() == null || dbManager.isClose()) {
			dbManager.init(ctx);
		}

		return dbManager;
	}

	public boolean isClose() {
		if (trafficDbUtils == null) {
			return true;
		}
		return false;
	}

	public void close() {
		if (trafficDbUtils != null) {
			trafficDbUtils.close();
			trafficDbUtils = null;
		}
	}

	private void init(Context ctx) {
		this.ctx = ctx;
		DbUtils.DaoConfig config = new DbUtils.DaoConfig(ctx);
		config.setDbName("kaikaiassistant.db");
		config.setDbVersion(1);
		try {
			trafficDbUtils = DbUtils.create(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Context getContext() {
		return ctx;
	}

	public void setTrafficTotal(long data) {
		List<TrafficTotalModel> datas = null;
		try {
			datas = trafficDbUtils.findAll(TrafficTotalModel.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (datas == null || datas.size() == 0) {
			return;
		}

		Iterator<TrafficTotalModel> it = datas.iterator();
		while (it.hasNext()) {
			TrafficTotalModel model = it.next();
			model.setTraffic(data);
		}
		try {
			trafficDbUtils.saveOrUpdateAll(datas);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param type
	 * @param data
	 */
	public void setTrafficTotal(int type, long data) {
		TrafficTotalModel model = null;
		try {
			model = trafficDbUtils.findFirst(Selector.from(
					TrafficTotalModel.class).where(COLUMN_TYPE, "=", type));
		} catch (DbException e) {
			e.printStackTrace();
		}
		if (model == null) {
			model = new TrafficTotalModel();
			model.setTraffic(data);
			model.setTime(TrafficUtils.currentTime());
			model.setType(type);
		} else {
			model.setTime(TrafficUtils.currentTime());
			model.setTraffic(data);
		}
		try {
			trafficDbUtils.saveOrUpdate(model);
		} catch (DbException e) {
			e.printStackTrace();
		}

	}

	public long getTrafficTotal(int type) {
		TrafficTotalModel model = null;
		try {
			model = trafficDbUtils.findFirst(Selector.from(
					TrafficTotalModel.class).where(COLUMN_TYPE, "=", type));
		} catch (DbException e) {
			e.printStackTrace();
		}
		if (model == null) {
			return 0L;
		} else {
			return model.getTraffic();
		}
	}

	public void setTrafficChange(int uid, long data) {
		TrafficModel model = new TrafficModel();
		model.setUid(uid);
		model.setRx(data);
		model.setTx(0L);
		model.setTime(TrafficUtils.currentTime());
		try {
			trafficDbUtils.save(model);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param time
	 */
	public long getTraffic(int uid, TrafficDate date, int time) {
		switch (date) {
		case DAY:
			try {
				if (time == 0) {
					time = getCurrentDay();
				}
				List<TrafficModel> datas = trafficDbUtils
						.findAll(Selector
								.from(TrafficModel.class)
								.where(COLUMN_UID, "=", uid)
								.and(COLUMN_TIME, "between",
										convertDayTimestamp(time)));
				return getTraffic(datas);
			} catch (DbException e) {
				e.printStackTrace();
			}
			break;
		case MONTH:
			try {
				if (time == 0) {
					time = getCurrentMonth();
				}
				List<TrafficModel> datas = trafficDbUtils.findAll(Selector
						.from(TrafficModel.class)
						.where(COLUMN_UID, "=", uid)
						.and(COLUMN_TIME, "between",
								convertMonthTimestamp(time)));
				return getTraffic(datas);
			} catch (DbException e) {
				e.printStackTrace();
			}
			break;
		case SEC:
			try {
				List<TrafficModel> datas = trafficDbUtils.findAll(Selector
						.from(TrafficModel.class)
						.where(COLUMN_TIME, "between",
								convertSecTimestamp(time))
						.and(WhereBuilder.b(COLUMN_UID, "=", UID_TOTAL_MOBILE)
								.or(COLUMN_UID, "=", UID_TOTAL_WIFI)));
				if (time == 0) {
					time = TrafficUtils.INTERVAL;
				}
				return getTraffic(datas) / time;
			} catch (DbException e) {
				e.printStackTrace();
			}
			break;
		}

		return 0L;
	}

	/**
	 * 获得指定时间戳当天的流量
	 * 
	 * @param uid
	 * @param time
	 * @return
	 */
	public long getTraffic(int uid, long time) {
		int[] dateDeparts = getDateDepart(time);
		int day = dateDeparts[2];
		List<TrafficModel> datas = null;
		try {
			datas = trafficDbUtils
					.findAll(Selector
							.from(TrafficModel.class)
							.where(COLUMN_UID, "=", uid)
							.and(COLUMN_TIME, "between",
									convertDayTimestamp(day, time)));
		} catch (DbException e) {
			e.printStackTrace();
		}
		return getTraffic(datas);
	}

	private long getTraffic(List<TrafficModel> datas) {
		long size = 0L;
		if (datas == null || datas.size() == 0) {
			return size;
		}

		Iterator<TrafficModel> it = datas.iterator();
		while (it.hasNext()) {
			TrafficModel model = it.next();
			size += model.getTx() + model.getRx();
		}
		return size;
	}

	private int getCurrentMonth() {
		int[] dateDeparts = getDateDepart(TrafficUtils.currentTime());
		return dateDeparts[1] + 1;
	}

	private int getCurrentDay() {
		int[] dateDeparts = getDateDepart(TrafficUtils.currentTime());
		return dateDeparts[2];
	}

	/**
	 * 得到time秒之前与当前时间之间的时间戳，time 0则为TrafficUtils.INTERVAL
	 * 
	 * @param time
	 * @return
	 */
	private long[] convertSecTimestamp(int time) {
		if (time == 0) {
			time = TrafficUtils.INTERVAL;
		}
		time++; // 为防止误差，多加
		long curTime = TrafficUtils.currentTime();
		long preTime = curTime - time * 1000; // 转成毫秒

		return new long[] { preTime, curTime };
	}

	/**
	 * 得到某一月份之间的时间戳
	 */
	private long[] convertMonthTimestamp(int month) {
		if (month > 12) {
			month = 12;
		} else if (month < 1) {
			month = 1;
		} else {
			month--;
		}
		long[] datas = new long[2];
		int[] dateDeparts = getDateDepart(TrafficUtils.currentTime());
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		c.set(Calendar.YEAR, dateDeparts[0]);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		datas[0] = c.getTimeInMillis();
		if (month == 11) {
			c.set(Calendar.YEAR, dateDeparts[0] + 1);
			c.set(Calendar.MONTH, 0);
		} else {
			c.set(Calendar.MONTH, month + 1);
		}
		datas[1] = c.getTimeInMillis();
		return datas;
	}

	private long[] convertDayTimestamp(int day) {
		return convertDayTimestamp(day, TrafficUtils.currentTime());
	}

	/**
	 * 得到某一天之间的时间
	 */
	private long[] convertDayTimestamp(int day, long time) {
		long[] datas = new long[2];
		int[] dateDeparts = getDateDepart(time);
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		c.set(Calendar.YEAR, dateDeparts[0]);
		c.set(Calendar.MONTH, dateDeparts[1]);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		datas[0] = c.getTimeInMillis();
		if (day == getDayInMonthMax(dateDeparts[1] + 1)) {
			if (dateDeparts[1] == 11) {
				c.set(Calendar.YEAR, dateDeparts[0] + 1);
				c.set(Calendar.MONTH, 0);
			} else {
				c.set(Calendar.MONTH, dateDeparts[1] + 1);
			}
			c.set(Calendar.DAY_OF_MONTH, 1);
		} else {
			c.set(Calendar.DAY_OF_MONTH, day + 1);
		}
		datas[1] = c.getTimeInMillis();
		return datas;
	}

	private int getDayInMonthMax(int month) {
		ArrayList<Integer> smallMonth = new ArrayList<Integer>();
		smallMonth.add(4);
		smallMonth.add(6);
		smallMonth.add(9);
		smallMonth.add(11);
		if (month == 2) {
			return 28;
		} else if (smallMonth.contains(month)) {
			return 30;
		} else {
			return 31;
		}
	}

	private long[] timeInMonth(long time) {
		long[] datas = new long[2];
		int[] dateDeparts = getDateDepart(time);
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		c.set(Calendar.YEAR, dateDeparts[0]);
		c.set(Calendar.MONTH, dateDeparts[1]);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		datas[0] = c.getTimeInMillis();
		if (dateDeparts[1] == 11) {
			c.set(Calendar.YEAR, dateDeparts[0] + 1);
			c.set(Calendar.MONTH, 0);
		} else {
			c.set(Calendar.YEAR, dateDeparts[0]);
			c.set(Calendar.MONTH, dateDeparts[1] + 1);
		}
		datas[1] = c.getTimeInMillis();
		return datas;
	}

	public static int[] getDateDepart(long timestamp) {
		int[] dateDepart = new int[7];
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		dateDepart[0] = calendar.get(Calendar.YEAR);
		dateDepart[1] = calendar.get(Calendar.MONTH);// 1月为0
		dateDepart[2] = calendar.get(Calendar.DAY_OF_MONTH);
		dateDepart[3] = calendar.get(Calendar.HOUR_OF_DAY);
		dateDepart[4] = calendar.get(Calendar.MINUTE);
		dateDepart[5] = calendar.get(Calendar.SECOND);
		dateDepart[6] = calendar.get(Calendar.MILLISECOND);
		return dateDepart;
	}
}
