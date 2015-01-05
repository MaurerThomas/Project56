package com.resist.pcbuilder;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.resist.pcbuilder.admin.AdminLoginHandler;
import com.resist.websocket.Connection;
import com.resist.websocket.ConnectionServer;
import com.resist.websocket.Message;
import com.resist.websocket.MessageHandler;

public class PcBuilder implements MessageHandler {
	public static final Logger LOG = Logger.getLogger(PcBuilder.class.getName());
	static {
		LOG.addHandler(new LogHandler());
		LOG.setLevel(Level.ALL);
		LOG.setUseParentHandlers(false);
	}

	private SearchHandler searchHandler;
	private MySQLConnection mysql;
	private JSONObject settings;
	private ConnectionServer adminServer;
	private ConnectionServer builderServer;

	public static void main(String[] args) {
		if (args.length > 0) {
			try {
				new PcBuilder(getSettingsFromFile(args[0]));
				return;
			} catch (IOException e) {
				LOG.log(Level.SEVERE, "Could not read settings file.", e);
			} catch (JSONException e) {
				LOG.log(Level.SEVERE, "Invalid settings file.", e);
			}
		} else {
			fatalError("No settings path specified.");
		}
	}

	private static JSONObject getSettingsFromFile(String path) throws IOException {
		FileReader reader = new FileReader(path);
		StringBuffer settings = new StringBuffer();
		int c = -1;
		while ((c = reader.read()) != -1) {
			settings.appendCodePoint(c);
		}
		reader.close();
		return new JSONObject(settings.toString());
	}

	public static void fatalError(String error) {
		LOG.log(Level.SEVERE,error);
		System.exit(1);
	}

	public PcBuilder(JSONObject settings) {
		if (!settingsArePresent(settings)) {
			fatalError("Invalid settings file.");
		}
		this.settings = settings;
		searchHandler = new SearchHandler(settings.getString("address"),
				settings.getInt("elasticPort"),
				settings.getString("elasticCluster"), this);
		connectToMySQL();
		listenForAdminConnections();
		listenForConnections();
		Runtime.getRuntime().addShutdownHook(new Thread(new PeacefulShutdown(this)));
		LOG.log(Level.INFO,"Started...");
	}

	private boolean settingsArePresent(JSONObject settings) {
		return settings.has("address") && settings.has("port")
				&& settings.has("path") && settings.has("adminPort")
				&& settings.has("adminPath") && settings.has("elasticPort")
				&& settings.has("elasticCluster")
				&& settings.has("mysqlAddress") && settings.has("mysqlPort")
				&& settings.has("mysqlDatabase");
	}

	public SearchHandler getSearchHandler() {
		return searchHandler;
	}

	public MySQLConnection getMysql() {
		return mysql;
	}

	public JSONObject getSettings() {
		return settings;
	}

	private void connectToMySQL() {
		try {
			mysql = new MySQLConnection(settings.getString("mysqlAddress"),
					settings.getInt("mysqlPort"),
					settings.getString("mysqlDatabase"),
					settings.getString("mysqlUsername"),
					settings.getString("mysqlPassword"));
			if (settings.has("adminPasswordSalt")) {
				mysql.setSalt(settings.getString("adminPasswordSalt"));
			}
		} catch (SQLException e) {
			searchHandler.close();
			LOG.log(Level.SEVERE, "Failed to connect to MySQL server.", e);
			System.exit(1);
		}
	}

	private void listenForAdminConnections() {
		final ConnectionServer admin = new ConnectionServer(
				settings.getString("address"), settings.getInt("adminPort"),
				settings.getString("adminPath"),
				LOG)
				.setMessageHandler(new AdminLoginHandler(this));
		if (settings.has("adminTimeout")) {
			admin.setTimeout(settings.getInt("adminTimeout"));
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				admin.manageConnections();
			}
		}).start();
		adminServer = admin;
	}

	private void listenForConnections() {
		final ConnectionServer user = new ConnectionServer(
				settings.getString("address"), settings.getInt("port"),
				settings.getString("path"), LOG).setMessageHandler(this);
		if (settings.has("timeout")) {
			user.setTimeout(settings.getInt("timeout"));
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				user.manageConnections();
			}
		}).start();
		builderServer = user;
	}

	@Override
	public void handleMessage(Message message) {
		if (message.getType() == Connection.OPCODE_TEXT_FRAME) {
			JSONObject json = parseInput(message.toString());
			if (json != null) {
				JSONObject out = handleJSON(message, json);
				sendReturn(message, out.toString());
			}
		}
	}

	private JSONObject parseInput(String message) {
		try {
			return new JSONObject(message);
		} catch (JSONException e) {
			LOG.log(Level.INFO, "Invalid JSON string.", e);
			return null;
		}
	}

	private JSONObject handleJSON(Message message, JSONObject json) {
		JSONObject out = new JSONObject();

		if (json.has("action") && json.getString("action").equals("filter")) {
			out.put("resultaten", searchHandler.handleIncomingMessage(json));
		} else if (json.has("action") && json.get("action").equals("init")) {
			out.put("init", mysql.getInit());
		}

		return out;
	}

	private void sendReturn(Message conn, String message) {
		if (!conn.getConnection().isClosed()) {
			conn.getConnection().sendMessage(message);
		}
	}

	public void stop() {
		builderServer.stop();
		adminServer.stop();
		searchHandler.close();
		mysql.close();
	}
}
