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

        if(json.has("type")) {
            return handleComponentType(json.getString("type"),json);
        }

      //  PreparedStatement s = pcBuilder.getMysql().("SELECT * FROM prijs_verloop");


        String field = "";
        String[] fields = null;

        if (json.has("model")){
            field = "model";
        }
        System.out.println(field);

        //Moederbord
        if (json.has("merk") && json.has("socket")){
            fields = new String[]{"merk","socket"};
        }
        //Processor
        if (json.has("merk") && json.has("socket") ){
            fields = new String[]{"merk","socket"};
        }
        //Harde schijf
        if (json.has("type") && json.has("aansluiting")){
            fields = new String[]{"type","aansluiting"};
        }
        //Grafische kaart
        if (json.has("merk") && json.has("aansluiting")){
            fields = new String[]{"merk","aansluiting"};
        }

        if (lengteJson == 1) {

            SearchResponse response = client
                    .prepareSearch("zoeker")

                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(
                            QueryBuilders.matchQuery(field,
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
        } else {

            SearchResponse response = client
                    .prepareSearch("zoeker")

                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(
                            QueryBuilders.multiMatchQuery(fields,
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
