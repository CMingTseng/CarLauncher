package com.tchip.carlauncher.model;

public class NaviHistory {

	int id; // 导航历史ID
	String key; // 导航关键字

	public NaviHistory(String key) {
		super();
		this.key = key;
	}

	public NaviHistory(int id, String key) {
		super();
		this.id = id;
		this.key = key;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
