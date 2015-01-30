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

public class HardDisk extends PcPart {
    public static final String COMPONENT = "Harde schijven intern";

    private String type;
    private String socket;

    public HardDisk(int euro, int cent, Date crawlDate, Map<String, Object> specs) {
        super(euro, cent, crawlDate, specs);
        socket = cleanInterfaceString((String) specs.get("Interface"));
        setSpec("interface", socket);
        type = getComponent();
    }

    private String cleanInterfaceString(String socket) {
        return socket.replaceAll("u'", " ").replace(')', ' ').replace('(', ' ').replace('\'', ' ').replace("Intern ,  ", "");
    }

    private HardDisk(String type, String socket) {
        super(0, 0, null, null);
        this.type = type;
        this.socket = socket;
    }

    public String getType() {
        return type;
    }

    public String getSocket() {
        return socket;
    }

    public static boolean isPart(Map<String, Object> specs) {
        return COMPONENT.equals(specs.get("component")) && specs.containsKey("Interface");
    }

    public static boolean isValidMatchKey(String key) {
        return key.equals("Interface");
    }

    /**
     * Returns a list of HDD types and interfaces from the database.
     *
     * @param conn The database connection to use
     * @return A list of types and interfaces
     */
    public static List<HardDisk> getHardDisks(Connection conn) {
        List<HardDisk> out = new ArrayList<HardDisk>();
        try {
            PreparedStatement s = conn.prepareStatement("SELECT " + DBConnection.COLUMN_HDD_TYPE + "," + DBConnection.COLUMN_HDD_INTERFACE + " FROM " + DBConnection.TABLE_HDD);
            ResultSet res = s.executeQuery();
            while (res.next()) {
                String type = res.getString(1);
                String aansluiting = res.getString(2);
                out.add(new HardDisk(type, aansluiting));
            }
            res.close();
            s.close();
        } catch (SQLException e) {
            PcBuilder.LOG.log(Level.WARNING, "Failed to get hard disks.", e);
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
