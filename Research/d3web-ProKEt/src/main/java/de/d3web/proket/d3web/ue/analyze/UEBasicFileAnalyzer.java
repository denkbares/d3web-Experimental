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
import de.d3web.proket.d3web.ue.UEUtils;
import java.io.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Gets the most basic log values out of given logfiles. Examples are the plain
 * retrieval of start time, end time, user group, system type, duration (from
 * start and end val), browser etc.
 *
 * @author Martina Freiberg @date 05.05.2012
 */
public class UEBasicFileAnalyzer {

    private JSONGlobalAnalyzer globa = null;
    private JSONFileAnalyzer filea = null;
    private File FILE;
    private JSONObject JSON;


    /*
     * Constructor
     */
    public UEBasicFileAnalyzer(File f) {
        //globa = new JSONGlobalAnalyzer(fn);
        filea = new JSONFileAnalyzer();
        FILE = f;
        JSON = JSONReader.getInstance().getJSONFromTxtFile(FILE.getAbsolutePath());
    }

    public String getGroup() {
        if (JSON.get(UETerm.GROUP.toString()) != null) {
            return JSON.get(UETerm.GROUP.toString()).toString().replace("\"", "");
        }
        return "/";
    }

    public String getStart() {
        if (JSON.get(UETerm.START.toString()) != null) {
            return JSON.get(UETerm.START.toString()).toString().replace("\"", "");
        }
        return "/";
    }

    public String getEnd() {
        if (JSON.get(UETerm.END.toString()) != null) {
            return JSON.get(UETerm.END.toString()).toString().replace("\"", "");
        }
        return "/";
    }

    public String getDuration() {
        long millis = getTotalTaskDurationInMilliSecs(JSON);
        return UEUtils.getHoursMinutesSecondsFromMilliseconds(millis);
    }

    public String getDialogType() {
        if (JSON.get(UETerm.TYPE.toString()) != null) {
            return JSON.get(UETerm.TYPE.toString()).toString().replace("\"", "");
        }
        return "/";
    }

    public String getBrowser() {
        if (JSON.get(UETerm.BROW.toString()) != null) {
            return JSON.get(UETerm.BROW.toString()).toString().replace("\"", "");
        }
        return "/";
    }

    private JSONArray getCompleteIntermediateResultsString() {
        if (JSON.get(UETerm.ISOL.toString()) != null) {
            return (JSONArray) JSON.get(UETerm.ISOL.toString());
        }
        return new JSONArray();
    }

    /**
     * Retrieves the evaluation of solutions by comparing the ArrayList with the
     * specification of correct solutions and ratings with the actually logged
     * results.
     *
     * @param sDefs the definition of the correct solution ratings; contains
     * solution name, solution rating and the study group (as different study
     * groups can have different rating results for the same solution objects)
     *
     * @return TreeMap with solutionName/evaluation (CORRECT, INCORRECT-with
     * original results, NaN)
     */
    public TreeMap<String, String> getSolutionsEvaluationForGroupFile(
            ArrayList<JSONObject> sDefs, String groupNr) {

        // the results as stored in the logfile
        JSONArray completeInterRes = getCompleteIntermediateResultsString();

        // initialize for the results
        TreeMap<String, String> interEval = new TreeMap();

        // go through all intermediateLoggedResults
        for (Object o : completeInterRes) {
            JSONObject loggedJO = (JSONObject) o;

            if (loggedJO.get(UETerm.SNAME.toString()) != null) {

                // this is the loggedIntermedSolutionID/Name
                String idToCheck = loggedJO.get(UETerm.SNAME.toString()).toString();

                // get corresponding sol/rating definition from the defining 
                // ArrayList with the JSONS
                JSONObject definedJSON =
                        getJSONFromArrayListBySolName(sDefs, idToCheck);

                if (definedJSON != null
                        && definedJSON.get(UETerm.SG.toString()).toString().equals(groupNr)) {


                    String realValue =
                            definedJSON.get(UETerm.SRAT.toString()).toString();
                    String loggedValue =
                            loggedJO.get(UETerm.SRAT.toString()).toString();

                    if (realValue.equals(loggedValue)) {
                        interEval.put(idToCheck, "CORRECT: " + loggedValue);
                    } else {
                        interEval.put(idToCheck, "INCORRECT: was "
                                + loggedValue + " / " + realValue);
                    }

                }
            }
        }
        System.out.println(interEval);

        // additionally go through provided solutions and check whether there
        // are elements that had not been logged
        // TODO REIHENFILGE STIMMT NOCH NICHT 
        for (JSONObject jos : sDefs) {

            if (!interEval.containsKey(jos.get(UETerm.SNAME.toString()).toString())) {
                interEval.put(jos.get(UETerm.SNAME.toString()).toString(), "NaN");
            }
        }
        System.out.println(interEval);
        return interEval;
    }

    private JSONObject getJSONFromArrayListBySolName(
            ArrayList<JSONObject> solDefs, String solName) {

        for (JSONObject jo : solDefs) {
            if (jo.get(UETerm.SNAME.toString()) != null) {
                if (jo.get(UETerm.SNAME.toString()).toString().equals(solName)) {
                    return jo;
                }
            }
        }

        return null;
    }

    public long getTotalTaskDurationInMilliSecs(JSONObject json) {
        if (json.get(UETerm.START.toString()) != null
                && json.get(UETerm.END.toString()) != null) {

            String dateformat = "EEE yyyy_MM_dd hh:mm:s";
            Date start =
                    UEUtils.parseDatestringToDateObject(
                    json.get(
                    UETerm.START.toString()).toString().replace("\"", ""),
                    dateformat,
                    Locale.GERMAN);
            Date end =
                    UEUtils.parseDatestringToDateObject(
                    json.get(
                    UETerm.END.toString()).toString().replace("\"", ""),
                    dateformat,
                    Locale.GERMAN);

            return end.getTime() - start.getTime();
        }
        return 0;
    }
}
