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
               makeVisitorCharts();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new OutputBuilder().htmlTemplate("#main","dashboard_grafieken").getOutput();
        } else if(input.has("makeChart")) {
            try {
                System.out.println("Ik ga handelen");
                System.out.println("Input = : " + input);
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
        if(action.equals("ProcessorKoeler")) {
            makeChartForProcessorCoolers();
        } else if (action.equals("Processor")){
            makeChartForProcessors();
        } else if (action.equals("Voeding")){
            makeChartForPowerSupply();
        } else if (action.equals("Moederbord")){
            makeChartForMotherboard();
        } else if (action.equals("Geheugen")){
            makeChartForMemory();
        } else if (action.equals("Videokaart")){
            makeChartForGrahpicsCard();
        } else if (action.equals("Behuizing")){
            makeChartForCase();
        } else if (action.equals("Schijven")){
            makeChartForHarddisk();
        }

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

    private void makeChartForProcessors() throws IOException {
       List<DatePrice> priceAndDate = getAveragePriceForComponent(Processor.COMPONENT);
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

        for (DatePrice datePrice : priceAndDate) {
            line_chart_dataset.addValue(datePrice.getPrice(), "Prijs", datePrice.getDate());
            
        }

        JFreeChart lineChartObject = ChartFactory.createLineChart(
                "Processoren", "Datum",
                "Prijs",
                line_chart_dataset, PlotOrientation.VERTICAL,
                true, true, false);

        int width = 1024; /* Width of the image */
        int height = 240; /* Height of the image */

        // Voor linux
        File lineChart = new File("//var//www//html//img//LineChartProcessoren.jpeg");
        ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);
    }

    private void makeChartForProcessorCoolers() throws IOException {
        List<DatePrice> priceAndDate = getAveragePriceForComponent(ProcessorCooler.COMPONENT);
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

        for (DatePrice datePrice : priceAndDate) {
            line_chart_dataset.addValue(datePrice.getPrice(), "Prijs", datePrice.getDate());

        }

        JFreeChart lineChartObject = ChartFactory.createLineChart(
                "Processor Coolers", "Datum",
                "Prijs",
                line_chart_dataset, PlotOrientation.VERTICAL,
                true, true, false);

        int width = 1024; /* Width of the image */
        int height = 240; /* Height of the image */

        // Voor linux
        File lineChart = new File("//var//www//html//img//LineChartProcessorenCoolers.jpeg");
        ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);
    }

    private void makeChartForPowerSupply() throws IOException {
        List<DatePrice> priceAndDate = getAveragePriceForComponent(PowerSupplyUnit.COMPONENT);
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

        for (DatePrice datePrice : priceAndDate) {
            line_chart_dataset.addValue(datePrice.getPrice(), "Prijs", datePrice.getDate());

        }

        JFreeChart lineChartObject = ChartFactory.createLineChart(
                "Voedingen", "Datum",
                "Prijs",
                line_chart_dataset, PlotOrientation.VERTICAL,
                true, true, false);

        int width = 1024; /* Width of the image */
        int height = 240; /* Height of the image */

        // Voor linux
        File lineChart = new File("//var//www//html//img//LineChartVoedingen.jpeg");
        ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);
    }

    private void makeChartForMotherboard() throws IOException {
        List<DatePrice> priceAndDate = getAveragePriceForComponent(Motherboard.COMPONENT);
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

        for (DatePrice datePrice : priceAndDate) {
            line_chart_dataset.addValue(datePrice.getPrice(), "Prijs", datePrice.getDate());

        }

        JFreeChart lineChartObject = ChartFactory.createLineChart(
                "Moederborden", "Datum",
                "Prijs",
                line_chart_dataset, PlotOrientation.VERTICAL,
                true, true, false);

        int width = 1024; /* Width of the image */
        int height = 240; /* Height of the image */

        // Voor linux
        File lineChart = new File("//var//www//html//img//LineChartMoederborden.jpeg");
        ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);
    }

    private void makeChartForMemory() throws IOException {
        List<DatePrice> priceAndDate = getAveragePriceForComponent(Memory.COMPONENT);
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

        for (DatePrice datePrice : priceAndDate) {
            line_chart_dataset.addValue(datePrice.getPrice(), "Prijs", datePrice.getDate());

        }

        JFreeChart lineChartObject = ChartFactory.createLineChart(
                "Geheugen", "Datum",
                "Prijs",
                line_chart_dataset, PlotOrientation.VERTICAL,
                true, true, false);

        int width = 1024; /* Width of the image */
        int height = 240; /* Height of the image */

        // Voor linux
        File lineChart = new File("//var//www//html//img//LineChartGeheugen.jpeg");
        ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);
    }

    private void makeChartForHarddisk() throws IOException {
        List<DatePrice> priceAndDate = getAveragePriceForComponent(HardDisk.COMPONENT);
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

        for (DatePrice datePrice : priceAndDate) {
            line_chart_dataset.addValue(datePrice.getPrice(), "Prijs", datePrice.getDate());

        }

        JFreeChart lineChartObject = ChartFactory.createLineChart(
                "Schijven", "Datum",
                "Prijs",
                line_chart_dataset, PlotOrientation.VERTICAL,
                true, true, false);

        int width = 1024; /* Width of the image */
        int height = 240; /* Height of the image */

        // Voor linux
        File lineChart = new File("//var//www//html//img//LineChartSchijven.jpeg");
        ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);
    }

    private void makeChartForGrahpicsCard() throws IOException {
        List<DatePrice> priceAndDate = getAveragePriceForComponent(GraphicsCard.COMPONENT);
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

        for (DatePrice datePrice : priceAndDate) {
            line_chart_dataset.addValue(datePrice.getPrice(), "Prijs", datePrice.getDate());

        }

        JFreeChart lineChartObject = ChartFactory.createLineChart(
                "Grafische kaarten", "Datum",
                "Prijs",
                line_chart_dataset, PlotOrientation.VERTICAL,
                true, true, false);

        int width = 1024; /* Width of the image */
        int height = 240; /* Height of the image */

        // Voor linux
        File lineChart = new File("//var//www//html//img//LineChartGrafischeKaarten.jpeg");
        ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);
    }

    private void makeChartForCase() throws IOException {
        List<DatePrice> priceAndDate = getAveragePriceForComponent(Case.COMPONENT);
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

        for (DatePrice datePrice : priceAndDate) {
            line_chart_dataset.addValue(datePrice.getPrice(), "Prijs", datePrice.getDate());

        }

        JFreeChart lineChartObject = ChartFactory.createLineChart(
                "Behuizingen", "Datum",
                "Prijs",
                line_chart_dataset, PlotOrientation.VERTICAL,
                true, true, false);

        int width = 1024; /* Width of the image */
        int height = 240; /* Height of the image */

        // Voor linux
        File lineChart = new File("//var//www//html//img//LineChartBehuizingen.jpeg");
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
