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

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.session.Session;
import de.d3web.proket.d3web.input.D3webRendererMapping;
import de.d3web.proket.d3web.output.render.QuestionnaireD3webRenderer;
import de.d3web.proket.d3web.output.render.SolutionPanelD3webRenderer;
import de.d3web.proket.d3web.utils.D3webUtils;
import de.d3web.proket.output.container.ContainerCollection;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
 * @date 25.10.2012;
 *
 */
public class StandardDialog extends D3webDialog {

    @Override
    protected String getSource(HttpServletRequest request, HttpSession http) {

        String source = "BaumDecTree.xml"; // default
        if (request.getParameter("src") != null) {
            source = request.getParameter("src");
        }
        return source.endsWith(".xml") ? source : source + ".xml";
    }

    /*
     * In consultation dialogs, additionally a solution panel is rendered and
     * needs to be updated each time, new facts are added
     */
    @Override
    protected void addFacts(HttpServletRequest request,
            HttpServletResponse response, HttpSession httpSession)
            throws IOException {

        /*
         * means: add questions as single objects, without reloading the entire
         * dialog UI but only the answered questions
         */
        super.addFacts(request, response, httpSession);

        Session d3webs = (Session) httpSession.getAttribute(D3WEB_SESSION);

        /*
         * similarly: update solution panel without needing to reload complete
         * dialog
         */
        updateDialogPanel(response.getWriter(), d3webs, httpSession, request);
    }

    /**
     * Assembling the diff and giving it to the writer. The diff contains the
     * element which should be replaced - here the solution panel - and the
     * contents that are to be written instead of the old ones.
     *
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

    @Override
    protected void reloadSelectedQuestionnaire(HttpServletRequest request,
            HttpServletResponse response, HttpSession httpSession) throws IOException {

        PrintWriter writer = response.getWriter();
        ContainerCollection cc = new ContainerCollection();

        String refreshContentID = "contents";
        writer.append(REPLACEID + refreshContentID);
        writer.append(REPLACECONTENT);
        writer.append("<div id='" + refreshContentID + "'>");

        String qsString = request.getParameter("questionnaire").toString();
        Session d3webs = (Session) httpSession.getAttribute(D3WEB_SESSION);
        QContainer qs =
                d3webs.getKnowledgeBase().getManager().searchQContainer(qsString);
        
        int loc = httpSession.getAttribute("locale") != null
                ? Integer.parseInt(httpSession.getAttribute("locale").toString()) : 2;
        TerminologyObject parent = qs instanceof QContainer ? d3wcon.getKb().getRootQASet()
                : D3webUtils.getQuestionnaireAncestor(qs);



        QuestionnaireD3webRenderer QSrenderer =
                (QuestionnaireD3webRenderer) D3webRendererMapping.getInstance().getRenderer(qs);
        String contentUpdate =
                QSrenderer.renderTerminologyObject(d3webs, cc, qs, parent, loc, httpSession, request);


        writer.append(contentUpdate);
        writer.append("</div>");
    }
}
