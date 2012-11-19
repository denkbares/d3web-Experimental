/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.d3web.proket.utils;

import java.io.*;

/**
 * Utility Class for getting tailored PrintStreams for writing dedicated event-
 * and error log files.
 *
 * @author Martina Freiberg
 */
public class SystemLoggerUtils {

    
    private static GlobalSettings GLOBSET = GlobalSettings.getInstance();

    

    /**
     * Retrieves the default errorlog stream, which writes to the file
     * webapppath/UPFiles/syslogs/EVENTLOG.txt
     *
     * @return
     */
    public static PrintStream getEventLoggerStream() {
        try {
            String logdir = GLOBSET.getSyslogsBasePath();

            File logpath = new File(logdir);
            File logfile = new File(logdir, GLOBSET.getEventLogFileName());
            if (!logpath.exists()) {
                logpath.mkdirs();
                if (!logfile.exists()) {
                    logfile.createNewFile();
                }
            }


            PrintStream es = new PrintStream(
                    new BufferedOutputStream(new FileOutputStream(
                    logfile)), true);
            return es;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves the default exception logger stream, which writes to the file
     * webapppath/UPFiles/syslogs/ExceptionReportTmp.txt
     *
     * @return
     */
    public static PrintStream getExceptionLoggerStream() {
        try {
            String logdir = 
                    GlobalSettings.getInstance().getSyslogsBasePath();

            File logpath = new File(logdir);
            File logfile = new File(logdir, GLOBSET.getExceptionReportTmpFileName());
            if (!logpath.exists()) {
                logpath.mkdirs();
                if (!logfile.exists()) {
                    logfile.createNewFile();
                }
            }

            
            PrintStream es = new PrintStream(
                    new BufferedOutputStream(new FileOutputStream(
                    logfile)), true);
            return es;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

   
}
