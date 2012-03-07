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
package de.d3web.proket.d3web.run;

import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.ue.analyze.JSONReader;
import de.d3web.proket.d3web.ue.JSONLogger;
import de.d3web.proket.utils.GlobalSettings;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONObject;

/**
 * Utility class that contains all methods required for processing the logging
 * requests coming from various potential Dialog Servlets, such as D3webDialog,
 * MediastinitisDialog, and also the prototype Servlets. Therefore, the methods
 * here receive the correctly formatted values right "as to be logged".
 *
 * @author Martina Freiberg
 *
 * @date 30.12.2011
 */
// TODO Logger needs to be stored in httpSEssion
// TODO IMPORTANT: when storing Logger in httpSession, always provide Logger
// with the methods here and store logfilename in Logger class!
// Here, ONLY the helper methods are allowed!
public class ServletLogUtils {

    
    /**
     * Initializes the logging helper servlet by getting the logger, and
     * assembling the logfilename (date plus ???) for logging prototypes
     *
     * @param jlogger
     * @param loggingstart
     * @param httpsess
     */
   /* public static void initForPrototypeDialogs(
            JSONLogger jlogger, 
            Date loggingstart, 
            HttpSession httpsess) {

        // TODO: refactor here as to create different paths dependent from
        // prototype type
        
        GlobalSettings.getInstance().setLogFolder(
                GlobalSettings.getInstance().getServletBasePath()
                + "../../Prototype-Data/logs");

        logger = jlogger; // set the logger

        // format timestring for logfilename
        String formatted = DATE_FORMAT_DEFAULT.format(loggingstart);

        logfilename = formatted + "_" + httpsess.getId() + ".txt";
    }

    /**
     * Resets the logfilename. Needed for example, when an old session is loaded
     * --> then, not a per default created newly named logfile should be used,
     * but the already existing one.
     *
     * @param idToCheck part of the name of the potentially already existing
     * logfile name.
     */
    public static void resetLogfileName(String idToCheck, JSONLogger logger) {

        List<File> logfiles = JSONReader.getInstance().retrieveAllLogfiles(
                GlobalSettings.getInstance().getLogFolder());

        for (File f : logfiles) {

            // check whether a file for newSid already exists
            if (f.getName().contains(idToCheck)) {

                JSONObject oldContents =
                        JSONReader.getInstance().getJSONFromTxtFile(f.getAbsolutePath());
                logger.setLogfileName(f.getName());
                logger.restore(oldContents);
                logger.writeJSONToFile();
            }
        }
    }

    /**
     * Logs initial values username and browser info. Only to be done once,
     * therefore check global setting "initlogged"
     *
     * TODO: factor out session start.
     *
     * @param browser the browser used
     * @param user the user that is logged
     */
    public static void logBaseInfo(String browser, String user, String start,
            JSONLogger logger) {

        // TODO: adapt if not global anymore
        // give values to logger
        logger.logStartValue(start);
        logger.logBrowserValue(browser);
        logger.logUserValue(user);

        logger.writeJSONToFile();

    }

    /**
     * Logs the end of a session. "cleans up" by creating a new Logger and
     * sending it to D3webConnector, and sets the "is logging initialized" flag
     * to false.
     *
     * @param end String representation of logging end time
     */
    public static void logSessionEnd(String end, JSONLogger logger) {
        logger.setEndValOnLoad(end);
        logger.logEndValue(end);
        logger.writeJSONToFile();
    }

    /**
     * Logs the click on a non-KB widget of the dialog, e.g., click on the save
     * button, click on a language flag....
     *
     * @param widgetID id of the widget to be logged
     * @param time the timestamp
     * @param language the language, when a language widget was clicked
     */
    //TODO: factor out click on language widget?!
    protected static void logWidget(String widgetID, String time, String language,
            JSONLogger logger) {

        if (widgetID.contains("LANGUAGE")) {
            logger.logClickedObjects(
                    "LANGUAGE", time, language);
        } else {
            if (widgetID.contains("reset")) {
                logger.logClickedObjects(
                        "RESETEND", time, "RESET");
            } else if (widgetID.contains("statistics")) {
                logger.logClickedObjects(
                        "STATISTICS", time, "STATISTICS");
            } else if (widgetID.contains("summary")) {
                logger.logClickedObjects(
                        "SUMMARY", time, "SUMMARY");
            } else if (widgetID.contains("followup")) {
                logger.logClickedObjects(
                        "FOLLOWUP", time, "FOLLOWUP");
            } else if (widgetID.contains("savecase")) {
                logger.logClickedObjects(
                        "SAVE", time, "SAVE");
            } else if (widgetID.contains("FFButton")) {
                logger.logClickedObjects(
                        "FFButton", time, "FEEDBACK FORM");
            } else if (widgetID.contains("UEQButton")) {
                logger.logClickedObjects(
                        "UEQButton", time, "USAB QUEST");
            }
        }
        logger.writeJSONToFile();

    }

    /**
     * Logs the loading=resuming of a case
     *
     * @param time
     * @param session id of the resumed session
     */
    protected static void logResume(String time, String session, JSONLogger logger) {
        logger.writeEndToIntermedEnd();
        logger.logClickedObjects("LOAD", time, session);
        logger.writeJSONToFile();
    }

    /**
     * Log mouseover on information popup.
     *
     * @param id
     * @param start
     * @param timediff
     */
    protected static void logInfoPopup(String id, String start, String timediff,
            JSONLogger logger) {
        logger.logClickedObjects(id, start, timediff);
        logger.writeJSONToFile();
    }

    /**
     * Log a question/value pair, i.e., answered questions, i.e. non-dialog-UI
     * widgets that are specified in the knowledge base (questions, answers...)
     *
     * @param question
     * @param value
     * @param logtime
     */
    protected static void logQuestionValue(String question, String value, String logtime,
            JSONLogger logger) {
        logger.logClickedObjects(question, logtime, value);
        logger.writeJSONToFile();
    }

    /**
     * Log attempts to enter not allowed values, e.g. numerical values outside a
     * given allowed range.
     *
     * @param request
     */
    protected static void logNotAllowed(String logtime, String value, String question,
            JSONLogger logger) {

        logger.logClickedObjects(
                "NOTALLOWED_" + question,
                logtime,
                value);
        logger.writeJSONToFile();
    }
    
    public static void logDiagnosis(String solutiontext, String rating, JSONLogger logger){
        logger.logDiagnosis(solutiontext, rating);
    }
}
