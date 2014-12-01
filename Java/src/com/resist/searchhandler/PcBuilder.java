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
import org.json.JSONException;
import org.json.JSONObject;

public class PcBuilder implements MessageHandler {
    private Client client;
    public static void main(String[] args) {
        new PcBuilder();
    }


    public PcBuilder() {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "elasticsearch").build();
        ConnectionServer server = new ConnectionServer("145.24.222.119",8080,"/search")
                .setMessageHandler(this)
                .setTimeout(24*60*60*1000);

        client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("145.24.222.119", 9300));



    }

    @Override
    public void handleMessage(Message message) {
        if(message.getType() == Connection.OPCODE_TEXT_FRAME) {
            handleJSON(message);
        }
    }

    private void handleJSON(Message message) {
        JSONObject jo = parseJSON(message.toString());
        try {
            json = new JSONObject(string);
        } catch(JSONException e) {
        }


        //res = query(jo);
        //JSONObject jr = new JSONObject(res);
        SearchResponse response = client.prepareSearch("zoeker")

                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchQuery())
                .setFrom(0).setSize(60).setExplain(true)
                .execute()
                .actionGet();
        if(!message.getConnection().isClosed()) {
            //message.getConnection().sendMessage(jr.toString());
        }
    }
}
