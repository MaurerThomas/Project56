package com.resist.pcbuilder.admin.dashboards;

import com.resist.pcbuilder.DatePrice;
import com.resist.pcbuilder.PcBuilder;
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
import java.util.logging.Level;

/**
 * Created by Thomas on 16-12-2014.
 */
public class Grafieken implements Dashboard {
	public static final String IDENTIFIER = "grafieken";
	private final static int width = 1024;
	private final static int height = 240;

	private AdminSession session;
	private JSONObject settings;

	public Grafieken(AdminSession session) {
		this.session = session;
		settings = session.getPcBuilder().getSettings();
	}

	@Override
	public JSONObject handleJSON(JSONObject input) {
		if (input.has("switchDashboard") && input.getString("switchDashboard").equals(IDENTIFIER)) {
			makeVisitorCharts();
			return new OutputBuilder().htmlTemplate("#main", "dashboard_grafieken").getOutput();
		} else if (input.has("makeChart")) {
			handleCharts(input);
		}
		return null;
	}

	private void handleCharts(JSONObject input) {
		Client client = session.getPcBuilder().getSearchClient();
		Connection conn = session.getConnection();
		String action = input.getString("makeChart");
		switch (action) {
			case "ProcessorKoeler":
				makeTheChartForComponents("Processor Koeler", ProcessorCooler.getAvgPrice(client, conn), settings.getString("processorCoolerGraphPath"));
				break;
			case "Processor":
				makeTheChartForComponents("Processor", Processor.getAvgPrice(client, conn), settings.getString("processorGraphPath"));
				break;
			case "Voeding":
				makeTheChartForComponents("Voeding", PowerSupplyUnit.getAvgPrice(client, conn), settings.getString("powerSupplyGraphPath"));
				break;
			case "Moederbord":
				makeTheChartForComponents("Moederbord", Motherboard.getAvgPrice(client, conn), settings.getString("motherboardGraphPath"));
				break;
			case "Geheugen":
				makeTheChartForComponents("Geheugen", Memory.getAvgPrice(client, conn), settings.getString("memoryGraphPath"));
				break;
			case "Videokaart":
				makeTheChartForComponents("Videokaart", GraphicsCard.getAvgPrice(client, conn), settings.getString("graphicsCardGraphPath"));
				break;
			case "Behuizing":
				makeTheChartForComponents("Behuizing", Case.getAvgPrice(client, conn), settings.getString("caseGraphPath"));
				break;
			case "Schijven":
				makeTheChartForComponents("Schijven", HardDisk.getAvgPrice(client, conn), settings.getString("harddiskGraphPath"));
				break;
		}
	}

	private void makeVisitorCharts() {
		List<Analytics> analyticsList = Analytics.getVisitors(session.getPcBuilder().getDBConnection());
		DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
		for (Analytics analytics : analyticsList) {
			line_chart_dataset.addValue(Integer.valueOf(analytics.getHashcodes()), "bezoekers", String.valueOf(analytics.getDatum()));
		}
		makeChart("Aantal Bezoekers","Datum","Bezoekers",line_chart_dataset,settings.getString("visitorGraphPath"));
	}

	private void makeTheChartForComponents(String titel, List<DatePrice> priceAndDate, String locatie) {
		DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
		for (DatePrice datePrice : priceAndDate) {
			line_chart_dataset.addValue(datePrice.getPrice(), "Prijs", datePrice.getDate());
		}
		makeChart(titel,"Datum","Prijs",line_chart_dataset,locatie);
	}

	private void makeChart(String titel, String xLabel, String yLabel, DefaultCategoryDataset line_chart_dataset, String locatie) {
		JFreeChart lineChartObject = ChartFactory.createLineChart(titel,
				xLabel, yLabel, line_chart_dataset, PlotOrientation.VERTICAL,
				true, true, false);
		File lineChart = new File(locatie);
		try {
			ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);
		} catch (IOException e) {
			PcBuilder.LOG.log(Level.WARNING,"Failed to create chart.",e);
		}
	}
}
