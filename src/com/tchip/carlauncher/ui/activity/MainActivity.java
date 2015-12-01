package com.tchip.carlauncher.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.SortedMap;

import net.sourceforge.jheader.App1Header;
import net.sourceforge.jheader.App1Header.Tag;
import net.sourceforge.jheader.ExifFormatException;
import net.sourceforge.jheader.JpegFormatException;
import net.sourceforge.jheader.JpegHeaders;
import net.sourceforge.jheader.TagFormatException;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.MyApplication;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.model.DriveVideo;
import com.tchip.carlauncher.model.DriveVideoDbHelper;
import com.tchip.carlauncher.model.Typefaces;
import com.tchip.carlauncher.service.FloatWindowService;
import com.tchip.carlauncher.service.RouteRecordService;
import com.tchip.carlauncher.service.SensorWatchService;
import com.tchip.carlauncher.service.SleepOnOffService;
import com.tchip.carlauncher.service.SpeakService;
import com.tchip.carlauncher.util.AudioPlayUtil;
import com.tchip.carlauncher.util.ClickUtil;
import com.tchip.carlauncher.util.DateUtil;
import com.tchip.carlauncher.util.MyLog;
import com.tchip.carlauncher.util.NetworkUtil;
import com.tchip.carlauncher.util.OpenUtil;
import com.tchip.carlauncher.util.OpenUtil.MODULE_TYPE;
import com.tchip.carlauncher.util.SettingUtil;
import com.tchip.carlauncher.util.StorageUtil;
import com.tchip.carlauncher.util.SignalUtil;
import com.tchip.carlauncher.view.AudioRecordDialog;
import com.tchip.tachograph.TachographCallback;
import com.tchip.tachograph.TachographRecorder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
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

	/**
	 * 前后摄像头切换
	 */
	private LinearLayout layoutCameraSwitch;
	private ImageView imageCameraSwitch;

	private ImageView imageSignalLevel, image3GType;

	private TelephonyManager telephonyManager;
	private int simState;
	private MyPhoneStateListener myPhoneStateListener;

	private ImageView imageAirplane; // 飞行模式图标
	private ImageView imageBluetooth; // 外置蓝牙图标

	private AudioRecordDialog audioRecordDialog;

	private WifiManager wifiManager;
	private ConnectivityManager connManager;
	private NetworkInfo mWifi;

	private PowerManager powerManager;

	private String strRecordStop = "停止录像";
	private String strRecordStart = "开始录像";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		sharedPreferences = getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();

		// 视频数据库
		videoDb = new DriveVideoDbHelper(getApplicationContext());

		// Dialog
		audioRecordDialog = new AudioRecordDialog(MainActivity.this);

		// 获取屏幕状态
		powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

		initialLayout();
		initialCameraButton();

		// 录像：配置参数，初始化布局
		setupRecordDefaults();
		setupRecordViews();

		// 3G信号
		myPhoneStateListener = new MyPhoneStateListener();
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		// SIM卡状态
		simState = telephonyManager.getSimState();
		telephonyManager.listen(myPhoneStateListener,
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

		// 注册wifi消息处理器
		registerReceiver(wifiIntentReceiver, wifiIntentFilter);

		// 初始化节点状态
		initialNodeState();

		// 初始化服务
		initialService();

		// 首次启动是否需要自动录像
		if (1 == SettingUtil.getAccStatus()) {
			// 关闭飞行模式
			sendBroadcast(new Intent("com.tchip.AIRPLANE_OFF"));

			// 序列任务线程
			new Thread(new AutoThread()).start();
		} else {
			// ACC未连接,进入休眠
			MyLog.v("[MainActivity]ACC Check:OFF, Send Broadcast:com.tchip.SLEEP_ON.");
			sendBroadcast(new Intent("com.tchip.SLEEP_ON"));
		}

		// 后台线程
		new Thread(new BackThread()).start();
	}

	private NetworkStateReceiver networkStateReceiver;

	/**
	 * 监听飞行模式，外置蓝牙广播
	 */
	private class NetworkStateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
				boolean isAirplaneOn = intent.getBooleanExtra("state", false);
				MyLog.v("[AirplaneReceiver]State:" + isAirplaneOn);
				setAirplaneIcon(isAirplaneOn);
			} else if (action.equals("com.tchip.BT_CONNECTED")) { // 外置蓝牙连接
				setBluetoothIcon(1);
			} else if (action.equals("com.tchip.BT_DISCONNECTED")) { // 外置蓝牙断开
				setBluetoothIcon(0);
			}
		}

	}

	/**
	 * 设置飞行模式图标
	 */
	private void setAirplaneIcon(boolean isAirplaneOn) {
		if (isAirplaneOn) {
			imageAirplane.setBackground(getResources().getDrawable(
					R.drawable.ic_qs_airplane_on));
		} else {
			imageAirplane.setBackground(getResources().getDrawable(
					R.drawable.ic_qs_airplane_off));
		}
	}

	/**
	 * 设置外置蓝牙图标
	 */
	private void setBluetoothIcon(int bluetoothState) {
		boolean isExtBluetoothOn = NetworkUtil
				.isExtBluetoothOn(getApplicationContext());
		if (isExtBluetoothOn) {
			if (bluetoothState == -1) {
				bluetoothState = 0;
			}
		} else {
			bluetoothState = -1;
		}

		switch (bluetoothState) {
		case 0: // 打开未连接
			imageBluetooth.setBackground(getResources().getDrawable(
					R.drawable.ic_qs_bluetooth_not_connected));
			break;

		case 1: // 打开并连接
			imageBluetooth.setBackground(getResources().getDrawable(
					R.drawable.ic_qs_bluetooth_on));
			break;

		case -1: // 关闭
		default:
			imageBluetooth.setBackground(getResources().getDrawable(
					R.drawable.ic_qs_bluetooth_off));
			break;

		}
	}

	/**
	 * 序列任务线程，分步执行：
	 * 
	 * 1.初次启动清空录像文件夹
	 * 
	 * 2.自动录像
	 * 
	 * 3.初始化服务：轨迹记录
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

					// File file = new File(sdcardPath + "tachograph/");
					// StorageUtil.RecursionDeleteFile(file);
					// MyLog.e("Delete video directory:tachograph !!!");

					editor.putBoolean("isFirstLaunch", false);
					editor.commit();
				} else {
					MyLog.e("Video card not exist or isn't first launch");
				}

				// 检查并删除异常视频文件，比较耗时阻塞线程
				// if (StorageUtil.isVideoCardExists()
				// && !MyApplication.isVideoReording) {
				// CheckErrorFile();
				// }

				// 自动录像:如果已经在录像则不处理
				if (Constant.Record.autoRecord
						&& !MyApplication.isVideoReording) {
					Thread.sleep(Constant.Record.autoRecordDelay);
					Message message = new Message();
					message.what = 1;
					autoHandler.sendMessage(message);
				}
				// 启动服务
				Thread.sleep(1000);
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

	/**
	 * 后台线程，用以监测是否需要录制碰撞加锁视频(停车侦测)
	 */
	public class BackThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(500);

					Message message = new Message();
					message.what = 1;
					backHandler.sendMessage(message);
					// 修正标志：不对第二段视频加锁
					if (!MyApplication.isVideoReording
							&& MyApplication.isVideoLockSecond) {
						MyApplication.isVideoLockSecond = false;
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

				// 停车侦测录像
				if (MyApplication.shouldCrashRecord) {
					if (Constant.Record.parkVideoLock) { // 是否需要加锁
						MyApplication.isVideoLock = true;
					}
					MyApplication.shouldCrashRecord = false;

					// 点亮屏幕
					if (!powerManager.isScreenOn()) {
						SettingUtil.lightScreen(getApplicationContext());
					}

					if (!MyApplication.isMainForeground) {
						// 发送Home键，回到主界面
						sendBroadcast(new Intent("com.tchip.powerKey")
								.putExtra("value", "home"));
					}

					new Thread(new RecordWhenCrashThread()).start();
				}
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 底层碰撞后录制一个视频线程
	 */
	public class RecordWhenCrashThread implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Message message = new Message();
			message.what = 1;
			recordWhenCrashHandler.sendMessage(message);
		}
	}

	final Handler recordWhenCrashHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				recordOneVideoWhenCrash();
				break;

			default:
				break;
			}
		}
	};

	private void recordOneVideoWhenCrash() {
		try {
			if (mRecordState == Constant.Record.STATE_RECORD_STOPPED) {

				// 点亮屏幕
				if (!powerManager.isScreenOn()) {
					SettingUtil.lightScreen(getApplicationContext());
				}

				if (!MyApplication.isMainForeground) {
					// 发送Home键，回到主界面
					sendBroadcast(new Intent("com.tchip.powerKey").putExtra(
							"value", "home"));
				}

				// 开始录像
				new Thread(new StartRecordThread()).start();
			} else if (mRecordState == Constant.Record.STATE_RECORD_STARTED) {
				if (stopRecorder() == 0) {
					mRecordState = Constant.Record.STATE_RECORD_STOPPED;
					MyApplication.isVideoReording = false;

					releaseCameraZone();
				}
			}
			setupRecordViews();
			if (Constant.isDebug) {
				MyLog.v("MyApplication.isVideoReording:"
						+ MyApplication.isVideoReording);
			}
		} catch (Exception e) {
			MyLog.e("[MainActivity]recordOneVideoWhenCrash catch exception: "
					+ e.toString());
		}
	}

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

			update3GSignalStrength(signalStrength.getGsmSignalStrength());
			update3GType();

			super.onSignalStrengthsChanged(signalStrength);
		}

		@Override
		public void onDataConnectionStateChanged(int state) {

			switch (state) {
			case TelephonyManager.DATA_DISCONNECTED:// 网络断开
				MyLog.v("3G TelephonyManager.DATA_DISCONNECTED");
				break;

			case TelephonyManager.DATA_CONNECTING:// 网络正在连接
				MyLog.v("3G TelephonyManager.DATA_CONNECTING");
				break;

			case TelephonyManager.DATA_CONNECTED:// 网络连接上
				MyLog.v("3G TelephonyManager.DATA_CONNECTED");
				break;
			}

			update3GType();
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
	private void update3GSignalStrength(int signal) {
		// imageSignalLevel,image3G.setVisibility(View.GONE);
		simState = telephonyManager.getSimState();
		MyLog.v("[update3GState]SIM State:" + simState);

		if (NetworkUtil.isAirplaneModeOn(getApplicationContext())) {
			imageSignalLevel.setBackground(getResources().getDrawable(
					R.drawable.ic_qs_signal_no_signal));
		} else if (simState == TelephonyManager.SIM_STATE_READY) {
			imageSignalLevel.setBackground(getResources().getDrawable(
					SignalUtil.get3GLevelImageByGmsSignalStrength(signal)));
		} else if (simState == TelephonyManager.SIM_STATE_UNKNOWN
				|| simState == TelephonyManager.SIM_STATE_ABSENT) {
			imageSignalLevel.setBackground(getResources().getDrawable(
					R.drawable.ic_qs_signal_no_signal));
		}
	}

	/**
	 * 更新3G图标
	 */
	private void update3GType() {
		int networkType = telephonyManager.getNetworkType();

		MyLog.v("[update3Gtype]NetworkType:" + networkType);

		image3GType.setBackground(getResources().getDrawable(
				SignalUtil.get3GTypeImageByNetworkType(networkType)));
	}

	/**
	 * 初始化服务
	 */
	private void initialService() {

		// ACC上下电侦测服务
		Intent intentSleepOnOff = new Intent(MainActivity.this,
				SleepOnOffService.class);
		startService(intentSleepOnOff);

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
		textRecordTime.setTypeface(Typefaces.get(this, Constant.Path.FONT
				+ "Font-Quartz-Regular.ttf"));

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

		// 前后摄像头切换
		layoutCameraSwitch = (LinearLayout) findViewById(R.id.layoutCameraSwitch);
		layoutCameraSwitch.setOnClickListener(new MyOnClickListener());

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
		image3GType = (ImageView) findViewById(R.id.image3GType);
		image3GType.setVisibility(View.GONE);

		// 飞行模式图标
		imageAirplane = (ImageView) findViewById(R.id.imageAirplane);

		// 外置蓝牙图标
		imageBluetooth = (ImageView) findViewById(R.id.imageBluetooth);

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

		// 拨号
		ImageView imageDialer = (ImageView) findViewById(R.id.imageDialer);
		imageDialer.setOnClickListener(new MyOnClickListener());

		if (Constant.Module.hasDialer) {
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

		// 切换前后图标
		imageCameraSwitch = (ImageView) findViewById(R.id.imageCameraSwitch);
		imageCameraSwitch.setOnClickListener(new MyOnClickListener());

		updateButtonState(isSurfaceLarge());
	}

	/**
	 * 切换录像预览窗口的大小
	 */
	private void updateSurfaceState() {
		if (!isSurfaceLarge) {
			// 16/9 = 1.7778
			// 854/480 = 1.7791
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
						return;
					} else if (!MyApplication.isPowerConnect) {
						// 电源断开
						MyLog.e("Stop Record:Power is unconnected");
						Message messagePowerUnconnect = new Message();
						messagePowerUnconnect.what = 3;
						updateRecordTimeHandler
								.sendMessage(messagePowerUnconnect);
						return;
					} else if (MyApplication.isSleeping
							&& !MyApplication.shouldStopWhenCrashVideoSave) {
						// 进入低功耗休眠
						MyLog.e("Stop Record:isSleeping = true");
						Message messageSleep = new Message();
						messageSleep.what = 5;
						updateRecordTimeHandler.sendMessage(messageSleep);
						return;
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

				if (MyApplication.shouldStopWhenCrashVideoSave
						&& MyApplication.isVideoReording) {
					if (secondCount == 30) {
						// 停止录像
						if (stopRecorder() == 0) {
							mRecordState = Constant.Record.STATE_RECORD_STOPPED;
							MyApplication.isVideoReording = false;
							setupRecordViews();
							releaseCameraZone();
						} else {
							if (stopRecorder() == 0) {
								mRecordState = Constant.Record.STATE_RECORD_STOPPED;
								MyApplication.isVideoReording = false;
								setupRecordViews();
								releaseCameraZone();
							}
						}

						MyApplication.shouldStopWhenCrashVideoSave = false;

						// 熄灭屏幕,判断当前屏幕是否关闭
						PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
						boolean isScreenOn = pm.isScreenOn();
						if (isScreenOn) {
							sendBroadcast(new Intent("com.tchip.SLEEP_ON"));
							// sendBroadcast(new Intent("com.tchip.powerKey")
							// .putExtra("value", "power"));
						}
					}
				}
				break;

			case 2:
				// SD卡异常移除：停止录像
				if (stopRecorder() == 0) {
					mRecordState = Constant.Record.STATE_RECORD_STOPPED;
					MyApplication.isVideoReording = false;
					setupRecordViews();

					releaseCameraZone();
				} else {
					if (stopRecorder() == 0) {
						mRecordState = Constant.Record.STATE_RECORD_STOPPED;
						MyApplication.isVideoReording = false;
						setupRecordViews();

						releaseCameraZone();
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

					releaseCameraZone();
				} else {
					if (stopRecorder() == 0) {
						mRecordState = Constant.Record.STATE_RECORD_STOPPED;
						MyApplication.isVideoReording = false;
						setupRecordViews();

						releaseCameraZone();
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
				MyApplication.isCrashed = false;

				// 碰撞后判断是否需要加锁第二段视频
				if (mIntervalState == Constant.Record.STATE_INTERVAL_1MIN) {
					if (secondCount > 45) {
						MyApplication.isVideoLockSecond = true;
					}
				} else if (mIntervalState == Constant.Record.STATE_INTERVAL_3MIN) {
					if (secondCount > 165) {
						MyApplication.isVideoLockSecond = true;
					}
				}
				setupRecordViews();
				break;

			case 5:
				// 进入休眠，停止录像
				if (stopRecorder() == 0) {
					mRecordState = Constant.Record.STATE_RECORD_STOPPED;
					MyApplication.isVideoReording = false;
					setupRecordViews();

					releaseCameraZone();
				} else {
					if (stopRecorder() == 0) {
						mRecordState = Constant.Record.STATE_RECORD_STOPPED;
						MyApplication.isVideoReording = false;
						setupRecordViews();

						releaseCameraZone();
					}
				}
				// 如果此时屏幕为点亮状态，则不回收
				boolean isScreenOn = powerManager.isScreenOn();
				if (!isScreenOn) {
					releaseCameraZone();
				}

				MyApplication.shouldResetRecordWhenResume = true;

				String strSleepOn = getResources().getString(
						R.string.stop_record_sleep_on);
				Toast.makeText(getApplicationContext(), strSleepOn,
						Toast.LENGTH_SHORT).show();
				MyLog.e("Record Stop:sleep on.");
				startSpeak(strSleepOn);

				// audioRecordDialog.showErrorDialog(strSleepOn);
				// new Thread(new dismissDialogThread()).start();

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
					if (StorageUtil.isVideoCardExists()) {
						if (mRecordState == Constant.Record.STATE_RECORD_STOPPED) {
							startSpeak(strRecordStart);
						} else if (mRecordState == Constant.Record.STATE_RECORD_STARTED) {
							startSpeak(strRecordStop);
						}
						startOrStopRecord();
					} else {
						noVideoSDHint();
					}
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
					textRecordTime.setText("00 : 00");
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
						textRecordTime.setText("00 : 00");
						textRecordTime.setVisibility(View.INVISIBLE);
						MyApplication.isVideoReording = false;

						if (StorageUtil.isVideoCardExists()) {
							startOrStopRecord();
						} else {
							noVideoSDHint();
						}
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
				}
				break;

			case R.id.imageCameraSwitch:
			case R.id.layoutCameraSwitch:
				sendBroadcast(new Intent("com.tchip.showUVC"));
				break;

			case R.id.layoutWeather:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.WEATHER);
				break;

			case R.id.imageNavi:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.BAIDUNAVI);
				break;

			case R.id.imageMusicOL:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.MUSIC);
				break;

			case R.id.imageEDog:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.EDOG);
				break;

			case R.id.imageMultimedia:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.MULTIMEDIA);
				break;

			case R.id.imageRouteTrack:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.ROUTE);
				break;

			case R.id.imageFmTransmit:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.FMTRANSMIT);
				break;

			case R.id.imageFileExplore:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.FILEEXPLORER);
				break;

			case R.id.imageVoiceChat:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.CHAT);
				break;

			case R.id.imageDialer:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.DIALER);
				break;

			case R.id.imageMessage:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.MMS);
				break;

			case R.id.imageSetting:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.SETTING);
				break;

			case R.id.layoutWiFi:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.WIFI);
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 开启或关闭录像
	 */
	private void startOrStopRecord() {
		try {
			if (mRecordState == Constant.Record.STATE_RECORD_STOPPED) {
				if (MyApplication.isSleeping) {
					startSpeak(getResources().getString(
							R.string.stop_record_sleeping));
				} else {
					// 点亮屏幕
					if (!powerManager.isScreenOn()) {
						SettingUtil.lightScreen(getApplicationContext());
					}

					if (!MyApplication.isMainForeground) {
						// 发送Home键，回到主界面
						sendBroadcast(new Intent("com.tchip.powerKey")
								.putExtra("value", "home"));
					}
					// 开始录像
					new Thread(new StartRecordThread()).start();
				}
			} else if (mRecordState == Constant.Record.STATE_RECORD_STARTED) {
				if (stopRecorder() == 0) {
					mRecordState = Constant.Record.STATE_RECORD_STOPPED;
					MyApplication.isVideoReording = false;
					if (MyApplication.shouldStopWhenCrashVideoSave) {
						MyApplication.shouldStopWhenCrashVideoSave = false;
					}

					releaseCameraZone();
				}
			}
			setupRecordViews();
			if (Constant.isDebug) {
				MyLog.v("MyApplication.isVideoReording:"
						+ MyApplication.isVideoReording);
			}
		} catch (Exception e) {
			MyLog.e("[MainActivity]startOrStopRecord catch exception: "
					+ e.toString());
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
			MyApplication.isVideoLockSecond = false;
			startSpeak(getResources().getString(R.string.video_unlock));
		}
		setupRecordViews();
	}

	/**
	 * 显示或隐藏录像提示悬浮窗
	 */
	private void setRecordHintFloatWindowVisible(boolean isVisible) {
		MyLog.v("[MainActivity]setRecordHintFloatWindowVisible:" + isVisible);
		if (isVisible) {
			Intent intentFloatWindow = new Intent(MainActivity.this,
					FloatWindowService.class);
			startService(intentFloatWindow);
		} else {
			Intent intentFloatWindow = new Intent(MainActivity.this,
					FloatWindowService.class);
			stopService(intentFloatWindow);
		}
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
	 * 如果录像界面不在前台且未在录像，则释放Camera，防止出现熄屏时未在录像仍在预览功耗高的问题
	 * 
	 * 调用地方：在成功执行{@link #stopRecorder}之后
	 */
	private void releaseCameraZone() {
		if (!MyApplication.isMainForeground) {
			release();
			// mHolder = null;
			if (mCamera != null) {
				mCamera.stopPreview();
			}
			MyApplication.shouldResetRecordWhenResume = true;
			MyLog.v("[Record]releaseCameraZone");
		}
	}

	@Override
	protected void onPause() {
		MyApplication.isMainForeground = false;
		// 3G信号
		telephonyManager.listen(myPhoneStateListener,
				PhoneStateListener.LISTEN_NONE);

		// 飞行模式
		if (networkStateReceiver != null) {
			unregisterReceiver(networkStateReceiver);
		}

		MyLog.v("[onPause]MyApplication.isVideoReording:"
				+ MyApplication.isVideoReording);

		if (!MyApplication.isVideoReording) {
			releaseCameraZone();
			MyApplication.isVideoLockSecond = false;
		}

		super.onPause();
	}

	@Override
	protected void onResume() {

		// 触摸声音
		Settings.System.putString(getContentResolver(),
				Settings.System.SOUND_EFFECTS_ENABLED, "1");

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
		telephonyManager.listen(myPhoneStateListener,
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

		// WiFi信号
		updateWiFiState();

		networkStateReceiver = new NetworkStateReceiver();
		IntentFilter networkFilter = new IntentFilter();
		networkFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		networkFilter.addAction("com.tchip.BT_CONNECTED");
		networkFilter.addAction("com.tchip.BT_DISCONNECTED");
		registerReceiver(networkStateReceiver, networkFilter);
		// 飞行模式
		setAirplaneIcon(NetworkUtil.isAirplaneModeOn(getApplicationContext()));
		// 外置蓝牙
		boolean isExtBluetoothConnected = NetworkUtil
				.isExtBluetoothConnected(getApplicationContext());
		if (isExtBluetoothConnected) {
			setBluetoothIcon(1);
		} else {
			setBluetoothIcon(0);
		}

		super.onResume();
	}

	@Override
	protected void onDestroy() {

		// 释放录像区域
		release();

		// 取消注册wifi消息处理器
		if (wifiIntentReceiver != null) {
			unregisterReceiver(wifiIntentReceiver);
		}

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

		// 录音,静音;默认录音
		boolean videoMute = sharedPreferences.getBoolean("videoMute",
				Constant.Record.muteDefault);
		if (videoMute) {
			mMuteState = Constant.Record.STATE_MUTE; // 不录音
		} else {
			mMuteState = Constant.Record.STATE_UNMUTE;
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

	/**
	 * 绘制录像按钮
	 */
	private void setupRecordViews() {
		setRecordHintFloatWindowVisible(MyApplication.isVideoReording);

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
		boolean videoMute = sharedPreferences.getBoolean("videoMute",
				Constant.Record.muteDefault);
		if (videoMute) {
			mMuteState = Constant.Record.STATE_MUTE; // 不录音
			largeVideoMute.setBackground(getResources().getDrawable(
					R.drawable.ui_camera_video_sound_off));
		} else {
			mMuteState = Constant.Record.STATE_UNMUTE;
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
			Camera.Parameters para = mCamera.getParameters();
			para.unflatten(Constant.Record.CAMERA_PARAMS);
			mCamera.setParameters(para);

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
	 * 删除最旧视频，调用此函数的地方：
	 * 
	 * 1.开启录像 {@link #startRecordTask}
	 * 
	 * 2.文件保存回调{@link #onFileSave}
	 */
	private boolean deleteOldestUnlockVideo() {
		try {
			deleteEmptyDirectory();
			String sdcardPath = Constant.Path.SDCARD_1 + File.separator;// "/storage/sdcard1/";
			if (Constant.Record.saveVideoToSD2) {
				sdcardPath = Constant.Path.SDCARD_2 + File.separator;// "/storage/sdcard2/";
			}
			// sharedPreferences.getString("sdcardPath","/mnt/sdcard2");

			float sdFree = StorageUtil.getSDAvailableSize(sdcardPath);
			float sdTotal = StorageUtil.getSDTotalSize(sdcardPath);
			int intSdFree = (int) sdFree;
			MyLog.v("[deleteOldestUnlockVideo] sdFree:" + intSdFree);
			while (intSdFree < Constant.Record.SD_MIN_FREE_STORAGE) {
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

						if (MyApplication.isVideoReording) {
							// TODO:停止录像
							// stopRecorder();
						}
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
						if (intSdFree < Constant.Record.SD_MIN_FREE_STORAGE) {
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
			MyLog.e("[MainActivity]deleteOldestUnlockVideo:Catch Exception:"
					+ e.toString());
			e.printStackTrace();
			return true;
		}
	}

	/**
	 * 删除空文件夹
	 */
	private void deleteEmptyDirectory() {
		File fileRoot = new File(Constant.Path.SDCARD_2 + File.separator
				+ "tachograph/");
		File[] listFileDate = fileRoot.listFiles();
		for (File file : listFileDate) {
			if (file.isDirectory()) {
				int numberChild = file.listFiles().length;
				if (numberChild == 0) {
					file.delete();
					MyLog.v("[deleteEmptyDirectory]Delete Directory:"
							+ file.getName() + ",Length:" + numberChild);
				}
			}
		}

	}

	/**
	 * 录像线程， 调用此线程地方：
	 * 
	 * 1.首次启动录像{@link AutoThread }
	 * 
	 * 2.ACC上电录像 {@link BackThread}
	 * 
	 * 3.停车侦测，录制一个加锁视频
	 */
	private class StartRecordThread implements Runnable {

		@Override
		public void run() {
			int i = 0;
			while (i < 6) {
				try {
					if (!StorageUtil.isVideoCardExists()) {
						// 如果是休眠状态，且不是停车侦测录像情况，避免线程执行过程中，ACC下电后仍然语音提醒“SD不存在”
						if (!MyApplication.shouldStopWhenCrashVideoSave
								&& MyApplication.isSleeping) {
							return;
						}
						Thread.sleep(1000);
						i++;
						MyLog.e("[RetryRecordWhenSDNotMountThread]No SD:try "
								+ i);
						if (i == 5) {
							Message messageRetry = new Message();
							messageRetry.what = 2;
							startRecordHandler.sendMessage(messageRetry);
							return;
						}
					} else {
						// 开始录像
						Message messageRecord = new Message();
						messageRecord.what = 1;
						startRecordHandler.sendMessage(messageRecord);
						return;
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	final Handler startRecordHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (startRecordTask() == 0) {
					mRecordState = Constant.Record.STATE_RECORD_STARTED;
					MyApplication.isVideoReording = true;

					textRecordTime.setVisibility(View.VISIBLE);
					new Thread(new updateRecordTimeThread()).start(); // 更新录制时间

					setupRecordViews();
				} else {
					if (Constant.isDebug)
						MyLog.e("Start Record Failed");
				}
				break;

			case 2:
				// SDCard2不存在
				noVideoSDHint();
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 开启录像
	 * 
	 * @return 0:成功 -1:失败
	 */
	public int startRecordTask() {
		if (mMyRecorder != null) {
			if (deleteOldestUnlockVideo()) {

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

	/**
	 * 视频SD卡不存在提示
	 */
	private void noVideoSDHint() {
		String strNoSD = getResources().getString(R.string.sd1_not_exist);
		if (Constant.Record.saveVideoToSD2) {
			strNoSD = getResources().getString(R.string.sd2_not_exist);
		}
		audioRecordDialog.showErrorDialog(strNoSD);
		new Thread(new dismissDialogThread()).start();
		startSpeak(strNoSD);
	}

	/**
	 * 检查并删除异常视频文件：SD存在但数据库中不存在的文件；视频文件较多时尤为耗时，需要启动线程
	 */
	private void CheckErrorFile() {
		MyLog.v("[CheckErrorFile]isVideoChecking:" + isVideoChecking);
		if (StorageUtil.isVideoCardExists() && !isVideoChecking) {
			new Thread(new CheckVideoThread()).start();
		}
	}

	/**
	 * 当前是否正在校验错误视频
	 */
	private boolean isVideoChecking = false;

	private class CheckVideoThread implements Runnable {

		@Override
		public void run() {
			MyLog.v("[CheckVideoThread]START:" + getTimeStr());
			isVideoChecking = true;
			String sdcardPath = Constant.Path.SDCARD_1 + File.separator; // "/storage/sdcard1/";
			if (Constant.Record.saveVideoToSD2) {
				sdcardPath = Constant.Path.SDCARD_2 + File.separator; // "/storage/sdcard2/";
			}
			File file = new File(sdcardPath + "tachograph/");
			RecursionCheckFile(file);
			MyLog.v("[CheckVideoThread]END:" + getTimeStr());
			isVideoChecking = false;
		}

	}

	private String getTimeStr() {
		long nowTime = System.currentTimeMillis();
		Date date = new Date(nowTime);
		String strs = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
			strs = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strs;
	}

	public void RecursionCheckFile(File file) {
		if (MyApplication.isVideoReording) {
			// 开始录像，终止删除
			MyLog.v("[MainActivity]Stop RecursionCheckFile in case of isVideoReording == true");
			return;
		} else {
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
				MyLog.e("[MainActivity]RecursionCheckFile:Catch Exception:"
						+ e.toString());
			}
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

	/**
	 * 停止录像
	 * 
	 * @return
	 */
	public int stopRecorder() {
		secondCount = -1; // 录制时间秒钟复位
		textRecordTime.setText("00 : 00");
		textRecordTime.setVisibility(View.INVISIBLE);
		if (mMyRecorder != null) {
			MyLog.d("Record Stop");
			AudioPlayUtil.playAudio(getApplicationContext(), FILE_TYPE_VIDEO);

			return mMyRecorder.stop();
		}

		return -1;
	}

	/**
	 * 设置视频分段
	 * 
	 * @param seconds
	 * @return
	 */
	public int setInterval(int seconds) {
		if (mMyRecorder != null) {
			return mMyRecorder.setVideoSeconds(seconds);
		}
		return -1;
	}

	/**
	 * 设置视频重叠
	 * 
	 * @param seconds
	 * @return
	 */
	public int setOverlap(int seconds) {
		if (mMyRecorder != null) {
			return mMyRecorder.setVideoOverlap(seconds);
		}
		return -1;
	}

	/**
	 * 拍照
	 * 
	 * @return
	 */
	public int takePhoto() {
		if (!StorageUtil.isVideoCardExists()) {
			// SDCard不存在
			noVideoSDHint();

			return -1;
		} else if (mMyRecorder != null) {

			// 设置保存路径，否则会保存到内部存储
			if (Constant.Record.saveVideoToSD2) {
				setDirectory(Constant.Path.SDCARD_2);
			} else {
				setDirectory(Constant.Path.SDCARD_1);
			}

			AudioPlayUtil.playAudio(getApplicationContext(), FILE_TYPE_IMAGE);
			return mMyRecorder.takePicture();
		}
		return -1;
	}

	/**
	 * 设置保存路径
	 * 
	 * @param dir
	 * @return
	 */
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

	/**
	 * 设置分辨率
	 * 
	 * @param state
	 * @return
	 */
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
			} else {
				mMyRecorder.setVideoOverlap(0);
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

			// 视频保存失败，原因：存储空间不足，清空文件夹，视频被删掉
			if (stopRecorder() == 0) {
				mRecordState = Constant.Record.STATE_RECORD_STOPPED;
				MyApplication.isVideoReording = false;
				setupRecordViews();
				releaseCameraZone();
			}
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

	/**
	 * 文件保存回调
	 * 
	 * @param type
	 *            0-图片 1-视频
	 * 
	 * @param path
	 *            视频：/mnt/sdcard/tachograph/2015-07-01/2015-07-01_105536.mp4
	 *            图片:/mnt/sdcard/tachograph/camera_shot/2015-07-01_105536.jpg
	 */
	@Override
	public void onFileSave(int type, String path) {
		if (type == 1) {
			secondCount = -1; // 录制时间秒钟复位
			textRecordTime.setText("00 : 00");

			deleteOldestUnlockVideo();

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
				if (MyApplication.isVideoReording
						&& MyApplication.isVideoLockSecond) {
					MyApplication.isVideoLock = true;
					MyApplication.isVideoLockSecond = false; // 不录像时修正加锁图标
				}
			}
			setupRecordViews(); // 更新录制按钮状态
			DriveVideo driveVideo = new DriveVideo(videoName, videoLock,
					videoResolution);
			videoDb.addDriveVideo(driveVideo);

			MyLog.v("[onFileSave]videoLock:" + videoLock
					+ ", isVideoLockSecond:" + MyApplication.isVideoLockSecond);
		} else {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.photo_save),
					Toast.LENGTH_SHORT).show();

			writeExif(path);
		}

		// 更新Media Database
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.parse("file://" + path)));
		MyLog.d("[onFileSave] File Save, Type=" + type + ",Save path:" + path);

		if (!MyApplication.isVideoReording) {
			// 需要在当前视频存储到数据库之后，且当前未录像时再进行;当行车视频较多时，该操作比较耗时
			CheckErrorFile(); // TEST
		}
	}

	/**
	 * 写入EXIF信息
	 * 
	 * @param imagePath
	 */
	private void writeExif(String imagePath) {
		// Android Way
		try {
			ExifInterface exif = new ExifInterface(imagePath);
			// 经度
			String strLongitude = sharedPreferences.getString("longitude",
					"0.00");
			double intLongitude = Double.parseDouble(strLongitude);
			exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, strLongitude);
			exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF,
					intLongitude > 0.0f ? "E" : "W");
			// 纬度
			String strLatitude = sharedPreferences
					.getString("latitude", "0.00");
			double intLatitude = Double.parseDouble(strLatitude);
			exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, strLongitude);
			exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,
					intLatitude > 0.0f ? "N" : "S");
			exif.setAttribute(ExifInterface.TAG_ORIENTATION, ""
					+ ExifInterface.ORIENTATION_NORMAL);

			exif.setAttribute(ExifInterface.TAG_MAKE, "zenlane");
			// 型号/机型
			exif.setAttribute(ExifInterface.TAG_MODEL, "X755");

			// exif.setAttribute(ExifInterface.TAG_FLASH, "1/30"); // 闪光灯
			// exif.setAttribute(ExifInterface.TAG_FOCAL_LENGTH, "5/1"); // 焦距
			// exif.setAttribute(ExifInterface.TAG_WHITE_BALANCE,
			// ExifInterface.WHITEBALANCE_AUTO+"/1"); // 白平衡
			// exif.setAttribute(ExifInterface.TAG_EXPOSURE_TIME, "1/30"); //
			// // 曝光时间
			// exif.setAttribute(ExifInterface.TAG_ISO, "100"); // 感光度
			// exif.setAttribute(ExifInterface.TAG_APERTURE, "2/1"); // 光圈

			exif.saveAttributes();
		} catch (Exception e) {
			MyLog.e("[Android]Set Attribute Catch Exception:" + e.toString());
			e.printStackTrace();
		}

		// JpegHeaders Way
		try {
			JpegHeaders jpegHeaders = new JpegHeaders(imagePath);
			App1Header exifHeader = jpegHeaders.getApp1Header();

			// 遍历显示EXIF
			// SortedMap tags = exifHeader.getTags();
			// for (Map.Entry entry : tags.entrySet()) {
			// System.out.println(entry.getKey() + "[" + entry.getKey().name
			// + "]:" + entry.getValue());
			// }

			// 修改EXIF
			// exifHeader.setValue(Tag.DATETIMEORIGINAL, "2015:05:55 05:55:55");
			exifHeader.setValue(Tag.ORIENTATION, "1"); // 浏览模式/方向:上/左
			exifHeader.setValue(Tag.APERTUREVALUE, "22/10"); // 光圈：2.2
			exifHeader.setValue(Tag.FOCALLENGTH, "7/2"); // 焦距：3.5mm
			exifHeader.setValue(Tag.WHITEBALANCE, "0"); // 白平衡：自动
			exifHeader.setValue(Tag.ISOSPEEDRATINGS, "100"); // ISO感光度：100
			exifHeader.setValue(Tag.EXPOSURETIME, "1/30"); // 曝光时间：1/30
			// 曝光补偿:EV值每增加1.0，相当于摄入的光线量增加一倍，如果照片过亮，要减小EV值，EV值每减小1.0，相当于摄入的光线量减小一倍
			exifHeader.setValue(Tag.EXPOSUREBIASVALUE,
					(1 + new Random().nextInt(10)) + "/10");
			exifHeader.setValue(Tag.METERINGMODE, "1"); // 测光模式：平均
			exifHeader.setValue(Tag.SATURATION,
					"" + (5 + new Random().nextInt(10))); // 饱和度：5-15

			exifHeader.setValue(Tag.FLASH, "0"); // 闪光灯：未使用

			// 保存,参数：是否保存原文件为.old
			jpegHeaders.save(false);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			MyLog.e("[JpegHeaders]Set Attribute Error,FileNotFoundException:"
					+ e.toString());
		} catch (ExifFormatException e) {
			e.printStackTrace();
			MyLog.e("[JpegHeaders]Set Attribute Error,ExifFormatException:"
					+ e.toString());
		} catch (TagFormatException e) {
			e.printStackTrace();
			MyLog.e("[JpegHeaders]Set Attribute Error,TagFormatException:"
					+ e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			MyLog.e("[JpegHeaders]Set Attribute Error,IOException:"
					+ e.toString());
		} catch (JpegFormatException e) {
			e.printStackTrace();
			MyLog.e("[JpegHeaders]Set Attribute Error,JpegFormatException:"
					+ e.toString());
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
	 * 初始化节点状态
	 */
	private void initialNodeState() {
		initialFmTransmit();
		initialAutoLight();
		initialParkingMonitor();
	}

	/**
	 * 启动时初始化FM发射频率节点
	 * 
	 * 频率范围：7600~10800:8750-10800
	 */
	private void initialFmTransmit() {
		try {
			int freq = SettingUtil.getFmFrequceny(this);
			if (freq >= 8750 && freq <= 10800)
				SettingUtil.setFmFrequency(this, freq);
			else
				SettingUtil.setFmFrequency(this, 8750);

			boolean isFmOn = SettingUtil.isFmTransmitOn(MainActivity.this);
			if (isFmOn) {
				Settings.System.putString(getContentResolver(),
						Constant.FMTransmit.SETTING_ENABLE, "1");
				SettingUtil.SaveFileToNode(SettingUtil.nodeFmEnable, "1");

				// 通知状态栏同步图标
				sendBroadcast(new Intent("com.tchip.FM_OPEN_CARLAUNCHER"));
			} else {
				Settings.System.putString(getContentResolver(),
						Constant.FMTransmit.SETTING_ENABLE, "0");
				SettingUtil.SaveFileToNode(SettingUtil.nodeFmEnable, "0");

				// 通知状态栏同步图标
				sendBroadcast(new Intent("com.tchip.FM_CLOSE_CARLAUNCHER"));
			}
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
					"autoScreenLight", Constant.Setting.AUTO_BRIGHT_DEFAULT_ON);
			SettingUtil.setAutoLight(getApplicationContext(), autoScreenLight);
		} catch (Exception e) {
			MyLog.e("[MainActivity]initialAutoLight: Catch Exception!");
		}
	}

	/**
	 * 初始化停车侦测开关
	 */
	private void initialParkingMonitor() {
		try {
			boolean isParkingMonitorOn = sharedPreferences.getBoolean(
					"parkingOn", Constant.Record.parkDefaultOn);
			SettingUtil.setParkingMonitor(getApplicationContext(),
					isParkingMonitorOn);
		} catch (Exception e) {
			MyLog.e("[MainActivity]initialParkingMonitor: Catch Exception!");
		}
	}

}
