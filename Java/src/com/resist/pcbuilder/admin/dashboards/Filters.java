package com.resist.pcbuilder.admin.dashboards;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import com.resist.pcbuilder.admin.*;
import org.json.JSONObject;

import com.resist.pcbuilder.DBConnection;
import com.resist.pcbuilder.PcBuilder;

/**
 * Created by Armindo on 9-1-2015.
 */
public class Filters implements Dashboard {
    public static final String IDENTIFIER = "filters";
    private AdminSession session;
    private PcBuilder pcbuilder;

    public Filters(AdminSession session) {
        this.session = session;

    }


    @Override
    public JSONObject handleJSON(JSONObject input) {
        if(input.has("switchDashboard") && input.getString("switchDashboard").equals(IDENTIFIER)) {
            return new OutputBuilder().htmlTemplate("#main","dashboard_filters").getOutput();
        } else if(input.has("action")) {
            return handleActions(input);
        }
        return null;
    }
    private JSONObject handleActions(JSONObject input) {
        String action = input.getString("action");
        if(action.equals("init")) {
            return session.getPcBuilder().getSearchHandler().getInit();
        } else if (action.equals("updatefilter")) {
            return updateFilter(input);
        } else if (action.equals("deletefilter")) {
            System.out.println(action);
            return deleteFilter(input);
        }else if (action.equals("createfilter")) {
            System.out.println(action);
            return createFilter(input);
        }
        return null;
    }

    public java.sql.Connection getConnection() {
        return pcbuilder.getDBConnection().getConnection();
    }

    private JSONObject updateFilter(JSONObject input) {
        String newname = input.getString("newname");
        String cat = input.getString("cat");
        String sub = input.getString("sub");
        boolean updatefilter = FilterFunctions.updateFilter(session.getConnection(), cat, sub, newname);
        return new JSONObject().put("updatefilter", updatefilter);
    }

    private JSONObject deleteFilter(JSONObject input) {
        String cat = input.getString("cat");
        String sub = input.getString("sub");
        boolean deletefilter = FilterFunctions.deleteFilter(session.getConnection(), cat, sub);
        return new JSONObject().put("deletefilter", deletefilter);
    }

    private JSONObject createFilter(JSONObject input) {
        String newname = input.getString("newname");
        String cat = input.getString("cat");
        boolean createfilter = FilterFunctions.createFilter(session.getConnection(), cat, newname);
        return new JSONObject().put("createfilter", createfilter);
    }
}
