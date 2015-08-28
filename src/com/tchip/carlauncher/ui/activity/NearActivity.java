package com.tchip.carlauncher.ui.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.service.SpeakService;
import com.tchip.carlauncher.util.NetworkUtil;
import com.tchip.carlauncher.view.AudioRecordDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class NearActivity extends Activity {

	// 语义理解对象（语音到语义）。
	private SpeechUnderstander mSpeechUnderstander;
	private ImageView imgVoiceSearch;
	private SharedPreferences mSharedPreferences;
	private EditText editSearchContent;
	private AudioRecordDialog audioRecordDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_near);

		audioRecordDialog = new AudioRecordDialog(NearActivity.this);

		mSharedPreferences = getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		editSearchContent = (EditText) findViewById(R.id.editSearchContent);

		Button btnToViceFromNear = (Button) findViewById(R.id.btnToViceFromNear);
		btnToViceFromNear.setOnClickListener(new MyOnClickListener());

		RelativeLayout layoutNearOilStation = (RelativeLayout) findViewById(R.id.layoutNearOilStation);
		layoutNearOilStation.setOnClickListener(new MyOnClickListener());

		RelativeLayout layoutNearParking = (RelativeLayout) findViewById(R.id.layoutNearParking);
		layoutNearParking.setOnClickListener(new MyOnClickListener());

		RelativeLayout layoutNear4s = (RelativeLayout) findViewById(R.id.layoutNear4s);
		layoutNear4s.setOnClickListener(new MyOnClickListener());

		RelativeLayout layoutNearBank = (RelativeLayout) findViewById(R.id.layoutNearBank);
		layoutNearBank.setOnClickListener(new MyOnClickListener());

		RelativeLayout layoutShop = (RelativeLayout) findViewById(R.id.layoutShop);
		layoutShop.setOnClickListener(new MyOnClickListener());

		RelativeLayout layoutNearHotel = (RelativeLayout) findViewById(R.id.layoutNearHotel);
		layoutNearHotel.setOnClickListener(new MyOnClickListener());

		Button btnCustomSearch = (Button) findViewById(R.id.btnCustomSearch);
		btnCustomSearch.setOnClickListener(new MyOnClickListener());

		ImageView imgVoiceSearch = (ImageView) findViewById(R.id.imgVoiceSearch);
		imgVoiceSearch.setOnClickListener(new MyOnClickListener());

	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnToViceFromNear:
				backToVice();
				break;

			case R.id.layoutNearOilStation:
				Intent intent1 = new Intent(NearActivity.this,
						NearResultActivity.class);
				intent1.putExtra("findType", "加油站");
				startActivity(intent1);
				break;

			case R.id.layoutNearParking:
				Intent intent2 = new Intent(NearActivity.this,
						NearResultActivity.class);
				intent2.putExtra("findType", "停车场");
				startActivity(intent2);
				break;

			case R.id.layoutNear4s:
				Intent intent3 = new Intent(NearActivity.this,
						NearResultActivity.class);
				intent3.putExtra("findType", "4S");
				startActivity(intent3);
				break;

			case R.id.layoutNearBank:
				Intent intent4 = new Intent(NearActivity.this,
						NearResultActivity.class);
				intent4.putExtra("findType", "ATM");
				startActivity(intent4);
				break;

			case R.id.layoutShop:
				Intent intent5 = new Intent(NearActivity.this,
						NearResultActivity.class);
				intent5.putExtra("findType", "超市");
				startActivity(intent5);
				break;

			case R.id.layoutNearHotel:
				Intent intent6 = new Intent(NearActivity.this,
						NearResultActivity.class);
				intent6.putExtra("findType", "酒店");
				startActivity(intent6);
				break;

			case R.id.imgVoiceSearch:
				if (-1 == NetworkUtil.getNetworkType(getApplicationContext())) {
					NetworkUtil.noNetworkHint(getApplicationContext());
				} else {
					startVoiceUnderstand();
				}
				break;

			case R.id.btnCustomSearch:
				String searchContent = editSearchContent.getText().toString();
				if (searchContent != null && searchContent.length() > 0) {
					Intent intent7 = new Intent(NearActivity.this,
							NearResultActivity.class);
					intent7.putExtra("findType", searchContent);
					startActivity(intent7);
				}
				break;
			}

		}
	}

	int ret = 0;// 函数调用返回值

	public void startVoiceUnderstand() {
		// 初始化对象
		mSpeechUnderstander = SpeechUnderstander.createUnderstander(
				NearActivity.this, speechUnderstanderListener);
		setParam();

		if (mSpeechUnderstander.isUnderstanding()) { // 开始前检查状态
			mSpeechUnderstander.stopUnderstanding(); // 停止录音
		} else {
			ret = mSpeechUnderstander.startUnderstanding(mRecognizerListener);
			if (ret != 0) {
				// 语义理解失败,错误码:ret
			} else {
				// showTip(getString(R.string.text_begin));
			}
		}
	}

	/**
	 * 初始化监听器（语音到语义）。
	 */
	private InitListener speechUnderstanderListener = new InitListener() {
		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
				// 初始化失败,错误码：code
			}
		}
	};

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
								+ "/iflytek/wavaudio.pcm"));
	}

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
						// 显示
						String text = result.getResultString();

						try {
							JSONObject jsonObject;
							jsonObject = new JSONObject(text);
							String strContent = jsonObject.getString("text");
							if (!TextUtils.isEmpty(text)) {
								editSearchContent.setText(strContent);
								Intent intent1 = new Intent(NearActivity.this,
										NearResultActivity.class);
								intent1.putExtra("findType", strContent);
								startActivity(intent1);
							}
						} catch (JSONException e) {

						}
					} else {
						// 识别结果不正确
					}
				}
			});
		}

		@Override
		public void onVolumeChanged(int volume, byte[] arg1) {
			audioRecordDialog.updateVolumeLevel(volume);
		}

		@Override
		public void onEndOfSpeech() {
			// showTip("onEndOfSpeech");
			audioRecordDialog.dismissDialog();

		}

		@Override
		public void onBeginOfSpeech() {
			// showTip("onBeginOfSpeech");
			audioRecordDialog.showVoiceDialog();
		}

		@Override
		public void onError(SpeechError error) {
			// showTip("onError Code：" + error.getErrorCode());
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backToVice();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	private void startSpeak(String content) {
		Intent intent = new Intent(NearActivity.this, SpeakService.class);
		intent.putExtra("content", content);
		startService(intent);
	}

	private void backToVice() {
		finish();
		overridePendingTransition(R.anim.zms_translate_down_out,
				R.anim.zms_translate_down_in);
	}

	@Override
	protected void onResume() {
		super.onResume();
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
	}
}
