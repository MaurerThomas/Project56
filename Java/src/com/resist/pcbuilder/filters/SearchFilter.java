package com.resist.pcbuilder.filters;


public abstract class SearchFilter {
	private String key;
	private Object value;

	protected SearchFilter(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	protected String getKey() {
		return key;
	}

	protected Object getValue() {
		return value;
	}

	public static SearchFilter getInstance(String key, Object value) {
		SearchFilter out = MySQLFilter.getInstance(key,value);
		if(out == null) {
			return ElasticSearchFilter.getInstance(key, value);
		}
		return out;
	}
}
