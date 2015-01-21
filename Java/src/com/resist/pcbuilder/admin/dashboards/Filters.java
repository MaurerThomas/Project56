package com.resist.pcbuilder.admin.dashboards;


import com.resist.pcbuilder.DBConnection;
import com.resist.pcbuilder.PcBuilder;
import com.resist.pcbuilder.SearchHandler;
import com.resist.pcbuilder.admin.AdminSession;
import com.resist.pcbuilder.admin.Dashboard;
import com.resist.pcbuilder.admin.OutputBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Created by Armindo on 9-1-2015.
 */
public class Filters implements Dashboard {
    public static final String IDENTIFIER = "filters";
    private AdminSession session;
    private SearchHandler initParts;
    private PcBuilder pcbuilder;

    public Filters(AdminSession session) {this.session = session;}

    @Override
    public JSONObject handleJSON(JSONObject input) {
        if(input.has("switchDashboard") && input.getString("switchDashboard").equals(IDENTIFIER)) {
            return new OutputBuilder().htmlTemplate("#main","dashboard_filters").getOutput();
        }
            //iets met return de getFilters functie hier
        return null;
    }

    public JSONObject getFilters() {
       return initParts.getInit();
    }

    public java.sql.Connection getConnection() {
        return pcbuilder.getDBConnection().getConnection();
    }

    public static boolean createFilter(Connection conn, String category, String filtername) {
        try {
            PreparedStatement s;
            if(category.equals("geheugen")) {
                s = conn.prepareStatement("INSERT INTO " + DBConnection.TABLE_MEMORY + "(" + DBConnection.COLUMN_MEMORY_TYPE + ")" + "VALUES(" + " ?)");
            } else if(category.equals("hardeschijven")) {
                s = conn.prepareStatement("INSERT INTO " + DBConnection.TABLE_HDD + "(" + DBConnection.COLUMN_HDD_INTERFACE + ")" + "VALUES(" + " ?)");
            } else if(category.equals("processors")){
                s = conn.prepareStatement("INSERT INTO " + DBConnection.TABLE_SOCKET + "(" + DBConnection.COLUMN_SOCKET_TYPE + ")" + "VALUES(" + " ?)");
            } else if(category.equals("grafischekaarten")) {
                s = conn.prepareStatement("INSERT INTO " + DBConnection.TABLE_INTERFACE + "(" + DBConnection.COLUMN_INTERFACE_TYPE + ")" + "VALUES(" + " ?)");
            } else {
                s = conn.prepareStatement("INSERT INTO " + DBConnection.TABLE_FORMFACTOR + "(" + DBConnection.COLUMN_FORMFACTOR_FORMFACTOR + ")" + "VALUES(" + " ?)");
            }
            s.setString(1, filtername);
            s.executeUpdate();
            s.close();
            return true;
        } catch (SQLException e) {
            PcBuilder.LOG.log(Level.WARNING,"Failed to delete the filter.",e);
            return false;
        }
    }

    public static boolean deleteFilter(Connection conn, String category, String filtername) {
        try {
            PreparedStatement s;
            if(category.equals("geheugen")) {
                s = conn.prepareStatement("DELETE FROM " + DBConnection.TABLE_MEMORY + " WHERE " + DBConnection.COLUMN_MEMORY_TYPE + " = ?");
            } else if(category.equals("hardeschijven")) {
                s = conn.prepareStatement("DELETE FROM " + DBConnection.TABLE_HDD + " WHERE " + DBConnection.COLUMN_HDD_INTERFACE + " = ?");
            } else if(category.equals("processors")) {
                s = conn.prepareStatement("DELETE FROM " + DBConnection.TABLE_SOCKET + " WHERE " + DBConnection.COLUMN_SOCKET_TYPE + " = ?");
            } else if(category.equals("grafischekaarten")) {
                s = conn.prepareStatement("DELETE FROM " + DBConnection.TABLE_INTERFACE + " WHERE " + DBConnection.COLUMN_INTERFACE_TYPE + " = ?");
            } else {
                s = conn.prepareStatement("DELETE FROM " + DBConnection.TABLE_FORMFACTOR + " WHERE " + DBConnection.COLUMN_FORMFACTOR_FORMFACTOR + " = ?");
            }
            s.setString(1, filtername);
            s.executeUpdate();
            s.close();
            return true;
        } catch (SQLException e) {
            PcBuilder.LOG.log(Level.WARNING,"Failed to delete the filter.",e);
            return false;
        }
    }


}
