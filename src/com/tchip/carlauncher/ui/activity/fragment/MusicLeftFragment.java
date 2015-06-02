package com.tchip.carlauncher.ui.activity.fragment;

import com.tchip.carlauncher.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MusicLeftFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.music_viewpager_trans_layout, container,
				false);
	}

}
