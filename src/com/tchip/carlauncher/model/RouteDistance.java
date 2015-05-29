package com.tchip.carlauncher.model;

public class RouteDistance {

	int _id; // 轨迹ID
	String _name; // 轨迹文件名（不含后缀）
	double _linear; // 起始点直线距离
	double _drive; // 起始点行驶距离

	public RouteDistance(String name, double linear, double drive) {
		_name = name;
		_linear = linear;
		_drive = drive;
	}

	public RouteDistance(int id, String name, double linear, double drive) {
		_id = id;
		_name = name;
		_linear = linear;
		_drive = drive;
	}

	public int getId() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public double getLinear() {
		return _linear;
	}

	public void setLinear(double linear) {
		this._linear = linear;
	}

	public double getDrive() {
		return _drive;
	}

	public void setDrive(double drive) {
		this._drive = drive;
	}
}
