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
import de.d3web.core.knowledge.ValueObject;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webRendererMapping;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.d3web.output.render.AbstractD3webRenderer;
import de.d3web.proket.d3web.output.render.DefaultRootD3webRenderer;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.d3web.ue.JSONLogger;
import de.d3web.proket.d3web.utils.PersistenceD3webUtils;
import de.d3web.proket.output.container.ContainerCollection;
import java.util.*;

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
public class ClarihieDialog extends D3webDialog {

    @Override
    protected String getSource(HttpServletRequest request, HttpSession http) {

        return "ITree140812";
    }

    /**
     * Add one or several given facts. Thereby, first check whether input-store
     * has elements, if yes, parse them and set them (for num/text/date
     * questions), if no, just parse and set a given single value.
     *
     * @Override
     */
    protected void addFactsYN(HttpServletRequest request,
            HttpServletResponse response, HttpSession httpSession)
            throws IOException {

        PrintWriter writer = response.getWriter();

        Session d3webSession = (Session) httpSession.getAttribute(D3WEB_SESSION);

        String question = request.getParameter("question");
        String value = request.getParameter("value");

        setValue(d3webSession, request, question, value, httpSession);

        PersistenceD3webUtils.saveCase((String) httpSession.getAttribute("user"), "autosave",
                d3webSession);


        // get the root renderer --> call getRenderer with null
        DefaultRootD3webRenderer d3webr =
                (DefaultRootD3webRenderer) D3webRendererMapping.getInstance().getRenderer(null);

        // new ContainerCollection needed each time to get an updated dialog
        ContainerCollection cc = new ContainerCollection();
        Session d3webSess = (Session) httpSession.getAttribute(D3WEB_SESSION);

        cc = d3webr.renderRoot(cc, d3webSess, httpSession, request);
        writer.print(cc.html.toString()); // deliver the rendered output
        writer.close(); // and close
    }

    /**
     * For adding date facts in itree
     *
     * @param request
     * @param response
     * @param httpSession
     * @throws IOException
     * @Override
     */
    protected void addFactITree(HttpServletRequest request,
            HttpServletResponse response, HttpSession httpSession)
            throws IOException {

        Session d3webSession = (Session) httpSession.getAttribute(D3WEB_SESSION);
        String question = request.getParameter("question");
        String value = request.getParameter("value");
        
        
        PersistenceD3webUtils.saveCase(
                (String) httpSession.getAttribute("user"),
                "autosave",
                d3webSession);
        
        D3webUtils.setValueITree(question, value, d3webSession);
    }

    private void setValue(Session d3webSession, HttpServletRequest request,
            String question, String value, HttpSession httpSession) {

        D3webUtils.setValueITree(question, value, d3webSession);
        if (d3wcon.isLogging()) {
            handleQuestionValueLogging(
                    request, httpSession, question, value, d3webSession);
        }
    }

    protected void saveShowStatus(HttpServletRequest request,
            HttpSession httpSession) {

        Session d3webSession = (Session) httpSession.getAttribute(D3WEB_SESSION);
        String parameterQuestion = request.getParameter("question");
        String q = AbstractD3webRenderer.getObjectNameForId(parameterQuestion);
        Question qFinal = d3webSession.getKnowledgeBase().getManager().searchQuestion(
                q == null ? parameterQuestion : q);


        /*
         * if(qFinal.getInfoStore().getValue(ProKEtProperties.ITREESHOWN) !=
         * null && qFinal.getInfoStore().getValue(ProKEtProperties.ITREESHOWN)
         * == true ){
         * qFinal.getInfoStore().addValue(ProKEtProperties.ITREESHOWN, false); }
         * else { qFinal.getInfoStore().addValue(ProKEtProperties.ITREESHOWN,
         * true); }
         */

    }

    private void handleQuestionValueLogging(HttpServletRequest request,
            HttpSession httpSession, String ques, String val, Session d3webSession) {

        // retrieve logtime
        String logtime = request.getParameter("timestring").replace("+", " ");

        // retrieve internal IDs of value and question and corresponding D3webObjects
        String question = AbstractD3webRenderer.getObjectNameForId(ques);
        Question q = D3webConnector.getInstance().getKb().getManager().searchQuestion(
                question == null ? ques : question);

        String value = AbstractD3webRenderer.getObjectNameForId(val);
        value = value == null ? val : AbstractD3webRenderer.getObjectNameForId(val);
        Value v = d3webSession.getBlackboard().getValue((ValueObject) q);

        String datestring = "";
        JSONLogger logger = (JSONLogger) httpSession.getAttribute("logger");

        ServletLogUtils.logQuestionValue(question, value, logtime, logger);
    }

    @Override
    protected void show(HttpServletRequest request, HttpServletResponse response,
            HttpSession httpSession) throws IOException {
        
        Session d3webs = (Session)httpSession.getAttribute(D3WEB_SESSION);
        //if(!(httpSession.getAttribute("showstati")!=null &&
          //      httpSession.getAttribute("showstati").equals("set"))){
            //TerminologyObject root = d3webs.getKnowledgeBase().getRootQASet();
            //Session session = setQuestionShowStati(root, d3webs);
            //httpSession.setAttribute(D3WEB_SESSION, session);
        //} 
        super.show(request, response, httpSession);
    }

    private Session setQuestionShowStati(TerminologyObject to, Session d3webs) {

        for (TerminologyObject child : to.getChildren()) {

            if (child.getInfoStore().getValue(ProKEtProperties.ITREEINIT) == null) {
                
                TerminologyObject questionToSet = 
                        d3webs.getKnowledgeBase().getManager().searchQuestion(child.getName());
                questionToSet.getInfoStore().addValue(ProKEtProperties.ITREEINIT, false);
            }
            
            if(child.getChildren().length > 0){
                for (TerminologyObject childschild: child.getChildren()){
                    setQuestionShowStati(childschild, d3webs);
                }
            }
        }
        
        return d3webs;
    }
    
    
    
}