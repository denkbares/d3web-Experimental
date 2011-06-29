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

import au.com.bytecode.opencsv.CSVReader;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.Resource;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
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
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.d3web.utils.PersistenceD3webUtils;
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

	private static final long serialVersionUID = -2466200526894064976L;

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
		// set both persistence (case saving) and image (images streamed from
		// kb) folder
		String fca = GlobalSettings.getInstance().getCaseFolder();
		String fim = GlobalSettings.getInstance().getKbImgFolder();
		if ((fca.equals(null) || fca.equals("")) &&
				(fim.equals(null) || fim.equals(""))) {

			String servletBasePath =
					request.getSession().getServletContext().getRealPath("/");
			GlobalSettings.getInstance().setCaseFolder(servletBasePath + "cases");
			GlobalSettings.getInstance().setKbImgFolder(servletBasePath + "kbimg");
		}
		/*
		 * FOLDER PATH: get the folder on the server for persistence storing
		 * only needed here in case the dialog is used without login mechanisms
		 */
		// String folderPath =
		// request.getSession().getServletContext().getRealPath("/");
		// String persistencePath = folderPath.replace("d3web-ProKEt",
		// "persistence");
		// GlobalSettings.getInstance().setCaseFolder(persistencePath);

		d3wcon = D3webConnector.getInstance();

		// in case nothing other is provided, "show" is the default action
		String action = request.getParameter("action");
		if (action == null) {
			// action = "mail";
			action = "show";
		}

		// try to get the src parameter, which defines the specification xml
		// with special properties for this dialog/knowledge base
		// if none available, default.xml is set
		String source = "default.xml";
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
		if (d3wcon.getKb() == null ||
				!source.equals(sourceSave)) {
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

		// Get the current httpSession or a new one
		HttpSession httpSession = request.getSession(true);

		// if (httpSession.getAttribute("imgStreamed") == null) {
		// streamImages();
		// httpSession.setAttribute("imgStreamed", true);
		// }

		/*
		 * otherwise, i.e. if session is null create a session according to the
		 * specified dialog strategy
		 */
		if (httpSession.getAttribute("d3webSession") == null) {
			// create d3web session and store in http session
			Session d3webSession = D3webUtils.createSession(d3wcon.getKb(),
					d3wcon.getDialogStrat());
			httpSession.setAttribute("d3webSession", d3webSession);
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
		else if (action.equalsIgnoreCase("reset")) {

			Session d3webSession = D3webUtils.createSession(d3wcon.getKb(), d3wcon.getDialogStrat());
			httpSession.setAttribute("d3webSession", d3webSession);
			httpSession.setAttribute("lastLoaded", "");
			return;
		}
		else if (action.equalsIgnoreCase("resetNewUser")) {

			Session d3webSession = D3webUtils.createSession(d3wcon.getKb(), d3wcon.getDialogStrat());
			httpSession.setAttribute("d3webSession", d3webSession);

			return;
		}
		else if (action.equalsIgnoreCase("checklogin")) {

			PrintWriter writer = response.getWriter();

			if (httpSession.getAttribute("user") == null) {
				httpSession.setAttribute("log", true);
				writer.append("NLI");
			}
			else {
				writer.append("NOLI");
			}

			return;
		}
		else if (action.equalsIgnoreCase("login")) {
			login(request, response, httpSession);
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
			return;
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
	private void show(HttpServletRequest request, HttpServletResponse response,
			HttpSession httpSession)
			throws IOException {

		PrintWriter writer = response.getWriter();

		// get the root renderer --> call getRenderer with null
		DefaultRootD3webRenderer d3webr = (DefaultRootD3webRenderer) D3webRendererMapping.getInstance().getRendererObject(
				null);

		// new ContainerCollection needed each time to get an updated dialog
		ContainerCollection cc = new ContainerCollection();

		Session d3webSess = (Session) httpSession.getAttribute("d3webSession");
		AbstractD3webRenderer.storeSession(d3webSess);
		cc = d3webr.renderRoot(cc, d3webSess, httpSession);

		writer.print(cc.html.toString()); // deliver the rendered output

		writer.close(); // and close
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

		Session sess = (Session) httpSession.getAttribute("d3webSession");

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
				&& !checkReqVal(reqVal, sess, all)) {

			writer.append("##missingfield##");
			writer.append(reqVal);
			return;
		}

		// get dialog state before setting values
		Set<QASet> indicatedTOsBefore = getActiveSet(sess);
		List<Question> answeredQuestionsBefore = sess.getBlackboard().getAnsweredQuestions();
		Set<TerminologyObject> unknownQuestionsBefore = getUnknownQuestions(sess);

		for (int i = 0; i < questions.size(); i++) {
			setValue(questions.get(i), values.get(i), sess);
		}

		resetAbandonedPaths(sess);

		// AUTOSAVE
		String folderPath = GlobalSettings.getInstance().getCaseFolder();
		Session d3webSession = (Session) httpSession.getAttribute("d3webSession");
		new SaveThread(folderPath, d3webSession).start();

		// Rerender changed Questions and Quesitonnaires
		Set<QASet> indicatedTOsAfter = getActiveSet(sess);

		Set<TerminologyObject> unknownQuestionsAfter = getUnknownQuestions(sess);

		Set<TerminologyObject> diff = new HashSet<TerminologyObject>();
		for (TerminologyObject to : unknownQuestionsBefore) {
			if (!unknownQuestionsAfter.contains(to)) diff.add(to);
		}

		getDiff(indicatedTOsBefore, indicatedTOsAfter, diff);
		getDiff(indicatedTOsAfter, indicatedTOsBefore, diff);

		List<Question> answeredQuestionsAfter = sess.getBlackboard().getAnsweredQuestions();
		diff.addAll(getUnknownQuestions(sess));
		answeredQuestionsAfter.removeAll(answeredQuestionsBefore);
		// System.out.println(answeredQuestionsAfter);
		diff.addAll(answeredQuestionsAfter);

		ContainerCollection cc = new ContainerCollection();
		for (TerminologyObject to : diff) {
			IQuestionD3webRenderer toRenderer = AbstractD3webRenderer.getRenderer(to);
			writer.append("##replaceid##" + AbstractD3webRenderer.getID(to));
			writer.append("##replacecontent##");
			writer.append(toRenderer.renderTerminologyObject(cc, to, to instanceof QContainer
					? d3wcon.getKb().getRootQASet()
					: getQuestionnaireAncestor(to)));
		}

	}

	private Set<TerminologyObject> getUnknownQuestions(Session sess) {
		Set<TerminologyObject> unknownQuestions = new HashSet<TerminologyObject>();
		for (TerminologyObject to : sess.getBlackboard().getValuedObjects()) {
			Fact mergedFact = sess.getBlackboard().getValueFact((ValueObject) to);
			if (mergedFact != null && Unknown.assignedTo(mergedFact.getValue())) {
				unknownQuestions.add(to);
			}
		}
		return unknownQuestions;
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

	protected class SaveThread extends Thread {

		private final String folderPath;
		private final Session d3webSession;

		public SaveThread(String folderPath, Session d3webSession) {
			this.folderPath = folderPath;
			this.d3webSession = d3webSession;
		}

		@Override
		public void run() {

			PersistenceD3webUtils.saveCaseTimestampOneQuestionAndInput(
					folderPath,
					D3webConnector.getInstance().getD3webParser().getRequired(),
					"autosave", d3webSession);
		}
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

		// retrieves path to /cases folder on the server
		String folderPath = GlobalSettings.getInstance().getCaseFolder();
		// folderPath += "/" + httpSession.getAttribute("user");
		// System.out.println(folderPath);
		String userFilename = request.getParameter("userfn");
		String lastLoaded = (String) httpSession.getAttribute("lastLoaded");

		// if any file had been loaded before as a case
		if (lastLoaded != null && !lastLoaded.equals("")) {

			if (PersistenceD3webUtils.existsCase(
					folderPath,
					userFilename,
					D3webConnector.getInstance().getD3webParser().getRequired(),
					(Session) httpSession.getAttribute("d3webSession"))) {

				// if user loaded case before, he can save with that already
				// existing filename
				if (lastLoaded.equals(userFilename)) {
					PersistenceD3webUtils.saveCaseTimestampOneQuestionAndInput(
							folderPath,
							D3webConnector.getInstance().getD3webParser().getRequired(),
							userFilename,
							(Session) httpSession.getAttribute("d3webSession"));
				}
				else {
					writer.append("exists");
				}
			}

			// otherwise
			else {
				System.out.println("save");
				PersistenceD3webUtils.saveCaseTimestampOneQuestionAndInput(
						folderPath,
						D3webConnector.getInstance().getD3webParser().getRequired(),
						userFilename,
						(Session) httpSession.getAttribute("d3webSession"));
			}
		}

		// if no file loaded, there should be no chance of saving with same name
		else {

			// if case already exists, do not enable saving
			if (PersistenceD3webUtils.existsCase(
					folderPath,
					userFilename,
					D3webConnector.getInstance().getD3webParser().getRequired(),
					(Session) httpSession.getAttribute("d3webSession"))) {
				writer.append("exists");
			}

			// otherwise if filename/case is not existent, it can be saved
			else {
				PersistenceD3webUtils.saveCaseTimestampOneQuestionAndInput(
						folderPath,
						D3webConnector.getInstance().getD3webParser().getRequired(),
						userFilename,
						(Session) httpSession.getAttribute("d3webSession"));
			}
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
	 * Loading a case.
	 * 
	 * @created 09.03.2011
	 * @param request ServletRequest
	 * @param response ServletResponse
	 */
	protected void loadCase(HttpServletRequest request,
			HttpServletResponse response, HttpSession httpSession) {

		// get the filename from the corresponding request parameter "fn"
		String filename = request.getParameter("fn");

		String user = "";
		if (httpSession.getAttribute("user") != null) {
			user = httpSession.getAttribute("user").toString();
		}

		// load the file = path + filename
		// PersistenceD3webUtils.loadCaseFromUserFilename(folderPath + "/" +
		// filename);

		Session session = null;
		if (!user.equals("")) {
			session = PersistenceD3webUtils.loadCaseFromUserFilename(filename, user);
		}
		else {
			session = PersistenceD3webUtils.loadCase(filename);
		}

		httpSession.setAttribute("d3webSession", session);

		httpSession.setAttribute("lastLoaded", filename);
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
	protected void login(HttpServletRequest req,
			HttpServletResponse res, HttpSession httpSession)
			throws IOException {

		// fetch the information sent via the request string from login
		String u = req.getParameter("u");
		String p = req.getParameter("p");

		// with login mechanism needed here for initially loading cases,
		// otherwise
		// put it into doGet
		String folderPath = req.getSession().getServletContext().getRealPath("/cases");

		GlobalSettings.getInstance().setCaseFolder(folderPath);

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

			// remove a previously set value
			lastFact = blackboard.getValueFact(to);
			if (lastFact != null) {
				blackboard.removeValueFact(lastFact);
			}

			// and add the unknown value
			value = Unknown.getInstance();
			Fact fact = FactFactory.createFact(sess, to, value,
					PSMethodUserSelected.getInstance(),
					PSMethodUserSelected.getInstance());
			blackboard.addValueFact(fact);

		}

		// otherwise, i.e., for all other "not-unknown" values
		else {

			// CHOICE questions
			if (to instanceof QuestionChoice) {
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
			}
			// TEXT questions
			else if (to instanceof QuestionText) {
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
			}
			// NUM questions
			else if (to instanceof QuestionNum) {
				try {
					value = new NumValue(Double.parseDouble(valString));
				}
				catch (NumberFormatException ex) {
					// value still null, will not be set
				}
			}
			// DATE questions
			else if (to instanceof QuestionDate) {
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
			}

			// if reasonable value retrieved, set it for the given
			// TerminologyObject
			if (value != null) {

				// remove previously set value
				lastFact = blackboard.getValueFact(to);
				if (lastFact != null) {
					blackboard.removeValueFact(lastFact);
				}

				if (UndefinedValue.isNotUndefinedValue(value)) {
					// add new value as UserEnteredFact
					Fact fact = FactFactory.createUserEnteredFact(to, value);
					blackboard.addValueFact(fact);
				}
			}
		}

	}

	private Collection<Question> resetAbandonedPaths(Session sess) {
		Blackboard bb = sess.getBlackboard();
		Collection<Question> resetQuestions = new LinkedList<Question>();
		Set<QASet> initQuestions = new HashSet<QASet>(d3wcon.getKb().getInitQuestions());
		for (Question question : bb.getAnsweredQuestions()) {
			if (!isActive(question, bb, initQuestions)) {
				Fact lastFact = bb.getValueFact(question);
				if (lastFact != null) {
					bb.removeValueFact(lastFact);
					resetQuestions.add(question);
				}
			}
		}
		return resetQuestions;
	}

	private Set<QASet> getActiveSet(Session sess) {
		Set<QASet> activeSet = new HashSet<QASet>();
		Set<QASet> initQuestions = new HashSet<QASet>(sess.getKnowledgeBase().getInitQuestions());
		for (QASet qaset : sess.getKnowledgeBase().getManager().getQASets()) {
			if (isActive(qaset, sess.getBlackboard(), initQuestions)) activeSet.add(qaset);
		}
		return activeSet;
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

	protected static Map<String, List<String>> getUserDat() {
		if (usrDat == null) {
			// get parent folder for storing cases
			usrDat = new HashMap<String, List<String>>();

			String caseFolder = GlobalSettings.getInstance().getCaseFolder();

			String csvFile = caseFolder + "/usrdat.csv";
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

	/**
	 * Check, whether the user has permissions to log in. Permissions are stored
	 * in userdat.csv in cases parent folder
	 * 
	 * @created 15.03.2011
	 * @param user The user name.
	 * @param password The password.
	 * @return True, if permissions are correct.
	 */
	private boolean permitUser(String user, String password) {

		List<String> values = getUserDat().get(user);
		if (values != null) {
			String pass = values.get(1);
			if (pass != null && password.equals(pass)) return true;
		}
		return false; // trust no one per default
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
	 * Stream images from the KB into intermediate storage in webapp
	 * 
	 * @created 29.04.2011
	 */
	protected void streamImages() {

		List<Resource> kbimages = D3webConnector.getInstance().getKb().getResources();

		if (kbimages != null && kbimages.size() != 0) {
			for (Resource r : kbimages) {
				String rname = r.getPathName();

				if (rname.endsWith(".jpg") || rname.endsWith(".JPG")) {
					BufferedImage bui = ImageHandler.getResourceAsBUI(r);
					try {
						File file =
								new File(GlobalSettings.getInstance().getKbImgFolder()
										+ "/" + rname);
						ImageIO.write(bui, "jpg", file);
					}
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (rname.endsWith(".png") || rname.endsWith(".PNG")) {
					BufferedImage bui = ImageHandler.getResourceAsBUI(r);
					try {
						File file =
								new File(GlobalSettings.getInstance().getKbImgFolder()
										+ "/" + rname);
						ImageIO.write(bui, "png", file);
					}
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
}