package com.tchip.carlauncher.ui.activity;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class UserCenterActivity extends Activity {

	private RelativeLayout layoutContentSign, layoutContentLogin,
			layoutContentDefault, layoutContentMessage, layoutSign,
			layoutLogin, layoutLogout;
	private SharedPreferences preferences;
	private Editor editor;

	private boolean isUserLogin = false;

	public static enum PannelState {
		DEFAULT, SIGN, LOGIN, MESSAGE
	}

	private PannelState pannelState = PannelState.DEFAULT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_user_center);

		preferences = getSharedPreferences(Constant.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		editor = preferences.edit();

		initialLayout();
		PannelState state = initialPannelState(isUserLogin());
		initialPannelLayout(state);

	}

	private void initialLayout() {
		RelativeLayout layoutBack = (RelativeLayout) findViewById(R.id.layoutBack);
		layoutBack.setOnClickListener(new MyOnClickListener());

		layoutSign = (RelativeLayout) findViewById(R.id.layoutSign);
		layoutSign.setOnClickListener(new MyOnClickListener());

		layoutLogin = (RelativeLayout) findViewById(R.id.layoutLogin);
		layoutLogin.setOnClickListener(new MyOnClickListener());

		layoutLogout = (RelativeLayout) findViewById(R.id.layoutLogout);
		layoutLogout.setOnClickListener(new MyOnClickListener());

		layoutContentDefault = (RelativeLayout) findViewById(R.id.layoutContentDefault);
		layoutContentSign = (RelativeLayout) findViewById(R.id.layoutContentSign);
		layoutContentLogin = (RelativeLayout) findViewById(R.id.layoutContentLogin);
		layoutContentMessage = (RelativeLayout) findViewById(R.id.layoutContentMessage);
	}

	private boolean isUserLogin() {
		isUserLogin = preferences.getBoolean("isUserLogin", false);
		return isUserLogin;
	}

	private PannelState initialPannelState(boolean isUserLogin) {
		if (isUserLogin) {
			return PannelState.MESSAGE;
		} else {
			return PannelState.DEFAULT;
		}
	}

	private void initialPannelLayout(PannelState state) {

		switch (state) {
		case SIGN: // 注册界面
			layoutLogout.setVisibility(View.GONE);

			layoutContentDefault.setVisibility(View.GONE);
			layoutContentSign.setVisibility(View.VISIBLE);
			layoutContentLogin.setVisibility(View.GONE);
			layoutContentMessage.setVisibility(View.GONE);
			break;

		case LOGIN: // 登录界面
			layoutLogout.setVisibility(View.GONE);

			layoutContentDefault.setVisibility(View.GONE);
			layoutContentSign.setVisibility(View.GONE);
			layoutContentLogin.setVisibility(View.VISIBLE);
			layoutContentMessage.setVisibility(View.GONE);
			break;

		case MESSAGE: // 登录成功后消息界面
			layoutLogout.setVisibility(View.VISIBLE);

			layoutContentDefault.setVisibility(View.GONE);
			layoutContentSign.setVisibility(View.GONE);
			layoutContentLogin.setVisibility(View.GONE);
			layoutContentMessage.setVisibility(View.VISIBLE);
			break;

		default: // 默认界面，提示
			layoutLogout.setVisibility(View.GONE);

			layoutContentDefault.setVisibility(View.VISIBLE);
			layoutContentSign.setVisibility(View.GONE);
			layoutContentLogin.setVisibility(View.GONE);
			layoutContentMessage.setVisibility(View.GONE);
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
				initialPannelLayout(PannelState.SIGN);
				break;

			case R.id.layoutLogin:
				initialPannelLayout(PannelState.LOGIN);
				break;

			case R.id.layoutLogout:
				userLogout();
				break;

			default:
				initialPannelLayout(PannelState.DEFAULT);
				break;
			}
		}
	}

	private void userLogout() {
		// TODO:对话框确认
		editor.putBoolean("isUserLogin", false);
		editor.putString("userName", "");
		editor.putString("UserPass", "");
		editor.commit();

		initialPannelLayout(PannelState.DEFAULT);
	}

}
