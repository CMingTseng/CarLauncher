package com.tchip.carlauncher.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.text.TextUtils;

public class WeatherService extends Service {
	private TextUnderstander mTextUnderstander;
	private SharedPreferences preferences;
	private Editor editor;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		preferences = getSharedPreferences("CarLauncher",
				getApplicationContext().MODE_PRIVATE);
		editor = preferences.edit();

		mTextUnderstander = TextUnderstander.createTextUnderstander(
				getApplicationContext(), textUnderstanderListener);

		getWeather(preferences.getString("cityName", "北京北京"));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		return super.onStartCommand(intent, flags, startId);
	}

	private InitListener textUnderstanderListener = new InitListener() {

		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
				// 初始化失败,错误码：" + code
				editor.putString("exception", "初始化失败,错误码：" + code);
			}
		}
	};

	private TextUnderstanderListener textListener = new TextUnderstanderListener() {

		@Override
		public void onResult(final UnderstanderResult result) {

			if (null != result) {
				// 获取结果
				String jsonString = result.getResultString();
				if (!TextUtils.isEmpty(jsonString)) {
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(jsonString);
						JSONArray mJSONArray = jsonObject.getJSONObject("data")
								.getJSONArray("result");
						for (int i = 0; i < 7; i++) {
							JSONObject jsonDay = mJSONArray.getJSONObject(i);
							String tempRange = jsonDay.getString("tempRange"); // 31℃~26℃
							String tempArray[] = tempRange.split("~");
							editor.putString("postTime",
									jsonDay.getString("lastUpdateTime"));

							editor.putString("day" + i + "weather",
									jsonDay.getString("weather"));
							editor.putString("day" + i + "tmpHigh",
									tempArray[0]);
							editor.putString("day" + i + "tmpLow", tempArray[1]);
							if (i == 1) {
								editor.putString("humidity",
										jsonDay.getString("humidity"));
								editor.putString("airQuality",
										jsonDay.getString("airQuality"));
							}

							String windDirection = jsonDay.getString("wind");
							if ("无持续风向微风".equals(windDirection))
								windDirection = "微风";
							editor.putString("day" + i + "wind", windDirection);
							// + jsonDay.getString("windLevel")
							editor.putString("day" + i + "date",
									jsonDay.getString("date"));

							editor.commit();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						editor.putString("exception", e.toString());
					} finally {
						stopSelf();
					}
				}
			} else {
				editor.putString("exception", " 识别结果不正确");
				// 识别结果不正确
			}

		}

		@Override
		public void onError(SpeechError error) {
			// showTip("onError Code：" + error.getErrorCode());
		}
	};

	private void getWeather(String cityStr) {
		int ret = 0;// 函数调用返回值

		String text = cityStr + "天气";

		if (mTextUnderstander.isUnderstanding()) {
			mTextUnderstander.cancel();
			// showTip("取消");
		} else {
			ret = mTextUnderstander.understandText(text, textListener);
			if (ret != 0) {
				// showTip("语义理解失败,错误码:" + ret);
			}
		}
	}

}
