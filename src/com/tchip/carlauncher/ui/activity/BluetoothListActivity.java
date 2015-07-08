package com.tchip.carlauncher.ui.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.adapter.BluetoothInfoAdapter;
import com.tchip.carlauncher.model.BluetoothInfo;
import com.tchip.carlauncher.ui.activity.WifiListActivity.refreshWifiThread;
import com.tchip.carlauncher.view.SwitchButton;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class BluetoothListActivity extends Activity {

	private BluetoothInfoAdapter adapter;
	private ListView listBluetooth;
	private ArrayList<BluetoothInfo> bluetoothArray;

	private ProgressBar updateProgress;
	private Button updateButton;

	private Handler _handler = new Handler();
	/* 取得默认的蓝牙适配器 */
	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();
	/* 用来存储搜索到的蓝牙设备 */
	private List<BluetoothDevice> _devices = new ArrayList<BluetoothDevice>();
	/* 是否完成搜索 */
	private volatile boolean _discoveryFinished;
	private Runnable _discoveryWorkder = new Runnable() {
		public void run() {
			/* 开始搜索 */
			_bluetooth.startDiscovery();
			for (;;) {
				if (_discoveryFinished) {
					break;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
		}
	};

	/**
	 * 接收器 当搜索蓝牙设备完成时调用
	 */
	private BroadcastReceiver _foundReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			/* 从intent中取得搜索结果数据 */
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			/* 将结果添加到列表中 */
			_devices.add(device);
			/* 显示列表 */
			showDevices();
		}
	};
	private BroadcastReceiver _discoveryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			/* 卸载注册的接收器 */
			unregisterReceiver(_foundReceiver);
			unregisterReceiver(this);
			_discoveryFinished = true;
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
		// WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_bluetooth_list);

		// 蓝牙列表
		listBluetooth = (ListView) findViewById(R.id.listBluetooth);

		SwitchButton switchBluetoothList = (SwitchButton) findViewById(R.id.switchBluetoothList);
		switchBluetoothList.setChecked(_bluetooth.isEnabled());
		switchBluetoothList
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked)
							_bluetooth.enable();
						else
							_bluetooth.disable();
					}
				});

		// 刷新按钮和进度条
		updateProgress = (ProgressBar) findViewById(R.id.updateProgress);
		updateProgress.setVisibility(View.INVISIBLE);
		updateButton = (Button) findViewById(R.id.updateButton);
		updateButton.setVisibility(View.VISIBLE);
		updateButton.setOnClickListener(new MyOnClickListener());

		RelativeLayout btnToSettingFromBluetooth = (RelativeLayout) findViewById(R.id.btnToSettingFromBluetooth);
		btnToSettingFromBluetooth.setOnClickListener(new MyOnClickListener());

		if (!_bluetooth.isEnabled()) {
			// TODO:提示打开蓝牙
		} else {
			/* 注册接收器 */
			IntentFilter discoveryFilter = new IntentFilter(
					BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			registerReceiver(_discoveryReceiver, discoveryFilter);
			IntentFilter foundFilter = new IntentFilter(
					BluetoothDevice.ACTION_FOUND);
			registerReceiver(_foundReceiver, foundFilter);

			// 正在搜索蓝牙设备
			// updateProgress.setVisibility(View.VISIBLE);
			// updateButton.setVisibility(View.INVISIBLE);
			// _discoveryWorkder.run();
			indeterminate(BluetoothListActivity.this, _handler,
					"正在扫描蓝牙设备，请稍候...", _discoveryWorkder,
					new OnDismissListener() {
						public void onDismiss(DialogInterface dialog) {

							for (; _bluetooth.isDiscovering();) {

								_bluetooth.cancelDiscovery();
							}

							_discoveryFinished = true;
						}
					}, true);
		}
	}

	private class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnToSettingFromBluetooth:
				finish();
				break;
			case R.id.updateButton:
				updateButton.setVisibility(View.INVISIBLE);
				updateProgress.setVisibility(View.VISIBLE);
				new Thread(new refreshBluetoothThread()).start();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 连接蓝牙设备
	 * 
	 * @param device
	 * @throws IOException
	 */
	private void connect(BluetoothDevice device) throws IOException {
		// 固定的UUID
		final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
		UUID uuid = UUID.fromString(SPP_UUID);
		BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
		socket.connect();
	}

	final Handler refreshBluetoothHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				showDevices();
				updateProgress.setVisibility(View.INVISIBLE);
				updateButton.setVisibility(View.VISIBLE);
				break;

			default:
				break;
			}
		}
	};

	public class refreshBluetoothThread implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(10000);
				Message message = new Message();
				message.what = 1;
				refreshBluetoothHandler.sendMessage(message);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 显示列表
	 */
	protected void showDevices() {
		// 已配对蓝牙
		// BluetoothAdapter bluetoothAdapter =
		// BluetoothAdapter.getDefaultAdapter();
		// Set<BluetoothDevice> pairDevices =
		// bluetoothAdapter.getBondedDevices();
		// for(int i=0; i<pairDevices.size(); i++)
		// {
		// BluetoothDevice device = (BluetoothDevice)
		// pairDevices.iterator().next();
		// System.out.println(device.getName());
		// }

		// 周围设备
		bluetoothArray = new ArrayList<BluetoothInfo>();

		for (int i = 0, size = _devices.size(); i < size; ++i) {
			BluetoothDevice d = _devices.get(i);
			BluetoothInfo bluetoothInfo = new BluetoothInfo(d.getAddress(),
					d.getName(), d.getBondState());
			bluetoothArray.add(bluetoothInfo);
		}

		adapter = new BluetoothInfoAdapter(getApplicationContext(),
				bluetoothArray);
		listBluetooth.setAdapter(adapter);

	}

	protected void onListItemClick(ListView l, View v, int position, long id) {

		Intent result = new Intent();
		result.putExtra(BluetoothDevice.EXTRA_DEVICE, _devices.get(position));
		setResult(RESULT_OK, result);
		finish();
	}

	/**
	 * ----------------------------------------------
	 * 
	 * below for dialog
	 * 
	 * ----------------------------------------------
	 */
	public static void indeterminate(Context context, Handler handler,
			String message, final Runnable runnable,
			OnDismissListener dismissListener, boolean cancelable) {
		try {

			indeterminateInternal(context, handler, message, runnable,
					dismissListener, cancelable);
		} catch (Exception e) {
			; // nop.
		}
	}

	private static ProgressDialog createProgressDialog(Context context,
			String message) {

		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setIndeterminate(false);
		dialog.setMessage(message);

		return dialog;
	}

	private static void indeterminateInternal(Context context,
			final Handler handler, String message, final Runnable runnable,
			OnDismissListener dismissListener, boolean cancelable) {

		final ProgressDialog dialog = createProgressDialog(context, message);
		dialog.setCancelable(cancelable);

		if (dismissListener != null) {
			dialog.setOnDismissListener(dismissListener);
		}

		dialog.show();

		new Thread() {

			@Override
			public void run() {
				runnable.run();
				handler.post(new Runnable() {
					public void run() {
						try {
							dialog.dismiss();
						} catch (Exception e) {
							; // nop.
						}
					}
				});
			};
		}.start();
	}
}
