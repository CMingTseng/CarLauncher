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
	 * syn_chnsnt_anonyf
	 * syn_chnsnt_anonyg
	 * syn_chnsnt_anonym
	 * syn_chnsnt_xyshenf
	 */
	public static String ttsSound = "syn_chnsnt_zhilingf";
	
	//思必驰语音界面是否在前台显示
	public static boolean speechUIShowing = false;
	//语音服务器和界面传递消息的广播
	public static String machineMessage = "com.tchip.speechMachineMessage";
	public static String userMesage = "com.tchip.speechUserMessage";
	public static String uiMesage = "com.tchip.speechUIMessage";
	
	//本地语音关键词
    public static String app = "app";
    public static String baiduMap = "百度地图";
    public static String music = "music";
    public static String musicAction[] ={"random", "resume", "play"}; 
    public static String phone = "phone";
    public static String number = "number";
    public static String person = "person";
    public static String t_chip = "t-chip";
    public static String screenOff = "关闭屏幕";
    public static String screenOffing = "正在关闭屏幕";
    public static String goCarLauncher = "返回桌面回到桌面打开桌面";
    public static String goCarLaunchering = "以返回桌面";
    

	//云端语音关键词
    public static String calendar = "日历";
    public static String time = "时间";
    public static String map = "地图";
    public static String hello = "你好";
}