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

import de.d3web.core.session.Session;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.ue.log.JSONLogger;
import de.d3web.proket.utils.GlobalSettings;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Utility class that contains all methods required for processing the
 * requests coming from various potential Dialog Servlets, such as D3webDialog, 
 * MediastinitisDialog, etc.
 * 
 * @author Martina Freiberg
 * 
 * @date 30.12.2011
 */
public class D3webServletLogUtils {

    protected static final String D3WEB_SESSION = "d3webSession";
    protected static String logfilename = "";
    protected static JSONLogger logger = null;

    /**
     * Initializes the logging helper servlet by getting the logger, and
     * assembling the logfilename (date plus d3web-sessionid)
     * 
     * @param jlogger
     * @param loggingstart
     * @param httpSession 
     */
    protected static void initialize(JSONLogger jlogger,
            Date loggingstart, HttpSession httpSession) {

        logger = jlogger; // set the logger

        // initialize logfile name
        SimpleDateFormat format =
                new SimpleDateFormat("yyyyMMdd_HHmmss");
        String formatted = format.format(loggingstart);
        String sid =
                ((Session) httpSession.getAttribute(D3WEB_SESSION)).getId();
        logfilename = formatted + "_" + sid + ".txt";
    }

    /**
     * Logs initial values username and browser info.
     * Only to be done once, therefore check global setting "initlogged"
     * 
     * @param request
     * @param httpSession 
     */
    protected static void logInitially(HttpServletRequest request, HttpSession httpSession) {

        if (!GlobalSettings.getInstance().initLogged()) {

            //logger = D3webConnector.getInstance().getLogger();

            // get values to logQuestionValue initially: browser, user, and start time
            String browser =
                    request.getParameter("browser").replace("+", " ");
            String user =
                    request.getParameter("user").replace("+", " ");
            String start = 
                    request.getParameter("timestring").replace("+", " ");
            // give values to logger
            logger.logStartValue(start);
            logger.logBrowserValue(browser);
            logger.logUserValue(user);

            logger.writeJSONToFile(logfilename);

            GlobalSettings.getInstance().setInitLogged(true);
        }
    }

    /**
     * Logs the end of a session.
     * "cleans up" by creating a new Logger and sending it to D3webConnector, 
     * and sets the "is logging initialized" flag to false.
     * @param request 
     */
    protected static void logSessionEnd(HttpServletRequest request) {
        // end date
         String end = request.getParameter("timestring").replace("+", " ");
         
        logger.logEndValue(end);
        logger.writeJSONToFile(logfilename);
        D3webConnector.getInstance().setLogger(new JSONLogger());
        GlobalSettings.getInstance().setInitLogged(false);
    }

    /**
     * Logs the click on a non-KB widget of the dialog, e.g., click on the
     * save button, click on a language flag....
     * @param request 
     */
    protected static void logWidget(HttpServletRequest request) {
        // TODO need to check here in case IDs are reworked globally one day
        String widgetID = request.getParameter("widget").replace("+", " ");
        String time = request.getParameter("timestring").replace("+", " ");

        if (request.getParameter("language") != null) {
            String language = request.getParameter("language");
            logger.logClickedObjects(
                    "LANGUAGE", time, language);
        } else {
            if (widgetID.contains("reset")) {
                logger.logClickedObjects(
                        "RESET", time, "RESET");
            } else if (widgetID.contains("statistics")) {
                logger.logClickedObjects(
                        "STATISTICS", time, "STATISTICS");
            } else if (widgetID.contains("summary")) {
                logger.logClickedObjects(
                        "SUMMARY", time, "SUMMARY");
            } else if (widgetID.contains("followup")) {
                logger.logClickedObjects(
                        "FOLLOWUP", time, "FOLLOWUP");
            } else if (widgetID.contains("loadcase")) {
                logger.logClickedObjects(
                        "LOAD", time, "LOAD");
            } else if (widgetID.contains("savecase")) {
                logger.logClickedObjects(
                        "SAVE", time, "SAVE");
            }
        }
        logger.writeJSONToFile(logfilename);

    }


    /**
     * Log mouseover on information popup.
     * @param request 
     */
    protected static void logInfoPopup(HttpServletRequest request) {
        String id = request.getParameter("id");
        String start = request.getParameter("timestring");
        String timediff = request.getParameter("value");
        id = id.replace("+", " ");
        start = start.replace("+", " ");

        logger.logClickedObjects(
                id, start, timediff);
        logger.writeJSONToFile(logfilename);
    }

    /**
     * Log a question/value pair, i.e., answered questions, i.e. non-dialog-UI
     * widgets that are specified in the knowledge base (questions, answers...)
     * @param question
     * @param value 
     */
    protected static void logQuestionValue(String question, String value, HttpServletRequest request) {
        String logtime = request.getParameter("timestring").replace("+", " ");
        logger.logClickedObjects(
                question, logtime, value);
        logger.writeJSONToFile(logfilename);
    }
}
