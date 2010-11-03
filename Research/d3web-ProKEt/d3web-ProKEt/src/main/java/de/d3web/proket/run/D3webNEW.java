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

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.proket.data.DialogStrategy;
import de.d3web.proket.data.DialogType;
import de.d3web.proket.input.d3web.D3webConnector;
import de.d3web.proket.input.d3web.D3webUtils;
import de.d3web.proket.input.xml.XMLd3webParser;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.output.render.D3webDialogRenderer;
import de.d3web.proket.utils.IDUtils;

/**
 * Servlet for creating and using d3web-binded dialogs.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 * 
 *         TODO: currently all attributes (kbname, dtype etc) are retrieved by
 *         the Servlet String. Refactor this so a custom XML is read!
 * @param <KnoweldgeBase>
 */
public class D3webNEW<KnoweldgeBase> extends HttpServlet {

	/* special parser for reading in the d3web-specification xml */
	private XMLd3webParser d3webParser;

	private Session d3webSession;

	private D3webConnector d3wcon;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public D3webNEW() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		d3wcon = D3webConnector.getInstance();

		String action = request.getParameter("action");
		if (action == null) {
			action = "show";
		}

		HttpSession httpSession = request.getSession(true);

		// Session is set inactive after maximum one day
		httpSession.setMaxInactiveInterval(24 * 3600);

		// get the d3web session. Is either stored in the d3webSession attribute
		// of the HTTP Session, or null at the first calling
		d3webSession =
				(Session) httpSession.getAttribute("d3webSession");

		// try to get the src parameter, if not default.xml is set
		String source = "default.xml";
		if (request.getParameter("src") != null) {
			source = request.getParameter("src");
		}
		if (!source.endsWith(".xml")) {
			source = source + ".xml";
		}
		d3webParser = new XMLd3webParser(source);

		d3wcon.setKb(d3webParser.getKnowledgeBase());
		String kbname = d3webParser.getKnowledgeBaseName();

		// get the dialog strategy if quick change via request is desired
		if (request.getParameter("dstrat") != null) {
			d3wcon.setDialogStrat(DialogStrategy.valueOf(request.getParameter("dstrat")));
		}
		else {
			d3wcon.setDialogStrat(d3webParser.getStrategy());
		}

		// get the dialog type if quick change via request is desired
		if (request.getParameter("dtype") != null) {
			d3wcon.setDialogType(DialogType.valueOf(request.getParameter("dtype")));
		}
		else {
			d3wcon.setDialogType(d3webParser.getType());
		}
		
		d3wcon.setCss(d3webParser.getCss());
		d3wcon.setHeader(d3webParser.getHeader());

		/* create a Session according to the type set */
		if (d3webSession == null
				|| (action.equals("show") && !kbname.equals(httpSession
						.getAttribute("kbName")))
				|| (!action.equals("show")
						&& request.getParameter("reset") != null
						&& request.getParameter("reset").equals("true"))) {

			d3webSession = D3webUtils.createSession(d3wcon.getKb(), d3wcon.getDialogStrat());
			httpSession.setAttribute("d3webSession", d3webSession);
			httpSession.setAttribute("kbName", kbname);
			d3wcon.setSession(d3webSession);
			d3wcon.setContainerCollection(new ContainerCollection());
		}

		// switch action
		if (action.equalsIgnoreCase("show")) {
			show(request, response, httpSession, d3webSession);
			return;
		}
		else if (action.equalsIgnoreCase("addfact")) {
			addFact(request, response, httpSession, d3webSession);
			show(request, response, httpSession, d3webSession);
			return;
		}
	}


	private void show(HttpServletRequest request, HttpServletResponse response,
			HttpSession httpSession, Session d3webSession) throws IOException {

		response.setContentType("text/html");
		response.setCharacterEncoding("utf8");
		PrintWriter writer = response.getWriter();

		if (request.getParameter("reset") != null &&
				request.getParameter("reset").equals("true")) {
			Session newS =
					D3webUtils.createSession(d3wcon.getKb(), d3wcon.getDialogStrat());
			httpSession.setAttribute("d3webSession", newS);
			d3wcon.setSession(newS);
		}

		// we need a new ContainerCollection each time to get an updated dialog
		D3webDialogRenderer d3webr =
				D3webDialogRenderer.getInstance(d3wcon.getSession(),
						new ContainerCollection());

		String dialog = d3webr.renderD3web();

		writer.print(dialog); // deliver the rendered output
		writer.close();
	}

	private void addFact(HttpServletRequest request,
			HttpServletResponse response, HttpSession httpSession,
			Session d3webSession) {
		Blackboard blackboard = d3webSession.getBlackboard();
		String qid = request.getParameter("qid");

		// remove prefix
		qid = IDUtils.removeNamspace(qid);
		TerminologyObject to =
				D3webUtils.getTerminologyObjectByID(d3webSession, qid);
		if (to == null) {
			return;
		}

		// create value
		Value value = null;
		String positions = request.getParameter("pos");

		// switch to the correct value
		if (to instanceof QuestionChoice) {
			List<Choice> choices = ((QuestionChoice) to).getAllAlternatives();
			if (to instanceof QuestionOC) {
				// valueString is the ID of the selected item
				try {
					value = new ChoiceValue(choices.get(Integer
							.parseInt(positions)));
				}
				catch (NumberFormatException nfe) {
					// value still null, will not be set
				}
			}
			else if (to instanceof QuestionMC) {
				// valueString is a comma separated list of the IDs of the
				// selected items
				List<Choice> values = new ArrayList<Choice>();
				String[] parts = positions.split(",");
				for (String part : parts) {
					values.add(new Choice(part));
				}
				value = MultipleChoiceValue.fromChoices(values);
			}
		}
		else if (to instanceof QuestionText) {
			value = new TextValue(positions);
		}
		else if (to instanceof QuestionNum) {
			try {
				value = new NumValue(Double.parseDouble(positions));
			}
			catch (NumberFormatException ex) {
				// value still null, will not be set
			}
		}

		// QuestionDate
		else if (to instanceof QuestionDate) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			try {
				// String is given in the format 20.03.2010
				String[] datesplit = positions.split("\\.");
				String parseableDate = datesplit[2] + "-" + datesplit[1] + "-" + datesplit[0]
						+ "-00-00-00";
				value = new DateValue(dateFormat.parse(parseableDate));
			}
			catch (ParseException e) {
				// value still null, will not be set
			}
		}

		if (value != null) {
			Fact fact = FactFactory.createFact(to, value,
					PSMethodUserSelected.getInstance(),
					PSMethodUserSelected.getInstance());
			blackboard.addValueFact(fact);
		}
	}
}