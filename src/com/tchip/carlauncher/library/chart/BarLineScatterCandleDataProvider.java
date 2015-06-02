package com.tchip.carlauncher.library.chart;

import com.tchip.carlauncher.library.chart.YAxis.AxisDependency;
import com.tchip.carlauncher.library.chart.Transformer;

public interface BarLineScatterCandleDataProvider extends ChartInterface {

	public Transformer getTransformer(AxisDependency axis);

	public int getMaxVisibleCount();

	public boolean isInverted(AxisDependency axis);

	public int getLowestVisibleXIndex();

	public int getHighestVisibleXIndex();
}
