/**
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;

/**
 * Basic class for analyzing JSON content Operating on multiple files. Examples
 * are the total number of cases, successful cases, cases per user...
 *
 * TODO: factor out the number-grapping methods to separate usabilityAnalysis-
 * classes and just leave the json-parsing/retrieving stuff in here
 *
 * @author Martina Freiberg @date 28/11/2011
 */
public class JSONGlobalAnalyzer {

    protected JSONReader jsonreader;
    protected String upmDir;

    /**
     * Constructor setting both the JSONReader to use and the uppermost (parent,
     * root) directory where files to be analyzed are stored
     *
     * @param uppermostDir
     */
    public JSONGlobalAnalyzer(String uppermostDir) {
        jsonreader = JSONReader.getInstance();
        upmDir = uppermostDir;
    }

    /*
     * Retrieve all cases stored in the root directory Independent of whether
     * the cases were successful, resumed, partly finished or the like. Just the
     * number of all partly & completely entered cases.
     *
     * @return the list with all case files
     *
     */
    public List<JSONObject> getAllCases() {
        return jsonreader.retrieveAllLogfilesAsJSON(getRootDir());
    }


    /**
     * Retrieves all cases within the root directory that had been worked
     * through in one flow, thus containing both a start and end time value
     *
     * @return a list of one flow cases, or an empty list if none one flow cases
     * available
     */
    public List<JSONObject> getOneFlowCases(JSONFileAnalyzer filea) {
        List<JSONObject> jsons =
                jsonreader.retrieveAllLogfilesAsJSON(getRootDir());
        List<JSONObject> oneFlowJsons =
                new ArrayList<JSONObject>();

        for (JSONObject json : jsons) {
            if (filea.isOneFlow(json)) {
                oneFlowJsons.add(json);
            }
        }
        return oneFlowJsons;
    }

    public String getRootDir() {
        return upmDir;
    }

    public JSONReader getReader() {
        return jsonreader;
    }
}
