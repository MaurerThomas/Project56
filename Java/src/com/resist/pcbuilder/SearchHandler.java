package com.resist.pcbuilder;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchHandler {
	private Client client;
    private PcBuilder pcBuilder;

	public SearchHandler(String address, int port, String clusterName, PcBuilder pcBuilder) {
        this.pcBuilder = pcBuilder;
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", clusterName).build();
		client = new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(address,port));
	}

	public Client getClient() {
		return client;
	}

    public JSONArray handleMethod(JSONObject json) {

        if (json.has("type") && json.has("merk")) {
            System.out.println("Multimatch query");
            handleQuery(json.getString("type"), json);
        } else {
            System.out.println("matchQuery");
            if (json.has("type")) {
                return handleComponentType(json.getString("type"), json);
            }
        }
        return null;
    }


	public JSONArray handleQuery(String text,JSONObject json) {
        int lengteJson = json.length();
        List<String> fields = new ArrayList<>();

        System.out.println(lengteJson);
        System.out.println(json);

        //Moederbord
        if (json.has("type") && json.has("merk")){
          fields.add("type");
          fields.add("merk");
        }
        System.out.println(fields);

        if (lengteJson > 2) {

                  SearchResponse response = client
                    .prepareSearch("zoeker")
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(
                            QueryBuilders.multiMatchQuery(text,fields.toArray(new String[fields.size()]))).setFrom(0).setSize(60)
                    .setExplain(true).execute().actionGet();

            SearchHit[] results = response.getHits().getHits();
            JSONArray resultaten = new JSONArray();
            System.out.println("Current results: " + results.length);
            for (SearchHit hit : results) {
                Map<String, Object> result = hit.getSource();
                resultaten.put(new JSONObject(result));
            }
            return resultaten;
        }  else {
                return null;
            }
        }

    public JSONArray handleComponentType(String type, JSONObject json){


        SearchResponse response = client
                .prepareSearch("zoeker")

                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(
                        QueryBuilders.matchQuery("type",
                                type)).setFrom(0).setSize(60)
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
