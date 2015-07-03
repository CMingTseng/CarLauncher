package com.tchip.carlauncher.adapter;

import java.util.ArrayList;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.model.WifiInfo;
import com.tchip.carlauncher.util.WiFiUtil;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WiFiInfoAdapter extends BaseAdapter {
	private ArrayList<WifiInfo> wifiArray;

	private LayoutInflater layoutInflater;
	private Context context;

	public WiFiInfoAdapter(Context context, ArrayList<WifiInfo> wifiArray) {
		super();
		this.context = context;
		this.wifiArray = wifiArray;
		layoutInflater = LayoutInflater.from(context);
		wifiArray = new ArrayList<WifiInfo>();
	}

	@Override
	public int getCount() {
		return wifiArray.size();
	}

	@Override
	public Object getItem(int position) {
		return wifiArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class MyViewHolder {
		ImageView imageSignal;
		TextView textName;
		TextView textState;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final MyViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new MyViewHolder();
			convertView = layoutInflater.inflate(R.layout.wifi_list_item, null);
			viewHolder.imageSignal = (ImageView) convertView
					.findViewById(R.id.imageSignal);
			viewHolder.textName = (TextView) convertView
					.findViewById(R.id.textName);
			viewHolder.textState = (TextView) convertView
					.findViewById(R.id.textState);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (MyViewHolder) convertView.getTag();
		}
		// TODO:Signal Level
		int level = wifiArray.get(position).getSignal();
		viewHolder.imageSignal.setImageResource(WiFiUtil.getImageBySignal(level));
		
		String wifiName = wifiArray.get(position).getName();
		viewHolder.textName.setText(wifiName);
		
		// TODO:State
		//if(wifiName!= null && wifiName.equals()){}
		//viewHolder.textState.setText(wifiArray.get(position).getAuthType());
		

		return convertView;
	}


	public void remove(Object item) {
		wifiArray.remove(item);
		notifyDataSetChanged();
	}

}
