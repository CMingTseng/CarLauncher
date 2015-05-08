package com.tchip.carlauncher.util;

public class DateUtil {

	public static String getWeekStrByInt(int week) {
		if (week > 7)
			week = week % 7;
		switch (week) {
		case 1:
			return "星期日";
		case 2:
			return "星期一";
		case 3:
			return "星期二";
		case 4:
			return "星期三";
		case 5:
			return "星期四";
		case 6:
			return "星期五";
		case 7:
			return "星期六";
		default:
			return "星期日";
		}
	}
}
