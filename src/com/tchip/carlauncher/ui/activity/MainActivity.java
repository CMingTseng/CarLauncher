package com.tchip.carlauncher.ui.activity;

import java.io.File;

import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.MyApplication;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.lib.filemanager.FolderActivity;
import com.tchip.carlauncher.model.DriveVideo;
import com.tchip.carlauncher.model.DriveVideoDbHelper;
import com.tchip.carlauncher.model.Typefaces;
import com.tchip.carlauncher.service.BrightAdjustService;
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
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
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
	private ImageView imageNaviState;

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
		initQuickIconLayout();
		// 录像：配置参数，初始化布局
		setupRecordDefaults();
		setupRecordViews();

		// 序列任务线程
		new Thread(new AutoThread()).start();

		// 3G信号
		MyListener = new MyPhoneStateListener();
		Tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		// SIM卡状态
		simState = Tel.getSimState();
		Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

		// 注册wifi消息处理器
		registerReceiver(wifiIntentReceiver, wifiIntentFilter);
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

				// 自动录像
				if (Constant.Record.autoRecord) {
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
	 * 更改分辨率后重启录像
	 */
	public class StartRecordWhenChangeSizeThread implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(3000);
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
		// 亮度自动调整服务
		if (Constant.Module.hasBrightAdjust) {
			Intent intentBrightness = new Intent(this,
					BrightAdjustService.class);
			startService(intentBrightness);
		}

		// 轨迹记录服务
		Intent intentRoute = new Intent(this, RouteRecordService.class);
		startService(intentRoute);

		// 碰撞侦测服务
		Intent intentSensor = new Intent(this, SensorWatchService.class);
		startService(intentSensor);
	}

	/**
	 * 初始化布局
	 */
	private void initialLayout() {
		// 录像窗口
		if (Constant.Record.hasCamera) {
			surfaceCamera = (SurfaceView) findViewById(R.id.surfaceCamera);
			surfaceCamera.setOnClickListener(new MyOnClickListener());
			surfaceCamera.getHolder().addCallback(this);
		}

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

		imageNaviState = (ImageView) findViewById(R.id.imageNaviState);

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
	 * 初始化导航实例
	 */
	private String mSDCardPath = null;
	private static final String APP_FOLDER_NAME = "CarLauncher";

	private void initialNaviInstance() {
		if (initDirs()) {
		}
		initNavi();
	}

	private boolean initDirs() {
		mSDCardPath = getSdcardDir();
		if (mSDCardPath == null) {
			return false;
		}
		File f = new File(mSDCardPath, APP_FOLDER_NAME);
		if (!f.exists()) {
			try {
				f.mkdir();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	String authinfo = null;
	public static final String ROUTE_PLAN_NODE = "routePlanNode";

	private void initNavi() {
		MyLog.v("Navi Instance is Initialing...");
		BaiduNaviManager.getInstance().setNativeLibraryPath(
				mSDCardPath + "/BaiduNaviSDK_SO");
		BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME,
				new NaviInitListener() {
					@Override
					public void onAuthResult(int status, String msg) {
						if (0 == status) {
							authinfo = "Success!";
							MyApplication.isNaviAuthSuccess = true;
						} else {
							authinfo = "Fail:" + msg;
							MyApplication.isNaviAuthSuccess = false;
						}

						MyLog.v("Baidu Navi:Key auth " + authinfo);
					}

					public void initSuccess() {
						// 导航初始化是异步的，需要一小段时间，以这个标志来识别引擎是否初始化成功，为true时候才能发起导航
						MyApplication.isNaviInitialSuccess = true;
						MyLog.v("Baidu Navi:Initial Success!");
						refreshNaviState();
					}

					public void initStart() {
						MyLog.v("Baidu Navi:Initial Start!");
					}

					public void initFailed() {
						MyApplication.isNaviInitialSuccess = false;
						MyLog.v("Baidu Navi:Initial Fail!");
						refreshNaviState();
					}
				}, /* null */mTTSCallback);
	}

	BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {

		@Override
		public void stopTTS() {

		}

		@Override
		public void resumeTTS() {

		}

		@Override
		public void releaseTTSPlayer() {

		}

		@Override
		public int playTTSText(String text, int arg1) {
			startSpeak(text);
			return 0;
		}

		@Override
		public void phoneHangUp() {

		}

		@Override
		public void phoneCalling() {

		}

		@Override
		public void pauseTTS() {

		}

		@Override
		public void initTTSPlayer() {

		}

		@Override
		public int getTTSState() {
			return 1;
		}
	};

	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(
				Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
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
						MyLog.e("Stop Record:Power is unconnected");
						Message messageEject = new Message();
						messageEject.what = 3;
						updateRecordTimeHandler.sendMessage(messageEject);
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

		if (quickWifi != null)
			quickWifi
					.setImageResource(wifiManager.isWifiEnabled() ? R.drawable.quick_icon_wifi_on
							: R.drawable.quick_icon_wifi_off);

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
				if (!isSurfaceLarge) {
					int widthFull = 854;
					int heightFull = 480;
					surfaceCamera
							.setLayoutParams(new RelativeLayout.LayoutParams(
									widthFull, heightFull));
					isSurfaceLarge = true;

					// 更新HorizontalScrollView阴影
					imageShadowLeft.setVisibility(View.GONE);
					imageShadowRight.setVisibility(View.GONE);

					updateButtonState(true);
				} else {
					int widthSmall = 480;
					int heightSmall = 270;
					surfaceCamera
							.setLayoutParams(new RelativeLayout.LayoutParams(
									widthSmall, heightSmall));
					isSurfaceLarge = false;

					// 更新HorizontalScrollView阴影
					hsvMain.scrollTo(0, 0);
					imageShadowLeft.setVisibility(View.GONE);
					imageShadowRight.setVisibility(View.VISIBLE);

					updateButtonState(false);
				}
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
					textRecordTime.setText("00:00:00");
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
						if (setInterval(5 * 60) == 0) {
							mIntervalState = Constant.Record.STATE_INTERVAL_5MIN;
							editor.putString("videoTime", "5");
							startSpeak(getResources().getString(
									R.string.hint_video_time_5));
						}
					} else if (mIntervalState == Constant.Record.STATE_INTERVAL_5MIN) {
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
					if (mMuteState == Constant.Record.STATE_MUTE) {
						if (setMute(false) == 0) {
							mMuteState = Constant.Record.STATE_UNMUTE;
							startSpeak(getResources().getString(
									R.string.hint_video_mute_off));
						}
					} else if (mMuteState == Constant.Record.STATE_UNMUTE) {
						if (setMute(true) == 0) {
							mMuteState = Constant.Record.STATE_MUTE;
							startSpeak(getResources().getString(
									R.string.hint_video_mute_on));
						}
					}
					setupRecordViews();
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
					if (NetworkUtil.isNetworkConnected(getApplicationContext())) {
						if (!MyApplication.isNaviAuthSuccess) {
							MyLog.e("Navigation:Auth Fail");
							if (NetworkUtil
									.isNetworkConnected(getApplicationContext())) {
								initialNaviInstance();
							}
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.hint_navi_auth_fail),
									Toast.LENGTH_SHORT).show();
						} else if (!MyApplication.isNaviInitialSuccess) {
							if (NetworkUtil
									.isNetworkConnected(getApplicationContext())) {
								initialNaviInstance();
							}
							MyLog.e("Navigation:Initial Fail");
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.hint_navi_init_fail),
									Toast.LENGTH_SHORT).show();
						} else {
							MyLog.v("Navi Instance Already Initial Success");
							Intent intentNavi = new Intent(MainActivity.this,
									NavigationActivity.class);
							startActivity(intentNavi);
							overridePendingTransition(
									R.anim.zms_translate_up_out,
									R.anim.zms_translate_up_in);
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
						// 思必驰语音
						// intentVoiceChat = new Intent(MainActivity.this,
						// WakeUpCloudAsr.class);
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

	/**
	 * 开启或关闭录像
	 */
	private void startOrStopRecord() {
		if (mRecordState == Constant.Record.STATE_RECORD_STOPPED) {
			if (startRecorder() == 0) {
				mRecordState = Constant.Record.STATE_RECORD_STARTED;
				MyApplication.isVideoReording = true;
			} else {
				if (Constant.isDebug)
					MyLog.e("Start Record Failed");
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
				quickWifi.setImageResource(R.drawable.quick_icon_wifi_oning);
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
						if (!MyApplication.isNaviAuthSuccess
								|| !MyApplication.isNaviInitialSuccess) {
							MyLog.v("updateWifiThread:Initial Navigation!");
							Message messageNavi = new Message();
							messageNavi.what = 2;
							updateWifiHandler.sendMessage(messageNavi);
						}
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

			case 2:
				initialNaviInstance();
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
	 * 导航初始化情况
	 */
	private void refreshNaviState() {
		if (MyApplication.isNaviAuthSuccess
				&& MyApplication.isNaviInitialSuccess) {
			imageNaviState.setImageResource(R.drawable.ui_main_navi_state_yes);
		} else {
			imageNaviState.setImageResource(R.drawable.ui_main_navi_state_no);
		}
	}

	@Override
	protected void onPause() {
		// 3G信号
		Tel.listen(MyListener, PhoneStateListener.LISTEN_NONE);

		super.onPause();
	}

	@Override
	protected void onResume() {
		refreshNaviState();

		// 更新录像界面按钮状态
		refreshRecordButton();
		setupRecordViews();

		// 3G信号
		Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

		// 导航实例
		if (MyApplication.isNaviAuthSuccess) {
			Log.v(Constant.TAG, "Navi Instance Already Initial Success");
		} else if (NetworkUtil.isNetworkConnected(getApplicationContext())) {
			initialNaviInstance();
			Log.v(Constant.TAG, "Navi Instance is Initialing...");
		}

		// 初始化fm发射
		initFmTransmit();

		initQuickIconStatus();

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
		mOverlapState = Constant.Record.STATE_OVERLAP_FIVE;

		mMuteState = Constant.Record.STATE_UNMUTE;
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
		String videoTimeStr = sharedPreferences.getString("videoTime", "5");
		if ("3".equals(videoTimeStr)) {
			mIntervalState = Constant.Record.STATE_INTERVAL_3MIN;
		} else {
			mIntervalState = Constant.Record.STATE_INTERVAL_5MIN;
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
		if (mIntervalState == Constant.Record.STATE_INTERVAL_3MIN) {
			largeVideoTime.setBackground(getResources().getDrawable(
					R.drawable.ui_camera_video_time_3));
		} else if (mIntervalState == Constant.Record.STATE_INTERVAL_5MIN) {
			largeVideoTime.setBackground(getResources().getDrawable(
					R.drawable.ui_camera_video_time_5));
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

		// 静音
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
	}

	private boolean openCamera() {
		if (mCamera != null) {
			closeCamera();
		}
		try {
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
		if (file.isFile()) {
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
		textRecordTime.setText("00:00:00");
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
		if (mIntervalState == Constant.Record.STATE_INTERVAL_5MIN) {
			mMyRecorder.setVideoSeconds(5 * 60);
		} else {
			mMyRecorder.setVideoSeconds(3 * 60);
		}
		if (mOverlapState == Constant.Record.STATE_OVERLAP_FIVE) {
			mMyRecorder.setVideoOverlap(5);
		}
		mMyRecorder.prepare();
	}

	private void releaseRecorder() {
		if (mMyRecorder != null) {
			mMyRecorder.stop();
			mMyRecorder.close();
			mMyRecorder.release();
			mMyRecorder = null;
			MyLog.d("Record Release");
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
		deleteOldestUnlockVideo();

		if (type == 1) {
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
	 * 启动时初始化fm发射
	 */
	// 频率节点 频率范围：7600~10800:8750-10800

	private void initFmTransmit() {
		// if (isFmTransmitOn())
		{
			int freq = SettingUtil.getFmFrequceny(this);
			// Toast.makeText(this, "freq : " + freq, Toast.LENGTH_LONG).show();
			if (freq >= 8750 && freq <= 10800)
				SettingUtil.setFmFrequency(this, freq);
			else
				SettingUtil.setFmFrequency(this, 8750);
		}
	}

	/**
	 * FM发射是否打开
	 * 
	 * @return
	 */
	private boolean isFmTransmitOn() {
		boolean isFmTransmitOpen = false;
		String fmEnable = Settings.System.getString(getContentResolver(),
				Constant.FMTransmit.SETTING_ENABLE);
		if (fmEnable.trim().length() > 0) {
			if ("1".equals(fmEnable)) {
				isFmTransmitOpen = true;
			} else {
				isFmTransmitOpen = false;
			}
		}
		return isFmTransmitOpen;
	}

	/**
	 * 主界面快捷按键
	 */
	LinearLayout quickIconLayout;
	ImageButton quickWifi, quickFm, quickGPS;

	private void initQuickIconLayout() {
		quickIconLayout = (LinearLayout) findViewById(R.id.quick_icon_layout);
		quickWifi = (ImageButton) findViewById(R.id.quick_wifi);
		quickFm = (ImageButton) findViewById(R.id.quick_fm);
		quickGPS = (ImageButton) findViewById(R.id.quick_gps);

		quickWifi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!wifiManager.isWifiEnabled()) {
					wifiManager.setWifiEnabled(true);
					quickWifi.setImageResource(R.drawable.quick_icon_wifi_on);
				} else {
					wifiManager.setWifiEnabled(false);
					quickWifi.setImageResource(R.drawable.quick_icon_wifi_off);
				}
			}
		});
		quickWifi.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if (Constant.Module.isWifiSystem) {
					startActivity(new Intent(
							android.provider.Settings.ACTION_WIFI_SETTINGS));
				} else {
					Intent intentWiFi = new Intent(MainActivity.this,
							WifiListActivity.class);
					startActivity(intentWiFi);
				}
				return false;
			}
		});

		quickFm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean fmOpen = isFmTransmitOn();
				Settings.System
						.putString(getContentResolver(),
								Constant.FMTransmit.SETTING_ENABLE,
								!fmOpen ? "1" : "0");
				SettingUtil.SaveFileToNode(SettingUtil.nodeFmEnable,
						(!fmOpen ? "1" : "0"));

				quickFm.setImageResource((!fmOpen) ? R.drawable.quick_icon_fm_on
						: R.drawable.quick_icon_fm_off);
			}
		});
		quickFm.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intentFmTransmit = new Intent(MainActivity.this,
						FmTransmitActivity.class);
				startActivity(intentFmTransmit);
				overridePendingTransition(R.anim.zms_translate_up_out,
						R.anim.zms_translate_up_in);
				return false;
			}
		});

		quickGPS.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		quickGPS.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

	/*
	 * 初始化quick icon状态
	 */
	private void initQuickIconStatus() {
		quickWifi
				.setImageResource(wifiManager.isWifiEnabled() ? R.drawable.quick_icon_wifi_on
						: R.drawable.quick_icon_wifi_off);
		quickFm.setImageResource(isFmTransmitOn() ? R.drawable.quick_icon_fm_on
				: R.drawable.quick_icon_fm_off);
	}
}
