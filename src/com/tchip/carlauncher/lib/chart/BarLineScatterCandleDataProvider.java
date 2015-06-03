package com.tchip.carlauncher.lib.chart;

import com.tchip.carlauncher.lib.chart.Transformer;
import com.tchip.carlauncher.lib.chart.YAxis.AxisDependency;

public interface BarLineScatterCandleDataProvider extends ChartInterface {

	public Transformer getTransformer(AxisDependency axis);

	public int getMaxVisibleCount();

	public boolean isInverted(AxisDependency axis);

	public int getLowestVisibleXIndex();

	public int getHighestVisibleXIndex();
}
