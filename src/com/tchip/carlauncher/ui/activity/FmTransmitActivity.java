package com.tchip.carlauncher.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.model.Typefaces;
import com.tchip.carlauncher.view.SwitchButton;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.provider.Settings;

public class FmTransmitActivity extends Activity {

	/**
	 * 开关节点
	 * 
	 * 1：开 0：关
	 */
	private File nodeFmEnable = new File(
			"/sys/devices/platform/mt-i2c.1/i2c-1/1-002c/enable_qn8027");

	/**
	 * 频率节点
	 * 
	 * 频率范围：7600~10800:8750-10800
	 */
	private File nodeFmChannel = new File(
			"/sys/devices/platform/mt-i2c.1/i2c-1/1-002c/setch_qn8027");

	/**
	 * 系统设置：FM发射开关
	 */
	private String FM_TRANSMITTER_ENABLE = "fm_transmitter_enable";

	/**
	 * 系统设置：FM发射频率
	 */
	private String FM_TRANSMITTER_CHANNEL = "fm_transmitter_channel";

	private Button fmLow, fmMiddle, fmHigh;

	private RelativeLayout layoutBack;
	private TextView textHint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		setContentView(R.layout.activity_fm_transmit);

		initialLayout();
	}

	private void initialLayout() {
		// 开关
		SwitchButton switchFm = (SwitchButton) findViewById(R.id.switchFm);
		fmLow = (Button) findViewById(R.id.fmLow);
		fmMiddle = (Button) findViewById(R.id.fmMiddle);
		fmHigh = (Button) findViewById(R.id.fmHigh);

		fmLow.setTypeface(Typefaces.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf"));
		fmMiddle.setTypeface(Typefaces.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf"));
		fmHigh.setTypeface(Typefaces.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf"));

		fmLow.setOnClickListener(new MyOnClickListener());
		fmMiddle.setOnClickListener(new MyOnClickListener());
		fmHigh.setOnClickListener(new MyOnClickListener());

		layoutBack = (RelativeLayout) findViewById(R.id.layoutBack);
		layoutBack.setOnClickListener(new MyOnClickListener());

		textHint = (TextView) findViewById(R.id.textHint);

		updateChoseButton(getFmFrequcenyId());

		switchFm.setChecked(isFmTransmitOn());
		setButtonEnabled(isFmTransmitOn());

		switchFm.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Settings.System.putString(getContentResolver(),
						FM_TRANSMITTER_ENABLE, isChecked ? "1" : "0");
				setButtonEnabled(isChecked);
				SaveFileToNode(nodeFmEnable, (isChecked ? "1" : "0"));
				if (!isChecked) {
					updateChoseButton(0);
				} else {
					int nowId = getFmFrequcenyId();
					updateChoseButton(nowId);
				}
			}
		});
	}

	/**
	 * 按钮是否可用
	 * 
	 * @param isFmTransmitOpen
	 */
	private void setButtonEnabled(boolean isFmTransmitOpen) {
		fmLow.setEnabled(isFmTransmitOpen);
		fmMiddle.setEnabled(isFmTransmitOpen);
		fmHigh.setEnabled(isFmTransmitOpen);
	}

	private int getFmFrequcenyId() {
		int nowFmChannel = 0;
		String fmChannel = Settings.System.getString(getContentResolver(),
				FM_TRANSMITTER_CHANNEL);
		if (isFmTransmitOn() && fmChannel.trim().length() > 0) {
			if ("8550".equals(fmChannel)) {
				nowFmChannel = 1;
			} else if ("10570".equals(fmChannel)) {
				nowFmChannel = 3;
			} else {
				nowFmChannel = 2;
			}
		}

		return nowFmChannel;
	}

	/**
	 * FM发射是否打开
	 * 
	 * @return
	 */
	private boolean isFmTransmitOn() {
		boolean isFmTransmitOpen = false;
		String fmEnable = Settings.System.getString(getContentResolver(),
				FM_TRANSMITTER_ENABLE);
		if (fmEnable.trim().length() > 0) {
			if ("1".equals(fmEnable)) {
				isFmTransmitOpen = true;
			} else {
				isFmTransmitOpen = false;
			}
		}
		return isFmTransmitOpen;
	}

	/**
	 * 根据选中状态更新按钮字体颜色
	 * 
	 * @param which
	 */
	private void updateChoseButton(int which) {
		switch (which) {
		case 0:
			fmLow.setTextColor(Color.BLACK);
			fmMiddle.setTextColor(Color.BLACK);
			fmHigh.setTextColor(Color.BLACK);
			textHint.setText("请打开FM发射开关");
			break;

		case 1:
			fmLow.setTextColor(Color.BLUE);
			fmMiddle.setTextColor(Color.BLACK);
			fmHigh.setTextColor(Color.BLACK);
			textHint.setText("当前发射频率" + Constant.FMTransmit.HINT_LOW + "兆赫");
			break;

		case 2:
			fmLow.setTextColor(Color.BLACK);
			fmMiddle.setTextColor(Color.BLUE);
			fmHigh.setTextColor(Color.BLACK);
			textHint.setText("当前发射频率" + Constant.FMTransmit.HINT_MIDDLE + "兆赫");
			break;

		case 3:
			fmLow.setTextColor(Color.BLACK);
			fmMiddle.setTextColor(Color.BLACK);
			fmHigh.setTextColor(Color.BLUE);
			textHint.setText("当前发射频率" + Constant.FMTransmit.HINT_HIGH + "兆赫");
			break;

		default:
			break;
		}
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layoutBack:
				finish();
				break;

			case R.id.fmLow:
				setFmFrequency(Constant.FMTransmit.CHANNEL_LOW);
				updateChoseButton(1);
				break;

			case R.id.fmMiddle:
				setFmFrequency(Constant.FMTransmit.CHANNEL_MIDDLE);
				updateChoseButton(2);
				break;

			case R.id.fmHigh:
				setFmFrequency(Constant.FMTransmit.CHANNEL_HIGH);
				updateChoseButton(3);
				break;
			}
		}
	}

	private void setFmFrequency(int frequency) {
		if (frequency >= 8750 || frequency <= 10800) {
			Settings.System.putString(getContentResolver(),
					FM_TRANSMITTER_CHANNEL, "" + frequency);

			SaveFileToNode(nodeFmChannel, String.valueOf(frequency));
			Log.v(Constant.TAG, "FM Transmit:Set FM Frequency success:"
					+ frequency);
		}
	}

	protected void SaveFileToNode(File file, String value) {
		if (file.exists()) {
			try {
				StringBuffer strbuf = new StringBuffer("");
				strbuf.append(value);
				OutputStream output = null;
				OutputStreamWriter outputWrite = null;
				PrintWriter print = null;

				try {
					output = new FileOutputStream(file);
					outputWrite = new OutputStreamWriter(output);
					print = new PrintWriter(outputWrite);
					print.print(strbuf.toString());
					print.flush();
					output.close();

				} catch (FileNotFoundException e) {
					e.printStackTrace();
					Log.e(Constant.TAG, "FM Transmit:output error");
				}
			} catch (IOException e) {
				Log.e(Constant.TAG, "FM Transmit:IO Exception");
			}
		} else {
			Log.e(Constant.TAG, "FM Transmit:File:" + file + "not exists");
		}
	}

}
