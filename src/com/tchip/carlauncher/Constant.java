package com.tchip.carlauncher;

import java.io.File;

import android.os.Environment;

public interface Constant {
	// FACE++ SDK
	public static final String FACE_API_KEY = "543e743fa43f0550c2977995f3ff2222";
	public static final String FACE_API_SECRET = "IEpqaPm-wa-eznyZfKhvwW8rEGgzLxRk";

	// 讯飞语音SDK
	public static final String XUNFEI_APP_ID = "5531bef5";

	// 百度地图SDK
	public static final String BAIDU_API_KEY = "Tycks4ezX3hSXfRtu7TvCVTl";
	public static final String BAIDU_MCODE = "6B:40:B2:47:13:F5:6A:F7:40:6A:89:84:46:53:33:47:AD:DC:C1:0C;com.tchip.carlauncher";

	/**
	 * 日志Tag
	 */
	public static final String TAG = "ZMS";
	
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
	 * 行驶轨迹文件存储位置
	 */
	public static final String ROUTE_TRACK_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/Route/";

	/**
	 * 字体目录
	 */
	public static final String FONT_PATH = "fonts/";

	/**
	 * 音乐背景图片目录
	 */
	public static final String MUSIC_IMAGE_PATH = "MusicBackground";

	/**
	 * SharedPreferences名称
	 */
	public static final String SHARED_PREFERENCES_NAME = "CarLauncher";

	/**
	 * 循环录像保留空间(单位：字节B)
	 */
	public static final long SD_MIN_FREE_STORAGE = 500 * 1024 * 1024; // 500M

	/**
	 * 循环录像最大占用空间百分比
	 */
	public static final float SD_MIN_FREE_PERCENT = 0.1f; // 10%

	// ========== Music START ==========
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
	// ========== Music END ==========

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
