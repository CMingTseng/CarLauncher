package com.tchip.carlauncher.ui;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.view.LayoutRipple;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingVoiceFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View voiceSettingView = inflater.inflate(
				R.layout.fragment_setting_voice, container, false);
		// 整点报时
		LayoutRipple layoutRippleSpeakHour = (LayoutRipple) voiceSettingView
				.findViewById(R.id.layoutRippleSpeakHour);
		iniRipple(layoutRippleSpeakHour);
		layoutRippleSpeakHour.setOnClickListener(new MyOnClickListener());

		// 语音口音
		LayoutRipple layoutRippleVoiceAccent = (LayoutRipple) voiceSettingView
				.findViewById(R.id.layoutRippleVoiceAccent);
		iniRipple(layoutRippleVoiceAccent);
		layoutRippleVoiceAccent.setOnClickListener(new MyOnClickListener());

		// 天气自动播报
		LayoutRipple layoutRippleSpeakWeather = (LayoutRipple) voiceSettingView
				.findViewById(R.id.layoutRippleSpeakWeather);
		iniRipple(layoutRippleSpeakWeather);
		layoutRippleSpeakWeather.setOnClickListener(new MyOnClickListener());

		// 天气数据自动更新
		LayoutRipple layoutRippleUpdateWeather = (LayoutRipple) voiceSettingView
				.findViewById(R.id.layoutRippleUpdateWeather);
		iniRipple(layoutRippleUpdateWeather);
		layoutRippleUpdateWeather.setOnClickListener(new MyOnClickListener());

		return voiceSettingView;
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layoutRippleSpeakHour:
				break;
			case R.id.layoutRippleVoiceAccent:
				break;
			case R.id.layoutRippleSpeakWeather:
				break;
			case R.id.layoutRippleUpdateWeather:
				break;
			default:
				break;
			}
		}
	}

	private void iniRipple(final LayoutRipple layoutRipple) {

		layoutRipple.post(new Runnable() {
			@Override
			public void run() {
				// 让Ripple的圆心在Image处
				// View v = layoutRipple.getChildAt(0);
				// layoutRipple.setxRippleOrigin(ViewHelper.getX(v) +
				// v.getWidth()
				// / 2);
				// layoutRipple.setyRippleOrigin(ViewHelper.getY(v)
				// + v.getHeight() / 2);
				layoutRipple.setRippleColor(Color.parseColor("#1E88E5"));
				layoutRipple.setRippleSpeed(50);
			}
		});
	}

}
