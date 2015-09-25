package com.tchip.carlauncher.ui.fragment;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.ui.activity.MainActivity;
import com.tchip.carlauncher.ui.dialog.SettingMapNavigationDialog;
import com.tchip.carlauncher.ui.dialog.SettingMapRouteRecordDialog;
import com.tchip.carlauncher.ui.dialog.SettingMapRouteSmoothDialog;
import com.tchip.carlauncher.ui.dialog.SettingMapRouteSpanDialog;
import com.tchip.carlauncher.ui.dialog.SettingVoiceSpeakHourDialog;
import com.tchip.carlauncher.view.LayoutRipple;
import com.tchip.carlauncher.view.SwitchButton;

import android.content.Context;
import android.content.Intent;
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

public class SettingMapFragment extends Fragment {
	private View mapSettingView;
	private SharedPreferences preferences;
	private Editor editor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mapSettingView = inflater.inflate(R.layout.fragment_setting_map,
				container, false);
		preferences = getActivity().getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		editor = preferences.edit();

		// 离线地图管理
		RelativeLayout layoutRippleOfflineMap = (RelativeLayout) mapSettingView
				.findViewById(R.id.layoutRippleOfflineMap);
		layoutRippleOfflineMap.setOnClickListener(new MyOnClickListener());

		// 默认导航
		RelativeLayout layoutRippleNavi = (RelativeLayout) mapSettingView
				.findViewById(R.id.layoutRippleNavi);
		layoutRippleNavi.setOnClickListener(new MyOnClickListener());

		// 行车轨迹记录
		// RelativeLayout layoutRippleRouteRecord = (RelativeLayout)
		// mapSettingView
		// .findViewById(R.id.layoutRippleRouteRecord);
		// layoutRippleRouteRecord.setOnClickListener(new MyOnClickListener());

		SwitchButton switchRouteRecord = (SwitchButton) mapSettingView
				.findViewById(R.id.switchRouteRecord);
		switchRouteRecord.setChecked(preferences
				.getBoolean("routeRecord", true));
		switchRouteRecord
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						editor.putBoolean("routeRecord", isChecked);
						editor.commit();
						updateRouteRecordText();
					}
				});

		// 轨迹平滑度优化
		// RelativeLayout layoutRippleRouteSmooth = (RelativeLayout)
		// mapSettingView
		// .findViewById(R.id.layoutRippleRouteSmooth);
		// layoutRippleRouteSmooth.setOnClickListener(new MyOnClickListener());
		SwitchButton switchRouteSmooth = (SwitchButton) mapSettingView
				.findViewById(R.id.switchRouteSmooth);
		switchRouteSmooth.setChecked(preferences
				.getBoolean("routeSmooth", true));
		switchRouteSmooth
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						editor.putBoolean("routeSmooth", isChecked);
						editor.commit();
						updateRouteSmoothText();
					}
				});

		// 轨迹绘制取样精度
		RelativeLayout layoutRippleRouteSpan = (RelativeLayout) mapSettingView
				.findViewById(R.id.layoutRippleRouteSpan);
		layoutRippleRouteSpan.setOnClickListener(new MyOnClickListener());

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
			case R.id.layoutRippleOfflineMap:
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
			// case R.id.layoutRippleRouteRecord:
			// SettingMapRouteRecordDialog mapRecordRouteDialog = new
			// SettingMapRouteRecordDialog(
			// getActivity());
			// mapRecordRouteDialog
			// .setOnAcceptButtonClickListener(new View.OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// updateRouteRecordText();
			// }
			// });
			// mapRecordRouteDialog.show();
			// break;
			// case R.id.layoutRippleRouteSmooth:
			// SettingMapRouteSmoothDialog mapRecordSmoothDialog = new
			// SettingMapRouteSmoothDialog(
			// getActivity());
			// mapRecordSmoothDialog
			// .setOnAcceptButtonClickListener(new View.OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// updateRouteSmoothText();
			// }
			// });
			// mapRecordSmoothDialog.show();
			// break;
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
