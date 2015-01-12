package com.resist.pcbuilder.pcparts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.resist.pcbuilder.PcBuilder;

public class HardDisk extends PcPart {
	private static final String hddTable = "hardeschijf";
	private static final String typeColumn = "type";
	private static final String socketColumn = "aansluitingtype";

	private String type;
	private String socket;

	private HardDisk(String type, String socket) {
		super(null,0,0);
		this.type = type;
		this.socket = socket;
	}

	public String getType() {
		return type;
	}

	public String getSocket() {
		return socket;
	}

	public static List<HardDisk> getHardDisks(Connection conn) {
		List<HardDisk> out = new ArrayList<HardDisk>();
		try {
			PreparedStatement s = conn.prepareStatement("SELECT "+typeColumn+","+socketColumn+" FROM "+hddTable);
	        ResultSet res = s.executeQuery();
	        while(res.next()) {
	            String type = res.getString(1);
	            String aansluiting = res.getString(2);
	            out.add(new HardDisk(type, aansluiting));
	        }
	        res.close();
	        s.close();
		} catch (SQLException e) {
			PcBuilder.LOG.log(Level.WARNING,"Failed to get hard disks.",e);
		}
		return out;
	}
}
