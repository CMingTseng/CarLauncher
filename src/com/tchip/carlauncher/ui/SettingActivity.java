package com.tchip.carlauncher.ui;

import java.util.ArrayList;
import java.util.List;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.view.MyViewPager;
import com.tchip.carlauncher.view.MyViewPagerContainer;
import com.tchip.carlauncher.view.MyViewPager.TransitionEffect;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

public class SettingActivity extends Activity {
	private View viewMain, viewVice;
	private List<View> viewList;
	private MyViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);

		LayoutInflater inflater = getLayoutInflater().from(this);
		viewMain = inflater.inflate(R.layout.activity_setting_one, null);
		viewVice = inflater.inflate(R.layout.activity_setting_two, null);

		viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
		viewList.add(viewMain);
		viewList.add(viewVice);
		viewPager = (MyViewPager) findViewById(R.id.viewpager);
		viewPager.setTransitionEffect(TransitionEffect.CubeOut);

		viewPager.setPageMargin(10);
		viewPager.setAdapter(pagerAdapter);
	}

	PagerAdapter pagerAdapter = new PagerAdapter() {

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			if (view instanceof MyViewPagerContainer) {
				return ((MyViewPagerContainer) view).getChildAt(0) == obj;
			} else {
				return view == obj;
			}
		}

		@Override
		public int getCount() {
			return viewList.size();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(viewList.get(position));
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(viewList.get(position));
			viewPager.setObjectForPosition(viewList.get(position), position); // 动画需要
			if (position == 0)
				updateMainLayout();
			else
				updateViceLayout();
			return viewList.get(position);
		}

	};

	private void updateMainLayout() {

	}

	private void updateViceLayout() {

	}

	/**
	 * 调整系统亮度
	 * 
	 * @param brightness
	 */
	private void setBrightness(int brightness) {
		try {
			int brightnessNow = Settings.System.getInt(getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS);
			if (brightness < 255 && brightness > 0)
				Settings.System.putInt(getContentResolver(),
						Settings.System.SCREEN_BRIGHTNESS, brightness);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
	}

}
