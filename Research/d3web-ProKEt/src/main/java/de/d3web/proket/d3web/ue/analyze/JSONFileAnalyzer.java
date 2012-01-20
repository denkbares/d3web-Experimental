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
    private static final String CLICKED = "clickedobjects";
    private static JSONFileAnalyzer instance = null;
    
    

    public boolean isOneFlow(JSONObject json) {
       return json.containsKey(START) && json.containsKey(END);
    }
    
    public boolean isOfUser(JSONObject json, String user){
       return json.containsKey(USER) 
               && json.get(USER).toString().replace("\"", "").equals(user);
    }
}
// elemente mehrfach geclickt
    // hilfe-element zu frage wie oft aufgerufen