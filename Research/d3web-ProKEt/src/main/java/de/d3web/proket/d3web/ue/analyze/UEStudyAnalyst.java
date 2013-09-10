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

import de.d3web.proket.d3web.ue.JSONReader;
import de.d3web.proket.d3web.ue.UETerm;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import org.json.simple.JSONObject;

/**
 * Basic class for evaluating logfiles from user studies. Provides basic
 * functionality for getting a summarizing .csv file from a bunch of logfiles.
 * So far: - get complete .csv witout solutions --> every file is contained, no
 * solution evaluations contained - get cleaned .csv with solutions --> only
 * correct/clean files, i.e. with logged start, end, duration --> also can
 * contain (intermed) solution evaluation(s)
 *
 * TODO add functionality to read a to-be-specified file or table that contains
 * the solution names and correct ratings
 * 
 * TODO: logSolution extra --> entfernen
 * 
 *
 * @author Martina Freiberg @date 05.05.2012
 */
public class UEStudyAnalyst {

    private static String PATH_TO_LOGFILES =
            "/Users/mafre/CodingSpace/Research/d3web-ProKEt/LOGS";
    private static String PATH_TO_ANALYSIS =
            "/Users/mafre/CodingSpace/Research/d3web-ProKEt/LOGS/";
    private static String FILENAME_ANALYSIS = "analysis.csv";

    public static void main(String[] args) {

        StringBuilder bui = new StringBuilder();

        /*
         * will contain a list of solution-group-correctrating defs
         */
        ArrayList<JSONObject> solutions = new ArrayList<JSONObject>();

        /*
         * define JSON Object for each solution
         */
        /*JSONObject mainSolution = new JSONObject();
        mainSolution.put(
                UETerm.SNAME.toString(), "Ist das Arbeitsverhältnis wirksam gekündigt worden?");
        mainSolution.put(UETerm.SRAT.toString(), "1");
        mainSolution.put(UETerm.SG.toString(), "1");
        
        JSONObject secondSolution = new JSONObject();
        secondSolution.put(
                UETerm.SNAME.toString(), "Ist das Arbeitsverhältnis formell?");
        secondSolution.put(UETerm.SRAT.toString(), "1");
        secondSolution.put(UETerm.SG.toString(), "1");
        
        solutions.add(mainSolution);
        solutions.add(secondSolution);
        */
        //String csv = retrieveCleanedCSVWithSolutions(
          //      PATH_TO_LOGFILES, solutions);

        
        String csv = retrieveCompleteCSVNoSolutions(
              PATH_TO_LOGFILES);

        try {
            writeCSV(PATH_TO_ANALYSIS + FILENAME_ANALYSIS, csv);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * Retrieves a csv representation/summary of the logfiles contained in the
     * provided log folder. Retrieves a CLEANED csv, i.e. containing no "empty"
     * entries, which are defined as incomplete, i.e. not containing valid start
     * and end values
     *
     * @param LogFolderName name of the log folder
     * @param sdefs a collection of solutions and their corresponding ratings
     * (i.e., ratings they should originally have if the case has been solved
     * correctly
     *
     * @return a csv file containing all the results
     */
    public static String retrieveCleanedCSVWithSolutions(
            String LogFolderName, ArrayList<JSONObject> sDefs) {

        StringBuilder bui = new StringBuilder();

        // Header Line of CSV: first basic stuff
        bui.append("filename , group , UI type , Duration , "
                + "Browser , StartTime , EndTime");

        // Header Line of CSV: additionally add Solution Names of sols to analyze
        if (!sDefs.isEmpty()) {
            for (JSONObject jo : sDefs) {
                bui.append(" , ");
                bui.append(jo.get(UETerm.SNAME.toString()).toString());
            }
        }
        bui.append("\n");


        List<File> logs =
                JSONReader.getInstance().retrieveAllLogfiles(LogFolderName);

        for (File logfile : logs) {

            UEBasicFileAnalyzer uebfa = new UEBasicFileAnalyzer(logfile);

            // only include "cleaned"=correct logs, i.e. with start,end,duration
            if (!uebfa.getStart().equals("/")
                    && !uebfa.getEnd().equals("/")
                    && !uebfa.getDuration().equals("/")) {

                bui.append(logfile.getName());
                bui.append(" , ");
                bui.append(uebfa.getGroup());
                bui.append(" , ");
                bui.append(uebfa.getDialogType());
                bui.append(" , ");
                bui.append(uebfa.getDuration());
                bui.append(" , ");
                bui.append(uebfa.getBrowser());
                bui.append(" , ");
                bui.append(uebfa.getStart());
                bui.append(" , ");
                bui.append(uebfa.getEnd());

                if (!sDefs.isEmpty()) {
                    TreeMap<String, String> interMedSolRatings =
                            uebfa.getSolutionsEvaluationForGroupFile(
                                sDefs, uebfa.getGroup());

                    for (String value : interMedSolRatings.values()) {
                        bui.append(" , ");
                        bui.append(value);
                    }
                }

                bui.append("\n");
            }
        }
        return bui.toString();
    }

    /**
     * Retrieves a csv representation/summary of the logfiles contained in the
     * provided log folder.
     *
     * @param LogFolderName name of the log folder
     * @param solAndRat a collection of solutions and their corresponding
     * ratings (i.e., ratings they should originally have if the case has been
     * solved correctly
     *
     * @return a csv file containing all the results
     */
    public static String retrieveCompleteCSVNoSolutions(String LogFolderName) {
        StringBuilder bui = new StringBuilder();

        // basic csv stuff, always needed
        bui.append("filename , group , UI type , Duration , "
                + "Browser , StartTime , EndTime, CoreIssue-KuendigungWirksam \n");

        List<File> logs = JSONReader.getInstance().retrieveAllLogfiles(LogFolderName);

        for (File logfile : logs) {

            UEBasicFileAnalyzer uebfa = new UEBasicFileAnalyzer(logfile);

            // only include correct logs, i.e. with start end and duration
            if (!uebfa.getStart().equals("/")) {

                bui.append(logfile.getName());
                bui.append(" , ");
                bui.append(uebfa.getGroup());
                bui.append(" , ");
                bui.append(uebfa.getDialogType());
                bui.append(" , ");
                bui.append(uebfa.getDuration());
                bui.append(" , ");
                bui.append(uebfa.getBrowser());
                bui.append(" , ");
                bui.append(uebfa.getStart());
                bui.append(" , ");
                bui.append(uebfa.getEnd());
                bui.append(" , ");
                bui.append(uebfa.getCoreIssue());
                bui.append(" , ");
                bui.append(uebfa.getGroup());
                bui.append("\n");
            }
        }
        return bui.toString();
    }

    /**
     * Write given csv content in the form of a String object to a defined path
     *
     * @param path filepath where to write the csv
     * @param csvContent the content to be written
     * @throws IOException
     */
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
