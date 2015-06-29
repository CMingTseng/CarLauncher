package com.tchip.carlauncher.controller;

import java.io.File;

import com.tchip.carlauncher.dao.VideoTableDao;
import com.tchip.carlauncher.model.VideoTable;
import com.tchip.carlauncher.util.SdcardUtil;


import android.content.Context;
import android.util.Log;

public class ECircleController {
	
	private Context mContext;
	private VideoTableDao mVideoTableDao;
	
	public ECircleController(Context context)
	{
		mContext = context;
		mVideoTableDao = new VideoTableDao(context);
	}
	
	public void delFileBySDcardFull(String  SDCardPath)
	{
		this.delFileBySDcardFull(SDCardPath, 0.1f);
	}
	
	
	public void delFileBySDcardFull(String  SDCardPath, float availablePercent)
	{
		float SDTotalSize = SdcardUtil.getSDTotalSize(SDCardPath);
		float SDAvailableSize = SdcardUtil.getSDAvailableSize(SDCardPath);
		
		if(SDAvailableSize < SDTotalSize * availablePercent)
		{
			VideoTable videoTable = mVideoTableDao.secectNormalFirst();
			if(videoTable != null)
			{
				mVideoTableDao.delById(videoTable.getId().toString());
				File f = new File(videoTable.getPath());
				if (f.exists()) {
					f.delete();
				}
			}
		}
	}
	
	
	
	
	

}
