package com.tchip.carlauncher.ui.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.tchip.carlauncher.lib.chart.Easing;
import com.tchip.carlauncher.lib.chart.Entry;
import com.tchip.carlauncher.lib.chart.Legend;
import com.tchip.carlauncher.lib.chart.LineChart;
import com.tchip.carlauncher.lib.chart.LineData;
import com.tchip.carlauncher.lib.chart.LineDataSet;
import com.tchip.carlauncher.lib.chart.YAxis;
import com.tchip.carlauncher.model.TrafficDateTrafficModel;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.util.TrafficProgressDialogUtils;
import com.tchip.carlauncher.util.TrafficUtils;
import com.tchip.carlauncher.view.TrafficMyMarkerView;

import java.text.SimpleDateFormat;
import java.util.*;

public class TrafficChartActivity extends Activity {

        public static final String TAG_TITLE = "title";
        public static final String TAG_UID = "uid";
        public static final int DATA_SIZE = 5;

        private int uid;
        private LineChart mChart;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.trafftic_chart_activity);
                initView();
                new FetchDataTask().execute();
        }

        private void initView() {
                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                        if (!TextUtils.isEmpty(bundle.getString(TAG_TITLE))) {
                                setTitle(bundle.getString(TAG_TITLE));
                        }
                        uid = bundle.getInt(TAG_UID, 0);
                } else {
                        Toast.makeText(this, "数据异常", Toast.LENGTH_SHORT).show();
                        finish();
                }

                mChart = (LineChart) findViewById(R.id.chart1);
                mChart.setNoDataTextDescription("未加载数�?");
        }

        private void initChart() {
                //                mChart.setOnChartGestureListener(this);
                //                mChart.setOnChartValueSelectedListener(this);
                mChart.setDrawGridBackground(false);
                mChart.setDescription("");
                mChart.setHighlightEnabled(true);
                mChart.setTouchEnabled(true);
                mChart.setDragEnabled(true);
                mChart.setScaleEnabled(true);
                // mChart.setScaleXEnabled(true);
                // mChart.setScaleYEnabled(true);
                mChart.setPinchZoom(true);

                TrafficMyMarkerView mv = new TrafficMyMarkerView(this, R.layout.trafftic_custom_marker_view);
                mChart.setMarkerView(mv);
                mChart.setHighlightIndicatorEnabled(false);

                mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
                Legend l = mChart.getLegend();
                l.setForm(Legend.LegendForm.LINE);
        }

        private void setData(List<TrafficDateTrafficModel> datas) {

                ArrayList<String> xVals = new ArrayList<String>();
                for (int i = 0; i < datas.size(); i++) {
                        xVals.add(new SimpleDateFormat("M.d").format(new Date(datas.get(i).getDate())));
                }

                ArrayList<Entry> yVals = new ArrayList<Entry>();

                for (int i = 0; i < datas.size(); i++) {

                        yVals.add(new Entry(TrafficUtils.dataToMB(datas.get(i).getTraffic()), i));
                }

                // create a dataset and give it a type
                LineDataSet set1 = new LineDataSet(yVals, DATA_SIZE + "天流量情况（单位：MB�?");
                // set1.setFillAlpha(110);
                // set1.setFillColor(Color.RED);

                // set the line to be drawn like this "- - - - - -"
                set1.enableDashedLine(10f, 5f, 0f);
                set1.setColor(Color.BLACK);
                set1.setCircleColor(Color.BLACK);
                set1.setLineWidth(1f);
                set1.setCircleSize(3f);
                set1.setDrawCircleHole(false);
                set1.setValueTextSize(9f);
                set1.setFillAlpha(65);
                set1.setFillColor(Color.BLACK);
                //        set1.setDrawFilled(true);
                // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
                // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

                ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
                dataSets.add(set1); // add the datasets

                // create a data object with the datasets
                LineData data = new LineData(xVals, dataSets);

                YAxis leftAxis = mChart.getAxisLeft();
                leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
                //                leftAxis.addLimitLine(ll1);
                //                leftAxis.addLimitLine(ll2);
                Collections.sort(datas, new Comparator<TrafficDateTrafficModel>() {
                        @Override
                        public int compare(TrafficDateTrafficModel lhs, TrafficDateTrafficModel rhs) {
                                return (int) (rhs.getTraffic() - lhs.getTraffic());
                        }
                });
                leftAxis.setAxisMaxValue(TrafficUtils.dataToMB(datas.get(0).getTraffic()) + 10.0f);
                leftAxis.setAxisMinValue(0.0f);
                leftAxis.setStartAtZero(false);
                leftAxis.enableGridDashedLine(10f, 10f, 0f);

                // limit lines are drawn behind data (and not on top)
                leftAxis.setDrawLimitLinesBehindData(true);

                mChart.getAxisRight().setEnabled(false);

                // set data
                mChart.setData(data);
        }

        private class FetchDataTask extends AsyncTask<Void, Void, List<TrafficDateTrafficModel>> {
                TrafficProgressDialogUtils pDlgUtl;

                @Override
                protected void onPreExecute() {
                        if (pDlgUtl == null) {
                                pDlgUtl = new TrafficProgressDialogUtils(TrafficChartActivity.this);
                                pDlgUtl.show();
                        }
                }

                @Override
                protected List<TrafficDateTrafficModel> doInBackground(Void... params) {
                        return TrafficUtils.fetchDayTrrafic(TrafficChartActivity.this, uid, DATA_SIZE);
                }

                @Override
                protected void onPostExecute(List<TrafficDateTrafficModel> datas) {
                        initChart();
                        setData(datas);
                        if (pDlgUtl != null) {
                                pDlgUtl.hide();
                        }
                        //                        mChart.animateXY(1000, 1000);
                }
        }
}
