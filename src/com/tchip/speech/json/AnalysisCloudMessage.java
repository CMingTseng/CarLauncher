package com.tchip.speech.json;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.tchip.aispeech.util.SpeechConfig;
import com.tchip.carlauncher.ui.activity.ChatActivity;
import com.tchip.carlauncher.ui.activity.NavigationActivity;

/**
 * 
 * 处理服务器云端返回数据
 * 
 * @author wwj
 *
 */
public class AnalysisCloudMessage{
    private Context context;
    public AnalysisCloudMessage(Context context) {
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
			CloudResultInfo crInfo = JSON.parseObject(result, CloudResultInfo.class);
			String semantics = crInfo.getSemantics();
			String input = crInfo.getInput();
			Log.d("wwj_test", "semantics : " + semantics);
			Log.d("wwj_test", "input : " + input);
			sendUserMessage(input);
			
			//处理一些日常用语
			if(SpeechConfig.hello.equals(input)){
				return SpeechConfig.hello;
			}else{
				if(semantics == null){
					return "我听不懂你说什么！";
				}
			}
			
			if(!(semantics == null)){
				CloudSemanticsInfo csInfo = JSON.parseObject(semantics, CloudSemanticsInfo.class);
				String request = csInfo.getRequest();
				if(!(request == null)){
					CloudRequestInfo cqInfo = JSON.parseObject(request, CloudRequestInfo.class);
					String domain = cqInfo.getDomain();
					String action = cqInfo.getAction();
					String param = cqInfo.getParam();

					Log.d("wwj_test", "domain : " + domain);
					Log.d("wwj_test", "action : " + action);
					Log.d("wwj_test", "param : " + param);
					return actionDo(domain, action, param);
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
     * 返回参数处理
     * @param domain
     * @param action
     * @param param
     */
    private String actionDo(String domain, String action, String param){
    	if(SpeechConfig.calendar.equals(domain)/* && calendar.equals(action)*/){
    		//时间
    		if(param != null && param.contains(SpeechConfig.time)){
    			Date d = new Date();
    			return d.toLocaleString();
    		}
    	}else if(SpeechConfig.map.equals(domain)){
    		//导航
    		if(param != null){
    			// 跳转到自写导航界面，不使用GeoCoder
    			int start = param.indexOf("终点名称") + 7;
	    			param = param.substring(start);
	    			param = param.substring(0, param.indexOf("\""));
					Intent intentNavi = new Intent(context, NavigationActivity.class);
					intentNavi.putExtra("destionation", param);
					intentNavi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intentNavi);
	    			return "正在启动导航";
    		}
    	}
    	return null;
    }
}