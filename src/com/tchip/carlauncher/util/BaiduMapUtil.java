package com.tchip.carlauncher.util;

import java.io.File;

import com.tchip.carlauncher.Constant;

public class BaiduMapUtil {

	/**
	 * 判断是否存在离线地图，弊端，不能判断是否存在当前城市的离线地图
	 * 
	 * @return
	 */

	public static boolean hasOfflineMap() {

		File file = new File(Constant.Path.BAIDU_OFFLINE_SUB);
		int fileCount = file.list().length;
		if (fileCount > 3)
			return true;
		else
			return false;
	}

}
