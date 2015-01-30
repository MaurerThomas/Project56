package com.resist.pcbuilder.pcparts;

import com.resist.pcbuilder.DatePrice;
import org.elasticsearch.client.Client;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import java.util.Map;

public class Motherboard extends PcPart {
    public static final String COMPONENT = "Moederborden";

    private String socket;
    private String formFactor;

    public Motherboard(int euro, int cent, Date crawlDate, Map<String, Object> specs) {
        super(euro, cent, crawlDate, specs);
        socket = (String) specs.get("Socket");
        formFactor = (String) specs.get("formFactor");
        setSpec("socket", socket);
        setSpec("formFactor", formFactor);
    }

    public String getSocket() {
        return socket;
    }

    public static boolean isPart(Map<String, Object> specs) {
        return COMPONENT.equals(specs.get("component"));
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
