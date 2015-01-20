package com.resist.pcbuilder.filters;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;


public abstract class ElasticSearchFilter extends SearchFilter {
	protected ElasticSearchFilter(String key, Object value) {
		super(key, value);
	}

	public QueryBuilder getQuery() {
		return null;
	}

	public static ElasticSearchFilter getInstance(String key, Object value) {
		ElasticSearchFilter out = ElasticSearchMatchFilter.getInstance(key, value);
		if(out == null) {
			return ElasticSearchRangeFilter.getInstance(key, value);
		}
		return out;
	}

	public static QueryBuilder buildFilters(List<SearchFilter> filters) {
		BoolQueryBuilder out = null;
		for(SearchFilter filter : filters) {
			if(filter instanceof ElasticSearchFilter) {
				QueryBuilder query = ((ElasticSearchFilter)filter).getQuery();
				if(out == null) {
					out = QueryBuilders.boolQuery().must(query);
				} else {
					out.must(query);
				}
			}
		}
		return out;
	}
}
