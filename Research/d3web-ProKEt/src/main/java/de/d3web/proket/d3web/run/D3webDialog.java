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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import de.d3web.core.manage.KnowledgeBaseManagement;
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

	/* current d3web session */
	private Session d3webSession;

	/* d3web connector for storing certain relevant properties */
	private D3webConnector d3wcon;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public D3webDialog() {
		super();
	}

	/**
	 * Basic initialization and servlet method.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		d3wcon = D3webConnector.getInstance();

		// in case nothing other is provided, "show" is the default action of
		// the dialog
		String action = request.getParameter("action");
		if (action == null) {
			action = "show";
		}

		// Get the current httpSession
		HttpSession httpSession = request.getSession(true);
		// Sets any httpSession inactive after max 24h
		httpSession.setMaxInactiveInterval(24 * 3600);

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
		if (d3wcon.getKb() == null) {
			d3wcon.setKb(d3webParser.getKnowledgeBase());
			d3wcon.setKbName(d3webParser.getKnowledgeBaseName());
			d3wcon.setKbm(
					KnowledgeBaseManagement.createInstance(d3wcon.getKb()));
			d3wcon.setDialogStrat(d3webParser.getStrategy());
			d3wcon.setDialogType(d3webParser.getType());
			d3wcon.setDialogColumns(d3webParser.getDialogColumns());
			d3wcon.setQuestionnaireColumns(d3webParser.getQuestionnaireColumns());
			d3wcon.setCss(d3webParser.getCss());
			d3wcon.setHeader(d3webParser.getHeader());
			d3wcon.setUserprefix(d3webParser.getUserPrefix());
			d3wcon.setSingleSpecs(d3webParser.getSingleSpecs());
		}

		// get existing d3websession if existing
		d3webSession = d3wcon.getSession();

		/*
		 * otherwise, i.e. if session is null, OR reset parameter is activated,
		 * OR browser has been refreshed (TODO if possible) create a session
		 * according to the specified dialog strategy
		 */
		if (d3webSession == null
				|| (request.getParameter("reset") != null
						&& request.getParameter("reset").equals("true"))) {

			d3webSession = D3webUtils.createSession(d3wcon.getKb(), d3wcon.getDialogStrat());
			httpSession.setAttribute("d3webSessionID", d3webSession.getId());
			d3wcon.setSession(d3webSession);
			d3wcon.setQuestionCount(0);
			d3wcon.setQuestionnaireCount(0);
		}

		
		// switch action as defined by the servlet call
		if (action.equalsIgnoreCase("show")) {
			show(request, response);
			return;
		}
		else if (action.equalsIgnoreCase("addfact")) {
			addFact(request, response);
			return;
		}
		else if (action.equalsIgnoreCase("solutions")) {
			// TODO No actions yet
		}
	}

	/**
	 * Basic servlet method for displaying the servlet.
	 * 
	 * @created 28.01.2011
	 * @param request
	 * @param response
	 * @param d3webSession
	 * @throws IOException
	 */
	private void show(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		response.setContentType("text/html");
		response.setCharacterEncoding("utf8");
		PrintWriter writer = response.getWriter();

		// get the root renderer --> call getRenderer with null
		//ID3webRenderer d3webr =
				//D3webRenderer.getRenderer(null);

		// new ContainerCollection needed each time to get an updated dialog
		//ContainerCollection cc = new ContainerCollection();
		//cc = d3webr.renderRoot(cc);

		String html = "" +
				"<html><head></head>" +
				"<body>" +
				"<img>D3webPicShow?src=Test</img>" +
				"</body>" +
				"</html>";
		// writer.print(cc.html.toString()); // deliver the rendered output
		writer.print(html);
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
			HttpServletResponse response) {

		// get the ID of a potentially given single question answered
		String qid = request.getParameter("qid");

		// get the input-store
		String store = request.getParameter("store");

		// if there are values in the input-store (i.e., multiple questions
		// answered or changed
		if (!store.equals("") || !store.equals(" ")) {

			// replace empty-space surrogate
			store = store.replace("+", " ");

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
						setValue(valPair[0], valPair[1]);
					}
				}
			}
			// for TEXT
			if (txts != null && !txts.equals(" ") && !txts.equals("")) {
				for (String s : txts) {
					if (!s.equals(" ") && !s.equals("")) {
						valPair = s.split("###");
						setValue(valPair[0], valPair[1]);
					}
				}
			}
			// for DATE
			if (dates != null) {
				for (String s : dates) {
					if (!s.equals(" ") && !s.equals("")) {
						valPair = s.split("###");
						setValue(valPair[0], valPair[1]);
					}
				}
			}
		}

		// get single value storage
		String positions = request.getParameter("pos");

		// if not EMPTY, i.e., if one single value was set
		if (!positions.equals("EMPTY")) {

			// replace whitespace surrogates and set value
			qid = qid.replace("+", " ");
			positions = positions.replace("+", " ");
			setValue(qid, positions);
		}
	}

	/**
	 * Adds a value for a given question to the current knowledge base in the
	 * current problem solving session.
	 * 
	 * @created 28.01.2011
	 * @param termObID The ID of the TerminologyObject, the value is to be
	 *        added.
	 * @param valString The value, that is to be added for the TerminologyObject
	 *        with ID valID.
	 */
	private void setValue(String termObID, String valString) {

		// remove prefix, e.g. "q_" in "q_BMI"
		termObID = IDUtils.removeNamspace(termObID);

		Fact lastFact = null;

		Blackboard blackboard =
				D3webConnector.getInstance().getSession().getBlackboard();

		Question to =
				KnowledgeBaseManagement.createInstance(d3wcon.getKb()).findQuestion(termObID);

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
			Fact fact = FactFactory.createFact(d3webSession, to, value,
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
						value = d3wcon.getKbm().findValue(to, valString);
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
					// String is given in the format 20.03.2010
					String[] datesplit = valString.split("\\.");
					String parseableDate = datesplit[2] + "-" + datesplit[1] + "-" + datesplit[0]
							+ "-00-00-00";
					value = new DateValue(dateFormat.parse(parseableDate));
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

		// check, that questions of all non-init and non-indicated
		// questionnaires
		// are reset, i.e., no value
		for (QASet qaSet : d3wcon.getKb().getManager().getQContainers()) {
			// find the appropriate qaset in the knowledge base

			if (!d3wcon.getKb().getInitQuestions().contains(qaSet) &&
					!qaSet.getName().equals("Q000") &&
					(blackboard.getIndication(qaSet).getState() != State.INDICATED &&
							blackboard.getIndication(
									qaSet).getState() != State.INSTANT_INDICATED)) {

					resetNotIndicated(qaSet, blackboard);
				}
		}

		// ensure, that follow-up questions are reset if parent-question doesn't
		// indicate any more.
		checkChildren(to, blackboard);
	}

	/**
	 * Utility method for resetting follow-up questions due to setting their
	 * parent question to Unknown. Then, the childrens' value should also be
	 * removed again,
	 * 
	 * @created 31.01.2011
	 * @param parent The parent TerminologyObject
	 * @param blackboard The currently active blackboard
	 */
	private void checkChildren(TerminologyObject parent,
			Blackboard blackboard) {

		if (parent.getChildren() != null && parent.getChildren().length != 0) {
			for (TerminologyObject c : parent.getChildren()) {

				Question qto =
						KnowledgeBaseManagement.createInstance(d3wcon.getKb()).
								findQuestion(c.getId());

				if (!isIndicated(qto, blackboard)
						|| !isParentIndicated(qto, blackboard)) {

					// remove a previously set value
					Fact lastFact = blackboard.getValueFact(qto);
					if (lastFact != null) {
						blackboard.removeValueFact(lastFact);
					}
				}

				checkChildren(c, blackboard);
			}
		}
	}

	public void resetNotIndicated(TerminologyObject parent, Blackboard bb) {

		if (parent.getChildren() != null && parent.getChildren().length != 0) {
			Fact lastFact = null;

			Blackboard blackboard =
					D3webConnector.getInstance().getSession().getBlackboard();

			for (TerminologyObject to : parent.getChildren()) {

				Question qto =
						KnowledgeBaseManagement.createInstance(d3wcon.getKb()).
								findQuestion(to.getId());

				// remove a previously set value
				lastFact = blackboard.getValueFact(qto);
				if (lastFact != null) {
					blackboard.removeValueFact(lastFact);
				}

				resetNotIndicated(to, bb);
			}
		}
	}

	public boolean isIndicated(TerminologyObject to, Blackboard bb) {
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

	public boolean isParentIndicated(TerminologyObject to, Blackboard bb) {
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
	 * Checks, whether a TerminologyObject child is the child of another
	 * TerminologyObject parent. That is, whether child is nested hierarchically
	 * underneath parent.
	 * 
	 * @created 30.01.2011
	 * @param parent The parent TerminologyObject
	 * @param child The child to check
	 * @return true, if child is the child of parent
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

}