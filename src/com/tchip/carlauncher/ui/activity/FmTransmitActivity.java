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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
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

	private RelativeLayout layoutBack;
	private TextView textHint;
	private SeekBar fmSeekBar;
	
	private Button fmFreqDecrease, fmFreqIncrease;

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

		layoutBack = (RelativeLayout) findViewById(R.id.layoutBack);
		layoutBack.setOnClickListener(new MyOnClickListener());

		textHint = (TextView) findViewById(R.id.textHint);
		textHint.setTypeface(Typefaces.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf"));

		switchFm.setChecked(isFmTransmitOn());
		switchFm.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Settings.System.putString(getContentResolver(),
						FM_TRANSMITTER_ENABLE, isChecked ? "1" : "0");
				SaveFileToNode(nodeFmEnable, (isChecked ? "1" : "0"));

			}
		});

		fmSeekBar = (SeekBar) findViewById(R.id.fmSeekBar);
		// 875-1080
		// 0- 205
		fmSeekBar.setMax(205);
		int nowFrequency = getFmFrequceny(); // 当前频率
		fmSeekBar.setProgress(nowFrequency / 10 - 875);
		textHint.setText("  " + nowFrequency / 100.0f + "MHz");
		fmSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				setFmFrequency((seekBar.getProgress() + 875) * 10);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				float frequency = (progress + 875.0f) / 10;
				textHint.setText("  " + frequency + "MHz");
			}
		});
		
		//fm频率0.1增加减少
		fmFreqDecrease = (Button) findViewById(R.id.fmFreqDecrease);
		fmFreqIncrease = (Button) findViewById(R.id.fmFreqIncrease);
		fmFreqDecrease.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setFmFrequencySmallDeIncrease(false);
			}
		});
		fmFreqIncrease.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setFmFrequencySmallDeIncrease(true);
			}
		});
	}
	
	/**
	 * 小幅度的调整fm频率
	 * @param deincrease
	 */
	private void setFmFrequencySmallDeIncrease(boolean deincrease){
		int nowFrequency = getFmFrequceny() + (deincrease ? 10 : -10); // 当前频率
		fmSeekBar.setProgress(nowFrequency / 10 - 875);
		textHint.setText("  " + nowFrequency / 100.0f + "MHz");
		
		setFmFrequency(nowFrequency);
	}
	

	/**
	 * 获取设置中存取的频率
	 * 
	 * @return 8750-10800
	 */
	private int getFmFrequceny() {
		String fmChannel = Settings.System.getString(getContentResolver(),
				FM_TRANSMITTER_CHANNEL);

		return Integer.parseInt(fmChannel);
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

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layoutBack:
				finish();
				break;
			}
		}
	}

	/**
	 * 设置FM发射频率:8750-10800
	 * 
	 * @param frequency
	 */
	private void setFmFrequency(int frequency) {
		if (frequency >= 8750 || frequency <= 10800) {
			Settings.System.putString(getContentResolver(),
					FM_TRANSMITTER_CHANNEL, "" + frequency);

			SaveFileToNode(nodeFmChannel, String.valueOf(frequency));
			Log.v(Constant.TAG, "FM Transmit:Set FM Frequency success:"
					+ frequency / 100.0f + "MHz");
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
