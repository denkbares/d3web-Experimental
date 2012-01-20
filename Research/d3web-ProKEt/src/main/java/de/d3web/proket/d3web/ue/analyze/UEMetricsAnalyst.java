/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.d3web.proket.d3web.ue.analyze;

import java.util.List;
import org.json.simple.JSONObject;

/**
 * Class for calculating and retrieving several usability-analysis related
 * metrics
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
     * for a defined user
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
     * value)
     *
     * @return the number of one flow cases
     */
    public int getNrOneFlowCases() {
        List<JSONObject> oneFlowJsons = globa.getOneFlowCases(filea);
        return oneFlowJsons.size();
    }

    public int getNrMultipleFlowCases() {
        return globa.getMultipleFlowCases(filea).size();
    }

    /**
     * TODO Retrieve the total number of successful cases. Thereby, a successful
     * case means cases, where all questions had been answered and thus start
     * and end values of time are provided
     *
     * @return the number of successful cases
     */
    public int getTotalNumberOfSuccessfulCases() {
        return 0;
    }

    /**
     * Retrieve total number of cases, independent whether those were completely
     * finished or only partly entered, resumed, or the like. Corresponds to the
     * total number of logfiles in the specified root directory that is
     * analyzed.
     *
     * @return the total number of cases
     */
    public int getTotalNrOfCases() {
        return globa.getAllCases().size();
    }

    public String getAnalysisResults() {
        StringBuilder bui = new StringBuilder();
        bui.append("###################################\n");
        bui.append("ANALYSIS OF DIRECTORY: " + ROOT + "\n");
        bui.append("###################################\n");
        bui.append("\n");
        bui.append("Total # of cases: \t\t\t" + getTotalNrOfCases() + "\n");
        bui.append("Total # of one-flow cases: \t\t" + getNrOneFlowCases() + "\n");
        bui.append("Total # of multiple-flow cases: \t" + getNrMultipleFlowCases() + "\n");
        bui.append("\n");
        bui.append("USER-specific analysis: " + "user\n");
        bui.append("-----------------------------------");
        bui.append("\n");
        bui.append("Total # one-flow cases: \t\t" + getNrOneFlowCasesOfUser("user") + "\n");
        bui.append("\n");
        bui.append("##############\n");
        bui.append("END OF ANALYIS\n");
        bui.append("##############\n");

        return bui.toString();
    }

    public static void main(String[] args) {
        UEMetricsAnalyst ue = new UEMetricsAnalyst("DEFAULT-DATA");
        System.out.println(ue.getAnalysisResults());
    }
}
