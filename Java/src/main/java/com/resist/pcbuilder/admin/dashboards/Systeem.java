package com.resist.pcbuilder.admin.dashboards;

import com.resist.pcbuilder.admin.AdminSession;
import com.resist.pcbuilder.admin.Dashboard;
import com.resist.pcbuilder.admin.OutputBuilder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by Thomas on 16-12-2014.
 */
public class Systeem implements Dashboard {

    public static final String IDENTIFIER = "systeem";
    private AdminSession session;

    public Systeem(AdminSession session){this.session = session;}

    @Override
    public JSONObject handleJSON(JSONObject input) {
        if(input.has("switchDashboard") && input.getString("switchDashboard").equals(IDENTIFIER)) {

            return new OutputBuilder().htmlTemplate("#main","dashboard_systeem").getOutput();
        }

        return null;
    }

}
