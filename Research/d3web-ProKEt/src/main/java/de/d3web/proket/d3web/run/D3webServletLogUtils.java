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

    protected static final SimpleDateFormat FORMATTER =
            new SimpleDateFormat("E yyyy_MM_dd HH:mm:ss");
    protected static final String D3WEB_SESSION = "d3webSession";
    protected static String logfilename = "";
    protected static JSONLogger logger;

    /**
     * Initializes logging mechanism and logs initial values. 
     * Therefore, the JSONLogger is retrieved from D3webConnector, a filename
     * for logging is assembled (date plus d3web-sessionid), and  username 
     * & used browser are logged right away.
     * @param request
     * @param httpSession 
     */
    protected static void logInitially(HttpServletRequest request, HttpSession httpSession) {
        if (!GlobalSettings.getInstance().initLogged()) {

            // get values to logQuestionValue initially: browser, user, and start time
            String browser =
                    request.getParameter("browser").replace("+", " ");
            String user =
                    request.getParameter("user").replace("+", " ");

            logger = D3webConnector.getInstance().getLogger();
            Date now = new Date();
            // give values to logger
            logger.logStartValue(FORMATTER.format(now));
            logger.logBrowserValue(browser);
            logger.logUserValue(user);

            // assemble filemane
            SimpleDateFormat format =
                    new SimpleDateFormat("yyyyMMdd_HHmmss");
            String formatted = format.format(now);
            String sid =
                    ((Session) httpSession.getAttribute(D3WEB_SESSION)).getId();
            logfilename = formatted + "_" + sid + ".txt";
            System.out.println("LOGNAME: " + logfilename);

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
        Date date = new Date();
        String datestring = FORMATTER.format(date);
        logger.logEndValue(datestring);
        logger.writeJSONToFile(logfilename);
        D3webConnector.getInstance().setLogger(new JSONLogger());
        GlobalSettings.getInstance().setInitLogged(false);
    }

    /**
     * Logs the click on a non-KB widget of the dialog, e.g., click on the
     * save button.
     * @param request 
     */
    protected static void logWidget(HttpServletRequest request) {
        // TODO need to check here in case IDs are reworked globally one day
        String widgetID = request.getParameter("widget");
        Date now = new Date();

        if (widgetID.contains("reset")) {
            logger.logClickedObjects(
                    "RESET", FORMATTER.format(now), "RESET");
        } else if (widgetID.contains("statistics")) {
            logger.logClickedObjects(
                    "STATISTICS", FORMATTER.format(now), "STATISTICS");
        } else if (widgetID.contains("summary")) {
            logger.logClickedObjects(
                    "SUMMARY", FORMATTER.format(now), "SUMMARY");
        } else if (widgetID.contains("followup")) {
            logger.logClickedObjects(
                    "FOLLOWUP", FORMATTER.format(now), "FOLLOWUP");
        } else if (widgetID.contains("loadcase")) {
            logger.logClickedObjects(
                    "LOAD", FORMATTER.format(now), "LOAD");
        } else if (widgetID.contains("savecase")) {
            logger.logClickedObjects(
                    "SAVE", FORMATTER.format(now), "SAVE");
        }
        logger.writeJSONToFile(logfilename);

    }

    /**
     * Log click on a language widget, i.e. widgets for switching languages of
     * the dialog (such as the flags in the EuraHS dialog).
     * @param request 
     */
    protected static void logLanguageWidget(HttpServletRequest request) {

        String language = request.getParameter("language");
        Date now = new Date();
        logger.logClickedObjects(
                "LANGUAGE", FORMATTER.format(now), language);
        logger.writeJSONToFile(logfilename);
    }

    /**
     * Log mouseover on information popup.
     * @param request 
     */
    protected static void logInfoPopup(HttpServletRequest request) {
        String prefix = request.getParameter("prefix");
        String ttwidget = request.getParameter("widget");
        ttwidget = ttwidget.replace("+", " ");
        String timestring = request.getParameter("timestring");
        logger.logClickedObjects(
                "INFOPOPUP_" + prefix, timestring, ttwidget);
        logger.writeJSONToFile(logfilename);
    }

    /**
     * Log a question/value pair, i.e., answered questions, i.e. non-dialog-UI
     * widgets that are specified in the knowledge base (questions, answers...)
     * @param question
     * @param value 
     */
    protected static void logQuestionValue(String question, String value) {
        Date now = new Date();
        logger.logClickedObjects(
                question, FORMATTER.format(now), value);
        logger.writeJSONToFile(logfilename);
    }
}
