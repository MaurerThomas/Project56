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

	/**
	 * Attempts to construct a filter using a key-value pair.
	 * 
	 * @param key The key to filter on
	 * @param value The value to filter on
	 * @return A filter if the key-value pair was valid or null
	 */
	public static SearchFilter getInstance(String key, Object value) {
		SearchFilter out = MySQLFilter.getInstance(key,value);
		if(out == null) {
			return ElasticSearchFilter.getInstance(key, value);
		}
		return out;
	}
}
