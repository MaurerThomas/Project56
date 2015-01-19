package com.resist.pcbuilder.pcparts;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.resist.pcbuilder.DBConnection;
import com.resist.pcbuilder.PcBuilder;
import com.resist.pcbuilder.SearchFilter;

public class Case extends PcPart {
	public static final String COMPONENT = "Behuizingen";

	private String formFactor;

	public Case(int euro, int cent, Date crawlDate, Map<String,Object> specs) {
		super(euro,cent,crawlDate,specs);
		formFactor = (String) specs.get("Bouwvorm");
		this.specs.put("formFactor",formFactor);
	}

	private Case(String formFactor) {
		super(0,0,null,null);
		this.formFactor = formFactor;
	}

	public String getFormFactor() {
		return formFactor;
	}

	public static boolean isPart(Map<String, Object> specs) {
		return COMPONENT.equals(specs.get("component")) && specs.containsKey("Bouwvorm");
	}

	public static boolean isValidElasticFilter(SearchFilter filter) {
		return filter.getKey().equals("Bouwvorm");
	}

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
}
