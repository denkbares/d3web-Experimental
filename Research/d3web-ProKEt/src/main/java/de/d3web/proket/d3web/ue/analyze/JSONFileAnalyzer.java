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

import de.d3web.proket.d3web.ue.UETerm;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Basic class for analyzing JSON content. Operating on single file only.
 * Retrieves values such as whether the file is of a given user, whether its a
 * single flow (start and end) input, etc.
 *
 * @author Martina Freiberg @date 28/11/2011
 */
public class JSONFileAnalyzer {

    private static JSONFileAnalyzer instance = null;

    public boolean isOneFlow(JSONObject json) {
        return json.containsKey(UETerm.START.toString())
                && json.containsKey(UETerm.END.toString())
                && !containsLoad(json);
    }

    public boolean isOfUser(JSONObject json, String user) {
        return json.containsKey(UETerm.USER.toString())
                && json.get(UETerm.USER.toString()).toString().replace("\"", "").equals(user);
    }

    public boolean containsEnd(JSONObject json) {
        return json.containsKey(UETerm.END.toString());
    }

    public boolean containsLoad(JSONObject json) {
        if (json.containsKey(UETerm.CLICKED.toString())) {
            JSONArray jar = (JSONArray) json.get(UETerm.CLICKED.toString());
            for (Object jjar : jar) {
                if (((JSONObject) jjar).containsKey(UETerm.ID.toString())
                        && ((JSONObject) jjar).get(UETerm.ID.toString()).equals("LOAD")) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getLoadValue(JSONObject json) {
        if (json.containsKey(UETerm.CLICKED.toString())) {
            JSONArray jar = (JSONArray) json.get(UETerm.CLICKED.toString());
            for (Object jjar : jar) {

                JSONObject job = (JSONObject) jjar;
                if (job.containsKey(UETerm.ID.toString())
                        && job.get(UETerm.ID.toString()).equals("LOAD")) {
                    return job.get(UETerm.VAL.toString()).toString();
                }
            }
        }
        return "";
    }

    public boolean containsEndMultiple(JSONObject json) {
        return containsEnd(json)
                && containsLoad(json);
    }

    /**
     * Checks whether a given case had been started but not worked on anymore (
     * in that case, only the base log info start, browser and user is contained
     * ). Happens for example, if user finishes a case (after finishing
     * automatically a new case-log-template is created) and quits the system.
     *
     * @param json the file to check
     * @return true if the case was started-only
     */
    public boolean isStartedButEmpty(JSONObject json) {

        Set<String> keys = json.keySet();

        if (keys.size() == 3
                && json.containsKey(UETerm.START.toString())
                && json.containsKey(UETerm.BROW.toString())
                && json.containsKey(UETerm.USER.toString())) {

            return true;
        }

        return false;
    }

    /**
     * Parses a given String (e.g., from the logfile) into a Date object
     * Logged date is in the format: 2012_11_1 10:46:0
     *
     * // TODO: store Date formats somewhere globally --> Global Settings?!
     * // TODO: refactor into parse(datestring, dateformat) and move to global
     * //   utility methods
     * @param datestring the String to be parsed
     * @return the Date from the parsed String
     */
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

    
    /**
     * Retrieves the task duration in milliseconds of a given logfile
     * @param json the logfile
     * @return the task duration in milliseconds
     */
    public long getTotalTaskDurationInMilliSecs(JSONObject json) {

        Date start = parse(json.get(UETerm.START.toString()).toString().replace("\"", ""));
        Date end = parse(json.get(UETerm.END.toString()).toString().replace("\"", ""));

        return end.getTime() - start.getTime();
    }

  
}
// elemente mehrfach geclickt
    // hilfe-element zu frage wie oft aufgerufen