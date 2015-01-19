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

public class Memory extends PcPart {
	public static final String COMPONENT = "Geheugen";

	private String socket;

	public Memory(int euro, int cent, Date crawlDate, Map<String,Object> specs) {
		super(euro,cent,crawlDate,specs);
	}

	private Memory(String socket) {
		super(0,0,null,null);
		this.socket = socket;
	}

	public String getSocket() {
		return socket;
	}

	public static boolean isPart(Map<String, Object> specs) {
		return COMPONENT.equals(specs.get("component"));
	}

	public static List<Memory> getSockets(Connection conn) {
		List<Memory> out = new ArrayList<Memory>();
		try {
			PreparedStatement s = conn.prepareStatement("SELECT "+DBConnection.COLUMN_MEMORY_TYPE+" FROM "+DBConnection.TABLE_MEMORY);
	        ResultSet res = s.executeQuery();
	        while(res.next()) {
	            String type = res.getString(1);
	            out.add(new Memory(type));
	        }
	        res.close();
	        s.close();
		} catch (SQLException e) {
			PcBuilder.LOG.log(Level.WARNING,"Failed to get ram interfaces.",e);
		}
		return out;
	}
}
