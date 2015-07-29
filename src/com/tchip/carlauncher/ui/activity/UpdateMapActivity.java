package com.tchip.carlauncher.ui.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.view.ButtonFlat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateMapActivity extends Activity {
	private final static String PATH_TO = "/storage/sdcard0/BaiduMapSDK/vmp/l/";

	private final static String[] pathFrom = {
			"/storage/sdcard1/BaiduMapSDK/vmp/l/",
			"/storage/sdcard1/BaiduMap/vmp/l/",
			"/storage/sdcard2/BaiduMapSDK/vmp/l/",
			"/storage/sdcard2/BaiduMap/vmp/l/" };

	private ProgressBar progressCopy;
	private TextView textHint, textDetail;
	private RelativeLayout layoutBack, layoutUpdateOnline;
	private ButtonFlat btnStart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_update_map);

		initialLayout();

		// statrtScanThread();
	}

	private void initialLayout() {
		progressCopy = (ProgressBar) findViewById(R.id.progressCopy);
		progressCopy.setVisibility(View.GONE);

		textHint = (TextView) findViewById(R.id.textHint);
		// textHint.setText("正在扫描SD卡是否有离线地图");

		textDetail = (TextView) findViewById(R.id.textDetail);
		layoutBack = (RelativeLayout) findViewById(R.id.layoutBack);
		layoutBack.setOnClickListener(new MyOnClickListener());

		btnStart = (ButtonFlat) findViewById(R.id.btnStart);
		btnStart.setBackgroundColor(Color.parseColor("#ffffff")); // TextColor
		btnStart.setOnClickListener(new MyOnClickListener());
		btnStart.setVisibility(View.VISIBLE);

		layoutUpdateOnline = (RelativeLayout) findViewById(R.id.layoutUpdateOnline);
		layoutUpdateOnline.setOnClickListener(new MyOnClickListener());
	}

	private void statrtScanThread() {
		// TODO：判断是否需要拷贝
		new Thread(new CopyThread()).start();
	}

	private void minimumWindow() {
		// Intent intent = new Intent(Intent.ACTION_MAIN);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// intent.addCategory(Intent.CATEGORY_HOME);
		// startActivity(intent);
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			minimumWindow();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layoutBack:
				minimumWindow();
				break;

			case R.id.btnStart:
				statrtScanThread();
				break;

			case R.id.layoutUpdateOnline:
				Intent intentUpdate = new Intent(UpdateMapActivity.this,
						OfflineBaiduMapActivity.class);
				startActivity(intentUpdate);
				break;

			default:
				break;
			}

		}
	}

	/**
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath
	 *            String 原文件路径
	 * @param newPath
	 *            String 复制后路径
	 * @return boolean
	 */
	public boolean copyFolder(String oldPath, String newPath) {
		boolean isok = true;

		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			progressCopy.setMax(file.length);
			if (file.length > 0) {
				Message message = new Message();
				message.what = 2;
				copyHandler.sendMessage(message);
			}

			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				// if (temp.isDirectory()) {// 如果是子文件夹
				// copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				// }
				Log.v(Constant.TAG, "Copy:" + file[i] + " Success");
				progressCopy.setProgress(i);
				Message message = new Message();
				message.what = 3;
				message.arg1 = i + 1;
				message.arg2 = progressCopy.getMax();
				copyHandler.sendMessage(message);
			}

			// 更新Media Database
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
					Uri.parse("file://" + PATH_TO)));

			Message message = new Message();
			message.what = 1;
			copyHandler.sendMessage(message);
		} catch (Exception e) {
			Log.e(Constant.TAG, "Copy Map form SD Error:" + e.toString());
			isok = false;
			Message message = new Message();
			message.what = 0;
			copyHandler.sendMessage(message);
		}

		return isok;
	}

	final Handler copyHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Log.e(Constant.TAG, "copy data failed!");
				textDetail.setVisibility(View.GONE);
				textHint.setText("SD卡未检测到离线地图");
				progressCopy.setVisibility(View.GONE);
				btnStart.setVisibility(View.VISIBLE);
				// finish();
				break;

			case 1:
				Log.e(Constant.TAG, "copy data success!");
				Intent i = new Intent();
				setResult(RESULT_OK, i);

				textHint.setText("离线地图更新完成.");
				textDetail.setVisibility(View.GONE);
				progressCopy.setVisibility(View.GONE);
				btnStart.setVisibility(View.VISIBLE);
				importOfflineMapFromSDCard();
				break;

			case 2:
				textHint.setText("SD卡检测到离线地图，正在导入，请勿离开。");
				progressCopy.setVisibility(View.VISIBLE);
				btnStart.setVisibility(View.GONE);
				break;

			case 3:
				int now = msg.arg1;
				int total = msg.arg2;
				textDetail.setVisibility(View.VISIBLE);
				textDetail.setText(now + "/" + total);
				break;

			case 4:
				// textHint.setText("正在检测SD卡是否有离线地图");
				break;
			}
			super.handleMessage(msg);
		}
	};

	public class CopyThread implements Runnable {

		@Override
		public void run() {

			for (int i = 0; i < pathFrom.length; i++) {
				if (!copyFolder(pathFrom[i], PATH_TO)) {

				} else {
					Message message = new Message();
					message.what = 4;
					copyHandler.sendMessage(message);
					Log.v(Constant.TAG, "Copy Map form SD Success, From:"
							+ pathFrom[i]);
					// importOfflineMapFromSDCard();
					break;
				}
			}
		}
	}

	/**
	 * 导入离线地图包 TODO：需要新Thread
	 */
	private MKOfflineMap mOffline = null;

	public void importOfflineMapFromSDCard() {
		mOffline = new MKOfflineMap();
		mOffline.init(new MyMKOfflineMapListener());
		int num = mOffline.importOfflineData();
		Log.v(Constant.TAG, "Import Baidu Offline Map number:" + num);
		if (num == 0) {
			// 没有导入离线包，可能是离线包放置位置不正确，或离线包已经导入过
		} else {
			// "成功导入 num 个离线包
		}
	}

	class MyMKOfflineMapListener implements MKOfflineMapListener {

		@Override
		public void onGetOfflineMapState(int type, int state) {
			switch (type) {
			case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
				MKOLUpdateElement update = mOffline.getUpdateInfo(state);
				// 处理下载进度更新提示
				if (update != null) {
					// stateView.setText(String.format("%s : %d%%",
					// update.cityName,
					// update.ratio));
					// updateView();
				}
				break;
			case MKOfflineMap.TYPE_NEW_OFFLINE:
				// 有新离线地图安装
				break;
			case MKOfflineMap.TYPE_VER_UPDATE:
				// 版本更新提示
				// MKOLUpdateElement e = mOffline.getUpdateInfo(state);
				break;
			}
		}
	}

}
