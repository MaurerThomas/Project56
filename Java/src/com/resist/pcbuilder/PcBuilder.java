package com.resist.pcbuilder;

import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.resist.websocket.Connection;
import com.resist.websocket.ConnectionServer;
import com.resist.websocket.Message;
import com.resist.websocket.MessageHandler;

public class PcBuilder implements MessageHandler {
	private SearchHandler searchHandler;
	private MySQLConnection mysql;

	public static void main(String[] args) {
		new PcBuilder("145.24.222.119", 8080, "/search", "elasticsearch", "pcbuilder", "pcbuilder", "project");
	}

	public PcBuilder(String address, int port, String path, String elasticCluster,String mysqlDatabase, String mysqlUsername, String mysqlPassword) {
		searchHandler = new SearchHandler(address, port, elasticCluster);
		try {
			mysql = new MySQLConnection(address,port,mysqlDatabase,mysqlUsername,mysqlPassword);
		} catch (SQLException e) {
			searchHandler.close();
			e.printStackTrace();
			System.exit(1);
		}
		new ConnectionServer(address, port, path).setMessageHandler(this)
				.setTimeout(24 * 60 * 60 * 1000).manageConnections();
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
		} catch (JSONException e) {
			System.out.println("geen json");
			return;
		}
		JSONObject out = new JSONObject();
		if(json.has("term")) {
			out.put("resultaten", searchHandler.handleQuery(json));
		} else if(json.has("action") && json.get("action").equals("init")) {
			out.put("init",mysql.getInit());
		}

		if(!message.getConnection().isClosed()) {
			String msg = out.toString();
			System.out.println(msg);
			try {
				message.getConnection().sendMessage(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
