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

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.search.SearchHit;

import com.resist.pcbuilder.DBConnection;
import com.resist.pcbuilder.PcBuilder;

public class PcPart {
	private String url;
	private int euro;
	private int cent;
	private Map<String, Object> specs;

	public PcPart(String url, int euro, int cent) {
		this.url = url;
		this.euro = euro;
		this.cent = cent;
		this.specs = null;
	}

	public PcPart(Map<String, Object> specs) {
		this(null,0,0);
		this.specs = specs;
	}

	public String getUrl() {
		return url;
	}

	public int getEuro() {
		return euro;
	}

	public int getCent() {
		return cent;
	}

	public Map<String, Object> getSpecs() {
		return specs;
	}

    public static List<PcPart> getPartsPrice(Connection conn, List<String> urls, Date date, Integer minPrice, Integer maxPrice) {
    	List<PcPart> out = new ArrayList<PcPart>();
        try {
            String sql = "SELECT url,euro,cent FROM prijs_verloop WHERE datum > ?";
            int args = 1;
            if(minPrice != null) {
                sql += " AND euro*100+cent >= ?";
                args++;
            }
            if(maxPrice != null) {
                sql += " AND euro*100+cent <= ?";
                args++;
            }
            PreparedStatement s = conn.prepareStatement(sql+" AND url "+DBConnection.getInQuery(urls.size()));
            s.setDate(1, date);
            if(minPrice != null) {
                s.setInt(2,minPrice*100);
            }
            if(maxPrice != null) {
                s.setInt(args,maxPrice*100);
            }
            for(int i=0;i < urls.size();i++) {
                s.setString(i+1+args,urls.get(i));
            }

            ResultSet res = s.executeQuery();
            while(res.next()) {
                out.add(new PcPart(res.getString(1),res.getInt(2),res.getInt(3)));
            }
            res.close();
            s.close();
        } catch (SQLException e) {
			PcBuilder.LOG.log(Level.WARNING,"Failed to get prices.",e);
        }
        return out;
    }

    public static List<PcPart> getFilteredParts(Client client, FilterBuilder filters, int numResults) {
    	List<PcPart> out = new ArrayList<PcPart>();
		SearchResponse response = client.prepareSearch("mongoindex")
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setSize(numResults)
				.setPostFilter(filters)
				.setExplain(false)
				.execute()
				.actionGet();
		SearchHit[] results = response.getHits().getHits();
		for (SearchHit hit : results) {
			Map<String, Object> result = hit.getSource();
			out.add(new PcPart(result));
		}
    	return out;
    }
}
