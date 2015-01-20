package com.resist.pcbuilder.filters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLPriceFilter extends MySQLFilter {
	private MySQLPriceFilter(String key, Object value) {
		super(key, value);
	}

	public Map<String,Integer> toMap() {
		Map<String,Integer> out = new HashMap<String,Integer>();
		@SuppressWarnings("unchecked")
		List<Integer> priceRange = (List<Integer>)getValue();
		out.put("minPrice",priceRange.get(0));
		out.put("maxPrice",priceRange.get(1));
		return out;
	}

	public static boolean isValidFilter(String key, Object value) {
		if(key.equals("price") && value instanceof List) {
			List<?> l = (List<?>)value;
			return l.size() == 2 && l.get(0) instanceof Integer;
		}
		return false;
	}

	public static MySQLFilter getInstance(String key, Object value) {
		if(isValidFilter(key, value)) {
			return new MySQLPriceFilter(key, value);
		}
		return null;
	}
}
