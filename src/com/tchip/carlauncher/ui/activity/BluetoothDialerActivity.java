package com.tchip.carlauncher.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class BluetoothDialerActivity extends Activity {
	private File mBt = new File("/dev/goc_serial");
	private Button btnDial;
	private EditText textNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth_dialer);

		btnDial = (Button) findViewById(R.id.btnDial);
		textNumber = (EditText) findViewById(R.id.textNumber);

		btnDial.setOnClickListener(new MyOnClickListener());
	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnDial:
				String EditTextStr = textNumber.getText().toString();
				String SetStr = "AT#CW" + EditTextStr + "\r\n";
				SetBt(mBt, SetStr);
				// SetBt(mBt, "AT#CW10086\r\n");
				break;

			default:
				break;
			}
		}
	}

	protected void SetBt(File file, String value) {
		if (file.exists()) {
			try {
				StringBuffer strbuf = new StringBuffer("");
				strbuf.append(value);
				Log.d(Constant.TAG, "11111111::::::	" + strbuf);

				OutputStream output = null;
				OutputStreamWriter outputWrite = null;
				PrintWriter print = null;

				try {

					output = new FileOutputStream(file);
					outputWrite = new OutputStreamWriter(output);

					print = new PrintWriter(outputWrite);
					print.print(strbuf.toString());
					print.flush();
					output.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					Log.e(Constant.TAG, "output error");
				}
			} catch (IOException e) {
				Log.e(Constant.TAG, "IO Exception");
			}
		} else {
			Log.e(Constant.TAG, "File:" + file + "not exists");
		}
	}

}
