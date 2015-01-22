package com.resist.pcbuilder.admin.dashboards;

import com.resist.pcbuilder.PcBuilder;
import com.resist.pcbuilder.admin.AdminSession;
import com.resist.pcbuilder.admin.Dashboard;
import com.resist.pcbuilder.admin.OutputBuilder;
import org.json.JSONObject;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

/**
 * Created by Wouter
 */
public class Systeem implements Dashboard {

    public static final String IDENTIFIER = "systeem";
    private AdminSession session;
    JSONObject settings;

    public Systeem(AdminSession session){
        this.session = session;
        settings = session.getPcBuilder().getSettings();
    }

    @Override
    public JSONObject handleJSON(JSONObject input) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm");
        Date date = Calendar.getInstance().getTime();
        String currentDate = simpleDateFormat.format(date);

        if(input.has("switchDashboard") && input.getString("switchDashboard").equals(IDENTIFIER)) {
            return new OutputBuilder().htmlTemplate("#main","dashboard_systeem").getOutput();
        }
		else if(input.getString("action").equals("clearLog"))
		{
            try {
                clearLog(currentDate);
            } catch (IOException e) {
                PcBuilder.LOG.log(Level.WARNING, "Failed to clear log ", e);
            }
        }
        else if(input.getString("action").equals("cronjob"))
        {
            setCronjob(input, currentDate);
        }
        return null;
    }
	
	private void clearLog(String date) throws IOException {
        String errorPath = settings.getString("errorLogPath");
        String outputPath = settings.getString("outputLogPath");
        String backupPath = settings.getString("logBackupDir");

        File outputlog = new File(outputPath+"output.log");
        File errorlog = new File(errorPath+"error.log");
        Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
        String outputlogname = "output_"+simpleDateFormat.format(date);
        String errorlogname = "error_"+simpleDateFormat.format(date);
        File destination = new File(backupPath);
        try{
            File archive = archiver.create(outputlogname, destination, outputlog);
            archive = archiver.create(errorlogname, destination, errorlog);
            /*outputlog.delete();
            errorlog.delete();
            outputlog = new File(outputPath+"output.log");
            errorlog = new File(errorPath+"error.log");

            try {
                outputlog.createNewFile();
                errorlog.createNewFile();
            } catch (IOException e) {
                PcBuilder.LOG.log(Level.WARNING, "Failed to archive files", e);
            }
            */

            Process p = Runtime.getRuntime().exec("rm -r"+outputlog.getAbsolutePath());
            p = Runtime.getRuntime().exec("rm -r"+errorlog.getAbsolutePath());
            p = Runtime.getRuntime().exec("touch "+errorlog.getAbsolutePath());
            p = Runtime.getRuntime().exec("touch "+outputlog.getAbsolutePath());
        } catch (IOException e) {
            PcBuilder.LOG.log(Level.WARNING, "Failed to create new file ", e);
        }

    }
    private void setCronjob(JSONObject input, String date)
    {
        boolean alternate = input.getBoolean("alternate");
        boolean cdromland = input.getBoolean("cdromland");
        String minute = input.getString("minute1");
        String hour = input.getString("hour1");
        String line1 = "";
        String line2 = "";
        if(alternate && minute.length() >= 1 && hour.length() >= 1 )
        {
            line1 += minute+" "+hour+" * * * "+settings.getString("crawlerPath")+settings.getString("alternateCrawl");
        }
        if(cdromland)
        {
            line2 += minute+" "+hour+" * * * "+settings.getString("crawlerPath")+settings.getString("cdromlandCrawl");
        }
        File file = new File(settings.getString("cronBackupDir")+date+".backup");
        try {
            file.createNewFile();
            file.setWritable(true);
            FileWriter writer = new FileWriter(file);
            writer.write(line1 +"\n");
            writer.write(line2 +"\n");

            writer.flush();
            writer.close();
            Process p = Runtime.getRuntime().exec("crontab " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
