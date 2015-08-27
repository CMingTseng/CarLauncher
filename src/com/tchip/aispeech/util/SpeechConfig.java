package com.tchip.aispeech.util;


/**
 * 
 * 配置语音识别参数
 * @author wwj
 *
 */
public class SpeechConfig {
	
	/*
	 * 设置tts声音  
	 * syn_chnsnt_zhilingf
	 * syn_chnsnt_anonyf 女播音员
	 * syn_chnsnt_anonyg 小女生
	 * syn_chnsnt_anonym 男播音员
	 * syn_chnsnt_xyshenf 女客服  声音有点小
	 */
	public static String ttsSound = "syn_chnsnt_anonyg";
	
	//思必驰语音界面是否在前台显示
	public static boolean speechUIShowing = false;
	//语音服务器和界面传递消息的广播
	public static String machineMessage = "com.tchip.speechMachineMessage";
	public static String userMesage = "com.tchip.speechUserMessage";
	public static String uiMesage = "com.tchip.speechUIMessage";
	
	public static String weatherMesage = "com.tchip.speechWeatherMessage";
	
	//本地语音关键词
    public static String app = "app";
    public static String baiduMap = "百度地图";
    public static String kuwoMusic = "酷我音乐盒";
    public static String kugouMusic = "酷狗音乐";
    public static String music = "music";
    public static String musicAction[] ={"random", "resume", "play"}; 
    public static String phone = "phone";
    public static String number = "number";
    public static String person = "person";
    public static String t_chip = "t-chip";
    public static String screenOff = "关闭屏幕";
    public static String screenOffing = "屏幕已关闭";
    public static String goCarLauncher = "返回桌面回到桌面打开桌面";
    public static String goCarLaunchering = "以返回桌面";
    public static String goBack = "返回";

    public static String volume = "volume";
    public static String upVolume = "增大音量";
    public static String downVolume = "减小音量";

	//云端语音关键词
    public static String calendar = "日历";
    public static String time = "时间";
    public static String map = "地图";
    public static String weather = "天气";
    
    //云端日常用语
    public static String hello = "你好";
    public static String helloAck = "你好，很高兴为你服务。";
    public static String me = "我";
    public static String userDo = "说什么"; //我可以说什么
    public static String userDoAck = "比如您可以说：\n 导航到深圳北站; \n 打开百度地图; \n 北京天气怎么样; \n 关闭屏幕、返回桌面、增大减小音量; \n 当然您也可以唤醒在后台的我哦。";
}