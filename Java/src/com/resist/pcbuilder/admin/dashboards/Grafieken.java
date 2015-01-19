package com.resist.pcbuilder.admin.dashboards;

import com.resist.pcbuilder.admin.AdminSession;
import com.resist.pcbuilder.admin.Analytics;
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
public class Grafieken implements Dashboard {

    public static final String IDENTIFIER = "grafieken";
    private AdminSession session;

    public Grafieken(AdminSession session){this.session = session; }


    @Override
    public JSONObject handleJSON(JSONObject input) {
        if(input.has("switchDashboard") && input.getString("switchDashboard").equals(IDENTIFIER)) {
            try {
                makeChartForPartsPrice();
            } catch (IOException e) {
                e.printStackTrace();
            }

            makeVisitorCharts();
            return new OutputBuilder().htmlTemplate("#main","dashboard_grafieken").getOutput();
        }

        return null;
    }


    private void makeVisitorCharts(){
        Analytics.getVisitors(session.getPcBuilder().getDBConnection());

    }

    private void makeChartForPartsPrice() throws IOException {
        JSONArray getPrijs = makeFiltersForGraph();
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

       // Voor debugging
       // int[] dataset = new int[getPrijs.length()];

        for (int i = 0; i < getPrijs.length(); ++i){
            JSONObject prijs = getPrijs.getJSONObject(i);
            line_chart_dataset.addValue(prijs.getInt("euro")+prijs.getInt("cent")/100.0, "Prijs", prijs.get("datum").toString());
        }
        JFreeChart lineChartObject = ChartFactory.createLineChart(
                "Schijven", "Datum",
                "Prijs",
                line_chart_dataset, PlotOrientation.VERTICAL,
                true, true, false);

        int width = 1024; /* Width of the image */
        int height = 240; /* Height of the image */

        // Voor linux
        File lineChart = new File("//var//www//html//img//LineChart.jpeg");
        ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);
    }

    private JSONArray makeFiltersForGraph(){

        JSONArray filters = new JSONArray();
        JSONObject x = new JSONObject();
        JSONObject z = new JSONObject();
        x.put("key","component").put("value","schijven");
        z.put("makechart", filters);
        filters.put(x);
        System.out.println(z);

        return session.getPcBuilder().getSearchHandler().handleSearch(z);
    }
}
