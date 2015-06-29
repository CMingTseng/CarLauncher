package com.tchip.carlauncher.ui.activity;



import com.tchip.carlauncher.R;
import com.tchip.carlauncher.ui.fragment.KeepSaveFragment;
import com.tchip.carlauncher.ui.fragment.NormalFragment;
import com.tchip.carlauncher.ui.fragment.PhotoFragment;
import com.tchip.carlauncher.ui.fragment.UrgentFragment;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;


public class VideoListActivity extends  Activity implements TabListener{
	
	private NormalFragment normalFragment;
	private UrgentFragment urgentFragment;
	private KeepSaveFragment keepSaveFragment;
	private PhotoFragment photoFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		normalFragment = new NormalFragment();
		urgentFragment = new UrgentFragment();
		keepSaveFragment = new KeepSaveFragment();
		photoFragment = new PhotoFragment();
		
		
		ActionBar  actionBar = getActionBar();
		actionBar.addTab(actionBar.newTab().setText("  正常视频  ").setTabListener(this).setTag(1));
		actionBar.addTab(actionBar.newTab().setText("  紧急视频  ").setTabListener(this).setTag(2));
		actionBar.addTab(actionBar.newTab().setText("  存档视频  ").setTabListener(this).setTag(3));
		actionBar.addTab(actionBar.newTab().setText("    照     片    ").setTabListener(this).setTag(4));
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setHomeButtonEnabled(true);
		
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(!normalFragment.fragmentOnOptionsItemSelected())
		{
			VideoListActivity.this.finish();
		}
		return true;
	}
	

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		switch ((Integer) tab.getTag()) {
		case 1:
			ft.add(R.id.context, normalFragment, null);
			break;
		case 2:
			ft.add(R.id.context, urgentFragment, null);
			break;
		case 3:
			ft.add(R.id.context, keepSaveFragment, null);
			break;
		case 4:
			ft.add(R.id.context, photoFragment, null);
			break;
		}
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		switch ((Integer) tab.getTag()) {
		case 1:
			ft.remove(normalFragment);
			break;
		case 2:
			ft.remove(urgentFragment);
			break;
		case 3:
			ft.remove(keepSaveFragment);
			break;
		case 4:
			ft.remove(photoFragment);
			break;

		default:
			break;
		}
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	
	
}
