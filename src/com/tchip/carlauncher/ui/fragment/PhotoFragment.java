package com.tchip.carlauncher.ui.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class PhotoFragment extends Fragment {
	private Context mContext;
	private GridView photoGridView;
	private List<String> fileList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		View photoView = inflater.inflate(R.layout.fragment_photo, container,
				false);
		photoGridView = (GridView) photoView.findViewById(R.id.gv_photo);
		photoGridView.setAdapter(getPhotoAdapter());
		photoGridView.setOnItemClickListener(onItemClickListener);

		return photoView;
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			File file = new File(fileList.get(position));
			intent.setDataAndType(Uri.fromFile(file), "image/*");
			startActivity(intent);
		}
	};

	private SimpleAdapter getPhotoAdapter() {

		SimpleAdapter simpleAdapter = null;
		fileList = new ArrayList<String>();
		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		File photoFoldeFile = new File(Constant.EROOTPATH.PHOTOPATH);
		String[] from = { "photo_img" };
		int[] to = { R.id.imv_photo_item };
		if (!photoFoldeFile.exists()) {
			photoFoldeFile.mkdir();
		}
		File[] photoFiles = photoFoldeFile.listFiles();
		for (int i = 0; i < photoFiles.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			Log.v("lzs", "====>photopath:" + photoFiles[i].getPath());
			map.put("photo_img", photoFiles[i].getPath());
			// map.put("photo_img", R.drawable.btn_add);
			data.add(map);
			fileList.add(photoFiles[i].getPath());
		}
		simpleAdapter = new SimpleAdapter(mContext, data, R.layout.photo_item,
				from, to);

		return simpleAdapter;
	}
	/*
	 * private class ImageAdapter extends BaseAdapter{ private Context mContext;
	 * 
	 * public ImageAdapter(Context context) { this.mContext=context; }
	 * 
	 * @Override public int getCount() { return mThumbIds.length; }
	 * 
	 * @Override public Object getItem(int position) { return
	 * mThumbIds[position]; }
	 * 
	 * @Override public long getItemId(int position) { // TODO Auto-generated
	 * method stub return 0; }
	 * 
	 * @Override public View getView(int position, View convertView, ViewGroup
	 * parent) { //定义一个ImageView,显示在GridView里 ImageView imageView;
	 * if(convertView==null){ imageView=new ImageView(mContext);
	 * imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
	 * imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	 * imageView.setPadding(8, 8, 8, 8); }else{ imageView = (ImageView)
	 * convertView; } imageView.setImageResource(mThumbIds[position]); return
	 * imageView; }
	 * 
	 * 
	 * 
	 * }
	 */
}
