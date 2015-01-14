package com.resist.pcbuilder.pcparts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.resist.pcbuilder.PcBuilder;

public class GraphicsCard extends PcPart {
	private static final String socketTable = "aansluiting";
	private static final String socketTypeColumn = "type";
	private static final String socketPartColumn = "onderdeeltype";
	private static final String part = "Grafischekaart";
	private static final String brandTable = "merk";
	private static final String brandNameColumn = "merk.naam";
	private static final String brandMidColumn = "merk.mid";
	private static final String joinTable = "tussentabel";
	private static final String joinMidColumn = "tussentabel.merkmid";

	private String socket;
	private String brand;

	private GraphicsCard(String brand, String socket) {
		super(null,0,0,null);
		this.socket = socket;
		this.brand = brand;
	}

	public String getSocket() {
		return socket;
	}

	public String getBrand() {
		return brand;
	}

	public static List<GraphicsCard> getSockets(Connection conn) {
		List<GraphicsCard> out = new ArrayList<GraphicsCard>();
		try {
			PreparedStatement s = conn.prepareStatement("SELECT "+socketTypeColumn+" FROM "+socketTable+" WHERE "+socketPartColumn+" = ?");
	        s.setString(1,part);
	        ResultSet res = s.executeQuery();
	        while(res.next()) {
	            String type = res.getString(1);
	            out.add(new GraphicsCard(type,null));
	        }
	        res.close();
	        s.close();
		} catch (SQLException e) {
			PcBuilder.LOG.log(Level.WARNING,"Failed to get gpu sockets.",e);
		}
		return out;
	}

	public static List<GraphicsCard> getBrands(Connection conn) {
		List<GraphicsCard> out = new ArrayList<GraphicsCard>();
		try {
			PreparedStatement s = conn.prepareStatement("SELECT "+brandNameColumn+" FROM "+brandTable+" JOIN "+joinTable+" ON ("+joinMidColumn+" = "+brandMidColumn+") WHERE "+socketPartColumn+" = ?");
	        s.setString(1, part);
	        ResultSet res = s.executeQuery();
	        while(res.next()) {
	        	String naam = res.getString(1);
	        	out.add(new GraphicsCard(null,naam));
	        }
	        res.close();
	        s.close();
		} catch (SQLException e) {
			PcBuilder.LOG.log(Level.WARNING,"Failed to get gpu brands.",e);
		}
		return out;
	}
}
