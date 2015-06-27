package com.tchip.carlauncher.ui.activity;

import com.tchip.carlauncher.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MultimediaActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_multimedia);
		initLayout();
	}

	private void initLayout() {
		// 图片
		RelativeLayout layoutImage = (RelativeLayout) findViewById(R.id.layoutImage);
		layoutImage.setOnClickListener(new MyOnClickListener());

		// 人脸检测
		ImageView imageSearch = (ImageView) findViewById(R.id.imageSearch);
		imageSearch.setOnClickListener(new MyOnClickListener());

		// 音乐
		RelativeLayout layoutMusic = (RelativeLayout) findViewById(R.id.layoutMusic);
		layoutMusic.setOnClickListener(new MyOnClickListener());

		// 视频
		RelativeLayout layoutVideo = (RelativeLayout) findViewById(R.id.layoutVideo);
		layoutVideo.setOnClickListener(new MyOnClickListener());

		// 返回
		Button btnToMainFromMultimedia = (Button) findViewById(R.id.btnBack);
		btnToMainFromMultimedia.setOnClickListener(new MyOnClickListener());

		// 搜索
		// Button btnSearch = (Button) findViewById(R.id.btnSearch);
		// btnSearch.setOnClickListener(new MyOnClickListener());
	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layoutImage:
				ComponentName componentImage = new ComponentName(
						"com.android.gallery3d",
						"com.android.gallery3d.app.GalleryActivity");
				Intent intentImage = new Intent();
				intentImage.setComponent(componentImage);
				startActivity(intentImage);
				break;

			case R.id.imageSearch:
				Intent intentFaceDetect = new Intent(getApplicationContext(),
						FaceDetectActivity.class);
				startActivity(intentFaceDetect);
				break;

			case R.id.layoutMusic:
				Intent intentMusic = new Intent(getApplicationContext(),
						MusicMainContentActivity.class);
				startActivity(intentMusic);
				break;

			case R.id.layoutVideo:
				ComponentName componentVideo = new ComponentName(
						"com.mediatek.videoplayer",
						"com.mediatek.videoplayer.MovieListActivity");
				Intent intentVideo = new Intent();
				intentVideo.setComponent(componentVideo);
				startActivity(intentVideo);
				break;

			case R.id.btnBack:
				backToMain();
				break;
			}

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backToMain();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	private void backToMain() {
		finish();
		overridePendingTransition(R.anim.zms_translate_down_out,
				R.anim.zms_translate_down_in);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
	}
}
