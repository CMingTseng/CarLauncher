package com.tchip.carlauncher.adapter;

import java.util.ArrayList;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.model.NaviResultInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NaviResultAdapter extends BaseAdapter {
	private ArrayList<NaviResultInfo> naviArray;

	private LayoutInflater layoutInflater;

	public NaviResultAdapter(Context context,
			ArrayList<NaviResultInfo> naviArray) {
		super();
		this.naviArray = naviArray;
		layoutInflater = LayoutInflater.from(context);
		naviArray = new ArrayList<NaviResultInfo>();
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
		TextView textName;
		TextView textDetail;
		TextView textDistance;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final MyViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new MyViewHolder();
			convertView = layoutInflater.inflate(
					R.layout.navi_result_list_item, null);

			viewHolder.textName = (TextView) convertView
					.findViewById(R.id.textName);
			viewHolder.textDetail = (TextView) convertView
					.findViewById(R.id.textDetail);
			viewHolder.textDistance = (TextView) convertView
					.findViewById(R.id.textDistance);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (MyViewHolder) convertView.getTag();
		}

		// Name
		String name = naviArray.get(position).getName();
		viewHolder.textName.setText(name);

		// Address
		String address = naviArray.get(position).getAddress();
		viewHolder.textDetail.setText(address);

		// Distance
		int distance = (int) naviArray.get(position).getDistance();
		if (distance < 1000) {
			viewHolder.textDistance.setText("距离：" + distance + "m");
		} else {
			viewHolder.textDistance.setText("距离：" + distance / 1000 + "km");
		}

		return convertView;
	}

	public void remove(Object item) {
		naviArray.remove(item);
		notifyDataSetChanged();
	}

}
