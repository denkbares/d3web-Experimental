/**
 * Copyright (C) 2012 Chair of Artificial Intelligence and Applied Informatics
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

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.session.Session;
import de.d3web.proket.d3web.output.render.AbstractD3webRenderer;
import de.d3web.proket.d3web.output.render.IQuestionD3webRenderer;
import de.d3web.proket.d3web.utils.D3webUtils;
import de.d3web.proket.d3web.utils.PersistenceD3webUtils;
import de.d3web.proket.output.container.ContainerCollection;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet representing the consultation questionary style
 *
 * @author Martina Freiberg @date September 2012
 */
public class OQD extends D3webDialog {

    @Override
    protected String getSource(HttpServletRequest request, HttpSession http) {

	String source = "OQD.xml"; // default
	if (request.getParameter("src") != null) {
	    source = request.getParameter("src");
	}
	return source.endsWith(".xml") ? source : source + ".xml";
    }

    /*
     * In QOD dialogs, we only need to set the fact and then call method to
     * update the question display part of dialog
     */
    @Override
    protected void addFacts(HttpServletRequest request,
	    HttpServletResponse response, HttpSession httpSession)
	    throws IOException {

	Session d3webSession = (Session) httpSession.getAttribute(D3WEB_SESSION);
	List<String> questions = new ArrayList<String>();
	List<String> values = new ArrayList<String>();

	// get all questions and answers lately answered as lists
	getParameterPairs(request, "question", "value", questions, values);

	// set the values
	setValues(d3webSession, questions, values, request, httpSession);

	// autosave the current state
	PersistenceD3webUtils.saveCase(
		(String) httpSession.getAttribute("user"),
		"autosave",
		d3webSession);

	updateQuestionDisplay(response.getWriter(), d3webSession, httpSession, request);
    }

    /**
     * Assembling the updating diff and giving it to the writer. The diff contains the
     * element which should be replaced - here the solution panel - and the
     * contents that are to be written instead of the old ones.
     *
     * @param writer
     * @param d3webSession
     * @param httpSession
     * @param request
     */
    protected void updateQuestionDisplay(PrintWriter writer, Session d3webSession,
	    HttpSession httpSession, HttpServletRequest request) {
	ContainerCollection cc = new ContainerCollection();

	String questionCellID = "questionCell";
	writer.append(REPLACEID + questionCellID);
	writer.append(REPLACECONTENT);

	TerminologyObject to = D3webUtils.getNextQuestion(d3webSession);

	writer.append("<div id='questionCell' class='cell'>");


	// if no further interview objects, i.e. interview is ended
	if (to == null) {
	    
	    String notification = "<div style='padding-top:100px; '> NO INTERVIEW OBJECTS ARE AVAILABLE (ANYMORE)<br />"
		+ "KEINE FRAGEN (MEHR) VERFÜGBAR!</div>";
	    writer.append(notification);
	    
	} else {
	    IQuestionD3webRenderer toRenderer = AbstractD3webRenderer.getRenderer(to);
	    TerminologyObject parent = to instanceof QContainer ? d3wcon.getKb().getRootQASet()
		    : D3webUtils.getQuestionnaireAncestor(to);

	    // set Locale=2 = english for default
	    int loc = httpSession.getAttribute("locale") != null
		    ? Integer.parseInt(httpSession.getAttribute("locale").toString()) : 2;


	    writer.append(
		    toRenderer.renderTerminologyObject(
		    d3webSession, new ContainerCollection(), to,
		    parent, loc,
		    httpSession, request));

	}
	
	
	writer.append("</div>");

    }
}
