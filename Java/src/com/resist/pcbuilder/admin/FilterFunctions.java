package com.resist.pcbuilder.admin;

import com.resist.pcbuilder.DBConnection;
import com.resist.pcbuilder.PcBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class FilterFunctions {

    public static boolean updateFilter(Connection conn, String category, String filtername, String newFiltername) {
        try {
            if (category.equals("Geheugen")) {
                PreparedStatement s = conn.prepareStatement("UPDATE " + DBConnection.TABLE_MEMORY + " SET " + DBConnection.COLUMN_MEMORY_TYPE + "= ? " + " WHERE " + DBConnection.COLUMN_MEMORY_TYPE + "= ?");
                s.setString(1, newFiltername);
                s.setString(2, filtername);
                s.executeUpdate();
                s.close();
            } else if (category.equals("Hardeschijf")) {
                PreparedStatement s = conn.prepareStatement("UPDATE " + DBConnection.TABLE_HDD + " SET " + DBConnection.COLUMN_HDD_INTERFACE + "= ? " + " WHERE " + DBConnection.COLUMN_HDD_INTERFACE + "= ?");
                s.setString(1, newFiltername);
                s.setString(2, filtername);
                s.executeUpdate();
                s.close();
            } else if (category.equals("Processor")) {
                PreparedStatement s = conn.prepareStatement("UPDATE  " + DBConnection.TABLE_SOCKET + " SET " + DBConnection.COLUMN_SOCKET_TYPE + "= ? " + " WHERE " + DBConnection.COLUMN_SOCKET_TYPE + "= ?");
                s.setString(1, newFiltername);
                s.setString(2, filtername);
                s.executeUpdate();
                s.close();
            } else if (category.equals("Grafischekaart")) {
                PreparedStatement s = conn.prepareStatement("UPDATE " + DBConnection.TABLE_INTERFACE + " SET " + DBConnection.COLUMN_INTERFACE_TYPE + "= ? " + " WHERE " + DBConnection.COLUMN_INTERFACE_TYPE + "= ?");
                s.setString(1, newFiltername);
                s.setString(2, filtername);
                s.executeUpdate();
                s.close();
            } else if (category.equals("Behuizing")) {
                PreparedStatement s = conn.prepareStatement("UPDATE " + DBConnection.TABLE_FORMFACTOR + " SET " + DBConnection.COLUMN_FORMFACTOR_FORMFACTOR + "= ? " + " WHERE " + DBConnection.COLUMN_FORMFACTOR_FORMFACTOR + "= ?");
                s.setString(1, newFiltername);
                s.setString(2, filtername);
                s.executeUpdate();
                s.close();
            }
            return true;
        } catch (SQLException e) {
            PcBuilder.LOG.log(Level.WARNING, "Failed to update the filter:" + newFiltername, e);
            return false;
        }
    }

    public static boolean createFilter(Connection conn, String category, String filtername) {
        try {
            if (category.equals("Geheugen")) {
                PreparedStatement s = conn.prepareStatement("INSERT INTO " + DBConnection.TABLE_MEMORY + "(" + DBConnection.COLUMN_MEMORY_TYPE + ")" + "VALUES(?)");
                s.setString(1, filtername);
                s.executeUpdate();
                s.close();
            } else if (category.equals("Hardeschijf")) {
                PreparedStatement s = conn.prepareStatement("INSERT INTO " + DBConnection.TABLE_HDD + "(" + DBConnection.COLUMN_HDD_INTERFACE + ")" + "VALUES(?)");
                s.setString(1, filtername);
                s.executeUpdate();
                s.close();
            } else if (category.equals("Processor")) {
                PreparedStatement s = conn.prepareStatement("INSERT INTO " + DBConnection.TABLE_SOCKET + "(" + DBConnection.COLUMN_SOCKET_TYPE + ")" + "VALUES(?)");
                s.setString(1, filtername);
                s.executeUpdate();
                s.close();
            } else if (category.equals("Grafischekaart")) {
                PreparedStatement s = conn.prepareStatement("INSERT INTO " + DBConnection.TABLE_INTERFACE + "(" + DBConnection.COLUMN_INTERFACE_TYPE + ")" + "VALUES(?)");
                s.setString(1, filtername);
                s.executeUpdate();
                s.close();
            } else if (category.equals("Behuizing")) {
                PreparedStatement s = conn.prepareStatement("INSERT INTO " + DBConnection.TABLE_FORMFACTOR + "(" + DBConnection.COLUMN_FORMFACTOR_FORMFACTOR + ")" + "VALUES(?)");
                s.setString(1, filtername);
                s.executeUpdate();
                s.close();
            }
            return true;
        } catch (SQLException e) {
            PcBuilder.LOG.log(Level.WARNING, "Failed to create the filter:" + filtername, e);
            return false;
        }
    }

    public static boolean deleteFilter(Connection conn, String category, String filtername) {
        try {
            if (category.equals("Geheugen")) {
                PreparedStatement s = conn.prepareStatement("DELETE FROM " + DBConnection.TABLE_MEMORY + " WHERE " + DBConnection.COLUMN_MEMORY_TYPE + " = ?");
                s.setString(1, filtername);
                s.executeUpdate();
                s.close();
            } else if (category.equals("Hardeschijf")) {
                PreparedStatement s = conn.prepareStatement("DELETE FROM " + DBConnection.TABLE_HDD + " WHERE " + DBConnection.COLUMN_HDD_INTERFACE + " = ?");
                s.setString(1, filtername);
                s.executeUpdate();
                s.close();
            } else if (category.equals("Processor")) {
                PreparedStatement s = conn.prepareStatement("DELETE FROM " + DBConnection.TABLE_SOCKET + " WHERE " + DBConnection.COLUMN_SOCKET_TYPE + " = ?");
                s.setString(1, filtername);
                s.executeUpdate();
                s.close();
            } else if (category.equals("Grafischekaart")) {
                PreparedStatement s = conn.prepareStatement("DELETE FROM " + DBConnection.TABLE_INTERFACE + " WHERE " + DBConnection.COLUMN_INTERFACE_TYPE + " = ?");
                s.setString(1, filtername);
                s.executeUpdate();
                s.close();
            } else if (category.equals("Behuizing")) {
                PreparedStatement s = conn.prepareStatement("DELETE FROM " + DBConnection.TABLE_FORMFACTOR + " WHERE " + DBConnection.COLUMN_FORMFACTOR_FORMFACTOR + " = ?");
                s.setString(1, filtername);
                s.executeUpdate();
                s.close();
            }
            return true;
        } catch (SQLException e) {
            PcBuilder.LOG.log(Level.WARNING, "Failed to delete the filter:" + filtername, e);
            return false;
        }
    }
}
