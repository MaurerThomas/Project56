package com.resist.pcbuilder.admin.dashboards;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;

import com.resist.pcbuilder.pcparts.*;
import org.elasticsearch.client.Client;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.json.JSONArray;
import org.json.JSONObject;

import com.resist.pcbuilder.DatePrice;
import com.resist.pcbuilder.admin.AdminSession;
import com.resist.pcbuilder.admin.Analytics;
import com.resist.pcbuilder.admin.Dashboard;
import com.resist.pcbuilder.admin.OutputBuilder;

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
                makeVisitorCharts();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return new OutputBuilder().htmlTemplate("#main","dashboard_grafieken").getOutput();
        }

        return null;
    }


    private void makeVisitorCharts() throws IOException {
        Analytics.getVisitors(session.getPcBuilder().getDBConnection());
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

        List<Analytics> analyticsList = Analytics.getVisitors(session.getPcBuilder().getDBConnection());

        for(Analytics analytics : analyticsList) {
           line_chart_dataset.addValue(Integer.valueOf(analytics.getHashcodes()),"bezoekers",String.valueOf(analytics.getDatum()));
            System.out.println(Integer.valueOf(analytics.getHashcodes()));

        }
        JFreeChart lineChartObject = ChartFactory.createLineChart(
                "Aantal Bezoekers", "Datum",
                "bezoekers",
                line_chart_dataset, PlotOrientation.VERTICAL,
                true, true, false);

        int width = 1024; /* Width of the image */
        int height = 240; /* Height of the image */

        // Voor linux
        File lineChart = new File("//var//www//html//img//LineChartVisitor.jpeg");
        ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);

    }

    private void makeChartForPartsPrice() throws IOException {
       List<DatePrice> priceAndDate = getAveragePriceForComponent(null);
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

       // Voor debugging
       // int[] dataset = new int[getPrijs.length()];



         //line_chart_dataset.addValue(prijs, "Prijs", date);


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

    private List<DatePrice> getAveragePriceForComponent(String part) {
    	Client client = session.getPcBuilder().getSearchClient();
    	Connection conn = session.getConnection();
        if(part.equals(Processor.COMPONENT)) {
           return  Processor.getAvgPrice(client,conn);
        } else if (part.equals(ProcessorCooler.COMPONENT)){
            return ProcessorCooler.getAvgPrice(client,conn);
        } else if (part.equals(PowerSupplyUnit.COMPONENT)){
            return PowerSupplyUnit.getAvgPrice(client,conn);
        } else if (part.equals(Motherboard.COMPONENT)){
            return Motherboard.getAvgPrice(client,conn);
        } else if (part.equals(Memory.COMPONENT)){
            return Memory.getAvgPrice(client,conn);
        } else if (part.equals(HardDisk.COMPONENT)){
            return HardDisk.getAvgPrice(client,conn);
        } else if (part.equals(GraphicsCard.COMPONENT)){
            return GraphicsCard.getAvgPrice(client,conn);
        } else if (part.equals(Case.COMPONENT)){
            return Case.getAvgPrice(client,conn);
        }
      return null;
    }

}
