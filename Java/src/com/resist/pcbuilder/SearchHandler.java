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

    public JSONObject handleIncomingMessage(JSONObject json) {
         if (json.has("filters")) {
                return handleQuery(json.getJSONArray("filters"));
        }
             return null;
    }

	public JSONObject handleQuery(JSONArray json) {
       List<String> urls = new ArrayList<String>();

       java.util.Date utilDate = new java.util.Date();
       java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime()-24*60*60*1000);

       if (json.length() > 0 && validateTerm(json,0)) {
           FilterBuilder filters = FilterBuilders.termsFilter(json.getJSONObject(0).getString("key"),json.getJSONObject(0).getString("value"));
           for(int i=1;i < json.length();i++) {
               if(validateTerm(json,i)) {
                   TermsFilterBuilder nieuw = FilterBuilders.termsFilter(json.getJSONObject(i).getString("key"), json.getJSONObject(i).getString("value"));
                   filters = FilterBuilders.andFilter(filters, nieuw);
               }
           }
           System.out.println(filters);
                  SearchResponse response = client
                    .prepareSearch("mongoindex")
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setFrom(0).setSize(60)
                            .setPostFilter(filters)
                            .setExplain(true).execute().actionGet();

            SearchHit[] results = response.getHits().getHits();
           JSONObject resultaten = new JSONObject();
            System.out.println("Current results: " + results.length);
            for (SearchHit hit : results) {
                Map<String, Object> result = (Map<String, Object>)hit.getSource().get("specs");
                String url = (String)result.get("url");
                urls.add(url);
                resultaten.put(url,new JSONObject(result));
            }
           System.out.println(urls);
             resultaten = combineMysqlResultsWithElasticsearch(resultaten, pcBuilder.getMysql().getPartsPrice(urls, sqlDate, null, null));
            return resultaten;
        }  else {
                return null;
            }
        }

    public JSONObject combineMysqlResultsWithElasticsearch(JSONObject elasticsearch,JSONArray mysql){


        for (int i = 0; i < mysql.length(); i++) {
            JSONObject element = mysql.getJSONObject(i);
            JSONObject element2 = elasticsearch.getJSONObject(element.getString("url"));
            element2.put("euro",element.getInt("euro"));
            element2.put("cent",element.getInt("cent"));
         }
        System.out.println("JSON object van elasticsearch combiner" +elasticsearch);

        return elasticsearch;
    }


    public boolean validateTerm(JSONArray ar, int i) {
        try {
            String val = ar.getJSONObject(i).getString("key");
            return val.equals("component") || val.equals("merk");
        } catch(JSONException e) {}
        return false;
    }

   	public void close() {
		client.close();
	}
}
