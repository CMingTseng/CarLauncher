package com.tchip.speech.json;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.tchip.aispeech.util.SpeechConfig;
import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.ui.activity.MainActivity;

/**
 * 
 * 处理服务器云端返回数据
 * 
 * @author wwj
 *
 */
public class AnalysisNativeMessage{
    private Context context;
    public AnalysisNativeMessage(Context context) {
		// TODO Auto-generated constructor stub
    	this.context = context;
	}

	/**
     * 解析数据
     * @param msg
     */
    public String analysis(String msg) {    	
    	SpeechInfo scInfo=JSON.parseObject(msg, SpeechInfo.class);
		String result = scInfo.getResult();
		Log.d("wwj_test", "result : " + result);
		if(!(result == null)){
			NativeResultInfo nrInfo = JSON.parseObject(result, NativeResultInfo.class);
			String post = nrInfo.getPost();
			String rec = nrInfo.getRec();
			Log.d("wwj_test", "post : " + post);
			Log.d("wwj_test", "rec : " + rec);
			sendUserMessage(rec);
			if(!(post == null)){
				NativePostInfo npInfo = JSON.parseObject(post, NativePostInfo.class);
				String sem = npInfo.getSem();
				if(!(sem == null)){
					String domain = null;
					String action = null;
					if(sem.contains(SpeechConfig.phone)){
						//电话
						if(sem.contains(SpeechConfig.number)){
							NativeSemNumberInfo nsInfo = JSON.parseObject(sem, NativeSemNumberInfo.class);
							domain = nsInfo.getDomain();
							action = nsInfo.getAction();
							String number = nsInfo.getNumber();
							return actionDo(domain, action, number, false);
						}else if(sem.contains(SpeechConfig.person)){
							NativeSemPersonInfo nsInfo = JSON.parseObject(sem, NativeSemPersonInfo.class);
							domain = nsInfo.getDomain();
							action = nsInfo.getAction();
							String person = nsInfo.getPerson();
							return actionDo(domain, action, person, true);
						}
					}else if(sem.contains(SpeechConfig.music)){
						//打开app，播放音乐
						NativeSemInfo nsInfo = JSON.parseObject(sem, NativeSemInfo.class);
						domain = nsInfo.getDomain();
						action = nsInfo.getAction();
						String appname = nsInfo.getAppname();
	
						Log.d("wwj_test", "domain : " + domain);
						Log.d("wwj_test", "action : " + action);
						Log.d("wwj_test", "appname : " + appname);
						return actionDo(domain, action, appname, false);
					}else if(sem.contains(SpeechConfig.t_chip)){
						//屏幕亮度，锁屏
						NativeSemInfo nsInfo = JSON.parseObject(sem, NativeSemInfo.class);
						domain = nsInfo.getDomain();
						action = nsInfo.getAction();
						//String appname = nsInfo.getAppname();
						if(rec != null)
							rec = rec.replace(" ", ""); //去掉空格
						
						Log.d("wwj_test", "domain : " + domain);
						Log.d("wwj_test", "action : " + action);
						Log.d("wwj_test", "rec : " + rec);
						return actionDo(domain, action, rec, false);
					}else if(sem.contains("open") && sem.contains("app")){
						//打开app
						NativeSemInfo nsInfo = JSON.parseObject(sem, NativeSemInfo.class);
						domain = nsInfo.getDomain();
						action = nsInfo.getAction();
						String appname = nsInfo.getAppname();
	
						Log.d("wwj_test", "domain : " + domain);
						Log.d("wwj_test", "action : " + action);
						Log.d("wwj_test", "appname : " + appname);
						return actionDo(domain, action, appname, false);
					}else if(sem.contains(SpeechConfig.volume)){
						//调整音量
						NativeSemInfo nsInfo = JSON.parseObject(sem, NativeSemInfo.class);
						domain = nsInfo.getDomain();
						action = nsInfo.getAction();
						
						Log.d("wwj_test", "domain : " + domain);
						Log.d("wwj_test", "action : " + action);
						return actionDo(domain, action, null, false);
					}
				}
			}
		}
		return null;
    }
    
    /**
     * 给界面发送广播数据
     * @param msg
     */
    private void sendUserMessage(String msg){
    	Intent intent = new Intent(SpeechConfig.userMesage);
    	intent.putExtra("value", msg);
    	context.sendBroadcast(intent);
    }

    
    /**
     * 
     * 打开app
     * @param domain
     * @param action
     * @param detail
     * @param person
     * @return
     */
    private String actionDo(String domain, String action, String detail, boolean person){
    	//先去掉空格
    	domain = domain.replace(" ", "");
    	if(SpeechConfig.app.equals(domain)){
    		//打开app
	    	if(SpeechConfig.baiduMap.equals(detail)){
	    		try{
					Intent intent = new Intent();
		    		//ComponentName comp = new ComponentName("com.baidu.BaiduMap", "com.baidu.baidumaps.WelcomeScreen");
		    		ComponentName comp = new ComponentName("com.baidu.navi.hd", "com.baidu.navi.NaviActivity");		    		
		    		intent.setComponent(comp);
		    		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    		context.startActivity(intent);
		    		return "正在打开百度地图";
	    		}catch(Exception e){
		    		return "没有找到百度地图";
				}
	    	}else if(SpeechConfig.kuwoMusic.contains(detail) || SpeechConfig.kugouMusic.contains(detail)){
    			//打开酷我音乐盒或者酷狗音乐
	    		return openMusic();
	    	}
    	}else if(SpeechConfig.music.equals(domain)){
    		//音乐播放
    		if(SpeechConfig.musicAction[0].equals(action) || SpeechConfig.musicAction[1].equals(action) || SpeechConfig.musicAction[2].equals(action)){
				Log.d("wwj_test", "打开酷我音乐盒");
    			//打开酷我音乐盒
				return openMusic();
    		}
    	}else if(SpeechConfig.phone.equals(domain)){
    		//打电话
    		if(detail != null){
    			if(person){
    				detail = findNumberByName(detail);
    			}
    			Intent intent = new Intent(Intent.ACTION_CALL , Uri.parse("tel:" +  detail));  
    			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);  
	    		return "正在拨打" + detail;
    		}
    	}else if(SpeechConfig.t_chip.equals(domain)){
    		if(SpeechConfig.screenOff.equals(detail)){
    			//关闭屏幕
    			return SpeechConfig.screenOff;
    		}else if(SpeechConfig.goCarLauncher.contains(detail)){
    			//返回桌面
				Intent intentLauncher = new Intent(context, MainActivity.class);
				intentLauncher.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intentLauncher);
    			return SpeechConfig.goCarLaunchering;
    		}
    	}else if(SpeechConfig.volume.equals(domain)){
    		//调整音量
    		if(action.equals("up")){
        		context.sendBroadcast(new Intent("com.tchip.powerKey").putExtra("value", "volume_up"));
    		}else if(action.equals("down")){
    			context.sendBroadcast(new Intent("com.tchip.powerKey").putExtra("value", "volume_down"));
    		}
    		return "";
    	}
    	
    	return null;
    }
    
    /*
     * 处理打开酷我音乐还是打开酷狗音乐
     */
    private String openMusic(){
    	ComponentName componentMusic;
		if(Constant.Module.isOnlineMusicKuwo){
			componentMusic = new ComponentName("cn.kuwo.kwmusichd", "cn.kuwo.kwmusichd.WelcomeActivity");
		}else{
			componentMusic = new ComponentName("com.kugou.playerHD2", "com.kugou.playerHD.activity.SplashActivity");
		}
		try{
			//ComponentName componentMusic = new ComponentName("cn.kuwo.player", "cn.kuwo.player.activities.EntryActivity");
			Intent intentMusic = new Intent();
			intentMusic.setComponent(componentMusic);
			intentMusic.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intentMusic);
			sendMusicBroadcast();
			
			if(Constant.Module.isOnlineMusicKuwo)
				return "正在打开酷我音乐盒";
			else
				return "正在打开酷狗音乐";
		}catch(Exception e){					
			if(Constant.Module.isOnlineMusicKuwo)
				return "没有找到酷我音乐盒";
			else
				return "没有找到酷狗音乐";
		}
    }

    
    private static final String[] PHONES_PROJECTION = new String[] {
        Phone.DISPLAY_NAME, Phone.NUMBER};
    
    /**联系人显示名称**/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
 
    /**电话号码**/
    private static final int PHONES_NUMBER_INDEX = 1;
	// 查询指定联系人的电话
	public String findNumberByName(String name) {
		String number = null;
		ContentResolver resolver = context.getContentResolver();
	      
	    // 获取手机联系人  
	    Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);  
	      
	      
	    if (phoneCursor != null) {  
	        while (phoneCursor.moveToNext()) {  

		        String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);  
		        if(name.equals(contactName)){
		        	number = phoneCursor.getString(PHONES_NUMBER_INDEX); 
		        	return number;
		        }
	        }  
	        phoneCursor.close();
	    }
		return number;
	}
	
	/**
	 * 发送音乐播放广播
	 */
	private void sendMusicBroadcast(){
		new Handler().postDelayed(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent intent = new Intent("com.android.music.musicservicecommand");
		        intent.putExtra("command", "play");
		        context.sendBroadcast(intent);
			}
			
		}, 2000);
	}
}