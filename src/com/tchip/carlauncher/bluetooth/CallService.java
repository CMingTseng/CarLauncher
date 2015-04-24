package com.tchip.carlauncher.bluetooth;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class CallService extends Service{  
    
    public static final String TAG = "CallService";  
  
    @Override  
    public void onCreate() {  
        super.onCreate();  
        //Toast.makeText(CallService.this,"calling...",Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate() executed");  
    }  
  
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        Log.d(TAG, "onStartCommand() executed");
        Toast.makeText(CallService.this,"calling...",Toast.LENGTH_LONG).show();  
        new Thread(new Runnable() {  
            @Override  
            public void run() {
            	
             Intent callintent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:13242215828"));
             startActivity(callintent);    
            }  
        }).start(); 
       
        return super.onStartCommand(intent, flags, startId);  
    }  
      
    @Override  
    public void onDestroy() {  
        super.onDestroy();  
        Log.d(TAG, "onDestroy() executed");  
    }  
  
    @Override  
    public IBinder onBind(Intent intent) {  
    	//Toast.makeText(CallService.this,"calling...",Toast.LENGTH_LONG).show();
        return null;  
    }  
  
}  