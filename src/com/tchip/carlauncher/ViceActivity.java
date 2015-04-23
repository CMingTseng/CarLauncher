package com.tchip.carlauncher;

import com.tchip.carlauncher.NearActivity.MyOnClickListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ViceActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		setContentView(R.layout.activity_vice);

		ImageView imgBackFromViceMetro = (ImageView) findViewById(R.id.imgBackFromViceMetro);
		imgBackFromViceMetro.setOnClickListener(new MyOnClickListener());

		ImageView imgBackFromViceArrow = (ImageView) findViewById(R.id.imgBackFromViceArrow);
		imgBackFromViceArrow.setOnClickListener(new MyOnClickListener());
	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.imgBackFromViceMetro:
			case R.id.imgBackFromViceArrow:
				backToMain();
				break;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backToMain();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	private void backToMain() {
		finish();
		// add for animation start
		int version = Integer.valueOf(android.os.Build.VERSION.SDK);
		if (version > 5) {
			overridePendingTransition(R.anim.zms_translate_right_out,
					R.anim.zms_translate_right_in);
		}
		// add for animation end
	}

}
