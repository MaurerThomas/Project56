package com.resist.pcbuilder.admin.dashboards;


import com.resist.pcbuilder.admin.AdminSession;
import com.resist.pcbuilder.admin.Dashboard;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by Thomas on 16-12-2014.
 */
public class Grafieken implements Dashboard {

    public static final String IDENTIFIER = "grafieken";
    private AdminSession session;

    @Override
    public JSONObject handleJSON(JSONObject input) {
        if (input.getString("action").equals("makechart")){
            try {
                makeChart();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public void makeChart() throws IOException {
        //session.getPcBuilder().getMysql().getPartsPrice();

        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

        line_chart_dataset.addValue(2, "schools", "2014");

        JFreeChart lineChartObject = ChartFactory.createLineChart(
                "Schools Vs Years", "Year",
                "Schools Count",
                line_chart_dataset, PlotOrientation.VERTICAL,
                true, true, false);

        int width = 640; /* Width of the image */
        int height = 480; /* Height of the image */

        // Voor linux
        //File lineChart = new File("//var//www//img//LineChart.jpeg");

        File lineChart = new File("LineChart.jpeg");
        ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);
    }

}
