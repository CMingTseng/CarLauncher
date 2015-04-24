package com.tchip.carlauncher.bluetooth;

import java.io.IOException;
import java.util.UUID;

import com.tchip.carlauncher.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class ClientSocketActivity extends Activity {
	private static final String TAG = ClientSocketActivity.class
			.getSimpleName();
	private static final int REQUEST_DISCOVERY = 0x1;;
	private Handler _handler = new Handler();
	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setContentView(R.layout.activity_bluetooth_client);
		if (!_bluetooth.isEnabled()) {
			finish();
			return;
		}
		Intent intent = new Intent(this, DiscoveryActivity.class);
		Toast.makeText(this, "select device to connect", Toast.LENGTH_SHORT)
				.show();
		startActivityForResult(intent, REQUEST_DISCOVERY);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != REQUEST_DISCOVERY) {
			return;
		}
		if (resultCode != RESULT_OK) {
			return;
		}
		final BluetoothDevice device = data
				.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		Toast.makeText(
				getApplicationContext(),
				"device type:"
						+ device.getBluetoothClass().getMajorDeviceClass(),
				Toast.LENGTH_SHORT).show();
		new Thread() {
			public void run() {
				connect(device);
			};
		}.start();
	}

	protected void connect(BluetoothDevice device) {
		BluetoothSocket socket = null;
		try {
			// socket =
			// device.createRfcommSocketToServiceRecord(BluetoothProtocols.OBEX_OBJECT_PUSH_PROTOCOL_UUID);
			socket = device.createRfcommSocketToServiceRecord(UUID
					.fromString("a60f35f0-b93a-11de-8a39-08002009c666"));
			socket.connect();
		} catch (IOException e) {
			Log.e(TAG, "", e);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					Log.e(TAG, "", e);
				}
			}
		}
	}
}
