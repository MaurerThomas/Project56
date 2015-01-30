package com.resist.pcbuilder.filters;

import com.resist.pcbuilder.pcparts.PcPart;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class ElasticSearchMatchFilter extends ElasticSearchFilter {
    private ElasticSearchMatchFilter(String key, Object value) {
        super(key, value);
    }

    @Override
    public MatchQueryBuilder getQuery() {
        return QueryBuilders.matchQuery(getKey(), String.valueOf(getValue()));
    }

    private static boolean isValidFilter(String key, Object value) {
        return PcPart.isValidMatchKey(key) && value instanceof String;
    }

    /**
     * Attempts to construct a filter using a key-value pair.
     *
     * @param key   The key to filter on
     * @param value The value to filter on
     * @return A filter if the key-value pair was valid or null
     */
    public static ElasticSearchMatchFilter getInstance(String key, Object value) {
        if (isValidFilter(key, value)) {
            return new ElasticSearchMatchFilter(key, value);
        }
        return null;
    }
}
