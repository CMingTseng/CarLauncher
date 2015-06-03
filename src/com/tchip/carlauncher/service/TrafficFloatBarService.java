package com.tchip.carlauncher.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.ui.activity.TrafficStatActivity;
import com.tchip.carlauncher.util.TrafficAndroidUtils;
import com.tchip.carlauncher.util.TrafficPreferencesUtils;
import com.tchip.carlauncher.util.TrafficUtils;

public class TrafficFloatBarService extends Service {

	public final static String ACTION_SERVICE = "float_bar_service";

	private WindowManager wm = null;
	private WindowManager.LayoutParams WmParams = null;
	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;
	private View rate_layout;
	private TextView rate;
	private boolean ismoving = false;

	@Override
	public void onCreate() {
		super.onCreate();

		/**
		 * 通过广播控制Service运行与更新悬浮窗
		 */
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(TrafficUtils.ACTION_UPDATE_TRAFFIC);
		mFilter.addAction(ACTION_SERVICE);
		registerReceiver(mReceiver, mFilter);

		createView();

		/**
		 * 启用前台Service
		 */
		Notification notification = new Notification(R.drawable.ic_launcher,
				"悬浮窗已开启", System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, TrafficStatActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(this, getString(R.string.app_name),
				"悬浮窗正在打开", pendingIntent);
		startForeground(10086, notification);
	}

	private void createView() {

		rate_layout = View.inflate(this, R.layout.trafftic_float_bar, null);
		rate = TrafficAndroidUtils.findViewById(rate_layout, R.id.rate);

		wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		WmParams = new WindowManager.LayoutParams();
		WmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		WmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		WmParams.gravity = Gravity.CENTER;
		WmParams.x = 0;
		WmParams.y = 0;
		WmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		WmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		WmParams.format = PixelFormat.RGBA_8888;
		wm.addView(rate_layout, WmParams);
		rate_layout.setOnTouchListener(new MyTouchEvent());
	}

	private class MyTouchEvent implements View.OnTouchListener {

		private float preXP;
		private float preXM;
		private float preYP;
		private float preYM;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			float oldX = x;
			float oldY = y;
			x = event.getRawX();
			y = event.getRawY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				ismoving = false;
				break;
			case MotionEvent.ACTION_MOVE:
				if (event.getPointerCount() != 1)
					break;
				ismoving = true;
				WmParams.x += getXInt(x - oldX);
				WmParams.y += getYInt(y - oldY);
				wm.updateViewLayout(rate_layout, WmParams);
				break;
			case MotionEvent.ACTION_UP:
				cealrPre();
				break;
			}
			if (!ismoving) {
				return false;
			} else {
				return true;
			}
		}

		private void cealrPre() {
			preXM = 0.0f;
			preXP = 0.0f;
			preYM = 0.0f;
			preYP = 0.0f;
		}

		private int getXInt(float accur) {
			int intNum = (int) accur;
			if (accur < 0) {
				preXM -= accur - intNum;
				if (preXM >= 1.0f) {
					preXM -= 1.0f;
					intNum -= 1.0f;
				}
			} else if (accur > 0) {
				preXP += accur - intNum;
				if (preXP >= 1.0f) {
					preXP -= 1.0f;
					intNum += 1.0f;
				}
			}

			return intNum;
		}

		private int getYInt(float accur) {
			int intNum = (int) accur;
			if (accur < 0) {
				preYM -= accur - intNum;
				if (preYM >= 1.0f) {
					preYM -= 1.0f;
					intNum -= 1.0f;
				}
			} else if (accur > 0) {
				preYP += accur - intNum;
				if (preYP >= 1.0f) {
					preYP -= 1.0f;
					intNum += 1.0f;
				}
			}

			return intNum;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			wm.removeViewImmediate(rate_layout);
		} catch (Exception e) {
			e.printStackTrace();
		}
		stopForeground(true);
		unregisterReceiver(mReceiver);
		TrafficPreferencesUtils.getInstance().setChecked(this,
				TrafficStatActivity.TAG_FLOAT, false);
	}

	private class FetchDataTask extends AsyncTask<Void, Void, Long> {

		@Override
		protected Long doInBackground(Void... params) {
			return TrafficUtils.getTrafficPerSec(TrafficFloatBarService.this);
		}

		@Override
		protected void onPostExecute(Long data) {
			Log.i("TAG", "(Service)FetchDataTask:onPostExecute() 界面更新");
			rate.setText(String.format(getString(R.string.rate_per_sec),
					TrafficUtils.dataSizeFormat(data)));
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_SERVICE)) {
				stopSelf();
			} else if (action.equals(TrafficUtils.ACTION_UPDATE_TRAFFIC)) {
				new FetchDataTask().execute();
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
