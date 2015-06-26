package com.tchip.carlauncher.ui.fragment;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.ui.dialog.SettingCameraCrashSensitiveDialog;
import com.tchip.carlauncher.ui.dialog.SettingCameraVideoQualityDialog;
import com.tchip.carlauncher.ui.dialog.SettingCameraVideoSizeDialog;
import com.tchip.carlauncher.ui.dialog.SettingCameraVideoTimeDialog;
import com.tchip.carlauncher.view.LayoutRipple;
import com.tchip.carlauncher.view.MaterialDialog;
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
import android.widget.Toast;

public class SettingCameraFragment extends Fragment {
	private View cameraSettingView;
	private SharedPreferences preferences;
	private Editor editor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		cameraSettingView = inflater.inflate(R.layout.fragment_setting_camera,
				container, false);

		preferences = getActivity().getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		editor = preferences.edit();

		// 开机自动录像
		SwitchButton switchAutoRecord = (SwitchButton) cameraSettingView
				.findViewById(R.id.switchAutoRecord);
		switchAutoRecord
				.setChecked(preferences.getBoolean("autoRecord", false));
		switchAutoRecord
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						editor.putBoolean("autoRecord", isChecked);
						editor.commit();
					}
				});

		// 视频尺寸
		RelativeLayout layoutRippleVideoSize = (RelativeLayout) cameraSettingView
				.findViewById(R.id.layoutRippleVideoSize);
		layoutRippleVideoSize.setOnClickListener(new MyOnClickListener());

		// 视频长度
		RelativeLayout layoutRippleVideoTime = (RelativeLayout) cameraSettingView
				.findViewById(R.id.layoutRippleVideoTime);
		layoutRippleVideoTime.setOnClickListener(new MyOnClickListener());

		// 碰撞灵敏度
		RelativeLayout layoutRippleCrashSensitive = (RelativeLayout) cameraSettingView
				.findViewById(R.id.layoutRippleCrashSensitive);
		layoutRippleCrashSensitive.setOnClickListener(new MyOnClickListener());

		// 视频质量
		RelativeLayout layoutRippleVideoQuality = (RelativeLayout) cameraSettingView
				.findViewById(R.id.layoutRippleVideoQuality);
		layoutRippleVideoQuality.setOnClickListener(new MyOnClickListener());

		updateVideoQualityText();
		updateVideoSizeText();
		updateVideoTimeText();
		updateCrashSensitive();

		return cameraSettingView;
	}

	private void updateVideoQualityText() {
		TextView textVideoQuality = (TextView) cameraSettingView
				.findViewById(R.id.textVideoQuality);
		String videoQualityNow = preferences.getString("videoQuality", "HIGH");
		if ("LOW".equals(videoQualityNow)) {
			textVideoQuality.setText("低");
		} else if ("MIDDLE".equals(videoQualityNow)) {
			textVideoQuality.setText("中");
		} else {
			textVideoQuality.setText("高");
		}
	}

	private void updateVideoSizeText() {
		TextView textVideoSize = (TextView) cameraSettingView
				.findViewById(R.id.textVideoSize);
		String videoSizeNow = preferences.getString("videoSize", "720");
		if ("1080".equals(videoSizeNow)) {
			textVideoSize.setText("1080P");
		} else {
			textVideoSize.setText("720P");
		}
	}

	private void updateVideoTimeText() {
		TextView textVideoTime = (TextView) cameraSettingView
				.findViewById(R.id.textVideoTime);
		String videoTimeNow = preferences.getString("videoTime", "5");
		if ("3".equals(videoTimeNow)) {
			textVideoTime.setText("3分钟");
		} else if ("10".equals(videoTimeNow)) {
			textVideoTime.setText("10分钟");
		} else {
			textVideoTime.setText("5分钟");
		}
	}

	private void updateCrashSensitive() {
		TextView textCrashSensitive = (TextView) cameraSettingView
				.findViewById(R.id.textCrashSensitive);
		String crashSensitiveNow = preferences.getString("crashSensitive",
				"MIDDLE");

		if ("LOW".equals(crashSensitiveNow)) {
			textCrashSensitive.setText("低");
		} else if ("HIGH".equals(crashSensitiveNow)) {
			textCrashSensitive.setText("高");
		} else {
			textCrashSensitive.setText("中");
		}
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layoutRippleVideoQuality:
				SettingCameraVideoQualityDialog videoQulityDialog = new SettingCameraVideoQualityDialog(
						getActivity());
				videoQulityDialog
						.setOnAcceptButtonClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								updateVideoQualityText();
							}
						});
				videoQulityDialog.show();
				break;

			case R.id.layoutRippleVideoSize:
				SettingCameraVideoSizeDialog videoSizeDialog = new SettingCameraVideoSizeDialog(
						getActivity());
				videoSizeDialog
						.setOnAcceptButtonClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								updateVideoSizeText();
							}
						});
				videoSizeDialog.show();
				break;

			case R.id.layoutRippleVideoTime:
				SettingCameraVideoTimeDialog videoTimeDialog = new SettingCameraVideoTimeDialog(
						getActivity());
				videoTimeDialog
						.setOnAcceptButtonClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								updateVideoTimeText();
							}
						});
				videoTimeDialog.show();
				break;

			case R.id.layoutRippleCrashSensitive:
				SettingCameraCrashSensitiveDialog crashSensitiveDialog = new SettingCameraCrashSensitiveDialog(
						getActivity());
				crashSensitiveDialog
						.setOnAcceptButtonClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								updateCrashSensitive();
							}
						});
				crashSensitiveDialog.show();
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
				layoutRipple.setRippleSpeed(Constant.SETTING_ITEM_RIPPLE_SPEED);
			}
		});
	}

}
