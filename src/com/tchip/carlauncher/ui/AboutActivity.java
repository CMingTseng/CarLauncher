package com.tchip.carlauncher.ui;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.R.anim;
import com.tchip.carlauncher.R.drawable;
import com.tchip.carlauncher.R.id;
import com.tchip.carlauncher.R.layout;
import com.tchip.carlauncher.bean.Titanic;
import com.tchip.carlauncher.bean.Typefaces;
import com.tchip.carlauncher.service.SpeakService;
import com.tchip.carlauncher.view.ButtonFloat;
import com.tchip.carlauncher.view.TitanicTextView;

import android.app.Activity;
import android.content.Intent;
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

		TitanicTextView titanicTextView = (TitanicTextView) findViewById(R.id.titanicTextView);
		titanicTextView.setOnClickListener(new MyOnClickListener());

		// set fancy typeface
		titanicTextView.setTypeface(Typefaces.get(this, "Satisfy-Regular.ttf"));

		// start animation
		new Titanic().start(titanicTextView);

	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.btnToViceFromAbout:
				backToVice();
				break;
			case R.id.titanicTextView:
				startSpeak("今天天气不错,去游泳吧");
				break;
			}
		}
	}

	private void startSpeak(String content) {
		Intent intent = new Intent(this, SpeakService.class);
		intent.putExtra("content", content);
		startService(intent);
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
