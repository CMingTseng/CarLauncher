package com.tchip.carlauncher;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

/**
 * Created by AlexZhou on 2015/4/8. 15:07
 */
public class SetBrandActivity extends Activity {
	private ListView listView;
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		setContentView(R.layout.set_brand);
		sharedPreferences = getSharedPreferences("RouteSetting",
				getApplicationContext().MODE_PRIVATE);
		BrandAdapter adapter = new BrandAdapter(getApplicationContext());
		/*
		 * listView = (ListView) findViewById(R.id.brandList);
		 * listView.setAdapter(adapter); listView.setOnItemClickListener(new
		 * MyOnItemClickListener());
		 */
		GridView brandGrid = (GridView) findViewById(R.id.brandGrid);
		brandGrid.setAdapter(adapter);
		brandGrid.setOnItemClickListener(new MyOnItemClickListener());
		// int brandNow = sharedPreferences.getInt("brand", 1);
		// if (brandNow > 4) {
		// brandGrid.setSelection(brandNow - 4);
		// } else {
		// brandGrid.setSelection(brandNow - 1);
		// }
	}

	class MyOnItemClickListener implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putInt("brand", position + 1);
			editor.commit();
			finish();
		}
	}
}
