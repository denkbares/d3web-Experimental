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

    private static final String BROWSER = "browser";
    private static final String USER = "user";
    private static final String RESULT = "result";
    private static final String START = "start";
    private static final String END = "end";
    private static final String CLICKED = "clickedwidgets";
    private static final String LOAD = "LOAD";
    private static final String ID = "id";
    
	protected static final String VAL = "value";
    
    private static JSONFileAnalyzer instance = null;

    public boolean isOneFlow(JSONObject json) {
        return json.containsKey(START) && json.containsKey(END) &&
                !containsLoad(json);
    }

    public boolean isOfUser(JSONObject json, String user) {
        return json.containsKey(USER)
                && json.get(USER).toString().replace("\"", "").equals(user);
    }

    public boolean containsEnd(JSONObject json) {
        return json.containsKey(END);
    }

    public boolean containsLoad(JSONObject json) {
        if (json.containsKey(CLICKED)) {
            JSONArray jar = (JSONArray) json.get(CLICKED);
            for (Object jjar : jar) {
                if (((JSONObject) jjar).containsKey(ID) &&
                        ((JSONObject)jjar).get(ID).equals("LOAD")) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public String getLoadValue(JSONObject json){
        if (json.containsKey(CLICKED)) {
            JSONArray jar = (JSONArray) json.get(CLICKED);
            for (Object jjar : jar) {
                
                JSONObject job = (JSONObject)jjar;
                if (job.containsKey(ID) &&
                        job.get(ID).equals("LOAD")) {
                    return job.get(VAL).toString();
                }
            }
        }
        return "";
    }

    public boolean containsEndMultiple(JSONObject json) {
        return containsEnd(json)
                && containsLoad(json);
    }
}
// elemente mehrfach geclickt
    // hilfe-element zu frage wie oft aufgerufen