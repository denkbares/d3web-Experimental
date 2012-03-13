/**
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.proket.run;

import de.d3web.proket.d3web.run.ServletLogUtils;
import de.d3web.proket.d3web.ue.JSONLogger;
import de.d3web.proket.data.DialogTree;
import de.d3web.proket.input.xml.XMLParser;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.output.render.IRenderer;
import de.d3web.proket.output.render.Renderer;
import de.d3web.proket.utils.GlobalSettings;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
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

/**
 * Servlet for rendering non-d3web dialogs.
 *
 * @author Martina Freiberg, Johannes Mitlmeier
 */
public class DialogServlet extends HttpServlet {

    private static final long serialVersionUID = -1514789465295324518L;
    private static final SimpleDateFormat DATE_FORMAT_DEFAULT =
            new SimpleDateFormat("yyyyMMdd_HHmmss");
    private String sourceSave = "";

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DialogServlet() {
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
        GlobalSettings.getInstance().setServletBasePath(servletcontext);

    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");

        HttpSession httpSession = request.getSession(true);
        
        if(httpSession.getAttribute("newload")==null ||
                httpSession.getAttribute("newload").equals("")){
            httpSession.setAttribute("newload", "true");
        }
                
        String source = getSource(request);

        // set SRC store attribute to "" per default for avoiding nullpointers
        if (sourceSave == null) {
            sourceSave = "";
        }

        // only parse again if stored source is not equal to current source
        // then a new dialog has been called
        if (!sourceSave.equals(source)) {
            httpSession.removeAttribute("initlogged");
            //httpSession.setAttribute("first", "true");
            sourceSave = source;
        }

        // in case nothing other is provided, "show" is the default action
        String action = request.getParameter("action");
        if (action == null) {
            action = "show";
            httpSession.setAttribute("newload", "first");
        }

         System.out.println(action);
        System.out.println(httpSession.getAttribute("newload"));
        
        
        if (!action.equalsIgnoreCase("logInit") && 
                !httpSession.getAttribute("newload").equals("first")){
            httpSession.setAttribute("newload", "false");
        }
        
        if (action.equalsIgnoreCase("logInit")&&
                httpSession.getAttribute("newload").equals("first")){
            httpSession.setAttribute("newload", "true");
        }  else if (action.equalsIgnoreCase("logInit")&&
                !httpSession.getAttribute("newload").equals("first")) {
            httpSession.setAttribute("newload", "false");
        }
        
        System.out.println(httpSession.getAttribute("newload") + "\n");
         
        //if(action.equalsIgnoreCase("logInit") &&
          //      httpSession.getAttribute("newload").equals("")){
        //}
        
        if (action.equalsIgnoreCase("show")) {
            show(httpSession, response, request);
            return;
        } else if (action.equalsIgnoreCase("logInit")) {

            if (httpSession.getAttribute("newload").equals("true")) {
                response.getWriter().append("firsttime");
                httpSession.setAttribute("newload", "false");
            } else {
                logInitially(request, response, httpSession);
            }

            return;
        } else if (action.equalsIgnoreCase("logEnd")) {

            String end = request.getParameter("timestring").replace("+", " ");
            JSONLogger logger = (JSONLogger) httpSession.getAttribute("logger");
            ServletLogUtils.logSessionEnd(end, logger);
            httpSession.invalidate();

        } else if (action.equalsIgnoreCase("logDiagnosis")) {

            String soltext = request.getParameter("id").replace("+", " ");
            String rating = request.getParameter("rating");
            JSONLogger logger = (JSONLogger) httpSession.getAttribute("logger");
            ServletLogUtils.logDiagnosis(soltext, rating, logger);

        } else if (action.equalsIgnoreCase("logUEFeedback")) {

            JSONLogger logger = (JSONLogger) httpSession.getAttribute("logger");
            String feedback = request.getParameter("ueFeedback").toString().replace("+", " ");
            String logtime =
                    request.getParameter("timestring").replace("+", " ");
            ServletLogUtils.logUEFeedback(feedback, logtime, logger);

        } else if (action.equalsIgnoreCase("logUEQuestionnaire")) {

            JSONLogger logger = (JSONLogger) httpSession.getAttribute("logger");
            String qData = request.getParameter("ueQData").toString().replace("_", " ").replace("+", " ");
            ServletLogUtils.logUEQuestionnaire(qData, logger);
            response.getWriter().append("success");

        } else if (action.equalsIgnoreCase("logQuestion")) {

            JSONLogger logger = (JSONLogger) httpSession.getAttribute("logger");
            String qtext = request.getParameter("qtext").toString().replace("+", " ").replace("\\", "");
            String qvalue = request.getParameter("qvalue").toString().replace("+", " ").replace("\\", "");
            String logtime = request.getParameter("timestring").toString().replace("+", " ");
            ServletLogUtils.logQuestionValue(qtext, qvalue, logtime, logger);

        } else if (action.equalsIgnoreCase("logDialogType")) {

            JSONLogger logger = (JSONLogger) httpSession.getAttribute("logger");
            String type = request.getParameter("type").toString();
            ServletLogUtils.logDialogType(type, logger);

        } else if (action.equalsIgnoreCase("logQuestionToggle")) {

            JSONLogger logger = (JSONLogger) httpSession.getAttribute("logger");
            String questionText = request.getParameter("qtext").toString().replace("+"," ");
            String toggleType = request.getParameter("ttype").toString();
            String logtime = request.getParameter("timestring").toString().replace("+", " ");
            ServletLogUtils.logQuestionToggle(questionText, toggleType, logtime, logger);

        } else if (action.equalsIgnoreCase("sendFeedbackMail")) {
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
            return;
        } else if (action.equalsIgnoreCase("sendUEQMail")) {

            try {
                sendUEQMail(request, response, httpSession);
                response.getWriter().append("success");
            } catch (MessagingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return;
        }
    }

    /**
     * Handle the actual display of the Servlet content
     *
     * @param session
     * @param response
     * @param request
     * @throws IOException
     */
    protected void show(HttpSession session,
            HttpServletResponse response,
            HttpServletRequest request)
            throws IOException {

        PrintWriter writer = response.getWriter();
        DialogTree dialogTree = parseInput(request);
        GlobalSettings.getInstance().setQuestionCount("0");

        System.out.println(dialogTree.getRoot());
        IRenderer rootRenderer = Renderer.getRenderer(dialogTree.getRoot());
        ContainerCollection cc = rootRenderer.renderRoot(dialogTree);
        String html = cc.html.toString();

        // deliver the rendered output
        writer.print(html);
        writer.close();
    }

    /**
     * Initialization of the logging mechanisms. If nothing has been logged
     * before, first browser and user info need to be gathered by JS, thus
     * return with "firsttime" in writer. Afterwards, JS calls this action
     * again, now has the browser and user info, and can start logging. // TODO:
     * do this refactoring (logInit not existing anymore) also for //
     * D3webServlets.
     *
     * @param request
     * @param response
     * @param httpSession
     * @throws IOException
     */
    protected void logInitially(HttpServletRequest request,
            HttpServletResponse response, HttpSession httpSession) throws IOException {

        PrintWriter writer = response.getWriter();
        GlobalSettings.getInstance().setLogFolder(
                GlobalSettings.getInstance().getServletBasePath()
                + "../../StudyG1-Data/logs");
        
        /*GlobalSettings.getInstance().setLogFolder(
                GlobalSettings.getInstance().getServletBasePath()
                + "../../Study-Data-G2"/logs");
*/
        
        /*
         * in this case, the logging initialisation, i.e. retrieval of browser
         * etc info has been done successfully and now those values can be
         * processed further
         */
         
        Date now = new Date();
        JSONLogger logger = new JSONLogger(createLogfileName(now, httpSession));

        String browser =
                request.getParameter("browser").replace("+", " ");
        String user =
                request.getParameter("user").replace("+", " ");
        String start =
                request.getParameter("timestring").replace("+", " ");
        ServletLogUtils.logBaseInfo(browser, user, start, logger);
        httpSession.setAttribute("logger", logger);

        //response.getWriter().append(GlobalSettings.getInstance().getLogFolder());
    }

    /**
     * Use the XMLParser to parse the prototype specification files. Therefore,
     * src parameter (if specified) is used for determining which prototype spec
     * to use, otherwise a default src is used. Returns a DialogTree
     * representation of the dialog.
     *
     * @param request
     * @param response
     * @param writer
     * @return the DialogTree representation of the parsed XML-specified dialog.
     */
    private static DialogTree parseInput(HttpServletRequest request) {

        /*
         * if src-Parameter is given, take it, if not, "Standarddialog" is the
         * default source
         */
        String source = "Standarddialog";
        if (request.getParameter("src") != null) {
            source = request.getParameter("src");
        }

        // parse the input source
        XMLParser parser = new XMLParser(source);

        // load the dialog into memory
        return parser.getTree();
    }

    // TODO ingetrate logfilename creation for prototpes
    protected String createLogfileName(Date loggingstart, HttpSession httpSession) {

        String formatted = DATE_FORMAT_DEFAULT.format(loggingstart);
        String sid = (String) httpSession.getId();
        //String sid = D3webConnector.getInstance().getSession().getId();

        String logfilename = formatted + "_" + sid + ".txt";
        httpSession.setAttribute("logfile", logfilename);

        return logfilename;
    }

    protected void sendFeedbackMail(HttpServletRequest request,
            HttpServletResponse response,
            HttpSession httpSession) throws MessagingException {

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
        //TODO include dialog flag here
        // TODO refactor: used both by d3web dialogs and normal prototypes ?!
        /*
         * String dialogFlag = d3wcon.getUserprefix(); if (dialogFlag == null) {
         * dialogFlag = ""; }
         */
        message.setSubject("Feedback");

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
        final String qData = request.getParameter("questionnaireData").toString().replace("+", " ");

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

        qDataBui.append("Corresponding Logfile: ");
        qDataBui.append(httpSession.getAttribute("logfile").toString());
        qDataBui.append("\n\n");
       
        for (String pair : qvpairs){
            String[] splitpair = pair.split("---");
            qDataBui.append(splitpair[0].replace("UE_", ""));
            qDataBui.append(" --> ");
            qDataBui.append(splitpair[1]);
            qDataBui.append("\n");
        }

        // TODO find way to include dialog name here
        /*
         * String dialogFlag = d3wcon.getUserprefix(); if (dialogFlag == null) {
         * dialogFlag = ""; }
         */
        message.setSubject("UE Questionnaire Data");

        message.setText("Username:\t" + username + "\n"
                + "Contact: \t" + contact + "\n\n"
                + "Questionnaire Data:\n"
                + qDataBui.toString().replace("_", " "));
        Transport.send(message);

    }

    protected String getSource(HttpServletRequest request) {

        // Overwrite if necessary
        String source = "Default.xml"; // default
        if (request.getParameter("src") != null) {
            source = request.getParameter("src");
        }
        return source.endsWith(".xml") ? source : source + ".xml";




    }
}