package com.tchip.carlauncher.util;

public class ClickUtil {

	/**
	 * 两次点击至少间隔时间,单位:ms
	 */
	public static int clickMinSpan = 800;

	private static long lastClickTime;

	public static boolean isQuickClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < clickMinSpan) {
			return true;
		}
		lastClickTime = time;
		return false;
	}
}
