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
import de.d3web.core.session.Session;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webXMLParser.LoginMode;
import de.d3web.proket.d3web.utils.PersistenceD3webUtils;
import de.d3web.proket.data.DialogType;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;

public class MediastinitisDefaultRootD3webRenderer extends DefaultRootD3webRenderer implements RootD3webRenderer {

	/**
	 * Basic rendering of the root, i.e., the framing stuff of a dialog, like
	 * basic structure, styles etc. Initiates the rendering of child-objects.
	 */
	@Override
	public ContainerCollection renderRoot(ContainerCollection cc,
			Session d3webSession, HttpSession http) {

		// D3webRenderer.d3webSession = d3webSession;

		D3webConnector d3wcon = D3webConnector.getInstance();

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
		String opts = PersistenceD3webUtils.getUserCaseList((String) http.getAttribute("user"));

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

		/*
		 * handle custom ContainerCollection modification, e.g., enabling
		 * certain JS stuff
		 */
		LoginMode loginMode = D3webConnector.getInstance().getD3webParser().getLogin();
		cc.js.setLoginMode(loginMode);
		if (loginMode == LoginMode.usrdat) st.setAttribute("login", "true");

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
