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
package de.d3web.proket.d3web.ue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.d3web.proket.utils.GlobalSettings;
import java.io.*;
import java.util.*;

/**
 * Basic class for writing logfiles in the JSON format. Collects values to log
 * in internal JSON representation. Can write JSONObject into corresponding
 * textfile.
 *
 * TODO: Umlauts!
 *
 * @author M. Freiberg @date 24/11/2011
 */
public class JSONLogger {

    private JSONObject logfile = new JSONObject();
    private JSONArray clickarray = new JSONArray();
    private String filename = "defaultlog.txt";
    private static final long serialVersionUID = -5766536853041423918L;
    private String endValOnNewLoad = "";

    public JSONLogger(String filename) {
        this.filename = filename;
    }

    /**
     * Adds the given value to the internal JSONObject representation.
     *
     * @param value the value to be added
     */
    public void logBrowserValue(String value) {
        logfile.put(UETerm.BROW.toString(), value);
    }

    /**
     * Adds the given value to the internal JSONObject representation.
     *
     * @param value the value to be added
     */
    public void logUserValue(String value) {
        logfile.put(UETerm.USER.toString(), value);
    }

    /**
     * Adds the given value to the internal JSONObject representation.
     *
     * @param value the value to be added
     */
    public void logResultValue(String value) {
        logfile.put(UETerm.RES.toString(), value);
    }

    /**
     * Adds the given value to the internal JSONObject representation.
     *
     * @param value the value to be added
     */
    public void logStartValue(String value) {
        logfile.put(UETerm.START.toString(), value);
    }

    /**
     * Adds the given value to the internal JSONObject representation.
     *
     * @param value the value to be added
     */
    public void logEndValue(String value) {
        logfile.put(UETerm.END.toString(), value);
    }

    /**
     * Adds a new JSONObject representing a clicked widget to the
     * clicked-objects-JSONArray of the JSON logfile. Therefore, first a new
     * JSONObject is created and then added to the JSONArray of the JSON
     * logfile.
     *
     * @param id for the JSONObject to be added to the array of the JSON
     * logfile.
     * @param timestamp timestamp for the JSONObject to be added to the array of
     * the JSON logfile.
     * @param value value for the JSONObject to be added to the array of the
     * JSON logfile.
     */
    public void logClickedObjects(Object id, Object timestamp, Object value) {

        JSONObject ob = new JSONObject();
        ob.put(UETerm.ID.toString(), id);
        ob.put(UETerm.TS.toString(), timestamp);
        ob.put(UETerm.VAL.toString(), value);

        JSONArray existingClickedObjects = getClickedObjects();
        existingClickedObjects.add(ob);

        logfile.put(UETerm.CLICKED.toString(), existingClickedObjects);
    }

    // get the existing clicked objects already stored in the internal array
    private JSONArray getClickedObjects() {
        return clickarray;
    }

    /**
     * Retrieve the already stored logdata as JSONObject
     *
     * @return the JSONObject representation of the logged data
     */
    public JSONObject getLogAsJSON() {
        return logfile;
    }

    /**
     * Restore values from a given existing JSON file into this currently logged
     * file.
     *
     * @param oldJSON the existing JSON
     */
    public void restore(JSONObject oldJSON) {
        Set<String> keysOld = oldJSON.keySet();
        for (String key : keysOld) {
            Object saved = oldJSON.get(key);
            if (saved instanceof String) {
                logfile.put(key, ((String) saved).replace("\"", ""));
            } else if (saved instanceof JSONArray) {
                clickarray = (JSONArray) saved;
            }
        }
    }

    public void setLogfileName(String logfilename) {
        this.filename = logfilename;
    }

    /**
     * Write the logdata currently stored internally in the JSONObject into a
     * specified file.
     *
     * @param file the file, the logdata is written to.
     */
    public void writeJSONToFile() {
        BufferedWriter bw = null;

        try {
            File dir = new File(GlobalSettings.getInstance().getLogFolder() + "/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String filepath = dir + "/" + filename;

            Writer w = new OutputStreamWriter(new FileOutputStream(filepath), "UTF8");
            bw = new BufferedWriter(w);
            bw.write(getLogAsJSON().toString());

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Close the BufferedWriter
            try {
                if (bw != null) {
                    bw.flush();
                    bw.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Rewrites an existing end-tag in logfile to a break-tag. This is a flag, 
     * that an already completed logfile is reopened again. Is done only once,
     * as we assume that once completed files remain (mostly) completed.
     */
    public void writeEndToIntermedEnd() {

        String prevEnd = "";
        if (logfile.get(UETerm.END.toString()) != null) {
           prevEnd = (String) logfile.get(UETerm.END.toString());

            if (!prevEnd.equals("")) {
                    logfile.put(UETerm.BREAK.toString(), prevEnd);
                
            }
        }
    }
    
    public void setEndValOnLoad(String endVal){
        endValOnNewLoad = endVal;
    }
    

    public static void main(String[] args) {
        JSONLogger logger = new JSONLogger("tester.txt");
        logger.logBrowserValue("firefox");
        logger.logUserValue("user");
        logger.logResultValue("result");
        logger.logStartValue("start");
        logger.logEndValue("end");

        logger.logClickedObjects("id1", "now", "answer1");

        logger.logClickedObjects("id2", "nowlater", "answer2");

        logger.writeJSONToFile();

        int ch;
        StringBuffer strContent = new StringBuffer("");
        FileInputStream fin = null;

        try {
            fin = new FileInputStream("TESTI.txt");

            while ((ch = fin.read()) != -1) {
                strContent.append((char) ch);
            }

            fin.close();

        } catch (FileNotFoundException e) {
        } catch (IOException ioe) {
        }

        JSONParser parser = new JSONParser();
        ContainerFactory containerFactory = new ContainerFactory() {

            @Override
            public List creatArrayContainer() {
                return new LinkedList();
            }

            @Override
            public Map createObjectContainer() {
                return new LinkedHashMap();
            }
        };

        Map json = null;
        try {
            json = (Map) parser.parse(strContent.toString(), containerFactory);
        } catch (ParseException pe) {
        }

        Iterator iter = json.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            System.out.println(entry.getKey() + "=>" + entry.getValue());
        }

        System.out.println(JSONValue.toJSONString(json));

    }
}
