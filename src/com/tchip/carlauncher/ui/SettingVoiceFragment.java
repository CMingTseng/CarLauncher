package com.tchip.carlauncher.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.tchip.carlauncher.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SettingVoiceFragment extends Fragment {
	private ListView routeList;
	private ArrayAdapter<String> adapter;
	private int focusItemPos = 0;
	private final String ROUTE_PATH = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View routeListView = inflater.inflate(R.layout.fragment_setting_voice, container,
				false);
//		routeList = (ListView) routeListView.findViewById(R.id.routeList);
//
//		TextView tvNoFile = (TextView) routeListView
//				.findViewById(R.id.tvNoFile);
//		tvNoFile.setVisibility(View.GONE);

		return routeListView;
	}
	

}
