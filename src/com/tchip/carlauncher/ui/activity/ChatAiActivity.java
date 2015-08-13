package com.tchip.carlauncher.ui.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.IMergeRule;
import com.aispeech.common.Util;
import com.aispeech.export.engines.AILocalGrammarEngine;
import com.aispeech.export.engines.AIMixASREngine;
import com.aispeech.export.listeners.AIASRListener;
import com.aispeech.export.listeners.AILocalGrammarListener;
import com.aispeech.speech.AIAuthEngine;
import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.service.SpeakService;
import com.tchip.carlauncher.util.AiSpeechGrammarHelper;
import com.tchip.carlauncher.util.NetworkUtil;
import com.tchip.carlauncher.util.ProgressAnimationUtil;
import com.tchip.carlauncher.util.VolumeUtil;
import com.tchip.carlauncher.view.AudioRecordDialog;
import com.tchip.carlauncher.view.CircularProgressDrawable;
import com.tchip.carlauncher.view.ResideMenu;

/**
 * 本示例将演示通过联合使用本地识别引擎和本地语法编译引擎实现定制识别。<br>
 * 将由本地语法编译引擎根据手机中的联系人和应用列表编译出可供本地识别引擎使用的资源，从而达到离线定制识别的功能。
 */
public class ChatAiActivity extends Activity {

	private RelativeLayout layoutBack;
	private LinearLayout layoutHelp; // 帮助
	private Button btnToMainFromVoiceAi, btnHelp;
	private TextView tvAnswer, tvQuestion;

	private boolean isResideMenuClose = true;
	// 左侧帮助侧边栏
	private ResideMenu resideMenu;
	private PackageManager packageManager;

	private AudioRecordDialog audioRecordDialog;

	private ImageView imageAnim; // 动画按钮
	private ImageView imageVoice; // 语音按钮
	private Animator currentAnimation;
	private CircularProgressDrawable drawable;

	Button bt_res;
	Toast mToast;

	AILocalGrammarEngine mGrammarEngine;
	AIMixASREngine mAsrEngine;

	String recResult;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_chat_ai);

		// 检测是否已生成并存在识别资源，若已存在，则立即初始化本地识别引擎，否则等待编译生成资源文件后加载本地识别引擎
		if (new File(Util.getResourceDir(this) + File.separator
				+ AILocalGrammarEngine.OUTPUT_NAME).exists()) {
			initAsrEngine();
		}

		initialLayout();

		// TODO:isAiSpeechFirstRun
		authAiSpeech();

		initGrammarEngine();

		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

		audioRecordDialog = new AudioRecordDialog(ChatAiActivity.this);
	}

	private void initialLayout() {
		tvAnswer = (TextView) findViewById(R.id.tvAnswer);
		tvQuestion = (TextView) findViewById(R.id.tvQuestion);
		tvQuestion.setVisibility(View.GONE);
		imageVoice = (ImageView) findViewById(R.id.imageVoice);
		imageVoice.setOnClickListener(new MyOnClickListener());

		// 按钮动画
		imageAnim = (ImageView) findViewById(R.id.imageAnim);
		imageAnim.setOnClickListener(new MyOnClickListener());
		drawable = new CircularProgressDrawable.Builder()
				.setRingWidth(
						getResources().getDimensionPixelSize(
								R.dimen.drawable_ring_size))
				.setOutlineColor(getResources().getColor(R.color.white))
				.setRingColor(
						getResources().getColor(R.color.ui_chat_voice_orange))
				.setCenterColor(
						getResources().getColor(R.color.ui_chat_voice_orange))
				.create();
		imageAnim.setImageDrawable(drawable);
		imageAnim.setVisibility(View.INVISIBLE); // 隐藏动画

		layoutBack = (RelativeLayout) findViewById(R.id.layoutBack);
		layoutBack.setOnClickListener(new MyOnClickListener());
		btnToMainFromVoiceAi = (Button) findViewById(R.id.btnToMainFromVoiceAi);
		btnToMainFromVoiceAi.setOnClickListener(new MyOnClickListener());

		// 帮助侧边栏
		layoutHelp = (LinearLayout) findViewById(R.id.layoutHelp);
		layoutHelp.setOnClickListener(new MyOnClickListener());

		resideMenu = new ResideMenu(this);
		resideMenu.setBackground(R.color.grey_dark_light);
		resideMenu.attachToActivity(this);
		resideMenu.setMenuListener(menuListener);
		// valid scale factor is between 0.0f and 1.0f. leftmenu'width is
		// 150dip.
		resideMenu.setScaleValue(0.6f);
		// 禁止使用右侧菜单
		resideMenu.setDirectionDisable(ResideMenu.DIRECTION_RIGHT);

		btnHelp = (Button) findViewById(R.id.btnHelp);
		btnHelp.setOnClickListener(new MyOnClickListener());
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.imageAnim:
			case R.id.imageVoice:
				// if ("识别".equals(bt_asr.getText())) {
				if (mAsrEngine == null) {
					// 生成资源
					startResGen();
				}
				mAsrEngine.setSaveAudioPath(Environment
						.getExternalStorageDirectory()
						+ File.separator
						+ "testwav"
						+ File.separator
						+ System.currentTimeMillis() + ".wav");
				mAsrEngine.start();
				// } else if ("停止".equals(bt_asr.getText())) {
				// if (mAsrEngine != null) {
				// mAsrEngine.stopRecording();
				// }
				// }
				break;

			case R.id.layoutBack:
			case R.id.btnToMainFromVoiceAi:
				backToMain();
				break;

			case R.id.btnHelp:
			case R.id.layoutHelp:
				resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
				break;

			default:
				break;
			}
		}
	}

	private void backToMain() {
		finish();
		overridePendingTransition(R.anim.zms_translate_down_out,
				R.anim.zms_translate_down_in);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isResideMenuClose) {
				backToMain();
			} else {
				resideMenu.closeMenu();
			}
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	/**
	 * 侧边栏打开关闭监听
	 */
	private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
		@Override
		public void openMenu() {
			isResideMenuClose = false;
			layoutHelp.setVisibility(View.GONE);
		}

		@Override
		public void closeMenu() {
			isResideMenuClose = true;
			layoutHelp.setVisibility(View.VISIBLE);
		}
	};

	public ResideMenu getResideMenu() {
		return resideMenu;
	}

	private void startSpeak(String content) {
		Intent intent = new Intent(ChatAiActivity.this, SpeakService.class);
		intent.putExtra("content", content);
		startService(intent);
	}

	/**
	 * 授权
	 */
	private void authAiSpeech() {
		AIAuthEngine mEngine = AIAuthEngine
				.getInstance(getApplicationContext());
		mEngine.init(Constant.AiSpeech.API_KEY, Constant.AiSpeech.API_SECRET,
				"aA-sS_dD");
		if (mEngine.isAuthed()) {
			Log.v(Constant.TAG, "AiSpeech:Auth Success");
		} else {
			Log.e(Constant.TAG, "AiSpeech:Auth Fail");
		}
	}

	/**
	 * 初始化资源编译引擎
	 */
	private void initGrammarEngine() {
		if (mGrammarEngine != null) {
			mGrammarEngine.destroy();
		}
		Log.i(Constant.TAG, "AiSpeech:grammar create");
		mGrammarEngine = AILocalGrammarEngine.createInstance();
		mGrammarEngine.setResFileName("ebnfc.aicar.0.0.2.bin");
		mGrammarEngine.init(this, new AILocalGrammarListenerImpl(),
				Constant.AiSpeech.API_KEY, Constant.AiSpeech.API_SECRET);
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
		Log.i(Constant.TAG, "AiSpeech:asr create");
		mAsrEngine = AIMixASREngine.createInstance();

		// 设置资源文件名，该文件由思必驰进行定制
		mAsrEngine.setResBin("ebnfr.aicar.0.0.2.bin");

		// 设置网络解码文件名，该文件由思必驰进行定制
		mAsrEngine.setNetBin(AILocalGrammarEngine.OUTPUT_NAME, true);

		mAsrEngine.setServer("ws://s.api.aispeech.com");

		// 设置识别所使用的资源，设为”assist”表示通用识别
		mAsrEngine.setRes("chezai");
		mAsrEngine.setWaitCloudTimeout(2000);
		mAsrEngine.setVadResource("vad.aicar.0.0.3.bin");
		if (getExternalCacheDir() != null) {
			mAsrEngine.setTmpDir(getExternalCacheDir().getAbsolutePath());
			mAsrEngine.setUploadEnable(true);
			mAsrEngine.setUploadInterval(1000);
		}

		// 自行设置合并规则:
		// 1. 如果无云端结果,则直接返回本地结果
		// 2. 如果有云端结果,当本地结果置信度大于阈值时,返回本地结果,否则返回云端结果
		mAsrEngine.setMergeRule(new IMergeRule() {

			@Override
			public AIResult mergeResult(AIResult localResult,
					AIResult cloudResult) {

				AIResult result = null;
				recResult = "";
				try {
					// Log.i(TAG, "local: " + localResult + " cloud: " +
					// cloudResult);
					if (cloudResult == null) {
						// 为结果增加标记,以标示来源于云端还是本地
						JSONObject localJsonObject = new JSONObject(localResult
								.getResultObject().toString());
						JSONObject locRes = localJsonObject
								.getJSONObject("result");
						if (locRes != null) {
							recResult = locRes.getString("rec");
						}
						localJsonObject.put("src", "native");

						localResult.setResultObject(localJsonObject);
						result = localResult;
					} else {
						int selLocFlag = 0;
						if (localResult != null) {
							JSONObject localJsonObject = new JSONObject(
									localResult.getResultObject().toString());
							JSONObject locRes = localJsonObject
									.getJSONObject("result");
							if (locRes != null) {
								recResult = locRes.getString("rec");
								Double confVal = Double.valueOf(locRes
										.getString("conf"));
								// Log.i(TAG, "Conf: " + confVal);
								if (confVal > 0.4) {
									selLocFlag = 1;
									// 如果连接网络，使用Cloud结果
									if (NetworkUtil
											.isNetworkConnected(getApplicationContext())) {
										selLocFlag = 0;
									}
								}
							}
							localJsonObject.put("src", "native");

							localResult.setResultObject(localJsonObject);
							result = localResult;
						}
						if (selLocFlag != 1) {
							JSONObject cloudJsonObject = new JSONObject(
									cloudResult.getResultObject().toString());
							JSONObject cloudRes = cloudJsonObject
									.getJSONObject("result");
							if (cloudRes != null) {
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

		mAsrEngine.init(this, new AIASRListenerImpl(),
				Constant.AiSpeech.API_KEY, Constant.AiSpeech.API_SECRET);
		mAsrEngine.setUseCloud(true);
		mAsrEngine.setUseXbnfRec(true);

		/*
		 * 设置多候选词个数上限,可设范围 1~10
		 */
		mAsrEngine.setNBest(1);
		mAsrEngine.setAthThreshold(0.4f);
		mAsrEngine.setRthThreshold(0.1f);
		mAsrEngine.setIsRelyOnLocalConf(true);
		if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
			mAsrEngine.setUseConf(true);
		}

		/*
		 * 设置无语音超时时长，单位毫秒，默认值为5000ms(setNoSpeechTimeOut(0))
		 * ；如果达到该设置值时，自动停止录音并放弃请求内核(即调用
		 * com.aispeech.speech.AISpeechEngine#cancel()方法)
		 */
		mAsrEngine.setNoSpeechTimeOut(5000);

		/*
		 * 设置音频最大录音时长，达到该值将取消语音引擎并抛出异常 允许的最大录音时长 单位秒
		 * 
		 * 0 表示无最大录音时长限制 默认大小为60S
		 */
		mAsrEngine.setMaxSpeechTimeS(30);
		mAsrEngine.setDeviceId("aA-sS_dD");
	}

	/**
	 * 开始生成识别资源
	 */

	private void startResGen() {
		// 生成ebnf语法
		AiSpeechGrammarHelper gh = new AiSpeechGrammarHelper(this);
		String contactString = gh.getConatcts();
		String appString = gh.getApps();
		String musicString = gh.getMusicTitles();
		// 如果手机通讯录没有联系人
		if (TextUtils.isEmpty(contactString)) {
			contactString = "无联系人";
		}
		String ebnf = gh.importAssets(contactString, musicString, appString,
				"grammar.xbnf");
		Log.i(Constant.TAG, ebnf);
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
			Log.e(Constant.TAG, "AiSpeech:" + error.getError());
		}

		@Override
		public void onUpdateCompleted(String recordId, String path) {
			Log.i(Constant.TAG, "AiSpeech:资源生成/更新成功\npath=" + path
					+ "\n重新加载识别引擎...");
			File file = new File(path);
			byte[] buffer = new byte[10240];
			int i = 0;
			try {
				FileInputStream fis = new FileInputStream(file);
				FileOutputStream fos = new FileOutputStream(new File(
						"/sdcard/gram.net.bin"));
				while ((i = fis.read(buffer)) > 0) {
					fos.write(buffer);
				}
				fis.close();
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			initAsrEngine();
		}

		@Override
		public void onInit(int status) {
			if (status == 0) {
				showInfo("资源定制引擎加载成功");
				if (mAsrEngine == null) {
				}
			} else {
				showInfo("资源定制引擎加载失败");
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
			showInfo("检测到说话");

		}

		@Override
		public void onEndOfSpeech() {
			showInfo("检测到语音停止，开始识别...");
			ts = System.currentTimeMillis();
		}

		@Override
		public void onRecorderReleased() {
			showInfo("检测到录音机停止");

			// dismiss对话框
			audioRecordDialog.dismissDialog();

			// 开始识别动画：隐藏语音按钮，显示动画按钮
			imageVoice.setVisibility(View.INVISIBLE);
			imageAnim.setVisibility(View.VISIBLE);

			if (currentAnimation != null) {
				currentAnimation.cancel();
			}
			currentAnimation = ProgressAnimationUtil
					.prepareStyle1Animation(drawable);
			currentAnimation.start();
		}

		@Override
		public void onReadyForSpeech() {
			showInfo("请说话...");

			audioRecordDialog.showVoiceDialog();
		}

		@Override
		public void onRmsChanged(float rmsdB) {
			// 音量变化 rmsdB 音量标量 0-100
			audioRecordDialog.updateVolumeLevel((int) (rmsdB * 0.3));
		}

		@Override
		public void onError(AIError error) {
			Log.e(Constant.TAG, "AiSpeech AIError:" + error.getErrId());
			switch (error.getErrId()) {
			case 70904:
				// 没有检测到语音

				break;

			default:
				break;
			}

			// 出现异常，停止动画
			if (currentAnimation != null) {
				currentAnimation.cancel();
			}
			currentAnimation = ProgressAnimationUtil
					.preparePulseAnimation(drawable);
			currentAnimation.start();

			// 显示语音按钮，隐藏动画按钮
			imageVoice.setVisibility(View.VISIBLE);
			imageAnim.setVisibility(View.INVISIBLE);
		}

		@Override
		public void onResults(AIResult results) {
			ts = System.currentTimeMillis() - ts;
			Log.i(Constant.TAG, "AiSpeech Result:"
					+ results.getResultObject().toString());
			try {
				showInfo("ts" + ts + "\n"
						+ ((JSONObject) results.getResultObject()).toString(4));
				// 解析JSON
				Log.v(Constant.TAG,
						((JSONObject) results.getResultObject()).toString());
				JSONObject jsonObject;
				jsonObject = (JSONObject) results.getResultObject();

				JSONObject jsonResult = jsonObject.getJSONObject("result");
				String strType = jsonObject.getString("src"); // Cloud,Native

				if ("cloud".equals(strType)) {// TODO：云端结果
					String strInput = jsonResult.getString("input"); // cloud
					if (strInput != null && strInput.trim().length() > 0) {
						tvQuestion.setVisibility(View.VISIBLE);
						tvQuestion.setText(strInput);
					}
					try {
						JSONObject jsonSemmantics = jsonResult
								.getJSONObject("semantics");
						JSONObject jsonRequest = jsonSemmantics
								.getJSONObject("request");
						String strAction = jsonRequest.getString("action");
						String strDomain = jsonRequest.getString("domain");
						if (strAction != null && strAction.trim().length() > 0) {
							if ("地图".equals(strDomain)) {
								JSONObject jsonParam = jsonRequest
										.getJSONObject("param");
								String strDestination = jsonParam
										.getString("终点名称");
								// TODO:跳转到导航
								if (strDestination != null
										&& strDestination.trim().length() > 0) {
									// 跳转到自写导航界面，不使用GeoCoder
									Intent intentNavi = new Intent(
											ChatAiActivity.this,
											NavigationActivity.class);
									intentNavi.putExtra("destionation",
											strDestination);
									startActivity(intentNavi);

								}
							} else if ("app".equals(strDomain)) {

							} else if ("日历".equals(strDomain)) {
								JSONObject jsonParam = jsonRequest
										.getJSONObject("param");
								String strDate = jsonParam.getString("阳历日期");
								Log.v(Constant.TAG, "阳历日期:" + strDate); // 格式：20150728

								String strAnswer = "日期：" + strDate;
								tvAnswer.setText(strAnswer);
								startSpeak(strAnswer);
							}
						}
					} catch (Exception e) {
					}

				} else if ("native".equals(strType)) { // TODO：本地结果
					String strInput = jsonObject.getJSONObject("result")
							.getString("rec");
					if (strInput != null && strInput.trim().length() > 0) {
						tvQuestion.setVisibility(View.VISIBLE);
						tvQuestion.setText(strInput);
					}

					JSONObject jsonPost = jsonResult.getJSONObject("post");
					JSONObject jsonSem = jsonPost.getJSONObject("sem");
					String strDomain = jsonSem.getString("domain");
					String strAction = jsonSem.getString("action");
					if (strDomain != null && strDomain.trim().length() > 0) {
						if ("phone".equals(strDomain)) {
							if (Constant.hasDialer) {
								// action:call

							} else {
								String strAnswer = "本机不支持通讯功能";
								tvAnswer.setText(strAnswer);
								startSpeak(strAnswer);
							}

						} else if ("app".equals(strDomain)) {
							// action:open
							String strAppName = jsonSem.getString("appname");
							if ("open".equals(strAction) && strAppName != null
									&& strAppName.trim().length() > 0) {

								String packageName = getAppPackageByName(strAppName);
								if (!"com.tchip.carlauncher"
										.equals(packageName)) {
									String strAnswer = "正在启动：" + strAppName;
									tvAnswer.setText(strAnswer);
									startSpeak(strAnswer);
									startAppbyPackage(packageName);
								} else {
									String strAnswer = "未找到应用：" + strAppName;
									tvAnswer.setText(strAnswer);
									startSpeak(strAnswer);
								}
							}
						} else if ("volume".equals(strDomain)) {
							// action: up
							if ("up".equals(strAction)) {
								// 增加音量
								VolumeUtil.plusVolume(getApplicationContext(),
										AudioManager.STREAM_MUSIC, 1);
							} else if ("down".equals(strAction)) {
								// 降低音量
								VolumeUtil.minusVolume(getApplicationContext(),
										AudioManager.STREAM_MUSIC, 1);
							} else if ("max".equals(strAction)) {
								// 最大音量
								VolumeUtil.setMaxVolume(
										getApplicationContext(),
										AudioManager.STREAM_MUSIC);
							} else if ("min".equals(strAction)
									|| "mute_on".equals(strAction)) {
								// 最小音量
								VolumeUtil.setMinVolume(
										getApplicationContext(),
										AudioManager.STREAM_MUSIC);
							} else if ("mute_off".equals(strAction)) {
								// 关闭静音，取消静音
								VolumeUtil.setUnmute(getApplicationContext(),
										AudioManager.STREAM_MUSIC);
							}
						}
					}
				}

				// 识别结束，停止动画
				if (currentAnimation != null) {
					currentAnimation.cancel();
				}
				currentAnimation = ProgressAnimationUtil
						.preparePulseAnimation(drawable);
				currentAnimation.start();

				// 显示语音按钮，隐藏动画按钮
				imageVoice.setVisibility(View.VISIBLE);
				imageAnim.setVisibility(View.INVISIBLE);

			} catch (JSONException e) {
				e.printStackTrace();
				// 识别错误，停止动画
				if (currentAnimation != null) {
					currentAnimation.cancel();
				}
				currentAnimation = ProgressAnimationUtil
						.preparePulseAnimation(drawable);
				currentAnimation.start();

				// 显示语音按钮，隐藏动画按钮
				imageVoice.setVisibility(View.VISIBLE);
				imageAnim.setVisibility(View.INVISIBLE);
			}
		}

		@Override
		public void onInit(int status) {
			if (status == 0) {
				Log.i(Constant.TAG, "AiSpeech:init local asr engine success");
				if (NetworkUtil.isWifiConnected(ChatAiActivity.this)) {
					if (mAsrEngine != null) {
						mAsrEngine.setNetWorkState("WIFI");
					}
				}
			} else {
				Log.e(Constant.TAG, "AiSpeech:init local asr engine fail");
			}
		}

	}

	private void showInfo(final String str) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				tvAnswer.setText(str);
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mGrammarEngine != null) {
			Log.i(Constant.TAG, "AiSpeech:grammar cancel");
			mGrammarEngine.cancel();
		}
		if (mAsrEngine != null) {
			Log.i(Constant.TAG, "AiSpeech:asr cancel");
			mAsrEngine.cancel();
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mGrammarEngine != null) {
			Log.i(Constant.TAG, "AiSpeech:grammar destroy");
			mGrammarEngine.destroy();
			mGrammarEngine = null;
		}
		if (mAsrEngine != null) {
			Log.i(Constant.TAG, "AiSpeech:asr destroy");
			mAsrEngine.destroy();
			mAsrEngine = null;
		}
	}

	private void startAppbyPackage(String packageName) {

		Intent intent = packageManager.getLaunchIntentForPackage(packageName);
		startActivity(intent);
	}

	private String getAppPackageByName(String appName) {
		packageManager = getApplicationContext().getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> resovleInfos = packageManager.queryIntentActivities(
				mainIntent, 0);
		for (ResolveInfo resolve : resovleInfos) {
			// 应用图标:resolve.loadIcon(packageManager)
			// 应用名称:resolve.loadLabel(packageManager)
			// 应用包名：resolve.activityInfo.packageName
			// 应用启动的第一个Activity：resolve.activityInfo.name
			if (UpperCaseLetter(appName).equals(
					resolve.loadLabel(packageManager).toString())) {
				return resolve.activityInfo.packageName.toString();
			}
		}
		return "com.tchip.carlauncher";
	}

	public String UpperCaseLetter(String b) {
		char letters[] = new char[b.length()];
		for (int i = 0; i < b.length(); i++) {

			char letter = b.charAt(i);
			if (letter >= 'a' && letter <= 'z') {
				letter = (char) (letter - 32);
			}
			letters[i] = letter;
		}
		return new String(letters);
	}

}