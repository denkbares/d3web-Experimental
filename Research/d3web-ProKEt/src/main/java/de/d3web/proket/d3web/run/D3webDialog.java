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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webRendererMapping;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.d3web.input.D3webXMLParser;
import de.d3web.proket.d3web.input.D3webXMLParser.LoginMode;
import de.d3web.proket.d3web.output.render.AbstractD3webRenderer;
import de.d3web.proket.d3web.output.render.DefaultRootD3webRenderer;
import de.d3web.proket.d3web.output.render.IQuestionD3webRenderer;
import de.d3web.proket.d3web.output.render.SummaryD3webRenderer;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.d3web.ue.log.JSONLogger;
import de.d3web.proket.d3web.utils.PersistenceD3webUtils;
import de.d3web.proket.database.DB;
import de.d3web.proket.database.DateCoDec;
import de.d3web.proket.database.TokenThread;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.GlobalSettings;
import java.util.Date;

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
    protected static final String REPLACECONTENT = "##replacecontent##";
    protected static final String REPLACEID = "##replaceid##";
    protected static String sourceSave;
    protected final GlobalSettings GLOBSET = GlobalSettings.getInstance();
    protected D3webXMLParser d3webParser;
    protected D3webConnector d3wcon;
    protected static Map<String, List<String>> usrDat = null;
    private String prevQ = "";
    private String prevV = "";

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
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");

        HttpSession httpSession = request.getSession(true);

        // try to get the src parameter, i.e. the specification of the dialog
        String source = getSource(request);
        d3webParser.setSourceToParse(source);

        d3wcon = D3webConnector.getInstance();
        d3wcon.setD3webParser(d3webParser);

        // set SRC store attribute to "" per default for avoiding nullpointers
        if (sourceSave == null) {
            sourceSave = "";
        }

        // only parse again if stored source is not equal to current source
        // then a new dialog has been called
        if (!sourceSave.equals(source)) {
            httpSession.setAttribute("first", "true");
            sourceSave = source;
            d3webParser.parse();

            d3wcon.setKb(d3webParser.getKnowledgeBase());
            d3wcon.setKbName(d3webParser.getKnowledgeBaseName());
            // d3wcon.setDialogStrat(d3webParser.getStrategy());
            d3wcon.setDialogType(d3webParser.getType());
            d3wcon.setIndicationMode(d3webParser.getIndicationMode());
            d3wcon.setDialogColumns(d3webParser.getDialogColumns());
            d3wcon.setQuestionColumns(d3webParser.getQuestionColumns());
            d3wcon.setQuestionnaireColumns(d3webParser.getQuestionnaireColumns());
            d3wcon.setCss(d3webParser.getCss());
            d3wcon.setHeader(d3webParser.getHeader());
            d3wcon.setUserprefix(d3webParser.getUserPrefix());
            d3wcon.setSingleSpecs(d3webParser.getSingleSpecs());
            d3wcon.setLoginMode(d3webParser.getLogin());

            // if a new dialog is loaded we also need a new session to start
            resetD3webSession(httpSession);

            // switch on/off logging depending on xml specification
            if (d3webParser.getLogging().contains("ON")) {
                d3wcon.setLogging(true);
            }

            // set dialog language (for internationalization of widgets, NOT
            // KB elements (specified in knowledge base
            if (!d3webParser.getLanguage().equals("")) {
                d3wcon.setLanguage(d3webParser.getLanguage());
            }

            // Get userprefix specification
            String userpref = "DEFAULT";
            if (!(d3wcon.getUserprefix().equals(""))
                    && !(d3wcon.getUserprefix() == null)) {
                userpref = d3wcon.getUserprefix();
            }

            // set necessary paths for saving stuff such as cases, logfiles...
            GLOBSET.setCaseFolder(
                    GLOBSET.getServletBasePath()
                    + "../../" + userpref + "-Data/cases");

            GLOBSET.setLogFolder(
                    GLOBSET.getServletBasePath()
                    + "../../" + userpref + "-Data/logs");

            // stream images from KB into webapp
            GLOBSET.setKbImgFolder(GLOBSET.getServletBasePath() + "kbimg");
            D3webUtils.streamImages();
        }

        // in case nothing other is provided, "show" is the default action
        String action = request.getParameter("action");

        if (action == null) {
            // action = "mail";
            action = "show";
        }
        if (action.equalsIgnoreCase("dbLogin")) {
            loginDB(request, response, httpSession);
            return;
        }

        // in case of db login (as for EuraHS) redirect to the EuraHS-Login
        // Servlet --> TODO refactor: rename EuraHS-Login Servlet or create
        // superservlet to be overwritten
        if (d3wcon.getLoginMode() == LoginMode.db) {
            String authenticated = (String) httpSession.getAttribute("authenticated");
            if (authenticated == null || !authenticated.equals("yes")) {
                response.sendRedirect("../EuraHS-Login");
                return;
            }
        }


        // switch action as defined by the servlet call
        if (action.equalsIgnoreCase("show")) {
            System.out.println("SHOW");
            show(request, response, httpSession);
            return;
        } else if (action.equalsIgnoreCase("addfacts")) {
            addFacts(request, response, httpSession);
            return;
        } else if (action.equalsIgnoreCase("savecase")) {
            saveCase(request, response, httpSession);
            return;
        } else if (action.equalsIgnoreCase("loadcase")) {
            loadCase(request, response, httpSession);
            return;
        } else if (action.equalsIgnoreCase("deletecase")) {
            deleteCase(request, response, httpSession);
            return;
        } else if (action.equalsIgnoreCase("updatesummary")) {
            updateSummary(request, response, httpSession);
            return;
        } else if (action.equalsIgnoreCase("reset")
                || action.equalsIgnoreCase("resetNewUser")) {
            resetD3webSession(httpSession);
            return;
        } else if (action.equalsIgnoreCase("gotoStatistics")) {
            gotoStatistics(response, httpSession);
            return;
        } else if (action.equalsIgnoreCase("checkUsrDatLogin")) {
            checkUsrDatLogin(response, httpSession);
            return;
        } else if (action.equalsIgnoreCase("usrDatlogin")) {
            loginUsrDat(request, response, httpSession);
            return;
        } else if (action.equalsIgnoreCase("sendmail")) {
            try {
                sendMail(request, response, httpSession);
                response.getWriter().append("success");
            } catch (MessagingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return;
        } else if (action.equalsIgnoreCase("language")) {
            setLanguageID(request);
            return;
        } else if (action.equalsIgnoreCase("logInit")) {
            JSONLogger logger = new JSONLogger();
            d3wcon.setLogger(logger);
            // TODO remove logger from d3webconnector
            Date now = new Date();
            D3webServletLogUtils.initialize(logger, now, httpSession);
            logInitially(request, httpSession);
            return;
        } else if (action.equalsIgnoreCase("logEnd")) {
            logSessionEnd(request);
            return;
        } else if (action.equalsIgnoreCase("logWidget")) {
            logWidget(request);
            return;
        } else if (action.equalsIgnoreCase("logInfoPopup")) {
            logInfoPopup(request);
            return;
        } else if (action.equalsIgnoreCase("checkInitialLoggingReload")) {
            checkInitialLoggingReload(httpSession, response);
            return;
        } else {
            handleDialogSpecificActions(httpSession, request, response, action);
            return;
        }
    }

    /* Initial check whether...
     * 
     * @created 26.07.2011
     * @param response
     * @param httpSession
     * @throws IOException
     */
    protected void checkInitialLoggingReload(HttpSession httpSession,
            HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();

        if (httpSession.getAttribute("first") == "true") {
            httpSession.setAttribute("first", "false");
            writer.append("firsttime");

        } else {
            writer.append("later");
        }
    }

    /**
     * Add one or several given facts. Thereby, first check whether input-store
     * has elements, if yes, parse them and set them (for num/text/date
     * questions), if no, just parse and set a given single value.
     * 
     * @created 28.01.2011
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
        getParameterPairs(request, "question", "value", questions, values);

        if (!handleRequiredValueCheck(writer, d3webSession, questions, values)) {
            return;
        }

        DialogState stateBefore = new DialogState(d3webSession);

        setValues(d3webSession, questions, values, request);
        D3webUtils.resetAbandonedPaths(d3webSession);

        PersistenceD3webUtils.saveCase((String) httpSession.getAttribute("user"), "autosave",
                d3webSession);

        DialogState stateAfter = new DialogState(d3webSession);

        Set<TerminologyObject> diff = calculateDiff(d3webSession, stateBefore, stateAfter);

        renderAndUpdateDiff(writer, d3webSession, diff);
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
    private boolean handleRequiredValueCheck(PrintWriter writer, Session d3webSession, List<String> questions, List<String> values) {
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

    private void setValues(Session d3webSession, List<String> questions, List<String> values, HttpServletRequest request) {
        for (int i = 0; i < questions.size(); i++) {
            D3webUtils.setValue(questions.get(i), values.get(i), d3webSession);

            if (d3wcon.isLogging()) {
                // logQuestionValue all changed widgets/items
                if (questions.get(i).equals(prevQ)) {
                    if (!values.get(i).equals(prevV)) {
                        D3webServletLogUtils.logQuestionValue(questions.get(i), values.get(i), request);
                        prevQ = questions.get(i);
                        prevV = values.get(i);
                    }
                } else {
                    D3webServletLogUtils.logQuestionValue(questions.get(i), values.get(i), request);
                    prevQ = questions.get(i);
                    prevV = values.get(i);
                }
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
        D3webUtils.getDiff(afterState.indicatedQASets, beforeState.indicatedQASets, diff);

        diff.addAll(D3webUtils.getUnknownQuestions(d3webSession));
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

    private void renderAndUpdateDiff(PrintWriter writer, Session d3webSession, Set<TerminologyObject> diff) {
        ContainerCollection cc = new ContainerCollection();
        for (TerminologyObject to : diff) {
            if (isHiddenOrHasHiddenParent(to)) {
                continue;
            }
            IQuestionD3webRenderer toRenderer = AbstractD3webRenderer.getRenderer(to);
            writer.append(REPLACEID + AbstractD3webRenderer.getID(to));
            writer.append(REPLACECONTENT);
            writer.append(toRenderer.renderTerminologyObject(d3webSession, cc, to,
                    to instanceof QContainer
                    ? d3wcon.getKb().getRootQASet()
                    : D3webUtils.getQuestionnaireAncestor(to)));
        }
        writer.append(REPLACEID + "headerInfoLine");
        writer.append(REPLACECONTENT);
        DefaultRootD3webRenderer rootRenderer =
                (DefaultRootD3webRenderer) D3webRendererMapping.getInstance().getRenderer(null);
        writer.append(rootRenderer.renderHeaderInfoLine(d3webSession));
    }

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
    protected void setLanguageID(HttpServletRequest request) {
        int localeID =
                Integer.parseInt(
                request.getParameter("langID").toString());
        GLOBSET.setLocaleIdentifier(localeID);
    }

    /**
     * Checks, whether a potentially required value is already set in the KB or
     * is contained in the current set of values to write to the KB. If yes, the
     * method returns true, if no, false.
     * 
     * @created 15.04.2011
     * @param requiredVal The required value that is to check
     * @param sess The d3webSession
     * @param valToSet The single value to set
     * @param store The value store
     * @return TRUE of the required value is already set or contained in the
     *         current set of values to set
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
        if (contains || (lastFact != null && lastFact.getValue().toString() != "")) {
            return true;
        }
        return false;
    }

    /**
     * Initial check whether...
     * 
     * @created 26.07.2011
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
    private void getParameterPairs(
            HttpServletRequest request, String paraName1, String paraName2,
            List<String> parameters1, List<String> parameters2) {
        int i = 0;
        while (true) {
            String para1 = request.getParameter(paraName1 + i);
            String para2 = request.getParameter(paraName2 + i);
            if (para1 == null || para2 == null) {
                break;
            }
            parameters1.add(para1);
            parameters2.add(para2);
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

    /**
     * Loading a case.
     * 
     * @created 09.03.2011
     * @param request ServletRequest
     * @param response ServletResponse
     */
    protected void loadCase(HttpServletRequest request,
            HttpServletResponse response, HttpSession httpSession) {

        String filename = request.getParameter("fn");
        String user = (String) httpSession.getAttribute("user");
        loadCaseUserFilename(httpSession, user, filename);
    }

    /*
     * Helper method for above loadCase() method and for single access of load
     * case mechanism with a given username and filename
     */
    protected void loadCaseUserFilename(HttpSession httpSession, String user, String filename) {
        if (PersistenceD3webUtils.existsCase(user, filename)) {
            httpSession.setAttribute(D3WEB_SESSION,
                    PersistenceD3webUtils.loadUserCase(user, filename));
            httpSession.setAttribute("lastLoaded", filename);
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

            loadCaseUserFilename(httpSession, email, "autosave");

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
        if (d3wcon.isLogging()) {
            GLOBSET.setInitLogged(false);
            d3wcon.setLogger(new JSONLogger());
        }
    }

    /**
     * Basic servlet method for displaying the dialog.
     * 
     * @created 28.01.2011
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

        // new ContainerCollection needed each time to get an updated dialog
        ContainerCollection cc = new ContainerCollection();
        Session d3webSess = (Session) httpSession.getAttribute(D3WEB_SESSION);
        cc = d3webr.renderRoot(cc, d3webSess, httpSession);
        writer.print(cc.html.toString()); // deliver the rendered output
        writer.close(); // and close
    }

    /**
     * Saving a case.
     * 
     * @created 08.03.2011
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
        boolean force = forceString != null && forceString.equals("true");
        Session d3webSession = (Session) httpSession.getAttribute(D3WEB_SESSION);

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
     * @param request
     * @param response
     * @param httpSession
     * @throws MessagingException
     */
    protected void sendMail(HttpServletRequest request, HttpServletResponse response,
            HttpSession httpSession) throws MessagingException {

        final String user = "SendmailAnonymus@freenet.de";
        final String pw = "sendmail";

        /* setup properties for mail server */
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

        /* A subject */
        message.setSubject(this.getClass().getSimpleName() + " Loginanfrage");

        message.setText("Bitte Logindaten erneut zusenden: \n\n"
                + "Benutzername: " + loginUser);
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
                SummaryD3webRenderer.SummaryType.QUESTIONNAIRE));
        writer.append("<div>");

        String gridContentID = "gridSummaryContent";

        writer.append(REPLACEID + gridContentID);
        writer.append(REPLACECONTENT);
        writer.append("<div id='" + gridContentID + "'>");
        writer.append(rootRenderer.renderSummaryDialog(
                (Session) httpSession.getAttribute(D3WEB_SESSION),
                SummaryD3webRenderer.SummaryType.GRID));
        writer.append("<div>");
    }

    protected void logInitially(HttpServletRequest request, HttpSession httpSession) {
        D3webServletLogUtils.logInitially(request, httpSession);
    }

    protected void logSessionEnd(HttpServletRequest request) {
        D3webServletLogUtils.logSessionEnd(request);
    }

    protected void logWidget(HttpServletRequest request) {
        D3webServletLogUtils.logWidget(request);
    }

    protected void logInfoPopup(HttpServletRequest request) {
        D3webServletLogUtils.logInfoPopup(request);
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
    protected String getSource(HttpServletRequest request) {

        // Overwrite if necessary
        String source = "Default.xml"; // default
        if (request.getParameter("src") != null) {
            source = request.getParameter("src");
        }
        return source.endsWith(".xml") ? source : source + ".xml";
    }

    private class DialogState {

        Set<QASet> indicatedQASets;
        List<Question> answeredQuestions;
        Set<TerminologyObject> unknownQuestions;

        DialogState(Session d3webSession) {
            indicatedQASets = D3webUtils.getActiveSet(d3webSession);
            answeredQuestions = d3webSession.getBlackboard().getAnsweredQuestions();
            unknownQuestions = D3webUtils.getUnknownQuestions(d3webSession);
        }
    }
}
