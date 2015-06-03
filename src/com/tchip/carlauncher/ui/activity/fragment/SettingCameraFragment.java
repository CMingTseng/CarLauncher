package com.tchip.carlauncher.ui.activity.fragment;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.ui.activity.dialog.SettingCameraCrashSensitiveDialog;
import com.tchip.carlauncher.ui.activity.dialog.SettingCameraVideoQualityDialog;
import com.tchip.carlauncher.ui.activity.dialog.SettingCameraVideoSizeDialog;
import com.tchip.carlauncher.ui.activity.dialog.SettingCameraVideoTimeDialog;
import com.tchip.carlauncher.view.LayoutRipple;
import com.tchip.carlauncher.view.MaterialDialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SettingCameraFragment extends Fragment {
	private View cameraSettingView;
	private SharedPreferences preferences;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		cameraSettingView = inflater.inflate(R.layout.fragment_setting_camera,
				container, false);
		// 视频质量
		LayoutRipple layoutRippleVideoQuality = (LayoutRipple) cameraSettingView
				.findViewById(R.id.layoutRippleVideoQuality);
		iniRipple(layoutRippleVideoQuality);
		layoutRippleVideoQuality.setOnClickListener(new MyOnClickListener());

		// 视频尺寸
		LayoutRipple layoutRippleVideoSize = (LayoutRipple) cameraSettingView
				.findViewById(R.id.layoutRippleVideoSize);
		iniRipple(layoutRippleVideoSize);
		layoutRippleVideoSize.setOnClickListener(new MyOnClickListener());

		// 视频长度
		LayoutRipple layoutRippleVideoTime = (LayoutRipple) cameraSettingView
				.findViewById(R.id.layoutRippleVideoTime);
		iniRipple(layoutRippleVideoTime);
		layoutRippleVideoTime.setOnClickListener(new MyOnClickListener());

		// 碰撞灵敏度
		LayoutRipple layoutRippleCrashSensitive = (LayoutRipple) cameraSettingView
				.findViewById(R.id.layoutRippleCrashSensitive);
		iniRipple(layoutRippleCrashSensitive);
		layoutRippleCrashSensitive.setOnClickListener(new MyOnClickListener());

		preferences = getActivity().getSharedPreferences("CarLauncher",
				Context.MODE_PRIVATE);
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
