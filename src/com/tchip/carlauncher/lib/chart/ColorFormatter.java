package com.tchip.carlauncher.lib.chart;

import com.tchip.carlauncher.lib.chart.Entry;

/**
 * Interface that can be used to return a customized color instead of setting
 * colors via the setColor(...) method of the DataSet.
 */
public interface ColorFormatter {

	public int getColor(Entry e, int index);
}