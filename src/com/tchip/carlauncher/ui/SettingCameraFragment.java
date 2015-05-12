package com.tchip.carlauncher.ui;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.view.LayoutRipple;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
		LayoutRipple layoutRippleSensitive = (LayoutRipple) cameraSettingView
				.findViewById(R.id.layoutRippleSensitive);
		iniRipple(layoutRippleSensitive);
		layoutRippleSensitive.setOnClickListener(new MyOnClickListener());

		return cameraSettingView;
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layoutRippleVideoQuality:
				break;
			case R.id.layoutRippleVideoSize:
				break;
			case R.id.layoutRippleVideoTime:
				break;
			case R.id.layoutRippleSensitive:
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
