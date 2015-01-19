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

    public Systeem(AdminSession session){this.session = session;}

    @Override
    public JSONObject handleJSON(JSONObject input) {

        PcBuilder.LOG.log(Level.INFO,"input : "+input.toString());
        if(input.has("switchDashboard") && input.getString("switchDashboard").equals(IDENTIFIER)) {
            return new OutputBuilder().htmlTemplate("#main","dashboard_systeem").getOutput();
        }
		else if(input.has("clearLog"))
		{
            PcBuilder.LOG.log(Level.INFO,"Clear Log initialized");
            try {
                clearLog();
            } catch (IOException e) {
                PcBuilder.LOG.log(Level.WARNING, "Failed to clear log ", e);
            }
        }
        return null;
    }
	
	private void clearLog() throws IOException {
        JSONObject settings = session.getPcBuilder().getSettings();
        String errorPath = settings.getString("errorLogPath");
        String outputPath = settings.getString("outputLogPath");
        String backupPath = settings.getString("logBackupDir");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm");
        Date date = Calendar.getInstance().getTime();

        File outputlog = new File(outputPath+"output.log");
        File errorlog = new File(errorPath+"error.log");
        Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
        String outputlogname = "output_"+simpleDateFormat.format(date);
        String errorlogname = "error_"+simpleDateFormat.format(date);
        File destination = new File(backupPath);
        try{
            File archive = archiver.create(outputlogname, destination, outputlog);
            archive = archiver.create(errorlogname, destination, errorlog);
            outputlog.delete();
            errorlog.delete();
            outputlog = new File(outputPath+"output.log");
            errorlog = new File(errorPath+"error.log");

            try {
                outputlog.createNewFile();
                errorlog.createNewFile();
            } catch (IOException e) {
                PcBuilder.LOG.log(Level.WARNING, "Failed to archive files", e);
            }
        } catch (IOException e) {
            PcBuilder.LOG.log(Level.WARNING, "Failed to create new file ", e);
        }

    }

}
