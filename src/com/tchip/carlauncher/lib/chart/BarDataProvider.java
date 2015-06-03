package com.tchip.carlauncher.lib.chart;

import com.tchip.carlauncher.lib.chart.BarData;

public interface BarDataProvider extends BarLineScatterCandleDataProvider {

	public BarData getBarData();

	public boolean isDrawBarShadowEnabled();

	public boolean isDrawValueAboveBarEnabled();

	public boolean isDrawHighlightArrowEnabled();

	public boolean isDrawValuesForWholeStackEnabled();
}
