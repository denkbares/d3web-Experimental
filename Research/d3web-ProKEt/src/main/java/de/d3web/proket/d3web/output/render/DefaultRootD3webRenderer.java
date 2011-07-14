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

import java.util.TreeMap;

import javax.servlet.http.HttpSession;

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.d3web.utils.PersistenceD3webUtils;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;

public class DefaultRootD3webRenderer extends AbstractD3webRenderer implements RootD3webRenderer {

	/**
	 * Basic rendering of the root, i.e., the framing stuff of a dialog, like
	 * basic structure, styles etc. Initiates the rendering of child-objects.
	 */
	@Override
	public ContainerCollection renderRoot(ContainerCollection cc,
			Session d3webSession, HttpSession http) {

		// D3webRenderer.d3webSession = d3webSession;

		D3webConnector d3wcon = D3webConnector.getInstance();

		// get the d3web base template according to dialog type
		StringTemplate st = TemplateUtils.getStringTemplate(d3wcon.getUserprefix() + "D3webDialog",
				"html");

		/* fill some basic attributes */
		st.setAttribute("header", D3webConnector.getInstance().getHeader());

		// load case list dependent from logged in user, e.g. MEDIASTINITIS
		String opts = getAvailableFiles(http);
		st.setAttribute("fileselectopts", opts);

		String info = renderHeaderInfoLine(d3webSession);
		st.setAttribute("info", info);

		// Summary dialog
		// String sum = fillSummaryDialog();
		// st.setAttribute("sumQuestionnaire", sum);

		// set language variable for StringTemplate Widgets
		String lang = D3webConnector.getInstance().getLanguage();
		if (lang.equals("de")) {
			st.setAttribute("langDE", "de");
		}
		else if (lang.equals("en")) {
			st.setAttribute("langEN", "en");
		}

		// add some buttons for basic functionality
		addButtons(st);

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
		renderChildren(st, cc, d3wcon.getKb().getRootQASet());

		// global JS initialization
		defineAndAddJS(cc);

		st.setAttribute("fullcss", cc.css.generateOutput());
		st.setAttribute("fulljs", cc.js.generateOutput());
		st.setDefaultArgumentValues();

		cc.html.add(st.toString());
		return cc;
	}

	@Override
	public String getAvailableFiles(HttpSession http) {
		String opts;
		if ((String) http.getAttribute("user") != null && (String) http.getAttribute("user") != "") {
			opts = PersistenceD3webUtils.getCaseListFromUserFilename((String) http.getAttribute("user"));
		}
		else {
			opts = PersistenceD3webUtils.getCaseList();
		}
		return opts;
	}

	@Override
	public void addButtons(StringTemplate st) {
		st.setAttribute("loadcase", "true");
		st.setAttribute("savecase", "true");
		st.setAttribute("reset", "true");
	}

	@Override
	public String renderHeaderInfoLine(Session d3webSession) {

		StringTemplate st = TemplateUtils.getStringTemplate("HeaderInfoLine", "html");

		TreeMap<Integer, Question> headerQuestions = new TreeMap<Integer, Question>();
		for (Question question : D3webConnector.getInstance().getKb().getManager().getQuestions()) {
			Boolean showInHeader = question.getInfoStore().getValue(ProKEtProperties.SHOW_IN_HEADER);
			if (showInHeader != null && showInHeader) {
				Integer pos = question.getInfoStore().getValue(ProKEtProperties.POSITION_IN_HEADER);
				headerQuestions.put(pos, question);
			}
		}
		StringBuilder infoStringBuilder = new StringBuilder();
		boolean first = true;
		for (Question headerQuestion : headerQuestions.values()) {
			Value value = AbstractD3webRenderer.d3webSession.getBlackboard().getValue(
					headerQuestion);
			first = verbalizeHeaderQuestion(headerQuestion, value, infoStringBuilder, first);
		}
		st.setAttribute("infoVerbalization", infoStringBuilder.toString());
		return st.toString();

	}

	private Boolean verbalizeHeaderQuestion(Question headerQuestion, Value headerQuestionValue, StringBuilder infoStringBuilder, Boolean first) {
		if (headerQuestionValue != null && UndefinedValue.isNotUndefinedValue(headerQuestionValue)
				&& !Unknown.getInstance().equals(headerQuestionValue)) {
			String questionString = headerQuestion.getName();
			String specificQuestionString = headerQuestion.getInfoStore().getValue(
					ProKEtProperties.HEADER_TEXT);
			if (specificQuestionString != null) questionString = specificQuestionString;
			String valueString = headerQuestionValue.toString();
			if (headerQuestionValue instanceof ChoiceValue) {
				Choice choice = ((ChoiceValue) headerQuestionValue).getChoice((QuestionChoice) headerQuestion);
				String specificHeaderText = choice.getInfoStore().getValue(
						ProKEtProperties.HEADER_TEXT);
				if (specificHeaderText != null) valueString = specificHeaderText;
			}
			if (!first) infoStringBuilder.append(", ");
			infoStringBuilder.append(questionString + ": " + valueString);
			first = false;
		}
		return first;
	}

	@Override
	public void handleCss(ContainerCollection cc) {

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
	@Override
	public void defineAndAddJS(ContainerCollection cc) {
		cc.js.enableD3Web();
		cc.js.add("$(function() {init_all();});", 1);
		cc.js.add("function init_all() {", 1);
		// cc.js.add("building = true;", 2);
		// cc.js.add("building = false;", 2);
		cc.js.add("hide_all_tooltips()", 2);
		cc.js.add("generate_tooltip_functions();", 3);
		cc.js.add("}", 31);

	}

	@Override
	public String fillSummaryDialog() {

		StringTemplate st = TemplateUtils.getStringTemplate("Summary",
				"html");

		StringBuilder bui = new StringBuilder();
		D3webConnector d3wcon = D3webConnector.getInstance();

		TerminologyObject root = d3wcon.getKb().getRootQASet();

		fillSummaryChildren(bui, root);

		st.setAttribute("sumQuestionnaire", bui.toString());

		return st.toString();
	}

	private void fillSummaryChildren(StringBuilder bui, TerminologyObject to) {

		if (to instanceof QContainer && !to.getName().contains("Q000")) {
			bui.append("<div style='margin-top:10px;'><b>" + countQcon + " " + to.getName()
					+ "</b></div>\n");
			countQcon++;
		}
		else if (to instanceof Question) {
			Value val =
					d3webSession.getBlackboard().getValue((ValueObject) to);

			if (val != null && UndefinedValue.isNotUndefinedValue(val)) {
				bui.append("<div style='margin-left:10px;'>" + countQ + " " + to.getName()
						+ " -- " + val + "</div>\n");
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
