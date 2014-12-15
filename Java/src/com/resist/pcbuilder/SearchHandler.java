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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchHandler {
	private Client client;
    private PcBuilder pcBuilder;
    //TODO Private maken
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

    public JSONArray handleIncomingMessage(JSONObject json) {
         if (json.has("filters")) {
                return handleQuery(json.getJSONArray("filters"));
        }


             return null;
    }



	public JSONArray handleQuery(JSONArray json) {

      // pcBuilder.getMysql();
        List<String> urls = new ArrayList<String>();

       if (json.length() > 0 && validateTerm(json,0)) {
           FilterBuilder filters = FilterBuilders.termsFilter(json.getJSONObject(0).getString("key"),json.getJSONObject(0).getString("value"));
           for(int i=1;i < json.length();i++) {
               if(validateTerm(json,i)) {
                   TermsFilterBuilder nieuw = FilterBuilders.termsFilter(json.getJSONObject(i).getString("key"), json.getJSONObject(i).getString("value"));
                   filters = FilterBuilders.andFilter(filters, nieuw);
               }
           }
                  SearchResponse response = client
                    .prepareSearch("zoeker")
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setFrom(0).setSize(60)

                            .setPostFilter(filters)
                            .setExplain(true).execute().actionGet();

            SearchHit[] results = response.getHits().getHits();
            JSONArray resultaten = new JSONArray();
            System.out.println("Current results: " + results.length);
            for (SearchHit hit : results) {
                Map<String, Object> result = hit.getSource();
                urls.add((String)result.get("url"));
                resultaten.put(new JSONObject(result));
            }
            return resultaten;
        }  else {
                return null;
            }
        }

    public boolean validateTerm(JSONArray ar, int i) {
        try {
            String val = ar.getJSONObject(i).getString("key");
            return val.equals("type") || val.equals("merk");
        } catch(JSONException e) {}
        return false;
    }

   	public void close() {
		client.close();
	}
}
