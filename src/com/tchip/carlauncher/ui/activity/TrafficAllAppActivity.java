package com.tchip.carlauncher.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.tchip.carlauncher.model.TrafficAppModel;
import com.tchip.carlauncher.model.TrafficViewHolder;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.util.TrafficAndroidUtils;
import com.tchip.carlauncher.util.TrafficProgressDialogUtils;
import com.tchip.carlauncher.util.TrafficUtils;

import java.util.List;
public class TrafficAllAppActivity extends Activity {

        private ListView app_list;
        private List<TrafficAppModel> dataList;
        private boolean isFirstIn;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.trafftic_allapp_activity);

                /**
                 * 控制加载对话框显�?
                 */
                isFirstIn = true;

                initView();

                /**
                 * 获得流量数据
                 */
                new FetchAllAppDataTask().execute();

                /**
                 * 监听流量更新
                 */
                IntentFilter mFilter = new IntentFilter();
                mFilter.addAction(TrafficUtils.ACTION_UPDATE_TRAFFIC);
                registerReceiver(mReceiver, mFilter);
        }

        @Override
        protected void onDestroy() {
                super.onDestroy();
                unregisterReceiver(mReceiver);
        }

        private void initView() {
                app_list = TrafficAndroidUtils.findViewById(this, R.id.app_list);

                app_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Bundle b = new Bundle();
                                b.putString(TrafficChartActivity.TAG_TITLE, dataList.get(position).getAppName());
                                b.putInt(TrafficChartActivity.TAG_UID, dataList.get(position).getUid());
                                TrafficAndroidUtils.startActivity(TrafficAllAppActivity.this, TrafficChartActivity.class, b);
                        }
                });
        }

        private BroadcastReceiver mReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                        new FetchAllAppDataTask().execute();
                }
        };

        private class FetchAllAppDataTask extends AsyncTask<Void, Void, List<TrafficAppModel>> {

                TrafficProgressDialogUtils pDlgUtl;

                @Override
                protected void onPreExecute() {
                        if (pDlgUtl == null && isFirstIn) {
                                isFirstIn = false;
                                pDlgUtl = new TrafficProgressDialogUtils(TrafficAllAppActivity.this);
                                pDlgUtl.show();
                        }
                }

                @Override
                protected List<TrafficAppModel> doInBackground(Void... params) {
                        return TrafficUtils.getAllAppTraffic(TrafficAllAppActivity.this);
                }

                @Override
                protected void onPostExecute(List<TrafficAppModel> datas) {
                        dataList = datas;
                        ListAdapter adapter = app_list.getAdapter();
                        if (adapter != null
                                && app_list.getAdapter() instanceof AllAppAdapter) {
                                ((AllAppAdapter) adapter).notifyDataSetChanged();
                        } else {
                                app_list.setAdapter(new AllAppAdapter());
                        }
                        if (pDlgUtl != null) {
                                pDlgUtl.hide();
                        }
                        Log.i("TAG", "FetchAllAppDataTask():onPostExecute() 界面更新");
                }
        }

        private class AllAppAdapter extends BaseAdapter {

                @Override
                public int getCount() {
                        return dataList.size();
                }

                @Override
                public TrafficAppModel getItem(int position) {
                        return dataList.get(position);
                }

                @Override
                public long getItemId(int position) {
                        return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                        if (convertView == null) {
                                convertView = getLayoutInflater().from(TrafficAllAppActivity.this)
                                        .inflate(R.layout.trafftic_allapp_item, null);
                        }
                        TextView app_name = TrafficViewHolder.get(convertView, R.id.app_name);
                        TextView app_traffic = TrafficViewHolder.get(convertView, R.id.app_traffic);
                        ImageView app_icon = TrafficViewHolder.get(convertView, R.id.app_icon);

                        TrafficAppModel item = getItem(position);
                        app_name.setText(item.getAppName());
                        app_traffic.setText(TrafficUtils.dataSizeFormat(item.getTraffic()));

                        try {
                                PackageManager pManager = getPackageManager();
                                Drawable icon = pManager.getApplicationIcon(item.getPkgName());
                                app_icon.setImageDrawable(icon);
                        } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                        }

                        return convertView;
                }
        }

}
