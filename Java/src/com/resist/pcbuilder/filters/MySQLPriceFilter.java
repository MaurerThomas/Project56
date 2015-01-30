package com.resist.pcbuilder.filters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLPriceFilter extends MySQLFilter {
    private MySQLPriceFilter(String key, Object value) {
        super(key, value);
    }

    /**
     * Returns a map representation of this filter.
     *
     * @return A map containing minPrice and maxPrice keys
     */
    public Map<String, Integer> toMap() {
        Map<String, Integer> out = new HashMap<String, Integer>();
        @SuppressWarnings("unchecked")
        List<Integer> priceRange = (List<Integer>) getValue();
        out.put("minPrice", priceRange.get(0));
        out.put("maxPrice", priceRange.get(1));
        return out;
    }

    private static boolean isValidFilter(String key, Object value) {
        if (key.equals("price") && value instanceof List) {
            List<?> l = (List<?>) value;
            return l.size() == 2 && l.get(0) instanceof Integer;
        }
        return false;
    }

    /**
     * Attempts to construct a filter using a key-value pair.
     *
     * @param key   The key to filter on
     * @param value The value to filter on
     * @return A filter if the key-value pair was valid or null
     */
    public static MySQLFilter getInstance(String key, Object value) {
        if (isValidFilter(key, value)) {
            return new MySQLPriceFilter(key, value);
        }
        return null;
    }
}
