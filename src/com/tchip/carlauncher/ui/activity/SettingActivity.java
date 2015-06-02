package com.tchip.carlauncher.ui.activity;

import java.util.ArrayList;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.adapter.SettingFragmentPagerAdapter;
import com.tchip.carlauncher.ui.activity.fragment.SettingCameraFragment;
import com.tchip.carlauncher.ui.activity.fragment.SettingMapFragment;
import com.tchip.carlauncher.ui.activity.fragment.SettingSystemFragment;
import com.tchip.carlauncher.ui.activity.fragment.SettingVoiceFragment;
import com.tchip.carlauncher.view.ButtonFloat;
import com.tchip.carlauncher.view.SettingFadeTabIndicator;
import com.tchip.carlauncher.view.SettingFadeTabIndicator.FadingTab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SettingActivity extends FragmentActivity {
	private SettingFadeTabIndicator settingFadeTabIndicator;
	private ViewPager viewPager;
	private ArrayList<Fragment> fragmentList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_setting);

		InitViewPager();

		// 返回
		ButtonFloat btnToViceFromSetting = (ButtonFloat) findViewById(R.id.btnToViceFromSetting);
		btnToViceFromSetting.setDrawableIcon(getResources().getDrawable(
				R.drawable.icon_arrow_up));
		btnToViceFromSetting.setOnClickListener(new MyOnClickListener());
	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnToViceFromSetting:
				backToVice();
				break;
			default:
				break;
			}
		}
	}

	private void backToVice() {
		finish();
		overridePendingTransition(R.anim.zms_translate_down_out,
				R.anim.zms_translate_down_in);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backToVice();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	/*
	 * 初始化ViewPager
	 */
	public void InitViewPager() {

		settingFadeTabIndicator = (SettingFadeTabIndicator) findViewById(R.id.fade_tab_indicator);
		viewPager = (ViewPager) findViewById(R.id.view_pager);

		fragmentList = new ArrayList<Fragment>();
		Fragment settingDriveFragment = new SettingCameraFragment();
		fragmentList.add(settingDriveFragment);

		Fragment settingVoiceFragment = new SettingVoiceFragment();
		fragmentList.add(settingVoiceFragment);

		Fragment settingMapFragment = new SettingMapFragment();
		fragmentList.add(settingMapFragment);

		Fragment settingSystemFragment = new SettingSystemFragment();
		fragmentList.add(settingSystemFragment);

		// 给ViewPager设置适配器
		viewPager.setAdapter(new FadeTabFragmentPagerAdapter(this,
				getSupportFragmentManager(), fragmentList));
		viewPager.setCurrentItem(0);// 设置当前显示标签页为第一页

		settingFadeTabIndicator.setViewPager(viewPager);
	}

	private class FadeTabFragmentPagerAdapter extends
			SettingFragmentPagerAdapter implements FadingTab {

		public FadeTabFragmentPagerAdapter(Context context, FragmentManager fm,
				ArrayList<Fragment> fragmentList) {
			super(context, fm, fragmentList);
		}

		@Override
		public int getTabNormalIconResId(int position) {
			return new int[] { R.drawable.icon_tab_setting_camera_off,
					R.drawable.icon_tab_setting_voice_off,
					R.drawable.icon_tab_setting_map_off,
					R.drawable.icon_tab_setting_system_off }[position];
		}

		@Override
		public int getTabSelectIconResId(int position) {
			return new int[] { R.drawable.icon_tab_setting_camera_on,
					R.drawable.icon_tab_setting_voice_on,
					R.drawable.icon_tab_setting_map_on,
					R.drawable.icon_tab_setting_system_on }[position];
		}

		@Override
		public int getTabNormalTextColorResId(int position) {
			return R.color.text_normal;
		}

		@Override
		public int getTabSelectTextColorResId(int position) {
			return R.color.text_select;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
}
