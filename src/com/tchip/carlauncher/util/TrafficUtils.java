package com.tchip.carlauncher.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.tchip.carlauncher.model.TrafficAppModel;
import com.tchip.carlauncher.model.TrafficDateTrafficModel;
import com.tchip.carlauncher.model.TrafficDbManager;
import com.tchip.carlauncher.model.TrafficDbManager.TrafficDate;
import com.tchip.carlauncher.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TrafficUtils {
	/**
	 * 获取流量间隔时间（秒�?
	 */
	public static final int INTERVAL = 5;

	/**
	 * �?有流量和每天的流量的数据的下�?
	 */
	public static final int INDEX_TOTAL_MOBILE = 0;
	public static final int INDEX_DAY_MOBILE = 1;
	public static final int INDEX_TOTAL_WIFI = 2;
	public static final int INDEX_DAY_WIFI = 3;

	/**
	 * 获取流量数据的广播Action
	 * 
	 * @param ctx
	 * @return
	 */
	public static final String ACTION_UPDATE_TRAFFIC = "action_update_traffic";

	public static String netWorkTypeToString(Context ctx) {
		ConnectivityManager manager = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			String type = networkInfo.getTypeName();
			if (type.equalsIgnoreCase("WIFI")) {
				return ctx.getString(R.string.wifi);
			} else if (type.equalsIgnoreCase("MOBILE")) {
				String proxyHost = android.net.Proxy.getDefaultHost();
				return TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(ctx) ? ctx
						.getString(R.string._3g_above) : ctx
						.getString(R.string._2g))
						: ctx.getString(R.string.wap);
			}
		}
		return ctx.getString(R.string.invalid_network);

	}

	private static boolean isFastMobileNetwork(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		switch (telephonyManager.getNetworkType()) {
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return false; // ~ 50-100 kbps
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return false; // ~ 14-64 kbps
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return false; // ~ 50-100 kbps
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			return true; // ~ 400-1000 kbps
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			return true; // ~ 600-1400 kbps
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return false; // ~ 100 kbps
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return true; // ~ 2-14 Mbps
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return true; // ~ 700-1700 kbps
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			return true; // ~ 1-23 Mbps
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return true; // ~ 400-7000 kbps
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			return true; // ~ 1-2 Mbps
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			return true; // ~ 5 Mbps
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return true; // ~ 10-20 Mbps
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return false; // ~25 kbps
		case TelephonyManager.NETWORK_TYPE_LTE:
			return true; // ~ 10+ Mbps
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			return false;
		default:
			return false;
		}
	}

	public static String dataSizeFormat(long size) {
		DecimalFormat formater = new DecimalFormat("#.#");
		double size1 = (double) size;
		if (size1 < 1024 * 1024) {
			double kSize = (size1 / 1024);
			return formater.format(kSize) + "KB";
		} else if (size1 < 1024 * 1024 * 1024) {
			double mSize = size1 / (1024 * 1024);
			return formater.format(mSize) + "MB";
		} else {
			double gSize = size1 / (1024 * 1024 * 1024);
			return formater.format(gSize) + "GB";
		}

		// else if (size1 < 1024 * 1024 * 1024 * 1024) {
		// double gSize = size1 / (1024 * 1024 * 1024);
		// return formater.format(gSize) + "GB";
		// }

	}

	public static float dataToMB(long size) {
		DecimalFormat formater = new DecimalFormat("#.#");
		double size1 = (double) size;
		double mSize = size1 / (1024 * 1024);
		return Float.parseFloat(formater.format(mSize));
	}

	public static long[] getMobileAndWifiData(Context ctx) {
		long[] datas = new long[4];
		datas[INDEX_TOTAL_MOBILE] = TrafficDbManager.getInstance(ctx).getTraffic(
				TrafficDbManager.UID_TOTAL_MOBILE, TrafficDbManager.TrafficDate.MONTH, 0);
		datas[INDEX_TOTAL_WIFI] = TrafficDbManager.getInstance(ctx).getTraffic(
				TrafficDbManager.UID_TOTAL_WIFI, TrafficDbManager.TrafficDate.MONTH, 0);
		datas[INDEX_DAY_MOBILE] = TrafficDbManager.getInstance(ctx).getTraffic(
				TrafficDbManager.UID_TOTAL_MOBILE, TrafficDbManager.TrafficDate.DAY, 0);
		datas[INDEX_DAY_WIFI] = TrafficDbManager.getInstance(ctx).getTraffic(
				TrafficDbManager.UID_TOTAL_WIFI, TrafficDbManager.TrafficDate.DAY, 0);
		return datas;
	}

	public static List<TrafficAppModel> getAllAppTraffic(Context ctx) {
		List<TrafficAppModel> datas = new ArrayList<TrafficAppModel>();
		PackageManager packageManager = ctx.getPackageManager();
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(
				intent, 0);
		for (ResolveInfo resolveInfo : resolveInfos) {
			String packageName = resolveInfo.activityInfo.packageName;
			String name = resolveInfo.loadLabel(packageManager).toString();
			int uid;
			try {
				PackageInfo packageInfo = packageManager.getPackageInfo(
						packageName, 0);
				uid = packageInfo.applicationInfo.uid;
				long traffic = TrafficDbManager.getInstance(ctx).getTraffic(uid,
						TrafficDbManager.TrafficDate.MONTH, 0);
				if (traffic == 0)
					continue;
				TrafficAppModel model = new TrafficAppModel();
				model.setTraffic(traffic);
				model.setPkgName(packageName);
				model.setUid(uid);
				model.setAppName(name);
				datas.add(model);
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		Collections.sort(datas, new Comparator<TrafficAppModel>() {
			@Override
			public int compare(TrafficAppModel lhs, TrafficAppModel rhs) {
				return (int) (rhs.getTraffic() - lhs.getTraffic());
			}
		});
		return datas;
	}

	/**
	 * 获得每秒钟的流量数据
	 */
	public static long getTrafficPerSec(Context ctx) {

		long perSecData = TrafficDbManager.getInstance(ctx).getTraffic(0,
				TrafficDbManager.TrafficDate.SEC, 0);
		if (perSecData < 0) {
			perSecData = 0;
		}
		return perSecData;
	}

	public static void fetchTraffic(Context ctx) {

		fetchTotalTraffic(ctx);
		fetchAppTraffic(ctx);

	}

	private static void fetchTotalTraffic(Context ctx) {
		long mobileData = TrafficStats.getMobileRxBytes()
				+ TrafficStats.getMobileTxBytes();
		long wifiData = TrafficStats.getTotalRxBytes()
				+ TrafficStats.getTotalTxBytes() - mobileData;

		long mobileDbData = TrafficDbManager.getInstance(ctx).getTrafficTotal(
				TrafficDbManager.UID_TOTAL_MOBILE);
		long wifiDbData = TrafficDbManager.getInstance(ctx).getTrafficTotal(
				TrafficDbManager.UID_TOTAL_WIFI);

		if (mobileData - mobileDbData > 2047) {
			TrafficDbManager.getInstance(ctx).setTrafficTotal(
					TrafficDbManager.UID_TOTAL_MOBILE, mobileData);
			TrafficDbManager.getInstance(ctx).setTrafficChange(
					TrafficDbManager.UID_TOTAL_MOBILE, mobileData - mobileDbData);
			Log.i("TAG", "TrafficUtils:fetchTotalTraffic() 更新手机数据完成");
		} else {
			Log.i("TAG", "TrafficUtils:fetchTotalTraffic() 流量变化小于2KB，不更新手机数据");
		}

		if (wifiData - wifiDbData > 2047) {
			TrafficDbManager.getInstance(ctx).setTrafficTotal(
					TrafficDbManager.UID_TOTAL_WIFI, wifiData);
			TrafficDbManager.getInstance(ctx).setTrafficChange(
					TrafficDbManager.UID_TOTAL_WIFI, wifiData - wifiDbData);
			Log.i("TAG", "TrafficUtils:fetchTotalTraffic() 更新WIFI数据完成");
		} else {
			Log.i("TAG", "TrafficUtils:fetchTotalTraffic() 流量变化小于2KB，不更新WIFI数据");
		}
	}

	private static void fetchAppTraffic(Context ctx) {
		PackageManager packageManager = ctx.getPackageManager();
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(
				intent, 0);
		int count = 0;
		for (ResolveInfo resolveInfo : resolveInfos) {
			String packageName = resolveInfo.activityInfo.packageName;
			String name = resolveInfo.loadLabel(packageManager).toString();
			int uid;
			try {
				PackageInfo packageInfo = packageManager.getPackageInfo(
						packageName, 0);
				uid = packageInfo.applicationInfo.uid;
				long received = TrafficStats.getUidRxBytes(uid);
				long transmitted = TrafficStats.getUidTxBytes(uid);
				long total = received + transmitted;
				if (total == 0) {
					continue;
				}
				long totalInDb = TrafficDbManager.getInstance(ctx)
						.getTrafficTotal(uid);
				if (total - totalInDb > 2047) {
					TrafficDbManager.getInstance(ctx).setTrafficTotal(uid, total);
					TrafficDbManager.getInstance(ctx).setTrafficChange(uid,
							total - totalInDb);
					Log.i("TAG", "TrafficUtils:fetchAppTraffic() 更新应用数据�?"
							+ name);
					count++;
				}
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		if (count == 0) {
			Log.i("TAG", "TrafficUtils:fetchAppTraffic() 没有应用更新数据");
		} else {
			Log.i("TAG", "TrafficUtils:fetchAppTraffic() 更新应用数据完成�?" + count
					+ "/" + resolveInfos.size() + ")");
		}
	}

	/**
	 * @return 指定uid的day前的数据（包括今天）
	 */
	public static List<TrafficDateTrafficModel> fetchDayTrrafic(Context ctx, int uid, int day) {
                long[] days = getDaysTimestamp(day);
                List<TrafficDateTrafficModel> datas = new ArrayList<>();
                for (int i = 0; i < day; i++) {
                        TrafficDateTrafficModel model = new TrafficDateTrafficModel();
                        model.setDate(days[i]);
                        long traffic = TrafficDbManager.getInstance(ctx)
                                .getTraffic(uid, days[i]);
                        model.setTraffic(traffic);
                        datas.add(model);
                }
                return datas;
        }

	private static long[] getDaysTimestamp(int day) {
		long[] days = new long[day];
		long cur = currentTime();
		for (int i = 1; i <= day; i++) {
			days[day - i] = cur - (i - 1) * 24 * 60 * 60 * 1000;
		}
		return days;
	}

	public static long currentTime() {
		return System.currentTimeMillis();
	}

	/**
	 * 定时更新流量的服�?
	 */
	public static void startRepeatingService(Context context, int seconds,
			Class<?> cls, String action) {
		AlarmManager manager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, cls);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		long triggerAtTime = SystemClock.elapsedRealtime();
		manager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime,
				seconds * 1000, pendingIntent);
		Log.i("TAG", "TrafficUtils:startRepeatingService() 设定定时获取流量");
	}

	/**
	 * 停止定时更新
	 */
	public static void stopRepeatingService(Context context, Class<?> cls,
			String action) {
		AlarmManager manager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, cls);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		manager.cancel(pendingIntent);
	}

}
