package com.tchip.carlauncher.model;

public class RouteList {
	String timeStart;
	String timeEnd;

	public String getTimeStart() {
		return timeStart;
	}

	public void setTimeStart(String timeStart) {
		this.timeStart = timeStart;
	}

	public String getTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(String timeEnd) {
		this.timeEnd = timeEnd;
	}

	public RouteList(String timeStart, String timeEnd) {
		super();
		this.timeStart = timeStart;
		this.timeEnd = timeEnd;
	}

}
