package com.resist.pcbuilder.admin.dashboards;
import com.resist.pcbuilder.admin.AdminSession;
import com.resist.pcbuilder.admin.Dashboard;
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

    public Grafieken(AdminSession session){this.session = session;}

    @Override
    public JSONObject handleJSON(JSONObject input) {
        if (input.getString("action").equals("makechart") && input.getString("merk").equals("asus")){
            try {
                makeChart(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void makeChart(JSONObject json) throws IOException {
        JSONArray getPrijs = session.getPcBuilder().getSearchHandler().handleIncomingMessage(json);
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
        System.out.println(getPrijs);

        int dataset = getPrijs.getInt(0);
        System.out.println(dataset);

        line_chart_dataset.addValue(dataset,"Prijs","2015");

        JFreeChart lineChartObject = ChartFactory.createLineChart(
                "Schools Vs Years", "Year",
                "Schools Count",
                line_chart_dataset, PlotOrientation.VERTICAL,
                true, true, false);

        int width = 640; /* Width of the image */
        int height = 480; /* Height of the image */

        // Voor linux
        File lineChart = new File("//var//www//img//LineChart.jpeg");
        ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);
    }
}
