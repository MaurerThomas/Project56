package com.resist.pcbuilder.admin.dashboards;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import org.json.JSONObject;

import com.resist.pcbuilder.DBConnection;
import com.resist.pcbuilder.PcBuilder;
import com.resist.pcbuilder.admin.AdminSession;
import com.resist.pcbuilder.admin.Dashboard;
import com.resist.pcbuilder.admin.OutputBuilder;

/**
 * Created by Armindo on 9-1-2015.
 */
public class Filters implements Dashboard {
    public static final String IDENTIFIER = "filters";
    private AdminSession session;
    private PcBuilder pcbuilder;

    public Filters(AdminSession session) {
        this.session = session;

    }


    @Override
    public JSONObject handleJSON(JSONObject input) {
        if(input.has("switchDashboard") && input.getString("switchDashboard").equals(IDENTIFIER)) {
            System.out.println(input);
            return new OutputBuilder().htmlTemplate("#main","dashboard_filters").getOutput();
        } else if(input.has("action")) {
            return handleActions(input);
        }
        return null;
    }
    private JSONObject handleActions(JSONObject input) {
        String action = input.getString("action");
        System.out.println(action);
        if(action.equals("init")) {
            return session.getPcBuilder().getSearchHandler().getInit();
        } else if (action.equals("updatefilter")) {
            return updateFilter(getConnection(), input.getString("cat"), input.getString("sub"), input.getString("newname"));
        } else if (action.equals("deletefilter")) {
            return deleteFilter(getConnection(), input.getString("sub"), input.getString("newname"));
        }else if (action.equals("createfilter")) {
            return createFilter(getConnection(), input.getString("cat"), input.getString("newname"));
        }
        return null;
    }

    public java.sql.Connection getConnection() {
        return pcbuilder.getDBConnection().getConnection();
    }

    public static boolean updateFilter(Connection conn, String category, String filtername, String newFiltername) {
        try {
            PreparedStatement s;
            if(category.equals("geheugen")) {
                s = conn.prepareStatement("UPDATE "+ DBConnection.TABLE_MEMORY +" SET " + DBConnection.COLUMN_MEMORY_TYPE + "=" + " ? " + " WHERE "+ DBConnection.COLUMN_MEMORY_TYPE + "= \'?\'");
            } else if(category.equals("hardeschijven")) {
                s = conn.prepareStatement("UPDATE "+ DBConnection.TABLE_HDD +" SET " + DBConnection.COLUMN_HDD_INTERFACE + "=" + " ? " + " WHERE "+ DBConnection.COLUMN_HDD_INTERFACE + "= \'?\'");
            } else if(category.equals("processors")) {
                s = conn.prepareStatement("UPDATE  "+ DBConnection.TABLE_SOCKET +" SET " + DBConnection.COLUMN_SOCKET_TYPE + "=" + " ? " + " WHERE "+ DBConnection.COLUMN_SOCKET_TYPE + "= \'?\'");
            } else if(category.equals("grafischekaarten")) {
                s = conn.prepareStatement("UPDATE "+ DBConnection.TABLE_INTERFACE +" SET " + DBConnection.COLUMN_INTERFACE_TYPE + "=" + " ? " + " WHERE "+ DBConnection.COLUMN_INTERFACE_TYPE + "= \'?\'");
            } else {
                s = conn.prepareStatement("UPDATE "+ DBConnection.TABLE_FORMFACTOR +" SET " + DBConnection.COLUMN_FORMFACTOR_FORMFACTOR + "=" + " ? " + " WHERE "+ DBConnection.COLUMN_FORMFACTOR_FORMFACTOR + "= \'?\'");
            }
            s.setString(1, newFiltername);
            s.setString(2, filtername);
            s.executeUpdate();
            s.close();
            return true;
        } catch (SQLException e) {
            PcBuilder.LOG.log(Level.WARNING,"Failed to update the filter:" + newFiltername,e);
            return false;
        }
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
            PcBuilder.LOG.log(Level.WARNING,"Failed to create the filter:" + filtername,e);
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
            PcBuilder.LOG.log(Level.WARNING,"Failed to delete the filter:" + filtername,e);
            return false;
        }
    }


}
