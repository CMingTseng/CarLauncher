package com.tchip.carlauncher.library.chart;

import com.tchip.carlauncher.library.chart.BarData;

public interface BarDataProvider extends BarLineScatterCandleDataProvider {

	public BarData getBarData();

	public boolean isDrawBarShadowEnabled();

	public boolean isDrawValueAboveBarEnabled();

	public boolean isDrawHighlightArrowEnabled();

	public boolean isDrawValuesForWholeStackEnabled();
}
