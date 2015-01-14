package com.resist.pcbuilder.pcparts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.resist.pcbuilder.PcBuilder;

public class Memory extends PcPart {
	private static final String memoryTable = "geheugen";
	private static final String typeColumn = "type";

	private String socket;

	private Memory(String socket) {
		super(null,0,0,null);
		this.socket = socket;
	}

	public String getSocket() {
		return socket;
	}

	public static List<Memory> getSockets(Connection conn) {
		List<Memory> out = new ArrayList<Memory>();
		try {
			PreparedStatement s = conn.prepareStatement("SELECT "+typeColumn+" FROM "+memoryTable);
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
