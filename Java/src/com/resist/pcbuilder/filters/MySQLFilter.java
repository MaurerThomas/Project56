package com.resist.pcbuilder.filters;


public abstract class MySQLFilter extends SearchFilter {
	protected MySQLFilter(String key, Object value) {
		super(key, value);
	}

	/**
	 * Attempts to construct a filter using a key-value pair.
	 * 
	 * @param key The key to filter on
	 * @param value The value to filter on
	 * @return A filter if the key-value pair was valid or null
	 */
	public static MySQLFilter getInstance(String key, Object value) {
		return MySQLPriceFilter.getInstance(key, value);
	}
}
