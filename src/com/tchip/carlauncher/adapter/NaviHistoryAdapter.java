package com.tchip.carlauncher.adapter;

import java.util.ArrayList;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.model.NaviHistory;
import com.tchip.carlauncher.model.WifiInfo;
import com.tchip.carlauncher.util.WiFiUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NaviHistoryAdapter extends BaseAdapter {
	private ArrayList<NaviHistory> naviArray;

	private LayoutInflater layoutInflater;
	private Context context;

	public NaviHistoryAdapter(Context context, ArrayList<NaviHistory> naviArray) {
		super();
		this.context = context;
		this.naviArray = naviArray;
		layoutInflater = LayoutInflater.from(context);
		naviArray = new ArrayList<NaviHistory>();
	}

	@Override
	public int getCount() {
		return naviArray.size();
	}

	@Override
	public Object getItem(int position) {
		return naviArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class MyViewHolder {
		ImageView imageSignal;
		TextView textKey;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final MyViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new MyViewHolder();
			convertView = layoutInflater.inflate(
					R.layout.navi_history_list_item, null);

			viewHolder.imageSignal = (ImageView) convertView
					.findViewById(R.id.imageSignal);
			viewHolder.textKey = (TextView) convertView
					.findViewById(R.id.textKey);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (MyViewHolder) convertView.getTag();
		}
		
		// Key
		String naviKey = naviArray.get(position).getKey();
		viewHolder.textKey.setText(naviKey);

		return convertView;
	}

	public void remove(Object item) {
		naviArray.remove(item);
		notifyDataSetChanged();
	}

}
