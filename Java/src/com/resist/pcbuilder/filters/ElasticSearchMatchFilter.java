package com.resist.pcbuilder.filters;

import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.resist.pcbuilder.pcparts.PcPart;

public class ElasticSearchMatchFilter extends ElasticSearchFilter {
	private ElasticSearchMatchFilter(String key, Object value) {
		super(key, value);
	}

	@Override
	public MatchQueryBuilder getQuery() {
		return QueryBuilders.matchQuery(getKey(),String.valueOf(getValue()));
	}

	public static boolean isValidFilter(String key, Object value) {
		return PcPart.isValidMatchKey(key) && value instanceof String;
	}

	public static ElasticSearchMatchFilter getInstance(String key, Object value) {
		if(isValidFilter(key, value)) {
			return new ElasticSearchMatchFilter(key, value);
		}
		return null;
	}
}
