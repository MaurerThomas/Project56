package com.resist.pcbuilder.admin.dashboards;


import com.resist.pcbuilder.PcBuilder;
import com.resist.pcbuilder.SearchHandler;
import com.resist.pcbuilder.admin.AdminSession;
import com.resist.pcbuilder.admin.Dashboard;
import com.resist.pcbuilder.admin.OutputBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Armindo on 9-1-2015.
 */
public class Filters implements Dashboard {
    public static final String IDENTIFIER = "filters";
    private AdminSession session;
    private SearchHandler initParts;
    private PcBuilder pcbuilder;

    public Filters(AdminSession session) {this.session = session;}

    @Override
    public JSONObject handleJSON(JSONObject input) {
        if(input.has("switchDashboard") && input.getString("switchDashboard").equals(IDENTIFIER)) {
            return new OutputBuilder().htmlTemplate("#main","dashboard_filters").getOutput();
        }
        return null;
    }

    public JSONObject getFilters() {
       return initParts.getInit();
    }

    public java.sql.Connection getConnection() {
        return pcbuilder.getDBConnection().getConnection();
    }


}
