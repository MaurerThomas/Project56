package com.resist.pcbuilder;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.elasticsearch.search.SearchHit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class SearchHandler {
	private Client client;
	private PcBuilder pcBuilder;

	public SearchHandler(String address, int port, String clusterName, PcBuilder pcBuilder) {
		this.pcBuilder = pcBuilder;
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();
		client = new TransportClient(settings);
		((TransportClient) client).addTransportAddress(new InetSocketTransportAddress(address, port));
	}

	public JSONArray handleIncomingMessage(JSONObject json) {
		if (json.has("filters")) {
			return handleQuery(json.getJSONArray("filters"));
		}
		if (json.has("makechart")){
			return getPartsPriceForGraph(json.getJSONArray("makechart"));
		}
		return null;
	}

	private JSONArray handleQuery(JSONArray json) {
		FilterBuilder filters = getElasticFilters(json);
		if (filters != null) {
			List<String> urls = new ArrayList<String>();
			JSONObject results = elasticSearchQuery(filters, 20, urls);
			if(!urls.isEmpty()) {
				Map<String,Integer> mysqlFilters = getMinMaxPrice(json);
				java.sql.Date sqlDate = getPastSQLDate(24 * 60 * 60 * 1000);
				return combineMysqlResultsWithElasticsearch(results, pcBuilder.getMysql().getPartsPrice(urls, sqlDate, mysqlFilters.get("minPrice"), mysqlFilters.get("maxPrice")));
			}
		}
		return null;
	}

	private JSONArray getPartsPriceForGraph(JSONArray json){
		FilterBuilder filters = getElasticFilters(json);
		if (filters != null) {
			List<String> urls = new ArrayList<String>();
			JSONObject results = elasticSearchQuery(filters, 1, urls);
			if(!urls.isEmpty()) {
				java.sql.Date sqlDate = getPastSQLDate(24 * 60 * 60 * 7 * 1000);
				return combineMysqlResultsWithElasticsearch(results, pcBuilder.getMysql().getPartsPrice(urls, sqlDate, null, null));
			}
		}
		return null;
	}

	private java.sql.Date getPastSQLDate(long ms) {
		java.util.Date utilDate = new java.util.Date();
		return new java.sql.Date(utilDate.getTime() - ms);
	}

	private JSONObject elasticSearchQuery(FilterBuilder filters, int numResults, List<String> urls) {
		JSONObject output = new JSONObject();
		SearchResponse response = client.prepareSearch("mongoindex")
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setSize(numResults)
				.setPostFilter(filters)
				.setExplain(false)
				.execute()
				.actionGet();
		SearchHit[] results = response.getHits().getHits();
		for (SearchHit hit : results) {
			@SuppressWarnings("unchecked")
			Map<String, Object> result = (Map<String, Object>) hit.getSource().get("specs");
			String url = (String) result.get("url");
			urls.add(url);
			output.put(url, new JSONObject(result));
		}
		return output;
	}

	private FilterBuilder getElasticFilters(JSONArray json) {
		FilterBuilder filters = null;
		for (int i = 0; i < json.length(); i++) {
			JSONObject filter = json.getJSONObject(i);
			if (validateTerm(filter)) {
				filters = appendElasticFilter(filter,filters);
			}
		}
		return filters;
	}

	private FilterBuilder appendElasticFilter(JSONObject filter, FilterBuilder filters) {
		String key = filter.getString("key");
		String value = filter.getString("value");
		if(filters == null) {
			return FilterBuilders.termsFilter(key,value);
		}
		TermsFilterBuilder part = FilterBuilders.termsFilter(key,value);
		return FilterBuilders.andFilter(filters, part);
	}

	private Map<String,Integer> getMinMaxPrice(JSONArray json) {
		Map<String,Integer> filters = new HashMap<String,Integer>();
		for(int i=0;i < json.length();i++) {
			JSONObject filter = json.getJSONObject(i);
			addMinMaxPrice(filter,filters);
		}
		return filters;
	}

	private void addMinMaxPrice(JSONObject filter, Map<String,Integer> filters) {
		String key = filter.getString("key");
		if(key.equals("minPrice") || key.equals("maxPrice")) {
			filters.put(key, filter.getInt("value"));

		}
	}

	private JSONArray combineMysqlResultsWithElasticsearch(JSONObject elasticsearch, JSONArray mysql) {

		for (int i = 0; i < mysql.length(); i++) {
			JSONObject mysqlElement = mysql.getJSONObject(i);
			JSONObject elasticElement = elasticsearch.getJSONObject(mysqlElement.getString("url"));
			@SuppressWarnings("unchecked")
			Iterator<String> it = elasticElement.keys();
			while(it.hasNext()) {
				String key = it.next();
				mysqlElement.put(key, elasticElement.get(key));
			}
		}
		System.out.println(mysql.toString().length());

		return mysql;
	}

	private boolean validateTerm(JSONObject term) {
		try {
			String val = term.getString("key");
			return val.equals("component") || val.equals("merk");
		} catch (JSONException e) {
			return false;
		}
	}

	public void close() {
		client.close();
	}
}
