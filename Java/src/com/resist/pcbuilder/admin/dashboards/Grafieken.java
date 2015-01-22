package com.resist.pcbuilder.admin.dashboards;

import com.resist.pcbuilder.DatePrice;
import com.resist.pcbuilder.admin.AdminSession;
import com.resist.pcbuilder.admin.Analytics;
import com.resist.pcbuilder.admin.Dashboard;
import com.resist.pcbuilder.admin.OutputBuilder;
import com.resist.pcbuilder.pcparts.*;
import org.elasticsearch.client.Client;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

/**
 * Created by Thomas on 16-12-2014.
 */
public class Grafieken implements Dashboard {
    private AdminSession session;
    private JSONObject settings;

    public Grafieken(AdminSession session) {
        this.session = session;
        settings = session.getPcBuilder().getSettings();
    }

    public  static final String IDENTIFIER = "grafieken";

    @Override
    public JSONObject handleJSON(JSONObject input) {
        if (input.has("switchDashboard") && input.getString("switchDashboard").equals(IDENTIFIER)) {
            try {
                makeVisitorCharts();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new OutputBuilder().htmlTemplate("#main", "dashboard_grafieken").getOutput();

        } else if (input.has("makeChart")) {
            try {
                handleCharts(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private void handleCharts(JSONObject input) throws IOException {
        String action = input.getString("makeChart");
        System.out.println(action);
        switch (action) {
            case "ProcessorKoeler":

                makeTheChartForComponents("Processor Koeler", ProcessorCooler.COMPONENT, settings.getString("processorCoolerGraphPath"));
                break;
            case "Processor":
                makeTheChartForComponents("Processor", Processor.COMPONENT, settings.getString("processorGraphPath"));
                break;
            case "Voeding":
                makeTheChartForComponents("Voeding", PowerSupplyUnit.COMPONENT, settings.getString("powerSupplyGraphPath"));
                break;
            case "Moederbord":
                makeTheChartForComponents("Moederbord", Motherboard.COMPONENT, settings.getString("motherboardGraphPath"));
                break;
            case "Geheugen":
                makeTheChartForComponents("Geheugen", Memory.COMPONENT, settings.getString("memoryGraphPath"));
                break;
            case "Videokaart":
                makeTheChartForComponents("Videokaart", GraphicsCard.COMPONENT, settings.getString("graphicsCardGraphPath"));
                break;
            case "Behuizing":
                makeTheChartForComponents("Behuizing", Case.COMPONENT, settings.getString("caseGraphPath"));
                break;
            case "Schijven":
                makeTheChartForComponents("Schijven", HardDisk.COMPONENT, settings.getString("harddiskGraphPath"));
                break;
        }
    }


    private void makeVisitorCharts() throws IOException {
        Analytics.getVisitors(session.getPcBuilder().getDBConnection());
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

        List<Analytics> analyticsList = Analytics.getVisitors(session.getPcBuilder().getDBConnection());

        for (Analytics analytics : analyticsList) {
            line_chart_dataset.addValue(Integer.valueOf(analytics.getHashcodes()), "bezoekers", String.valueOf(analytics.getDatum()));
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
        File lineChart = new File(settings.getString("visitorGraphPath"));
        ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);

    }

    private void makeTheChartForComponents(String titel, String onderdeel, String locatie) throws IOException {
        List<DatePrice> priceAndDate = getAveragePriceForComponent(onderdeel);
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

        for (DatePrice datePrice : priceAndDate) {
            line_chart_dataset.addValue(datePrice.getPrice(), "Prijs", datePrice.getDate());

        }

        JFreeChart lineChartObject = ChartFactory.createLineChart(
                titel, "Datum",
                "Prijs",
                line_chart_dataset, PlotOrientation.VERTICAL,
                true, true, false);

        int width = 1024; /* Width of the image */
        int height = 240; /* Height of the image */

        // Voor linux
        File lineChart = new File(locatie);
        ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);

    }


    private List<DatePrice> getAveragePriceForComponent(String part) {
        Client client = session.getPcBuilder().getSearchClient();
        Connection conn = session.getConnection();
        switch (part) {
            case Processor.COMPONENT:
                return Processor.getAvgPrice(client, conn);
            case ProcessorCooler.COMPONENT:
                return ProcessorCooler.getAvgPrice(client, conn);
            case PowerSupplyUnit.COMPONENT:
                return PowerSupplyUnit.getAvgPrice(client, conn);
            case Motherboard.COMPONENT:
                return Motherboard.getAvgPrice(client, conn);
            case Memory.COMPONENT:
                return Memory.getAvgPrice(client, conn);
            case HardDisk.COMPONENT:
                return HardDisk.getAvgPrice(client, conn);
            case GraphicsCard.COMPONENT:
                return GraphicsCard.getAvgPrice(client, conn);
            case Case.COMPONENT:
                return Case.getAvgPrice(client, conn);
        }
        return null;
    }

}
