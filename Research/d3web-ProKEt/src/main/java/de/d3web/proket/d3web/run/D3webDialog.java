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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import au.com.bytecode.opencsv.CSVReader;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.InterviewObject;
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
import de.d3web.core.session.values.Unknown;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.d3web.input.D3webXMLParser;
import de.d3web.proket.d3web.output.render.D3webRenderer;
import de.d3web.proket.d3web.output.render.ID3webRenderer;
import de.d3web.proket.d3web.utils.PersistenceD3webUtils;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.GlobalSettings;
import de.d3web.proket.utils.IDUtils;

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

	/* special parser for reading in the d3web-specification xml */
	private D3webXMLParser d3webParser;

	/* d3web connector for storing certain relevant properties */
	private D3webConnector d3wcon;

	private String sourceSave = "";

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

		String folderPath = request.getSession().getServletContext().getRealPath("/cases");
		GlobalSettings.getInstance().setCaseFolder(folderPath);

		d3wcon = D3webConnector.getInstance();

		// in case nothing other is provided, "show" is the default action
		String action = request.getParameter("action");
		if (action == null) {
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

		// only invoke parser, if XML hasn't been parsed before
		// if it has, a knowledge base already exists
		if (d3wcon.getKb() == null ||
				!source.equals(sourceSave)) {
			d3wcon.setKb(d3webParser.getKnowledgeBase());
			d3wcon.setKbName(d3webParser.getKnowledgeBaseName());
			d3wcon.setDialogStrat(d3webParser.getStrategy());
			d3wcon.setDialogType(d3webParser.getType());
			d3wcon.setDialogColumns(d3webParser.getDialogColumns());
			d3wcon.setQuestionnaireColumns(d3webParser.getQuestionnaireColumns());
			d3wcon.setCss(d3webParser.getCss());
			d3wcon.setHeader(d3webParser.getHeader());
			d3wcon.setUserprefix(d3webParser.getUserPrefix());
			d3wcon.setSingleSpecs(d3webParser.getSingleSpecs());
			sourceSave = source;
		}

		// Get the current httpSession or a new one
		HttpSession httpSession = request.getSession(true);

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

		d3wcon.setQuestionCount(0);
		d3wcon.setQuestionnaireCount(0);

		// switch action as defined by the servlet call
		if (action.equalsIgnoreCase("show")) {
			show(request, response, httpSession);
			return;
		}
		else if (action.equalsIgnoreCase("addfact")) {
			addFact(request, response, httpSession);
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
			return;
		}
		else if (action.equalsIgnoreCase("resetNewUser")) {

			Session d3webSession = D3webUtils.createSession(d3wcon.getKb(), d3wcon.getDialogStrat());
			httpSession.setAttribute("d3webSession", d3webSession);

			return;
		}
		else if (action.equalsIgnoreCase("checklogin")) {

			response.setContentType("text/html");
			response.setCharacterEncoding("utf8");
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

		response.setContentType("text/html");
		response.setCharacterEncoding("utf8");
		PrintWriter writer = response.getWriter();

		// get the root renderer --> call getRenderer with null
		ID3webRenderer d3webr =
				D3webRenderer.getRenderer(null);

		// new ContainerCollection needed each time to get an updated dialog
		ContainerCollection cc = new ContainerCollection();

		Session d3webSess = (Session) httpSession.getAttribute("d3webSession");
		D3webRenderer.storeSession(d3webSess);
		cc = d3webr.renderRoot(cc, d3webSess, httpSession);

		/*
		 * String html = "" + "<html><head></head>" + "<body>" +
		 * "<img>D3webPicShow?src=Test</img>" + "</body>" + "</html>";
		 */

		writer.print(cc.html.toString()); // deliver the rendered output
		// writer.print(html);
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
	private void addFact(HttpServletRequest request,
			HttpServletResponse response, HttpSession httpSession)
			throws IOException {

		response.setContentType("text/html");
		response.setCharacterEncoding("utf8");
		PrintWriter writer = response.getWriter();

		Session sess = (Session) httpSession.getAttribute("d3webSession");

		// get the ID of a potentially given single question answered
		String qid = request.getParameter("qid");

		// ONLY GO ON SAVING if required att(s) is/are already set
		boolean goOn = checkReqVal("Betreffende Klinik", sess, qid);

		if (!goOn) {
			writer.append("noReqs");
			return;
		}

		// get the input-store
		String store = request.getParameter("store");

		// if there are values in the input-store (i.e., multiple questions
		// answered or changed
		if (!store.equals("") || !store.equals(" ")) {

			// replace empty-space surrogate
			store = store.replace("q_", "").replace("_", " ").replace("+", " ");

			// split store into the 3 categories num/text/date
			String[] cats = store.split("&&&&");
			String[] nums = null;
			String[] txts = null;
			String[] dates = null;

			// split category Strings into id-value pairs
			if (cats[0] != null) {
				nums = cats[0].split(";");
			}
			if (cats[1] != null) {
				txts = cats[1].split(";");
			}
			if (cats[2] != null) {
				dates = cats[2].split(";");
			}

			// split id-value pairs and set values correspondingly
			// for NUM
			String[] valPair = null;
			if (nums != null) {
				for (String s : nums) {
					if (!s.equals(" ") && !s.equals("")) {
						valPair = s.split("###");
						setValue(valPair[0], valPair[1], sess);
					}
				}
			}
			// for TEXT
			if (txts != null && !txts.equals(" ") && !txts.equals("")) {
				for (String s : txts) {
					if (!s.equals(" ") && !s.equals("")) {
						valPair = s.split("###");
						setValue(valPair[0], valPair[1], sess);
					}
				}
			}
			// for DATE
			if (dates != null) {
				for (String s : dates) {
					if (!s.equals(" ") && !s.equals("")) {
						valPair = s.split("###");
						setValue(valPair[0], valPair[1], sess);
					}
				}
			}
		}

		// get single value storage
		String positions = request.getParameter("pos");

		// if not EMPTY, i.e., if one single value was set
		if (!positions.equals("EMPTY")) {

			// replace whitespace surrogates and set value
			qid = qid.replace("q_", "").replace("_", " ");
			qid = "q_" + qid;
			positions = positions.replace("_", " ");

			setValue(qid, positions, sess);

			// AUTOSAVE
			String folderPath = request.getSession().getServletContext().getRealPath("/cases");
			GlobalSettings.getInstance().setCaseFolder(folderPath);

			// AUTOSAVE
			PersistenceD3webUtils.saveCaseTimestampOneQuestionAndInput(
						folderPath,
						"Betreffende Klinik",
						"autosave",
						(Session) httpSession.getAttribute("d3webSession"));

		}
	}

	/**
	 * Saving a case.
	 * 
	 * @created 08.03.2011
	 * @param request ServletRequest
	 * @param response ServletResponse
	 */
	private void saveCase(HttpServletRequest request,
			HttpServletResponse response, HttpSession httpSession) {

		response.setContentType("text/html");
		response.setCharacterEncoding("utf8");
		PrintWriter writer = null;

		// retrieves path to /cases folder on the server
		String folderPath = request.getSession().getServletContext().getRealPath("/cases");
		GlobalSettings.getInstance().setCaseFolder(folderPath);
		// PersistenceD3webUtils.saveCaseTimestampOneQuestionVal(folderPath,
		// "Betreffende Klinik");

		try {
			writer = response.getWriter();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String userFilename = request.getParameter("userfn");
		String lastLoaded = (String) httpSession.getAttribute("lastLoaded");

		if (lastLoaded != null && lastLoaded != "") {
			if (lastLoaded.equals(userFilename)) {
				PersistenceD3webUtils.saveCaseTimestampOneQuestionAndInput(
						folderPath,
						"Betreffende Klinik",
						userFilename,
						(Session) httpSession.getAttribute("d3webSession"));
			}
			else {
				if (PersistenceD3webUtils.existsCase(
						folderPath,
						userFilename,
						"Betreffende Klinik",
						(Session) httpSession.getAttribute("d3webSession"))) {
					writer.append("exists");
				}
			}
		}
	}

	/**
	 * Loading a case.
	 * 
	 * @created 09.03.2011
	 * @param request ServletRequest
	 * @param response ServletResponse
	 */
	private void loadCase(HttpServletRequest request,
			HttpServletResponse response, HttpSession httpSession) {

		// get the filename from the corresponding request parameter "fn"
		String filename = request.getParameter("fn");

		String user = httpSession.getAttribute("user").toString();
		// load the file = path + filename
		// PersistenceD3webUtils.loadCaseFromUserFilename(folderPath + "/" +
		// filename);
		Session session = PersistenceD3webUtils.loadCaseFromUserFilename(filename, user);
		httpSession.setAttribute("d3webSession", session);

		httpSession.setAttribute("lastLoaded", filename);
	}

	private void login(HttpServletRequest req,
			HttpServletResponse res, HttpSession httpSession)
			throws IOException {

		res.setContentType("text/html");

		// fetch the information sent via the request string from login
		String u = req.getParameter("u");
		String p = req.getParameter("p");

		// TODO check if needed here
		String folderPath = req.getSession().getServletContext().getRealPath("/cases");
		GlobalSettings.getInstance().setCaseFolder(folderPath);

		// get the response writer for communicating back via Ajax
		PrintWriter writer = res.getWriter();

		httpSession.setMaxInactiveInterval(30);

		// if no valid login
		if (!permitUser(u, p)) {

			// causes JS to display error message
			writer.append("nosuccess");
			return;
		}

		// set user attribute for the HttpSession
		httpSession.setAttribute("user", u);
		

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
	private void setValue(String termObID, String valString, Session sess) {

		// remove prefix, e.g. "q_" in "q_BMI"
		termObID = IDUtils.removeNamspace(termObID);

		Fact lastFact = null;

		Blackboard blackboard =
				sess.getBlackboard();

		Question to =
				(Question) KnowledgeBaseUtils.findTerminologyObjectByName(
						termObID, d3wcon.getKb());

		// if TerminologyObject not found in the current KB return & do nothing
		if (to == null) {
			return;
		}

		// init Value object...
		Value value = null;

		// check if unknown option was chosen
		if (valString.contains("unknown")) {

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
						valString = valString.replace("q_", "");
						value = KnowledgeBaseUtils.findValue(to, valString);
					}
					catch (NumberFormatException nfe) {
						// value still null, will not be set
					}
				}
				else if (to instanceof QuestionMC) {
					// valueString is a comma separated list of the IDs of the
					// selected items
					List<Choice> values = new ArrayList<Choice>();
					String[] parts = valString.split(",");
					for (String part : parts) {
						values.add(new Choice(part));
					}
					value = MultipleChoiceValue.fromChoices(values);
				}
			}
			// TEXT questions
			else if (to instanceof QuestionText) {
				value = new TextValue(valString);
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

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
				try {
					// String is given in the format 03/2010
					String[] datesplit = valString.split("\\/");
					if (datesplit.length == 2) {
						String parseableDate = datesplit[1] + "-" + datesplit[0] + "-"
								+ "01-00-00-00";
						value = new DateValue(dateFormat.parse(parseableDate));
					}
				}
				catch (ParseException e) {
					// value still null, will not be set
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

				// add new value as UserEnteredFact
				Fact fact = FactFactory.createUserEnteredFact(to, value);
				blackboard.addValueFact(fact);
			}
		}

		// TODO: CHECK whether we need both the resetNotIndicated and
		// checkChildren methods

		// check, that questions of all non-init and non-indicated
		// questionnaires are reset, i.e., no value
		for (QASet qaSet : d3wcon.getKb().getManager().getQContainers()) {
			// find the appropriate qaset in the knowledge base

			if (!d3wcon.getKb().getInitQuestions().contains(qaSet) &&
					!qaSet.getName().equals("Q000") &&
					(blackboard.getIndication(qaSet).getState() != State.INDICATED &&
							blackboard.getIndication(
									qaSet).getState() != State.INSTANT_INDICATED)) {

				resetNotIndicatedTOs(qaSet, blackboard, sess);
			}
		}

		// ensure, that follow-up questions are reset if parent-question doesn't
		// indicate any more.
		checkChildrenAndRemoveVals(to, blackboard);
	}

	/**
	 * Utility method for resetting follow-up questions due to setting their
	 * parent question to Unknown. Then, the childrens' value should also be
	 * removed again, recursively also for childrens' children and so on.
	 * 
	 * @created 31.01.2011
	 * @param parent The parent TerminologyObject
	 * @param blackboard The currently active blackboard
	 */
	private void checkChildrenAndRemoveVals(TerminologyObject parent,
			Blackboard blackboard) {

		if (parent.getChildren() != null && parent.getChildren().length != 0) {
			for (TerminologyObject c : parent.getChildren()) {

				Question qto =
						(Question) KnowledgeBaseUtils.findTerminologyObjectByName(
								c.getName(), d3wcon.getKb());

				if (!isIndicated(qto, blackboard)
						|| !isParentIndicated(qto, blackboard)) {

					// remove a previously set value
					Fact lastFact = blackboard.getValueFact(qto);
					if (lastFact != null) {
						blackboard.removeValueFact(lastFact);
					}
				}

				checkChildrenAndRemoveVals(c, blackboard);
			}
		}
	}

	/**
	 * Utility method for resetting
	 * 
	 * @created 09.03.2011
	 * @param parent
	 * @param bb
	 */
	private void resetNotIndicatedTOs(TerminologyObject parent, Blackboard bb,
			Session sess) {

		if (parent.getChildren() != null && parent.getChildren().length != 0) {
			Fact lastFact = null;

			Blackboard blackboard =
					sess.getBlackboard();

			for (TerminologyObject to : parent.getChildren()) {

				Question qto =
						(Question) KnowledgeBaseUtils.findTerminologyObjectByName(
								to.getName(), d3wcon.getKb());

				// remove a previously set value
				lastFact = blackboard.getValueFact(qto);
				if (lastFact != null) {
					blackboard.removeValueFact(lastFact);
				}

				resetNotIndicatedTOs(to, bb, sess);
			}
		}
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
	private boolean isIndicated(TerminologyObject to, Blackboard bb) {
		for (QASet qaSet : bb.getSession().getKnowledgeBase().getManager().getQASets()) {
			// find the appropriate qaset in the knowledge base
			if (qaSet.getName().equals(to.getName()) &&
					// and check its indication state
					(bb.getIndication((InterviewObject) to).getState() == State.INDICATED
					|| bb.getIndication((InterviewObject) to).getState() == State.INSTANT_INDICATED)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Utility method for checking whether the parent object of a given
	 * terminology object is (instant) indicated.
	 * 
	 * @created 09.03.2011
	 * @param to The terminology object, the parent of which is to be checked.
	 * @param bb
	 * @return True, if there exists a parent object of the given terminology
	 *         object that is indicated.
	 */
	private boolean isParentIndicated(TerminologyObject to, Blackboard bb) {
		for (QASet qaSet : bb.getSession().getKnowledgeBase().getManager().getQASets()) {

			// get questionnaires only
			if (qaSet instanceof QContainer) {
				QContainer qcon = (QContainer) qaSet;

				// and check its indication state
				if (bb.getSession().getKnowledgeBase().getInitQuestions().contains(qcon)
						|| bb.getIndication(qcon).getState() == State.INDICATED
						|| bb.getIndication(qcon).getState() == State.INSTANT_INDICATED) {

					// if questionnaire indicated, check whether to is its child
					if (hasChild(qcon, to)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Utility method that checks, whether a TerminologyObject child is the
	 * child of another TerminologyObject parent. That is, whether child is
	 * nested hierarchically underneath parent.
	 * 
	 * @created 30.01.2011
	 * @param parent The parent TerminologyObject
	 * @param child The child to check
	 * @return True, if child is the child of parent
	 */
	private boolean hasChild(TerminologyObject parent, TerminologyObject child) {

		if (parent.getChildren() != null && parent.getChildren().length != 0) {
			for (TerminologyObject c : parent.getChildren()) {
				if (c.equals(child)) {
					return true;
				}
			}
			for (TerminologyObject c : parent.getChildren()) {
				if (c.getChildren().length != 0) {
					return hasChild(c, child);
				}
			}
		}
		return false;
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

		// get parent folder for storing cases
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
					if (nextLine[0].equals(user) && nextLine[1].equals(password)) {
						return true;
					}
				}
			}

		}
		catch (FileNotFoundException fnfe) {
			// TODO Auto-generated catch block
			fnfe.printStackTrace();
		}
		catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}

		return false; // trust no one per default
	}

	private boolean checkReqVal(String requiredVal, Session sess, String valToSet) {
		Blackboard blackboard = sess.getBlackboard();

		valToSet = valToSet.replace("q_", "").replace("_", " ");
		Question to =
				(Question) KnowledgeBaseUtils.findTerminologyObjectByName(
						requiredVal, d3wcon.getKb());

		Fact lastFact = blackboard.getValueFact(to);
		if (requiredVal.equals(valToSet) ||
				(lastFact != null && lastFact.getValue().toString() != "")) {
			return true;
		}
		return false;
	}
}