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
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.session.Session;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webRendererMapping;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.d3web.input.D3webXMLParser;
import de.d3web.proket.d3web.output.render.AbstractD3webRenderer;
import de.d3web.proket.d3web.output.render.HerniaDefaultRootD3webRenderer;
import de.d3web.proket.d3web.utils.Base64CoDec;
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
 * @date 14.01.2011; Last Update: Mai 2011
 * 
 */
public class Hernia extends HttpServlet {

	/* special parser for reading in the d3web-specification xml */
	private D3webXMLParser d3webParser;

	/* d3web connector for storing certain relevant properties */
	private D3webConnector d3wcon;

	private String sourceSave = "";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Hernia() {
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

		/*
		 * D3web RELATED STUFF FIRST needs to be setup first to have kb etc
		 * available
		 */

		String source = "Hernia_Standard";
		if (!source.endsWith(".xml")) {
			source = source + ".xml";
		}
		d3wcon = D3webConnector.getInstance();

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
			if (!d3webParser.getLanguage().equals("")) {
				d3wcon.setLanguage(d3webParser.getLanguage());
			}
			sourceSave = source;
		}

		d3wcon.setQuestionCount(0);
		d3wcon.setQuestionnaireCount(0);

		/*
		 * HERNIA specific Login is done via external part-system which sends
		 * respective URL parameters for further processing
		 */

		// needed for assembling filename
		String login = request.getParameter("l");
		String nname = request.getParameter("n");
		String inst = request.getParameter("i");

		// needed for date check at login
		String token = request.getParameter("t");
		String action = null;

		// System.out.println(login + " " + nname + " " + inst + " " + token);

		if (login != null && nname != null && inst != null && token != null) {

			// decode Strings
			String decodedLogin = Base64CoDec.getPlainString(login);
			String decodedNname = Base64CoDec.getPlainString(nname);
			String decodedInstitute = Base64CoDec.getPlainString(inst);
			String decodedToken = Base64CoDec.getPlainString(token);

			// decode date from token
			Date d = Base64CoDec.getDate(token);
			Date now = new Date(); // current date

			// if login was less than 20 sec before, allow login (i.e., just go
			// on in code) otherwise redirect to login page

			// if (D3webUtils.getDifference(d, now) < -20) {
			// response.sendRedirect(
			// response.encodeRedirectURL(
			// "http://casetrain-test.informatik.uni-wuerzburg.de/Eura-HS/"));
			// }

			// save decoded info in httpSession
			HttpSession httpSession = request.getSession();
			httpSession.setAttribute("login", decodedLogin);
			httpSession.setAttribute("nname", decodedNname);
			httpSession.setAttribute("institute", decodedInstitute);

			// TODO: check whether login etc are already set, if not, start new
			// session, add user to parameters for file loading etc
			String sesslogin = (String) request.getSession().getAttribute("login");
			String sessnname = (String) request.getSession().getAttribute("nname");
			System.out.println(sesslogin + " --- " + sessnname);
			if (sesslogin != null && sessnname != null) {
				if (!sesslogin.equals(decodedLogin) && !sessnname.equals(decodedNname)) {

					resetNewUser(request.getSession(), decodedLogin, decodedNname);
					System.out.println("reset new user");
				}
			}
			else {
				resetNewUser(request.getSession(), decodedLogin, decodedNname);
				System.out.println("reset new user II");
			}
		}

		// set both persistence (case saving) and image (images streamed from
		// kb) folder paths
		String fca = GlobalSettings.getInstance().getCaseFolder();
		String fim = GlobalSettings.getInstance().getKbImgFolder();
		if ((fca.equals(null) || fca.equals("")) &&
				(fim.equals(null) || fim.equals(""))) {

			String servletBasePath =
					request.getSession().getServletContext().getRealPath("/");
			GlobalSettings.getInstance().setCaseFolder(servletBasePath + "cases");
			GlobalSettings.getInstance().setKbImgFolder(servletBasePath + "kbimg");
		}

		// in case nothing other is provided, "show" is the default action
		action = request.getParameter("action");
		if (action == null) {
			action = "show";
		}

		// Get the current httpSession or a new one
		HttpSession httpSession = request.getSession(true);

		if (httpSession.getAttribute("imgStreamed") == null) {
			System.out.println("stream");
			D3webUtils.streamImages();
			httpSession.setAttribute("imgStreamed", true);
		}

		/*
		 * otherwise, i.e. if session is null create a session according to the
		 * specified dialog strategy SHOULD NEVER BE THE CASE HERE AS IS CREATED
		 * user dependent
		 */
		if (httpSession.getAttribute("d3webSession") == null) {
			// create d3web session and store in http session
			System.out.println("new session");
			Session d3webSession = D3webUtils.createSession(d3wcon.getKb(),
					d3wcon.getDialogStrat());
			httpSession.setAttribute("d3webSession", d3webSession);
		}

		// switch action as defined by the servlet call
		if (action.equalsIgnoreCase("show")) {
			show(request, response, httpSession);
			return;
		}
		else if (action.equalsIgnoreCase("addfact")) {
			addFact(request, response, httpSession);
			return;
		}
		else if (action.equalsIgnoreCase("addmcfact")) {
			addMCFact(request, response, httpSession);
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
			httpSession.setAttribute("lastLoaded", "");
			return;
		}
		else if (action.equalsIgnoreCase("login")) {
			login(request, response, httpSession);
			return;
		}
		else if (action.equalsIgnoreCase("checkrange")) {
			checkRange(request, response);
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

		response.setContentType("text/html");
		response.setCharacterEncoding("utf8");
		PrintWriter writer = response.getWriter();

		// get the root renderer --> call getRenderer with null
		HerniaDefaultRootD3webRenderer d3webr = (HerniaDefaultRootD3webRenderer) D3webRendererMapping.getInstance().getRendererObject(
				null);

		// new ContainerCollection needed each time to get an updated dialog
		ContainerCollection cc = new ContainerCollection();

		Session d3webSess = (Session) httpSession.getAttribute("d3webSession");
		AbstractD3webRenderer.storeSession(d3webSess);

		// TODO remove httpSession from method
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
	private void addFact(HttpServletRequest request,
			HttpServletResponse response, HttpSession httpSession)
			throws IOException {

		response.setContentType("text/html");
		response.setCharacterEncoding("utf8");
		PrintWriter writer = response.getWriter();

		Session sess = (Session) httpSession.getAttribute("d3webSession");

		// get the ID of a potentially given single question answered
		String qid = request.getParameter("qid");
		// get the input-store
		String store = request.getParameter("store");

		/*
		 * Check, whether a required value (for saving) is specified. If yes,
		 * check whether this value has already been set in the KB or is about
		 * to be set in the current call --> go on normally. Otherwise, return a
		 * marker "<required value>" so the user is informed by AJAX to provide
		 * this marked value.
		 */

		String reqVal = D3webConnector.getInstance().getD3webParser().getRequired();
		if ((!reqVal.equals("")) &&
				(!D3webUtils.checkReqVal(reqVal, sess, qid, store))) {

			writer.append(reqVal);
			return;
		}

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
						D3webUtils.setValue(valPair[0], valPair[1], sess);
					}
				}
			}
			// for TEXT
			if (txts != null && !txts.equals(" ") && !txts.equals("")) {
				for (String s : txts) {
					if (!s.equals(" ") && !s.equals("")) {
						valPair = s.split("###");
						D3webUtils.setValue(valPair[0], valPair[1], sess);
					}
				}
			}
			// for DATE
			if (dates != null) {
				for (String s : dates) {
					if (!s.equals(" ") && !s.equals("")) {
						valPair = s.split("###");
						D3webUtils.setValue(valPair[0], valPair[1], sess);
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

			D3webUtils.setValue(qid, positions, sess);

			// AUTOSAVE
			String folderPath = GlobalSettings.getInstance().getCaseFolder();
			Session d3webSession = (Session) httpSession.getAttribute("d3webSession");
			long before = System.currentTimeMillis();
			new SaveThread(folderPath, d3webSession).start();
			System.out.println(System.currentTimeMillis() - before);

		}
	}

	/**
	 * Adding MC facts
	 * 
	 * @created 29.04.2011
	 * @param request
	 * @param response
	 * @param httpSession
	 * @throws IOException
	 */
	private void addMCFact(HttpServletRequest request,
			HttpServletResponse response, HttpSession httpSession)
			throws IOException {

		response.setContentType("text/html");
		response.setCharacterEncoding("utf8");
		PrintWriter writer = response.getWriter();

		Session sess = (Session) httpSession.getAttribute("d3webSession");

		// get the ID of a potentially given single question answered
		String qid = request.getParameter("qid");
		qid = qid.replace("q_", "").replace("_", " ");

		String mcVals = request.getParameter("mcs");
		mcVals = mcVals.replace("_", " ");

		/*
		 * Check, whether a required value (for saving) is specified. If yes,
		 * check whether this value has already been set in the KB or is about
		 * to be set in the current call --> go on normally. Otherwise, return a
		 * marker "<required value>" so the user is informed by AJAX to provide
		 * this marked value.
		 */

		String reqVal = D3webConnector.getInstance().getD3webParser().getRequired();
		if ((!reqVal.equals("") || !reqVal.equals("none")) &&
				(!D3webUtils.checkReqVal(reqVal, sess, qid, ""))) {

			writer.append(reqVal);
			return;
		}

		D3webUtils.setValue(qid, mcVals, sess);

		// AUTOSAVE
		String folderPath = GlobalSettings.getInstance().getCaseFolder();
		Session d3webSession = (Session) httpSession.getAttribute("d3webSession");
		long before = System.currentTimeMillis();
		new SaveThread(folderPath, d3webSession).start();
		System.out.println(System.currentTimeMillis() - before);
	}

	private class SaveThread extends Thread {

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

	// TODO
	/**
	 * Saving a case.
	 * 
	 * @created 08.03.2011
	 * @param request ServletRequest
	 * @param response ServletResponse
	 */
	private void saveCase(HttpServletRequest request,
			HttpServletResponse response, HttpSession httpSession)
			throws IOException {

		response.setContentType("text/html");
		response.setCharacterEncoding("utf8");
		PrintWriter writer = null;
		writer = response.getWriter();

		HttpSession hsess = request.getSession();
		String login = (String) hsess.getAttribute("login");
		String nname = (String) hsess.getAttribute("nname");
		String inst = (String) hsess.getAttribute("institute");
		String idString = login + nname + inst;
		// System.out.println(login + nname + inst);

		// retrieves path to /cases folder on the server
		String folderPath = GlobalSettings.getInstance().getCaseFolder();
		String userFilename = idString + "&&&" + request.getParameter("userfn");
		System.out.println(userFilename);
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
					PersistenceD3webUtils.saveCaseWithLoginNnameInstituteTimestamp(
							folderPath,
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
				PersistenceD3webUtils.saveCaseWithLoginNnameInstituteTimestamp(
						folderPath,
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
				PersistenceD3webUtils.saveCaseWithLoginNnameInstituteTimestamp(
						folderPath,
						userFilename,
						(Session) httpSession.getAttribute("d3webSession"));
			}
		}
	}

	// TODO
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

	// TODO
	/**
	 * Handle login of new user
	 * 
	 * @created 29.04.2011
	 * @param req
	 * @param res
	 * @param httpSession
	 * @throws IOException
	 */
	private void login(HttpServletRequest req,
			HttpServletResponse res, HttpSession httpSession)
			throws IOException {

		res.setContentType("text/html");

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
		// if (!permitUser(u, p)) {

		// causes JS to display error message
		// writer.append("nosuccess");
		// return;
		// }

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
	 * Checks, whether for a numerical question a range is given and gives back
	 * this range (if specified) as the writer backstring so it can be further
	 * processed (checked) by JS.
	 * 
	 * @created 05.05.2011
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void checkRange(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("utf8");
		PrintWriter writer = response.getWriter();

		String qidsString = request.getParameter("qids");
		qidsString = qidsString.replace("q_", "");

		String[] qids = qidsString.split(";");
		String qidBackstring = "";

		for (String qid : qids) {
			String[] idVal = qid.split("%");

			// TerminologyManager man = new
			// TerminologyManager(d3wcon.getKb());
			Question to = D3webConnector.getInstance().getKb().getManager().searchQuestion(
					idVal[0]);

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
	 * Creates a new d3websession for a newly logged in user. Stores the
	 * respective values (session, loginname, famname and lastloaded session
	 * (nothing when new) into the httpsession for storage during user session.
	 * 
	 * @created 05.05.2011
	 * @param httpSession the current httpSession
	 * @param login the login name of the user
	 * @param nname the familiy name of the user
	 */
	private void resetNewUser(HttpSession httpSession, String login, String nname) {
		Session d3webSession = D3webUtils.createSession(d3wcon.getKb(), d3wcon.getDialogStrat());
		httpSession.setAttribute("d3webSession", d3webSession);
		httpSession.setAttribute("lastLoaded", "");
		httpSession.setAttribute("login", login);
		httpSession.setAttribute("nname", nname);
	}
}