package com.tchip.carlauncher.model;

import android.hardware.Camera;

public interface LZSIRecorderControl {
	
	/**
	 * setup
	 */
	public void setup();
	
	/**
	 * release
	 */
	public void release();
	
	/**
	 * 开始录像
	 */
	public void startRecorder();
	
	/**
	 * 停止录像
	 */
	public void stopRecorder();
	
	/**
	 * 拍照
	 * @return 
	 */
	public int takePhoto();
	
	/**
	 * 设置录像分辨率
	 * @return true/false是否设置成功
	 */
	public boolean setResolution(int res);
	
	/**
	 * 设置分段保持录像时间
	 * @return true/false是否设置成功
	 */
	public boolean setSaveTime(int res);
	
	/**
	 * 设置碰撞检测灵敏度
	 * @return true/false是否设置成功
	 */
	public boolean setSensitivity(int res);
	
	//public void func(String path);

}




