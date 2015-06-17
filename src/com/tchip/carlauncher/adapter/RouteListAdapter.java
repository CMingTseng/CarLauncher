package com.tchip.carlauncher.adapter;

import java.util.ArrayList;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.model.RouteList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RouteListAdapter extends BaseAdapter {
	private ArrayList<RouteList> routeArray;
	private LayoutInflater layoutInflater;
	private Context context;

	public RouteListAdapter(Context context, ArrayList<RouteList> routeArray) {
		super();
		this.context = context;
		this.routeArray = routeArray;
		layoutInflater = LayoutInflater.from(context);
		routeArray = new ArrayList<RouteList>();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return routeArray.size();
	}

	@Override
	public Object getItem(int position) {
		return routeArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class MyViewHolder {
		TextView textStartTime;
		TextView textEndTime;
		LinearLayout layoutMain;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final MyViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new MyViewHolder();
			convertView = layoutInflater
					.inflate(R.layout.route_list_item, null);
			viewHolder.textStartTime = (TextView) convertView
					.findViewById(R.id.textStartTime);
			viewHolder.textEndTime = (TextView) convertView
					.findViewById(R.id.textEndTime);
			viewHolder.layoutMain = (LinearLayout) convertView
					.findViewById(R.id.layoutMain);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (MyViewHolder) convertView.getTag();
		}
		viewHolder.textStartTime.setText("开始："
				+ convertStringToDate(routeArray.get(position).getTimeStart()));
		viewHolder.textEndTime.setText("结束："
				+ convertStringToDate(routeArray.get(position).getTimeEnd()));
		if ((position % 2) == 0) {
			viewHolder.layoutMain
					.setBackgroundResource(R.drawable.ui_route_track_list_bg_light);
		} else {
			viewHolder.layoutMain
					.setBackgroundResource(R.drawable.ui_route_track_list_bg_dark);
		}

		return convertView;
	}

	private String convertStringToDate(String str) {
		// in: 20150401_135625
		// out: 2015年4月1日 13:56:25
		String date = "";
		date = str.substring(0, 4) + "年" + str.substring(4, 6) + "月"
				+ str.substring(6, 8) + "日 " + str.substring(9, 11) + ":"
				+ str.substring(11, 13) + ":" + str.substring(13, 15);

		return date;
	}

	public void remove(Object item) {
		routeArray.remove(item);
		notifyDataSetChanged();
	}

}
