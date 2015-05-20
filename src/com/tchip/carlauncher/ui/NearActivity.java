package com.tchip.carlauncher.ui;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.ui.ChatActivity.MyOnGetGeoCoderResultListener;
import com.tchip.carlauncher.util.PinYinUtil;
import com.tchip.carlauncher.util.ProgressAnimationUtil;
import com.tchip.carlauncher.view.ButtonFloat;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class NearActivity extends Activity {

	// 语义理解对象（语音到语义）。
	private SpeechUnderstander mSpeechUnderstander;
	private ImageView imgVoiceSearch;
	private SharedPreferences mSharedPreferences;
	private EditText editSearchContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_near);

		mSharedPreferences = getSharedPreferences("CarLauncher",
				Context.MODE_PRIVATE);
		editSearchContent = (EditText) findViewById(R.id.editSearchContent);

		ButtonFloat btnToViceFromNear = (ButtonFloat) findViewById(R.id.btnToViceFromNear);
		btnToViceFromNear.setDrawableIcon(getResources().getDrawable(
				R.drawable.icon_arrow_down));
		btnToViceFromNear.setOnClickListener(new MyOnClickListener());

		ImageView imgNearOilStation = (ImageView) findViewById(R.id.imgNearOilStation);
		imgNearOilStation.setOnClickListener(new MyOnClickListener());

		ImageView imgNearHotel = (ImageView) findViewById(R.id.imgNearHotel);
		imgNearHotel.setOnClickListener(new MyOnClickListener());

		ImageView imgNear4S = (ImageView) findViewById(R.id.imgNear4S);
		imgNear4S.setOnClickListener(new MyOnClickListener());

		ImageView imgNearMarket = (ImageView) findViewById(R.id.imgNearMarket);
		imgNearMarket.setOnClickListener(new MyOnClickListener());

		ImageView imgNearBank = (ImageView) findViewById(R.id.imgNearBank);
		imgNearBank.setOnClickListener(new MyOnClickListener());

		ImageView imgNearHospital = (ImageView) findViewById(R.id.imgNearHospital);
		imgNearHospital.setOnClickListener(new MyOnClickListener());

		ImageView imgCustomSearch = (ImageView) findViewById(R.id.imgCustomSearch);
		imgCustomSearch.setOnClickListener(new MyOnClickListener());

		ImageView imgVoiceSearch = (ImageView) findViewById(R.id.imgVoiceSearch);
		imgVoiceSearch.setOnClickListener(new MyOnClickListener());

	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnToViceFromNear:
				backToVice();
				break;
			case R.id.imgNearOilStation:
				Intent intent1 = new Intent(NearActivity.this,
						NearResultActivity.class);
				intent1.putExtra("findType", "加油站");
				startActivity(intent1);
				break;
			case R.id.imgNearHotel:
				Intent intent2 = new Intent(NearActivity.this,
						NearResultActivity.class);
				intent2.putExtra("findType", "酒店");
				startActivity(intent2);
				break;
			case R.id.imgNear4S:
				Intent intent3 = new Intent(NearActivity.this,
						NearResultActivity.class);
				intent3.putExtra("findType", "4S");
				startActivity(intent3);
				break;
			case R.id.imgNearMarket:
				Intent intent4 = new Intent(NearActivity.this,
						NearResultActivity.class);
				intent4.putExtra("findType", "超市");
				startActivity(intent4);
				break;
			case R.id.imgNearBank:
				Intent intent5 = new Intent(NearActivity.this,
						NearResultActivity.class);
				intent5.putExtra("findType", "ATM");
				startActivity(intent5);
				break;
			case R.id.imgNearHospital:
				Intent intent6 = new Intent(NearActivity.this,
						NearResultActivity.class);
				intent6.putExtra("findType", "医院");
				startActivity(intent6);
				break;
			case R.id.imgVoiceSearch:
				startVoiceUnderstand();
				break;
			case R.id.imgCustomSearch:
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
		public void onVolumeChanged(int v) {
			// showTip("onVolumeChanged：" + v);
		}

		@Override
		public void onEndOfSpeech() {
			// showTip("onEndOfSpeech");

		}

		@Override
		public void onBeginOfSpeech() {
			// showTip("onBeginOfSpeech");
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backToVice();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	private void backToVice() {
		finish();
		overridePendingTransition(R.anim.zms_translate_up_out,
				R.anim.zms_translate_up_in);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
	}
}
