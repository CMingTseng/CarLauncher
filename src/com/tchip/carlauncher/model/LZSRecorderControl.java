package com.tchip.carlauncher.model;


import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.widget.Toast;

public class LZSRecorderControl implements LZSIRecorderControl{
	public Context mContext;
	
	
	public LZSRecorderControl(Context context) {
		super();
		mContext = context;
	}

	@Override
	public void startRecorder() {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, "开始录像", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void stopRecorder() {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, "停止录像", Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public int takePhoto() {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, "拍照", Toast.LENGTH_SHORT).show();
		return 0;
	}

	/**
	 * 设置录像分辨率
	 * @param /Constant.RESOLUTION.res1080P/Constant.RESOLUTION.res720P
	 * @return true/false是否设置成功
	 */
	@Override
	public boolean setResolution(int res) {
		// TODO Auto-generated method stub
		Log.v("tag", "======>设置分辨率:" + res);
		return false;
	}

	/**
	 * 设置分段保持录像时间
	 * @param /Constant.SAVE_TIME.time_3min/Constant.SAVE_TIME.time_5min
	 * @return true/false是否设置成功
	 */
	@Override
	public boolean setSaveTime(int res) {
		// TODO Auto-generated method stub
		Log.v("tag", "======>设置保存时间:" + res);
		return false;
	}

	/**
	 * 设置碰撞检测灵敏度
	 * @param /Constant.SENSITIVITY.sen_high/Constant.SENSITIVITY.sen_min/Constant.SENSITIVITY.sen_low/
	 * @return true/false是否设置成功
	 */
	@Override
	public boolean setSensitivity(int res) {
		// TODO Auto-generated method stub
		Log.v("tag", "======>设置灵敏度:" + res);
		return false;
	}

	@Override
	public void setup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		
	}
	

}
