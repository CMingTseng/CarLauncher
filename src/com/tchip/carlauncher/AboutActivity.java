package com.tchip.carlauncher;

import com.tchip.carlauncher.view.ButtonFloat;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

public class AboutActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_about);

		ButtonFloat btnToViceFromAbout = (ButtonFloat) findViewById(R.id.btnToViceFromAbout);
		btnToViceFromAbout.setDrawableIcon(getResources().getDrawable(
				R.drawable.icon_arrow_up));
		btnToViceFromAbout.setOnClickListener(new MyOnClickListener());
	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.btnToViceFromAbout:
				backToVice();
				break;
			}
		}
	}

	private void backToVice() {
		finish();
		int version = Integer.valueOf(android.os.Build.VERSION.SDK);
		if (version > 5) {
			overridePendingTransition(R.anim.zms_translate_down_out,
					R.anim.zms_translate_down_in);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backToVice();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
	}

}
