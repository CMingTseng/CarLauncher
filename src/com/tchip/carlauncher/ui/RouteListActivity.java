package com.tchip.carlauncher.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.adapter.RouteListAdapter;
import com.tchip.carlauncher.adapter.SwipeMenuCreator;
import com.tchip.carlauncher.model.RouteDistanceDbHelper;
import com.tchip.carlauncher.model.RouteList;
import com.tchip.carlauncher.model.SwipeMenu;
import com.tchip.carlauncher.model.SwipeMenuItem;
import com.tchip.carlauncher.view.ButtonFlat;
import com.tchip.carlauncher.view.ButtonFloat;
import com.tchip.carlauncher.view.SwipeMenuListView;
import com.tchip.carlauncher.view.SwipeMenuListView.OnMenuItemClickListener;
import com.tchip.carlauncher.view.SwipeMenuListView.OnSwipeListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RouteListActivity extends Activity {
	private SwipeMenuListView routeList;
	private final String ROUTE_PATH = "/sdcard/Route/";
	// private ArrayAdapter<String> adapter;
	private CalendarView filterDate;
	private TextView tvNoFile;
	private ButtonFlat btnShowAll;
	private List<String> fileNameList;
	private TextView tvFilterState;
	private ArrayList<RouteList> routeArray;
	private RouteListAdapter routeAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_route_list);

		tvNoFile = (TextView) findViewById(R.id.tvNoFile);
		tvFilterState = (TextView) findViewById(R.id.tvFilterState);

		filterDate = (CalendarView) findViewById(R.id.filterDate);
		filterDate.setShowWeekNumber(false);
		filterDate.setOnDateChangeListener(new MyDateChangeListener());

		btnShowAll = (ButtonFlat) findViewById(R.id.btnShowAll);
		btnShowAll.setBackgroundColor(Color.parseColor("#ffffff")); // TextColor
		btnShowAll.setOnClickListener(new MyOnClickListener());

		routeList = (SwipeMenuListView) findViewById(R.id.routeList);
		showRouteList("20");
		tvFilterState.setText("轨迹未筛选");

		ButtonFloat btnToMainFromRouteList = (ButtonFloat) findViewById(R.id.btnToMainFromRouteList);
		btnToMainFromRouteList.setDrawableIcon(getResources().getDrawable(
				R.drawable.icon_arrow_up));
		btnToMainFromRouteList.setOnClickListener(new MyOnClickListener());

		// Swipe Menu START
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				// 查看
				SwipeMenuItem openItem = new SwipeMenuItem(
						getApplicationContext());
				// openItem.setBackground(new ColorDrawable(Color.rgb(0xC9,
				// 0xC9,
				// 0xCE)));
				openItem.setBackground(new ColorDrawable(Color.rgb(0x1E, 0x88,
						0xE5)));
				openItem.setWidth(dp2px(90));
				openItem.setTitle("查看");
				openItem.setTitleSize(25);
				openItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(openItem);

				// 删除
				// SwipeMenuItem deleteItem = new SwipeMenuItem(
				// getApplicationContext());
				// deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
				// 0x3F, 0x25)));
				// deleteItem.setWidth(dp2px(90));
				// deleteItem.setIcon(R.drawable.icon_swipe_delete);
				// menu.addMenuItem(deleteItem);

				// 删除
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				deleteItem.setWidth(dp2px(90));
				deleteItem.setTitle("删除");
				deleteItem.setTitleSize(25);
				deleteItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		routeList.setMenuCreator(creator);

		// step 2. listener item click event
		routeList.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu,
					int index) {
				switch (index) {
				case 0:
					// 打开
					Intent intent = new Intent(RouteListActivity.this,
							RouteShowActivity.class);
					intent.putExtra("filePath", fileNameList.get(position));
					startActivity(intent);
					overridePendingTransition(R.anim.zms_translate_left_out,
							R.anim.zms_translate_left_in);
					break;
				case 1:
					// 删除
					// TODO
					// File file = new File(ROUTE_PATH
					// + routeAdapter.getItem(position));
					File file = new File(ROUTE_PATH
							+ fileNameList.get(position));
					file.delete();
					DeleteUpdateList(position);
					try { // 若数据库存在轨迹距离信息，同步删除
						RouteDistanceDbHelper _db = new RouteDistanceDbHelper(
								getApplicationContext());
						_db.deleteRouteDistanceByName(fileNameList
								.get(position));
					} catch (Exception e) {
						Toast.makeText(getApplicationContext(), e.toString(),
								Toast.LENGTH_SHORT).show();

					}
					Toast.makeText(getApplicationContext(), "轨迹已删除",
							Toast.LENGTH_SHORT).show();
					break;
				}
				return false;
			}
		});

		// set SwipeListener
		routeList.setOnSwipeListener(new OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
				// swipe start

				// 若正在播放左滑动画，取消之
				hideSlideLeftArrow();
			}

			@Override
			public void onSwipeEnd(int position) {
				// swipe end
			}
		});

		// other setting
		// listView.setCloseInterpolator(new BounceInterpolator());

		// 长按提示左滑
		routeList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getApplicationContext(), "左滑更多选项",
						Toast.LENGTH_SHORT).show();
				showSlideLeftArrow();
				return true;
			}
		});

		// Swipe Menu END
	}

	private Animation slideLeftAnimation;
	private ImageView imageSlideLeft;

	private void showSlideLeftArrow() {
		slideLeftAnimation = AnimationUtils.loadAnimation(
				RouteListActivity.this, R.anim.route_list_slide_left);
		imageSlideLeft = (ImageView) findViewById(R.id.imageSlideLeft);

		imageSlideLeft.setVisibility(View.VISIBLE);
		imageSlideLeft.startAnimation(slideLeftAnimation);
	}

	private void hideSlideLeftArrow() {
		imageSlideLeft = (ImageView) findViewById(R.id.imageSlideLeft);
		imageSlideLeft.clearAnimation();
		imageSlideLeft.setVisibility(View.GONE);
	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnShowAll:
				showRouteList("20");
				tvFilterState.setText("轨迹未筛选");
				break;
			case R.id.btnToMainFromRouteList:
				backToMain();
				break;
			}
		}
	}

	private void showRouteList(String datePrefix) {
		try {
			tvNoFile.setVisibility(View.GONE);
			File[] files = new File(ROUTE_PATH).listFiles();
			fileNameList = new ArrayList<String>();
			routeArray = new ArrayList<RouteList>();
			for (File file : files) {
				String fileName = file.getName();
				String format = fileName.substring(
						fileName.lastIndexOf('.') + 1, fileName.length());
				if (format.equals("txt") || format.equals("json")
						|| format.equals("art"))
					if (file.getName().startsWith(datePrefix)) {
						// TODO
						fileNameList.add(file.getName());
						RouteList routeList = new RouteList(fileName.substring(
								0, fileName.lastIndexOf('-')),
								fileName.substring(
										fileName.lastIndexOf('-') + 1,
										fileName.lastIndexOf('.')));
						routeArray.add(routeList);
					}
			}

			routeList.setVisibility(View.VISIBLE);
			btnShowAll.setVisibility(View.INVISIBLE);
			tvNoFile.setVisibility(View.INVISIBLE);

			if (fileNameList.isEmpty()) {
				routeList.setVisibility(View.INVISIBLE);
				btnShowAll.setVisibility(View.VISIBLE);
				tvNoFile.setVisibility(View.VISIBLE);
				tvNoFile.setText("选定日期无轨迹");
			}
			// (context, resource, textViewResourceId, objects)
			// adapter = new ArrayAdapter<String>(this,
			// R.layout.route_list_item,
			// R.id.textStartTime, fileNameList);

			routeAdapter = new RouteListAdapter(getApplicationContext(),
					routeArray);
			routeList.setAdapter(routeAdapter);

			// 单击监听
			routeList
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(
								android.widget.AdapterView<?> parent,
								android.view.View view, int position, long id) {
							// 若正在播放左滑动画，取消之
							hideSlideLeftArrow();
							// focusItemPos = position;
							Intent intent = new Intent(RouteListActivity.this,
									RouteShowActivity.class);
							intent.putExtra("filePath",
									fileNameList.get(position));
							startActivity(intent);
							overridePendingTransition(
									R.anim.zms_translate_left_out,
									R.anim.zms_translate_left_in);
						}
					});

			// 长按弹出ContextMenu
			// registerForContextMenu(routeList);

		} catch (Exception e) {
			e.printStackTrace();
			tvNoFile.setVisibility(View.VISIBLE); // 无轨迹文件
			tvNoFile.setText("暂无轨迹文件" + e);
			btnShowAll.setVisibility(View.INVISIBLE);
			routeList.setVisibility(View.INVISIBLE);
		}
	}

	class MyDateChangeListener implements CalendarView.OnDateChangeListener {

		@Override
		public void onSelectedDayChange(CalendarView view, int year, int month,
				int dayOfMonth) {
			String strDate = "";
			String strDay = "" + dayOfMonth;
			month = month + 1;
			String strMonth = "" + month;
			if (month < 10)
				strMonth = "0" + month;
			if (dayOfMonth < 10)
				strDay = "0" + dayOfMonth;
			strDate = year + strMonth + strDay;
			tvFilterState.setText("显示" + year + "-" + strMonth + "-" + strDay
					+ "轨迹");

			showRouteList(strDate);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("选项");
		// add(int groupId, int itemId, int order,
		// CharSequence title)
		menu.add(0, 0, 0, "删除");
		menu.add(0, 1, 1, "编辑");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// ContextMenuInfo menuInfo = (ContextMenuInfo) item.getMenuInfo();
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		// 获得AdapterContextMenuInfo,以此来获得选择的listview项目

		switch (item.getItemId()) {
		case 0:
			// 删除
			File file = new File(ROUTE_PATH
					+ routeAdapter.getItem(menuInfo.position));
			file.delete();
			DeleteUpdateList(menuInfo.position);
			return true;
		case 1:
			// 编辑
			// Intent intent = new Intent("android.intent.action.VIEW");
			Intent intent = new Intent("android.intent.action.EDIT");
			// intent.addCategory("android.intent.category.DEFAULT");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Uri uri = Uri.fromFile(new File(ROUTE_PATH
					+ routeAdapter.getItem(menuInfo.position)));
			intent.setDataAndType(uri, "text/plain");
			startActivity(intent);
			return true;
		}
		return false;
	}

	/**
	 * 用ContextMenu删除Item时刷新ListView
	 * 
	 * @param position
	 */
	private void DeleteUpdateList(int position) {
		// TODO
		routeAdapter.remove(routeAdapter.getItem(position));
	}

	@Override
	protected void onResume() {
		super.onResume();
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
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

	// Swipe Menu START

	// private void delete(ApplicationInfo item) {
	// // delete app
	// try {
	// Intent intent = new Intent(Intent.ACTION_DELETE);
	// intent.setData(Uri.fromParts("package", item.packageName, null));
	// startActivity(intent);
	// } catch (Exception e) {
	// }
	// }

	// private void open(ApplicationInfo item) {
	// // open app
	// Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
	// resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	// resolveIntent.setPackage(item.packageName);
	// List<ResolveInfo> resolveInfoList = getPackageManager()
	// .queryIntentActivities(resolveIntent, 0);
	// if (resolveInfoList != null && resolveInfoList.size() > 0) {
	// ResolveInfo resolveInfo = resolveInfoList.get(0);
	// String activityPackageName = resolveInfo.activityInfo.packageName;
	// String className = resolveInfo.activityInfo.name;
	//
	// Intent intent = new Intent(Intent.ACTION_MAIN);
	// intent.addCategory(Intent.CATEGORY_LAUNCHER);
	// ComponentName componentName = new ComponentName(
	// activityPackageName, className);
	//
	// intent.setComponent(componentName);
	// startActivity(intent);
	// }
	// }

	// class AppAdapter extends BaseAdapter {
	//
	// @Override
	// public int getCount() {
	// return mAppList.size();
	// }
	//
	// @Override
	// public ApplicationInfo getItem(int position) {
	// return mAppList.get(position);
	// }
	//
	// @Override
	// public long getItemId(int position) {
	// return position;
	// }
	//
	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// if (convertView == null) {
	// convertView = View.inflate(getApplicationContext(),
	// R.layout.item_list_app, null);
	// new ViewHolder(convertView);
	// }
	// ViewHolder holder = (ViewHolder) convertView.getTag();
	// ApplicationInfo item = getItem(position);
	// holder.iv_icon.setImageDrawable(item.loadIcon(getPackageManager()));
	// holder.tv_name.setText(item.loadLabel(getPackageManager()));
	// return convertView;
	// }
	//
	// class ViewHolder {
	// ImageView iv_icon;
	// TextView tv_name;
	//
	// public ViewHolder(View view) {
	// iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
	// tv_name = (TextView) view.findViewById(R.id.tv_name);
	// view.setTag(this);
	// }
	// }
	// }

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	// Swipe Menu END
}
