
// onResume()

class Record {

	private void record() {
		// 重置预览区域
		if (mCamera == null) {
			// mHolder = holder;
			try {
				if (mMyRecorder != null) {
					mMyRecorder.stop();
					mMyRecorder.close();
					mMyRecorder.release();
					mMyRecorder = null;
					MyLog.d("Record Release");
				}
			} catch (Exception e) {
				MyLog.e("[MainActivity]releaseRecorder: Catch Exception!");
			}
			if (mCamera == null)
				return true;
			try {
				mCamera.lock();
				mCamera.stopPreview();
				mCamera.setPreviewDisplay(null);
				mCamera.release();
				mCamera.unlock();
				mCamera = null;
				return true;
			} catch (Exception ex) {
				mCamera = null;
				MyLog.e("[MainActivity]closeCamera:Catch Exception!");
				return false;
			}
			if (openCamera()) {
				try {
					if (mMyRecorder != null) {
						mMyRecorder.stop();
						mMyRecorder.close();
						mMyRecorder.release();
						mMyRecorder = null;
						MyLog.d("Record Release");
					}
				} catch (Exception e) {
					MyLog.e("[MainActivity]releaseRecorder: Catch Exception!");
				}
				try {
					mMyRecorder = new TachographRecorder();
					mMyRecorder.setTachographCallback(this);
					mMyRecorder.setCamera(mCamera);
					mMyRecorder.setClientName(this.getPackageName());
					if (mResolutionState == Constant.Record.STATE_RESOLUTION_1080P) {
						mMyRecorder.setVideoSize(1920, 1088); // 16倍数
						mMyRecorder.setVideoFrameRate(30);
						mMyRecorder.setVideoBiteRate(5500000 * 2); // 8500000
					} else {
						mMyRecorder.setVideoSize(1280, 720);
						mMyRecorder.setVideoFrameRate(30);
						mMyRecorder.setVideoBiteRate(5500000); // 3500000
					}
					if (mSecondaryState == Constant.Record.STATE_SECONDARY_ENABLE) {
						mMyRecorder.setSecondaryVideoEnable(true);
						mMyRecorder.setSecondaryVideoSize(320, 240);
						mMyRecorder.setSecondaryVideoFrameRate(30);
						mMyRecorder.setSecondaryVideoBiteRate(120000);
					} else {
						mMyRecorder.setSecondaryVideoEnable(false);
					}
					if (mIntervalState == Constant.Record.STATE_INTERVAL_1MIN) {
						mMyRecorder.setVideoSeconds(1 * 60);
					} else {
						mMyRecorder.setVideoSeconds(3 * 60);
					}
					if (mOverlapState == Constant.Record.STATE_OVERLAP_FIVE) {
						mMyRecorder.setVideoOverlap(5);
					} else {
						mMyRecorder.setVideoOverlap(0);
					}
					mMyRecorder.prepare();
				} catch (Exception e) {
					MyLog.e("[MainActivity]setupRecorder: Catch Exception!");
				}
			}
		} else {
			try {
				mCamera.lock();
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();
				mCamera.unlock();
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
	}

	public int startRecordTask() {
		if (mMyRecorder != null) {
			if (StorageUtil.deleteOldestUnlockVideo(MainActivity.this)) {

				MyLog.d("Record Start");
				// 设置保存路径
				if (Constant.Record.saveVideoToSD2) {
					setDirectory(Constant.Path.SDCARD_2);
				} else {
					setDirectory(Constant.Path.SDCARD_1);
				}

				// 设置录像静音
				boolean videoMute = sharedPreferences.getBoolean("videoMute",
						Constant.Record.muteDefault);
				if (videoMute) {
					mMuteState = Constant.Record.STATE_MUTE; // 不录音
					setMute(true);
				} else {
					mMuteState = Constant.Record.STATE_UNMUTE;
					setMute(false);
				}

				AudioPlayUtil.playAudio(getApplicationContext(),
						FILE_TYPE_VIDEO);
				return mMyRecorder.start();
			}
		}
		return -1;
	}

}