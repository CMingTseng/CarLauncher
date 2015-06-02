package com.tchip.carlauncher.library.chart;

import android.content.Context;
import android.util.AttributeSet;

import com.tchip.carlauncher.library.chart.CandleData;
import com.tchip.carlauncher.library.chart.CandleDataProvider;
import com.tchip.carlauncher.library.chart.CandleStickChartRenderer;

public class CandleStickChart extends BarLineChartBase<CandleData> implements
		CandleDataProvider {

	public CandleStickChart(Context context) {
		super(context);
	}

	public CandleStickChart(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CandleStickChart(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void init() {
		super.init();

		mRenderer = new CandleStickChartRenderer(this, mAnimator,
				mViewPortHandler);
		mXChartMin = -0.5f;
	}

	@Override
	protected void calcMinMax() {
		super.calcMinMax();

		mXChartMax += 0.5f;
		mDeltaX = Math.abs(mXChartMax - mXChartMin);
	}

	@Override
	public CandleData getCandleData() {
		return mData;
	}
}
