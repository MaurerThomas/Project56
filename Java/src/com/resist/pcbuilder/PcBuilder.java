package com.resist.pcbuilder;

import com.resist.pcbuilder.admin.AdminLoginHandler;
import com.resist.websocket.ConnectionServer;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PcBuilder {
    /**
     * The search index used by ElasticSearch.
     */
    public static final String MONGO_SEARCH_INDEX = "mongoindex";
    /**
     * The logger to use throughout the program.
     */
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

    /**
     * The program's entry point.
     *
     * @param args An array of arguments; the first argument must contain a path to the settings file to use
     */
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
            LOG.log(Level.SEVERE, "No settings path specified.");
            System.exit(1);
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

    public PcBuilder(JSONObject settings) {
        if (!settingsArePresent(settings)) {
            LOG.log(Level.SEVERE, "Invalid settings file.");
            System.exit(1);
        }
        this.settings = settings;
        initSearch(settings.getString("address"), settings.getInt("elasticPort"), settings.getString("elasticCluster"));
        searchHandler = new SearchHandler(this);
        connectToMySQL();
        listenForAdminConnections();
        listenForConnections();
        Runtime.getRuntime().addShutdownHook(new Thread(new PeacefulShutdown(this)));
        LOG.log(Level.INFO, "Started...");
    }

    /**
     * Retrieves the object that handles calls to ElasticSearch.
     *
     * @return The search handler
     */
    public SearchHandler getSearchHandler() {
        return searchHandler;
    }

    /**
     * Retrieves the object that handles calls to MySQL.
     *
     * @return The database connection
     */
    public DBConnection getDBConnection() {
        return conn;
    }

    /**
     * Retrieves the settings parsed from the file path the program was started with.
     *
     * @return The PC Builder's settings
     */
    public JSONObject getSettings() {
        return settings;
    }

    /**
     * Retrieves the connection to ElasticSearch.
     *
     * @return The search client
     */
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
        ConnectionServer admin = new ConnectionServer(
                settings.getString("address"), settings.getInt("adminPort"),
                settings.getString("adminPath"), LOG)
                .setMessageHandler(new AdminLoginHandler(this));
        if (settings.has("adminTimeout")) {
            admin.setTimeout(settings.getInt("adminTimeout"));
        }
        startOnNewThread(admin);
        adminServer = admin;
    }

    private void listenForConnections() {
        ConnectionServer user = new ConnectionServer(
                settings.getString("address"), settings.getInt("port"),
                settings.getString("path"), LOG)
                .setMessageHandler(new InputHandler(this));
        if (settings.has("timeout")) {
            user.setTimeout(settings.getInt("timeout"));
        }
        startOnNewThread(user);
        builderServer = user;
    }

    private void startOnNewThread(final ConnectionServer cs) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                cs.manageConnections();
            }
        }).start();
    }

    /**
     * Attempts to stop the various parts of the program in a sensible order.
     */
    public void stop() {
        builderServer.stop();
        adminServer.stop();
        searchClient.close();
        conn.close();
    }
}
