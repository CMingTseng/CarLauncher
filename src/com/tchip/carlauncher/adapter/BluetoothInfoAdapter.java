package com.tchip.carlauncher.adapter;

import java.util.ArrayList;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.model.BluetoothInfo;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BluetoothInfoAdapter extends BaseAdapter {
	private ArrayList<BluetoothInfo> bluetoothArray;

	private LayoutInflater layoutInflater;

	// private String nowWifiBssid;

	public BluetoothInfoAdapter(Context context,
			ArrayList<BluetoothInfo> bluetoothArray) {
		super();
		this.bluetoothArray = bluetoothArray;
		layoutInflater = LayoutInflater.from(context);
		bluetoothArray = new ArrayList<BluetoothInfo>();
		// nowWifiBssid = WiFiUtil.getConnectWifiBssid(context);
	}

	@Override
	public int getCount() {
		return bluetoothArray.size();
	}

	@Override
	public Object getItem(int position) {
		return bluetoothArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class MyViewHolder {
		TextView textName;
		TextView textAddress;
		TextView textState;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final MyViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new MyViewHolder();
			convertView = layoutInflater.inflate(R.layout.bluetooth_list_item,
					null);

			viewHolder.textName = (TextView) convertView
					.findViewById(R.id.textName);
			viewHolder.textAddress = (TextView) convertView
					.findViewById(R.id.textAddress);
			viewHolder.textState = (TextView) convertView
					.findViewById(R.id.textState);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (MyViewHolder) convertView.getTag();
		}

		// Name
		String bluetoothName = bluetoothArray.get(position).getName();
		viewHolder.textName.setText(bluetoothName);

		// Address
		String bluetoothAddress = bluetoothArray.get(position).getAddress();
		viewHolder.textAddress.setText(bluetoothAddress);

		// State
		int bondState = bluetoothArray.get(position).getState();
		if (BluetoothDevice.BOND_BONDED == bondState) {
			viewHolder.textState.setText("已配对");
		} else if (BluetoothDevice.BOND_BONDING == bondState) {
			viewHolder.textState.setText("配对中");
		} else { // BOND_NONE
			viewHolder.textState.setText("");
		}

		return convertView;
	}

	public void remove(Object item) {
		bluetoothArray.remove(item);
		notifyDataSetChanged();
	}

}
