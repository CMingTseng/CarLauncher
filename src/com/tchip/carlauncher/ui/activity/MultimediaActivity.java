package com.tchip.carlauncher.ui.activity;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.MyApplication;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.model.MusicInfo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MultimediaActivity extends Activity {

	private TextView textRecent, textRecentHint;
	private RelativeLayout layoutMusicRecent;

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

		textRecent = (TextView) findViewById(R.id.textRecent);
		textRecentHint = (TextView) findViewById(R.id.textRecentHint);
		layoutMusicRecent = (RelativeLayout) findViewById(R.id.layoutMusicRecent);
		updateRecentMusic();

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

	private void updateRecentMusic() {
		String nowPlayMusic = MyApplication.nowPlayMusic;
		if (MyApplication.mServiceManager != null
				&& MyApplication.mServiceManager.getPlayState() == Constant.MPS_PLAYING
				&& nowPlayMusic != null && nowPlayMusic.trim().length() > 0) {
			MusicInfo musicNow = MyApplication.mServiceManager.getCurMusic();
			MyApplication.nowPlayMusic = musicNow.artist + "-"
					+ musicNow.musicName;

			textRecent.setText(MyApplication.nowPlayMusic);
			layoutMusicRecent.setVisibility(View.VISIBLE);
			textRecentHint.setText(getResources().getString(
					R.string.music_play_now));

			editor.putString("rencentMusicList", MyApplication.nowPlayMusic);
			editor.commit();
		} else {
			nowPlayMusic = preferences.getString("rencentMusicList", "NULL");
			if (!"NULL".equals(nowPlayMusic)) {
				layoutMusicRecent.setVisibility(View.VISIBLE);
				textRecent.setText(nowPlayMusic);
				textRecentHint.setText(getResources().getString(
						R.string.music_play_recent));
			} else
				layoutMusicRecent.setVisibility(View.INVISIBLE);
		}
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

			case R.id.layoutFace:
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
		super.onResume();
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

		// 更新最近播放音乐
		updateRecentMusic();
	}
}
