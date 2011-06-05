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

import javax.mail.MessagingException;
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
import de.d3web.core.session.blackboard.Fact;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webRendererMapping;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.d3web.input.D3webXMLParser;
import de.d3web.proket.d3web.output.render.AbstractD3webRenderer;
import de.d3web.proket.d3web.output.render.DefaultRootD3webRenderer;
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
public class Mediastinitis extends D3webDialog {

	private static final long serialVersionUID = 4798072917307992413L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Mediastinitis() {
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
		String source = "Mediastinitis";
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
			return;
		}
		else if (action.equalsIgnoreCase("sendmail")) {
			try {
				sendMail(request, response, httpSession);
				response.setContentType("text/html");
				response.setCharacterEncoding("utf8");
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
			response.setContentType("text/html");
			response.setCharacterEncoding("utf8");
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

		response.setContentType("text/html");
		response.setCharacterEncoding("utf8");
		PrintWriter writer = response.getWriter();

		// get the root renderer --> call getRenderer with null
		DefaultRootD3webRenderer d3webr = (DefaultRootD3webRenderer) D3webRendererMapping.getInstance().getRendererObject(
				null);

		// new ContainerCollection needed each time to get an updated dialog
		ContainerCollection cc = new ContainerCollection();

		Session d3webSess = (Session) httpSession.getAttribute("d3webSession");
		AbstractD3webRenderer.storeSession(d3webSess);

		// set clinic to user name, since its the only clinic it can see cases of
		String user = (String) httpSession.getAttribute("user");
		if (user != null && user != "") {
			String definingObject = "Betreffende Klinik";
			Question to = D3webConnector.getInstance().getKb().getManager().searchQuestion(
					definingObject);
			Fact fact = d3webSess.getBlackboard().getValueFact(to);
			if (to != null && (fact == null || !fact.getValue().getValue().toString().equals(user))) {
				setValue(definingObject, user, d3webSess);
			}
		}


		cc = d3webr.renderRoot(cc, d3webSess, httpSession);

		writer.print(cc.html.toString()); // deliver the rendered output

		writer.close(); // and close
	}

}