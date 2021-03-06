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

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

/**
 * @author Wouter
 */
public class Systeem implements Dashboard {

    public static final String IDENTIFIER = "systeem";
    private JSONObject settings;

    public Systeem(AdminSession session) {
        settings = session.getPcBuilder().getSettings();
    }

    @Override
    public JSONObject handleJSON(JSONObject input) {
        if (input.has("switchDashboard") && input.getString("switchDashboard").equals(IDENTIFIER)) {
            return new OutputBuilder().htmlTemplate("#main", "dashboard_systeem").getOutput();
        } else if (input.has("action")) {
            return handleActions(input);
        }
        return null;
    }

    private JSONObject handleActions(JSONObject input) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm");
        Date date = Calendar.getInstance().getTime();
        String currentDate = simpleDateFormat.format(date);
        String action = input.getString("action");
        if (action.equals("getLogs")) {
            return displayLog();
        } else if (action.equals("getCron")) {
            return displayCron();
        } else if (action.equals("clearLog")) {
            try {
                clearLog(currentDate);
            } catch (IOException e) {
                PcBuilder.LOG.log(Level.WARNING, "Failed to clear log.", e);
            }
        } else if (action.equals("cronjob")) {
            setCronjob(input);
        }
        return null;
    }

    private JSONObject displayCron() {
        String minute = "";
        String hour = "";
        boolean alternate = false;
        boolean cdromland = false;
        BufferedReader br = null;
        JSONObject out = new JSONObject();
        try {
            br = new BufferedReader(new FileReader(settings.getString("cronDir") + "cron.x"));
            String line = br.readLine();
            while (line != null) {
                if (line.contains("alternate")) {
                    alternate = true;
                }
                if (line.contains("cdromland")) {
                    cdromland = true;
                }
                line = br.readLine();
            }
            br.close();
            JSONObject cron = new JSONObject();
            cron.put("alternate", alternate);
            cron.put("cdromland", cdromland);
            cron.put("minute", minute);
            cron.put("hour", hour);
            out.put("cron", cron);
            return out;
        } catch (Exception e) {
            PcBuilder.LOG.log(Level.WARNING, "Failed to display cron", e);
        }
        return null;
    }

    private JSONObject displayLog() {
        BufferedReader br = null;
        JSONObject out = new JSONObject();
        try {
            br = new BufferedReader(new FileReader(settings.getString("errorLogPath") + "error.log"));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            br.close();
            out.put("log", sb.toString());
            return out;
        } catch (IOException e) {
            PcBuilder.LOG.log(Level.WARNING, "Failed to display log", e);
        }
        return null;
    }

    private void clearLog(String date) throws IOException {
        String errorPath = settings.getString("errorLogPath");
        String outputPath = settings.getString("outputLogPath");
        String backupPath = settings.getString("logBackupDir");

        File outputlog = new File(outputPath + "output.log");
        File errorlog = new File(errorPath + "error.log");
        Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
        String outputlogname = "output_" + date;
        String errorlogname = "error_" + date;
        File destination = new File(backupPath);
        try {
            archiver.create(outputlogname, destination, outputlog);
            archiver.create(errorlogname, destination, errorlog);
            outputlog.delete();
            errorlog.delete();
            outputlog = new File(outputPath + "output.log");
            errorlog = new File(errorPath + "error.log");
            try {
                outputlog.createNewFile();
                outputlog.canWrite();
                outputlog.canRead();
                errorlog.createNewFile();
                errorlog.canRead();
                errorlog.canWrite();
            } catch (IOException e) {
                PcBuilder.LOG.log(Level.WARNING, "Failed to archive files", e);
            }
        } catch (IOException e) {
            PcBuilder.LOG.log(Level.WARNING, "Failed to create new file ", e);
        }
    }

    private void setCronjob(JSONObject input) {
        int minute = input.getInt("minute1");
        int hour = input.getInt("hour1");
        boolean alternate = input.getBoolean("alternate");
        boolean cdromland = input.getBoolean("cdromland");
        String line1 = "";
        String line2 = "";
        if (minute >= 0 && minute <= 59 && hour >= 0 && hour <= 23) {
            if (alternate) {
                line1 += minute + " " + hour + " * * * " + settings.getString("alternateCrawl");
            }
            if (cdromland) {
                line2 += ((minute + 10) % 60) + " " + ((hour + ((minute + 10) / 60)) % 24) + " * * * " + settings.getString("cdromlandCrawl");
            }
        }
        File file = new File(settings.getString("cronDir") + "cron.x");
        try {
            file.createNewFile();
            file.setWritable(true);
            FileWriter writer = new FileWriter(file);
            writer.write(line1 + "\n");
            writer.write(line2 + "\n");
            writer.flush();
            writer.close();
            Runtime.getRuntime().exec("crontab " + file.getAbsolutePath());
        } catch (IOException e) {
            PcBuilder.LOG.log(Level.WARNING, "Failed to write crontab.", e);
        }
    }
}
