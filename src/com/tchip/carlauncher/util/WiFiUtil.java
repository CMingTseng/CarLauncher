package com.tchip.carlauncher.util;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.tchip.carlauncher.R;

public class WiFiUtil {

	public static final int MIN_RSSI = -100;
	public static final int MAX_RSSI = -55;
	public static final int RSSI_LEVELS = 5;

	public static int getImageBySignal(int signal) {

		int wifiLevel = calculateSignalLevel(signal, RSSI_LEVELS);
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
	public static int calculateSignalLevel(int rssi, int numLevels) {
		if (rssi <= MIN_RSSI) {
			return 0;
		} else if (rssi >= MAX_RSSI) {
			return numLevels - 1;
		} else {
			float inputRange = (MAX_RSSI - MIN_RSSI);
			float outputRange = (numLevels - 1);
			return (int) ((float) (rssi - MIN_RSSI) * outputRange / inputRange);
		}
	}

	public static String getConnectWifiBssid(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		android.net.wifi.WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return wifiInfo.getBSSID();
		// return wifiInfo.getSSID();
	}

}
