package com.tchip.carlauncher;

import android.os.Environment;

public interface Constant {
	/**
	 * Debug：打印Log
	 */
	public static final boolean isDebug = true;

	/**
	 * 日志Tag
	 */
	public static final String TAG = "ZMS";

	public static final class Setting {

		/**
		 * 最大亮度
		 */
		public static final int MAX_BRIGHTNESS = 196; // 255;

		/**
		 * 默认亮度
		 */
		public static final int DEFAULT_BRIGHTNESS = 180;

		/**
		 * Camera自动调节亮度是否打开
		 */
		public static final boolean AUTO_BRIGHT_DEFAULT_ON = false;
	}

	public static final class GravitySensor {
		/**
		 * 碰撞侦测是否默认打开
		 */
		public static final boolean DEFAULT_ON = true;

		/**
		 * 碰撞侦测默认灵敏度Level
		 */
		public static final float VALUE = 9.8f;

		public static final int SENSITIVE_LOW = 0;
		public static final int SENSITIVE_MIDDLE = 1;
		public static final int SENSITIVE_HIGH = 2;
		public static final int SENSITIVE_DEFAULT = SENSITIVE_MIDDLE;

		public static final float VALUE_LOW = VALUE * 1.8f;
		public static final float VALUE_MIDDLE = VALUE * 1.5f;
		public static final float VALUE_HIGH = VALUE * 1;
		public static final float VALUE_DEFAULT = VALUE_MIDDLE;

	}

	public static final class Record {
		/**
		 * 是否包含录像模块，去掉可用作其他平台做对比测试
		 */
		public static final boolean hasCamera = true;

		/**
		 * 是否开机自动录像
		 */
		public static final boolean autoRecord = true;

		/**
		 * 默认是否静音
		 */
		public static final boolean muteDefault = false;

		/**
		 * 停车侦测录像是否加锁
		 */
		public static final boolean parkVideoLock = false;

		/**
		 * 停车侦测是否默认打开
		 */
		public static final boolean parkDefaultOn = true;

		/**
		 * 开机自动录像延时
		 */
		public static final int autoRecordDelay = 2500;

		/**
		 * 录像保存到SD卡2 true:SD2 false:SD1
		 */
		public static final boolean saveVideoToSD2 = true;

		/**
		 * 循环录像保留空间(单位：字节B)
		 */
		public static final long SD_MIN_FREE_STORAGE = 500 * 1024 * 1024; // 500M

		// 分辨率
		public static final int STATE_RESOLUTION_720P = 0;
		public static final int STATE_RESOLUTION_1080P = 1;

		// 录像状态
		public static final int STATE_RECORD_STARTED = 0;
		public static final int STATE_RECORD_STOPPED = 1;

		// 视频分段
		public static final int STATE_INTERVAL_3MIN = 0;
		public static final int STATE_INTERVAL_1MIN = 2;
		// public static final int STATE_INTERVAL_5MIN = 1;

		// 第二视图
		public static final int STATE_SECONDARY_ENABLE = 0;
		public static final int STATE_SECONDARY_DISABLE = 1;

		// 路径
		public static final int STATE_PATH_ZERO = 0;
		public static final int STATE_PATH_ONE = 1;
		public static final int STATE_PATH_TWO = 2;
		public static final String PATH_ZERO = "/mnt/sdcard";

		// 重叠
		public static final int STATE_OVERLAP_ZERO = 0;
		public static final int STATE_OVERLAP_FIVE = 1;

		// 静音
		public static final int STATE_MUTE = 0;
		public static final int STATE_UNMUTE = 1;

		/**
		 * 相机参数
		 */
		public static final String CAMERA_PARAMS = "zoom=0;fb-smooth-level-max=4;max-num-detected-faces-hw=15;"
				+ "cap-mode=normal;whitebalance=auto;afeng-min-focus-step=0;"
				+ "preview-format-values=yuv420sp,yuv420p,yuv420i-yyuvyy-3plane;"
				+ "rotation=0;jpeg-thumbnail-quality=100;preview-format=yuv420sp;"
				+ "iso-speed=auto;hue-values=low,middle,high;preview-frame-rate=30;"
				+ "jpeg-thumbnail-width=160;"
				+ "scene-mode-values=auto,portrait,landscape,night,night-portrait,theatre,beach,snow,sunset,steadyphoto,fireworks,sports,party,candlelight,hdr;"
				+ "video-size=1920x1088;preview-fps-range-values=(5000,60000);"
				+ "contrast-values=low,middle,high;"
				+ "preview-size-values=176x144,320x240,352x288,480x320,480x368,640x480,720x480,800x480,800x600,864x480,960x540,1280x720;"
				+ "auto-whitebalance-lock=false;preview-fps-range=5000,60000;"
				+ "antibanding=auto;min-exposure-compensation=-3;max-num-focus-areas=1;"
				+ "vertical-view-angle=49;fb-smooth-level-min=-4;eng-focus-fullscan-frame-interval=0;"
				+ "fb-skin-color=0;brightness_value=17;video-stabilization-supported=true;"
				+ "saturation-values=low,middle,high;eng-flash-duty-value=-1;edge=middle;"
				+ "iso-speed-values=auto,100,200,400,800,1600;picture-format-values=jpeg;"
				+ "exposure-compensation-step=1.0;eng-flash-duty-min=0;picture-size=2560x1440;"
				+ "saturation=middle;picture-format=jpeg;"
				+ "whitebalance-values=auto,incandescent,fluorescent,warm-fluorescent,daylight,cloudy-daylight,twilight,shade;"
				+ "afeng-max-focus-step=0;eng-shading-table=0;"
				+ "preferred-preview-size-for-video=1280x720;hue=middle;"
				+ "eng-focus-fullscan-frame-interval-max=65535;recording-hint=true;"
				+ "video-stabilization=false;zoom-supported=true;fb-smooth-level=0;"
				+ "fb-sharp=0;contrast=middle;eng-save-shading-table=0;jpeg-quality=90;"
				+ "scene-mode=auto;burst-num=1;metering-areas=(0,0,0,0,0);eng-flash-duty-max=1;"
				+ "video-size-values=176x144,480x320,640x480,864x480,1280x720,1920x1080;"
				+ "eng-focus-fullscan-frame-interval-min=0;focal-length=3.5;"
				+ "preview-size=1280x720;rec-mute-ogg=0;"
				+ "cap-mode-values=normal,face_beauty,continuousshot,smileshot,bestshot,evbracketshot,autorama,mav,asd;"
				+ "preview-frame-rate-values=15,24,30;max-num-metering-areas=9;fb-sharp-max=4;"
				+ "sensor-type=252;focus-mode-values=auto,macro,infinity,continuous-picture,continuous-video,manual,fullscan;"
				+ "fb-sharp-min=-4;jpeg-thumbnail-size-values=0x0,160x128,320x240;"
				+ "zoom-ratios=100,114,132,151,174,200,229,263,303,348,400;"
				+ "picture-size-values=320x240,640x480,1024x768,1280x720,1280x768,1280x960,1600x1200,2048x1536,2560x1440,2560x1920;"
				+ "edge-values=low,middle,high;horizontal-view-angle=53;brightness=middle;"
				+ "eng-flash-step-max=0;jpeg-thumbnail-height=128;capfname=/sdcard/DCIM/cap00;"
				+ "smooth-zoom-supported=true;zsd-mode=off;focus-mode=auto;auto-whitebalance-lock-supported=true;"
				+ "fb-skin-color-max=4;fb-skin-color-min=-4;max-num-detected-faces-sw=0;"
				+ "video-frame-format=yuv420p;max-exposure-compensation=3;focus-areas=(0,0,0,0,0);"
				+ "exposure-compensation=0;video-snapshot-supported=true;"
				+ "brightness-values=low,middle,high;auto-exposure-lock=false;"
				+ "effect-values=none,mono,negative,sepia,aqua,whiteboard,blackboard;"
				+ "eng-flash-step-min=0;effect=none;max-zoom=10;focus-distances=0.95,1.9,Infinity;"
				+ "mtk-cam-mode=2;auto-exposure-lock-supported=true;zsd-mode-values=off,on;"
				+ "antibanding-values=off,50hz,60hz,auto";
	}

	public static final class Module {
		/**
		 * 主界面是否显示讯飞语音助手
		 */
		public static final boolean hasVoiceChat = false;

		/**
		 * 主界面是否显示设置入口
		 */
		public static final boolean hasSetting = false;

		/**
		 * 休眠时是否静音
		 */
		public static final boolean muteWhenSleep = false;

		/**
		 * 导航是否是百度:true-百度 false-独立导航Demo(百度SDK)
		 */
		public static final boolean isNavigationBaidu = false;

		/**
		 * 拔掉电源熄灭屏幕
		 */
		public static final boolean autoCloseScreen = false;

		/**
		 * 是否有用户中心
		 */
		public static final boolean hasUserCenter = false;

		/**
		 * 是否有电子狗
		 */
		public static final boolean hasEDog = true;

		/**
		 * 是否有拨号短信模块
		 */
		public static final boolean hasDialer = false;

		/**
		 * 在线音乐是酷我还是酷狗
		 */
		public static final boolean isOnlineMusicKuwo = true;

		/**
		 * 本地音乐是否是系统音乐
		 */
		public static final boolean isLocalMusicSystem = true;

		/**
		 * 是否有文件管理功能
		 */
		public static final boolean hasFileManager = true;

		/**
		 * WiFi设置是否使用系统
		 */
		public static final boolean isWifiSystem = true;

		/**
		 * 语音模块是否是讯飞：true-讯飞 false-思必驰
		 */
		public static final boolean isVoiceXunfei = true;
	}

	/**
	 * FM发射
	 */
	public static final class FMTransmit {
		/**
		 * 系统设置：FM发射开关
		 */
		public static final String SETTING_ENABLE = "fm_transmitter_enable";

		/**
		 * 系统设置：FM发射频率
		 */
		public static final String SETTING_CHANNEL = "fm_transmitter_channel";

		public static final int CHANNEL_LOW = 8750;
		public static final int CHANNEL_MIDDLE = 9750;
		public static final int CHANNEL_HIGH = 10750;

		public static final String HINT_LOW = "87.5";
		public static final String HINT_MIDDLE = "97.5";
		public static final String HINT_HIGH = "107.5";
	}

	/**
	 * 行驶轨迹
	 */
	public static final class RouteTrack {
		/**
		 * 存储位置
		 */
		public static final String PATH = Environment
				.getExternalStorageDirectory().getPath() + "/Route/";

		/**
		 * 扩展名
		 */
		public static final String EXTENSION = ".art"; // Auto Route Track
	}

	/**
	 * 路径
	 */
	public static final class Path {
		/**
		 * SDcard Path
		 */
		public static final String SD_CARD = Environment
				.getExternalStorageDirectory().getPath();

		public static final String SDCARD_1 = "/storage/sdcard1";
		public static final String SDCARD_2 = "/storage/sdcard2";

		/**
		 * 音乐背景图片目录
		 */
		public static final String MUSIC_IMAGE = "MusicBackground";

		/**
		 * 字体目录
		 */
		public static final String FONT = "fonts/";

		/**
		 * 百度离线地图子级目录
		 */
		public static final String BAIDU_OFFLINE_SUB = "/storage/sdcard0/BaiduMapSDK/vmp/l/";

		/**
		 * 百度离线地图，存储卡位置
		 */
		public static final String SD_CARD_MAP = "/storage/sdcard1";
	}

	public static final class MagicCode {

		/**
		 * 进入MagicActivity的密码
		 */
		public static final String PASSWORD = "55555";

		/**
		 * 启动测试应用命令:DeviceTest
		 */
		public static final String DEVICE_TEST = "*#86*#";

		/**
		 * 启动工程模式：EngineerMode
		 */
		public static final String ENGINEER_MODE = "*#*#3646633#*#*";

		/**
		 * 启动系统设置:Setting
		 */
		public static final String SETTING = "*#7388464#*";
	}

	/**
	 * FACE++ SDK
	 */
	public static final class FacePlusPlus {
		public static final String API_KEY = "543e743fa43f0550c2977995f3ff2222";
		public static final String API_SECRET = "IEpqaPm-wa-eznyZfKhvwW8rEGgzLxRk";
	}

	/**
	 * 讯飞语音SDK
	 */
	public static final String XUNFEI_APP_ID = "5531bef5";

	/**
	 * 百度地图SDK
	 */
	public static final class BaiduMap {
		public static final String API_KEY = "Tycks4ezX3hSXfRtu7TvCVTl";
		public static final String MCODE = "6B:40:B2:47:13:F5:6A:F7:40:6A:89:84:46:53:33:47:AD:DC:C1:0C;com.tchip.carlauncher";

		/**
		 * 轨迹绘制取样精度：每1个点取1个
		 */
		public static final int ROUTE_POINT_OFFSET_HIGH = 1;

		/**
		 * 轨迹绘制取样精度：每2个点取1个
		 */
		public static final int ROUTE_POINT_OFFSET_MIDDLE = 2;

		/**
		 * 轨迹绘制取样精度：每5个点取1个
		 */
		public static final int ROUTE_POINT_OFFSET_LOW = 5;

		/**
		 * 轨迹绘制取样默认精度
		 */
		public static final int ROUTE_POINT_OFFSET_DEFAULT = ROUTE_POINT_OFFSET_HIGH;
	}

	/**
	 * SharedPreferences名称
	 */
	public static final String SHARED_PREFERENCES_NAME = "CarLauncher";
	public static final String SHARED_PREFERENCES_SPEECH_NAME = "speech";

	public static final class Music {

		public static final String BROADCAST_NAME = "com.tchip.carlauncher.music.broadcast";
		public static final String SERVICE_NAME = "com.tchip.carlauncher.music.service.MusicMediaService";
		public static final String BROADCAST_QUERY_COMPLETE_NAME = "com.tchip.carlauncher.music.querycomplete.broadcast";
		public static final String BROADCAST_CHANGEBG = "com.tchip.carlauncher.music.changebg";
		public static final String BROADCAST_SHAKE = "com.tchip.carlauncher.music.shake";
		public static final String SHAKE_ON_OFF = "SHAKE_ON_OFF"; // 是否开启了振动模式
		public static final String SP_NAME = "com.tchip.carlauncher.music_preference";
		public static final String SP_BG_PATH = "bg_path";
		public static final String SP_SHAKE_CHANGE_SONG = "shake_change_song";
		public static final String SP_AUTO_DOWNLOAD_LYRIC = "auto_download_lyric";
		public static final String SP_FILTER_SIZE = "filter_size";
		public static final String SP_FILTER_TIME = "filter_time";
		public static final int REFRESH_PROGRESS_EVENT = 0x100;
		// 播放状态
		public static final int MPS_NOFILE = -1; // 无音乐文件
		public static final int MPS_INVALID = 0; // 当前音乐文件无效
		public static final int MPS_PREPARE = 1; // 准备就绪
		public static final int MPS_PLAYING = 2; // 播放中
		public static final int MPS_PAUSE = 3; // 暂停
		// 播放模式
		public static final int MPM_LIST_LOOP_PLAY = 0; // 列表循环
		public static final int MPM_ORDER_PLAY = 1; // 顺序播放
		public static final int MPM_RANDOM_PLAY = 2; // 随机播放
		public static final int MPM_SINGLE_LOOP_PLAY = 3; // 单曲循环

		public static final String PLAY_STATE_NAME = "PLAY_STATE_NAME";
		public static final String PLAY_MUSIC_INDEX = "PLAY_MUSIC_INDEX";

		// 歌手和专辑列表点击都会进入MyMusic 此时要传递参数表明是从哪里进入的
		public static final String FROM = "from";
		public static final int START_FROM_ARTIST = 1;
		public static final int START_FROM_ALBUM = 2;
		public static final int START_FROM_LOCAL = 3;
		public static final int START_FROM_FOLDER = 4;
		public static final int START_FROM_FAVORITE = 5;

		public static final int FOLDER_TO_MYMUSIC = 6;
		public static final int ALBUM_TO_MYMUSIC = 7;
		public static final int ARTIST_TO_MYMUSIC = 8;

		public static final int MENU_BACKGROUND = 9;
	}

	/**
	 * 设置条目点击波纹速度
	 */
	public static final int SETTING_ITEM_RIPPLE_SPEED = 80;
}
