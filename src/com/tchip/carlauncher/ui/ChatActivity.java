package com.tchip.carlauncher.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.sunflower.FlowerCollector;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.service.SpeakService;
import com.tchip.carlauncher.util.ProgressAnimationUtil;
import com.tchip.carlauncher.view.CircularProgressDrawable;

public class ChatActivity extends Activity implements OnClickListener {
	private static String TAG = ChatActivity.class.getSimpleName();
	// 语义理解对象（语音到语义）。
	private SpeechUnderstander mSpeechUnderstander;
	// 语义理解对象（文本到语义）。
	private TextUnderstander mTextUnderstander;
	private Toast mToast;
	private EditText tvHint;
	private TextView tvQuestion, tvAnswer;

	private SharedPreferences mSharedPreferences;

	// 动画按钮
	private ImageView ivDrawable;
	private Animator currentAnimation;
	private CircularProgressDrawable drawable;

	private ScrollView scrollArea;

	@SuppressLint("ShowToast")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat);

		initLayout();
		// 初始化对象
		mSpeechUnderstander = SpeechUnderstander.createUnderstander(
				ChatActivity.this, speechUnderstanderListener);
		mTextUnderstander = TextUnderstander.createTextUnderstander(
				ChatActivity.this, textUnderstanderListener);

		mToast = Toast.makeText(ChatActivity.this, "", Toast.LENGTH_SHORT);

		// 监听屏幕熄灭与点亮
		// final IntentFilter screenFilter = new IntentFilter();
		// screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
		// screenFilter.addAction(Intent.ACTION_SCREEN_ON);
		// registerReceiver(ScreenOnOffReceiver, screenFilter);
		
		startSpeak("你好，我是小天，有什么可以帮您？");

	}

	private final BroadcastReceiver ScreenOnOffReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();
			if (Intent.ACTION_SCREEN_ON.equals(action)) {
				Log.e("zms", "-----------------screen is on...");
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				Log.e("zms", "----------------- screen is off...");

			}
		}
	};

	/**
	 * 初始化Layout。
	 */
	private void initLayout() {
		ivDrawable = (ImageView) findViewById(R.id.iv_drawable);
		findViewById(R.id.iv_drawable).setOnClickListener(ChatActivity.this);

		tvHint = (EditText) findViewById(R.id.tvHint);

		mSharedPreferences = getSharedPreferences("CarLauncher",
				getApplicationContext().MODE_PRIVATE);

		drawable = new CircularProgressDrawable.Builder()
				.setRingWidth(
						getResources().getDimensionPixelSize(
								R.dimen.drawable_ring_size))
				.setOutlineColor(
						getResources().getColor(android.R.color.darker_gray))
				.setRingColor(
						getResources().getColor(
								android.R.color.holo_green_light))
				.setCenterColor(
						getResources().getColor(android.R.color.holo_blue_dark))
				.create();
		ivDrawable.setImageDrawable(drawable);

		scrollArea = (ScrollView) findViewById(R.id.scrollArea);
		tvQuestion = (TextView) findViewById(R.id.tvQuestion);
		tvAnswer = (TextView) findViewById(R.id.tvAnswer);

	}

	/**
	 * 初始化监听器（语音到语义）。
	 */
	private InitListener speechUnderstanderListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "speechUnderstanderListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				// 初始化失败,错误码：code
			}
		}
	};

	/**
	 * 初始化监听器（文本到语义）。
	 */
	private InitListener textUnderstanderListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d(TAG, "textUnderstanderListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				// 初始化失败,错误码： code
			}
		}
	};

	int ret = 0;// 函数调用返回值

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		// 进入参数设置页面
		// Intent intent = new Intent(ChatActivity.this,
		// UnderstanderSettings.class);
		// startActivity(intent);
		// 开始文本理解
		// mUnderstanderText.setText("");
		// String text = "明天的天气怎么样？";
		// if (mTextUnderstander.isUnderstanding()) {
		// mTextUnderstander.cancel();
		// } else {
		// ret = mTextUnderstander.understandText(text, textListener);
		// if (ret != 0) {
		// }
		// }
		// 开始语音理解
		case R.id.iv_drawable:
			tvHint.setText("");
			// 设置参数
			setParam();

			if (mSpeechUnderstander.isUnderstanding()) { // 开始前检查状态
				mSpeechUnderstander.stopUnderstanding();
				// 停止录音
			} else {
				ret = mSpeechUnderstander
						.startUnderstanding(mRecognizerListener);
				if (ret != 0) {
					// 语义理解失败,错误码:ret
				} else {
					showTip(getString(R.string.text_begin));
				}
			}
			break;
		// 停止语音理解
		// case R.id.understander_stop:
		// mSpeechUnderstander.stopUnderstanding();
		// break;
		// 取消语音理解
		// case R.id.understander_cancel:
		// mSpeechUnderstander.cancel();
		// break;
		default:
			break;
		}
	}

	private TextUnderstanderListener textListener = new TextUnderstanderListener() {

		@Override
		public void onResult(final UnderstanderResult result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (null != result) {
						// 显示
						// String text = result.getResultString();
						// if (!TextUtils.isEmpty(text)) {
						// tvHint.setText(text);
						// }
					} else {
						// 识别结果不正确
					}
				}
			});
		}

		@Override
		public void onError(SpeechError error) {
			// showTip("onError Code：" + error.getErrorCode());

		}
	};

	/**
	 * 识别回调。
	 */
	private SpeechUnderstanderListener mRecognizerListener = new SpeechUnderstanderListener() {

		@Override
		public void onResult(final UnderstanderResult result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (null != result) {

						tvAnswer.setText(""); // 清空回答
						// 显示
						String text = result.getResultString();
						if (!TextUtils.isEmpty(text)) {
							tvHint.setText(text);

							try {
								JSONObject jsonObject;
								jsonObject = new JSONObject(text);
								String strQuestion = jsonObject
										.getString("text");
								tvQuestion.setText(strQuestion);

								String strService = jsonObject
										.getString("service");
								if ("openQA".equals(strService)
										|| "datetime".equals(strService)
										|| "chat".equals(strService)) {
									String strAnswer = jsonObject
											.getJSONObject("answer").getString(
													"text");
									tvAnswer.setText(strAnswer);
									startSpeak(strAnswer);
								} else if ("baike".equals(strService)) {
									String strAnswer = jsonObject
											.getJSONObject("answer").getString(
													"text");
									tvAnswer.setText(strAnswer);
								} else if ("weather".equals(strService)) {

									JSONArray mJSONArray = jsonObject
											.getJSONObject("data")
											.getJSONArray("result");
									JSONObject todayJSON = mJSONArray
											.getJSONObject(0);
									String tempRange = todayJSON
											.getString("tempRange");
									String weather = todayJSON
											.getString("weather");
									String city = todayJSON.getString("city");
									String strAnswer = city + "天气：" + weather
											+ ",温度" + tempRange;
									tvAnswer.setText(strAnswer);
									startSpeak(strAnswer);
								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();

								String strNoAnswer = "小天不知道怎么回答了";
								tvAnswer.setText(strNoAnswer);
								startSpeak(strNoAnswer);
							}
							makeScrollViewDown(scrollArea);

						}
					} else {
						// 识别结果不正确
					}
				}
			});
		}

		@Override
		public void onVolumeChanged(int v) {
			// showTip("onVolumeChanged：" + v);
		}

		@Override
		public void onEndOfSpeech() {
			// showTip("onEndOfSpeech");
			if (currentAnimation != null) {
				currentAnimation.cancel();
			}
			// currentAnimation = prepareStyle1Animation();
			// currentAnimation = prepareStyle2Animation();
			// currentAnimation = prepareStyle3Animation();
			currentAnimation = ProgressAnimationUtil
					.preparePulseAnimation(drawable);
			currentAnimation.start();

		}

		@Override
		public void onBeginOfSpeech() {
			// showTip("onBeginOfSpeech");
			if (currentAnimation != null) {
				currentAnimation.cancel();
			}
			currentAnimation = ProgressAnimationUtil
					.prepareStyle1Animation(drawable);
			// currentAnimation = prepareStyle2Animation();
			// currentAnimation = prepareStyle3Animation();
			// currentAnimation = preparePulseAnimation();
			currentAnimation.start();
		}

		@Override
		public void onError(SpeechError error) {
			// showTip("onError Code：" + error.getErrorCode());
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * 跳转ScrollView到底部
	 */
	private void makeScrollViewDown(ScrollView scrollView) {
		scrollView.fullScroll(ScrollView.FOCUS_DOWN);
	}

	private void startSpeak(String content) {
		Intent intent = new Intent(ChatActivity.this, SpeakService.class);
		intent.putExtra("content", content);
		startService(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出时释放连接
		mSpeechUnderstander.cancel();
		mSpeechUnderstander.destroy();
		if (mTextUnderstander.isUnderstanding())
			mTextUnderstander.cancel();
		mTextUnderstander.destroy();
	}

	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}

	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	public void setParam() {
		String lag = mSharedPreferences.getString("voiceAccent", "mandarin");
		if (lag.equals("en_us")) {
			// 设置语言
			mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mSpeechUnderstander.setParameter(SpeechConstant.ACCENT, lag);
		}
		// 设置语音前端点
		mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS,
				mSharedPreferences.getString("voiceBos", "4000"));
		// 设置语音后端点
		mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS,
				mSharedPreferences.getString("voiceEos", "1000"));
		// 设置标点符号
		mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT,
				mSharedPreferences.getString("understander_punc_preference",
						"1"));
		// 设置音频保存路径
		mSpeechUnderstander.setParameter(
				SpeechConstant.ASR_AUDIO_PATH,
				mSharedPreferences.getString("voicePath",
						Environment.getExternalStorageDirectory()
								+ "/iflytek/wavaudio.pcm")

		);
	}

	@Override
	protected void onResume() {
		// 移动数据统计分析
		FlowerCollector.onResume(ChatActivity.this);
		FlowerCollector.onPageStart(TAG);
		super.onResume();

		// 隐藏状态栏
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
	}

	@Override
	protected void onPause() {
		// 移动数据统计分析
		FlowerCollector.onPageEnd(TAG);
		FlowerCollector.onPause(ChatActivity.this);
		super.onPause();
	}

}
