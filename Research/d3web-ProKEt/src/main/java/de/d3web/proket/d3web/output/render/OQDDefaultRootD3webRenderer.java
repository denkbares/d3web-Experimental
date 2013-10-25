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

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.proket.d3web.input.*;
import de.d3web.proket.d3web.settings.GeneralDialogSettings;
import de.d3web.proket.d3web.settings.UISettings;
import de.d3web.proket.d3web.utils.D3webUtils;
import de.d3web.proket.d3web.utils.StringTemplateUtils;
import de.d3web.proket.output.container.ContainerCollection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.antlr.stringtemplate.StringTemplate;

public class OQDDefaultRootD3webRenderer extends DefaultRootD3webRenderer {

    @Override
    public ContainerCollection renderRoot(ContainerCollection cc,
	    Session d3webSession, HttpSession http, HttpServletRequest request) {

	StringTemplate oqdFrameSt = StringTemplateUtils.getTemplate("oqd/oqdFrame");
	
	renderBasicDialogStuff(cc, http, oqdFrameSt);

	TerminologyObject question = D3webUtils.getNextQuestion(d3webSession);

	IQuestionD3webRenderer qrenderer =
		AbstractD3webRenderer.getRenderer(question);

	// TODO: locale
	String qString = qrenderer.renderTerminologyObject(d3webSession, cc, question, question, 2, http, request);
	oqdFrameSt.setAttribute("children", qString);

	cc.html.add(oqdFrameSt.toString());
	return cc;
    }

    @Override
    public void defineAndAddJS(ContainerCollection cc) {
	cc.js.enableD3Web();
	if (D3webUESettings.getInstance().isLogging()) {
	    cc.js.enableClickLogging();
	}

	cc.js.setOQD();

	cc.js.add("$(function() {init_all();});", 1);
	cc.js.add("function init_all() {", 1);
	cc.js.add("hide_all_tooltips()", 2);
	cc.js.add("generate_tooltip_functions();", 3);
	cc.js.add("}", 31);

    }

    public void renderBasicDialogStuff(ContainerCollection cc,
	    HttpSession http, StringTemplate oqdFrameSt) {

	UISettings uis = UISettings.getInstance();
	GeneralDialogSettings gds = GeneralDialogSettings.getInstance();
	
	
	// get the d3web base template according to dialog type
	String userprefix = uis.getDialogType().toString();
	
	/*
	 * fill some basic attributes
	 */
	oqdFrameSt.setAttribute("header", gds.getHeader());
	oqdFrameSt.setAttribute("title", userprefix + "-Dialog");

	// load case list dependent from logged in user, e.g. MEDIASTINITIS
	String opts = renderUserCaseList((String) http.getAttribute("user"), http);
	oqdFrameSt.setAttribute("fileselectopts", opts);

	// add some buttons for basic functionality
	oqdFrameSt.setAttribute("loadcase", "true");
	oqdFrameSt.setAttribute("savecase", "true");
	oqdFrameSt.setAttribute("reset", "true");

	if (D3webUESettings.getInstance().isFeedbackform()) {
	    oqdFrameSt.setAttribute("feedback", "true");
	}

	if (!D3webUESettings.getInstance().getUequestionnaire().equals(D3webUESettings.UEQ.NONE)) {
	    oqdFrameSt.setAttribute("ueq", "true");
	}
	
	/*
	 * handle custom ContainerCollection modification, e.g., enabling
	 * certain JS stuff
	 */
	if (D3webConnector.getInstance().getD3webParser().getLogging().equals("ON")) {
	    oqdFrameSt.setAttribute("logging", true);
	}
	
	// handle Css
	handleCss(cc);

	// global JS initialization
	defineAndAddJS(cc);

	oqdFrameSt.setAttribute("fullcss", cc.css.generateOutput());
	oqdFrameSt.setAttribute("fulljs", cc.js.generateOutput());
	oqdFrameSt.setDefaultArgumentValues();
    }

    
    /**
     * Render the dialog frame plus a notification, that no interview objects
     * are available.
     * This is called, when the one question dialog is initialized but no 
     * valid dialog objects (questions) can be retrieved from KB
     * 
     * @param cc
     * @param d3webSession
     * @param http
     * @param request
     * @return 
     */
    public ContainerCollection renderNoObjectsNotification(
	    ContainerCollection cc,
	    Session d3webSession, HttpSession http, HttpServletRequest request) {
	
	StringTemplate oqdFrameSt = StringTemplateUtils.getTemplate("oqd/oqdFrame");
	
	renderBasicDialogStuff(cc, http, oqdFrameSt);

	// TODO: beautify rendering of no objects notification!
	// RENDER NOTIFICATION INSTEAD OF CHILDERN = QUESTION
	String qString = "<div style='padding-top:100px; '> NO INTERVIEW OBJECTS ARE AVAILABLE (ANYMORE)<br />"
		+ "KEINE FRAGEN (MEHR) VERFÜGBAR!</div>";
	
	oqdFrameSt.setAttribute("children", qString);
	
	cc.html.add(oqdFrameSt.toString());
	return cc;
    }
}
