package com.tchip.carlauncher.util;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.tchip.carlauncher.R;

public class SignalUtil {

	public static final int WIFI_MIN_RSSI = -100;
	public static final int WIFI_MAX_RSSI = -55;
	public static final int WIFI_RSSI_LEVELS = 5;

	public static int getWifiImageBySignal(int signal) {

		int wifiLevel = calculateWifiSignalLevel(signal, WIFI_RSSI_LEVELS);
		switch (wifiLevel) {
		case -1:
			return R.drawable.ic_qs_wifi_no_network;
		case 0:
			return R.drawable.ic_qs_wifi_full_1;
		case 1:
			return R.drawable.ic_qs_wifi_full_2;
		case 2:
			return R.drawable.ic_qs_wifi_full_3;
		case 3:
			return R.drawable.ic_qs_wifi_full_4;
		case 4:
			return R.drawable.ic_qs_wifi_full_4;

		default:
			return R.drawable.ic_qs_wifi_no_network;
		}

	}

	/**
	 * Calculates the level of the signal. This should be used any time a signal
	 * is being shown.
	 * 
	 * @param rssi
	 *            The power of the signal measured in RSSI.
	 * @param numLevels
	 *            The number of levels to consider in the calculated level.
	 * @return A level of the signal, given in the range of 0 to numLevels-1
	 *         (both inclusive).
	 */
	public static int calculateWifiSignalLevel(int rssi, int numLevels) {
		if (rssi <= WIFI_MIN_RSSI) {
			return 0;
		} else if (rssi >= WIFI_MAX_RSSI) {
			return numLevels - 1;
		} else {
			float inputRange = (WIFI_MAX_RSSI - WIFI_MIN_RSSI);
			float outputRange = (numLevels - 1);
			return (int) ((float) (rssi - WIFI_MIN_RSSI) * outputRange / inputRange);
		}
	}

	public static String getConnectWifiBssid(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		android.net.wifi.WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return wifiInfo.getBSSID();
		// return wifiInfo.getSSID();
	}

	/**
	 * the GSM Signal Strength, valid values are (0-31, 99) as defined in TS
	 * 
	 * between 0..31, 99 is unknown
	 */
	public static final int SIGNAL_3G_MIN = 0;
	public static final int SIGNAL_3G_MAX = 31;
	public static final int SIGNAL_3G_LEVEL = 5;

	public static int get3GImageBySignal(int signal) {
		int signalLevel = calculate3GSignalLevel(signal);
		switch (signalLevel) {
		case -1:
			return R.drawable.ic_qs_signal_no_signal;
		case 0:
			return R.drawable.ic_qs_signal_full_0;
		case 1:
			return R.drawable.ic_qs_signal_full_1;
		case 2:
			return R.drawable.ic_qs_signal_full_2;
		case 3:
			return R.drawable.ic_qs_signal_full_3;
		case 4:
			return R.drawable.ic_qs_signal_full_4;

		default:
			return R.drawable.ic_qs_signal_no_signal;
		}
	}

	/**
	 * L4:23~31:8
	 * 
	 * L3:14~22:8
	 * 
	 * L2:07~13:6
	 * 
	 * L1:01~06:5
	 * 
	 * @param signal
	 * @return
	 */
	public static int calculate3GSignalLevel(int signal) {
		if (signal > 31 || signal < 0)
			return -1;
		else if (signal > 22)
			return 4;
		else if (signal > 13)
			return 3;
		else if (signal > 7)
			return 2;
		else if (signal > 0)
			return 1;
		else
			return -1;
	}

}
