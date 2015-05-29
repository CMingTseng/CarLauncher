package com.tchip.carlauncher.model;

public class RoutePoint {
	private double lng; // longitude 经度
	private double lat; // latitude 纬度

	@Override
	public String toString() {
		return "[lng=" + lng + ", lat=" + lat + "]";
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}
}
