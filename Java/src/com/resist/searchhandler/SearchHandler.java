package com.resist.searchhandler;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.util.Map;

public class SearchHandler {

    public static void main(String[] args)  {
    SearchHandler SearchHandler = new com.resist.searchhandler.SearchHandler();
    SearchHandler.connectWithClient();
    }

     public void connectWithClient()  {
         Settings settings = ImmutableSettings.settingsBuilder()
		.put("cluster.name", "elasticsearch").build();	
         Client client = new TransportClient(settings)
                 .addTransportAddress(new InetSocketTransportAddress("145.24.222.119", 9300));

         SearchResponse response = client.prepareSearch("zoeker")
                 
                 .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                 .setQuery(QueryBuilders.matchQuery("name", "gigabyte"))
                 .setFrom(0).setSize(60).setExplain(true)
                 .execute()
                 .actionGet();
        // on shutdown

         SearchHit[] results = response.getHits().getHits();

         System.out.println("Current results: " + results.length);
         for (SearchHit hit : results){
             System.out.println("--------------------------------");
             Map<String, Object> result = hit.getSource();
             System.out.println(result);
         }


         client.close();

      }
}
