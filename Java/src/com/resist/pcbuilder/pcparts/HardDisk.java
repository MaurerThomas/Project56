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

public class HardDisk extends PcPart {
	public static final String COMPONENT = "schijven";

	private String type;
	private String socket;

	public HardDisk(int euro, int cent, Date crawlDate, Map<String,Object> specs) {
		super(euro,cent,crawlDate,specs);
		socket = (String) specs.get("Interface");
		setSpec("interface",socket);
		type = getComponent();
	}

	private HardDisk(String type, String socket) {
		super(0,0,null,null);
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
		return ((String)specs.get("component")).contains(COMPONENT) && specs.containsKey("Interface");
	}

	public static boolean isValidElasticFilter(SearchFilter filter) {
		return filter.getKey().equals("Interface");
	}

	public static List<HardDisk> getHardDisks(Connection conn) {
		List<HardDisk> out = new ArrayList<HardDisk>();
		try {
			PreparedStatement s = conn.prepareStatement("SELECT "+DBConnection.COLUMN_HDD_TYPE+","+DBConnection.COLUMN_HDD_INTERFACE+" FROM "+DBConnection.TABLE_HDD);
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
