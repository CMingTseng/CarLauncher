package com.tchip.carlauncher.ui.activity;

import com.tchip.carlauncher.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class UserCenterActivity extends Activity {

	private RelativeLayout layoutSignContent, layoutLoginContent,
			layoutNoLoginContent;

	public enum UserState {
		LOGIN, LOGOUT
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_user_center);

		initialLayout();
		initalPannel(getUserState());
	}

	private void initialLayout() {
		RelativeLayout layoutBack = (RelativeLayout) findViewById(R.id.layoutBack);
		layoutBack.setOnClickListener(new MyOnClickListener());

		RelativeLayout layoutSign = (RelativeLayout) findViewById(R.id.layoutSign);
		layoutSign.setOnClickListener(new MyOnClickListener());
		layoutSignContent = (RelativeLayout) findViewById(R.id.layoutSignContent);

		RelativeLayout layoutLogin = (RelativeLayout) findViewById(R.id.layoutLogin);
		layoutLogin.setOnClickListener(new MyOnClickListener());
		layoutLoginContent = (RelativeLayout) findViewById(R.id.layoutLoginContent);

		RelativeLayout layoutLogout = (RelativeLayout) findViewById(R.id.layoutLogout);
		layoutLogout.setOnClickListener(new MyOnClickListener());
		layoutNoLoginContent = (RelativeLayout) findViewById(R.id.layoutNoLoginContent);
	}

	private UserState getUserState() {
		// TODO:
		return UserState.LOGOUT;
	}

	private void initalPannel(UserState state) {
		switch (state) {
		case LOGIN:

			break;
		case LOGOUT:
			break;

		default:
			break;
		}
	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layoutBack:
				finish();
				break;

			case R.id.layoutSign:
				break;

			case R.id.layoutLogin:
				break;

			case R.id.layoutLogout:
				break;

			default:
				break;
			}
		}
	}

}
