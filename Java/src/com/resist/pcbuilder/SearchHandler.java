package com.resist.pcbuilder;

import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.json.JSONArray;
import org.json.JSONObject;

public class SearchHandler {
	private Client client;

	public SearchHandler(String address, int port, String clusterName) {
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", clusterName).build();
		client = new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(address,port));
	}

	public Client getClient() {
		return client;
	}

	public JSONArray handleQuery(JSONObject json) {
		SearchResponse response = client
				.prepareSearch("zoeker")

				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(
						QueryBuilders.matchQuery("model",
								json.getString("term"))).setFrom(0).setSize(60)
				.setExplain(true).execute().actionGet();

		SearchHit[] results = response.getHits().getHits();
		JSONArray resultaten = new JSONArray();
		System.out.println("Current results: " + results.length);
		for (SearchHit hit : results) {

			Map<String, Object> result = hit.getSource();
			resultaten.put(new JSONObject(result));
		}
		return resultaten;
	}

	public void close() {
		client.close();
	}
}
