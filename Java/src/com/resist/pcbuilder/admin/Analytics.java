package com.resist.pcbuilder.admin;

import com.resist.pcbuilder.DBConnection;
import com.resist.pcbuilder.PcBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Thomas
 */
public class Analytics {
    private int hashcodes;
    private Date datum;

    public int getHashcodes() {
        return hashcodes;
    }

    public Date getDatum() {
        return datum;
    }

    public Analytics(int hashcodes, Date datum) {
        this.hashcodes = hashcodes;
        this.datum = datum;
    }

    public static void insertVisitors(int hashcode, DBConnection dbConnection) {
        Connection conn = dbConnection.getConnection();
        try {
            PreparedStatement s = conn.prepareStatement("INSERT INTO " + DBConnection.TABLE_ANALYTICS + "(" + DBConnection.COLUMN_ANALYTICS_HASHCODE + "," + DBConnection.COLUMN_ANALYTICS_DATUM + ")VALUES(?,?)");
            s.setInt(1, hashcode);
            s.setDate(2, new java.sql.Date(new java.util.Date().getTime()));
            s.executeUpdate();
            s.close();
        } catch (SQLException e) {
            PcBuilder.LOG.log(Level.WARNING, "Failed to insert visitor.", e);
        }
    }

    public static List<Analytics> getVisitors(DBConnection dbConnection) {
        Connection conn = dbConnection.getConnection();
        List<Analytics> out = new ArrayList<Analytics>();
        try {
            Statement s = conn.createStatement();
            ResultSet res = s.executeQuery("SELECT COUNT" + "(" + DBConnection.COLUMN_ANALYTICS_DATUM + ")" + " , " + DBConnection.COLUMN_ANALYTICS_DATUM + " FROM " + DBConnection.TABLE_ANALYTICS + " GROUP BY " + DBConnection.COLUMN_ANALYTICS_DATUM);
            while (res.next()) {
                out.add(new Analytics(res.getInt(1), res.getDate(2)));
            }
            res.close();
            s.close();
        } catch (SQLException e) {
            PcBuilder.LOG.log(Level.WARNING, "Failed to get analytics data.", e);
        }
        return out;
    }
}
