package com.resist.pcbuilder.filters;


public abstract class MySQLFilter extends SearchFilter {
	protected MySQLFilter(String key, Object value) {
		super(key, value);
	}

	public static MySQLFilter getInstance(String key, Object value) {
		return MySQLPriceFilter.getInstance(key, value);
	}
}
