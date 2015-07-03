package com.tchip.carlauncher.model;

public class WifiInfo {
	public WifiInfo(String macAddress, String name, String authType, int signal) {
		super();
		this.macAddress = macAddress;
		this.name = name;
		this.authType = authType;
		this.signal = signal;
	}

	/**
	 * BSSID
	 */
	String macAddress;

	/**
	 * SSID
	 */
	String name;

	/**
	 * capabilities
	 */
	String authType;

	/**
	 * level
	 */
	int signal;

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public int getSignal() {
		return signal;
	}

	public void setSignal(int signal) {
		this.signal = signal;
	}

}
