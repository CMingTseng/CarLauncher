package com.tchip.carlauncher;

import java.io.File;

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

	public static final class Record {

		/**
		 * 是否包含录像模块，去掉可用作其他平台做对比测试
		 */
		public static boolean hasCamera = true;

		/**
		 * 是否开机自动录像
		 */
		public static boolean autoRecord = true;

		/**
		 * 开机自动录像延时
		 */
		public static int autoRecordDelay = 5000;

		/**
		 * 录像保存到SD卡2 true:SD2 false:SD1
		 */
		public static boolean saveVideoToSD2 = true;

		/**
		 * 循环录像保留空间(单位：字节B)
		 */
		public static final long SD_MIN_FREE_STORAGE = 500 * 1024 * 1024; // 500M

		/**
		 * 循环录像最大占用空间百分比
		 */
		public static final float SD_MIN_FREE_PERCENT = 0.1f; // 10%

		// 分辨率
		public static final int STATE_RESOLUTION_720P = 0;
		public static final int STATE_RESOLUTION_1080P = 1;

		// 录像状态
		public static final int STATE_RECORD_STARTED = 0;
		public static final int STATE_RECORD_STOPPED = 1;

		// 视频分段
		public static final int STATE_INTERVAL_3MIN = 0;
		public static final int STATE_INTERVAL_5MIN = 1;

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
	}

	public static final class Module {
		/**
		 * 是否有拨号短信模块
		 */
		public static final boolean hasDialer = false;

		/**
		 * 在线音乐是酷我还是酷狗
		 */
		public static final boolean isMusicKuwo = false;

		/**
		 * 是否有亮度自动调整功能
		 */
		public static final boolean hasBrightAdjust = false;

		/**
		 * 天气界面是否有动画
		 */
		public static final boolean hasWeatherAnimation = false;

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
	 * 思必驰
	 */
	public static final class AiSpeech {
		public static final String API_KEY = "1437739850000406";// 添加您的APPKEY"1366851760000038 "1353458340
		public static final String API_SECRET = "af227accdc76fad067bf622a1b13cfbb";// 添加您的SECRETKEY"ba312327c815536394f7eb6fd0b915c4"c0911ca544fa36fa47d5baccee1c58a0c936a4df
	}

	/**
	 * FM发射
	 */
	public static final class FMTransmit {
		public static final int CHANNEL_LOW = 8750;
		public static final int CHANNEL_MIDDLE = 9750;
		public static final int CHANNEL_HIGH = 10750;

		public static final String HINT_LOW = "87.5";
		public static final String HINT_MIDDLE = "97.5";
		public static final String HINT_HIGH = "107.5";
	}

	/**
	 * 路径
	 */
	public final static class Path {
		/**
		 * 行驶轨迹文件存储位置
		 */
		public static final String ROUTE_TRACK = Environment
				.getExternalStorageDirectory().getPath() + "/Route/";

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
		 * 启动测试应用命令:DeviceTest
		 */
		public final static String DEVICE_TEST = "*#86*#";

		/**
		 * 启动工程模式：EngineerMode
		 */
		public final static String ENGINEER_MODE = "*#*#3646633#*#*";

		/**
		 * 启动系统设置:Setting
		 */
		public final static String SETTING = "*#7388464#*";
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
	}

	/**
	 * SharedPreferences名称
	 */
	public static final String SHARED_PREFERENCES_NAME = "CarLauncher";
	
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
		public final static int REFRESH_PROGRESS_EVENT = 0x100;
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

	// 循环录像和加锁相关
	public static final class RESOLUTION {
		public static final int res1080P = 1;
		public static final int res720P = 2;
	}

	public static final class SAVE_TIME {
		public static final int time_3min = 1;
		public static final int time_5min = 2;
	}

	public static final class SENSITIVITY {
		public static final int sen_high = 1;
		public static final int sen_mid = 2;
		public static final int sen_low = 3;
	}

	public static final class EROOTPATH {
		public static final String ROOTPATH = "/mnt/sdcard" + File.separator
				+ "tachograph" + File.separator;
		public static final String TEMPVIDEOPATH = ROOTPATH;// + "TempVieo" +
															// File.separator;
		public static final String URGENVIDEOPATH = ROOTPATH + "UrgentVieo"
				+ File.separator;
		public static final String SAVEPATH = ROOTPATH + "SaveVieo"
				+ File.separator;
		public static final String PHOTOPATH = ROOTPATH + "Photo"
				+ File.separator;
	}

}
