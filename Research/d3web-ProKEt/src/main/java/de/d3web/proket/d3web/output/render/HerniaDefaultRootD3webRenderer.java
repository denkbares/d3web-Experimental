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

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.utils.PersistenceD3webUtils;
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
public class HerniaDefaultRootD3webRenderer extends DefaultRootD3webRenderer {

	/**
	 * Basic rendering of the root, i.e., the framing stuff of a dialog, like
	 * basic structure, styles etc. Initiates the rendering of child-objects.
	 */
	@Override
	public ContainerCollection renderRoot(ContainerCollection cc,
			Session d3webSession, HttpSession http) {

		D3webConnector d3wcon = D3webConnector.getInstance();

		// System.out.println(d3wcon.getSingleSpecs().toString());
		TerminologyObject root = d3wcon.getKb().getRootQASet();

		StringTemplate st = null;

		// get the d3web base template according to dialog type
		if (d3wcon.getUserprefix() != "") {
			st = TemplateUtils.getStringTemplate(d3wcon.getUserprefix() + "D3webDialog", "html");
		}
		else {
			st = TemplateUtils.getStringTemplate("D3webDialog", "html");
		}

		/* fill some basic attributes */
		st.setAttribute("header", D3webConnector.getInstance().getHeader());

		// load case list dependent from logged in user, e.g. HERNIA
		String opts = "";
		if ((String) http.getAttribute("login") != null
				&& (String) http.getAttribute("login") != "" &&
				(String) http.getAttribute("nname") != null
				&& (String) http.getAttribute("nname") != "" &&
				(String) http.getAttribute("institute") != null
				&& (String) http.getAttribute("institute") != "") {

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

		/* HERNIA specific */
		// route
		Question route = D3webConnector.getInstance().getKb().getManager().searchQuestion(
				"Please select your hernia route");
		Value rVal = AbstractD3webRenderer.d3webSession.getBlackboard().getValue(route);

		// level
		Question level = D3webConnector.getInstance().getKb().getManager().searchQuestion(
				"Please choose the database level");
		Value lVal = AbstractD3webRenderer.d3webSession.getBlackboard().getValue(level);

		if ((rVal != null && UndefinedValue.isNotUndefinedValue(rVal)) || (lVal != null
				&& UndefinedValue.isNotUndefinedValue(lVal))) {
			String infoString = "";
			if (lVal != null && UndefinedValue.isNotUndefinedValue(lVal)) {
				infoString += "Level: " + lVal.toString().replace("Level ", "");
			}
			if (rVal != null && UndefinedValue.isNotUndefinedValue(rVal)) {
				if (!infoString.isEmpty()) {
					infoString += ", ";
				}
				infoString += "Route: " + rVal.toString().replace(" route", "");
			}
			st.setAttribute("info", infoString);
		}
		else {
			st.setAttribute("info", "");
		}

		// add some buttons for basic functionality
		st.setAttribute("loadcase", "true");
		st.setAttribute("savecase", "true");
		st.setAttribute("reset", "true");

		// ONLY FOR HERNIA, 3 custom buttons
		st.setAttribute("summary", true);
		st.setAttribute("statistics", true);
		st.setAttribute("followup", true);

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

}
