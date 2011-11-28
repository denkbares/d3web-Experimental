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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
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

/**
 * Basic class for reading in JSON files for further processing.
 * Files basically need to be stored in .txt format and are required to be
 * valid JSON.
 * 
 * Is implemented as Singleton as one reader is enough.
 * 
 * TODO: what about Umlauts?!
 * 
 * @author M. Freiberg
 * @date 26/11/2011
 */
public class JSONReader implements Serializable{

    private static JSONReader INSTANCE = null;
    private static JSONParser parser;
    private static ContainerFactory cf;
    
    /* The String representation of the following finals needs to be exactly
    as in the .txt logfile! */
    private static final String BROWSER = "browser";
    private static final String USER = "user";
    private static final String RESULT = "result";
    private static final String START = "start";
    private static final String END = "end";
    private static final String CLICKED = "clickedobjects";
    
    /* SVUID*/
    private static final long serialVersionUID = -175322145587796331L;

    /* The required custom date format */
    private static final String DATE_FORMAT = "yyyy MMM dd HH:mm:ss";
   
    
    /**
     * Singleton creator for retrieving the JSONReader instance
     * @return the one and only JSONReader instance
     */
    public static JSONReader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JSONReader();
        }
        return INSTANCE;
    }

    
    /**
     * Singleton JSONReader constructor
     */
    private JSONReader() {
        parser = new JSONParser();

        cf = new ContainerFactory() {

            public List creatArrayContainer() {
                return new LinkedList();
            }

            public Map createObjectContainer() {
                return new LinkedHashMap();
            }
        };
    }

    
    /**
     * Creates and retrieves a JSONObject from a given textfile.
     * Textfile needs to be .txt and contents need to be valid JSON.
     * 
     * @param file the textfile to be transformed into JSON.
     * @return the created JSONObject.
     */
    public JSONObject getJSONFromTxtFile(String file){
        
        HashMap<String, String> logdata = parseFile(file);
        JSONObject ob = new JSONObject();
        
        ob.put(BROWSER, logdata.get(BROWSER).toString());
        ob.put(USER, logdata.get(USER).toString());
        ob.put(START, logdata.get(START).toString());
        ob.put(END, logdata.get(END).toString());
        ob.put(RESULT, logdata.get(RESULT).toString());
        ob.put(CLICKED, getJSONArrayFromString(logdata.get(CLICKED).toString()));
        
        return ob;
    }
    
    
    /**
     * Read the contents of a textfile file (.txt, needs to contain valid JSON) 
     * into an internal HashMap-representation for further processing.
     * 
     * @param file the file to be read
     * @return the internal HashMap representation of the textfile contents
     */
    private HashMap<String, String> parseFile(String file) {

        HashMap<String, String> logdata = new HashMap<String, String>();
        
        // opening the file and reading it
        int ch;
        StringBuffer jsonstring = new StringBuffer("");
        FileInputStream fin = null;

        try {
            fin = new FileInputStream(file);
            while ((ch = fin.read()) != -1) {
                jsonstring.append((char) ch);
            }
            fin.close();

        } catch (FileNotFoundException e) {
        } catch (IOException ioe) {
        }

        // parsing the streamed contents and writing the internal HashMap
        try {
            Map json = (Map) parser.parse(jsonstring.toString(), cf);
            
            Iterator iter = json.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String keyval = entry.getKey().toString();
                String value = JSONValue.toJSONString(entry.getValue());

                if (keyval.equals(BROWSER)) {
                    logdata.put(BROWSER, value);
                } else if (keyval.equals(USER)) {
                    logdata.put(USER, value);
                } else if (keyval.equals(RESULT)) {
                    logdata.put(RESULT, value);
                } else if (keyval.equals(START)) {
                    logdata.put(START, value);
                } else if (keyval.equals(END)) {
                    logdata.put(END, value);
                } else if (keyval.equals(CLICKED)) {
                    logdata.put(CLICKED, value);
                }
            }
        } catch (ParseException pe) {
            System.out.println(pe);
        }
        
        return logdata;
    }

 
    /**
     * Retrieves a JSONArray from a given String representation of a JSONArray
     * structure.
     * 
     * @param s the String representation of the JSONArray
     * @return the JSONAray
     */
    private JSONArray getJSONArrayFromString(String s) {

        JSONParser p = new JSONParser();
        JSONArray ja = null;
        
        try {
            ja = (JSONArray)p.parse(s);

        } catch (ParseException pe) {
            System.out.println(pe);
        }

        return ja;
    }


    
    public static void main(String[] args) {
        JSONReader reader = JSONReader.getInstance();
        JSONObject jo = reader.getJSONFromTxtFile("TESTA.txt");
        
        System.out.println(((JSONArray) jo.get(CLICKED)).get(0));
        System.out.println(((JSONArray) jo.get(CLICKED)).get(1));
        System.out.println(jo.get(BROWSER));


        /* LATER FOR FORMATTING DATE Date end = null;
        
        // SimpleDateFormat to match custom log representation:
        // 2011 Nov 25 Fri 12:03:23
        DateFormat format = new SimpleDateFormat(DATE_FORMAT);
        
        try {
        end = (Date) format.parse(logdata.get(END).toString());
        } catch (java.text.ParseException e) {
        e.printStackTrace();
        }*/

    }
}
