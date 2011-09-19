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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;

import au.com.bytecode.opencsv.CSVReader;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.Resource;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webRendererMapping;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.d3web.input.D3webXMLParser;
import de.d3web.proket.d3web.output.render.AbstractD3webRenderer;
import de.d3web.proket.d3web.output.render.DefaultRootD3webRenderer;
import de.d3web.proket.d3web.output.render.IQuestionD3webRenderer;
import de.d3web.proket.d3web.output.render.ImageHandler;
import de.d3web.proket.d3web.output.render.SummaryD3webRenderer;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.d3web.utils.PersistenceD3webUtils;
import de.d3web.proket.database.DB;
import de.d3web.proket.database.DateCoDec;
import de.d3web.proket.database.TokenThread;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.GlobalSettings;

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

	protected static final String D3WEB_SESSION = "d3webSession";

	protected static final String REPLACECONTENT = "##replacecontent##";

	protected static final String REPLACEID = "##replaceid##";

	private static final long serialVersionUID = -2466200526894064976L;

	protected static Map<String, List<String>> getUserDat() {
		if (usrDat == null) {
			// get parent folder for storing cases
			usrDat = new HashMap<String, List<String>>();

			String csvFile = GlobalSettings.getInstance().getServletBasePath()
					+ "/users/usrdat.csv";
			CSVReader csvr = null;
			String[] nextLine = null;

			try {
				csvr = new CSVReader(new FileReader(csvFile));
				// go through file
				while ((nextLine = csvr.readNext()) != null) {
					// skip first line
					if (!nextLine[0].startsWith("usr")) {
						// if username and pw could be found, return true
						List<String> values = new ArrayList<String>();
						for (String word : nextLine) {
							values.add(word);
						}
						usrDat.put(nextLine[0], values);
					}
				}

			}
			catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return usrDat; // trust no one per default
	}

	/* special parser for reading in the d3web-specification xml */
	protected D3webXMLParser d3webParser;

	/* d3web connector for storing certain relevant properties */
	protected D3webConnector d3wcon;

	protected String sourceSave = "";

	protected static Map<String, List<String>> usrDat = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public D3webDialog() {
		super();
	}

	/**
	 * Basic initialization and servlet method. Always called first, if servlet
	 * is refreshed, called newly etc.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");

		HttpSession httpSession = request.getSession(true);

		// set both persistence (case saving) and image (images streamed from
		// kb) folder
		String fca = GlobalSettings.getInstance().getCaseFolder();
		String fim = GlobalSettings.getInstance().getKbImgFolder();
		if ((fca == (null) || fca.equals("")) &&
				(fim == null || fim.equals(""))) {

			String servletBasePath =
					request.getSession().getServletContext().getRealPath("/");
			GlobalSettings.getInstance().setServletBasePath(servletBasePath);
			GlobalSettings.getInstance().setCaseFolder(servletBasePath + "../../EuraHS-Data/cases");
			GlobalSettings.getInstance().setKbImgFolder(servletBasePath + "kbimg");
		}
		d3wcon = D3webConnector.getInstance();

		// try to get the src parameter, which defines the specification xml
		// with special properties for this dialog/knowledge base
		// if none available, default.xml is set
		String source = getSource();
		if (request.getParameter("src") != null) {
			source = request.getParameter("src");
		}
		if (!source.endsWith(".xml")) {
			source = source + ".xml";
		}

		// d3web parser for interpreting the source/specification xml
		d3webParser = new D3webXMLParser(source);
		d3wcon.setD3webParser(d3webParser);

		// only invoke parser, if XML hasn't been parsed before
		// if it has, a knowledge base already exists
		if (d3wcon.getKb() == null
				|| !source.equals(sourceSave)
				|| !d3wcon.getUserprefix().equals(d3webParser.getUserPrefix())) {
			d3wcon.setKb(d3webParser.getKnowledgeBase());
			d3wcon.setKbName(d3webParser.getKnowledgeBaseName());
			d3wcon.setDialogStrat(d3webParser.getStrategy());
			d3wcon.setDialogType(d3webParser.getType());
			d3wcon.setIndicationMode(d3webParser.getIndicationMode());
			d3wcon.setDialogColumns(d3webParser.getDialogColumns());
			d3wcon.setQuestionColumns(d3webParser.getQuestionColumns());
			d3wcon.setQuestionnaireColumns(d3webParser.getQuestionnaireColumns());
			d3wcon.setCss(d3webParser.getCss());
			d3wcon.setHeader(d3webParser.getHeader());
			d3wcon.setUserprefix(d3webParser.getUserPrefix());
			d3wcon.setSingleSpecs(d3webParser.getSingleSpecs());
			sourceSave = source;
			if (!d3webParser.getLanguage().equals("")) {
				d3wcon.setLanguage(d3webParser.getLanguage());
			}
			streamImages();
		}

		/*
		 * otherwise, i.e. if session is null create a session according to the
		 * specified dialog strategy
		 */
		if (httpSession.getAttribute(D3WEB_SESSION) == null) {
			// create d3web session and store in http session
			Session d3webSession = D3webUtils.createSession(d3wcon.getKb(),
					d3wcon.getDialogStrat());
			httpSession.setAttribute(D3WEB_SESSION, d3webSession);
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
		// Get the current httpSession or a new one
		String authenticated = (String) httpSession.getAttribute("authenticated");
		if (authenticated == null || !authenticated.equals("yes")) {
			response.sendRedirect("../EuraHS-Login");
			return;
		}

		// switch action as defined by the servlet call
		if (action.equalsIgnoreCase("show")) {
			show(request, response, httpSession);
			return;
		}
		else if (action.equalsIgnoreCase("addfacts")) {
			addFacts(request, response, httpSession);
			return;
		}
		else if (action.equalsIgnoreCase("savecase")) {
			saveCase(request, response, httpSession);
			return;
		}
		else if (action.equalsIgnoreCase("loadcase")) {
			loadCase(request, response, httpSession);
			return;
		}
		else if (action.equalsIgnoreCase("updatesummary")) {
			updateSummary(request, response, httpSession);
			return;
		}
		else if (action.equalsIgnoreCase("reset")) {
			Session d3webSession = D3webUtils.createSession(d3wcon.getKb(), d3wcon.getDialogStrat());
			httpSession.setAttribute(D3WEB_SESSION, d3webSession);
			httpSession.setAttribute("lastLoaded", "");
			return;
		}
		else if (action.equalsIgnoreCase("resetNewUser")) {
			Session d3webSession = D3webUtils.createSession(d3wcon.getKb(), d3wcon.getDialogStrat());
			httpSession.setAttribute(D3WEB_SESSION, d3webSession);
			return;
		}
		else if (action.equalsIgnoreCase("gotoStatistics")) {
			gotoStatistics(response, httpSession);
			return;
		}
		else if (action.equalsIgnoreCase("checkUsrDatLogin")) {
			checkUsrDatLogin(response, httpSession);
			return;
		}
		else if (action.equalsIgnoreCase("usrDatlogin")) {
			loginUsrDat(request, response, httpSession);
			return;
		}
		else if (action.equalsIgnoreCase("sendmail")) {
			try {
				sendMail(request, response, httpSession);
				PrintWriter writer = response.getWriter();
				writer.append("success");
			}
			catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		else if (action.equalsIgnoreCase("checkrange")) {
			checkRange(request, response);
			return;
		}
		else {
			handleDialogSpecificActions(httpSession, request, response, action);
		}
	}

	protected void handleDialogSpecificActions(HttpSession httpSession, HttpServletRequest request, HttpServletResponse response, String action) throws IOException {
		// Overwrite if necessary
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
		getParameter(request, "ocq", "occhoice", questions, values);
		getParameter(request, "mcq", "mcchoices", questions, values);
		getParameter(request, "dateq", "date", questions, values);
		getParameter(request, "textq", "text", questions, values);
		getParameter(request, "numq", "num", questions, values);

		/*
		 * Check, whether a required value (for saving) is specified. If yes,
		 * check whether this value has already been set in the KB or is about
		 * to be set in the current call --> go on normally. Otherwise, return a
		 * marker "<required value>" so the user is informed by AJAX to provide
		 * this marked value.
		 */
		List<String> all = new LinkedList<String>();
		all.addAll(questions);
		all.addAll(values);
		String reqVal = D3webConnector.getInstance().getD3webParser().getRequired();
		if (!reqVal.equals("")
				&& !checkReqVal(reqVal, d3webSession, all)) {

			writer.append("##missingfield##");
			writer.append(reqVal);
			return;
		}

		// get dialog state before setting values
		Set<QASet> indicatedTOsBefore = getActiveSet(d3webSession);
		List<Question> answeredQuestionsBefore = d3webSession.getBlackboard().getAnsweredQuestions();
		Set<TerminologyObject> unknownQuestionsBefore = getUnknownQuestions(d3webSession);

		for (int i = 0; i < questions.size(); i++) {
			setValue(questions.get(i), values.get(i), d3webSession);
		}

		resetAbandonedPaths(d3webSession);

		// AUTOSAVE
		PersistenceD3webUtils.saveCase((String) httpSession.getAttribute("user"), "autosave",
				d3webSession);

		// Rerender changed Questions and Questionnaires
		Set<QASet> indicatedTOsAfter = getActiveSet(d3webSession);

		Set<TerminologyObject> unknownQuestionsAfter = getUnknownQuestions(d3webSession);

		Set<TerminologyObject> diff = new HashSet<TerminologyObject>();
		for (TerminologyObject to : unknownQuestionsBefore) {
			if (!unknownQuestionsAfter.contains(to)) diff.add(to);
		}

		getDiff(indicatedTOsBefore, indicatedTOsAfter, diff);
		getDiff(indicatedTOsAfter, indicatedTOsBefore, diff);

		List<Question> answeredQuestionsAfter = d3webSession.getBlackboard().getAnsweredQuestions();
		diff.addAll(getUnknownQuestions(d3webSession));
		answeredQuestionsAfter.removeAll(answeredQuestionsBefore);
		// System.out.println(answeredQuestionsAfter);
		diff.addAll(answeredQuestionsAfter);

		ContainerCollection cc = new ContainerCollection();
		for (TerminologyObject to : diff) {
			IQuestionD3webRenderer toRenderer = AbstractD3webRenderer.getRenderer(to);
			writer.append(REPLACEID + AbstractD3webRenderer.getID(to));
			writer.append(REPLACECONTENT);
			writer.append(toRenderer.renderTerminologyObject(d3webSession, cc, to,
					to instanceof QContainer
							? d3wcon.getKb().getRootQASet()
							: getQuestionnaireAncestor(to)));
		}
		writer.append(REPLACEID + "headerInfoLine");
		writer.append(REPLACECONTENT);
		DefaultRootD3webRenderer rootRenderer =
				(DefaultRootD3webRenderer) D3webRendererMapping
						.getInstance().getRenderer(null);
		writer.append(rootRenderer.renderHeaderInfoLine(d3webSession));
	}

	protected void checkRange(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter writer = response.getWriter();

		String qidsString = request.getParameter("qids");
		qidsString = qidsString.replace("q_", "");

		String[] qids = qidsString.split(";");
		String qidBackstring = "";

		for (String qid : qids) {
			String[] idVal = qid.split("%");

			Question to = d3wcon.getKb().getManager().searchQuestion(idVal[0]);

			if (to instanceof QuestionNum) {
				if (to.getInfoStore().getValue(BasicProperties.QUESTION_NUM_RANGE) != null) {
					NumericalInterval range = to.getInfoStore().getValue(
							BasicProperties.QUESTION_NUM_RANGE);
					qidBackstring += to.getName() + "%" + idVal[1] + "%";
					qidBackstring += range.getLeft() + "-" + range.getRight() + ";";
				}
			}

		}

		writer.append(qidBackstring);
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
		}
		else {
			writer.append("NOLI");
		}
	}

	private Set<QASet> getActiveSet(Session sess) {
		Set<QASet> activeSet = new HashSet<QASet>();
		Set<QASet> initQuestions = new HashSet<QASet>(sess.getKnowledgeBase().getInitQuestions());
		for (QASet qaset : sess.getKnowledgeBase().getManager().getQASets()) {
			if (isActive(qaset, sess.getBlackboard(), initQuestions)) activeSet.add(qaset);
		}
		return activeSet;
	}

	private void getDiff(Set<QASet> set1, Set<QASet> set2, Set<TerminologyObject> diff) {
		for (InterviewObject io : set1) {
			if (!set2.contains(io)) {
				if (io instanceof Question) {
					diff.add(getQuestionnaireAncestor(io));
				}
				else {
					diff.add(io);
				}
			}
		}
	}

	/**
	 * Retrieve the difference between two date objects in seconds
	 * 
	 * @created 29.04.2011
	 * @param d1 First date
	 * @param d2 Second date
	 * @return the difference in seconds
	 */
	protected float getDifference(Date d1, Date d2) {
		return (d1.getTime() - d2.getTime()) / 1000;
	}

	private void getParameter(HttpServletRequest request, String paraName1, String paraName2, List<String> parameters1, List<String> parameters2) {
		int i = 0;
		while (true) {
			String para1 = request.getParameter(paraName1 + i);
			String para2 = request.getParameter(paraName2 + i);
			if (para1 == null || para2 == null) break;
			parameters1.add(para1.replace("+", " "));
			parameters2.add(para2.replace("+", " "));
			i++;
		}
	}

	private TerminologyObject getQuestionnaireAncestor(TerminologyObject to) {
		if (to.getParents() != null) {
			for (TerminologyObject parent : to.getParents()) {
				if (parent instanceof QContainer) {
					return parent;
				}
				else {
					return getQuestionnaireAncestor(parent);
				}
			}
		}
		return null;
	}

	protected String getSource() {
		String source = "default.xml";
		return source;
	}

	private Set<TerminologyObject> getUnknownQuestions(Session sess) {
		Set<TerminologyObject> unknownQuestions = new HashSet<TerminologyObject>();
		for (TerminologyObject to : sess.getBlackboard().getValuedObjects()) {
			Fact mergedFact = sess.getBlackboard().getValueFact(to);
			if (mergedFact != null && Unknown.assignedTo(mergedFact.getValue())) {
				unknownQuestions.add(to);
			}
		}
		return unknownQuestions;
	}

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
	 * Utility method for checking whether a given terminology object is
	 * indicated or instant_indicated or not in the current session.
	 * 
	 * @created 09.03.2011
	 * @param to The terminology object to check
	 * @param bb
	 * @return True, if the terminology object is (instant) indicated.
	 */
	private boolean isActive(QASet qaset, Blackboard bb, Set<QASet> initQuestions) {
		boolean indicatedParent = false;
		for (TerminologyObject parentQASet : qaset.getParents()) {
			if (parentQASet instanceof QContainer
					&& isIndicated((QASet) parentQASet, bb, initQuestions)) {
				indicatedParent = true;
				break;
			}
		}
		return indicatedParent || isIndicated(qaset, bb, initQuestions);
	}

	private boolean isIndicated(QASet qaset, Blackboard bb, Set<QASet> initQuestions) {
		return initQuestions.contains(qaset)
				|| bb.getIndication(qaset).getState() == State.INDICATED
				|| bb.getIndication(qaset).getState() == State.INSTANT_INDICATED;
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

		loadCase(httpSession, user, filename);
	}

	protected void loadCase(HttpSession httpSession, String user, String filename) {
		if (PersistenceD3webUtils.existsCase(user, filename)) {
			httpSession.setAttribute(D3WEB_SESSION,
					PersistenceD3webUtils.loadUserCase(user, filename));
			httpSession.setAttribute("lastLoaded", filename);
		}
	}

	protected void loginDB(HttpServletRequest request, HttpServletResponse response, HttpSession httpSession) throws IOException {
		String token = request.getParameter("t");
		String email = new String(Base64.decodeBase64(request.getParameter("e")));

		if (DB.isValidToken(token, email)) {
			httpSession.setAttribute("authenticated", "yes");
			httpSession.setAttribute("user", email);

			loadCase(httpSession, email, "autosave");

			response.sendRedirect("../EuraHS-Dialog");
		}
		else {
			response.sendRedirect("../EuraHS-Login");
		}
	}

	/**
	 * Handle login of new user
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
	 * Check, whether the user has permissions to log in. Permissions are stored
	 * in userdat.csv in cases parent folder
	 * 
	 * @created 15.03.2011
	 * @param user The user name.
	 * @param password The password.
	 * @return True, if permissions are correct.
	 */
	protected boolean permitUser(String user, String password) {

		List<String> values = getUserDat().get(user);
		if (values != null) {
			String pass = values.get(1);
			if (pass != null && password.equals(pass)) return true;
		}
		return false; // trust no one per default
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
		DefaultRootD3webRenderer d3webr = (DefaultRootD3webRenderer) D3webRendererMapping.getInstance().getRenderer(
				null);

		// new ContainerCollection needed each time to get an updated dialog
		ContainerCollection cc = new ContainerCollection();

		Session d3webSess = (Session) httpSession.getAttribute(D3WEB_SESSION);
		cc = d3webr.renderRoot(cc, d3webSess, httpSession);

		writer.print(cc.html.toString()); // deliver the rendered output

		writer.close(); // and close
	}

	private Collection<Question> resetAbandonedPaths(Session sess) {
		Blackboard bb = sess.getBlackboard();
		Collection<Question> resetQuestions = new LinkedList<Question>();
		Set<QASet> initQuestions = new HashSet<QASet>(d3wcon.getKb().getInitQuestions());
		for (Question question : bb.getAnsweredQuestions()) {
			if (!isActive(question, bb, initQuestions)) {
				Fact lastFact = bb.getValueFact(question);
				if (lastFact != null
						&& lastFact.getPSMethod() == PSMethodUserSelected.getInstance()) {
					bb.removeValueFact(lastFact);
					resetQuestions.add(question);
				}
			}
		}
		return resetQuestions;
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
		}
		else {
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

		String toAddress = "striffler@informatik.uni-wuerzburg.de";
		List<String> usrDat = getUserDat().get(loginUser);
		if (usrDat != null) {
			String email = usrDat.get(2);
			if (email != null) {
				toAddress = email;
			}
		}
		InternetAddress to = new InternetAddress(toAddress);
		message.addRecipient(Message.RecipientType.TO, to);

		/* A subject */
		message.setSubject(this.getClass().getSimpleName() + " Loginanfrage");

		message.setText("Bitte Logindaten erneut zusenden: \n\n" +
				"Benutzername: " + loginUser);
		Transport.send(message);

	}

	/**
	 * Utility method for adding values. Adds a single value for a given
	 * question to the current knowledge base in the current problem solving
	 * session.
	 * 
	 * @created 28.01.2011
	 * @param termObID The ID of the TerminologyObject, the value is to be
	 *        added.
	 * @param valString The value, that is to be added for the TerminologyObject
	 *        with ID valID.
	 */
	protected void setValue(String termObID, String valString, Session sess) {

		if (termObID == null || valString == null) return;

		Fact lastFact = null;
		Blackboard blackboard = sess.getBlackboard();
		Question to = D3webConnector.getInstance().getKb().getManager().searchQuestion(termObID);

		// if TerminologyObject not found in the current KB return & do nothing
		if (to == null) {
			return;
		}

		// init Value object...
		Value value = null;

		// check if unknown option was chosen
		if (valString.equalsIgnoreCase("unknown")) {
			value = setQuestionToUnknown(sess, to);
		}

		// otherwise, i.e., for all other "not-unknown" values
		else {

			// CHOICE questions
			if (to instanceof QuestionChoice) {
				value = setQuestionChoice(to, valString);
			}
			// TEXT questions
			else if (to instanceof QuestionText) {
				value = setQuestionText(to, valString);
			}
			// NUM questions
			else if (to instanceof QuestionNum) {
				value = setQuestionNum(valString);
			}
			// DATE questions
			else if (to instanceof QuestionDate) {
				value = setQuestionDate(to, valString);
			}

			// if reasonable value retrieved, set it for the given
			// TerminologyObject
			if (value != null) {

				if (UndefinedValue.isNotUndefinedValue(value)) {
					// add new value as UserEnteredFact
					Fact fact = FactFactory.createUserEnteredFact(to, value);
					blackboard.addValueFact(fact);
				}
			}
		}

	}

	private Value setQuestionDate(Question to, String valString) {
		Value value = null;
		String dateDescription = to.getInfoStore().getValue(ProKEtProperties.DATE_FORMAT);
		if (dateDescription != null && !dateDescription.isEmpty()) {
			String[] dateDescSplit = dateDescription.split("OR");
			for (String dateDesc : dateDescSplit) {
				dateDesc = dateDesc.trim();
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat(dateDesc);
					value = new DateValue(dateFormat.parse(valString));
				}
				catch (ParseException e) {
					// value still null, will not be set
				}
				catch (IllegalArgumentException e) {
					// value still null, will not be set
				}
				if (value != null) {
					break;
				}
			}
		}
		else {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
				value = new DateValue(dateFormat.parse(valString));
			}
			catch (ParseException e) {
				// value still null, will not be set
			}
		}
		return value;
	}

	private Value setQuestionNum(String valString) {
		try {
			return new NumValue(Double.parseDouble(valString.replace(",", ".")));
		}
		catch (NumberFormatException ex) {
			return null;
		}
	}

	private Value setQuestionText(Question to, String valString) {
		Value value = null;
		String textPattern = to.getInfoStore().getValue(ProKEtProperties.TEXT_FORMAT);
		Pattern p = null;
		if (textPattern != null && !textPattern.isEmpty()) {
			try {
				p = Pattern.compile(textPattern);
			}
			catch (Exception e) {

			}
		}
		if (p != null) {
			Matcher m = p.matcher(valString);
			if (m.find()) {
				value = new TextValue(m.group());
			}
		}
		else {
			value = new TextValue(valString);
		}
		return value;
	}

	private Value setQuestionChoice(Question to, String valString) {
		Value value = null;
		if (to instanceof QuestionOC) {
			// valueString is the ID of the selected item
			try {
				value = KnowledgeBaseUtils.findValue(to, valString);
			}
			catch (NumberFormatException nfe) {
				// value still null, will not be set
			}
		}
		else if (to instanceof QuestionMC) {

			if (valString.equals("")) {
				value = UndefinedValue.getInstance();
			}
			else {
				String[] choices = valString.split(",");
				List<Choice> cs = new ArrayList<Choice>();

				for (String c : choices) {
					cs.add(new Choice(c));
				}
				value = MultipleChoiceValue.fromChoices(cs);

			}
		}
		return value;
	}

	private Value setQuestionToUnknown(Session sess, Question to) {
		Blackboard blackboard = sess.getBlackboard();

		// remove a previously set value
		Fact lastFact = blackboard.getValueFact(to);
		if (lastFact != null) {
			blackboard.removeValueFact(lastFact);
		}

		// and add the unknown value
		Value value = Unknown.getInstance();
		Fact fact = FactFactory.createFact(sess, to, value,
				PSMethodUserSelected.getInstance(),
				PSMethodUserSelected.getInstance());
		blackboard.addValueFact(fact);
		return value;
	}

	/**
	 * Stream images from the KB into intermediate storage in webapp
	 * 
	 * @created 29.04.2011
	 */
	protected void streamImages() {

		for (Resource resource : D3webConnector.getInstance().getKb().getResources()) {
			String rName = resource.getPathName();
			String rType = rName.substring(rName.lastIndexOf('.') + 1).toLowerCase();

			if (rType.equals("jpg") || rType.equals("png")) {
				BufferedImage bui = ImageHandler.getResourceAsBUI(resource);
				try {
					File file = new File(GlobalSettings.getInstance().getKbImgFolder() + "/"
							+ rName);
					ImageIO.write(bui, rType, file);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void updateSummary(HttpServletRequest request, HttpServletResponse response, HttpSession httpSession) throws IOException {
		PrintWriter writer = response.getWriter();

		String questionnaireContentID = "questionnaireSummaryContent";

		writer.append(REPLACEID + questionnaireContentID);
		writer.append(REPLACECONTENT);
		SummaryD3webRenderer rootRenderer = D3webRendererMapping
						.getInstance().getSummaryRenderer();
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
}