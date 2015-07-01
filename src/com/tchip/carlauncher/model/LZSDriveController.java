package com.tchip.carlauncher.model;

import java.util.Date;

import com.tchip.carlauncher.Constant;
import com.tchip.tachograph.TachographCallback;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

public class LZSDriveController implements LZSIRecorderControl, TachographCallback {
	private static final String TAG = "EDriveController";
	private LZSDriveRecorder mEDriveRecorder;
	private Context mContext;
	private Camera mCamera;
	private SurfaceHolder mHolder;
	private LZSVideoTableManager mVideoTableManager;
	private Long tempBtime;

	public LZSDriveController(Context context, SurfaceHolder h) {
		mContext = context;
		mHolder = h;
		mVideoTableManager = new LZSVideoTableManager(mContext);
	}

	@Override
	public void startRecorder() {
		if (mEDriveRecorder != null) {
			mEDriveRecorder.start();
			tempBtime = System.currentTimeMillis();
		}
	}

	@Override
	public void stopRecorder() {
		if (mEDriveRecorder != null) {
			mEDriveRecorder.stop();
		}
	}

	@Override
	public int takePhoto() {
		Log.d(TAG, "takePhoto");
		if (mEDriveRecorder != null) {
			mEDriveRecorder.takePicture();
		}
		return 0;
	}

	@Override
	public boolean setResolution(int res) {
		Toast.makeText(mContext, "setResolution " + res, Toast.LENGTH_SHORT)
				.show();
		if (res == Constant.RESOLUTION.res720P) {
			release();
			if (openCamera()) {
				setupLowRecorder();
			}
		} else {
			release();
			if (openCamera()) {
				setupHighRecorder();
			}
		}
		return true;
	}

	@Override
	public boolean setSaveTime(int res) {
		Toast.makeText(mContext, "setSaveTime " + res, Toast.LENGTH_SHORT)
				.show();
		if (mEDriveRecorder == null)
			return false;
		if (Constant.SAVE_TIME.time_5min == res) {
			mEDriveRecorder.setVideoSeconds(50);
		} else {
			mEDriveRecorder.setVideoSeconds(30);
		}
		return true;
	}

	@Override
	public boolean setSensitivity(int res) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setup() {
		release();
		if (openCamera()) {
			setupHighRecorder();
		}
	}

	@Override
	public void release() {
		releaseRecorder();
		closeCamera();
	}

	/**
	 * 保存错误回调 1保存视频错误 2保存图片错误
	 */
	@Override
	public void onError(int err) {
		Toast.makeText(mContext, "Error : " + err, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 保存视频回调 1视频 2图片
	 */
	@Override
	public void onFileSave(int type, String path) {
		Toast.makeText(mContext, "Save " + path, Toast.LENGTH_SHORT).show();
		mVideoTableManager
				.addVideo(path, tempBtime, System.currentTimeMillis());
		tempBtime = System.currentTimeMillis();
	}

	private boolean openCamera() {
		if (mCamera != null) {
			closeCamera();
		}
		try {
			mCamera = Camera.open(0);
			mCamera.lock();
			Camera.Parameters para = mCamera.getParameters();
			para.unflatten(params_str);
			mCamera.setParameters(para);
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
			mCamera.unlock();
			return true;
		} catch (Exception ex) {
			closeCamera();
			return false;
		}
	}

	private boolean closeCamera() {
		if (mCamera == null)
			return true;
		try {
			mCamera.stopPreview();
			mCamera.setPreviewDisplay(null);
			mCamera.release();
			mCamera = null;
			return true;
		} catch (Exception ex) {
			mCamera = null;
			return false;
		}
	}

	private void setupLowRecorder() {
		releaseRecorder();
		mEDriveRecorder = new LZSDriveRecorder();
		mEDriveRecorder.setTachographCallback(this);
		mEDriveRecorder.setCamera(mCamera);
		// mEDriveRecorder.setPreviewSurface(mHolder.getSurface());
		mEDriveRecorder.setClientName(mContext.getPackageName());
		mEDriveRecorder.setVideoSize(1280, 720);
		mEDriveRecorder.setVideoFrameRate(30);
		mEDriveRecorder.setVideoBiteRate(3500000);
		mEDriveRecorder.prepare();
	}

	private void setupHighRecorder() {
		releaseRecorder();
		mEDriveRecorder = new LZSDriveRecorder();
		mEDriveRecorder.setTachographCallback(this);
		mEDriveRecorder.setCamera(mCamera);
		// mEDriveRecorder.setPreviewSurface(mHolder.getSurface());
		mEDriveRecorder.setClientName(mContext.getPackageName());
		mEDriveRecorder.setVideoSize(1920, 1080);
		mEDriveRecorder.setVideoFrameRate(30);
		mEDriveRecorder.setVideoBiteRate(8500000);
		mEDriveRecorder.prepare();
	}

	private void releaseRecorder() {
		if (mEDriveRecorder != null) {
			mEDriveRecorder.close();
			mEDriveRecorder.release();
			mEDriveRecorder = null;
		}
	}

	private String params_str = "zoom=0;fb-smooth-level-max=4;max-num-detected-faces-hw=15;cap-mode=normal;whitebalance=auto;afeng-min-focus-step=0;preview-format-values=yuv420sp,yuv420p,yuv420i-yyuvyy-3plane;rotation=0;jpeg-thumbnail-quality=100;preview-format=yuv420sp;iso-speed=auto;hue-values=low,middle,high;preview-frame-rate=30;jpeg-thumbnail-width=160;scene-mode-values=auto,portrait,landscape,night,night-portrait,theatre,beach,snow,sunset,steadyphoto,fireworks,sports,party,candlelight,hdr;video-size=1920x1088;preview-fps-range-values=(5000,60000);contrast-values=low,middle,high;preview-size-values=176x144,320x240,352x288,480x320,480x368,640x480,720x480,800x480,800x600,864x480,960x540,1280x720;auto-whitebalance-lock=false;preview-fps-range=5000,60000;antibanding=auto;min-exposure-compensation=-3;max-num-focus-areas=1;vertical-view-angle=49;fb-smooth-level-min=-4;eng-focus-fullscan-frame-interval=0;fb-skin-color=0;brightness_value=17;video-stabilization-supported=true;saturation-values=low,middle,high;eng-flash-duty-value=-1;edge=middle;iso-speed-values=auto,100,200,400,800,1600;picture-format-values=jpeg;exposure-compensation-step=1.0;eng-flash-duty-min=0;picture-size=2560x1440;saturation=middle;picture-format=jpeg;whitebalance-values=auto,incandescent,fluorescent,warm-fluorescent,daylight,cloudy-daylight,twilight,shade;afeng-max-focus-step=0;eng-shading-table=0;preferred-preview-size-for-video=1280x720;hue=middle;eng-focus-fullscan-frame-interval-max=65535;recording-hint=true;video-stabilization=false;zoom-supported=true;fb-smooth-level=0;fb-sharp=0;contrast=middle;eng-save-shading-table=0;jpeg-quality=90;scene-mode=auto;burst-num=1;metering-areas=(0,0,0,0,0);eng-flash-duty-max=1;video-size-values=176x144,480x320,640x480,864x480,1280x720,1920x1080;eng-focus-fullscan-frame-interval-min=0;focal-length=3.5;preview-size=1280x720;rec-mute-ogg=0;cap-mode-values=normal,face_beauty,continuousshot,smileshot,bestshot,evbracketshot,autorama,mav,asd;preview-frame-rate-values=15,24,30;max-num-metering-areas=9;fb-sharp-max=4;sensor-type=252;focus-mode-values=auto,macro,infinity,continuous-picture,continuous-video,manual,fullscan;fb-sharp-min=-4;jpeg-thumbnail-size-values=0x0,160x128,320x240;zoom-ratios=100,114,132,151,174,200,229,263,303,348,400;picture-size-values=320x240,640x480,1024x768,1280x720,1280x768,1280x960,1600x1200,2048x1536,2560x1440,2560x1920;edge-values=low,middle,high;horizontal-view-angle=53;brightness=middle;eng-flash-step-max=0;jpeg-thumbnail-height=128;capfname=/sdcard/DCIM/cap00;smooth-zoom-supported=true;zsd-mode=off;focus-mode=auto;auto-whitebalance-lock-supported=true;fb-skin-color-max=4;fb-skin-color-min=-4;max-num-detected-faces-sw=0;video-frame-format=yuv420p;max-exposure-compensation=3;focus-areas=(0,0,0,0,0);exposure-compensation=0;video-snapshot-supported=true;brightness-values=low,middle,high;auto-exposure-lock=false;effect-values=none,mono,negative,sepia,aqua,whiteboard,blackboard;eng-flash-step-min=0;effect=none;max-zoom=10;focus-distances=0.95,1.9,Infinity;mtk-cam-mode=2;auto-exposure-lock-supported=true;zsd-mode-values=off,on;antibanding-values=off,50hz,60hz,auto";

}
