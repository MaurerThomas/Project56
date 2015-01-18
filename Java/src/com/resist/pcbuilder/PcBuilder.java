package com.resist.pcbuilder;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.json.JSONException;
import org.json.JSONObject;

import com.resist.pcbuilder.admin.AdminLoginHandler;
import com.resist.websocket.ConnectionServer;

public class PcBuilder {
	public static final Logger LOG = Logger.getLogger(PcBuilder.class.getName());
	static {
		LOG.addHandler(new LogHandler());
		LOG.setLevel(Level.ALL);
		LOG.setUseParentHandlers(false);
	}

	private SearchHandler searchHandler;
	private DBConnection conn;
	private Client searchClient;
	private JSONObject settings;
	private ConnectionServer adminServer;
	private ConnectionServer builderServer;

	public static void main(String[] args) {
		if (args.length > 0) {
			try {
				new PcBuilder(getSettingsFromFile(args[0]));
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
		initSearch(settings.getString("address"),settings.getInt("elasticPort"),settings.getString("elasticCluster"));
		searchHandler = new SearchHandler(this);
		connectToMySQL();
		listenForAdminConnections();
		listenForConnections();
		Runtime.getRuntime().addShutdownHook(new Thread(new PeacefulShutdown(this)));
		LOG.log(Level.INFO,"Started...");
	}

	public SearchHandler getSearchHandler() {
		return searchHandler;
	}

	public DBConnection getDBConnection() {
		return conn;
	}

	public JSONObject getSettings() {
		return settings;
	}

	public Client getSearchClient() {
		return searchClient;
	}

	private boolean settingsArePresent(JSONObject settings) {
		return settings.has("address") && settings.has("port")
				&& settings.has("path") && settings.has("adminPort")
				&& settings.has("adminPath") && settings.has("elasticPort")
				&& settings.has("elasticCluster")
				&& settings.has("mysqlAddress") && settings.has("mysqlPort")
				&& settings.has("mysqlDatabase");
	}

	private void initSearch(String address, int port, String clusterName) {
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();
		searchClient = new TransportClient(settings);
		((TransportClient) searchClient).addTransportAddress(new InetSocketTransportAddress(address, port));
	}

	private void connectToMySQL() {
		try {
			conn = new DBConnection(settings.getString("mysqlAddress"),
					settings.getInt("mysqlPort"),
					settings.getString("mysqlDatabase"),
					settings.getString("mysqlUsername"),
					settings.getString("mysqlPassword"));
		} catch (SQLException e) {
			searchClient.close();
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
				settings.getString("path"), LOG).setMessageHandler(new InputHandler(this));
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

	public void stop() {
		builderServer.stop();
		adminServer.stop();
		searchClient.close();
		conn.close();
	}
}
