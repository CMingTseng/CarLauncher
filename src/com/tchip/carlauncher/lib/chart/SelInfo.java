package com.tchip.carlauncher.lib.chart;

import com.tchip.carlauncher.lib.chart.DataSet;

/**
 * Class that encapsulates information of a value that has been
 * selected/highlighted and its DataSet index. The SelInfo objects give
 * information about the value at the selected index and the DataSet it belongs
 * to. Needed only for highlighting onTouch().
 */
public class SelInfo {

	public float val;
	public int dataSetIndex;
	public DataSet<?> dataSet;

	public SelInfo(float val, int dataSetIndex, DataSet<?> set) {
		this.val = val;
		this.dataSetIndex = dataSetIndex;
		this.dataSet = set;
	}
}
