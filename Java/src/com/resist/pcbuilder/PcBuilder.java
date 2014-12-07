package com.resist.pcbuilder;

import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.resist.pcbuilder.dashboard.AdminLoginHandler;
import com.resist.websocket.Connection;
import com.resist.websocket.ConnectionServer;
import com.resist.websocket.Message;
import com.resist.websocket.MessageHandler;

public class PcBuilder implements MessageHandler {
	private SearchHandler searchHandler;
	private MySQLConnection mysql;

	public static void main(String[] args) {
		//TODO settings uit een bestand inlezen
		JSONObject settings = new JSONObject();
		settings.put("address","145.24.222.119");
		settings.put("port",8080);
		settings.put("path","/search");
		settings.put("timeout",6*60*60*1000);
		settings.put("adminTimeout",30*60*1000);
		settings.put("adminPort",8081);
		settings.put("adminPath","/admin");
		settings.put("elasticCluster","elasticsearch");
		settings.put("elasticPort",9300);
		settings.put("mysqlAddress","localhost");
		settings.put("mysqlPort",3306);
		settings.put("mysqlDatabase","pcbuilder");
		settings.put("mysqlUsername","pcbuilder");
		settings.put("mysqlPassword","project");
		new PcBuilder(settings);
	}

	public PcBuilder(JSONObject settings) {
		searchHandler = new SearchHandler(settings.getString("address"), settings.getInt("elasticPort"), settings.getString("elasticCluster"));
		connectToMySQL(settings);
		listenForAdminConnections(settings);
		listenForConnections(settings);
	}

	public SearchHandler getSearchHandler() {
		return searchHandler;
	}

	public MySQLConnection getMysql() {
		return mysql;
	}

	private void connectToMySQL(JSONObject settings) {
		try {
			mysql = new MySQLConnection(settings.getString("mysqlAddress"),settings.getInt("mysqlPort"),settings.getString("mysqlDatabase"),settings.getString("mysqlUsername"),settings.getString("mysqlPassword"));
		} catch (SQLException e) {
			searchHandler.close();
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void listenForAdminConnections(JSONObject settings) {
		final ConnectionServer admin = new ConnectionServer(settings.getString("address"), settings.getInt("adminPort"), settings.getString("adminPath"))
			.setMessageHandler(new AdminLoginHandler(this))
			.setTimeout(settings.getInt("adminTimeout"));
		new Thread(new Runnable() {
			@Override
			public void run() {
				admin.manageConnections();
			}
		}).run();
	}

	private void listenForConnections(JSONObject settings) {
		new ConnectionServer(settings.getString("address"), settings.getInt("port"), settings.getString("path"))
			.setMessageHandler(this)
			.setTimeout(settings.getInt("timeout"))
			.manageConnections();
	}

	@Override
	public void handleMessage(Message message) {
		if(message.getType() == Connection.OPCODE_TEXT_FRAME) {
			JSONObject json = parseInput(message.toString());
			if(json != null) {
				JSONObject out = handleJSON(message,json);
				sendReturn(message,out.toString());
			}
		}
	}

	private JSONObject parseInput(String message) {
		try {
			return new JSONObject(message);
		} catch (JSONException e) {
			System.out.println(message);
			return null;
		}
	}

	private JSONObject handleJSON(Message message,JSONObject json) {
		JSONObject out = new JSONObject();

		if(json.has("term")) {
			out.put("resultaten", searchHandler.handleQuery(json));
		} else if(json.has("action") && json.get("action").equals("init")) {
			out.put("init",mysql.getInit());
		}

		return out;
	}

	private void sendReturn(Message conn, String message) {
		if(!conn.getConnection().isClosed()) {
			try {
				conn.getConnection().sendMessage(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
