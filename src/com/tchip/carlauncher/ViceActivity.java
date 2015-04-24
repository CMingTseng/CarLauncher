package com.tchip.carlauncher;

import com.tchip.carlauncher.view.ButtonFloat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class ViceActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_vice);

		ButtonFloat btnToMainFromVice = (ButtonFloat) findViewById(R.id.btnToMainFromVice);
		btnToMainFromVice.setDrawableIcon(getResources().getDrawable(
				R.drawable.icon_arrow_left));
		btnToMainFromVice.setOnClickListener(new MyOnClickListener());

		ImageView imgNear = (ImageView) findViewById(R.id.imgNear);
		imgNear.setOnClickListener(new MyOnClickListener());

		ImageView imgRoutePlan = (ImageView) findViewById(R.id.imgRoutePlan);
		imgRoutePlan.setOnClickListener(new MyOnClickListener());

		ImageView imgAbout = (ImageView) findViewById(R.id.imgAbout);
		imgAbout.setOnClickListener(new MyOnClickListener());

	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			int version = Integer.valueOf(android.os.Build.VERSION.SDK);
			switch (v.getId()) {
			case R.id.btnToMainFromVice:
				backToMain();
				break;
			case R.id.imgNear:
				Intent intent1 = new Intent(ViceActivity.this,
						NearActivity.class);
				startActivity(intent1);
				// add for animation start

				if (version > 5) {
					overridePendingTransition(R.anim.zms_translate_down_out,
							R.anim.zms_translate_down_in);
				}
				// add for animation end
				break;
			case R.id.imgRoutePlan:
				Intent intent2 = new Intent(ViceActivity.this,
						RoutePlanActivity.class);
				startActivity(intent2);
				// add for animation start

				if (version > 5) {
					overridePendingTransition(R.anim.zms_translate_down_out,
							R.anim.zms_translate_down_in);
				}
				// add for animation end
				break;
			case R.id.imgAbout:
				Intent intent3 = new Intent(ViceActivity.this,
						AboutActivity.class);
				startActivity(intent3);

				if (version > 5) {
					overridePendingTransition(R.anim.zms_translate_up_out,
							R.anim.zms_translate_up_in);
				}
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
	}

}
