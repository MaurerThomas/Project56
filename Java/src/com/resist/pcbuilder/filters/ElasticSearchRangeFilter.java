package com.resist.pcbuilder.filters;

import java.util.List;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import com.resist.pcbuilder.pcparts.PcPart;

public class ElasticSearchRangeFilter extends ElasticSearchFilter {
	private ElasticSearchRangeFilter(String key, Object value) {
		super(key, value);
	}

	@Override
	public RangeQueryBuilder getQuery() {
		@SuppressWarnings("unchecked")
		List<Integer> range = (List<Integer>)getValue();
		return QueryBuilders.rangeQuery(getKey()).from(range.get(0)).to(range.get(1));
	}

	private static boolean isValidFilter(String key, Object value) {
		if(PcPart.isValidRangeKey(key) && value instanceof List) {
			List<?> l = (List<?>)value;
			return l.size() == 2 && l.get(0) instanceof Integer;
		}
		return false;
	}

	/**
	 * Attempts to construct a filter using a key-value pair.
	 * 
	 * @param key The key to filter on
	 * @param value The value to filter on
	 * @return A filter if the key-value pair was valid or null
	 */
	public static ElasticSearchRangeFilter getInstance(String key, Object value) {
		if(isValidFilter(key, value)) {
			return new ElasticSearchRangeFilter(key, value);
		}
		return null;
	}
}
