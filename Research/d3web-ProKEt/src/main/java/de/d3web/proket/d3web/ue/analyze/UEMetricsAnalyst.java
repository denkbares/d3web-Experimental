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

import java.util.List;
import org.json.simple.JSONObject;

/**
 * Class for calculating and retrieving several usability-analysis related
 * metrics
 *
 * // TODO: what about CONSULTATION logging? We have to think about how cases
 * are defined as "ended"/successful there, as the methods here currently only
 * consider data entry = documentation case.
 *
 * // TODO multiple flow per USER?! // TODO
 * getTotalNrSuccessfulCasesConsultation(){} // TODO
 * getTotalNrUnsccessfulCasesConsultation(){}
 *
 * @author Martina Freiberg @date 20/01/2012
 */
public class UEMetricsAnalyst {

    private JSONGlobalAnalyzer globa = null;
    private JSONFileAnalyzer filea = null;
    private String ROOT = "";

    /*
     * Constructor
     */
    public UEMetricsAnalyst(String rootDir) {
        globa = new JSONGlobalAnalyzer(rootDir);
        filea = new JSONFileAnalyzer();
        ROOT = rootDir;
    }

    /**
     * Retrieves the number of one flow cases (containing start and end value)
     * for a defined user. Only COMPLETE cases are considered, i.e. containing
     * both start and end.
     *
     * @param user the defined user
     * @return the number of one flow cases
     */
    public int getNrOneFlowCasesOfUser(String user) {
        List<JSONObject> oneFlowJsons = globa.getOneFlowCases(filea);
        int counter = 0;
        for (JSONObject json : oneFlowJsons) {
            if (filea.isOfUser(json, user)) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * Retrieves the total number of one flow cases (containing start and end
     * value) Only COMPLETE cases are considered, i.e. containing both start and
     * end.
     *
     * @return the number of one flow cases
     */
    public int getNrOneFlowCases() {
        List<JSONObject> oneFlowJsons = globa.getOneFlowCases(filea);
        return oneFlowJsons.size();
    }

    /**
     * Retrieves the total numer of multiple-flow cases (that were
     * broken/resumed and thus contain LOAD statement(s) Only COMPLETE cases are
     * considered, which are those that contain a start- load, load-save,
     * load-end or similar chain, but always start and end. One flow cases are
     * NOT included here!
     *
     * @return the number of multiple flow cases
     */
    public int getNrMultipleFlowCases() {
        return globa.getMultipleFlowCases(filea).size();
    }

    /**
     * Retrieve the total number of successful cases. Successful case means for
     * us, that all questions have been answered and thus start and end values
     * of time are provided. Corresponds to #OneFlowCases PLUS
     * #MultipleFlowCases
     *
     * @return the number of successful cases
     */
    public int getTotalNrSuccessfulCases() {
        return getNrOneFlowCases() + getNrMultipleFlowCases();
    }

    /**
     * Counterpart for getTotalNrSuccessfulCases(): unsuccessful cases are all
     * cases that were somehow started but not completely entered/finished
     *
     * @return the total number of unsuccessful cases
     */
    public int getTotalNrUnsuccessfulCases() {
        return getTotalNrOfCases() - getTotalNrSuccessfulCases();
    }

    /**
     * Retrieve total number of cases, independent whether those were completely
     * finished or only partly entered, resumed, or the like. Corresponds to the
     * total number of logfiles in the specified root directory that is analyzed
     * MINUS the logfiles that contain only the base-log (start, brow, user) but
     * no real additional data.
     *
     * @return the total number of cases
     */
    public int getTotalNrOfCases() {
        return globa.getAllCases().size() - getNrStartedEmptyCases();
    }

    /**
     * Retrieve the number of all logfiles in the log dir. Includes empty cases
     * (with only base log) and all forms of logs (successful, unsuccessful...)
     *
     * @return the total nr of logfiles
     */
    public int getTotalNrOfLogs() {
        return globa.getAllCases().size();
    }

    /**
     * Retrieves number of cases that have been started (and thus contain the
     * start log - user, browser, start time - but nothing more).
     *
     * @return the number of started but empty cases
     */
    public int getNrStartedEmptyCases() {
        int count = 0;
        List<JSONObject> allcases = globa.getAllCases();

        for (JSONObject json : allcases) {
            if (filea.isStartedButEmpty(json)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Success Rate / Correctness metric: = correctly completed tasks / total
     * tasks * 100
     *
     * @return the success rate value
     */
    public float getSuccessRate() {
        return (float) getTotalNrSuccessfulCases() / (float) getTotalNrOfCases() * 100;
    }

    /**
     * Error Rate metric: = successful completed tasks / failed tasks
     *
     * @return the error rate value
     */
    public float getErrorRate() {
        return (float) getTotalNrUnsuccessfulCases() / (float) getTotalNrOfCases() * 100;
    }
    
    /**
     * Calculate and retrieve the (arithmethic) mean duration of all
     * successful tasks (i.e., tasks complete with start and end time)
     * Retrieves the task duration of each successful task in milliseconds,
     * adds them up, and divides them by the number of all successful cases.
     *  
     * @return the mean task duration in seconds
     */
    public String getMeanTaskDuration(){
        float dur = 0;
        List<JSONObject> allCases = globa.getAllSuccessfulCases(filea);
        for(JSONObject jcase: allCases){
            dur += filea.getTotalTaskDurationInMilliSecs(jcase);            
        }
        
        long millis = (long)dur/allCases.size(); 
        
        return getHoursMinutesSecondsFromMilliseconds(millis);
        
    }

    public String getAnalysisResults() {
        StringBuilder bui = new StringBuilder();
        bui.append("###################################\n");
        bui.append("ANALYSIS OF DIRECTORY: " + ROOT + "\n");
        bui.append("###################################\n");
        bui.append("\n");
        bui.append("Total # of logs: \t\t\t" + getTotalNrOfLogs() + "\n");
        bui.append("Total # of cases: \t\t\t" + getTotalNrOfCases() + "\n");
        bui.append("Total # of one-flow cases: \t\t" + getNrOneFlowCases() + "\n");
        bui.append("Total # of multiple-flow cases: \t" + getNrMultipleFlowCases() + "\n");
        bui.append("Total # of successful cases: \t\t" + getTotalNrSuccessfulCases() + "\n");
        bui.append("Total # of unsuccessful cases: \t\t" + getTotalNrUnsuccessfulCases() + "\n");
        bui.append("Total # of started-but-empty cases: \t" + getNrStartedEmptyCases() + "\n\n");
        bui.append("------------------------------------\n\n");
        bui.append("Success Rate / Correctness: \t\t" + getSuccessRate() + "\n");
        bui.append("Error Rate: \t\t\t\t" + getErrorRate() + "\n");
        bui.append("Mean task duration (arithmetic): \t" + getMeanTaskDuration() + "\n\n");
        //bui.append("USER-specific analysis: " + "user\n");
        //bui.append("-----------------------------------");
        //bui.append("\n");
        //bui.append("Total # one-flow cases: \t\t" + getNrOneFlowCasesOfUser("user") + "\n");
        //bui.append("\n");
        bui.append("\n##############\n");
        bui.append("END OF ANALYIS\n");
        bui.append("##############\n");

        return bui.toString();
    }

    public static void main(String[] args) {
        UEMetricsAnalyst ue = new UEMetricsAnalyst("DEFAULT-DATA");
        System.out.println(ue.getAnalysisResults());
    }


    // TODO; move; general utilit METHODS
      public String getHoursMinutesSecondsFromMilliseconds(long millis) {

        String format = String.format("%%0%dd", 2);
        millis = millis / 1000;
        String seconds = String.format(format, millis % 60);
        String minutes = String.format(format, (millis % 3600) / 60);
        String hours = String.format(format, millis / 3600);
        String time = hours + ":" + minutes + ":" + seconds;
        return time;
    }
}
