package com.tchip.carlauncher.model;

import java.io.File;

import com.tchip.carlauncher.util.LZSSdcardUtil;

import android.content.Context;
import android.util.Log;

public class LZSCircleController {

	private Context mContext;
	private LZSVideoTableDao mVideoTableDao;

	public LZSCircleController(Context context) {
		mContext = context;
		mVideoTableDao = new LZSVideoTableDao(context);
	}

	public void delFileBySDcardFull(String SDCardPath) {
		this.delFileBySDcardFull(SDCardPath, 0.1f);
	}

	public void delFileBySDcardFull(String SDCardPath, float availablePercent) {
		float SDTotalSize = LZSSdcardUtil.getSDTotalSize(SDCardPath);
		float SDAvailableSize = LZSSdcardUtil.getSDAvailableSize(SDCardPath);

		if (SDAvailableSize < SDTotalSize * availablePercent) {
			LZSVideoTable videoTable = mVideoTableDao.secectNormalFirst();
			if (videoTable != null) {
				mVideoTableDao.delById(videoTable.getId().toString());
				File f = new File(videoTable.getPath());
				if (f.exists()) {
					f.delete();
				}
			}
		}
	}

}
