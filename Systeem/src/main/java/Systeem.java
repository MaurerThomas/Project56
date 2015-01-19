import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Wouter on 1/19/2015.
 */
public class Systeem {
    public static void main(String[] args) {
        new Systeem();
    }
    public Systeem()
    {
        clearLog();
    }

    private void clearLog() {
        //JSONObject settings = session.getPcBuilder().getSettings();
        //String errorPath = settings.getString("errorLogPath");
        //String outputPath = settings.getString("outputLogPath");
        //String backupPath = settings.getString("logBackupDir");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        Date date = Calendar.getInstance().getTime();

        String errorPath = "C:/Users/Wouter/Desktop/archivetest/";
        String outputPath = "C:/Users/Wouter/Desktop/archivetest/";
        String backupPath = "C:/Users/Wouter/Desktop/archivetest/backup/";


        File outputlog = new File(outputPath+"output.log");
        File errorlog = new File(errorPath+"error.log");
        Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
        String outputlogname = "output_"+simpleDateFormat.format(date);
        String errorlogname = "error_"+simpleDateFormat.format(date);
        File destination = new File(backupPath);
        try{
            File archive = archiver.create(outputlogname, destination, outputlog);

            archive = archiver.create(errorlogname, destination, errorlog);
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputlog.delete();
        errorlog.delete();
        outputlog = new File(outputPath+"output.log");
        errorlog = new File(errorPath+"error.log");

        try {
            outputlog.createNewFile();
            errorlog.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
