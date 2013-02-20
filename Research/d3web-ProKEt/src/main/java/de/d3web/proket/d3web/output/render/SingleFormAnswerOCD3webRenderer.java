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

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.utils.D3webUtils;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;
import javax.servlet.http.HttpSession;

/**
 * Renderer for rendering basic OCAnswers.
 *
 * TODO CHECK: 1) basic properties for answers 2) d3web resulting properties,
 * e.g. is indicated, is shown etc.
 *
 * @author Martina Freiberg @created 15.01.2011
 */
public class SingleFormAnswerOCD3webRenderer extends AbstractD3webRenderer implements AnswerD3webRenderer {

    @Override
    /**
     * Specifically adapted for OCAnswer rendering
     */
    public String renderTerminologyObject(ContainerCollection cc, Session d3webSession,
            Choice c, TerminologyObject to, TerminologyObject parent, int loc,
            HttpSession httpSession) {

        StringBuilder sb = new StringBuilder();

        // return if the InterviewObject is null
        if (to == null) {
            return "";
        }

        StringTemplate st = null;

        // get the template. In case user prefix was specified, the specific
        // TemplateName is returned, otherwise the base object name.
        st = TemplateUtils.getStringTemplate(
                super.getTemplateName("OcAnswerFlat"), "html");

        st.setAttribute("fullId", getID(c));// .getName().replace(" ", "_"));
        st.setAttribute("realAnswerType", "oc");
        st.setAttribute("parentFullId", getID(to));// getName().replace(" ",
        // "_"));


        String resString = D3webUtils.getPopupPromptChoices(c, loc);
        if (resString != null) {
            st.setAttribute("tooltip", resString);
        }

        Blackboard bb = d3webSession.getBlackboard();
        Value value = bb.getValue((ValueObject) to);


        //st.setAttribute("text", c.getName());
        st.setAttribute("text", D3webUtils.getAnswerPrompt(to, c, loc));
        st.setAttribute("count", D3webConnector.getInstance().getTOCount(to));
        if (to.getInfoStore().getValue(ProKEtProperties.IMAGE) != null) {
            st.setAttribute("imageAnswer", "true");
        }



        // if question is an abstraction question --> readonly
        if (to.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION)) {
            st.setAttribute("readonly", "true");
            st.setAttribute("inactive", "true");
        }

        //System.out.println(to.getName());
        //System.out.println(parent.getName() + isIndicated(parent, bb));
        //System.out.println();
        // QContainer indicated
        if (bb.getSession().getKnowledgeBase().getInitQuestions().contains(parent)
                || D3webUtils.isIndicatedPlain(parent, bb) || D3webUtils.isIndicatedByChild(parent, bb)) {

            if (D3webUtils.isIndicatedPlain(parent, bb) || D3webUtils.isIndicatedPlain(to, bb)) {


                // show, if indicated follow up
                if ((D3webUtils.isFollowUpToQCon(to, parent) && D3webUtils.isIndicatedPlain(to, bb))
                        || (!D3webUtils.isFollowUpToQCon(to, parent))) {
                    st.removeAttribute("readonly");
                    st.removeAttribute("inactive");
                    st.removeAttribute("qstate");
                    st.setAttribute("qstate", "");
                } else {
                    st.setAttribute("inactive", "true");
                    st.setAttribute("readonly", "true");
                }

            } else {
                st.setAttribute("inactive", "true");
                st.setAttribute("readonly", "true");
            }
        } else {
            st.setAttribute("inactive", "true");
            st.setAttribute("readonly", "true");
        }








        // if value of the to=question equals this choice
        if (value.toString().equals(c.toString())) {

            // if to=question is abstraction question, was readonly before, but
            // value
            // has been set (e.g. by other answer & Kb indication), remove
            // readonly
            if (to.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION)) {
                st.removeAttribute("readonly");
                st.removeAttribute("inactive");
            }

            // set selected
            st.removeAttribute("selection");
            st.setAttribute("selection", "checked=\'checked\'");
        } // if value of question is undefined or unknown remove previous choice
        else if (UndefinedValue.isUndefinedValue(value)
                || value.equals(Unknown.getInstance())) {
            st.removeAttribute("selection");
            st.setAttribute("selection", "");
        }

        sb.append(st.toString());

        super.makeTables(c, to, cc, sb);

        return sb.toString();
    }
}