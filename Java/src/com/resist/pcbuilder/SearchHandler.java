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

	public JSONArray handleQuery(JSONObject json) {
        int lengteJson = json.length();
        String zoek1 = "";
        String zoek2 = "";
        System.out.println(lengteJson);
        System.out.println(json);

        //Moederbord
        if (json.has("type") && json.has("model")){
            zoek1 = "type" + "" + "model";

        }
        System.out.println(zoek1);



        if (lengteJson > 2) {
            System.out.println("Multimatch query");
                  SearchResponse response = client
                    .prepareSearch("zoeker")

                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(
                            QueryBuilders.multiMatchQuery("type" + "" + "model",zoek1)).setFrom(0).setSize(60)
                    .setExplain(true).execute().actionGet();

            SearchHit[] results = response.getHits().getHits();
            JSONArray resultaten = new JSONArray();
            System.out.println("Current results: " + results.length);
            for (SearchHit hit : results) {

                Map<String, Object> result = hit.getSource();
                resultaten.put(new JSONObject(result));
            }
            return resultaten;
        } else {
            System.out.println("matchQuery");
            if(json.has("type")) {
                return handleComponentType(json.getString("type"),json);

            } else {
                return null;
            }

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
