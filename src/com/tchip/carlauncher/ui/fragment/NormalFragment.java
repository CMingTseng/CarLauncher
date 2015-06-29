package com.tchip.carlauncher.ui.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.dao.VideoTableDao;
import com.tchip.carlauncher.model.VideoTable;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class NormalFragment extends Fragment {
	private Context mContext;
	private ListView mListView;

	private String currentFolder;
	private List<String> folderList;
	private List<String> fileList;

	private VideoTableDao mVideoTableDao;
	private ActionBar actionBar;

	private boolean IsFolder = true;

	private List<HashMap<String, Object>> fileData = new ArrayList<HashMap<String, Object>>();
	private String[] fileFrom = { "img_thumbnail", "name", "duration",
			"create_time", "video_size" };
	private int[] fileTo = { R.id.img_thumbnail, R.id.tv_name,
			R.id.tv_duration, R.id.tv_time, R.id.tv_size };
	private SimpleAdapter fileSimpleAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = this.getActivity();
		mVideoTableDao = new VideoTableDao(mContext);
		actionBar = getActivity().getActionBar();

		View normalView = inflater.inflate(R.layout.fragment_normal, container,
				false);
		mListView = (ListView) normalView.findViewById(R.id.lv_normal);
		mListView.setOnItemClickListener(onItemClickListener);
		mListView.setOnItemLongClickListener(onItemLongClickListener);
		mListView
				.setAdapter(getFolderAdapter(Constant.EROOTPATH.TEMPVIDEOPATH));
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		IsFolder = true;
		return normalView;
	}

	public boolean fragmentOnOptionsItemSelected() {
		if (!IsFolder) {
			mListView
					.setAdapter(getFolderAdapter(Constant.EROOTPATH.TEMPVIDEOPATH));
			IsFolder = true;
			return true;
		} else {
			return false;
		}

	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (IsFolder) {
				IsFolder = false;
				currentFolder = folderList.get(position);
				fileSimpleAdapter = new SimpleAdapter(mContext,
						getFileAdapterData(currentFolder),
						R.layout.list_item_fragment, fileFrom, fileTo);
				mListView.setAdapter(fileSimpleAdapter);
				Thread thread = new Thread(new MyThread());
				thread.start();
			} else {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(android.content.Intent.ACTION_VIEW);
				File file = new File(fileList.get(position));
				intent.setDataAndType(Uri.fromFile(file), "video/*");
				startActivity(intent);
			}
		}
	};

	private OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			if (!IsFolder) {
				// deleteDialog(fileList.get(position), IsFolder);
				fileLongClickDialog(fileList.get(position), IsFolder);
			} else {
				deleteDialog(folderList.get(position), IsFolder);
			}
			return true;
		}
	};

	private void EDeleteFile(String filePath, boolean isFolder) {
		if (!isFolder) {
			File file = new File(filePath);
			if (file.delete()) {
				mVideoTableDao.delByPath(filePath);
				// mListView.setAdapter(getFileAdapter(currentFolder));
				getFileAdapterData(currentFolder);
				if (null != fileSimpleAdapter) {
					fileSimpleAdapter.notifyDataSetChanged();
					Thread thread = new Thread(new MyThread());
					thread.start();
				}
			} else {
				Toast.makeText(mContext, getString(R.string.del_fail),
						Toast.LENGTH_LONG).show();
			}
		} else {
			Log.v("lzs2", "========>folderPaht:" + filePath);
			EDelFolder(Constant.EROOTPATH.TEMPVIDEOPATH + filePath
					+ File.separator);
			mListView
					.setAdapter(getFolderAdapter(Constant.EROOTPATH.TEMPVIDEOPATH));
		}
	}

	private void EDelFolder(String folderName) {
		mVideoTableDao.delByFoldPath(folderName);
		File file = new File(folderName);
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				Log.v("lzs2", "=====>childFiles:" + file.getPath());
				return;
			}
			for (int i = 0; i < childFiles.length; i++) {
				EDelFolder(childFiles[i].getPath());
			}
			file.delete();
		}
	}

	private void fileLongClickDialog(final String filePath,
			final boolean isFolder) {
		Log.v("lzs", "=====>long_filepath:" + filePath);
		VideoTable videoTable = mVideoTableDao.selectOneByPath(filePath);

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_file_longclick, null);
		ImageView fileLongClickImageView = (ImageView) view
				.findViewById(R.id.imv_filelongclick);
		TextView fileLongClickTextView = (TextView) view
				.findViewById(R.id.tv_filelongclick);

		if (null != videoTable.getPath_thumbnail())
			fileLongClickImageView.setImageDrawable(Drawable
					.createFromPath(videoTable.getPath_thumbnail()));
		final String fileName = videoTable.getName();
		final String thumbPath = videoTable.getPath_thumbnail();
		if (null != videoTable.getName())
			fileLongClickTextView.setText(fileName);

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(getString(R.string.choose_file_ctrl));
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setView(view);
		builder.setPositiveButton(R.string.dlg_keep_save,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						moveToKeep(fileName, filePath, thumbPath);
					}
				});

		builder.setNeutralButton(R.string.delete,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EDeleteFile(filePath, isFolder);
					}
				});

		builder.setNegativeButton(R.string.cancle,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});
		builder.show();
	}

	private void moveToKeep(String srcFileName, String srcFilePath,
			String thumbPath) {
		String destFileName = Constant.EROOTPATH.SAVEPATH + srcFileName;
		File saveFolderFile = new File(Constant.EROOTPATH.SAVEPATH);
		if (!saveFolderFile.exists()) {
			saveFolderFile.mkdir();
		}
		File srcFile = new File(srcFilePath);
		boolean moveSuccess = srcFile.renameTo(new File(destFileName));

		/*
		 * String thumbName =
		 * srcFileName.substring(0,srcFileName.lastIndexOf('.')) + ".png";
		 * 
		 * String thumbFolder = Constant.EROOTPATH.SAVEPATH + ".Thumb" +
		 * File.separator; File thumbFolderFile = new File(thumbFolder);
		 * if(!thumbFolderFile.exists()){ thumbFolderFile.mkdir(); } File
		 * thumbFile = new File(thumbPath);
		 * 
		 * boolean thumbMove = thumbFile.renameTo(new File(thumbFolder,
		 * thumbName));
		 */
		if (moveSuccess) {
			mVideoTableDao.updateToKeepSave(srcFileName);
			// mListView.setAdapter(getFileAdapter(currentFolder));
			getFileAdapterData(currentFolder);
			if (null != fileSimpleAdapter) {
				fileSimpleAdapter.notifyDataSetChanged();
				Thread thread = new Thread(new MyThread());
				thread.start();
			}

		}

	}

	private void deleteDialog(final String filePath, final boolean isFolder) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(getString(R.string.del_title));
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setMessage(getString(R.string.del_msg));
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				EDeleteFile(filePath, isFolder);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		builder.create().show();
	}

	private SimpleAdapter getFolderAdapter(String folderPath) {
		SimpleAdapter simpleAdapter = null;
		folderList = new ArrayList<String>();
		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		String[] from = { "folder_name" };
		int[] to = { R.id.tv_folder_name };
		File folderFile = new File(folderPath);
		File[] files = folderFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			folderList.add(files[i].getName());
			map.put("folder_name", files[i].getName());
			data.add(map);
		}
		simpleAdapter = new SimpleAdapter(mContext, data, R.layout.folder_item,
				from, to);
		return simpleAdapter;
	}

	private SimpleAdapter getFileAdapter(String folderPath) {
		folderPath = Constant.EROOTPATH.TEMPVIDEOPATH + folderPath
				+ File.separator;
		Log.v("lzs", "======>folderPath:" + folderPath);
		fileList = new ArrayList<String>();
		SimpleAdapter simpleAdapter = null;
		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		String[] from = { "img_thumbnail", "name", "duration", "create_time",
				"video_size" };
		int[] to = { R.id.img_thumbnail, R.id.tv_name, R.id.tv_duration,
				R.id.tv_time, R.id.tv_size };
		Cursor cursor = mVideoTableDao.selectByFolderPath(folderPath);
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

	private List<HashMap<String, Object>> getFileAdapterData(String folderPath) {
		fileData.clear();
		folderPath = Constant.EROOTPATH.TEMPVIDEOPATH + folderPath
				+ File.separator;
		fileList = new ArrayList<String>();
		Cursor cursor = mVideoTableDao.selectByFolderPath(folderPath);
		while (cursor.moveToNext()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("img_thumbnail",
					cursor.getString(cursor.getColumnIndex("path_thumbnail")));
			map.put("name", cursor.getString(cursor.getColumnIndex("name")));
			map.put("path", cursor.getString(cursor.getColumnIndex("path")));
			map.put("duration",
					cursor.getString(cursor.getColumnIndex("duration")));
			map.put("create_time",
					cursor.getString(cursor.getColumnIndex("btime")));
			map.put("video_size",
					cursor.getString(cursor.getColumnIndex("file_size")) + "MB");
			fileData.add(map);
			fileList.add(cursor.getString(cursor.getColumnIndex("path")));
		}
		return fileData;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (null != fileSimpleAdapter)
					fileSimpleAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		};
	};

	class MyThread implements Runnable {
		@Override
		public void run() {
			Log.v("lzs", "thread=======>run");
			String foldPath = Constant.EROOTPATH.TEMPVIDEOPATH + currentFolder
					+ File.separator;
			String thumbFoldPath = foldPath + "thumb" + File.separator;
			File thumbFold = new File(thumbFoldPath);
			if (!thumbFold.exists()) {
				thumbFold.mkdir();
			}
			for (int i = 0; i < fileData.size(); i++) {
				String videoFilePath = (String) fileData.get(i).get("path");
				String videoFileName = (String) fileData.get(i).get("name");
				if (videoFileName.equals("thumb"))
					continue;
				String thumbName = videoFileName.substring(0,
						videoFileName.lastIndexOf('.'))
						+ ".png";
				String thumbPath = thumbFoldPath + thumbName;

				Log.v("lzs", "===>videopath:" + videoFilePath
						+ "=====>videoFileName:" + videoFileName);
				Log.v("lzs", "===>thumbPath:" + thumbPath + "=====>thumbName:"
						+ thumbName);

				File thumbFile = new File(thumbPath);
				if (!thumbFile.exists()) {
					saveThumbnail(videoFilePath, thumbFoldPath, thumbName);
				}
				fileData.get(i).put("img_thumbnail", thumbPath);

				Message message = Message.obtain();
				message.what = 1;
				mHandler.sendMessage(message);

			}
		}
	}

	private void saveThumbnail(String videoFilePath, String thumbPath,
			String thumbName) {
		Bitmap thumbBitmap = getVideoThumbnail(videoFilePath);
		if (thumbBitmap != null) {
			saveBitmap(thumbBitmap, thumbPath, thumbName);
		}
	}

	private Bitmap getVideoThumbnail(String videoPath) {
		return getVideoThumbnail(videoPath, 96, 72,
				MediaStore.Images.Thumbnails.MINI_KIND);
	}

	private Bitmap getVideoThumbnail(String videoPath, int width, int height,
			int kind) {
		Log.v("lzs", "=========>videoPath:" + videoPath);
		Bitmap bitmap = null;
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	public void saveBitmap(Bitmap bm, String savePath, String picName) {
		if (bm == null) {
			Log.v("lzs", "====>bm==null");
		}
		Log.v("lzs", "=====>saveBitmap:" + bm);
		File file = new File(savePath);
		if (!file.exists()) {
			file.mkdir();
		}
		File f = new File(savePath, picName);
		if (f.exists()) {
			f.delete();
		}
		try {
			Log.v("lzs", "======>begin_save");
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
			Log.i("lzs", "bit_map_save!!");
		} catch (FileNotFoundException e) {
			Log.v("lzs", "====>e1:" + e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.v("lzs", "====>e2:" + e);
			e.printStackTrace();
		}
	}

}
