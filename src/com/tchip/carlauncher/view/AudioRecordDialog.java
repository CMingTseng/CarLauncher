package com.tchip.carlauncher.view;

import com.tchip.carlauncher.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class AudioRecordDialog {
	private Dialog dialog;
	private ImageView imageVolume;

	private Context context;

	public AudioRecordDialog(Context context) {
		this.context = context;
	}

	public void showDialog() {

		dialog = new Dialog(context, R.style.Theme_RecordDialog);
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_audio_record, null);
		dialog.setContentView(view);

		imageVolume = (ImageView) dialog.findViewById(R.id.imageVolume);

		dialog.show();
	}

	public void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
	}

	/**
	 * 更新音量
	 * 
	 * @param volume
	 *            0-30
	 */
	public void updateVolumeLevel(int volume) {
		if (dialog != null && dialog.isShowing()) {
			int level = 7 * volume / 31 + 1;
			int volumeResId = context.getResources().getIdentifier(
					"icon_volume_" + level, "drawable",
					context.getPackageName());
			imageVolume.setImageResource(volumeResId);
		}
	}

}
