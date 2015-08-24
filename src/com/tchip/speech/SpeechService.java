package com.tchip.speech;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.json.JSONException;
import org.json.JSONObject;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.IMergeRule;
import com.aispeech.common.AIConstant;
import com.aispeech.export.engines.AILocalWakeupEngine;
import com.aispeech.export.engines.AIMixASREngine;
import com.aispeech.export.listeners.AIASRListener;
import com.aispeech.export.listeners.AILocalWakeupListener;
import com.tchip.aispeech.util.BeepPlayer;
import com.tchip.aispeech.util.SpeechConfig;
import com.tchip.carlauncher.R;
import com.tchip.speech.SpeechCloudTTS.OnSpeechCloudTTSCompleteListener;
import com.tchip.speech.json.AnalysisCloudMessage;
import com.tchip.speech.json.AnalysisNativeMessage;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;


/**
 * 
 * 语音后台服务，用于监听呼叫，启动语音助手界面
 * 语音唤醒词“小智”
 * @author wwj
 *
 */
public class SpeechService extends Service {
	private static String Tag = "SpeechService";
    AILocalWakeupEngine mWakeupEngine;
    AIMixASREngine mAsrEngine;
    //TextView resultText;
    Button btnStart;
    Button btnStop;
    BeepPlayer startBeep;
    private CircularFifoQueue<byte[]> mFifoQueue = null;
    String recResult;
    Toast mToast;

    SpeechCloudTTS sctts;
    
    LinearLayout chartMsgPanel;
    ScrollView chartMsgScroll;
	private LayoutInflater inflater;
	
	//声明键盘管理器
	KeyguardManager km = null; 
	//声明键盘锁
	private KeyguardLock kl = null; 
	//声明电源管理器
	private PowerManager pm;
	private PowerManager.WakeLock wl;
	@Override
	public void onCreate(){
		super.onCreate();

        
        new LocalGrammar(this);
        
        sctts = new SpeechCloudTTS(this);
        sctts.setOnSpeechCloudTTSCompleteListener(new OnSpeechCloudTTSCompleteListener() {
			
			@Override
			public void complete() {
				// TODO Auto-generated method stub
		        mWakeupEngine.start();
		        mWakeupEngine.setStopOnWakeupSuccess(false);
			}
		});
        
        initSpeech();
        
        //初始化解析类
        acm = new AnalysisCloudMessage(getApplicationContext());
        anm = new AnalysisNativeMessage(getApplicationContext());
        
        km= (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);  
        kl = km.newKeyguardLock("");   
        pm=(PowerManager) getSystemService(Context.POWER_SERVICE);  
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright"); 
	}
	
	//初始化speech本地和云端语音
	private void initSpeech(){
        mWakeupEngine = AILocalWakeupEngine.createInstance();

        mWakeupEngine.setNetBin("net.bin.imy");
        mWakeupEngine.setResBin("res.bin.imy");
        mWakeupEngine.setWakeupRetMode(AIConstant.WAKEUP_RET_MODE_1);
        
        mWakeupEngine.init(this, new AISpeechListenerImpl(), AppKey.APPKEY, AppKey.SECRETKEY);
        mWakeupEngine.setDeviceId("aA-sS_dD");
        mAsrEngine = AIMixASREngine.createInstance();
        mAsrEngine.setDBable("i am a partner of aispeech");
        mAsrEngine.setNetBin("local.net.bin");
        mAsrEngine.setResBin("ebnfr.aicar.0.0.2.bin");
        mAsrEngine.setVadResource("vad.aicar.0.0.3.bin");
        mAsrEngine.setUseCloud(true);
        mAsrEngine.setServer("ws://s.api.aispeech.com");
        mAsrEngine.setRes("chezai");
        mAsrEngine.setWaitCloudTimeout(2000);
        mAsrEngine.setUseConf(true);
        mAsrEngine.setUseXbnfRec(true);
        // 设置本地置信度阈值
        mAsrEngine.setAthThreshold(0.4f);
        mAsrEngine.setIsRelyOnLocalConf(true);
    	// 自行设置合并规则:
 		// 1. 如果无云端结果,则直接返回本地结果
 		// 2. 如果有云端结果,当本地结果置信度大于阈值时,返回本地结果,否则返回云端结果
 		mAsrEngine.setMergeRule(new IMergeRule() {
 			
             @Override
             public AIResult mergeResult(AIResult localResult, AIResult cloudResult) {
             
                 AIResult result = null;
                 recResult = "";
                 try {
                	// Log.i(TAG, "local: " + localResult + " cloud: " + cloudResult);
                     if (cloudResult == null) {
                         // 为结果增加标记,以标示来源于云端还是本地
                         JSONObject localJsonObject = new JSONObject(localResult.getResultObject()
                                 .toString());
                         JSONObject locRes = localJsonObject.getJSONObject("result");
                         if(locRes != null){
                        	 recResult = locRes.getString("rec");                             
                         }                             
                         localJsonObject.put("src", "native");

                         localResult.setResultObject(localJsonObject);
                         result = localResult;
                     } else {
                    	 int selLocFlag = 0;
                    	 if(localResult != null){
                             JSONObject localJsonObject = new JSONObject(localResult.getResultObject()
                                     .toString());
                             JSONObject locRes = localJsonObject.getJSONObject("result");
                             if(locRes != null){
                            	 recResult = locRes.getString("rec");
                            	 Double confVal = Double.valueOf(locRes.getString("conf"));
                            	// Log.i(TAG, "Conf: " + confVal);
                            	 if(confVal > 0.4){
                            		 selLocFlag = 1;
                            	 }
                             }
                             localJsonObject.put("src", "native");

                             localResult.setResultObject(localJsonObject);
                             result = localResult;                                 
                    	 }                        
                    	 if(selLocFlag != 1){
                             JSONObject cloudJsonObject = new JSONObject(cloudResult.getResultObject()
                                     .toString());
                             JSONObject cloudRes = cloudJsonObject.getJSONObject("result");
                             if(cloudRes != null){
                            	 recResult = cloudRes.getString("input");
                             }
                            	 
                             cloudJsonObject.put("src", "cloud");
                             cloudResult.setResultObject(cloudJsonObject);
                             result = cloudResult;
                    	 }
                     }
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
                 return result;
            	 
             }
         });
        
        mAsrEngine.init(this, new AIASRListenerImpl(), AppKey.APPKEY, AppKey.SECRETKEY);
        mAsrEngine.setDeviceId("aA-sS_dD");
        startBeep = new BeepPlayer(this, R.raw.open);
        startBeep.setOnCompletionTask(startBeep.new OnCompletionTask(mHandler, MSG_START_ASR));

        mFifoQueue = new CircularFifoQueue<byte[]>(600);
        
        

        mWakeupEngine.start();
        mWakeupEngine.setStopOnWakeupSuccess(false);
	}
	

    private static final int MSG_START_ASR = 1;
    Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
            case MSG_START_ASR:
                mAsrEngine.start();
                break;
            default:
                break;
            }
        }
    };
	
	@Override
	public void onStart(Intent intent, int startId){
		super.onStart(intent, startId);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public boolean onUnbind(Intent arg0){
		return super.onUnbind(arg0);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(mWakeupEngine != null)
			mWakeupEngine.destroy();
		if(mAsrEngine != null)
			mAsrEngine.destroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
    private class AISpeechListenerImpl implements AILocalWakeupListener {

        @Override
        public void onBeginningOfSpeech() {
        	//sendMachineMessage("检测到开始说话");
            Log.i(Tag, "on beginning of speech");
            sendUIMessage("speech_start");
        }

        @Override
        public void onError(AIError error) {
            //showTip(error.toString());
            initSpeech();
            sctts.cloudTTS("引擎错误，以重新初始化");
            sendUIMessage("speech_end");
            Log.d("wwj_test", "error aispeech : " + error.getError());
        }

        @Override
        public void onEndOfSpeech() {
        	//sendMachineMessage("检测到语音停止");
            Log.i(Tag, "onEnd of Speech");
            sendUIMessage("speech_end");
        }

        @Override
        public void onInit(int status) {
            Log.i(Tag, "Init result " + status);
            if (status == AIConstant.OPT_SUCCESS) {
                //resultText.append("初始化成功!\n");
            	//sendMachineMessage("初始化成功");
                //btnStart.setEnabled(true);
                //btnStop.setEnabled(true);
            } else {
                //resultText.setText("初始化失败!code:" + status);
            	sendMachineMessage("初始化失败");
            }
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            // TODO Auto-generated method stub
        	sendMachineMessage("" + rmsdB);
        }

        @Override
        public void onWakeup(String recordId, int wakeupValue, String wakeupWord, boolean isLast) {
            sendUIMessage("speech_end");
            if (wakeupValue >= 4) {
                //resultText.append("唤醒成功 wakeupValue = " + wakeupValue + "  wakeupWord = "
                //        + wakeupWord + "\n");
            	if(!SpeechConfig.speechUIShowing){
            		if(!pm.isScreenOn()){ 
            	        //解锁  
            	        kl.disableKeyguard();  
            		}
            		SpeechConfig.speechUIShowing = true;
            		Intent intent = new Intent(SpeechService.this, WakeUpCloudAsr.class);
            		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            		startActivity(intent);
            	}

    	        //点亮屏幕  
    	        wl.acquire();  
    	        //释放  
    	        wl.release(); 
            	sendMachineMessage("唤醒成功");
            } else {
                //resultText.append("唤醒失败\n");
        		//machineQuery("唤醒失败");
            	sendMachineMessage("唤醒失败");
            }
            startBeep.playBeep();
            mWakeupEngine.stop();
            Log.i("adfafasfafasfdaadf", String.valueOf(wakeupValue));
            if(wakeupValue != 6){
                mAsrEngine.start();
            }
            //writeAudioToFile(wakeupWord, recordId);
            //resultText.append("\n小智");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            mFifoQueue.add(buffer);
        }

        @Override
        public void onReadyForSpeech() {
            //resultText.setText("说小智以唤醒\n");
        	sendMachineMessage("说小智以唤醒");
            mFifoQueue.clear();
        }

        @Override
        public void onRecorderReleased() {
            // TODO Auto-generated method stub
            sendUIMessage("speech_end");
        }

    }

    private class AIASRListenerImpl implements AIASRListener {

        @Override
        public void onBeginningOfSpeech() {
            sendUIMessage("speech_start");
        }

        @Override
        public void onEndOfSpeech() {
            // TODO Auto-generated method stub
            sendUIMessage("speech_end_user");
        }

        @Override
        public void onError(AIError error) {
            //showTip(error.toString());
            mAsrEngine.start();
            //initSpeech();
            //sctts.cloudTTS("引擎错误，以重新初始化");
            //Log.d("wwj_test", "error aiasr : " + error.getError());
        }

        @Override
        public void onInit(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onReadyForSpeech() {
            //resultText.setText("可以开始说话了\n");
        	//sendMachineMessage("可以开始说话了");
            sendUIMessage("speech_end");
        }

        @Override
        public void onResults(AIResult result) {
            Log.d(Tag, "onReuslt:" + result.getResultObject().toString());
            JSONObject object;
            try {
                object = new JSONObject(result.getResultObject().toString());
                //resultText.append(object.toString(4));
                sendUserMessage(object.toString(4));
                String data = analysisSpeechMessage(object.toString(4));
                if(data == null){
                    sendMachineMessage("欢迎使用小智");
                	sctts.cloudTTS("欢迎使用小智");
                } else {
                	if(data.contains(SpeechConfig.screenOff)){
                		kl.reenableKeyguard();
                		wl.acquire(1000);
	                	sctts.cloudTTS(SpeechConfig.screenOffing);
	                    sendMachineMessage(SpeechConfig.screenOffing);
                	}else{
	                    sendMachineMessage(data);
	                	sctts.cloudTTS(data);
                	}
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            sendUIMessage("speech_end");
            //mWakeupEngine.start();
            //mWakeupEngine.setStopOnWakeupSuccess(false);
        }

        @Override
        public void onRmsChanged(float arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onRecorderReleased() {
            // TODO Auto-generated method stub

        }

    }

    private void showTip(final String str) {
                mToast.setText(str);
                mToast.show();
    }
    
    /*
     * service 发送机器消息
     */
    private void sendMachineMessage(String msg){
    	Intent intent = new Intent(SpeechConfig.machineMessage);
    	intent.putExtra("value", msg);
    	sendBroadcast(intent);
    }
    /*
     * service 发送用户消息
     */
    private void sendUserMessage(String msg){
    	Intent intent = new Intent(SpeechConfig.userMesage);
    	intent.putExtra("value", msg);
    	sendBroadcast(intent);
    }
    /*
     * service 发送界面消息
     */
    private void sendUIMessage(String msg){
    	Intent intent = new Intent(SpeechConfig.uiMesage);
    	intent.putExtra("value", msg);
    	sendBroadcast(intent);
    }
    
    
    AnalysisCloudMessage acm;
    AnalysisNativeMessage anm;
    /*
     * 返回语音数据处理
     * @param msg
     */
    private String analysisSpeechMessage(String msg){
    	if (msg == null)
    		return null;
    	
    	if (msg.contains("cloud")) {
    		return acm.analysis(msg);
    	} else if (msg.contains("native")) {
    		return anm.analysis(msg);
    	} else {
    		return null;
    	}
    }
}

