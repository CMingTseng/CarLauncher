package com.tchip.carlauncher.ui;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.view.LayoutRipple;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingMapFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View mapSettingView = inflater.inflate(R.layout.fragment_setting_map,
				container, false);
		// 行车轨迹记录
		LayoutRipple layoutRippleRouteRecord = (LayoutRipple) mapSettingView
				.findViewById(R.id.layoutRippleRouteRecord);
		iniRipple(layoutRippleRouteRecord);
		layoutRippleRouteRecord.setOnClickListener(new MyOnClickListener());

		// 轨迹平滑度优化
		LayoutRipple layoutRippleRouteSmooth = (LayoutRipple) mapSettingView
				.findViewById(R.id.layoutRippleRouteSmooth);
		iniRipple(layoutRippleRouteSmooth);
		layoutRippleRouteSmooth.setOnClickListener(new MyOnClickListener());

		// 轨迹绘制取样精度
		LayoutRipple layoutRippleRouteSpan = (LayoutRipple) mapSettingView
				.findViewById(R.id.layoutRippleRouteSpan);
		iniRipple(layoutRippleRouteSpan);
		layoutRippleRouteSpan.setOnClickListener(new MyOnClickListener());

		// 默认导航
		LayoutRipple layoutRippleNavi = (LayoutRipple) mapSettingView
				.findViewById(R.id.layoutRippleNavi);
		iniRipple(layoutRippleNavi);
		layoutRippleNavi.setOnClickListener(new MyOnClickListener());

		return mapSettingView;
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layoutRippleRouteRecord:
				break;
			case R.id.layoutRippleRouteSmooth:
				break;
			case R.id.layoutRippleRouteSpan:
				break;
			case R.id.layoutRippleNavi:
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
