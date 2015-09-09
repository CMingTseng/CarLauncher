package com.tchip.carlauncher.ui.activity;

import com.tchip.carlauncher.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class UserCenterActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_user_center);

		initialLayout();
	}

	private void initialLayout() {
		RelativeLayout layoutSign = (RelativeLayout) findViewById(R.id.layoutSign);
		layoutSign.setOnClickListener(new MyOnClickListener());
		
		RelativeLayout layoutLogin = (RelativeLayout) findViewById(R.id.layoutLogin);
		layoutLogin.setOnClickListener(new MyOnClickListener());
	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layoutSign:
				break;
				
			case R.id.layoutLogin:
				break;

			default:
				break;
			}
		}
	}

}
