package com.tchip.carlauncher.adapter;

import java.util.ArrayList;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.ui.fragment.SettingCameraFragment;
import com.tchip.carlauncher.view.SampleFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SettingFragmentPagerAdapter extends FragmentPagerAdapter {

	private Context mContext;
	private ArrayList<Fragment> list;

	public SettingFragmentPagerAdapter(Context context, FragmentManager fm,
			ArrayList<Fragment> list) {
		super(fm);
		this.mContext = context;
		this.list = list;
	}

	@Override
	public Fragment getItem(int position) {
		return list.get(position);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return new String[] { mContext.getString(R.string.tab1_text),
				mContext.getString(R.string.tab2_text),
				mContext.getString(R.string.tab3_text),
				mContext.getString(R.string.tab4_text) }[position];
	}

	@Override
	public int getCount() {
		return list.size();
	}
}