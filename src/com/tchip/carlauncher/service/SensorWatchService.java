package com.tchip.carlauncher.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by AlexZhou on 2015/3/26. 11:06
 */
public class SensorWatchService extends Service {
	private final static int AXIS_X = 0;
	private final static int AXIS_Y = 1;
	private final static int AXIS_Z = 2;

	private float valueX = 0f;
	private float valueY = 0f;
	private float valueZ = 0f;

	private final static float LIMIT_X = 11f;
	private final static float LIMIT_Y = 11f;
	private final static float LIMIT_Z = 11f;

	private int[] crashFlag = { 0, 0, 0 }; // {X-Flag, Y-Flag, Z-Flag}
	private boolean isCrash = false;
	private Toast toast = null;

	@Override
	public void onCreate() {
		super.onCreate();

		SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// Using TYPE_ACCELEROMETER first if exit, then TYPE_GRAVITY
		Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
			sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
		SensorEventListener sensorEventListener = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent event) {
				valueX = event.values[AXIS_X];
				valueY = event.values[AXIS_Y];
				valueZ = event.values[AXIS_Z];

				if (valueX > LIMIT_X || valueX < -LIMIT_X) {
					crashFlag[0] = 1;
					isCrash = true;
				}
				if (valueY > LIMIT_Y || valueY < -LIMIT_Y) {
					crashFlag[1] = 1;
					isCrash = true;
				}
				if (valueZ > LIMIT_Z || valueZ < -LIMIT_Z) {
					crashFlag[2] = 1;
					isCrash = true;
				}
				if (isCrash) {
					// showTextToast("Crashed at " + getTime());
					isCrash = false;
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		// 0:SENSOR_DELAY_FASTEST 1:SENSOR_DELAY_GAME 2:SENSOR_DELAY_UI
		// 3:SENSOR_DELAY_NORMAL
		sensorManager.registerListener(sensorEventListener, sensor,
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private void showTextToast(String msg) {
		if (toast == null) {
			toast = Toast.makeText(getApplicationContext(), msg,
					Toast.LENGTH_SHORT);
			toast.show();
		} else {
			toast.setText(msg);
		}

	}

	public String getTime() {
		long nowTime = System.currentTimeMillis();
		Date date = new Date(nowTime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return sdf.format(date);
	}

	public float getValue(int axis) {
		switch (axis) {
		case AXIS_X:
			return valueX;
		case AXIS_Y:
			return valueY;
		case AXIS_Z:
			return valueZ;
		default:
			return 0f;
		}
	}
}
