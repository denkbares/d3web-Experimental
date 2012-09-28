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

import de.d3web.proket.d3web.utils.D3webUtils;
import de.d3web.core.knowledge.TerminologyManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.*;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.values.Unknown;
import de.d3web.proket.d3web.input.*;
import de.d3web.proket.d3web.input.D3webXMLParser.LoginMode;
import de.d3web.proket.d3web.output.render.AbstractD3webRenderer;
import de.d3web.proket.d3web.output.render.DefaultRootD3webRenderer;
import de.d3web.proket.d3web.output.render.IQuestionD3webRenderer;
import de.d3web.proket.d3web.output.render.SummaryD3webRenderer;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.d3web.ue.JSONLogger;
import de.d3web.proket.d3web.utils.PersistenceD3webUtils;
import de.d3web.proket.database.DB;
import de.d3web.proket.database.DateCoDec;
import de.d3web.proket.database.TokenThread;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.GlobalSettings;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Servlet for creating and using dialogs with d3web binding. Binding is more of
 * a loose binding: if no d3web etc session exists, a new d3web session is
 * created and knowledge base and specs are read from the corresponding XML
 * specfication.
 *
 * Basically, when the user selects answers in the dialog, those are transferred
 * back via AJAX calls and processed by this servlet. Here, values are
 * propagated to the d3web session (and later re-read by the renderers).
 *
 * Both browser refresh and pressing the "new case"/"neuer Fall" Button in the
 * dialog leads to the creation of a new d3web session, i.e. all values set so
 * far are discarded, and an "empty" problem solving session begins.
 *
 * @author Martina Freiberg
 *
 * @date 14.01.2011; Update: 28/01/2011
 *
 */
public class D3webDialog extends HttpServlet {

    private static final long serialVersionUID = -2466200526894064976L;
    protected static final String D3WEB_SESSION = "d3webSession";
    protected static final String USER_SETTINGS = "userSettings";
    protected static final String SOURCE_SAVE = "sourceSave";
    protected static final String SERVLET_CLASS_SAVE = "classSave";
    protected static final String REPLACECONTENT = "##replacecontent##";
    protected static final String REPLACEID = "##replaceid##";
    protected static String sourceSave = "";
    protected static Class classSave = Object.class;
    protected final GlobalSettings GLOBSET = GlobalSettings.getInstance();
    protected D3webXMLParser d3webParser;
    protected D3webConnector d3wcon;
    protected UISettings uis;
    protected D3webUESettings uesettings;
    protected static Map<String, List<String>> usrDat = null;
    private String prevQ = "";
    private String prevV = "";
    private static final SimpleDateFormat SDF_FILENAME_DEFAULT =
            new SimpleDateFormat("yyyyMMdd_HHmmss");
    protected static final String SDF_DEFAULT =
            "EEE yyyy_MM_dd hh:mm:s";

    // TODO get Date everywhere in JS instead of Servlet
    /**
     * @see HttpServlet#HttpServlet()
     */
    public D3webDialog() {
        super();
    }

    /**
     * Init method -- put all the stuff here, that should be done ONCE for the
     * Servlet at the beginning, such as initialising paths, DB connections,
     * etc.
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        String servletcontext = config.getServletContext().getRealPath("/");
        GLOBSET.setServletBasePath(servletcontext);

        d3webParser = new D3webXMLParser();

    }

    /**
     * Basic servlet method. Always called, if servlet is refreshed, called
     * newly etc.
     *
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        response.setContentType("text/html; charset=UTF-8");
        HttpSession httpSession = request.getSession(true);
        httpSession.setMaxInactiveInterval(20 * 60);

        // source and parser
        String source = getSource(request, httpSession);
        d3webParser.setSourceToParse(source);
        d3wcon = D3webConnector.getInstance();
        d3wcon.setD3webParser(d3webParser);

        // UI settings
        uis = UISettings.getInstance();

        // usability extension settings: TODO rename
        uesettings = D3webUESettings.getInstance();

        /*
         * parse the dialog specification -> only parse if stored source is
         * different to current source as then a new dialog specification (e.g.
         * new KB) has been called -> also parse, if source is same, but
         * different Servlet class as then a new dialog type has been called
         */
        String sSave = httpSession.getAttribute(SOURCE_SAVE) != null
                ? httpSession.getAttribute(SOURCE_SAVE).toString() : "";
        String cSave = httpSession.getAttribute(SERVLET_CLASS_SAVE) != null
                ? httpSession.getAttribute(SERVLET_CLASS_SAVE).toString() : "";

        if (!sSave.equals(source)) {
            httpSession.setAttribute(SOURCE_SAVE, source);

            parseAndInitDialogServlet(httpSession);
        } else if (!cSave.equals(this.getClass().toString())) {

            httpSession.setAttribute(SERVLET_CLASS_SAVE, this.getClass().toString());
            parseAndInitDialogServlet(httpSession);
        }


        /*
         * Reset the session in case the d3websession in the httpSession is
         * still null; this could be the case, if the same dialog is called from
         * different browsers (?! correct?) as then the httpSession is refreshed
         * and we also need a new d3webSession
         */
        // THIS IS REALLY NEEDED; OTHERWISE PROBLEMS WITH DIFFERENT SESSIONS ON SAME KB
        if (httpSession.getAttribute(D3WEB_SESSION) == null) {
            resetD3webSession(httpSession);
        }




        // in case nothing other is provided, "show" is the default action
        String action = request.getParameter("action");
        if (action == null) {
            action = "show";
        }

        System.out.println("0: " + action);
        System.out.println(httpSession.getAttribute(D3WEB_SESSION));

        if (action.equalsIgnoreCase(
                "dbLogin")) {
            loginDB(request, response, httpSession);
            return;
        }

        if (uis.getLoginMode()
                == LoginMode.DB) {
            String authenticated = (String) httpSession.getAttribute("authenticated");
            if (authenticated == null || !authenticated.equals("yes")) {
                response.sendRedirect("../EuraHS-Login");
                return;
            }
        }

        // set handleBrowsers flag null for all actions other than handlecheck
        // itself; that way, the check can be processed correctly also after 
        // refreshs or the like
        if (!action.equalsIgnoreCase(
                "checkHandleBrowsers")) {
            httpSession.setAttribute("handleBrowsers", null);
        }

        // switch action as defined by the servlet call
        if (action.equalsIgnoreCase(
                "show")) {
            show(request, response, httpSession);
        } else if (action.equalsIgnoreCase(
                "addfacts")) {
            //System.out.println("add Facts base");
            addFacts(request, response, httpSession);
        } else if (action.equalsIgnoreCase(
                "addfactsyn")) {
            //System.out.println("add Facts base");
            addFactsYN(request, response, httpSession);
        } else if (action.equalsIgnoreCase(
                "savecase")) {
            saveCase(request, response, httpSession);
        } else if (action.equalsIgnoreCase(
                "loadcase")) {
            loadCase(request, response, httpSession);
        } else if (action.equalsIgnoreCase(
                "deletecase")) {
            deleteCase(request, response, httpSession);
        } else if (action.equalsIgnoreCase(
                "updatesummary")) {
            updateSummary(request, response, httpSession);
        } else if (action.equalsIgnoreCase(
                "reset")
                || action.equalsIgnoreCase("resetNewUser")) {
            resetD3webSession(httpSession);
        } else if (action.equalsIgnoreCase(
                "gotoStatistics")) {
            gotoStatistics(response, httpSession);
        } else if (action.equalsIgnoreCase(
                "gotoGroups")) {
            gotoGroups(response, httpSession);
        } else if (action.equalsIgnoreCase(
                "gotoTxtDownload")) {
            gotoTxtDownload(response, request, httpSession);
        } else if (action.equalsIgnoreCase(
                "checkUsrDatLogin")) {
            checkUsrDatLogin(response, httpSession);
        } else if (action.equalsIgnoreCase(
                "usrDatlogin")) {

            loginUsrDat(request, response, httpSession);
        } else if (action.equalsIgnoreCase(
                "sendmail")) {
            try {
                sendMail(request, response, httpSession);
                response.getWriter().append("success");
            } catch (MessagingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (action.equalsIgnoreCase(
                "language")) {
            setLanguageID(request, httpSession);
        } else if (action.equalsIgnoreCase(
                "logInit")) {

            // TODO remove
            //ServletLogUtils.initForD3wDialogs(logger, now);

            JSONLogger logger = (JSONLogger) httpSession.getAttribute("logger");
            logInitially(request, logger, httpSession);

            httpSession.setAttribute("loginit", true);
            //GLOBSET.setInitLogged(true); // TODO remove

        } else if (action.equalsIgnoreCase(
                "logEnd")) {
            logSessionEnd(request, httpSession);
        } else if (action.equalsIgnoreCase(
                "logNotAllowed")) {
            logNotAllowed(request, httpSession);
        } else if (action.equalsIgnoreCase(
                "logWidget")) {
            logWidget(request, httpSession);
        } else if (action.equalsIgnoreCase(
                "logInfoPopup")) {
            logInfoPopup(request, httpSession);
        } else if (action.equalsIgnoreCase(
                "markWidget")) {
            markWidget(request, httpSession);
        } else if (action.equalsIgnoreCase(
                "checkWidgetClicked")) {
            checkWidgetClicked(httpSession, response);
        } else if (action.equalsIgnoreCase(
                "checkInitialLoggingReload")) {
            checkInitialLoggingReload(httpSession, response);
        } else if (action.equalsIgnoreCase(
                "checkLoggingEnd")) {
            checkLoggingEnd(httpSession, response);

        } else if (action.equalsIgnoreCase(
                "checkHandleBrowsers")) {
            PrintWriter writer = response.getWriter();
            if (httpSession.getAttribute("handleBrowsers") == null) {
                writer.print("true");
                httpSession.setAttribute("handleBrowsers", "false");
            }
        } else if (action.equalsIgnoreCase("saveShowStatus")) {
            saveShowStatus(request, httpSession);
        } else if (action.equalsIgnoreCase(
                "handleBrowsers")) {

            PrintWriter writer = response.getWriter();

            // is the case ONLY when called from handleUnsupportedBrowsers-js method
            String browser = request.getParameter("browser").replace("+", " ");
            String version = request.getParameter("version").replace("+", " ");

            if (browser.equals("Explorer")
                    && !equalOrHigher(version, "9")) {

                browser = "Internet Explorer";
                writer.print(assembleBrowserCompatibilityMessage(browser, version));

                writer.close();

            } else {

                // in case of DB login (as for EuraHS) redirect to the EuraHS-Login
                // Servlet --> TODO refactor: rename EuraHS-Login Servlet or create
                // superservlet to be overwritten
                if (uis.getLoginMode() == LoginMode.DB) {
                    String authenticated = (String) httpSession.getAttribute("authenticated");
                    if (authenticated == null || !authenticated.equals("yes")) {
                        response.sendRedirect("../EuraHS-Login");

                    }
                }

            }

        } else if (action.equalsIgnoreCase(
                "sendFeedbackMail")) {

            String state = "";
            if (request.getParameter("feedback") != null) {
                if (request.getParameter("feedback").equals("")) {
                    state = "nofeedback";
                } else {
                    try {
                        sendFeedbackMail(request, response, httpSession);
                        state = "success";
                    } catch (MessagingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            response.getWriter().append(state);
        } else if (action.equalsIgnoreCase(
                "sendUEQMail")) {

            try {
                sendUEQMail(request, response, httpSession);
                response.getWriter().append("success");
            } catch (MessagingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (action.equalsIgnoreCase("addFactITree")) {
            addFactITree(request, response, httpSession);
        } else if (action.equalsIgnoreCase("loadcaseClear")) {
            loadCaseClear(request, response, httpSession);
        } else {
            handleDialogSpecificActions(httpSession, request, response, action);
        }
    }

    /*
     * Initial check whether...
     *
     * @created 26.07.2011
     *
     * @param response
     *
     * @param httpSession
     *
     * @throws IOException
     *
     * TODO: move checkInitialLogging Reload and logInit to ONE method
     */
    protected void checkInitialLoggingReload(HttpSession httpSession,
            HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();

        if (httpSession.getAttribute("loginit") == null) {
            httpSession.setAttribute("loginit", false);
        }

        if (httpSession.getAttribute("loginit").equals(false)) {
            writer.append("firsttime");

        } else {
            writer.append("later");
        }
    }

    /*
     * Everytime check whether the end of a session has been reached: - either
     * by click on denoted widget (TODO) - or when all active/indicated
     * questions have been answered: in that case we create/display an
     * alert/dialog that tells the user if he is finished, and that (s)he should
     * confirm that or otherwise just go on
     *
     * @created 26.07.2011
     *
     * @param response
     *
     * @param httpSession
     *
     * @throws IOException
     */
    protected void checkLoggingEnd(HttpSession httpSession,
            HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();


        TerminologyManager tman = D3webConnector.getInstance().getKb().getManager();
        List<Question> allquestions = tman.getQuestions();
        List<Question> indicated = new ArrayList<Question>();
        for (Question q : allquestions) {
            if (D3webUtils.isIndicated(q, D3webConnector.getInstance().getSession().getBlackboard())) {
                indicated.add(q);
            }

            if (D3webUtils.isInit(q)) {
                indicated.add(q);
            }
        }
        boolean indicatedNotAnswered = false;
        Collection<Question> valued = D3webConnector.getInstance().getSession().getBlackboard().getValuedQuestions();

        if (valued.size() > 0) {
            for (Question iq : indicated) {
                if (!valued.contains(iq)) {

                    indicatedNotAnswered = true;
                }
            }
        } else {
            indicatedNotAnswered = true;
        }

        if (indicatedNotAnswered) {
            writer.append("true");

        } else {
            writer.append("false");
        }
    }

    /**
     * Add one or several given facts. Thereby, first check whether input-store
     * has elements, if yes, parse them and set them (for num/text/date
     * questions), if no, just parse and set a given single value.
     *
     * @created 28.01.2011
     *
     * @param request ServletRequest
     * @param response ServletResponse
     */
    protected void addFacts(HttpServletRequest request,
            HttpServletResponse response, HttpSession httpSession)
            throws IOException {

        PrintWriter writer = response.getWriter();
        Session d3webSession = (Session) httpSession.getAttribute(D3WEB_SESSION);
        List<String> questions = new ArrayList<String>();
        List<String> values = new ArrayList<String>();

        // get all questions and answers lately answered as lists
        getParameterPairs(request, "question", "value", questions, values);


        // check if all required values are set correctly, if not, do not go on!
        if (!handleRequiredValueCheck(writer, d3webSession, questions, values)) {
            return;
        }

        // save beforeState
        DialogState stateBefore = new DialogState(d3webSession);
        //System.out.println("BEF: " + stateBefore.answeredQuestions); 

        // set the values
        setValues(d3webSession, questions, values, request, httpSession);

        // "clean up" blackboard
        D3webUtils.resetAbandonedPaths(d3webSession, httpSession);

        // autosave the current state
        PersistenceD3webUtils.saveCase(
                (String) httpSession.getAttribute("user"),
                "autosave",
                d3webSession);

        // save afterState
        DialogState stateAfter = new DialogState(d3webSession);
        //System.out.println("AFT:" +stateAfter.answeredQuestions);

        // calculate the difference set of before and after states of the dialog
        Set<TerminologyObject> diff = calculateDiff(d3webSession, stateBefore, stateAfter);


        // update the dialog (partially, i.e. all changed questions)
        renderAndUpdateDiff(writer, d3webSession, diff, httpSession, request);
    }

    /**
     * Check, whether a required value (for saving) is specified. If yes, check
     * whether this value has already been set in the KB or is about to be set
     * in the current call --> go on normally. Otherwise, return a marker
     * "<required value>" so the user is informed by AJAX to provide this marked
     * value.
     *
     * @return true of the value is set, false if it is not yet set
     */
    protected boolean handleRequiredValueCheck(PrintWriter writer, Session d3webSession, List<String> questions, List<String> values) {
        List<String> all = new LinkedList<String>();
        all.addAll(questions);
        all.addAll(values);
        String reqVal = D3webConnector.getInstance().getD3webParser().getRequired();
        if (!reqVal.equals("") && !checkReqVal(reqVal, d3webSession, all)) {
            writer.append("##missingfield##");
            writer.append(reqVal);
            return false;
        }
        return true;
    }

    protected void setValues(Session d3webSession, List<String> questions, List<String> values, HttpServletRequest request,
            HttpSession httpSession) {

        // before-state of abstraction questions
        Collection abstractionsBefore =
                D3webUtils.getValuedAbstractions(d3webSession);

        for (int i = 0; i < questions.size(); i++) {

            D3webUtils.setValue(questions.get(i), values.get(i), d3webSession);

            // state of abstractions AFTER setting values
            Collection abstractionsAfter =
                    D3webUtils.getValuedAbstractions(d3webSession);

            if (uesettings.isLogging()) {

                Collection newAbstractions = new ArrayList<TerminologyObject>();

                // check whether new abstractions have fired
                for (Object vaNew : abstractionsAfter) {
                    if (!abstractionsBefore.contains((TerminologyObject) vaNew)) {
                        newAbstractions.add(vaNew);
                    }
                }
                handleQuestionValueLogging(
                        request, httpSession, questions.get(i),
                        values.get(i), d3webSession, newAbstractions);
            }
        }
    }

    protected void handleQuestionValueLogging(HttpServletRequest request,
            HttpSession httpSession, String ques, String val,
            Session d3webSession, Collection<TerminologyObject> newAbstractions) {

        // retrieve logtime
        String logtime = request.getParameter("timestring").replace("+", " ");

        // retrieve internal IDs of value and question and corresponding D3webObjects
        String question = AbstractD3webRenderer.getObjectNameForId(ques);
        Question q = D3webConnector.getInstance().getKb().getManager().searchQuestion(
                question == null ? ques : question);

        String value = AbstractD3webRenderer.getObjectNameForId(val);
        value = value == null ? val : AbstractD3webRenderer.getObjectNameForId(val);
        Value v = d3webSession.getBlackboard().getValue((ValueObject) q);

        String datestring = "";
        JSONLogger logger = (JSONLogger) httpSession.getAttribute("logger");

        /*
         * Question Logging: only if - question remains same but value changed -
         * OR question differs
         */
        if ((ques.equals(prevQ) && (!val.equals(prevV))
                || !ques.equals(prevQ))) {
            //if (!val.equals(prevV)) {

            if (q instanceof QuestionDate) {
                //String dateFormat = q.getInfoStore().getValue(ProKEtProperties.DATE_FORMAT);
                //String df = dateFormat == null?SDF_DEFAULT:dateFormat;
                if (!value.equals("Unknown")) {
                    datestring =
                            D3webUtils.getFormattedDateFromString(
                            (Date) v.getValue(), SDF_DEFAULT);

                    value = datestring;
                }

            } else if (q instanceof QuestionMC) {
                String valMC = "";

                if (val.equals("")) {
                    value = Unknown.getInstance().toString();
                } else if (val.contains("##mcanswer")) {

                    String[] choiceIds = val.split("##mcanswer##");

                    for (String choiceId : choiceIds) {
                        String choiceName = AbstractD3webRenderer.getObjectNameForId(choiceId);
                        valMC += choiceName == null ? choiceId : choiceName;
                        valMC += "###";
                        value = valMC;
                    }
                }
                question = "MC_" + question;

            }
            ServletLogUtils.logQuestionValue(question, value, logtime, logger);
            prevQ = ques;
            prevV = val;

            // also log newly set abstraction values
            // TODO ERROR FOR YES NO ABSTRACTION e.g Question 81 Single Dose
            for (TerminologyObject to : newAbstractions) {
                Question qa =
                        D3webConnector.getInstance().getKb().getManager().searchQuestion(to.getName());
                Value va = d3webSession.getBlackboard().getValue((ValueObject) qa);
                int doubleAsInt = (int) Double.parseDouble(va.toString());
                ServletLogUtils.logQuestionValue(qa.getName(), Integer.toString(doubleAsInt), logtime, logger);
            }
        }
    }

    private Set<TerminologyObject> calculateDiff(Session d3webSession, DialogState beforeState, DialogState afterState) {


        Set<TerminologyObject> diff = new HashSet<TerminologyObject>();

        for (TerminologyObject to : beforeState.unknownQuestions) {
            if (!afterState.unknownQuestions.contains(to)) {
                diff.add(to);
            }
        }

        D3webUtils.getDiff(beforeState.indicatedQASets, afterState.indicatedQASets, diff);
        //     System.out.println("DIFF 1: " + diff);

        D3webUtils.getDiff(afterState.indicatedQASets, beforeState.indicatedQASets, diff);

        //       System.out.println("DIFF 2: " + diff);


        diff.addAll(D3webUtils.getUnknownQuestions(d3webSession));
        diff.addAll(D3webUtils.getMCQuestions(d3webSession));

        // we simply update answered abstract questions every time
        // actually we only need to update them if their facts have changed, but
        // thats more complex to test and probably not even faster...
        List<Question> abstractAnsweredQuestionsBefore = new ArrayList<Question>(
                beforeState.answeredQuestions.size());
        for (Question answeredQuestionBefore : beforeState.answeredQuestions) {
            Boolean isAbstract = answeredQuestionBefore.getInfoStore().getValue(
                    BasicProperties.ABSTRACTION_QUESTION);
            if (isAbstract != null && isAbstract) {
                abstractAnsweredQuestionsBefore.add(answeredQuestionBefore);
            }
        }
        beforeState.answeredQuestions.removeAll(abstractAnsweredQuestionsBefore);
        afterState.answeredQuestions.removeAll(beforeState.answeredQuestions);
        // System.out.println(answeredQuestionsAfter);
        diff.addAll(afterState.answeredQuestions);

        return diff;
    }

    private void renderAndUpdateDiff(PrintWriter writer, Session d3webSession, Set<TerminologyObject> diff, HttpSession httpSession,
            HttpServletRequest request) {
        ContainerCollection cc = new ContainerCollection();

        for (TerminologyObject to : diff) {
            if (isHiddenOrHasHiddenParent(to)) {

                continue;
            }
            IQuestionD3webRenderer toRenderer = AbstractD3webRenderer.getRenderer(to);

            // get back the ID from store for finding element in HTML
            writer.append(REPLACEID + AbstractD3webRenderer.getID(to));
            writer.append(REPLACECONTENT);

            TerminologyObject parent = to instanceof QContainer ? d3wcon.getKb().getRootQASet()
                    : D3webUtils.getQuestionnaireAncestor(to);

            // set Locale=2 = english for default
            int loc = httpSession.getAttribute("locale") != null
                    ? Integer.parseInt(httpSession.getAttribute("locale").toString()) : 2;
            // get the HTML code for rendering the parent containing the to-update element
            writer.append(
                    toRenderer.renderTerminologyObject(
                    d3webSession, cc, to,
                    parent, loc,
                    httpSession, request));
        }
        writer.append(REPLACEID + "headerInfoLine");
        writer.append(REPLACECONTENT);
        DefaultRootD3webRenderer rootRenderer =
                (DefaultRootD3webRenderer) D3webRendererMapping.getInstance().getRenderer(null);

        // render the headerinfoline as latest as to have the most-current infos there
        writer.append(rootRenderer.renderHeaderInfoLine(d3webSession));
        //  System.out.println(response.getContentType() + " " + response.getCharacterEncoding());
        //System.out.println(writer.toString());
    }

// TODO: move to D3webUtils
    private boolean isHiddenOrHasHiddenParent(TerminologyObject to) {
        Boolean hide = to.getInfoStore().getValue(ProKEtProperties.HIDE);
        if (hide != null && hide) {
            return true;
        }
        for (TerminologyObject parent : to.getParents()) {
            if (isHiddenOrHasHiddenParent(parent)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parse the language parameter (set via JS depending on what language (for
     * KB elements) was chosen in the dialog and set it in global settings.
     *
     * @param request
     */
    protected void setLanguageID(HttpServletRequest request, HttpSession httpSession) {
        int localeID =
                Integer.parseInt(
                request.getParameter("langID").toString());
        httpSession.setAttribute("locale", localeID);

//        GLOBSET.setLocaleIdentifier(localeID);
    }

    /**
     * Checks, whether a potentially required value is already set in the KB or
     * is contained in the current set of values to write to the KB. If yes, the
     * method returns true, if no, false.
     *
     * @created 15.04.2011
     *
     * @param requiredVal The required value that is to check
     * @param sess The d3webSession
     * @param valToSet The single value to set
     * @param store The value store
     * @return TRUE of the required value is already set or contained in the
     * current set of values to set
     */
    private boolean checkReqVal(String requiredVal, Session sess, List<String> check) {
        Blackboard blackboard = sess.getBlackboard();

        Question to = D3webConnector.getInstance().getKb().getManager().searchQuestion(requiredVal);

        Fact lastFact = blackboard.getValueFact(to);

        boolean contains = false;
        for (String s : check) {
            String objectNameForId = AbstractD3webRenderer.getObjectNameForId(s);
            if (objectNameForId != null) {
                s = objectNameForId;
            }

            if (s.equals(requiredVal)) {
                contains = true;
                break;
            }
        }
        if (contains || (lastFact != null && !lastFact.getValue().toString().equals(""))) {
            return true;
        }
        return false;
    }

    /**
     * Initial check whether...
     *
     * @created 26.07.2011
     *
     * @param response
     * @param httpSession
     * @throws IOException
     */
    protected void checkUsrDatLogin(HttpServletResponse response, HttpSession httpSession) throws IOException {
        PrintWriter writer = response.getWriter();

        if (httpSession.getAttribute("user") == null) {
            httpSession.setAttribute("log", true);
            writer.append("NLI");
        } else {
            writer.append("NOLI");
        }
    }

    /**
     * Delete a case.
     *
     * @created 09.03.2011
     *
     * @param request ServletRequest
     * @param response ServletResponse
     */
    protected void deleteCase(HttpServletRequest request,
            HttpServletResponse response, HttpSession httpSession) {

        String filename = request.getParameter("fn");
        String user = (String) httpSession.getAttribute("user");

        if (PersistenceD3webUtils.existsCase(user, filename)) {
            PersistenceD3webUtils.deleteUserCase(user, filename);
        }
    }

    /**
     * Get several parameters with specified names from the request object.
     * Mainly used for getting more than one question with corresponding answers
     * from the request. Return the parameters as a questions and answers list
     * respectively.
     *
     * @param request
     * @param paraName1 first parameter name
     * @param paraName2 second parameter name
     * @param parameters1
     * @param parameters2
     */
    protected void getParameterPairs(
            HttpServletRequest request, String paraName1, String paraName2,
            List<String> parameters1, List<String> parameters2) {
        int i = 0;
        while (true) {
            String para1 = request.getParameter(paraName1 + i);
            String para2 = request.getParameter(paraName2 + i);
            if (para1 == null || para2 == null) {
                break;
            }
            try {
                parameters1.add(URLDecoder.decode(para1, "UTF-8"));
                parameters2.add(URLDecoder.decode(para2, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                continue;
            }
            i++;
        }
    }

    /**
     * Redirect to the statistics site.
     *
     * @param response
     * @param httpSession
     * @throws IOException
     */
    protected void gotoStatistics(HttpServletResponse response,
            HttpSession httpSession) throws IOException {

        String email = (String) httpSession.getAttribute("user");

        String gotoUrl = "../Statistics/Statistic.jsp?action=dbLogin";

        String token = DateCoDec.getCode();
        gotoUrl += "&t=" + token;
        gotoUrl += "&e=" + Base64.encodeBase64String(email.getBytes());

        new TokenThread(token, email).start();
        PrintWriter writer = response.getWriter();
        writer.print(gotoUrl);
        writer.close();
        // response.sendRedirect(gotoUrl);
    }

    protected void gotoGroups(HttpServletResponse response,
            HttpSession httpSession) throws IOException {

        String email = (String) httpSession.getAttribute("user");

        String gotoUrl = "../Statistics/editGroup.jsp?action=dbLogin";

        String token = DateCoDec.getCode();
        gotoUrl += "&t=" + token;
        gotoUrl += "&e=" + Base64.encodeBase64String(email.getBytes());

        gotoUrl += "&edit=showGroupTable";

        new TokenThread(token, email).start();
        PrintWriter writer = response.getWriter();



        writer.print(gotoUrl);
        writer.close();
        // response.sendRedirect(gotoUrl);
    }

    protected void gotoTxtDownload(HttpServletResponse response, HttpServletRequest request,
            HttpSession httpSession) throws IOException {

        String email = (String) httpSession.getAttribute("user");
        String path = GlobalSettings.getInstance().getServletBasePath();

        // Important: /Download... doesn't work both locally and on server due
        // to webapp paths etc
        String gotoUrl = "Download?flag=summary";

        PrintWriter writer = response.getWriter();
        writer.print(gotoUrl);
        writer.close();
        // response.sendRedirect(gotoUrl);
    }

    /**
     * Loading a case.
     *
     * @created 09.03.2011
     *
     * @param request ServletRequest
     * @param response ServletResponse
     */
    protected void loadCase(HttpServletRequest request,
            HttpServletResponse response, HttpSession httpSession) {

        String filename = request.getParameter("fn");
        String user = (String) httpSession.getAttribute("user");
        loadCaseUserFilename(request, httpSession, user, filename);
    }

    /*
     * Helper method for above loadCase() method and for single access of load
     * case mechanism with a given username and filename
     */
    protected void loadCaseUserFilename(HttpServletRequest request, HttpSession httpSession, String user, String filename) {
        Session session = null;
        if (PersistenceD3webUtils.existsCase(user, filename)) {
            session = PersistenceD3webUtils.loadUserCase(user, filename);
            httpSession.setAttribute(D3WEB_SESSION,
                    session);
            httpSession.setAttribute("lastLoaded", filename);
            D3webConnector.getInstance().setSession(session);

            JSONLogger logger =
                    (JSONLogger) httpSession.getAttribute("logger");

            // TODO is logging () into httpSession
            if (uesettings.isLogging()) {
                ServletLogUtils.resetLogfileName(session.getId(), logger);
                String time;
                if (request.getParameter("timestring") != null) {
                    time = request.getParameter("timestring").replace("+", " ");
                } else {
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat(SDF_DEFAULT);
                    time = sdf.format(date);
                }
                ServletLogUtils.logResume(time, session.getId(), logger);
            }
        }
    }

    /**
     * Login redirect - EURAHS only --> refactor: move to EuraHS Servlet
     *
     * @param request
     * @param response
     * @param httpSession
     * @throws IOException
     */
    protected void loginDB(HttpServletRequest request, HttpServletResponse response, HttpSession httpSession) throws IOException {
        String token = request.getParameter("t");
        String email = new String(Base64.decodeBase64(request.getParameter("e")));

        if (DB.isValidToken(token, email)) {
            httpSession.setAttribute("authenticated", "yes");
            httpSession.setAttribute("user", email);

            loadCaseUserFilename(request, httpSession, email, "autosave");

            response.sendRedirect("../EuraHS-Dialog");
        } else {
            response.sendRedirect("../EuraHS-Login");
        }
    }

    /**
     * Handle login of new user in simple-textdatabase case, e.g. for
     * Mediastinitis
     *
     * @created 29.04.2011
     *
     * @param req
     * @param res
     * @param httpSession
     * @throws IOException
     */
    protected void loginUsrDat(HttpServletRequest req,
            HttpServletResponse res, HttpSession httpSession)
            throws IOException {

        // fetch the information sent via the request string from login
        String u = req.getParameter("u");
        String p = req.getParameter("p");

        // get the response writer for communicating back via Ajax
        PrintWriter writer = res.getWriter();

        httpSession.setMaxInactiveInterval(60 * 60);

        // if no valid login
        if (!permitUser(u, p)) {

            // causes JS to display error message
            writer.append("nosuccess");
            return;
        }

        // set user attribute for the HttpSession
        httpSession.setAttribute("user", u);

        httpSession.setAttribute("lastLoaded", "");

        /*
         * in case we should have more than one user per clinic, we distinguish
         * them by adding "_1"... to the clinic name, i.e. WUE_1, WUE_2 etc.
         * Thus we need to extract part of the login name that also denotes the
         * clinic name and thus the subfolder, where cases are stored that
         * should be visible for THIS user.
         */
        int splitter = u.indexOf("_");
        if (splitter != -1) {
            String toReplace = u.substring(splitter, u.length());
            String userSubfolder = u.replace(toReplace, "");
            httpSession.setAttribute("user", userSubfolder);
        }

        // causes JS to start new session and d3web case finally
        writer.append("newUser");
    }

    /**
     * Check, whether the user has permissions to logQuestionValue in.
     * Permissions are stored in userdat.csv in cases parent folder
     *
     * @created 15.03.2011
     *
     * @param user The user name.
     * @param password The password.
     * @return True, if permissions are correct.
     */
    private boolean permitUser(String user, String password) {

        List<String> values = usrDat.get(user);
        if (values != null) {
            String pass = values.get(1);
            if (pass != null && password.equals(pass)) {
                return true;
            }
        }
        return false; // trust no one per default
    }

    /**
     * Reset a running d3web session.
     *
     * @param httpSession
     */
    protected void resetD3webSession(HttpSession httpSession) {

        Session d3webSession =
                D3webUtils.createSession(d3wcon.getKb(), d3wcon.getDialogStrat());
        httpSession.setAttribute(D3WEB_SESSION, d3webSession);
        httpSession.setAttribute("lastLoaded", "");
        httpSession.setAttribute("handleBrowsers", null);

        D3webConnector.getInstance().setSession(d3webSession);
        if (uesettings.isLogging()) {

            httpSession.setAttribute("loginit", false);
            initializeLoggingMechanism(httpSession);
        }

        sourceSave = "";

    }

    private void initializeLoggingMechanism(HttpSession httpSession) {
        Date now = new Date();
        String filename = // init logfilename
                createLogfileName(now, httpSession);

        JSONLogger logger = null;

        // create logger
        logger = new JSONLogger(filename);
        httpSession.setAttribute("logger", logger);
    }

    /**
     * Basic servlet method for displaying the dialog.
     *
     * @created 28.01.2011
     *
     * @param request
     * @param response
     * @param d3webSession
     * @throws IOException
     */
    protected void show(HttpServletRequest request, HttpServletResponse response,
            HttpSession httpSession)
            throws IOException {

        PrintWriter writer = response.getWriter();
        // get the root renderer --> call getRenderer with null
        DefaultRootD3webRenderer d3webr =
                (DefaultRootD3webRenderer) D3webRendererMapping.getInstance().getRenderer(null);
        System.out.println("RENDEER: " + d3webr.getClass());

        // new ContainerCollection needed each time to get an updated dialog
        ContainerCollection cc = new ContainerCollection();
        Session d3webSess = (Session) httpSession.getAttribute(D3WEB_SESSION);

        //System.out.println(d3webr.getClass());

        cc = d3webr.renderRoot(cc, d3webSess, httpSession, request);
        writer.print(cc.html.toString()); // deliver the rendered output
        writer.close(); // and close
    }

    /**
     * Saving a case.
     *
     * @created 08.03.2011
     *
     * @param request ServletRequest
     * @param response ServletResponse
     */
    protected void saveCase(HttpServletRequest request,
            HttpServletResponse response, HttpSession httpSession)
            throws IOException {

        PrintWriter writer = null;
        writer = response.getWriter();

        String userFilename = request.getParameter("userfn");
        String user = (String) httpSession.getAttribute("user");
        String lastLoaded = (String) httpSession.getAttribute("lastLoaded");
        String forceString = request.getParameter("force");

        // force wird im JS gesetzt, falls der User unter bereits vorhandenem
        // Namen speichern will und das nochmal bestätigt.
        boolean force = forceString != null && forceString.equals("true");

        Session d3webSession = (Session) httpSession.getAttribute(D3WEB_SESSION);

        // if: really overwrite existing OR case not exists OR case exists but
        // has been loaded for modification
        if (force
                || !PersistenceD3webUtils.existsCase(user, userFilename)
                || (PersistenceD3webUtils.existsCase(user, userFilename)
                && lastLoaded != null && lastLoaded.equals(userFilename))) {

            PersistenceD3webUtils.saveCase(
                    user,
                    userFilename,
                    d3webSession);

            httpSession.setAttribute("lastLoaded", userFilename);
        } else {
            writer.append("exists");
        }
    }

    /**
     * Send a mail with login request via account "user" and to the contact
     * person specified in InternetAdress "to"
     *
     * @created 29.04.2011
     *
     * @param request
     * @param response
     * @param httpSession
     * @throws MessagingException
     */
    protected void sendMail(HttpServletRequest request, HttpServletResponse response,
            HttpSession httpSession) throws MessagingException {

        final String user = "SendmailAnonymus@freenet.de";
        final String pw = "sendmail";

        /*
         * setup properties for mail server
         */
        Properties props = new Properties();
        props.put("mail.smtp.host", "mx.freenet.de");
        props.put("mail.smtp.port", "587");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.user", user);
        props.put("mail.password", pw);
        // props.put("mail.debug", "true");

        javax.mail.Session session = javax.mail.Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {

                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, pw);
                    }
                });

        MimeMessage message = new MimeMessage(session);

        // from-identificator
        InternetAddress from = new InternetAddress(user);
        message.setFrom(from);

        String loginUser = request.getParameter("user");

        // default toAddress if nothing else specified in userdat.csv
        String toAddress = "striffler@informatik.uni-wuerzburg.de";
        usrDat.get(loginUser);
        if (usrDat != null) {
            String email = usrDat.get(loginUser).get(2);
            if (email != null) {
                toAddress = email;
            }
        }
        InternetAddress to = new InternetAddress(toAddress);
        message.addRecipient(Message.RecipientType.TO, to);

        /*
         * A subject
         */
        message.setSubject(this.getClass().getSimpleName() + " Loginanfrage");

        message.setText("Bitte Logindaten erneut zusenden: \n\n"
                + "Benutzername: " + loginUser);
        Transport.send(message);

    }

    protected void sendFeedbackMail(HttpServletRequest request,
            HttpServletResponse response,
            HttpSession httpSession) throws MessagingException {

        //System.out.println("FEEDBACKMAIL: ");
        final String user = "SendmailAnonymus@freenet.de";
        final String pw = "sendmail";

        final String username = request.getParameter("user").toString();
        final String contact = request.getParameter("contact").toString();
        final String feedback = request.getParameter("feedback").toString();

        /*
         * setup properties for mail server
         */
        Properties props = new Properties();
        props.put("mail.smtp.host", "mx.freenet.de");
        props.put("mail.smtp.port", "587");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.user", user);
        props.put("mail.password", pw);
        // props.put("mail.debug", "true");

        javax.mail.Session session = javax.mail.Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {

                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, pw);
                    }
                });

        MimeMessage message = new MimeMessage(session);

        // from-identificator
        InternetAddress from = new InternetAddress(user);
        message.setFrom(from);

        // default toAddress --> developer/evaluator interested in feedback
        String toAddress = "freiberg@informatik.uni-wuerzburg.de";
        InternetAddress to = new InternetAddress(toAddress);
        message.addRecipient(Message.RecipientType.TO, to);

        /*
         * Constructing the message
         */
        String dialogFlag = uis.getUIprefix();
        if (dialogFlag == null) {
            dialogFlag = "";
        }
        message.setSubject("Feedback" + " " + dialogFlag);

        message.setText("Username:\t" + username + "\n"
                + "Contact: \t" + contact + "\n\n"
                + "Feedback:\n"
                + feedback.replace("+", " "));
        Transport.send(message);

    }

    protected void sendUEQMail(HttpServletRequest request,
            HttpServletResponse response,
            HttpSession httpSession) throws MessagingException {


        final String user = "SendmailAnonymus@freenet.de";
        final String pw = "sendmail";

        final String username = request.getParameter("user").toString();
        final String contact = request.getParameter("contact").toString();
        final String qData = request.getParameter("questionnaireData").toString();

        /*
         * setup properties for mail server
         */
        Properties props = new Properties();
        props.put("mail.smtp.host", "mx.freenet.de");
        props.put("mail.smtp.port", "587");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.user", user);
        props.put("mail.password", pw);
        // props.put("mail.debug", "true");

        javax.mail.Session session = javax.mail.Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {

                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, pw);
                    }
                });

        MimeMessage message = new MimeMessage(session);

        // from-identificator
        InternetAddress from = new InternetAddress(user);
        message.setFrom(from);

        // default toAddress --> developer/evaluator interested in feedback
        String toAddress = "freiberg@informatik.uni-wuerzburg.de";
        InternetAddress to = new InternetAddress(toAddress);
        message.addRecipient(Message.RecipientType.TO, to);

        /*
         * Constructing the message Questionnaire Data:
         * questionID1***value1###questionID2***value2###
         */

        String[] qvpairs = qData.split("###");
        StringBuilder qDataBui = new StringBuilder();

        for (String pair : qvpairs) {
            String[] splitpair = pair.split("---");
            qDataBui.append(splitpair[0].replace("UE_", ""));
            qDataBui.append(" --> ");
            qDataBui.append(splitpair[1]);
            qDataBui.append("\n");
        }

        String dialogFlag = uis.getUIprefix();
        if (dialogFlag == null) {
            dialogFlag = "";
        }
        message.setSubject("UE Questionnaire Data" + " " + dialogFlag);

        message.setText("Username:\t" + username + "\n"
                + "Contact: \t" + contact + "\n\n"
                + "Questionnaire Data:\n"
                + qDataBui.toString().replace("_", " "));
        Transport.send(message);

    }

    protected void updateSummary(HttpServletRequest request, HttpServletResponse response, HttpSession httpSession) throws IOException {
        PrintWriter writer = response.getWriter();

        String questionnaireContentID = "questionnaireSummaryContent";

        writer.append(REPLACEID + questionnaireContentID);
        writer.append(REPLACECONTENT);
        SummaryD3webRenderer rootRenderer = D3webRendererMapping.getInstance().getSummaryRenderer();
        writer.append("<div id='" + questionnaireContentID + "'>");
        writer.append(rootRenderer.renderSummaryDialog(
                (Session) httpSession.getAttribute(D3WEB_SESSION),
                SummaryD3webRenderer.SummaryType.QUESTIONNAIRE, httpSession));
        writer.append("<div>");

        String questionnaireLevel1ContendID = "level1SummaryContent";
        writer.append(REPLACEID + questionnaireLevel1ContendID);
        writer.append(REPLACECONTENT);
        writer.append("<div id='" + questionnaireLevel1ContendID + "'>");
        writer.append(rootRenderer.renderSummaryDialog(
                (Session) httpSession.getAttribute(D3WEB_SESSION),
                SummaryD3webRenderer.SummaryType.QUESTIONNAIRE_LEVEL1, httpSession));
        writer.append("<div>");

        String gridContentID = "gridSummaryContent";

        writer.append(REPLACEID + gridContentID);
        writer.append(REPLACECONTENT);
        writer.append("<div id='" + gridContentID + "'>");
        writer.append(rootRenderer.renderSummaryDialog(
                (Session) httpSession.getAttribute(D3WEB_SESSION),
                SummaryD3webRenderer.SummaryType.GRID, httpSession));
        writer.append("<div>");
    }

    protected void logInitially(HttpServletRequest request, JSONLogger logger, HttpSession httpSession) {
        // get values to logQuestionValue initially: browser, user, and start time
        String browser =
                request.getParameter("browser").replace("+", " ");
        String user =
                request.getParameter("user").replace("+", " ");
        String start =
                request.getParameter("timestring").replace("+", " ");
        ServletLogUtils.logBaseInfo(browser, user, start, logger);


        // on first show, also log the USABILITY GROUP - for multiple group
        // testing - as specified in the 
        // prototype xml as well as the SYSTEM TYPE
        String group =
                httpSession.getAttribute("uegroup").toString() != null
                ? httpSession.getAttribute("uegroup").toString() : "";
        // WIRD IM DialogRenderer in die httpSession geschrieben!
        if (group != null && !group.equals("")) {
            String isGroupLogged =
                    httpSession.getAttribute("isGroupLogged") != null
                    ? httpSession.getAttribute("isGroupLogged").toString() : "";

            if (!isGroupLogged.equals("true")) {
                ServletLogUtils.logUEGroup(group, logger);
                httpSession.setAttribute("isGroupLogged", "true");
            }
        }


        /*
         * String uesystemtype =
         * httpSession.getAttribute("uesystemtype").toString() != null ?
         * httpSession.getAttribute("uesystemtype").toString() : ""; // WIRD IM
         * DialogRenderer in die httpSession geschrieben! if (uesystemtype !=
         * null && !uesystemtype.equals("")) { String isSystemTypeLogged =
         * httpSession.getAttribute("isSystemTypeLogged") != null ?
         * httpSession.getAttribute("isSystemTypeLogged").toString() : "";
         *
         * if (!isSystemTypeLogged.equals("true")) {
         * ServletLogUtils.logDialogType(uesystemtype, logger);
         * httpSession.setAttribute("isSystemTypeLogged", "true"); } }
         */
    }

    protected void logSessionEnd(HttpServletRequest request, HttpSession httpSession) {
        String end = request.getParameter("timestring").replace("+", " ");
        JSONLogger logger = (JSONLogger) httpSession.getAttribute("logger");
        ServletLogUtils.logSessionEnd(end, logger);

        //httpSession.setAttribute("logger", new JSONLogger(createLogfileName()));
        //httpSession.setAttribute("initlog", end);
        //D3webConnector.getInstance().setLogger(new JSONLogger());
        //GlobalSettings.getInstance().setInitLogged(false);
    }

    protected void logWidget(HttpServletRequest request, HttpSession httpSession) {
        String widgetID = request.getParameter("widget");
        String time = request.getParameter("timestring").replace("+", " ");
        String language = "";

        if (request.getParameter("language") != null) {
            widgetID = "LANGUAGE";
            language = request.getParameter("language");
        }

        JSONLogger logger = (JSONLogger) httpSession.getAttribute("logger");
        ServletLogUtils.logWidget(widgetID, time, language, logger);
    }

    protected void logInfoPopup(HttpServletRequest request, HttpSession httpSession) {
        String id = request.getParameter("id");
        String start = request.getParameter("timestring");
        String timediff = request.getParameter("value");
        id = id.replace("+", " ");
        start = start.replace("+", " ");

        JSONLogger logger = (JSONLogger) httpSession.getAttribute("logger");
        ServletLogUtils.logInfoPopup(id, start, timediff, logger);
    }

    protected void logNotAllowed(HttpServletRequest request, HttpSession httpSession) {
        String logtime = request.getParameter("timestring").replace("+", " ");
        String value = request.getParameter("value");
        String question = request.getParameter("id");
        question = AbstractD3webRenderer.getObjectNameForId(question);

        JSONLogger logger = (JSONLogger) httpSession.getAttribute("logger");
        ServletLogUtils.logNotAllowed(logtime, value, question, logger);
    }

    /**
     * Default additional method that can be used to handle additional (default)
     * dialog specific actions only by overwriting this.
     *
     * @param httpSession
     * @param request
     * @param response
     * @param action
     * @throws IOException
     */
    protected void handleDialogSpecificActions(
            HttpSession httpSession, HttpServletRequest request,
            HttpServletResponse response, String action)
            throws IOException {
        // Overwrite if necessary
    }

    /**
     * Default method for getting the source parameter. As default, Default.xml
     * is suggested; yet if a src parameter is given by the webapp (when calling
     * from ControlCenter, for example), that one is taken.
     *
     * @param request the HttpServletRequest
     * @return the source string
     */
    protected String getSource(HttpServletRequest request, HttpSession http) {

        // Overwrite if necessary
        String source = "Default.xml"; // default
        if (request.getParameter("src") != null) {
            source = request.getParameter("src");
        }
        return source.endsWith(".xml") ? source : source + ".xml";



    }

    protected class DialogState {

        Set<QASet> indicatedQASets;
        List<Question> answeredQuestions;
        Set<TerminologyObject> unknownQuestions;

        DialogState(Session d3webSession) {
            indicatedQASets = D3webUtils.getActiveSet(d3webSession);
            answeredQuestions = d3webSession.getBlackboard().getAnsweredQuestions();
            unknownQuestions = D3webUtils.getUnknownQuestions(d3webSession);
        }
    }
// TODO ingetrate logfilename creation for prototpes

    protected String createLogfileName(Date loggingstart, HttpSession httpSession) {
        String formatted = SDF_FILENAME_DEFAULT.format(loggingstart);
        Session sid = (Session) httpSession.getAttribute(D3WEB_SESSION);
        //String sid = D3webConnector.getInstance().getSession().getId();

        return formatted + "_" + sid.getId() + ".txt";
    }

    protected void markWidget(HttpServletRequest request, HttpSession httpSession) {
        if (request.getParameter("isWidget") != null) {
            String mark = request.getParameter("isWidget");
            httpSession.setAttribute("isWidget", mark);
        }

    }

    protected void checkWidgetClicked(HttpSession httpSession, HttpServletResponse response)
            throws IOException {
        PrintWriter writer = response.getWriter();
        if (httpSession.getAttribute("isWidget") != null) {
            //System.out.println("MARK: " + httpSession.getAttribute("isWidget"));
            if (httpSession.getAttribute("isWidget").toString().equals("true")) {

                writer.append("true");

            } else {
                writer.append("false");
            }

        }
    }

    private boolean equalOrHigher(String version, String refNr) {
        int nr = -1;
        if (version.contains(".")) {
            String verArray[] = version.split("\\.");
            nr = Integer.parseInt(verArray[0]);
        } else {
            nr = Integer.parseInt(version);
        }
        int ref = Integer.parseInt(refNr);

        if (nr >= ref) {
            return true;
        }

        return false;
    }

    private String assembleBrowserCompatibilityMessage(String browser, String version) {
        StringBuilder bui = new StringBuilder();

        bui.append("<div id=\"BROWSERINFO\" style='width: 700px; margin-left:auto; margin-right:auto; margin-top:50px; font-size:1.5em; color: red'>");
        bui.append("You are currently using <b>" + browser + " " + version);
        bui.append(".</b><br /><br />This website does NOT fully support this browser/version! <br /><br />");
        bui.append("Please use instead one of the following suggestions:");
        bui.append("<ul>");
        bui.append("<li>");
        bui.append("<a href='http://www.mozilla-europe.org/de/'>Mozilla Firefox</a>");
        bui.append("</li>");
        bui.append("<li>");
        bui.append("<a href='http://www.google.com/chrome/'>Google Chrome</a>");
        bui.append("</li>");
        bui.append("<li>");
        bui.append("<a href='http://windows.microsoft.com/de-DE/internet-explorer/products/ie-9/features'>Internet Explorer <b>9.0</b></a>");
        bui.append("</li>");
        bui.append("</ul>");
        bui.append("</div>");

        return bui.toString();
    }

    protected void saveShowStatus(HttpServletRequest request,
            HttpSession httpSession) {
        // do nothing for default dialogs so far, as not needed there
        // overwritten in ClarihieDilaog (which should be renamed to iTreeDialog)
    }

    /*
     * itree specific
     */
    protected void addFactsYN(HttpServletRequest request,
            HttpServletResponse response, HttpSession httpSession)
            throws IOException {
        // overwritten by ClariHie Dialog
    }

    protected void addFactITree(HttpServletRequest request,
            HttpServletResponse response, HttpSession httpSession)
            throws IOException {
        // overwritten by ClariHie Dialog
    }

    /*
     * eurahs specific
     */
    protected void loadCaseClear(HttpServletRequest request,
            HttpServletResponse response, HttpSession httpSession)
            throws IOException {
        // overwritten by EuraHS Dialog
    }

    /**
     * Parse the dialog specification and initialize the dialog/servlet
     * correspondingly
     *
     * @param httpSession
     * @param source
     * @throws IOException
     */
    protected void parseAndInitDialogServlet(HttpSession httpSession)
            throws IOException {

        httpSession.setAttribute("loginit", false);

        File specs = null;
        if (httpSession.getAttribute("latestSpec") != null) {
            specs = (File)httpSession.getAttribute("latestSpec");
            d3webParser.parse(specs);
        } else {
            d3webParser.parse();
        }

        // Only parse d3web from XML specs if it was not provided before,
        // e.g., by the DialogManager Servlet
        File d3web = null;
        if (httpSession.getAttribute("latestD3web") != null) {
            d3web = (File)httpSession.getAttribute("latestD3web");
            System.out.println(d3web);
            d3wcon.setKb(D3webUtils.getDocToD3webKnowledgeBase(d3web));
        } else {
            d3wcon.setKb(d3webParser.getKnowledgeBase());
        }
        d3wcon.setDialogStrat(d3webParser.getStrategy());
        d3wcon.setDialogType(d3webParser.getType());
        d3wcon.setIndicationMode(d3webParser.getIndicationMode());

        uis.setDialogColumns(d3webParser.getDialogColumns());
        uis.setQuestionColumns(d3webParser.getQuestionColumns());
        uis.setQuestionnaireColumns(d3webParser.getQuestionnaireColumns());
        uis.setCss(d3webParser.getCss());
        uis.setHeader(d3webParser.getHeader());
        uis.setUIprefix(d3webParser.getUIPrefix());
        uis.setLoginMode(d3webParser.getLoginMode());


        String uegroup = d3webParser.getUEGroup() != null ? d3webParser.getUEGroup() : "";
        httpSession.setAttribute("uegroup", uegroup);

        uesettings.setLogging(d3webParser.getLogging());
        uesettings.setFeedbackform(d3webParser.getFeedbackform());
        uesettings.setUequestionnaire(d3webParser.getUEQuestionnaire());

        // set dialog language (for internationalization of widgets, NOT
        // KB elements (specified in knowledge base
        if (!d3webParser.getLanguage().equals("")) {
            uis.setLanguage(d3webParser.getLanguage());
        }

        // Get userprefix specification
        String userpref = "DEFAULT";
        if (!(uis.getUIprefix().equals(""))
                && !(uis.getUIprefix() == null)) {
            userpref = uis.getUIprefix();
        }

        // set necessary paths for saving stuff such as cases, logfiles...
        GLOBSET.setCaseFolder(
                GLOBSET.getServletBasePath()
                + "../../" + userpref + "-Data/cases");


        GLOBSET.setLogBaseFolder(
                GlobalSettings.getInstance().getServletBasePath()
                + "../../" + userpref + "-Data/LOGS");


        // if a new dialog is loaded we also need a new session to start
        //resetD3webSession(httpSession);
        resetD3webSession(httpSession);


        /*
         * initialize logging
         */
        initializeLoggingMechanism(httpSession);

        // stream images from KB into webapp
        GLOBSET.setKbImgFolder(GLOBSET.getServletBasePath() + "kbimg");
        D3webUtils.streamImages();


        // do we need to enable debug mode?!  
        if (d3webParser.getDebug() != null && d3webParser.getDebug()) {
            httpSession.setAttribute("debug", "true");
        }
    }
}
