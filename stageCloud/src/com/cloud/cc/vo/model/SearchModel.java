package com.cloud.cc.vo.model;

public class SearchModel {

	@Override
	public String toString() {
		return "SearchModel [key=" + key + ", val=" + val + "]";
	}
	private String key;
	private String val;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
}
