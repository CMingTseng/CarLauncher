package com.tchip.carlauncher.ui.activity;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class MagicActivity extends Activity {
	private EditText textPass;
	private Button btnGo, btnBack;
	private RelativeLayout layoutMagic, layoutBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setBackgroundDrawable(null);
		setContentView(R.layout.activity_magic);

		initialLayout();
	}

	private void initialLayout() {
		layoutMagic = (RelativeLayout) findViewById(R.id.layoutMagic);
		layoutBack = (RelativeLayout) findViewById(R.id.layoutBack);
		layoutBack.setOnClickListener(new MyOnClickListener());
		btnBack = (Button)findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new MyOnClickListener());
		
		textPass = (EditText) findViewById(R.id.textPass);
		btnGo = (Button) findViewById(R.id.btnGo);
		btnGo.setOnClickListener(new MyOnClickListener());

		Button btnDeviceTest = (Button) findViewById(R.id.btnDeviceTest);
		btnDeviceTest.setOnClickListener(new MyOnClickListener());

		Button btnEngineerMode = (Button) findViewById(R.id.btnEngineerMode);
		btnEngineerMode.setOnClickListener(new MyOnClickListener());

		Button btnSystemSetting = (Button) findViewById(R.id.btnSystemSetting);
		btnSystemSetting.setOnClickListener(new MyOnClickListener());

	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnGo:
				String strInput = textPass.getText().toString();
				if (Constant.MagicCode.PASSWORD.equals(strInput)) {
					layoutMagic.setVisibility(View.VISIBLE);
				} else {
					textPass.setText("");
				}
				break;

			case R.id.btnDeviceTest:
				Intent intentDeviceTest = new Intent(Intent.ACTION_VIEW);
				intentDeviceTest.setClassName("com.DeviceTest",
						"com.DeviceTest.DeviceTest");
				intentDeviceTest.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intentDeviceTest);
				break;

			case R.id.btnEngineerMode:
				Intent intentEngineerMode = new Intent(Intent.ACTION_VIEW);
				intentEngineerMode.setClassName("com.mediatek.engineermode",
						"com.mediatek.engineermode.EngineerMode");
				intentEngineerMode.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intentEngineerMode);
				break;

			case R.id.btnSystemSetting:
				ComponentName componentSetting = new ComponentName(
						"com.android.settings", "com.android.settings.Settings");
				Intent intentSetting = new Intent();
				intentSetting.setComponent(componentSetting);
				startActivity(intentSetting);
				break;

			case R.id.layoutBack:
			case R.id.btnBack:
				finish();
				break;

			default:
				break;
			}

		}

	}

}
