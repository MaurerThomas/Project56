package com.resist.searchhandler;

import com.resist.websocket.Connection;
import com.resist.websocket.ConnectionServer;
import com.resist.websocket.Message;
import com.resist.websocket.MessageHandler;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class PcBuilder implements MessageHandler {
    private Client client;
    public static void main(String[] args) {
        new PcBuilder();
    }


    public PcBuilder() {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "elasticsearch").build();

                client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("145.24.222.119", 9300));

        new ConnectionServer("145.24.222.119",8080,"/search")
                .setMessageHandler(this)
                .setTimeout(24*60*60*1000).manageConnections();
    }

    @Override
    public void handleMessage(Message message) {
        if(message.getType() == Connection.OPCODE_TEXT_FRAME) {
            handleJSON(message);
        }
    }

    private void handleJSON(Message message) {
        System.out.println(message.toString());
        JSONObject json;
        try {
            json = new JSONObject(message.toString());
        } catch(JSONException e) {
            System.out.println("geen json");
            return;
        }
        String term = json.getString("term");


        SearchResponse response = client.prepareSearch("zoeker")

                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchQuery("model",term))
                .setFrom(0).setSize(60).setExplain(true)
                .execute()
                .actionGet();

        SearchHit[] results = response.getHits().getHits();
        JSONArray resultaten = new JSONArray();
        System.out.println("Current results: " + results.length);
        for (SearchHit hit : results){

                Map<String, Object> result = hit.getSource();
            resultaten.put(new JSONObject(result));
        }


        if(!message.getConnection().isClosed()) {
            String msg = resultaten.toString();
            System.out.println(msg);
            try {
                message.getConnection().sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
