package com.tchip.carlauncher.ui.activity;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.util.BTCommand;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class BluetoothDialerActivity extends Activity {
	private Button btnDial;
	private EditText textNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_bluetooth_dialer);

		textNumber = (EditText) findViewById(R.id.textNumber);
		btnDial = (Button) findViewById(R.id.btnDial);
		btnDial.setOnClickListener(new MyOnClickListener());
	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnDial:
				String strNumber = textNumber.getText().toString();
				if (strNumber != null && strNumber.trim().length() > 0) {
					BTCommand.SendCommand(BTCommand.CM_DIALER + strNumber);
				} else {
					// 号码为空
				}
				break;

			default:
				break;
			}
		}
	}

}
