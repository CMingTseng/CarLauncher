package com.tchip.carlauncher.ui.activity;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MultimediaActivity extends Activity {

	private SharedPreferences preferences;
	private Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_multimedia);

		preferences = getSharedPreferences(Constant.SHARED_PREFERENCES_NAME,
				MODE_PRIVATE);
		editor = preferences.edit();
		initLayout();
	}

	private void initLayout() {
		// 图片
		RelativeLayout layoutImage = (RelativeLayout) findViewById(R.id.layoutImage);
		layoutImage.setOnClickListener(new MyOnClickListener());

		// 人脸检测
		RelativeLayout imageSearch = (RelativeLayout) findViewById(R.id.layoutFace);
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

	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layoutImage:
				try {
					ComponentName componentImage = new ComponentName(
							"com.android.gallery3d",
							"com.android.gallery3d.app.GalleryActivity");
					Intent intentImage = new Intent();
					intentImage.setComponent(componentImage);
					intentImage.addCategory(Intent.CATEGORY_LAUNCHER);
					intentImage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intentImage);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case R.id.layoutFace:
				Intent intentFaceDetect = new Intent(getApplicationContext(),
						FaceDetectActivity.class);
				startActivity(intentFaceDetect);
				break;

			case R.id.layoutMusic:
				try {
					ComponentName componentMusic;
					// 普通HD版："cn.kuwo.kwmusichd","cn.kuwo.kwmusichd.WelcomeActivity"
					// 车载HD版："cn.kuwo.kwmusiccar","cn.kuwo.kwmusiccar.WelcomeActivity"
					componentMusic = new ComponentName("cn.kuwo.kwmusiccar",
							"cn.kuwo.kwmusiccar.WelcomeActivity");
					Intent intentMusic = new Intent();
					intentMusic.setComponent(componentMusic);
					startActivity(intentMusic);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case R.id.layoutVideo:
				// intentVideo.setAction("android.intent.action.VIEW");
				// intentVideo.addCategory("android.intent.category.LAUNCHER");
				try {
					ComponentName componentVideo = new ComponentName(
							"com.mediatek.videoplayer",
							"com.mediatek.videoplayer.MovieListActivity");
					Intent intentVideo = new Intent();
					intentVideo.setComponent(componentVideo);
					intentVideo.addCategory(Intent.CATEGORY_DEFAULT);
					intentVideo.addCategory(Intent.CATEGORY_LAUNCHER);
					intentVideo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					startActivity(intentVideo);
				} catch (Exception e) {
					e.printStackTrace();
				}
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
		super.onResume();
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

		// 更新MediaStore
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.parse("file://" + "/mnt/sdcard/tachograph")));
	}
}
