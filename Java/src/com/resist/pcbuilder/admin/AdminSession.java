package com.resist.pcbuilder.admin;

import com.resist.pcbuilder.PcBuilder;
import com.resist.pcbuilder.admin.dashboards.AdminFunctions;
import com.resist.pcbuilder.admin.dashboards.Filters;
import com.resist.pcbuilder.admin.dashboards.Grafieken;
import com.resist.pcbuilder.admin.dashboards.Systeem;
import com.resist.websocket.Connection;
import com.resist.websocket.Message;
import com.resist.websocket.MessageHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A MessageHandler that handles incoming WebSocket messages from logged in admins.
 */
public class AdminSession implements MessageHandler {
    private AdminLoginHandler loginHandler;
    private String username;
    private Dashboard currentDashboard;
    private Map<String, Dashboard> dashboards;

    public AdminSession(AdminLoginHandler loginHandler, Message message) {
        this.loginHandler = loginHandler;
        this.username = new JSONObject(message.toString()).getJSONObject("login").getString("username");
        initDashboards();
        initSession(message.getConnection());
    }

    /**
     * Provides the username of the logged in admin.
     *
     * @return The username of the admin
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the connection to MySQL.
     *
     * @return The database connection
     */
    public java.sql.Connection getConnection() {
        return loginHandler.getConnection();
    }

    /**
     * Retrieves the PC Builder.
     *
     * @return The PC Builder running this session
     */
    public PcBuilder getPcBuilder() {
        return loginHandler.getPcBuilder();
    }

    /**
     * Hashes a password.
     *
     * @param password The password to hash
     * @return The hashed password
     */
    public String getPasswordHash(String password) {
        if (password == null) {
            return null;
        }
        return loginHandler.getPasswordHash(password);
    }

    private void initDashboards() {
        dashboards = new HashMap<String, Dashboard>();
        dashboards.put("main", null);
        dashboards.put(AdminFunctions.IDENTIFIER, new AdminFunctions(this));
        dashboards.put(Grafieken.IDENTIFIER, new Grafieken(this));
        dashboards.put(Filters.IDENTIFIER, new Filters(this));
        dashboards.put(Systeem.IDENTIFIER, new Systeem(this));
    }

    private void initSession(Connection conn) {
        new OutputBuilder().replace("username", username).htmlTemplate("#main", "index").send(conn);
    }

    @Override
    public void handleMessage(Message message) {
        if (message.getType() == Connection.OPCODE_TEXT_FRAME) {
            JSONObject json = parseJSON(message.toString());
            if (json != null) {
                JSONObject out = handleJSON(json);
                if (out != null) {
                    sendReturn(message.getConnection(), out.toString());
                }
            }
        }
    }

    private JSONObject parseJSON(String message) {
        try {
            return new JSONObject(message);
        } catch (JSONException e) {
            return null;
        }
    }

    private JSONObject handleJSON(JSONObject input) {
        if (input.has("switchDashboard") && dashboards.containsKey(input.getString("switchDashboard"))) {
            currentDashboard = dashboards.get(input.getString("switchDashboard"));
            if (currentDashboard == null) {
                return new OutputBuilder().htmlTemplate("#main", "index").getOutput();
            }
        }
        if (currentDashboard == null) {
            return null;
        }
        return currentDashboard.handleJSON(input);
    }

    private void sendReturn(Connection conn, String message) {
        if (!conn.isClosed()) {
            conn.sendMessage(message);
        }
    }
}
