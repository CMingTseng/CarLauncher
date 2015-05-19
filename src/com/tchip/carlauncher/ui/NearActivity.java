package com.tchip.carlauncher.ui;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.view.ButtonFloat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

public class NearActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_near);

		ButtonFloat btnToViceFromNear = (ButtonFloat) findViewById(R.id.btnToViceFromNear);
		btnToViceFromNear.setDrawableIcon(getResources().getDrawable(
				R.drawable.icon_arrow_down));
		btnToViceFromNear.setOnClickListener(new MyOnClickListener());

		ImageView imgNearOilStation = (ImageView) findViewById(R.id.imgNearOilStation);
		imgNearOilStation.setOnClickListener(new MyOnClickListener());

		ImageView imgNearHotel = (ImageView) findViewById(R.id.imgNearHotel);
		imgNearHotel.setOnClickListener(new MyOnClickListener());

		ImageView imgNear4S = (ImageView) findViewById(R.id.imgNear4S);
		imgNear4S.setOnClickListener(new MyOnClickListener());

		ImageView imgNearMarket = (ImageView) findViewById(R.id.imgNearMarket);
		imgNearMarket.setOnClickListener(new MyOnClickListener());

		ImageView imgNearBank = (ImageView) findViewById(R.id.imgNearBank);
		imgNearBank.setOnClickListener(new MyOnClickListener());

		ImageView imgNearHospital = (ImageView) findViewById(R.id.imgNearHospital);
		imgNearHospital.setOnClickListener(new MyOnClickListener());

		ImageView imgCustomSearch = (ImageView) findViewById(R.id.imgCustomSearch);
		imgCustomSearch.setOnClickListener(new MyOnClickListener());

	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnToViceFromNear:
				backToVice();
				break;
			case R.id.imgNearOilStation:
				Intent intent1 = new Intent(NearActivity.this,
						NearResultActivity.class);
				intent1.putExtra("findType", "加油站");
				startActivity(intent1);
				break;
			case R.id.imgNearHotel:
				Intent intent2 = new Intent(NearActivity.this,
						NearResultActivity.class);
				intent2.putExtra("findType", "酒店");
				startActivity(intent2);
				break;
			case R.id.imgNear4S:
				Intent intent3 = new Intent(NearActivity.this,
						NearResultActivity.class);
				intent3.putExtra("findType", "4S");
				startActivity(intent3);
				break;
			case R.id.imgNearMarket:
				Intent intent4 = new Intent(NearActivity.this,
						NearResultActivity.class);
				intent4.putExtra("findType", "超市");
				startActivity(intent4);
				break;
			case R.id.imgNearBank:
				Intent intent5 = new Intent(NearActivity.this,
						NearResultActivity.class);
				intent5.putExtra("findType", "ATM");
				startActivity(intent5);
				break;
			case R.id.imgNearHospital:
				Intent intent6 = new Intent(NearActivity.this,
						NearResultActivity.class);
				intent6.putExtra("findType", "医院");
				startActivity(intent6);
				break;
			case R.id.imgCustomSearch:
				EditText editSearchContent = (EditText) findViewById(R.id.editSearchContent);
				String searchContent = editSearchContent.getText().toString();
				if (searchContent != null && searchContent.length() > 0) {
					Intent intent7 = new Intent(NearActivity.this,
							NearResultActivity.class);
					intent7.putExtra("findType", searchContent);
					startActivity(intent7);
				}
				break;
			}

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backToVice();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	private void backToVice() {
		finish();
		overridePendingTransition(R.anim.zms_translate_up_out,
				R.anim.zms_translate_up_in);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
	}
}
