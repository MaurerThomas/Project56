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

public class Memory extends PcPart {
    public static final String COMPONENT = "Geheugen";

    private String socket;

    public Memory(int euro, int cent, Date crawlDate, Map<String, Object> specs) {
        super(euro, cent, crawlDate, specs);
    }

    private Memory(String socket) {
        super(0, 0, null, null);
        this.socket = socket;
    }

    public String getSocket() {
        return socket;
    }

    public static boolean isPart(Map<String, Object> specs) {
        return COMPONENT.equals(specs.get("component"));
    }

    /**
     * Returns a list of sockets from the database.
     *
     * @param conn The database connection to use
     * @return A list of sockets
     */
    public static List<Memory> getSockets(Connection conn) {
        List<Memory> out = new ArrayList<Memory>();
        try {
            PreparedStatement s = conn.prepareStatement("SELECT " + DBConnection.COLUMN_MEMORY_TYPE + " FROM " + DBConnection.TABLE_MEMORY);
            ResultSet res = s.executeQuery();
            while (res.next()) {
                String type = res.getString(1);
                out.add(new Memory(type));
            }
            res.close();
            s.close();
        } catch (SQLException e) {
            PcBuilder.LOG.log(Level.WARNING, "Failed to get ram interfaces.", e);
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
