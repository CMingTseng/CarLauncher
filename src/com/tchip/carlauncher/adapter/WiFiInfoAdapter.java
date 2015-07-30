package com.tchip.carlauncher.adapter;

import java.util.ArrayList;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.model.WifiInfo;
import com.tchip.carlauncher.util.SignalUtil;

import android.content.Context;
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
	private String nowWifiBssid;

	public WiFiInfoAdapter(Context context, ArrayList<WifiInfo> wifiArray) {
		super();
		this.context = context;
		this.wifiArray = wifiArray;
		layoutInflater = LayoutInflater.from(context);
		wifiArray = new ArrayList<WifiInfo>();
		nowWifiBssid = SignalUtil.getConnectWifiBssid(context);
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
		TextView textSafety;
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
			viewHolder.textSafety = (TextView) convertView
					.findViewById(R.id.textSafety);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (MyViewHolder) convertView.getTag();
		}

		// WiFi Signal Level
		int level = wifiArray.get(position).getSignal();
		viewHolder.imageSignal.setImageResource(SignalUtil
				.getWifiImageBySignal(level));

		String wifiName = wifiArray.get(position).getName();
		viewHolder.textName.setText(wifiName);

		// Wifi State & AuthType
		String wifiBssid = wifiArray.get(position).getMacAddress();
		String wifiAuth = wifiArray.get(position).getAuthType();

		if (wifiBssid != null && wifiBssid.trim().length() > 0
				&& wifiBssid.equals(nowWifiBssid)) {
			viewHolder.textState.setText("已连接");
		} else {
			viewHolder.textState.setText("");
		}

		if (wifiAuth != null && wifiAuth.trim().length() > 0) {
			if ("[ESS]".equals(wifiAuth) || "[WPS][ESS]".equals(wifiAuth)) {
				viewHolder.textSafety.setText("开放");
			} else {
				viewHolder.textSafety.setText("安全");
			}
		} else {
			viewHolder.textSafety.setText("");
		}

		// 剔除 'NVRAM WARNING:Err=0x10' 的情况
		if (wifiName == "NVRAM WARNING:Err=0x10"
				|| wifiBssid.equals("00:00:00:00:00:00")) {
			wifiArray.remove(position);
			notifyDataSetChanged();
		}

		// 剔除只有BSSID最后两位不同的同名WiFi
		for (int i = 0; i < wifiArray.size(); i++) {
			WifiInfo wifiInfo = wifiArray.get(i);
			if ( wifiBssid.substring(0, 13) == wifiInfo.getMacAddress()
							.substring(0, 13)) {
				wifiArray.remove(position);
			}
		}

		return convertView;
	}

	public void remove(Object item) {
		wifiArray.remove(item);
		notifyDataSetChanged();
	}

}
