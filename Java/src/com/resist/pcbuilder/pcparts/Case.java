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

public class Case extends PcPart {
	public static final String COMPONENT = "Behuizingen";

	private String formFactor;
	private String afmetingen;
	private String psuHeight;
	private String psuWidth;
	private String gpuHeight;
	private String gpuWidth;
	private String coolerHeight;

	public Case(int euro, int cent, Date crawlDate, Map<String,Object> specs) {
		super(euro,cent,crawlDate,specs);
		formFactor = (String) specs.get("Formaat");
		afmetingen = (String) specs.get("Afmetingen (BxHxD)");
		psuWidth = (String) specs.get("max, lengte voeding");
		psuHeight = (String) specs.get("max, hoogte voeding");
		gpuWidth = (String) specs.get("max, lengte grafische kaart");
		gpuHeight = (String) specs.get("max, hoogte grafische kaart");
		coolerHeight = (String) specs.get("max, hoogte CPU koeler");
		setSpec("formFactor",formFactor);
		setSpec("afmetingen",afmetingen);
		setSpec("max. voeding lengte",psuWidth);
		setSpec("max. voeding hoogte",psuHeight);
		setSpec("max. videokaart lengte",gpuHeight);
		setSpec("max. videokaart hoogte",gpuWidth);
		setSpec("max. processorkoeler hoogte",coolerHeight);
	}

	private Case(String formFactor) {
		super(0,0,null,null);
		this.formFactor = formFactor;
	}

	public String getFormFactor() {
		return formFactor;
	}

	public String getAfmetingen() {
		return  afmetingen;
	}

	public static boolean isPart(Map<String, Object> specs) {
		return COMPONENT.equals(specs.get("component")) && specs.containsKey("Formaat");
	}

	public static boolean isValidMatchKey(String key) {
		return key.equals("Formaat");
	}

	/**
	 * Returns a list of form factors from the database.
	 * 
	 * @param conn The database connection to use
	 * @return A list of form factors
	 */
	public static List<Case> getFormFactors(Connection conn) {
		List<Case> out = new ArrayList<Case>();
		try {
	        PreparedStatement s = conn.prepareStatement("SELECT "+DBConnection.COLUMN_FORMFACTOR_FORMFACTOR+" FROM "+DBConnection.TABLE_FORMFACTOR);
	        ResultSet res = s.executeQuery();
	        while(res.next()) {
	            String formfactor = res.getString(1);
	            out.add(new Case(formfactor));
	        }
	        res.close();
	        s.close();
		} catch (SQLException e) {
			PcBuilder.LOG.log(Level.WARNING,"Failed to get case form factors.",e);
		}
        return out;
	}

	/**
	 * Retrieves the average price of this component over time.
	 * 
	 * @param client The client to find parts on
	 * @param conn The database connection to get prices from
	 * @return A list of prices and dates
	 */
    public static List<DatePrice> getAvgPrice(Client client, Connection conn) {
        return PcPart.getAvgPrice(client,conn,COMPONENT);
    }
}
