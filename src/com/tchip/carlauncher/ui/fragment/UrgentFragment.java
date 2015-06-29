package com.tchip.carlauncher.ui.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.dao.VideoTableDao;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class UrgentFragment extends Fragment {

	private Context mContext;
	private VideoTableDao mVideoTableDao;
	private ListView mListView;
	private List<String> fileList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = this.getActivity();
		mVideoTableDao = new VideoTableDao(mContext);

		View urgentView = inflater.inflate(R.layout.fragment_urgent, container,
				false);
		mListView = (ListView) urgentView.findViewById(R.id.lv_urgen);
		mListView.setAdapter(getFileAdapter());
		mListView.setOnItemLongClickListener(onItemLongClickListener);
		mListView.setOnItemClickListener(onItemClickListener);
		return urgentView;
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			File file = new File(fileList.get(position));
			intent.setDataAndType(Uri.fromFile(file), "video/*");
			startActivity(intent);
		}
	};

	private OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			deleteDialog(fileList.get(position));
			return true;
		}
	};

	private void deleteDialog(final String filePath) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(getString(R.string.del_title));
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setMessage(getString(R.string.del_msg));
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				EDeleteFile(filePath);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		builder.create().show();
	}

	private void EDeleteFile(String filePath) {
		File file = new File(filePath);
		if (file.delete()) {
			mVideoTableDao.delByPath(filePath);
			mListView.setAdapter(getFileAdapter());
		}
	}

	private SimpleAdapter getFileAdapter() {
		SimpleAdapter simpleAdapter = null;
		fileList = new ArrayList<String>();
		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		String[] from = { "img_thumbnail", "name", "duration", "create_time",
				"video_size" };
		int[] to = { R.id.img_thumbnail, R.id.tv_name, R.id.tv_duration,
				R.id.tv_time, R.id.tv_size };

		Cursor cursor = mVideoTableDao.selectProtect();

		while (cursor.moveToNext()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("img_thumbnail",
					cursor.getString(cursor.getColumnIndex("path_thumbnail")));
			map.put("name", cursor.getString(cursor.getColumnIndex("name")));
			map.put("duration",
					cursor.getString(cursor.getColumnIndex("duration")));
			map.put("create_time",
					cursor.getString(cursor.getColumnIndex("btime")));
			map.put("video_size",
					cursor.getString(cursor.getColumnIndex("file_size")) + "MB");
			data.add(map);
			fileList.add(cursor.getString(cursor.getColumnIndex("path")));
		}
		simpleAdapter = new SimpleAdapter(mContext, data,
				R.layout.list_item_fragment, from, to);
		return simpleAdapter;
	}
}
