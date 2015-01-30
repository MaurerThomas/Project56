package com.resist.pcbuilder.pcparts;

import com.resist.pcbuilder.DBConnection;
import com.resist.pcbuilder.DatePrice;
import com.resist.pcbuilder.PcBuilder;
import org.elasticsearch.client.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class GraphicsCard extends PcPart {
    public static final String COMPONENT = "Grafische kaarten";

    private String socket;

    public GraphicsCard(int euro, int cent, Date crawlDate, Map<String, Object> specs) {
        super(euro, cent, crawlDate, specs);
        socket = (String) specs.get("Aansluiting");
        setSpec("interface", socket);
    }

    private GraphicsCard(String brand, String socket) {
        super(0, 0, null, null);
        this.socket = socket;
        setBrand(brand);
    }

    public String getSocket() {
        return socket;
    }

    public static boolean isPart(Map<String, Object> specs) {
        return COMPONENT.equals(specs.get("component")) && specs.containsKey("Aansluiting");
    }

    public static boolean isValidMatchKey(String key) {
        return key.equals("Aansluiting");
    }

    /**
     * Returns a list of sockets from the database.
     *
     * @param conn The database connection to use
     * @return A list of sockets
     */
    public static List<GraphicsCard> getSockets(Connection conn) {
        List<GraphicsCard> out = new ArrayList<GraphicsCard>();
        try {
            PreparedStatement s = conn.prepareStatement("SELECT " + DBConnection.COLUMN_INTERFACE_TYPE + " FROM " + DBConnection.TABLE_INTERFACE + " WHERE " + DBConnection.COLUMN_INTERFACE_PART + " = ?");
            s.setString(1, DBConnection.PART_GPU);
            ResultSet res = s.executeQuery();
            while (res.next()) {
                String type = res.getString(1);
                out.add(new GraphicsCard(null, type));
            }
            res.close();
            s.close();
        } catch (SQLException e) {
            PcBuilder.LOG.log(Level.WARNING, "Failed to get gpu sockets.", e);
        }
        return out;
    }

    /**
     * Returns a list of brands from the database.
     *
     * @param conn The database connection to use
     * @return A list of brands
     */
    public static List<GraphicsCard> getBrands(Connection conn) {
        List<GraphicsCard> out = new ArrayList<GraphicsCard>();
        try {
            PreparedStatement s = conn.prepareStatement("SELECT " + DBConnection.COLUMN_BRAND_NAME + " FROM " + DBConnection.TABLE_BRAND + " JOIN " + DBConnection.TABLE_JOIN + " ON (" + DBConnection.COLUMN_JOIN_MID + " = " + DBConnection.COLUMN_BRAND_MID + ") WHERE " + DBConnection.COLUMN_JOIN_TYPE + " = ?");
            s.setString(1, DBConnection.PART_GPU);
            ResultSet res = s.executeQuery();
            while (res.next()) {
                String naam = res.getString(1);
                out.add(new GraphicsCard(naam, null));
            }
            res.close();
            s.close();
        } catch (SQLException e) {
            PcBuilder.LOG.log(Level.WARNING, "Failed to get gpu brands.", e);
        }
        return out;
    }

    /**
     * Retrieves the average price of this component over time.
     *
     * @param client The client to find parts on
     * @param conn   The database connection to get prices from
     * @return A list of prices and dates
     */
    public static List<DatePrice> getAvgPrice(Client client, Connection conn) {
        return PcPart.getAvgPrice(client, conn, COMPONENT);
    }
}
