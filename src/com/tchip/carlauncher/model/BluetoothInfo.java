package com.tchip.carlauncher.model;

public class BluetoothInfo {

	String address;
	String name;
	int state;

	public BluetoothInfo(String address, String name, int state) {
		super();
		this.address = address;
		this.name = name;
		this.state = state;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
