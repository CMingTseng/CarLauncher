package com.tchip.carlauncher.controller;

import com.tchip.tachograph.TachographRecorder;

import android.util.Log;

public class EDriveRecorder extends TachographRecorder {

	private static final String TAG = "EDriveRecorder";

	public EDriveRecorder() {
		// TODO Auto-generated constructor stub
	}

//	@Override
//	public void onError(int err) {
//		Log.d(TAG, "onError err=" + err);
//		if (mTachographCallback != null) {
//			mTachographCallback.onError(err);
//		}
//	}
//
//	@Override
//	public void onFileSave(int type, String path) {
//		Log.d(TAG, "onFileSave type=" + type + ",path=" + path);
//		if (mTachographCallback != null) {
//			mTachographCallback.onFileSave(type, path);
//		}
//	}

	private TachographCallback mTachographCallback;

	public void setTachographCallback(TachographCallback tc) {
		mTachographCallback = tc;
	}

	public interface TachographCallback {
		public void onError(int err);

		public void onFileSave(int type, String path);
	}

}
