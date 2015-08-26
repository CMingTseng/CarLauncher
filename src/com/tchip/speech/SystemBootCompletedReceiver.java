package com.tchip.speech;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


/**
 * 当系统启动后系统speech服务
 * 
 * @author wu
 *
 */
public class SystemBootCompletedReceiver extends BroadcastReceiver {
	private String TAG = "SystemBootCompletedReceiver";
	
    @Override
    public void onReceive(Context context, Intent intent) {
		Log.d("wwj_test", "action : " + intent.getAction());
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
        	startMyService(context);
        }
    }
    
	
	/**
	 *  启动思必驰语音服务
	 * @param context
	 */
	private void startMyService(Context context){
		Intent intent = new Intent(context, SpeechService.class);
		context.startService(intent);
	}
}
