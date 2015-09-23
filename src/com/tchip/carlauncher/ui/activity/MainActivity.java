package com.tchip.carlauncher.ui.activity;

import java.io.File;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.MyApplication;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.lib.filemanager.FolderActivity;
import com.tchip.carlauncher.model.DriveVideo;
import com.tchip.carlauncher.model.DriveVideoDbHelper;
import com.tchip.carlauncher.model.Typefaces;
import com.tchip.carlauncher.service.RouteRecordService;
import com.tchip.carlauncher.service.SensorWatchService;
import com.tchip.carlauncher.service.SpeakService;
import com.tchip.carlauncher.service.WeatherService;
import com.tchip.carlauncher.util.AudioPlayUtil;
import com.tchip.carlauncher.util.ClickUtil;
import com.tchip.carlauncher.util.DateUtil;
import com.tchip.carlauncher.util.MyLog;
import com.tchip.carlauncher.util.NetworkUtil;
import com.tchip.carlauncher.util.SettingUtil;
import com.tchip.carlauncher.util.StorageUtil;
import com.tchip.carlauncher.util.SignalUtil;
import com.tchip.carlauncher.view.AudioRecordDialog;
import com.tchip.tachograph.TachographCallback;
import com.tchip.tachograph.TachographRecorder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements TachographCallback,
		Callback {

	private SharedPreferences sharedPreferences;
	private Editor editor;

	private DriveVideoDbHelper videoDb;

	private SurfaceView surfaceCamera;
	private boolean isSurfaceLarge = false;

	private ImageView smallVideoRecord, smallVideoLock, smallVideoCamera;
	private RelativeLayout layoutLargeButton, layoutMap;
	private TextView textRecordTime;

	private ImageView imageWifiLevel; // WiFi状态图标
	private IntentFilter wifiIntentFilter; // WiFi状态监听器

	private ImageView imageShadowRight, imageShadowLeft;

	private HorizontalScrollView hsvMain;

	// Record
	private ImageView largeVideoSize, largeVideoTime, largeVideoLock,
			largeVideoMute, largeVideoRecord, largeVideoCamera;
	private SurfaceHolder mHolder;
	private TachographRecorder mMyRecorder;
	private Camera mCamera;

	private int mResolutionState, mRecordState, mIntervalState, mPathState,
			mSecondaryState, mOverlapState, mMuteState;

	private LinearLayout layoutVideoSize, layoutVideoTime, layoutVideoLock,
			layoutVideoMute, layoutVideoRecord, layoutVideoCamera,
			layoutVideoRecordSmall, layoutVideoCameraSmall,
			layoutVideoLockSmall;

	private ImageView imageSignalLevel, image3G;

	private TelephonyManager Tel;
	private int simState;
	private MyPhoneStateListener MyListener;

	private AudioRecordDialog audioRecordDialog;

	private WifiManager wifiManager;
	private ConnectivityManager connManager;
	private NetworkInfo mWifi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setBackgroundDrawable(null);
		setContentView(R.layout.activity_main);

		sharedPreferences = getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();

		videoDb = new DriveVideoDbHelper(getApplicationContext());

		// Dialog
		audioRecordDialog = new AudioRecordDialog(MainActivity.this);

		initialLayout();
		initialCameraButton();
		// 录像：配置参数，初始化布局
		setupRecordDefaults();
		setupRecordViews();

		// 序列任务线程
		new Thread(new AutoThread()).start();

		// 后台线程
		new Thread(new BackThread()).start();

		// 3G信号
		MyListener = new MyPhoneStateListener();
		Tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		// SIM卡状态
		simState = Tel.getSimState();
		Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

		// 注册wifi消息处理器
		registerReceiver(wifiIntentReceiver, wifiIntentFilter);

		// 初始化自动亮度开关
		initialAutoLight();
	}

	/**
	 * 序列任务线程，分步执行
	 */
	public class AutoThread implements Runnable {

		@Override
		public void run() {
			try {
				// 初次启动清空录像文件夹
				if (StorageUtil.isVideoCardExists()
						&& sharedPreferences.getBoolean("isFirstLaunch", true)) {
					String sdcardPath = Constant.Path.SDCARD_1 + File.separator; // "/storage/sdcard1/";
					if (Constant.Record.saveVideoToSD2) {
						sdcardPath = Constant.Path.SDCARD_2 + File.separator; // "/storage/sdcard2/";
					}

					File file = new File(sdcardPath + "tachograph/");
					StorageUtil.RecursionDeleteFile(file);
					MyLog.e("Delete video directory:tachograph !!!");

					editor.putBoolean("isFirstLaunch", false);
					editor.commit();
				} else {
					MyLog.e("Video card not exist or isn't first launch");
				}

				// 检查并删除异常视频文件
				if (StorageUtil.isVideoCardExists()
						&& !MyApplication.isVideoReording) {
					CheckErrorFile();
				}

				// 自动录像:如果已经在录像则不处理
				if (Constant.Record.autoRecord
						&& !MyApplication.isVideoReording) {
					Thread.sleep(Constant.Record.autoRecordDelay);
					Message message = new Message();
					message.what = 1;
					autoHandler.sendMessage(message);
				}
				// 启动服务
				Thread.sleep(3000);
				initialService();

			} catch (InterruptedException e) {
				e.printStackTrace();
				MyLog.e("[MainActivity]AutoThread: Catch Exception!");
			}
		}
	}

	final Handler autoHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				startOrStopRecord();
				break;

			default:
				break;
			}
		}
	};

	public class BackThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);
					if (!MyApplication.isSleeping) {
						Message message = new Message();
						message.what = 1;
						backHandler.sendMessage(message);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	final Handler backHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (!MyApplication.isSleeping) {
					if (NetworkUtil.isWifiConnected(getApplicationContext())) {
						updateWiFiState();
					}

					if (MyApplication.shouldWakeRecord) {
						// 序列任务线程
						new Thread(new AutoThread()).start();
						MyApplication.shouldWakeRecord = false;
					}
				}
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 更改分辨率后重启录像
	 */
	public class StartRecordWhenChangeSizeThread implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(1000);
				Message message = new Message();
				message.what = 1;
				startRecordWhenChangeSize.sendMessage(message);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 更改录音/静音状态后重启录像
	 */
	public class StartRecordWhenChangeMuteThread implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(500);
				Message message = new Message();
				message.what = 1;
				startRecordWhenChangeSize.sendMessage(message);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	final Handler startRecordWhenChangeSize = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				startOrStopRecord();
				break;

			default:
				break;
			}
		}
	};

	private class MyPhoneStateListener extends PhoneStateListener {

		/**
		 * 更新3G信号强度
		 */
		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			super.onSignalStrengthsChanged(signalStrength);
			update3GState(signalStrength.getGsmSignalStrength());
		}

		@Override
		public void onDataConnectionStateChanged(int state) {

			switch (state) {
			case TelephonyManager.DATA_DISCONNECTED:// 网络断开
				MyLog.v("3G TelephonyManager.DATA_DISCONNECTED");
				image3G.setVisibility(View.GONE);
				break;

			case TelephonyManager.DATA_CONNECTING:// 网络正在连接
				MyLog.v("3G TelephonyManager.DATA_CONNECTING");
				image3G.setVisibility(View.VISIBLE);
				break;

			case TelephonyManager.DATA_CONNECTED:// 网络连接上
				MyLog.v("3G TelephonyManager.DATA_CONNECTED");
				image3G.setVisibility(View.VISIBLE);
				break;
			}
		}
	}

	/**
	 * 更新3G状态
	 * 
	 * SIM_STATE_UNKNOWN = 0:Unknown.
	 * 
	 * SIM_STATE_ABSENT = 1:no SIM card is available in the device
	 * 
	 * SIM_STATE_PIN_REQUIRED = 2:requires the user's SIM PIN to unlock
	 * 
	 * SIM_STATE_PUK_REQUIRED = 3:requires the user's SIM PUK to unlock
	 * 
	 * SIM_STATE_NETWORK_LOCKED = 4:requires a network PIN to unlock
	 * 
	 * SIM_STATE_READY = 5:Ready
	 * 
	 */
	private void update3GState(int signal) {
		// imageSignalLevel,image3G.setVisibility(View.GONE);
		simState = Tel.getSimState();
		MyLog.v("SIM State:" + simState);
		if (simState == TelephonyManager.SIM_STATE_READY) {

			imageSignalLevel.setBackground(getResources().getDrawable(
					SignalUtil.get3GImageBySignal(signal)));
			if (signal > 0 && signal < 31)
				image3G.setVisibility(View.VISIBLE);
			else
				image3G.setVisibility(View.GONE);
		} else if (simState == TelephonyManager.SIM_STATE_UNKNOWN
				|| simState == TelephonyManager.SIM_STATE_ABSENT) {
			image3G.setVisibility(View.GONE);
			imageSignalLevel.setBackground(getResources().getDrawable(
					R.drawable.ic_qs_signal_no_signal));
		}
	}

	/**
	 * 初始化服务
	 */
	private void initialService() {

		// 轨迹记录服务
		Intent intentRoute = new Intent(this, RouteRecordService.class);
		startService(intentRoute);

		// 碰撞侦测服务
		Intent intentSensor = new Intent(this, SensorWatchService.class);
		startService(intentSensor);
	}

	private void initialCameraSurface() {
		if (Constant.Record.hasCamera) {
			surfaceCamera = (SurfaceView) findViewById(R.id.surfaceCamera);
			surfaceCamera.setOnClickListener(new MyOnClickListener());
			surfaceCamera.getHolder().addCallback(this);
		}
	}

	/**
	 * 初始化布局
	 */
	private void initialLayout() {
		// 录像窗口
		initialCameraSurface();

		textRecordTime = (TextView) findViewById(R.id.textRecordTime);

		// 增大点击区域
		layoutVideoSize = (LinearLayout) findViewById(R.id.layoutVideoSize);
		layoutVideoSize.setOnClickListener(new MyOnClickListener());

		layoutVideoTime = (LinearLayout) findViewById(R.id.layoutVideoTime);
		layoutVideoTime.setOnClickListener(new MyOnClickListener());

		layoutVideoLock = (LinearLayout) findViewById(R.id.layoutVideoLock);
		layoutVideoLock.setOnClickListener(new MyOnClickListener());

		layoutVideoMute = (LinearLayout) findViewById(R.id.layoutVideoMute);
		layoutVideoMute.setOnClickListener(new MyOnClickListener());

		layoutVideoRecord = (LinearLayout) findViewById(R.id.layoutVideoRecord);
		layoutVideoRecord.setOnClickListener(new MyOnClickListener());

		layoutVideoCamera = (LinearLayout) findViewById(R.id.layoutVideoCamera);
		layoutVideoCamera.setOnClickListener(new MyOnClickListener());

		layoutVideoRecordSmall = (LinearLayout) findViewById(R.id.layoutVideoRecordSmall);
		layoutVideoRecordSmall.setOnClickListener(new MyOnClickListener());

		layoutVideoCameraSmall = (LinearLayout) findViewById(R.id.layoutVideoCameraSmall);
		layoutVideoCameraSmall.setOnClickListener(new MyOnClickListener());

		layoutVideoLockSmall = (LinearLayout) findViewById(R.id.layoutVideoLockSmall);
		layoutVideoLockSmall.setOnClickListener(new MyOnClickListener());

		// 天气预报和时钟,状态图标
		RelativeLayout layoutWeather = (RelativeLayout) findViewById(R.id.layoutWeather);
		layoutWeather.setOnClickListener(new MyOnClickListener());
		TextClock textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setTypeface(Typefaces.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf"));

		TextClock textDate = (TextClock) findViewById(R.id.textDate);
		textDate.setTypeface(Typefaces.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf"));

		TextClock textWeek = (TextClock) findViewById(R.id.textWeek);
		textWeek.setTypeface(Typefaces.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf"));

		LinearLayout layoutWiFi = (LinearLayout) findViewById(R.id.layoutWiFi);
		layoutWiFi.setOnClickListener(new MyOnClickListener());

		// WiFi状态信息
		imageWifiLevel = (ImageView) findViewById(R.id.imageWifiLevel);

		wifiIntentFilter = new IntentFilter();
		wifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		wifiIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		wifiIntentFilter.setPriority(Integer.MAX_VALUE);
		updateWiFiState();

		// 3G状态信息
		imageSignalLevel = (ImageView) findViewById(R.id.imageSignalLevel);
		image3G = (ImageView) findViewById(R.id.image3G);
		image3G.setVisibility(View.GONE);

		// 导航
		ImageView imageNavi = (ImageView) findViewById(R.id.imageNavi);
		imageNavi.setOnClickListener(new MyOnClickListener());

		// 在线音乐
		ImageView imageMusicOL = (ImageView) findViewById(R.id.imageMusicOL);
		imageMusicOL.setOnClickListener(new MyOnClickListener());

		// 电子狗
		ImageView imageEDog = (ImageView) findViewById(R.id.imageEDog);
		imageEDog.setOnClickListener(new MyOnClickListener());

		RelativeLayout layoutEDog = (RelativeLayout) findViewById(R.id.layoutEDog);
		if (Constant.Module.hasEDog) {
			layoutEDog.setVisibility(View.VISIBLE);
		} else {
			layoutEDog.setVisibility(View.GONE);
		}

		// 多媒体
		ImageView imageMultimedia = (ImageView) findViewById(R.id.imageMultimedia);
		imageMultimedia.setOnClickListener(new MyOnClickListener());

		// 文件管理
		ImageView imageFileExplore = (ImageView) findViewById(R.id.imageFileExplore);
		imageFileExplore.setOnClickListener(new MyOnClickListener());

		RelativeLayout layoutFileExplore = (RelativeLayout) findViewById(R.id.layoutFileExplore);
		if (Constant.Module.hasFileManager) {
			layoutFileExplore.setVisibility(View.VISIBLE);
		} else {
			layoutFileExplore.setVisibility(View.GONE);
		}

		// 语音助手
		ImageView imageVoiceChat = (ImageView) findViewById(R.id.imageVoiceChat);
		imageVoiceChat.setOnClickListener(new MyOnClickListener());
		RelativeLayout layoutVoiceChat = (RelativeLayout) findViewById(R.id.layoutVoiceChat);
		if (Constant.Module.hasVoiceChat) {
			layoutVoiceChat.setVisibility(View.VISIBLE);
		} else {
			layoutVoiceChat.setVisibility(View.GONE);
		}

		// 行驶轨迹
		ImageView imageRouteTrack = (ImageView) findViewById(R.id.imageRouteTrack);
		imageRouteTrack.setOnClickListener(new MyOnClickListener());

		// FM发射
		ImageView imageFmTransmit = (ImageView) findViewById(R.id.imageFmTransmit);
		imageFmTransmit.setOnClickListener(new MyOnClickListener());

		if (Constant.Module.hasDialer) {
			// 拨号
			ImageView imageDialer = (ImageView) findViewById(R.id.imageDialer);
			imageDialer.setOnClickListener(new MyOnClickListener());

			// 短信
			ImageView imageMessage = (ImageView) findViewById(R.id.imageMessage);
			imageMessage.setOnClickListener(new MyOnClickListener());
		}

		// 设置
		ImageView imageSetting = (ImageView) findViewById(R.id.imageSetting);
		imageSetting.setOnClickListener(new MyOnClickListener());
		RelativeLayout layoutSetting = (RelativeLayout) findViewById(R.id.layoutSetting);
		if (Constant.Module.hasSetting) {
			layoutSetting.setVisibility(View.VISIBLE);
		} else {
			layoutSetting.setVisibility(View.GONE);
		}

		// HorizontalScrollView，左右两侧阴影
		imageShadowLeft = (ImageView) findViewById(R.id.imageShadowLeft);
		imageShadowRight = (ImageView) findViewById(R.id.imageShadowRight);

		hsvMain = (HorizontalScrollView) findViewById(R.id.hsvMain);
		hsvMain.setDrawingCacheEnabled(true);
		if (Constant.Module.isHsvTouch) {
			hsvMain.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_MOVE:
						View childFirst = ((HorizontalScrollView) v)
								.getChildAt(0);

						// 右侧阴影
						if (v.getScrollX() + v.getWidth() + 20 >= childFirst
								.getMeasuredWidth()) {
							imageShadowRight.setVisibility(View.INVISIBLE);
						} else {
							imageShadowRight.setVisibility(View.VISIBLE);
						}
						// 左侧阴影
						if (v.getScrollX() >= 20) {
							imageShadowLeft.setVisibility(View.VISIBLE);
						} else {
							imageShadowLeft.setVisibility(View.INVISIBLE);
						}
						break;
					default:
						break;
					}
					return false;
				}
			});
		}
	}

	private void startSpeak(String content) {
		Intent intent = new Intent(MainActivity.this, SpeakService.class);
		intent.putExtra("content", content);
		startService(intent);
	}

	/**
	 * 初始化录像按钮
	 */
	private void initialCameraButton() {
		// ********** 小视图 **********
		// 录制
		smallVideoRecord = (ImageView) findViewById(R.id.smallVideoRecord);
		smallVideoRecord.setOnClickListener(new MyOnClickListener());

		// 锁定
		smallVideoLock = (ImageView) findViewById(R.id.smallVideoLock);
		smallVideoLock.setOnClickListener(new MyOnClickListener());

		// 拍照
		smallVideoCamera = (ImageView) findViewById(R.id.smallVideoCamera);
		smallVideoCamera.setOnClickListener(new MyOnClickListener());

		// ********** 大视图 **********
		layoutLargeButton = (RelativeLayout) findViewById(R.id.layoutLargeButton);

		// 视频尺寸
		largeVideoSize = (ImageView) findViewById(R.id.largeVideoSize);
		largeVideoSize.setOnClickListener(new MyOnClickListener());

		// 视频分段长度
		largeVideoTime = (ImageView) findViewById(R.id.largeVideoTime);
		largeVideoTime.setOnClickListener(new MyOnClickListener());

		// 锁定
		largeVideoLock = (ImageView) findViewById(R.id.largeVideoLock);
		largeVideoLock.setOnClickListener(new MyOnClickListener());

		// 静音
		largeVideoMute = (ImageView) findViewById(R.id.largeVideoMute);
		largeVideoMute.setOnClickListener(new MyOnClickListener());

		// 录制
		largeVideoRecord = (ImageView) findViewById(R.id.largeVideoRecord);
		largeVideoRecord.setOnClickListener(new MyOnClickListener());

		// 拍照
		largeVideoCamera = (ImageView) findViewById(R.id.largeVideoCamera);
		largeVideoCamera.setOnClickListener(new MyOnClickListener());

		updateButtonState(isSurfaceLarge());
	}

	/**
	 * 切换录像预览窗口的大小
	 */
	private void updateSurfaceState() {
		if (!isSurfaceLarge) {
			int widthFull = 854;
			int heightFull = 480;
			surfaceCamera.setLayoutParams(new RelativeLayout.LayoutParams(
					widthFull, heightFull));
			isSurfaceLarge = true;

			// 更新HorizontalScrollView阴影
			imageShadowLeft.setVisibility(View.GONE);
			imageShadowRight.setVisibility(View.GONE);

			updateButtonState(true);
		} else {
			int widthSmall = 480;
			int heightSmall = 270;
			surfaceCamera.setLayoutParams(new RelativeLayout.LayoutParams(
					widthSmall, heightSmall));
			isSurfaceLarge = false;

			// 更新HorizontalScrollView阴影
			hsvMain.scrollTo(0, 0);
			imageShadowLeft.setVisibility(View.GONE);
			imageShadowRight.setVisibility(View.VISIBLE);

			updateButtonState(false);
		}
	}

	/**
	 * 更新录像大小按钮显示状态
	 * 
	 * @param isSurfaceLarge
	 */
	private void updateButtonState(boolean isSurfaceLarge) {
		if (isSurfaceLarge) {
			smallVideoRecord.setVisibility(View.INVISIBLE);
			smallVideoLock.setVisibility(View.INVISIBLE);
			smallVideoCamera.setVisibility(View.INVISIBLE);
			layoutLargeButton.setVisibility(View.VISIBLE);
		} else {
			smallVideoRecord.setVisibility(View.VISIBLE);
			smallVideoLock.setVisibility(View.VISIBLE);
			smallVideoCamera.setVisibility(View.VISIBLE);
			layoutLargeButton.setVisibility(View.INVISIBLE);
		}
	}

	private boolean isSurfaceLarge() {
		return isSurfaceLarge;
	}

	private int secondCount = -1;

	public class updateRecordTimeThread implements Runnable {

		@Override
		public void run() {
			// 解决录像时，快速点击录像按钮两次，线程叠加跑秒过快的问题
			synchronized (updateRecordTimeHandler) {
				do {
					if (MyApplication.isCrashed) {
						Message messageVideoLock = new Message();
						messageVideoLock.what = 4;
						updateRecordTimeHandler.sendMessage(messageVideoLock);
					}
					if (MyApplication.isVideoCardEject) {
						// 录像时视频SD卡拔出停止录像
						MyLog.e("SD card remove badly or power unconnected, stop record!");
						Message messageEject = new Message();
						messageEject.what = 2;
						updateRecordTimeHandler.sendMessage(messageEject);
						break;
					} else if (!MyApplication.isPowerConnect) {
						// 电源断开
						MyLog.e("Stop Record:Power is unconnected");
						Message messagePowerUnconnect = new Message();
						messagePowerUnconnect.what = 3;
						updateRecordTimeHandler
								.sendMessage(messagePowerUnconnect);
						break;
					} else if (MyApplication.isSleeping) {
						// 进入低功耗休眠
						MyLog.e("Stop Record:isSleeping = true");
						Message messageSleep = new Message();
						messageSleep.what = 5;
						updateRecordTimeHandler.sendMessage(messageSleep);
						break;
					} else {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Message messageSecond = new Message();
						messageSecond.what = 1;
						updateRecordTimeHandler.sendMessage(messageSecond);
					}
				} while (MyApplication.isVideoReording);
			}
		}
	}

	final Handler updateRecordTimeHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				secondCount++;
				textRecordTime.setText(DateUtil
						.getFormatTimeBySecond(secondCount));
				break;

			case 2:
				// SD卡异常移除：停止录像
				if (stopRecorder() == 0) {
					mRecordState = Constant.Record.STATE_RECORD_STOPPED;
					MyApplication.isVideoReording = false;
					setupRecordViews();
				} else {
					if (stopRecorder() == 0) {
						mRecordState = Constant.Record.STATE_RECORD_STOPPED;
						MyApplication.isVideoReording = false;
						setupRecordViews();
					}
				}

				String strVideoCardEject = getResources().getString(
						R.string.sd_remove_badly);
				Toast.makeText(getApplicationContext(), strVideoCardEject,
						Toast.LENGTH_SHORT).show();
				MyLog.e("CardEjectReceiver:Video SD Removed");
				startSpeak(strVideoCardEject);
				audioRecordDialog.showErrorDialog(strVideoCardEject);
				new Thread(new dismissDialogThread()).start();
				break;

			case 3:
				// 电源断开，停止录像
				if (stopRecorder() == 0) {
					mRecordState = Constant.Record.STATE_RECORD_STOPPED;
					MyApplication.isVideoReording = false;
					setupRecordViews();
				} else {
					if (stopRecorder() == 0) {
						mRecordState = Constant.Record.STATE_RECORD_STOPPED;
						MyApplication.isVideoReording = false;
						setupRecordViews();
					}
				}

				String strPowerUnconnect = getResources().getString(
						R.string.stop_record_power_unconnect);
				Toast.makeText(getApplicationContext(), strPowerUnconnect,
						Toast.LENGTH_SHORT).show();
				MyLog.e("Record Stop:power unconnect.");
				startSpeak(strPowerUnconnect);
				audioRecordDialog.showErrorDialog(strPowerUnconnect);
				new Thread(new dismissDialogThread()).start();
				break;

			case 4:
				setupRecordViews();
				MyApplication.isCrashed = false;
				break;

			case 5:
				// 进入休眠，停止录像
				if (stopRecorder() == 0) {
					mRecordState = Constant.Record.STATE_RECORD_STOPPED;
					MyApplication.isVideoReording = false;
					setupRecordViews();
				} else {
					if (stopRecorder() == 0) {
						mRecordState = Constant.Record.STATE_RECORD_STOPPED;
						MyApplication.isVideoReording = false;
						setupRecordViews();
					}
				}
				releaseCameraZone();
				MyApplication.shouldResetRecordWhenResume = true;

				String strSleepOn = getResources().getString(
						R.string.stop_record_sleep_on);
				Toast.makeText(getApplicationContext(), strSleepOn,
						Toast.LENGTH_SHORT).show();
				MyLog.e("Record Stop:sleep on.");
				startSpeak(strSleepOn);
				audioRecordDialog.showErrorDialog(strSleepOn);
				new Thread(new dismissDialogThread()).start();
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 更新WiF状态
	 */
	private void updateWiFiState() {
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (wifiManager.isWifiEnabled() && mWifi.isConnected()) {
			int level = ((WifiManager) getSystemService(WIFI_SERVICE))
					.getConnectionInfo().getRssi();// Math.abs()
			imageWifiLevel.setImageResource(SignalUtil
					.getWifiImageBySignal(level));

		} else {
			imageWifiLevel.setImageResource(R.drawable.ic_qs_wifi_no_network);
		}
	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.surfaceCamera:
				updateSurfaceState();
				break;

			case R.id.smallVideoRecord:
			case R.id.largeVideoRecord:
			case R.id.layoutVideoRecord:
			case R.id.layoutVideoRecordSmall:
				if (!ClickUtil.isQuickClick(1000)) {
					startOrStopRecord();
				}
				break;

			case R.id.smallVideoLock:
			case R.id.largeVideoLock:
			case R.id.layoutVideoLock:
			case R.id.layoutVideoLockSmall:
				if (!ClickUtil.isQuickClick(800)) {
					lockOrUnlockVideo();
				}
				break;

			case R.id.largeVideoSize:
			case R.id.layoutVideoSize:
				if (!ClickUtil.isQuickClick(1500)) {
					// 切换分辨率录像停止，需要重置时间
					MyApplication.shouldVideoRecordWhenChangeSize = MyApplication.isVideoReording;
					MyApplication.isVideoReording = false;
					secondCount = -1; // 录制时间秒钟复位
					textRecordTime.setText("00:00");
					textRecordTime.setVisibility(View.INVISIBLE);

					if (mResolutionState == Constant.Record.STATE_RESOLUTION_1080P) {
						setResolution(Constant.Record.STATE_RESOLUTION_720P);
						editor.putString("videoSize", "720");
						mRecordState = Constant.Record.STATE_RECORD_STOPPED;
						startSpeak(getResources().getString(
								R.string.hint_video_size_720));
					} else if (mResolutionState == Constant.Record.STATE_RESOLUTION_720P) {
						setResolution(Constant.Record.STATE_RESOLUTION_1080P);
						editor.putString("videoSize", "1080");
						mRecordState = Constant.Record.STATE_RECORD_STOPPED;
						startSpeak(getResources().getString(
								R.string.hint_video_size_1080));
					}
					editor.commit();
					setupRecordViews();

					// 修改分辨率后按需启动录像
					if (MyApplication.shouldVideoRecordWhenChangeSize) {
						new Thread(new StartRecordWhenChangeSizeThread())
								.start();
						MyApplication.shouldVideoRecordWhenChangeSize = false;
					}
				}
				break;

			case R.id.largeVideoTime:
			case R.id.layoutVideoTime:
				if (!ClickUtil.isQuickClick(800)) {
					if (mIntervalState == Constant.Record.STATE_INTERVAL_3MIN) {
						if (setInterval(1 * 60) == 0) {
							mIntervalState = Constant.Record.STATE_INTERVAL_1MIN;
							editor.putString("videoTime", "1");
							startSpeak(getResources().getString(
									R.string.hint_video_time_1));
						}
					} else if (mIntervalState == Constant.Record.STATE_INTERVAL_1MIN) {
						if (setInterval(3 * 60) == 0) {
							mIntervalState = Constant.Record.STATE_INTERVAL_3MIN;
							editor.putString("videoTime", "3");
							startSpeak(getResources().getString(
									R.string.hint_video_time_3));
						}
					}
					editor.commit();
					setupRecordViews();
				}
				break;

			case R.id.largeVideoMute:
			case R.id.layoutVideoMute:
				if (!ClickUtil.isQuickClick(800)) {
					// 切换录音/静音状态停止录像，需要重置时间
					MyApplication.shouldVideoRecordWhenChangeSize = MyApplication.isVideoReording;

					if (MyApplication.isVideoReording) {
						secondCount = -1; // 录制时间秒钟复位
						textRecordTime.setText("00:00");
						textRecordTime.setVisibility(View.INVISIBLE);
						MyApplication.isVideoReording = false;
						startOrStopRecord();
					}
					if (mMuteState == Constant.Record.STATE_MUTE) {
						if (setMute(false) == 0) {
							mMuteState = Constant.Record.STATE_UNMUTE;
							startSpeak(getResources().getString(
									R.string.hint_video_mute_off));
							editor.putBoolean("videoMute", false);
							editor.commit();
						}
					} else if (mMuteState == Constant.Record.STATE_UNMUTE) {
						if (setMute(true) == 0) {
							mMuteState = Constant.Record.STATE_MUTE;
							startSpeak(getResources().getString(
									R.string.hint_video_mute_on));
							editor.putBoolean("videoMute", true);
							editor.commit();
						}
					}

					setupRecordViews();

					// 修改录音/静音后按需还原录像状态
					if (MyApplication.shouldVideoRecordWhenChangeSize) {
						new Thread(new StartRecordWhenChangeMuteThread())
								.start();
						MyApplication.shouldVideoRecordWhenChangeSize = false;
					}
				}
				break;

			case R.id.smallVideoCamera:
			case R.id.largeVideoCamera:
			case R.id.layoutVideoCamera:
			case R.id.layoutVideoCameraSmall:
				if (!ClickUtil.isQuickClick(500)) {
					takePhoto();
					AudioPlayUtil.playAudio(getApplicationContext(),
							FILE_TYPE_IMAGE);
				}
				break;

			case R.id.layoutWeather:
				if (!ClickUtil.isQuickClick(800)) {
					Intent intentWeather = new Intent(MainActivity.this,
							WeatherActivity.class);
					startActivity(intentWeather);
					overridePendingTransition(R.anim.zms_translate_down_out,
							R.anim.zms_translate_down_in);
				}
				break;

			case R.id.imageNavi:
				if (!ClickUtil.isQuickClick(800)) {
					// com.tchip.baidunavi
					if (NetworkUtil.isNetworkConnected(getApplicationContext())) {
						try {
							ComponentName componentBaiduNavi;
							componentBaiduNavi = new ComponentName(
									"com.tchip.baidunavi",
									"com.tchip.baidunavi.ui.activity.MainActivity");
							Intent intentBaiduNavi = new Intent();
							intentBaiduNavi
									.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intentBaiduNavi.setComponent(componentBaiduNavi);
							startActivity(intentBaiduNavi);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						NetworkUtil.noNetworkHint(getApplicationContext());
					}
				}
				break;

			case R.id.imageMusicOL:
				if (!ClickUtil.isQuickClick(800)) {
					try {
						ComponentName componentMusic;
						componentMusic = new ComponentName("cn.kuwo.kwmusichd",
								"cn.kuwo.kwmusichd.WelcomeActivity");
						Intent intentMusic = new Intent();
						intentMusic.setComponent(componentMusic);
						startActivity(intentMusic);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;

			case R.id.imageEDog:
				if (!ClickUtil.isQuickClick(800)) {
					try {
						ComponentName componentEDog = new ComponentName(
								"entry.dsa2014", "entry.dsa2014.MainActivity");
						Intent intentEDog = new Intent();
						intentEDog.setComponent(componentEDog);
						startActivity(intentEDog);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;

			case R.id.imageMultimedia:
				if (!ClickUtil.isQuickClick(800)) {
					Intent intentMultimedia = new Intent(MainActivity.this,
							MultimediaActivity.class);
					startActivity(intentMultimedia);
					overridePendingTransition(R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				}
				break;

			case R.id.imageRouteTrack:
				if (!ClickUtil.isQuickClick(800)) {
					Intent intentRouteTrack = new Intent(MainActivity.this,
							RouteListActivity.class);
					startActivity(intentRouteTrack);
					overridePendingTransition(R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				}
				break;

			case R.id.imageFmTransmit:
				if (!ClickUtil.isQuickClick(800)) {
					Intent intentFmTransmit = new Intent(MainActivity.this,
							FmTransmitActivity.class);
					startActivity(intentFmTransmit);
					overridePendingTransition(R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				}
				break;

			case R.id.imageFileExplore:
				if (!ClickUtil.isQuickClick(800)) {
					Intent intentFileExplore = new Intent(MainActivity.this,
							FolderActivity.class);
					startActivity(intentFileExplore);
					overridePendingTransition(R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				}
				break;

			case R.id.imageVoiceChat:
				if (!ClickUtil.isQuickClick(800)) {
					Intent intentVoiceChat;
					if (Constant.Module.isVoiceXunfei) {
						// 讯飞语音
						intentVoiceChat = new Intent(MainActivity.this,
								ChatActivity.class);
					} else {
					}
					startActivity(intentVoiceChat);
					overridePendingTransition(R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				}
				break;

			case R.id.imageDialer:
				// try {
				// ComponentName componentDialer = new ComponentName(
				// "com.android.dialer",
				// "com.android.dialer.DialtactsActivity");
				// Intent intentDialer = new Intent();
				// intentDialer.setComponent(componentDialer);
				// startActivity(intentDialer);
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				if (!ClickUtil.isQuickClick(800)) {
					Intent intentBTDialer = new Intent(MainActivity.this,
							BluetoothDialerActivity.class);
					startActivity(intentBTDialer);
					overridePendingTransition(R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				}
				break;

			case R.id.imageMessage:
				if (!ClickUtil.isQuickClick(800)) {
					try {
						ComponentName componentMessage = new ComponentName(
								"com.android.mms",
								"com.android.mms.ui.BootActivity");
						Intent intentMessage = new Intent();
						intentMessage.setComponent(componentMessage);
						startActivity(intentMessage);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;

			case R.id.imageSetting:
				if (!ClickUtil.isQuickClick(800)) {
					Intent intentSetting = new Intent(MainActivity.this,
							SettingActivity.class);
					startActivity(intentSetting);
					overridePendingTransition(R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				}
				break;

			case R.id.layoutWiFi:
				if (!ClickUtil.isQuickClick(800)) {
					if (Constant.Module.isWifiSystem) {
						startActivity(new Intent(
								android.provider.Settings.ACTION_WIFI_SETTINGS));
					} else {
						Intent intentWiFi = new Intent(MainActivity.this,
								WifiListActivity.class);
						startActivity(intentWiFi);
					}
				}
				break;

			default:
				break;
			}
		}
	}

	class RealRecordThread implements Runnable {

		@Override
		public void run() {
			synchronized (realRecordHandler) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				} finally {
					Message messageRealRecord = new Message();
					messageRealRecord.what = 1;
					realRecordHandler.sendMessage(messageRealRecord);
				}
			}
		}
	}

	final Handler realRecordHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// 开始录像
				if (startRecorder() == 0) {
					mRecordState = Constant.Record.STATE_RECORD_STARTED;
					MyApplication.isVideoReording = true;
				} else {
					if (Constant.isDebug)
						MyLog.e("Start Record Failed");
				}
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 开启或关闭录像
	 */
	private void startOrStopRecord() {
		if (mRecordState == Constant.Record.STATE_RECORD_STOPPED) {
			if (MyApplication.isSleeping) {
				startSpeak("正在休眠，无法录像");
			} else {
				if (!MyApplication.isMainForeground) {
					// 录像需切换到预览界面且点亮屏幕，否则无法录像
					// 发送Home键，回到主界面
					sendBroadcast(new Intent("com.tchip.powerKey").putExtra(
							"value", "home"));
					// 点亮屏幕
					SettingUtil.lightScreen(getApplicationContext());

					// 录像线程
					new Thread(new RealRecordThread()).start();
				} else {
					// 直接开始录像
					if (startRecorder() == 0) {
						mRecordState = Constant.Record.STATE_RECORD_STARTED;
						MyApplication.isVideoReording = true;
					} else {
						if (Constant.isDebug)
							MyLog.e("Start Record Failed");
					}
				}
			}
		} else if (mRecordState == Constant.Record.STATE_RECORD_STARTED) {
			if (stopRecorder() == 0) {
				mRecordState = Constant.Record.STATE_RECORD_STOPPED;
				MyApplication.isVideoReording = false;
			}
		}
		AudioPlayUtil.playAudio(getApplicationContext(), FILE_TYPE_VIDEO);
		setupRecordViews();
		if (Constant.isDebug) {
			MyLog.v("MyApplication.isVideoReording:"
					+ MyApplication.isVideoReording);
		}
	}

	/**
	 * 加锁或加锁视频
	 */
	private void lockOrUnlockVideo() {
		if (!MyApplication.isVideoLock) {
			MyApplication.isVideoLock = true;
			startSpeak(getResources().getString(R.string.video_lock));
		} else {
			MyApplication.isVideoLock = false;
			startSpeak(getResources().getString(R.string.video_unlock));
		}
		setupRecordViews();
	}

	/**
	 * WiFi状态Receiver
	 */
	private BroadcastReceiver wifiIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int wifi_state = intent.getIntExtra("wifi_state", 0);
			int level = ((WifiManager) getSystemService(WIFI_SERVICE))
					.getConnectionInfo().getRssi();// Math.abs()
			updateWiFiState();
			MyLog.v("wifiIntentReceiver, Wifi Level:" + level);

			switch (wifi_state) {
			case WifiManager.WIFI_STATE_ENABLED:
				updateWiFiState();
				new Thread(new updateWifiThread()).start();
				break;

			case WifiManager.WIFI_STATE_ENABLING:
				updateWiFiState();
				new Thread(new updateWifiThread()).start();
				break;

			case WifiManager.WIFI_STATE_DISABLING:
				// quickWifi.setImageResource(R.drawable.quick_icon_wifi_offing);
			case WifiManager.WIFI_STATE_DISABLED:
			case WifiManager.WIFI_STATE_UNKNOWN:
				updateWiFiState();
				break;
			}
		}
	};

	/**
	 * 更新wifi图标
	 */
	public class updateWifiThread implements Runnable {
		@Override
		public void run() {
			synchronized (updateWifiHandler) {
				int updateWifiTime = 1;
				boolean shouldUpdateWifi = true;
				while (shouldUpdateWifi) {
					try {
						Thread.sleep(2500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					MyLog.v("updateWifiThread:Refresh Wifi! " + updateWifiTime);
					Message messageWifi = new Message();
					messageWifi.what = 1;
					updateWifiHandler.sendMessage(messageWifi);
					updateWifiTime++;
					if (updateWifiTime > 5) {
						shouldUpdateWifi = false;
					}

				}
			}
		}
	}

	final Handler updateWifiHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				updateWiFiState();
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 更新天气
	 */
	private void startWeatherService() {
		Intent intent = new Intent(this, WeatherService.class);
		startService(intent);
	}

	/**
	 * 释放Camera
	 */
	private void releaseCameraZone() {
		release();
		// mHolder = null;
		if (mCamera != null) {
			mCamera.stopPreview();
		}
		MyApplication.shouldResetRecordWhenResume = true;
	}

	@Override
	protected void onPause() {
		MyApplication.isMainForeground = false;
		// 3G信号
		Tel.listen(MyListener, PhoneStateListener.LISTEN_NONE);

		if (!MyApplication.isVideoReording) {
			MyLog.v("[MainActivity]onPause, releaseCameraZone() when not recording");
			releaseCameraZone();
		}

		super.onPause();
	}

	@Override
	protected void onResume() {
		// 按HOME键将预览区域还原为小窗口
		if (isSurfaceLarge()) {
			updateSurfaceState();
		}

		MyApplication.isMainForeground = true;
		if (!MyApplication.isFirstLaunch) {
			if (!MyApplication.isVideoReording
					|| MyApplication.shouldResetRecordWhenResume) {
				MyApplication.shouldResetRecordWhenResume = false;
				// 重置预览区域
				if (mCamera == null) {
					// mHolder = holder;
					setup();
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
		} else {
			MyApplication.isFirstLaunch = false;
		}

		// 更新录像界面按钮状态
		refreshRecordButton();
		setupRecordViews();

		// 3G信号
		Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

		// 初始化FM发射
		initalFmTransmit();

		super.onResume();
	}

	@Override
	protected void onDestroy() {

		// 取消注册wifi消息处理器
		unregisterReceiver(wifiIntentReceiver);
		// 录像区域
		release();

		super.onDestroy();
	}

	// *********** Record ***********

	/**
	 * 设置录制初始值
	 */
	private void setupRecordDefaults() {
		refreshRecordButton();

		mRecordState = Constant.Record.STATE_RECORD_STOPPED;
		MyApplication.isVideoReording = false;

		mPathState = Constant.Record.STATE_PATH_ZERO;
		mSecondaryState = Constant.Record.STATE_SECONDARY_DISABLE;
		mOverlapState = Constant.Record.STATE_OVERLAP_ZERO;

		// 录音,静音;默认不录音
		boolean videoMute = sharedPreferences.getBoolean("videoMute", true);
		if (!videoMute) {
			mMuteState = Constant.Record.STATE_UNMUTE;
		} else {
			mMuteState = Constant.Record.STATE_MUTE; // 不录音
		}

	}

	private void refreshRecordButton() {

		// 视频尺寸
		String videoSizeStr = sharedPreferences.getString("videoSize", "720");
		if ("1080".equals(videoSizeStr)) {
			mResolutionState = Constant.Record.STATE_RESOLUTION_1080P;
		} else {
			mResolutionState = Constant.Record.STATE_RESOLUTION_720P;
		}

		// 视频分段
		String videoTimeStr = sharedPreferences.getString("videoTime", "3");
		if ("1".equals(videoTimeStr)) {
			mIntervalState = Constant.Record.STATE_INTERVAL_1MIN;
		} else {
			mIntervalState = Constant.Record.STATE_INTERVAL_3MIN;
		}
	}

	private void setupRecordViews() {
		// 视频分辨率
		if (mResolutionState == Constant.Record.STATE_RESOLUTION_720P) {
			largeVideoSize.setBackground(getResources().getDrawable(
					R.drawable.ui_camera_video_size_720));
		} else if (mResolutionState == Constant.Record.STATE_RESOLUTION_1080P) {
			largeVideoSize.setBackground(getResources().getDrawable(
					R.drawable.ui_camera_video_size_1080));
		}

		// 录像按钮
		if (mRecordState == Constant.Record.STATE_RECORD_STOPPED) {
			largeVideoRecord.setBackground(getResources().getDrawable(
					R.drawable.ui_main_video_record));
			smallVideoRecord.setBackground(getResources().getDrawable(
					R.drawable.ui_main_video_record));
		} else if (mRecordState == Constant.Record.STATE_RECORD_STARTED) {
			largeVideoRecord.setBackground(getResources().getDrawable(
					R.drawable.ui_main_video_pause));
			smallVideoRecord.setBackground(getResources().getDrawable(
					R.drawable.ui_main_video_pause));
		}

		// 视频分段
		if (mIntervalState == Constant.Record.STATE_INTERVAL_1MIN) {
			largeVideoTime.setBackground(getResources().getDrawable(
					R.drawable.ui_camera_video_time_1));
		} else if (mIntervalState == Constant.Record.STATE_INTERVAL_3MIN) {
			largeVideoTime.setBackground(getResources().getDrawable(
					R.drawable.ui_camera_video_time_3));
		}

		// 视频加锁
		if (MyApplication.isVideoLock) {
			smallVideoLock.setBackground(getResources().getDrawable(
					R.drawable.ui_camera_video_lock));
			largeVideoLock.setBackground(getResources().getDrawable(
					R.drawable.ui_camera_video_lock));
		} else {
			smallVideoLock.setBackground(getResources().getDrawable(
					R.drawable.ui_camera_video_unlock));
			largeVideoLock.setBackground(getResources().getDrawable(
					R.drawable.ui_camera_video_unlock));
		}

		// 静音按钮
		if (mMuteState == Constant.Record.STATE_MUTE) {
			largeVideoMute.setBackground(getResources().getDrawable(
					R.drawable.ui_camera_video_sound_off));
		} else if (mMuteState == Constant.Record.STATE_UNMUTE) {
			largeVideoMute.setBackground(getResources().getDrawable(
					R.drawable.ui_camera_video_sound_on));
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// mHolder = holder;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		MyLog.v("[Record]surfaceCreated");

		if (mCamera == null) {
			mHolder = holder;
			setup();
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

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		MyLog.v("[Record]surfaceDestroyed");
	}

	private boolean openCamera() {
		if (mCamera != null) {
			closeCamera();
		}
		try {
			MyLog.v("[Record] Camera.open");
			mCamera = Camera.open(0);
			mCamera.lock();

			// 设置系统Camera参数
			// Camera.Parameters para = mCamera.getParameters();
			// para.unflatten(Constant.CAMERA_PARAMS);
			// mCamera.setParameters(para);

			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
			mCamera.unlock();
			return true;
		} catch (Exception ex) {
			closeCamera();
			MyLog.e("[MainActivity]openCamera:Catch Exception!");
			return false;
		}
	}

	private boolean closeCamera() {
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
	}

	/**
	 * 删除最旧视频
	 */
	private boolean deleteOldestUnlockVideo() {
		try {
			String sdcardPath = Constant.Path.SDCARD_1 + File.separator;// "/storage/sdcard1/";
			if (Constant.Record.saveVideoToSD2) {
				sdcardPath = Constant.Path.SDCARD_2 + File.separator;// "/storage/sdcard2/";
			}
			// sharedPreferences.getString("sdcardPath","/mnt/sdcard2");
			float sdFree = StorageUtil.getSDAvailableSize(sdcardPath);
			float sdTotal = StorageUtil.getSDTotalSize(sdcardPath);
			int intSdFree = (int) sdFree;
			MyLog.v("[deleteOldestUnlockVideo] sdFree:" + intSdFree);
			while (sdFree < sdTotal * Constant.Record.SD_MIN_FREE_PERCENT
					|| intSdFree < Constant.Record.SD_MIN_FREE_STORAGE) {
				int oldestUnlockVideoId = videoDb.getOldestUnlockVideoId();
				// 删除较旧未加锁视频文件
				if (oldestUnlockVideoId != -1) {
					String oldestUnlockVideoName = videoDb
							.getVideNameById(oldestUnlockVideoId);
					File f = new File(sdcardPath + "tachograph/"
							+ oldestUnlockVideoName.split("_")[0]
							+ File.separator + oldestUnlockVideoName);
					if (f.exists() && f.isFile()) {
						MyLog.d("Delete Old Unlock Video:" + f.getName());
						int i = 0;
						while (!f.delete() && i < 5) {
							i++;
							MyLog.d("Delete Old Unlock Video:" + f.getName()
									+ " Filed!!! Try:" + i);
						}
					}
					// 删除数据库记录
					videoDb.deleteDriveVideoById(oldestUnlockVideoId);
				} else {
					int oldestVideoId = videoDb.getOldestVideoId();
					if (oldestVideoId == -1) {
						/**
						 * 有一种情况：数据库中无视频信息。导致的原因：
						 * 1：升级时选Download的话，不会清理USB存储空间，应用数据库被删除； 2：应用被清除数据
						 * 这种情况下旧视频无法直接删除， 此时如果满存储，需要直接删除
						 */
						File file = new File(sdcardPath + "tachograph/");
						StorageUtil.RecursionDeleteFile(file);
						MyLog.e("!!! Delete tachograph/ Directory");

						sdFree = StorageUtil.getSDAvailableSize(sdcardPath);
						intSdFree = (int) sdFree;
						if (sdFree < sdTotal
								* Constant.Record.SD_MIN_FREE_PERCENT
								|| intSdFree < Constant.Record.SD_MIN_FREE_STORAGE) {
							// 此时若空间依然不足,提示用户清理存储（已不是行车视频的原因）
							MyLog.e("Storage is full...");

							String strNoStorage = getResources().getString(
									R.string.storage_full_cause_by_other);

							audioRecordDialog.showErrorDialog(strNoStorage);
							// new Thread(new dismissDialogThread()).start();
							startSpeak(strNoStorage);

							return false;
						}
					} else {
						// 提示用户清理空间，删除较旧的视频（加锁）
						String strStorageFull = getResources().getString(
								R.string.storage_full_and_delete_lock);
						startSpeak(strStorageFull);
						Toast.makeText(getApplicationContext(), strStorageFull,
								Toast.LENGTH_SHORT).show();

						String oldestVideoName = videoDb
								.getVideNameById(oldestVideoId);
						File f = new File(sdcardPath + "tachograph/"
								+ oldestVideoName.split("_")[0]
								+ File.separator + oldestVideoName);
						if (f.exists() && f.isFile()) {
							MyLog.d("Delete Old lock Video:" + f.getName());
							int i = 0;
							while (!f.delete() && i < 5) {
								i++;
								MyLog.d("Delete Old lock Video:" + f.getName()
										+ " Filed!!! Try:" + i);
							}
						}
						// 删除数据库记录
						videoDb.deleteDriveVideoById(oldestVideoId);
					}
				}
				// 更新剩余空间
				sdFree = StorageUtil.getSDAvailableSize(sdcardPath);
				intSdFree = (int) sdFree;
			}
			return true;
		} catch (Exception e) {
			/*
			 * 异常原因：1.文件由用户手动删除
			 */
			MyLog.e("[MainActivity]deleteOldestUnlockVideo:Catch Exception!");
			e.printStackTrace();
			return true;
		}
	}

	public int startRecorder() {
		if (!StorageUtil.isVideoCardExists()) {
			// SDCard2不存在
			String strNoSD = getResources().getString(R.string.sd1_not_exist);
			if (Constant.Record.saveVideoToSD2) {
				strNoSD = getResources().getString(R.string.sd2_not_exist);
			}
			audioRecordDialog.showErrorDialog(strNoSD);
			new Thread(new dismissDialogThread()).start();
			startSpeak(strNoSD);
			return -1;
		} else if (mMyRecorder != null) {
			if (deleteOldestUnlockVideo()) {
				textRecordTime.setVisibility(View.VISIBLE);
				new Thread(new updateRecordTimeThread()).start(); // 更新录制时间
				MyLog.d("Record Start");
				// 设置保存路径
				if (Constant.Record.saveVideoToSD2) {
					setDirectory(Constant.Path.SDCARD_2);
				} else {
					setDirectory(Constant.Path.SDCARD_1);
				}

				// 设置录像静音
				if (mMuteState == Constant.Record.STATE_UNMUTE) {
					setMute(false);
				} else {
					setMute(true);
				}
				return mMyRecorder.start();
			}
		}
		return -1;
	}

	/**
	 * 检查并删除异常视频文件：SD存在但数据库中不存在的文件
	 */
	private void CheckErrorFile() {
		if (StorageUtil.isVideoCardExists()) {
			String sdcardPath = Constant.Path.SDCARD_1 + File.separator; // "/storage/sdcard1/";
			if (Constant.Record.saveVideoToSD2) {
				sdcardPath = Constant.Path.SDCARD_2 + File.separator; // "/storage/sdcard2/";
			}
			File file = new File(sdcardPath + "tachograph/");
			RecursionCheckFile(file);
		}
	}

	public void RecursionCheckFile(File file) {
		try {
			if (file.isFile() && !file.getName().endsWith(".jpg")) {
				if (!videoDb.isVideoExist(file.getName())) {
					file.delete();
					MyLog.v("[RecursionCheckFile] Delete Error File:"
							+ file.getName());
				}
				return;
			}
			if (file.isDirectory()) {
				File[] childFile = file.listFiles();
				if (childFile == null || childFile.length == 0) {
					// file.delete();
					return;
				}
				for (File f : childFile) {
					RecursionCheckFile(f);
				}
				// file.delete();
			}
		} catch (Exception e) {
			MyLog.e("[MainActivity]RecursionCheckFile:Catch Exception!");
		}
	}

	public class dismissDialogThread implements Runnable {
		@Override
		public void run() {
			synchronized (dismissDialogHandler) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Message messageEject = new Message();
				messageEject.what = 1;
				dismissDialogHandler.sendMessage(messageEject);
			}
		}
	}

	final Handler dismissDialogHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				audioRecordDialog.dismissDialog();
				break;

			default:
				break;
			}
		}
	};

	public int stopRecorder() {
		secondCount = -1; // 录制时间秒钟复位
		textRecordTime.setText("00:00");
		textRecordTime.setVisibility(View.INVISIBLE);
		if (mMyRecorder != null) {
			MyLog.d("Record Stop");
			return mMyRecorder.stop();
		}

		return -1;
	}

	public int setInterval(int seconds) {
		if (mMyRecorder != null) {
			return mMyRecorder.setVideoSeconds(seconds);
		}
		return -1;
	}

	public int setOverlap(int seconds) {
		if (mMyRecorder != null) {
			return mMyRecorder.setVideoOverlap(seconds);
		}
		return -1;
	}

	public int takePhoto() {
		if (!StorageUtil.isVideoCardExists()) {
			// SDCard不存在
			String strNoSD = getResources().getString(R.string.sd1_not_exist);
			if (Constant.Record.saveVideoToSD2) {
				strNoSD = getResources().getString(R.string.sd2_not_exist);
			}
			audioRecordDialog.showErrorDialog(strNoSD);
			new Thread(new dismissDialogThread()).start();
			startSpeak(strNoSD);
			return -1;
		} else if (mMyRecorder != null) {
			return mMyRecorder.takePicture();
		}
		return -1;
	}

	public int setDirectory(String dir) {
		if (mMyRecorder != null) {
			return mMyRecorder.setDirectory(dir);
		}
		return -1;
	}

	/**
	 * 设置录像静音，需要已经初始化mMyRecorder
	 * 
	 * @param mute
	 * @return
	 */
	private int setMute(boolean mute) {
		if (mMyRecorder != null) {
			return mMyRecorder.setMute(mute);
		}
		return -1;
	}

	public int setResolution(int state) {
		if (state != mResolutionState) {
			mResolutionState = state;
			release();
			if (openCamera()) {
				setupRecorder();
			}
		}
		return -1;
	}

	public int setSecondary(int state) {
		if (state == Constant.Record.STATE_SECONDARY_DISABLE) {
			if (mMyRecorder != null) {
				return mMyRecorder.setSecondaryVideoEnable(false);
			}
		} else if (state == Constant.Record.STATE_SECONDARY_ENABLE) {
			if (mMyRecorder != null) {
				mMyRecorder.setSecondaryVideoSize(320, 240);
				mMyRecorder.setSecondaryVideoFrameRate(30);
				mMyRecorder.setSecondaryVideoBiteRate(120000);
				return mMyRecorder.setSecondaryVideoEnable(true);
			}
		}
		return -1;
	}

	private void setupRecorder() {
		releaseRecorder();
		try {
			mMyRecorder = new TachographRecorder();
			mMyRecorder.setTachographCallback(this);
			mMyRecorder.setCamera(mCamera);
			mMyRecorder.setClientName(this.getPackageName());
			if (mResolutionState == Constant.Record.STATE_RESOLUTION_1080P) {
				mMyRecorder.setVideoSize(1920, 1088); // 16倍数
				mMyRecorder.setVideoFrameRate(30);
				mMyRecorder.setVideoBiteRate(9000000 * 2); // 8500000
			} else {
				mMyRecorder.setVideoSize(1280, 720);
				mMyRecorder.setVideoFrameRate(30);
				mMyRecorder.setVideoBiteRate(9000000); // 3500000
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
			}
			mMyRecorder.prepare();
		} catch (Exception e) {
			MyLog.e("[MainActivity]setupRecorder: Catch Exception!");
		}
	}

	private void releaseRecorder() {
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
	}

	@Override
	public void onError(int error) {
		switch (error) {
		case TachographCallback.ERROR_SAVE_VIDEO_FAIL:
			Toast.makeText(getApplicationContext(), "视频保存失败",
					Toast.LENGTH_SHORT).show();
			MyLog.e("Record Error : ERROR_SAVE_VIDEO_FAIL");
			break;

		case TachographCallback.ERROR_SAVE_IMAGE_FAIL:
			Toast.makeText(getApplicationContext(), "图片保存失败",
					Toast.LENGTH_SHORT).show();
			MyLog.e("Record Error : ERROR_SAVE_IMAGE_FAIL");
			break;

		case TachographCallback.ERROR_RECORDER_CLOSED:
			MyLog.e("Record Error : ERROR_RECORDER_CLOSED");
			break;
		default:
			break;
		}
	}

	@Override
	public void onFileSave(int type, String path) {
		/**
		 * [Type] 0-图片 1-视频
		 * 
		 * [Path] 视频：/mnt/sdcard/tachograph/2015-07-01/2015-07-01_105536.mp4
		 * 图片:/mnt/sdcard/tachograph/camera_shot/2015-07-01_105536.jpg
		 */

		if (type == 1) {
			deleteOldestUnlockVideo();
			secondCount = -1; // 录制时间秒钟复位
			textRecordTime.setText("00:00");

			String videoName = path.split("/")[5];
			editor.putString("sdcardPath", "/mnt/" + path.split("/")[2] + "/");
			editor.commit();
			int videoResolution = 720;
			int videoLock = 0;

			if (mResolutionState == Constant.Record.STATE_RESOLUTION_1080P) {
				videoResolution = 1080;
			}
			if (MyApplication.isVideoLock) {
				videoLock = 1;
				MyApplication.isVideoLock = false; // 还原
				setupRecordViews(); // 更新录制按钮状态
			}
			DriveVideo driveVideo = new DriveVideo(videoName, videoLock,
					videoResolution);
			videoDb.addDriveVideo(driveVideo);
		} else {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.photo_save),
					Toast.LENGTH_SHORT).show();
		}

		// 更新Media Database
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.parse("file://" + path)));
		MyLog.d("[onFileSave] File Save, Type=" + type + ",Save path:" + path);

		if (!MyApplication.isVideoReording) {
			// 需要在当前视频存储到数据库之后，且当前未录像时再进行
			CheckErrorFile();
		}
	}

	public void setup() {
		release();
		if (openCamera()) {
			setupRecorder();
		}
	}

	public void release() {
		releaseRecorder();
		closeCamera();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 如果视频全屏预览开启，返回关闭
			if (isSurfaceLarge()) {
				int widthSmall = 480;
				int heightSmall = 270;
				surfaceCamera.setLayoutParams(new RelativeLayout.LayoutParams(
						widthSmall, heightSmall));
				isSurfaceLarge = false;
				// 更新HorizontalScrollView阴影
				hsvMain.scrollTo(0, 0);
				imageShadowLeft.setVisibility(View.GONE);
				imageShadowRight.setVisibility(View.VISIBLE);

				updateButtonState(false);
			}
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	/**
	 * 启动时初始化FM发射频率节点
	 * 
	 * 频率范围：7600~10800:8750-10800
	 */
	private void initalFmTransmit() {
		try {
			int freq = SettingUtil.getFmFrequceny(this);
			if (freq >= 8750 && freq <= 10800)
				SettingUtil.setFmFrequency(this, freq);
			else
				SettingUtil.setFmFrequency(this, 8750);
		} catch (Exception e) {
			MyLog.e("[MainActivity]initFmTransmit: Catch Exception!");
		}
	}

	/**
	 * 初始化自动亮度节点
	 */
	private void initialAutoLight() {
		try {
			boolean autoScreenLight = sharedPreferences.getBoolean(
					"autoScreenLight", true);
			SettingUtil.setAutoLight(getApplicationContext(), autoScreenLight);
		} catch (Exception e) {
			MyLog.e("[MainActivity]initialAutoLight: Catch Exception!");
		}
	}

}
