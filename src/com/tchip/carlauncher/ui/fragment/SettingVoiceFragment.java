package com.tchip.carlauncher.ui.fragment;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.ui.dialog.SettingVoiceAccentDialog;
import com.tchip.carlauncher.ui.dialog.SettingVoiceSpeakHourDialog;
import com.tchip.carlauncher.ui.dialog.SettingVoiceSpeakWeatherDialog;
import com.tchip.carlauncher.ui.dialog.SettingVoiceUpdateWeatherDialog;
import com.tchip.carlauncher.view.LayoutRipple;
import com.tchip.carlauncher.view.SwitchButton;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingVoiceFragment extends Fragment {
	private View voiceSettingView;
	private SharedPreferences preferences;
	private Editor editor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		voiceSettingView = inflater.inflate(R.layout.fragment_setting_voice,
				container, false);

		preferences = getActivity().getSharedPreferences("CarLauncher",
				Context.MODE_PRIVATE);
		editor = preferences.edit();

		// 整点报时
		// RelativeLayout layoutRippleSpeakHour = (RelativeLayout)
		// voiceSettingView
		// .findViewById(R.id.layoutRippleSpeakHour);
		// layoutRippleSpeakHour.setOnClickListener(new MyOnClickListener());

		SwitchButton switchSpeakHour = (SwitchButton) voiceSettingView
				.findViewById(R.id.switchSpeakHour);
		switchSpeakHour.setChecked(preferences.getBoolean("voiceSpeakHour",
				false));
		switchSpeakHour
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						editor.putBoolean("voiceSpeakHour", isChecked);
						editor.commit();
						updateVoiceSpeakHourText();
					}
				});

		// 语音口音
		RelativeLayout layoutRippleVoiceAccent = (RelativeLayout) voiceSettingView
				.findViewById(R.id.layoutRippleVoiceAccent);
		layoutRippleVoiceAccent.setOnClickListener(new MyOnClickListener());

		// 天气自动播报
		// RelativeLayout layoutRippleSpeakWeather = (RelativeLayout)
		// voiceSettingView
		// .findViewById(R.id.layoutRippleSpeakWeather);
		// layoutRippleSpeakWeather.setOnClickListener(new MyOnClickListener());

		SwitchButton switchSpeakWeather = (SwitchButton) voiceSettingView
				.findViewById(R.id.switchSpeakWeather);
		switchSpeakWeather.setChecked(preferences.getBoolean(
				"voiceSpeakWeather", true));
		switchSpeakWeather
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						editor.putBoolean("voiceSpeakWeather", isChecked);
						editor.commit();
						updateVoiceSpeakHourText();
					}
				});

		// 天气数据自动更新
		// RelativeLayout layoutRippleUpdateWeather = (RelativeLayout)
		// voiceSettingView
		// .findViewById(R.id.layoutRippleUpdateWeather);
		// layoutRippleUpdateWeather.setOnClickListener(new
		// MyOnClickListener());
		SwitchButton switchUpdateWeather = (SwitchButton) voiceSettingView
				.findViewById(R.id.switchUpdateWeather);
		switchUpdateWeather.setChecked(preferences.getBoolean(
				"voiceUpdateWeather", true));
		switchUpdateWeather
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						editor.putBoolean("voiceUpdateWeather", isChecked);
						editor.commit();
						updateVoiceUpdateWeatherText();
					}
				});

		updateVoiceSpeakHourText();
		updateVoiceAccentText();
		updateVoiceSpeakWeatherText();
		updateVoiceUpdateWeatherText();

		return voiceSettingView;
	}

	private void updateVoiceSpeakHourText() {
		TextView textVoiceSpeakHour = (TextView) voiceSettingView
				.findViewById(R.id.textVoiceSpeakHour);
		boolean voiceSpeakHourNow = preferences.getBoolean("voiceSpeakHour",
				false);
		if (voiceSpeakHourNow) {
			textVoiceSpeakHour.setText("打开");
		} else {
			textVoiceSpeakHour.setText("关闭");
		}
	}

	private void updateVoiceSpeakWeatherText() {
		TextView textSpeakWeather = (TextView) voiceSettingView
				.findViewById(R.id.textSpeakWeather);
		boolean voiceSpeakWeatherNow = preferences.getBoolean(
				"voiceSpeakWeather", true);
		if (voiceSpeakWeatherNow) {
			textSpeakWeather.setText("打开");
		} else {
			textSpeakWeather.setText("关闭");
		}
	}

	private void updateVoiceUpdateWeatherText() {
		TextView textUpdateWeather = (TextView) voiceSettingView
				.findViewById(R.id.textUpdateWeather);
		boolean voiceSpeakWeatherNow = preferences.getBoolean(
				"voiceUpdateWeather", true);
		if (voiceSpeakWeatherNow) {
			textUpdateWeather.setText("打开");
		} else {
			textUpdateWeather.setText("关闭");
		}
	}

	private void updateVoiceAccentText() {
		TextView textVoiceAccent = (TextView) voiceSettingView
				.findViewById(R.id.textVoiceAccent);
		String voiceAccentConfig = preferences.getString("voiceAccent",
				"Mandarin");
		if ("English".equals(voiceAccentConfig)) {
			textVoiceAccent.setText("英语");
		} else if ("Cantonese".equals(voiceAccentConfig)) {
			textVoiceAccent.setText("粤语");
		} else {
			textVoiceAccent.setText("普通话");
		}
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// case R.id.layoutRippleSpeakHour:
			// SettingVoiceSpeakHourDialog voiceSpeakHourDialog = new
			// SettingVoiceSpeakHourDialog(
			// getActivity());
			// voiceSpeakHourDialog
			// .setOnAcceptButtonClickListener(new View.OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// updateVoiceSpeakHourText();
			// }
			// });
			// voiceSpeakHourDialog.show();
			// break;
			case R.id.layoutRippleVoiceAccent:
				SettingVoiceAccentDialog voiceAccentDialog = new SettingVoiceAccentDialog(
						getActivity());
				voiceAccentDialog
						.setOnAcceptButtonClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								updateVoiceAccentText();
							}
						});
				voiceAccentDialog.show();
				break;
			// case R.id.layoutRippleSpeakWeather:
			// SettingVoiceSpeakWeatherDialog voiceSpeakWeatherDialog = new
			// SettingVoiceSpeakWeatherDialog(
			// getActivity());
			// voiceSpeakWeatherDialog
			// .setOnAcceptButtonClickListener(new View.OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// updateVoiceSpeakWeatherText();
			// }
			// });
			// voiceSpeakWeatherDialog.show();
			// break;
			// case R.id.layoutRippleUpdateWeather:
			// SettingVoiceUpdateWeatherDialog voiceUpdateWeatherDialog = new
			// SettingVoiceUpdateWeatherDialog(
			// getActivity());
			// voiceUpdateWeatherDialog
			// .setOnAcceptButtonClickListener(new View.OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// updateVoiceUpdateWeatherText();
			// }
			// });
			// voiceUpdateWeatherDialog.show();
			// break;
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
				layoutRipple.setRippleSpeed(Constant.SETTING_ITEM_RIPPLE_SPEED);
			}
		});
	}

}
