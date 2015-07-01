package com.tchip.carlauncher.model;

import java.text.SimpleDateFormat;
import java.util.Date;


import android.content.Context;


public class LZSVideoTableManager {
	private Context mContext;
	private LZSVideoTableDao mVideoTableDao;

	public LZSVideoTableManager(Context context) {
		this.mContext = context;
		mVideoTableDao = new LZSVideoTableDao(mContext);
	}
	
	public void addVideo(String path,long btime,  long etime)
	{
		LZSVideoTable videoTable = new LZSVideoTable();
		String[] strarray = path.split("\\/");
		String videoName = strarray[strarray.length - 1];
		String pathWithoutName = path.replace(videoName, "");
		
		SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
		String bTimeString = sDateFormat.format(new Date(btime));
		Integer bTimeUnix = (int)(btime/1000);
		String eTimeString = sDateFormat.format(new Date(etime));
		Integer eTimeUnix = (int)(etime/1000);
		
		videoTable.setName(videoName);
		videoTable.setPath(path);
		videoTable.setPath_withoutname(pathWithoutName);
		videoTable.setProtect(0);
		videoTable.setKeep_save(0);
		videoTable.setResolution("");
		videoTable.setDuration(getDurationTime((int)((etime - btime)/1000)));
		videoTable.setBtime(bTimeString);
		videoTable.setBtime_unix(bTimeUnix);
		videoTable.setEtime(eTimeString);
		videoTable.setEtime_unix(eTimeUnix);
		
		mVideoTableDao.addVideo(videoTable);
	}
	
	
	private String getDurationTime(int time_value)
	{
		if(time_value < 0)
		{
			return "";
		}
		String timeString = null;
		if(time_value < 60)
		{
			if(time_value < 10)
			{
				timeString = "00:0" + time_value;
			}
			else
			{
				timeString = "00:" + time_value;
			}
		}
		else
		{
			String time_min = null;
			String time_sec = null;
			if((time_value / 60)  < 10)
			{
				time_min = "0" + time_value/60;
			}
			else
			{
				time_min = "" + time_value/60;
			}
			
			if((time_value % 60) < 10)
			{
				time_sec = "0" + time_value%60;
			}
			else
			{
				time_sec = "" + time_value%60;
			}
			timeString =  time_min + ":" + time_sec;
		}
		return "" + timeString;
	}
	

}
