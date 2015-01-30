package com.resist.pcbuilder.filters;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.List;


public abstract class ElasticSearchFilter extends SearchFilter {
    protected ElasticSearchFilter(String key, Object value) {
        super(key, value);
    }

    public QueryBuilder getQuery() {
        return null;
    }

    /**
     * Attempts to construct a filter using a key-value pair.
     *
     * @param key   The key to filter on
     * @param value The value to filter on
     * @return A filter if the key-value pair was valid or null
     */
    public static ElasticSearchFilter getInstance(String key, Object value) {
        ElasticSearchFilter out = ElasticSearchMatchFilter.getInstance(key, value);
        if (out == null) {
            return ElasticSearchRangeFilter.getInstance(key, value);
        }
        return out;
    }

    /**
     * Converts a list of filters to an ElasticSearch query.
     *
     * @param filters A list of filters to turn into a query
     * @return An ElasticSearch query or null if the list contained no valid filters
     */
    public static QueryBuilder buildFilters(List<SearchFilter> filters) {
        BoolQueryBuilder out = null;
        for (SearchFilter filter : filters) {
            if (filter instanceof ElasticSearchFilter) {
                QueryBuilder query = ((ElasticSearchFilter) filter).getQuery();
                if (out == null) {
                    out = QueryBuilders.boolQuery().must(query);
                } else {
                    out.must(query);
                }
            }
        }
        return out;
    }
}
