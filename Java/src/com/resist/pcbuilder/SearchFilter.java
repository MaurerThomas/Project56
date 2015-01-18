package com.resist.pcbuilder;

public class SearchFilter {
	private String key;
	private String value;

	public SearchFilter(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
