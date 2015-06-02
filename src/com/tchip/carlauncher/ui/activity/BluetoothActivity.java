package com.tchip.carlauncher.ui.activity;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.bluetooth.ClientSocketActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

public class BluetoothActivity extends Activity {

	private BluetoothAdapter bluetoothAdapter;
	// 请求打开蓝牙
	private static final int REQUEST_ENABLE = 0x1;
	// 请求能够被搜索
	private static final int REQUEST_DISCOVERABLE = 0x2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

//		Intent intent = new Intent(BluetoothActivity.this, CallService.class);
//		startService(intent);
		
		Intent intent  =  new Intent(BluetoothActivity.this, ClientSocketActivity.class);
		startActivity(intent);
	}

	/**
	 * 开启蓝牙
	 */
	private void enableBluetooth() {
		bluetoothAdapter.enable();
	}

	/**
	 * 关闭蓝牙
	 */
	private void disableBluetooth() {
		bluetoothAdapter.disable();
	}

	/**
	 * 使设备能够搜索
	 */
	public void discoverableBluetooth() {

		Intent enabler = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		startActivityForResult(enabler, REQUEST_DISCOVERABLE);
	}

}
