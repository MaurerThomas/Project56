package com.resist.pcbuilder.pcparts;

import com.resist.pcbuilder.DBConnection;
import com.resist.pcbuilder.PcBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.search.SearchHit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class PcPart {
	private static final String priceTable = "prijs_verloop";
	private static final String urlColumn = "url";
	private static final String euroColumn = "euro";
	private static final String centColumn = "cent";
	private static final String dateColumn = "datum";
	private static final String mongoSearchIndex = "mongoindex";

	private String url;
	private int euro;
	private int cent;
    private Date datum;
	private Map<String, Object> specs;

	public PcPart(String url, int euro, int cent, Date datum) {
		this.url = url;
		this.euro = euro;
		this.cent = cent;
        this.datum = datum;
		this.specs = null;
	}

	public PcPart(Map<String, Object> specs)  {
		this(null,0,0,null);
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

    public Date getDatum(){return datum;}

	public Map<String, Object> getSpecs() {
		return specs;
	}

    public static List<PcPart> getPartsPrice(Connection conn, List<String> urls, Date date, Integer minPrice, Integer maxPrice) {
    	List<PcPart> out = new ArrayList<PcPart>();
        try {
        	StringBuilder sql = new StringBuilder("SELECT DISTINCT ");

        	sql.append(urlColumn).append(",").append(euroColumn).append(",").append(centColumn).append(",").append(dateColumn)
        	.append(" FROM ").append(priceTable).append(" WHERE ").append(dateColumn).append(" > ?");
            int args = 1;
            if(minPrice != null) {
                sql.append(" AND ").append(euroColumn).append("*100+").append(centColumn).append(" >= ?");
                args++;
            }
            if(maxPrice != null) {
                sql.append(" AND ").append(euroColumn).append("*100+").append(centColumn).append(" <= ?");
                args++;
            }
            sql.append(" AND ").append(urlColumn).append(DBConnection.getInQuery(urls.size()));
            PreparedStatement s = conn.prepareStatement(sql.toString());
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
                out.add(new PcPart(res.getString(1),res.getInt(2),res.getInt(3),res.getDate(4)));
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
        PcBuilder.LOG.log(Level.INFO,filters.toString());
		SearchResponse response = client.prepareSearch(mongoSearchIndex)
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
