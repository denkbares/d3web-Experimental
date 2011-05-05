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
package de.d3web.proket.d3web.output.render;

import javax.servlet.http.HttpSession;

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionZC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webRendererMapping;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.d3web.utils.PersistenceD3webUtils;
import de.d3web.proket.data.DialogType;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;

/**
 * Basic Renderer Class for d3web-based dialogs. Defines the basic rendering of
 * d3web dialogs and methods, required by all rendering sub-classes.
 *
 * TODO CHECK: 1) renderRoot: other dialog types or maybe write specific
 * renderers for each one particularly? Maybe better... 2) renderRoot: basic
 * properties such as header, title, HTML header... 3) check global JS 5)
 * IMPORTANT think about how to include mechanism to get specific renderes for
 * specific "dialogs", e.g by defining "hierarchic" in the XML and having
 * HierarchicQuestionnaireRenderer etc used automatically (or if not existing,
 * just return to base renderer.)
 *
 * TODO CHECK: what happens for more deeply nested question/f-u question
 * hierarchies? Also check an exit-condition for endless recursion!
 *
 * TODO LATER: 1) renderRoot: navigation 4) refactor D3webConnector to
 * class-variable?! 5) makeTables: add varying colspans from the XML
 * specification into this method one day 2) Handle cycles!!! 3) handle MC
 * Questions
 *
 * @author Martina Freiberg
 * @created 13.01.2011
 */
public class D3webRenderer implements ID3webRenderer {

	protected static Session d3webSession;
	private int countQcon = 1;
	private int countQ = 1;

	public static void storeSession(Session session) {
		d3webSession = session;
	}

	/**
	 * Retrieves the appropriate renderer class according to what base object is
	 * given from the d3web knowledge base. EXCLUDES answers, as those need a
	 * specific handling.
	 *
	 * @created 14.01.2011
	 * @param to the TerminologyObject that needs to retrieve the renderer.
	 * @return the suiting renderer class
	 */
	public static ID3webRenderer getRenderer(TerminologyObject to) {

		ID3webRenderer renderer =
				(ID3webRenderer) D3webRendererMapping.getInstance().getRendererObject(to);

		return renderer;
	}

	/**
	 * Retrieves the appropriate renderer class for answers,according to what
	 * base object (question type) is given.
	 *
	 * @created 15.01.2011
	 * @param to the TerminologyObject that needs to retrieve the answer
	 *        renderer.
	 * @return the suiting renderer class.
	 */
	public static ID3webRenderer getAnswerRenderer(TerminologyObject to) {

		ID3webRenderer renderer =
				(ID3webRenderer)
					D3webRendererMapping.getInstance().getAnswerRendererObject(to);

		return renderer;
	}

	/**
	 * Retrieves the renderer for the Unknown object (unknown option for
	 * dialogs).
	 *
	 * @created 23.01.2011
	 * @return the suiting renderer class.
	 */
	public static ID3webRenderer getUnknownRenderer() {

		ID3webRenderer renderer =
				(ID3webRenderer)
					D3webRendererMapping.getInstance().getUnknownRenderer();

		return renderer;
	}

	/**
	 * Basic rendering of the root, i.e., the framing stuff of a dialog, like
	 * basic structure, styles etc. Initiates the rendering of child-objects.
	 */
	@Override
	public ContainerCollection renderRoot(ContainerCollection cc,
			Session d3webSession, HttpSession http) {

		D3webRenderer.d3webSession = d3webSession;

		D3webConnector d3wcon = D3webConnector.getInstance();

		// System.out.println(d3wcon.getSingleSpecs().toString());
		TerminologyObject root = d3wcon.getKb().getRootQASet();

		// get the d3web base template according to dialog type
		StringTemplate st = null;
		if (d3wcon.getDialogType().equals(DialogType.SINGLEFORM)) {
			st = TemplateUtils.getStringTemplate("D3webDialog", "html");
		}

		if (d3wcon.getUserprefix() != "") {
			st = TemplateUtils.getStringTemplate(d3wcon.getUserprefix() + "D3webDialog", "html");
		}
		/* fill some basic attributes */
		st.setAttribute("header", D3webConnector.getInstance().getHeader());

		// load case list dependent from logged in user, e.g. MEDIASTINITIS
		String opts = "";
		if ((String) http.getAttribute("user") != null && (String) http.getAttribute("user") != "") {
			opts = PersistenceD3webUtils.getCaseListFromUserFilename((String) http.getAttribute("user"));
		}
		// load case list dependent from login, fam name and institute, e.g.
		// HERNIA
		else if ((String) http.getAttribute("login") != null
				&& (String) http.getAttribute("login") != "" &&
				(String) http.getAttribute("nname") != null
				&& (String) http.getAttribute("nname") != "" &&
				(String) http.getAttribute("institute") != null
				&& (String) http.getAttribute("institute") != "") {

			System.out.println("HERNIA");
			String idString = (String) http.getAttribute("login") +
					(String) http.getAttribute("nname") + (String) http.getAttribute("institute");
			opts = PersistenceD3webUtils.getCaseListFilterByID(idString);
		}
		// otherwise
		else {
			opts = PersistenceD3webUtils.getCaseList();
		}
		st.setAttribute("fileselectopts", opts);

		// Summary dialog
		String sum = fillSummaryDialog();
		st.setAttribute("sumQuestionnaire", sum);

		// set language variable for StringTemplate Widgets
		String lang = D3webConnector.getInstance().getLanguage();
		if (lang.equals("de")) {
			st.setAttribute("langDE", "de");
		}
		else if (lang.equals("en")) {
			st.setAttribute("langEN", "en");
		}

		// add some buttons for basic functionality
		st.setAttribute("loadcase", "true");
		st.setAttribute("savecase", "true");
		st.setAttribute("reset", "true");

		// ONLY FOR HERNIA! Disable for Mediastinitis?!
		st.setAttribute("summary", true);
		st.setAttribute("statistics", true);
		st.setAttribute("followup", true);

		// create a summary of already answered questions

		// st.setAttribute("sendexit", "true");

		/*
		 * handle custom ContainerCollection modification, e.g., enabling
		 * certain JS stuff
		 */
		if (D3webConnector.getInstance().getD3webParser().getLogin()) {
			st.setAttribute("login", "true");
			cc.js.enableLogin();
		}

		// handle Css
		handleCss(cc);

		// render the children
		renderChildren(st, cc, root);

		// global JS initialization
		defineAndAddJS(cc);

		st.setAttribute("fullcss", cc.css.generateOutput());
		st.setAttribute("fulljs", cc.js.generateOutput());
		st.setDefaultArgumentValues();

		cc.html.add(st.toString());
		return cc;
	}

	/**
	 * Renders the children of a given TerminologyObject and assembles the
	 * result into the given StringTemplate and writes into the given
	 * ContainerCollection.
	 *
	 * @created 15.01.2011
	 * @param st The StringTemplate for assembly
	 * @param cc The ContainerCollection for writing into
	 * @param to The TerminologyObject the children of which are rendered.
	 */
	protected void renderChildren(StringTemplate st, ContainerCollection cc,
			TerminologyObject to) {

		StringBuilder childrenHTML = new StringBuilder();
		D3webConnector d3wcon = D3webConnector.getInstance();

		// number of columns that is to be set for this element, default 1-col
		int columns = 1;
		if (to.getName().equals("Q000")) {
			columns = d3wcon.getDialogColumns();
		}
		else if (to instanceof QContainer) {
			columns = d3wcon.getQuestionnaireColumns();
		}
		else if (to instanceof Question) {
			columns = d3wcon.getQuestionColumns();
		}

		// if more than one column is required, get open-table tag from
		// TableContainer and append it to the HTML
		if (columns > 1) {
			String tableOpening =
					cc.tc.openTable(to.getName().replace(" ", "_"), columns);
			childrenHTML.append(tableOpening);
		}

		// for each of the child elements
		TerminologyObject[] children = to.getChildren();
		for (TerminologyObject child : children) {

			// get the matching renderer
			ID3webRenderer childRenderer = D3webRenderer.getRenderer(child);
			// System.out.println(childRenderer);

			// receive the rendering code from the Renderer and append
			String childHTML =
					childRenderer.renderTerminologyObject(cc, child, to);
			if (childHTML != null) {
				childrenHTML.append(childHTML);
			}

			// if the child is a question, check recursively for follow-up-qs
			// as this is done after having inserted the normal child, the
			// follow up is appended in between the child and its follow-up
			if (child instanceof Question) {
				String fus = renderFollowUps(cc, child, to);
				childrenHTML.append(fus);
			}
		}

		// close the table that had been opened for multicolumn cases
		if (columns > 1) {
			String tableClosing = cc.tc.closeTable(to.getName().replace(" ", "_"));
			childrenHTML.append(tableClosing);
		}

		// if children, fill the template attribute children with children-HTML
		if (children.length > 0) {
			st.setAttribute("children", childrenHTML.toString());
		}
	}

	/**
	 * Handle the rendering of follow-up questions of question elements. If the
	 * children of a question-child are questions again, those are inserted
	 * right here (i.e., e.g. underneath the questionnaire), and next/right
	 * after to the "parent"-question.
	 *
	 * @created 20.01.2011
	 * @param cc ContainerCollection to be used
	 * @param child The (question) child of the TerminologyObject parent, that
	 *        might posess follow up questions.
	 * @param parent The parent TerminologyObject.
	 * @return
	 */
	private String renderFollowUps(ContainerCollection cc, TerminologyObject child,
			TerminologyObject parent) {
		StringBuilder fus = new StringBuilder();

		// if child (question) has children and at least the 1st also a question
		if (child.getChildren() != null && child.getChildren().length != 0
				&& child.getChildren()[0] instanceof Question) {

			// get the (probably question) children of the child
			for (TerminologyObject childsChild : child.getChildren()) {

				// get appropriate renderer
				ID3webRenderer childRenderer = D3webRenderer.getRenderer(childsChild);

				// receive the rendering code from the Renderer and append
				StringBuilder childHTML = new StringBuilder(
						childRenderer.renderTerminologyObject(cc, childsChild, parent));
				if (child instanceof Question) {
					childHTML.append(renderFollowUps(cc, childsChild, parent));
				}
				if (childHTML != null) {
					fus.append(childHTML);
				}
			}
		}
		return fus.toString();
	}

	/**
	 * Renders the choices of a given (question) TerminologyObject and assembles
	 * the result into the given StringTemplate(s) and writes everything into
	 * the given ContainerCollection.
	 *
	 * @created 15.01.2011
	 * @param st The StringTemplate
	 * @param cc The ContainerCollection
	 * @param to The TerminologyObject
	 */
	protected void renderChoices(StringTemplate st, ContainerCollection cc,
			TerminologyObject to, TerminologyObject parent) {

		StringBuilder childrenHTML = new StringBuilder();

		// number of columns that is to be set for this element
		int columns = 1;

		// CAREFUL: default setting: 1-col style for q's with input field,
		// ALSO if something different is set in xml
		if (to instanceof QuestionNum ||
				to instanceof QuestionDate ||
				to instanceof QuestionText) {
			columns = 1;
		}
		else if (D3webConnector.getInstance().getQuestionColumns() != -1) {
			columns = D3webConnector.getInstance().getQuestionColumns();
		}
		else {
			// default: set 2 columns for questions, i.e., answers displayed in
			// 2 cols
			columns = 2;
		}

		// if more than one column open table tag via TableContainer and
		// append
		if (columns > 1) {
			String tableOpening =
					cc.tc.openTable(to.getName().replace(" ", "_"), columns);
			childrenHTML.append(tableOpening);
		}

		// for choice questions (oc only so far...)
		if (to instanceof QuestionChoice) {
			// here the grids are rendered for info questions
			if (to instanceof QuestionZC) {
				String gridString = to.getInfoStore().getValue(ProKEtProperties.GRID);
				if (gridString != null && !gridString.isEmpty()) {
					childrenHTML.append(gridString);
				}
			}
			else {
				for (Choice c : ((QuestionChoice) to).getAllAlternatives()) {

					// get the suiting child renderer (i.e., for answers)
					ID3webRenderer childRenderer = D3webRenderer.getAnswerRenderer(to);

					// receive the matching HTML from the Renderer and append
					String childHTML =
							childRenderer.renderTerminologyObject(cc, c, to, parent);
					if (childHTML != null) {
						childrenHTML.append(childHTML);
					}
				}
			}

			// otherwise (num, text, date... questions)
		}
		else {
			// get the suiting child renderer (i.e., for answers)
			ID3webRenderer childRenderer = D3webRenderer.getAnswerRenderer(to);
			// System.out.println(childRenderer);

			// receive the matching HTML from the Renderer and append
			String childHTML =
					childRenderer.renderTerminologyObject(cc, to, parent);
			if (childHTML != null) {
				childrenHTML.append(childHTML);
			}
		}

		// render unknown option only for NON-abstract questions
		if (!(to.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION))
				&& !(to instanceof QuestionZC)) {

			/*
			 * Append result of the unknown-renderer, i.e., unknown option, if -
			 * unknown by default option of KB is activated - AND nothing set
			 * additionally for single questions
			 */
			if (D3webConnector.getInstance().getKb().getInfoStore().getValue(
					BasicProperties.UNKNOWNBYDEFAULT) == true) {

				// TODO: ugly hack. In case all unknowns should be rendered by
				// default, the exclusion of single questions needs to be
				// flagged by a unknown visible = true flag, as unknownVisible
				// obviously is false per default :-/
				if (!to.getInfoStore().getValue(BasicProperties.UNKNOWN_VISIBLE) == true) {

					ID3webRenderer unknownRenderer =
							D3webRenderer.getUnknownRenderer();

					// receive the matching HTML from the Renderer and append
					String childHTML =
							unknownRenderer.renderTerminologyObject(cc, to, parent);
					System.out.println(childHTML);
					if (childHTML != null) {
						childrenHTML.append(childHTML);
					}
				}
			}
		}

		// close the table that had been opened for multicolumn
		if (columns > 1) {
			String tableClosing = cc.tc.closeTable(to.getName().replace(" ", "_"));
			childrenHTML.append(tableClosing);
		}

		// if there had been children, fill the template attribute children
		if (childrenHTML.length() > 0) {
			st.setAttribute("children", childrenHTML.toString());
		}
	}

	/**
	 * Handles CSS specifications from the specification XML, i.e. checks the
	 * format, retrieves the corresponding CSS files from file system, and adds
	 * them to the final ContainerCollection of the dialog.
	 *
	 * @created 15.01.2011
	 * @param cc ContainerCollection containing all infos about the resulting
	 *        dialog.
	 * @param d3wcon the d3web Connector for retrieving the css
	 */
	protected void handleCss(ContainerCollection cc) {

		D3webConnector d3wcon = D3webConnector.getInstance();
		// css code from the specification XML
		String css = d3wcon.getCss();

		if (css != null) {
			// file reference or inline css?
			// regex prüft ob der css-String was in der Form
			// "file1, file2, file3" ist, also 1-mehrere CSS File Angaben
			if (css.matches("[\\w-,\\s]*")) {
				String[] parts = css.split(","); // aufspilitten

				for (String partCSS : parts) {
					// replace whitespace characters with empty string
					// and then get the corresponding css file
					StringTemplate stylesheet =
							TemplateUtils.getStringTemplate
									(partCSS.replaceAll("\\s", ""), "css");

					// if not at the end of stylesheet string
					if (stylesheet != null) {

						// Write css into codecontainer
						cc.css.add(stylesheet.toString());
					}
				}
			}
		}
	}

	/**
	 * Defines the necessary JavaScript required by this renderer/dialog, and
	 * adds it to the JS into the ContainerCollection.
	 *
	 * @created 15.01.2011
	 * @param cc The ContainerCollection
	 */
	protected void defineAndAddJS(ContainerCollection cc) {
		cc.js.enableD3Web();
		cc.js.add("$(function() {init_all();});", 1);
		cc.js.add("function init_all() {", 1);
		// cc.js.add("building = true;", 2);
		// cc.js.add("building = false;", 2);
		cc.js.add("hide_all_tooltips()", 2);
		cc.js.add("generate_tooltip_functions();", 3);
		cc.js.add("}", 31);

	}

	/**
	 * Prepares the table-framing for a given TerminologyObject by opening a new
	 * cell from the "parent's view", closing it properly, adding those cells to
	 * the given StringBuilder (i.e. opening BEFORE the to, and closing AFTER
	 * the to) and adding everything to the CodeCollection.
	 *
	 * @created 15.01.2011
	 * @param to The TerminologyObject
	 * @param parentID The parent or the TerminologyObject
	 * @param cc The ContainerCollection
	 * @param result The StringBuilder of the TerminologyObject that is
	 *        decorated.
	 */
	protected void makeTables(TerminologyObject to, TerminologyObject parent,
			ContainerCollection cc, StringBuilder result) {

		// get the parent. If not existent, return
		if (parent.getName() == null) {
			return;
		}

		// int colspan = dialogObject.getInheritableAttributes().getColspan();
		int colspan = 1;

		// insert table cell opening string before the content of result which
		// is the content/rendering of the dialog object itself
		result.insert(0,
				cc.tc.getNextCellOpeningString(parent.getName().replace(" ", "_"), colspan));

		// append table cell closing
		result.append(cc.tc.getNextCellClosingString(parent.getName().replace(" ", "_"), colspan));

		// add to the table container
		cc.tc.addNextCell(parent.getName().replace(" ", "_"), colspan);
	}

	/**
	 * Create tables structures (tr and td) for surrounding the given Choice
	 * object.
	 *
	 * @created 23.01.2011
	 * @param c The choice to be put into the table.
	 * @param parent The parent TerminologyObject, needed for
	 *        finalizing/inserting the table structure.
	 * @param cc ContainerCollection to be used.
	 * @param result StringBuilder, the tables are inserted into.
	 */
	protected void makeTables(Choice c, TerminologyObject parent,
			ContainerCollection cc, StringBuilder result) {

		// get the parent. If not existent, return
		if (parent.getName() == null) {
			return;
		}

		// int colspan = dialogObject.getInheritableAttributes().getColspan();
		int colspan = 1;

		// insert table cell opening string before the content of result which
		// is the content/rendering of the dialog object itself
		result.insert(0,
				cc.tc.getNextCellOpeningString(parent.getName().replace(" ", "_"), colspan));

		// append table cell closing
		result.append(cc.tc.getNextCellClosingString(parent.getName().replace(" ", "_"), colspan));

		// add to the table container
		cc.tc.addNextCell(parent.getName().replace(" ", "_"), colspan);
	}

	/**
	 * Retrieves the suiting StringTemplate (html) for a given base object name.
	 * MAYBE MOVE TO TEMPLATE UTILS
	 *
	 * @created 23.01.2011
	 * @param baseObjectName Name of the base object.
	 * @return The suitable StringTemplate name.
	 */
	public String getTemplateName(String baseObjectName) {
		String tempName = "";
		D3webConnector d3w = D3webConnector.getInstance();
		String up = d3w.getUserprefix();
		if (up != "" && up != null) {
			// hier evtl noch einfügen Prüfung auf Großbuchstaben oder
			// automatisch umwandeln
			tempName = D3webConnector.getInstance().getUserprefix() + baseObjectName;
		}
		else {
			tempName = baseObjectName;
		}
		return tempName;
	}

	@Override
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

	@Override
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

	public boolean isParentOfFollowUpQuIndicated(TerminologyObject to, Blackboard bb) {
		for (Question q : bb.getSession().getKnowledgeBase().getManager().getQuestions()) {

			// and check its indication state
			if (bb.getSession().getKnowledgeBase().getInitQuestions().contains(q)
						|| bb.getIndication(q).getState() == State.INDICATED
						|| bb.getIndication(q).getState() == State.INSTANT_INDICATED) {

				return true;
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

	@Override
	/*
	 * Overridden by sub-classing renderers, so no basic functionality here.
	 */
	public String renderTerminologyObject(ContainerCollection cc, TerminologyObject to,
			TerminologyObject parent) {

		return "";
	}

	@Override
	/*
	 * Overridden by sub-classing renderers, so no basic functionality here.
	 */
	public String renderTerminologyObject(ContainerCollection cc, Choice C, TerminologyObject to, TerminologyObject parent) {
		return "";
	}

	private String fillSummaryDialog() {

		StringBuilder bui = new StringBuilder();
		D3webConnector d3wcon = D3webConnector.getInstance();

		TerminologyObject root = d3wcon.getKb().getRootQASet();

		fillSummaryChildren(bui, root);

		return bui.toString();
	}

	private void fillSummaryChildren(StringBuilder bui, TerminologyObject to) {

		if (to instanceof QContainer && !to.getName().contains("Q000")) {
			bui.append("<div style='margin-top:10px;'><b>" + countQcon + " " + to.getName()
					+ "</b></div>");
			countQcon++;
		}
		else if (to instanceof Question) {
			Value val =
					d3webSession.getBlackboard().getValue((ValueObject) to);

			if (val != null && UndefinedValue.isNotUndefinedValue(val)) {
				bui.append("<div style='margin-left:10px;'>" + countQ + " " + to.getName()
						+ " -- " + val + "</div>");
			}
			countQ++;
		}

		if (to.getChildren() != null && to.getChildren().length != 0) {
			for (TerminologyObject toc : to.getChildren()) {
				fillSummaryChildren(bui, toc);
			}
		}

	}

}
