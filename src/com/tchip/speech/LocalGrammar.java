/*******************************************************************************
 * Copyright 2014 AISpeech
 ******************************************************************************/
package com.tchip.speech;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.IMergeRule;
import com.aispeech.common.Util;
import com.aispeech.export.engines.AILocalGrammarEngine;
import com.aispeech.export.engines.AIMixASREngine;
import com.aispeech.export.listeners.AIASRListener;
import com.aispeech.export.listeners.AILocalGrammarListener;
import com.tchip.aispeech.util.GrammarHelper;
import com.tchip.aispeech.util.NetworkUtil;

/**
 * 本示例将演示通过联合使用本地识别引擎和本地语法编译引擎实现定制识别。<br>
 * 将由本地语法编译引擎根据手机中的联系人和应用列表编译出可供本地识别引擎使用的资源，从而达到离线定制识别的功能。
 * 
 */
public class LocalGrammar{
    public static final String TAG = LocalGrammar.class.getName();
    Context context;
    Toast mToast;
    AILocalGrammarEngine mGrammarEngine;
    AIMixASREngine mAsrEngine;
    String recResult;
    
    public LocalGrammar(Context context) {
        this.context = context;

        initGrammarEngine();

        // 检测是否已生成并存在识别资源，若已存在，则立即初始化本地识别引擎，否则等待编译生成资源文件后加载本地识别引擎
        if (new File(Util.getResourceDir(context) + File.separator + AILocalGrammarEngine.OUTPUT_NAME).exists()) {
            initAsrEngine();
        }

        mToast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        
        startResGen();
    }

    /**
     * 初始化资源编译引擎
     */
    private void initGrammarEngine() {
        if (mGrammarEngine != null) {
            mGrammarEngine.destroy();
        }
        Log.i(TAG, "grammar create");
        mGrammarEngine = AILocalGrammarEngine.createInstance();
        mGrammarEngine.setResFileName("ebnfc.aicar.0.0.2.bin");
        mGrammarEngine.init(context, new AILocalGrammarListenerImpl(), AppKey.APPKEY, AppKey.SECRETKEY);
        mGrammarEngine.setDeviceId("aA-sS_dD");
    }

    /**
     * 初始化本地合成引擎
     */
    @SuppressLint("NewApi")
    private void initAsrEngine() {
        if (mAsrEngine != null) {
            mAsrEngine.destroy();
        }
        Log.i(TAG, "asr create");
        mAsrEngine = AIMixASREngine.createInstance();
        mAsrEngine.setResBin("ebnfr.aicar.0.0.2.bin");
        mAsrEngine.setNetBin(AILocalGrammarEngine.OUTPUT_NAME,true);

        mAsrEngine.setServer("ws://s.api.aispeech.com");
        mAsrEngine.setRes("chezai");
        mAsrEngine.setWaitCloudTimeout(2000);
        mAsrEngine.setVadResource("vad.aicar.0.0.3.bin");
        if(context.getExternalCacheDir() != null){
            mAsrEngine.setTmpDir(context.getExternalCacheDir().getAbsolutePath());
            mAsrEngine.setUploadEnable(true);
            mAsrEngine.setUploadInterval(1000);
        }
        
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
        
        mAsrEngine.init(context, new AIASRListenerImpl(), AppKey.APPKEY, AppKey.SECRETKEY);
        mAsrEngine.setUseCloud(true);
        mAsrEngine.setUseXbnfRec(true);
        mAsrEngine.setNBest(1);
        mAsrEngine.setAthThreshold(0.4f);
        mAsrEngine.setRthThreshold(0.1f);
        mAsrEngine.setIsRelyOnLocalConf(true);
        mAsrEngine.setUseConf(true);
        mAsrEngine.setNoSpeechTimeOut(0);
        mAsrEngine.setDeviceId("aA-sS_dD");
    }

    /**
     * 开始生成识别资源
     */
    
    private void startResGen() {
        // 生成ebnf语法
        GrammarHelper gh = new GrammarHelper(context);
        String contactString = gh.getConatcts();
        String appString = gh.getApps();
        String musicString = gh.getMusicTitles();
        // 如果手机通讯录没有联系人
        if (TextUtils.isEmpty(contactString)) {
            contactString = "无联系人";
        }
        String ebnf = gh.importAssets(contactString,musicString, appString, "grammar.xbnf");
        Log.i(TAG, ebnf);
        // 设置ebnf语法
        mGrammarEngine.setEbnf(ebnf);
        // 启动语法编译引擎，更新资源
        mGrammarEngine.update();
    }

    /**
     * 语法编译引擎回调接口，用以接收相关事件
     */
    public class AILocalGrammarListenerImpl implements AILocalGrammarListener {

        @Override
        public void onError(AIError error) {
            //showInfo("资源生成发生错误");
            showTip(error.getError());
            //setResBtnEnable(true);
        }

        @Override
        public void onUpdateCompleted(String recordId, String path) {
            //showInfo("资源生成/更新成功\npath=" + path + "\n重新加载识别引擎...");
            Log.i(TAG, "资源生成/更新成功\npath=" + path + "\n重新加载识别引擎...");
            File file = new File(path);
            byte[] buffer = new byte[10240];
            int i = 0;
            try {
                FileInputStream fis = new FileInputStream(file);
                FileOutputStream fos = new FileOutputStream(new File("/sdcard/gram.net.bin"));
                while((i = fis.read(buffer)) > 0){
                    fos.write(buffer);
                }
                fis.close();
                fos.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            initAsrEngine();
        }

        @Override
        public void onInit(int status) {
            if (status == 0) {
                //showInfo("资源定制引擎加载成功");
                if (mAsrEngine == null) {
                    //setResBtnEnable(true);
                }
            } else {
                //showInfo("资源定制引擎加载失败");
            }
        }
    }

    long ts;

    /**
     * 本地识别引擎回调接口，用以接收相关事件
     */
    public class AIASRListenerImpl implements AIASRListener {

        @Override
        public void onBeginningOfSpeech() {
            //showInfo("检测到说话");

        }

        @Override
        public void onEndOfSpeech() {
            //showInfo("检测到语音停止，开始识别...");
            ts = System.currentTimeMillis();
        }

        @Override
        public void onReadyForSpeech() {
            //showInfo("请说话...");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            showTip("RmsDB = " + rmsdB);
        }

        @Override
        public void onError(AIError error) {
            //showInfo("识别发生错误");
            showTip(error.getErrId() + "");
            //setAsrBtnState(true, "识别");
        }

        @Override
        public void onResults(AIResult results) {
            ts = System.currentTimeMillis() - ts;
            Log.i(TAG, results.getResultObject().toString());
            //try {
                //showInfo("ts"+ ts+"\n" +((JSONObject)results.getResultObject()).toString(4) );
            //} catch (JSONException e) {
            //    e.printStackTrace();
            //}
            //setAsrBtnState(true, "识别");
        }

        @Override
        public void onInit(int status) {
            if (status == 0) {
                Log.i(TAG, "end of init asr engine");
                //showInfo("本地识别引擎加载成功");
                //setAsrBtnState(true, "识别");
                if(NetworkUtil.isWifiConnected(context)){
                    if(mAsrEngine != null){
                        mAsrEngine.setNetWorkState("WIFI");
                    }
                }
            } else {
                //showInfo("本地识别引擎加载失败");
            }
        }

        @Override
        public void onRecorderReleased() {
            //showInfo("检测到录音机停止");
        }
    }

    private void showTip(String str) {
    	mToast.setText(str);
    	mToast.show();
    }

    public void pause() {
        if (mGrammarEngine != null) {
            Log.i(TAG, "grammar cancel");
            mGrammarEngine.cancel();
        }
        if (mAsrEngine != null) {
            Log.i(TAG, "asr cancel");
            mAsrEngine.cancel();
        }
    }

    public void destroy() {
        if (mGrammarEngine != null) {
            Log.i(TAG, "grammar destroy");
            mGrammarEngine.destroy();
            mGrammarEngine = null;
        }
        if (mAsrEngine != null) {
            Log.i(TAG, "asr destroy");
            mAsrEngine.destroy();
            mAsrEngine = null;
        }
    }
}