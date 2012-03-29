/**
 * Copyright (C) 2012 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.proket.d3web.ue.analyze;

import de.d3web.proket.d3web.ue.UETerm;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.json.simple.JSONObject;

public class UEStudyAnalyst {

    private JSONGlobalAnalyzer globa = null;
    private JSONFileAnalyzer filea = null;
    private File FILE;
    private JSONObject JSON;

    /*
     * Constructor
     */
    public UEStudyAnalyst(File f) {
        //globa = new JSONGlobalAnalyzer(fn);
        filea = new JSONFileAnalyzer();
        FILE = f;
        JSON = JSONReader.getInstance().getJSONFromTxtFile(FILE.getAbsolutePath());
    }

    private String getStart() {
        if (JSON.get(UETerm.START.toString()) != null) {
            return JSON.get(UETerm.START.toString()).toString().replace("\"", "");
        }
        return "/";
    }

    private String getEnd() {
        if (JSON.get(UETerm.END.toString()) != null) {
            return JSON.get(UETerm.END.toString()).toString().replace("\"", "");
        }
        return "/";
    }

    private String getDuration() {
        long millis = getTotalTaskDurationInMilliSecs(JSON);
        return getHoursMinutesSecondsFromMilliseconds(millis);
    }

    private String getDialogType() {
        if (JSON.get(UETerm.TYPE.toString()) != null) {
            return JSON.get(UETerm.TYPE.toString()).toString().replace("\"", "");
        }
        return "/";
    }
    
    private String getBrowser() {
        if (JSON.get(UETerm.BROW.toString()) != null) {
            return JSON.get(UETerm.BROW.toString()).toString().replace("\"", "");
        }
        return "/";
    }

    private String getRealResult() {
        if (JSON.get(UETerm.TYPE.toString()) != null) {
            if (JSON.get(UETerm.TYPE.toString()).equals("\"ClariHIE\"")) {
                return "wirksam";
            } else if (JSON.get(UETerm.TYPE.toString()).equals("\"ClariOQD\"")) {
                return "unwirksam";
            }
        }
        return "/";
    }
    

    private String getResult() {
        //if(JSON.get(UETerm.SOL.toString()) != null){
        if (JSON.get(UETerm.SOL.toString()) != null) {
            String rating = JSON.get(UETerm.SOL.toString()).toString();
            if (rating.equals("\"1\"")) {
                return "wirksam";
            } else if (rating.equals("\"2\"")) {
                return "unentschieden";
            } else if (rating.equals("\"3\"")) {
                return "unwirksam";
            }
        }
        return "/";
    }

    public String getAnalysisResults() {
        StringBuilder bui = new StringBuilder();
        // bui.append("###################################\n");
        bui.append("ANALYSIS OF FILE: " + FILE.getName() + "\n");
        bui.append("###################################\n");
        bui.append("\n");
        bui.append("Start time: \t" + getStart() + "\n");
        bui.append("End time: \t" + getEnd() + "\n");
        bui.append("Task duration: \t" + getDuration() + "\n");
        bui.append("Cons. Result: \t" + getResult() + "\n");
        // bui.append("\n##############\n");
        //bui.append("END OF ANALYIS\n");
//        bui.append("##############\n");
        return bui.toString();
    }

    public static String assembleCSVString(String LogFolderName) {
        StringBuilder bui = new StringBuilder();

        bui.append("filename , UI type , Duration , ResultUser , ResultOrig , "
                + "Browser , StartTime , EndTime\n");

        List<File> logs = JSONReader.getInstance().retrieveAllLogfiles(LogFolderName);
        for (File logfile : logs) {
            System.out.println("parsing: " + logfile.getName());
            UEStudyAnalyst ue = new UEStudyAnalyst(logfile);
            bui.append(logfile.getName());
            bui.append(" , ");
            bui.append(ue.getDialogType());
            bui.append(" , ");
            bui.append(ue.getDuration());
            bui.append(" , ");
            bui.append(ue.getResult());
            bui.append(" , ");
            bui.append(ue.getRealResult());
            bui.append(" , ");
            bui.append(ue.getBrowser());
            bui.append(" , ");
            bui.append(ue.getStart());
            bui.append(" , ");
            bui.append(ue.getEnd());
            bui.append("\n");
        }
        return bui.toString();
    }

    public static void main(String[] args) {

        StringBuilder bui = new StringBuilder();
        List<File> logs = JSONReader.getInstance().retrieveAllLogfiles("G1logs");
        for (File logfile : logs) {
            UEStudyAnalyst ue = new UEStudyAnalyst(logfile);
        }

        String csv = assembleCSVString("G2logs");
        System.out.println(csv);
        try {
            writeCSV("/Users/mafre/Promotion/Projects/2012JuriSearch/UserStudyMar2012/auswertungG2LogsNew.csv", csv);
        } catch (IOException io) {
            io.printStackTrace();
        }

    }

    // TODO; move; general utilit METHODS
    public String getHoursMinutesSecondsFromMilliseconds(long millis) {
        String time = "/";
        if (millis != 0) {
            String format = String.format("%%0%dd", 2);
            millis = millis / 1000;
            String seconds = String.format(format, millis % 60);
            String minutes = String.format(format, (millis % 3600) / 60);
            String hours = String.format(format, millis / 3600);
            time = hours + ":" + minutes + ":" + seconds;
        }

        return time;
    }

    public long getTotalTaskDurationInMilliSecs(JSONObject json) {
        if (json.get(UETerm.START.toString()) != null
                && json.get(UETerm.END.toString()) != null) {
            Date start = parse(json.get(UETerm.START.toString()).toString().replace("\"", ""));
            Date end = parse(json.get(UETerm.END.toString()).toString().replace("\"", ""));

            return end.getTime() - start.getTime();
        }
        return 0;
    }

    public Date parse(String datestring) {

        String[] dateparts = datestring.split(" ");
        String dateToParse = dateparts[1] + " " + dateparts[2];

        Date date = null;
        DateFormat sdf = new SimpleDateFormat("EEE yyyy_MM_dd hh:mm:s", Locale.GERMAN);
        try {
            date = sdf.parse(datestring);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return date;
    }

    public static String makeUTF8(final String toConvert) {
        try {
            return new String(toConvert.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeCSV(String path, String csvContent) throws IOException {

        File f = new File(path);
        f.createNewFile();
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
        try {
            writer.write(csvContent);
            writer.flush();
        } finally {
            writer.close();
        }
    }
}