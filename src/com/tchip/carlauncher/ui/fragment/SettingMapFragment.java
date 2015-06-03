package com.tchip.carlauncher.ui.fragment;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.ui.dialog.SettingMapNavigationDialog;
import com.tchip.carlauncher.ui.dialog.SettingMapRouteRecordDialog;
import com.tchip.carlauncher.ui.dialog.SettingMapRouteSmoothDialog;
import com.tchip.carlauncher.ui.dialog.SettingMapRouteSpanDialog;
import com.tchip.carlauncher.ui.dialog.SettingVoiceSpeakHourDialog;
import com.tchip.carlauncher.view.LayoutRipple;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingMapFragment extends Fragment {
	private View mapSettingView;
	private SharedPreferences preferences;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mapSettingView = inflater.inflate(R.layout.fragment_setting_map,
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

		preferences = getActivity().getSharedPreferences("CarLauncher",
				Context.MODE_PRIVATE);
		updateRouteRecordText();
		updateRouteSmoothText();
		updateRouteSpanText();
		updateNavigationText();

		return mapSettingView;
	}

	private void updateRouteRecordText() {
		TextView textRouteRecord = (TextView) mapSettingView
				.findViewById(R.id.textRouteRecord);
		boolean recordRouteConfig = preferences.getBoolean("routeRecord", true);
		if (!recordRouteConfig) {
			textRouteRecord.setText("关闭");
		} else {
			textRouteRecord.setText("打开");
		}
	}

	private void updateRouteSmoothText() {
		TextView textRouteSmooth = (TextView) mapSettingView
				.findViewById(R.id.textRouteSmooth);
		boolean recordSmoothConfig = preferences
				.getBoolean("routeSmooth", true);
		if (!recordSmoothConfig) {
			textRouteSmooth.setText("关闭");
		} else {
			textRouteSmooth.setText("打开");
		}
	}

	private void updateRouteSpanText() {
		TextView textRouteSpan = (TextView) mapSettingView
				.findViewById(R.id.textRouteSpan);
		String recordSpanConfig = preferences.getString("routeSpan", "HIGH");
		if ("LOW".equals(recordSpanConfig)) {
			textRouteSpan.setText("低");
		} else if ("MIDDLE".equals(recordSpanConfig)) {
			textRouteSpan.setText("中");
		} else {
			textRouteSpan.setText("高");
		}
	}

	private void updateNavigationText() {
		TextView textNavigation = (TextView) mapSettingView
				.findViewById(R.id.textNavigation);
		String navigationConfig = preferences.getString("defaultNavi", "BAIDU");
		if ("GAODE".equals(navigationConfig)) {
			textNavigation.setText("高德");
		} else {
			textNavigation.setText("百度");
		}
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layoutRippleRouteRecord:
				SettingMapRouteRecordDialog mapRecordRouteDialog = new SettingMapRouteRecordDialog(
						getActivity());
				mapRecordRouteDialog
						.setOnAcceptButtonClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								updateRouteRecordText();
							}
						});
				mapRecordRouteDialog.show();
				break;
			case R.id.layoutRippleRouteSmooth:
				SettingMapRouteSmoothDialog mapRecordSmoothDialog = new SettingMapRouteSmoothDialog(
						getActivity());
				mapRecordSmoothDialog
						.setOnAcceptButtonClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								updateRouteSmoothText();
							}
						});
				mapRecordSmoothDialog.show();
				break;
			case R.id.layoutRippleRouteSpan:
				SettingMapRouteSpanDialog mapRecordSpanDialog = new SettingMapRouteSpanDialog(
						getActivity());
				mapRecordSpanDialog
						.setOnAcceptButtonClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								updateRouteSpanText();
							}
						});
				mapRecordSpanDialog.show();
				break;
			case R.id.layoutRippleNavi:
				SettingMapNavigationDialog mapNavigationDialog = new SettingMapNavigationDialog(
						getActivity());
				mapNavigationDialog
						.setOnAcceptButtonClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								updateNavigationText();
							}
						});
				mapNavigationDialog.show();
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
