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

import de.d3web.core.session.Session;
import de.d3web.proket.d3web.input.D3webRendererMapping;
import de.d3web.proket.d3web.output.render.SolutionPanelD3webRenderer;
import de.d3web.proket.output.container.ContainerCollection;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet representing the consultation questionary style
 * 
 * @author Martina Freiberg @date September 2012
 */
public class ConsQuestionary extends D3webDialog {
    
    @Override
    protected String getSource(HttpServletRequest request, HttpSession http) {

        String source = "QuestionaryCons.xml"; // default
        if (request.getParameter("src") != null) {
            source = request.getParameter("src");
        }
        return source.endsWith(".xml") ? source : source + ".xml";
    }

    /* In consultation dialogs, additionally a solution panel is rendered and
     needs to be updated each time, new facts are added */
    @Override
     protected void addFacts(HttpServletRequest request,
            HttpServletResponse response, HttpSession httpSession)
            throws IOException {

        /* means: add questions as single objects, without reloading the
          entire dialog UI but only the answered questions */
        super.addFacts(request, response, httpSession);
        
        Session d3webs = (Session) httpSession.getAttribute(D3WEB_SESSION);
        
        /* similarly: update solution panel without needing to reload complete 
          dialog */
        updateDialogPanel(response.getWriter(), d3webs, httpSession, request);
     }
   
    /**
     * Assembling the diff and giving it to the writer. The diff contains 
     * the element which should be replaced - here the solution panel - 
     * and the contents that are to be written instead of the old ones.
     * @param writer
     * @param d3webSession
     * @param httpSession
     * @param request 
     */
    protected void updateDialogPanel(PrintWriter writer, Session d3webSession,
            HttpSession httpSession, HttpServletRequest request) {
        ContainerCollection cc = new ContainerCollection();

        String solutionPanelID = "solutionPanel";
        writer.append(REPLACEID + solutionPanelID);
        writer.append(REPLACECONTENT);
        writer.append("<div id='" + solutionPanelID + "'>");
        writer.append("<div id='sol_heading'>Solutions:</div>");
        
        
        SolutionPanelD3webRenderer spr = D3webRendererMapping.getInstance().getSolutionPanelRenderer();
        String spUpdate = spr.renderSolutionPanel(d3webSession, 
                    SolutionPanelD3webRenderer.EXPLANATIONTYPE.TEXTUALLISTING,
                    httpSession);
        
        writer.append(spUpdate);
        writer.append("</div>");
    }
}
