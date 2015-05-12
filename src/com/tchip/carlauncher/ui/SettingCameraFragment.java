package com.tchip.carlauncher.ui;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.view.LayoutRipple;
import com.tchip.carlauncher.view.MaterialDialog;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class SettingCameraFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View cameraSettingView = inflater.inflate(
				R.layout.fragment_setting_camera, container, false);
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

		return cameraSettingView;
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
								// Toast.makeText(getActivity(),
								// "Click accept button", 1)
								// .show();
							}
						});
				videoQulityDialog.show();
				break;
			case R.id.layoutRippleVideoSize:
				SettingCameraVideoSizeDialog videoSizeDialog = new SettingCameraVideoSizeDialog(
						getActivity());
				videoSizeDialog.show();
				break;
			case R.id.layoutRippleVideoTime:
				SettingCameraVideoTimeDialog videoTimeDialog = new SettingCameraVideoTimeDialog(
						getActivity());
				videoTimeDialog.show();
				break;
			case R.id.layoutRippleCrashSensitive:
				SettingCameraCrashSensitive crashSensitiveDialog = new SettingCameraCrashSensitive(
						getActivity());
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
				layoutRipple.setRippleSpeed(50);
			}
		});
	}

}
