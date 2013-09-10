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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.proket.d3web.utils.D3webUtils;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;
import javax.servlet.http.HttpSession;

/**
 * Renderer for rendering basic NumAnswers.
 *
 * TODO CHECK: 1) basic properties for answers
 *
 * @author Martina Freiberg @created 16.01.2011
 */
public class AnswerNumD3webRenderer extends AbstractD3webRenderer implements AnswerD3webRenderer {

    @Override
    /**
     * Specifically adapted for rendering NumAnswers
     */
    public String renderTerminologyObject(ContainerCollection cc, Session d3webSession, Choice c,
            TerminologyObject to, TerminologyObject parent, int loc, HttpSession httpSession) {

        QuestionNum nq = (QuestionNum) to;

        StringBuilder sb = new StringBuilder();

        // return if the InterviewObject is null
        if (to == null) {
            return "";
        }

        // get the fitting template. In case user prefix was specified, the
        // specific TemplateName is returned, else the base object name.
        StringTemplate st = TemplateUtils.getStringTemplate(
                super.getTemplateName("NumAnswer"), "html");

        st.setAttribute("fullId", getID(nq));// .getName().replace(" ", "_"));
        st.setAttribute("realAnswerType", "num");
        st.setAttribute("parentFullId", getID(parent));// .getName().replace(" ",
        // "_"));

        String unit = nq.getInfoStore().getValue(MMInfo.UNIT);
        if (nq.getInfoStore().getValue(MMInfo.UNIT) != null) {
            NumericalInterval interVal = nq.getInfoStore().getValue(
                    BasicProperties.QUESTION_NUM_RANGE);
            String inter = "";
            if (interVal != null) {
                String left = trimPZ(interVal.getLeft());
                String right = trimPZ(interVal.getRight());
                inter = left + " - " + right + " ";
                st.setAttribute("left", left);
                st.setAttribute("right", right);
            }
            st.setAttribute("text", inter + unit);
        }

       
        /*
         * // set units if available String unit =
         * nq.getInfoStore().getValue(MMInfo.UNIT); if
         * (nq.getInfoStore().getValue(MMInfo.UNIT) != null) { NumericalInterval
         * interVal = nq.getInfoStore().getValue(
         * BasicProperties.QUESTION_NUM_RANGE); String left = ""; String right =
         * ""; if (interVal != null) { left = trimPZ(interVal.getLeft()); right
         * = trimPZ(interVal.getRight()); st.setAttribute("left", left);
         * st.setAttribute("right", right); }
         *
         * // show interval AND unit as descriptive info for widget
         * st.setAttribute("text", unit + " [" + left + " " + right + "]");
         *
         * }
         */

        Blackboard bb = d3webSession.getBlackboard();
        Value value = bb.getValue(nq);

        if (to.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION)) {
            st.setAttribute("readonly", "true");
            st.setAttribute("inactive", "true");
        }

        // QContainer indicated
        if (bb.getSession().getKnowledgeBase().getInitQuestions().contains(parent)
                || D3webUtils.isIndicatedPlain(parent, bb)) {

            // show, if indicated follow up
            if ((D3webUtils.isFollowUpToQCon(to, parent) && D3webUtils.isIndicatedPlain(to, bb))
                    || (!D3webUtils.isFollowUpToQCon(to, parent))) {
                st.removeAttribute("readonly");
            } else {
                st.setAttribute("readonly", "true");
                // also remove possible set values
                st.removeAttribute("selection");
                st.setAttribute("selection", "");
            }
        } // otherwise, readonly and no vals
        else {
            st.setAttribute("readonly", "true");
            // also remove possible set values
            st.removeAttribute("selection");
            st.setAttribute("selection", "");
        }

        // if not undefined and not unknown, value needs to be written into
        // num field
        if (value != null && UndefinedValue.isNotUndefinedValue(value)
                && !value.equals(Unknown.getInstance())) {

            if (to.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION)) {
                st.removeAttribute("readonly");
                st.removeAttribute("inactive");
            }

            // quick tweak for double-num values formatting
            String doubleString = value.getValue().toString();
            try {
                BigDecimal myDec = new BigDecimal((Double) value.getValue());
                Double numround = myDec.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                DecimalFormat df =
                        (DecimalFormat) DecimalFormat.getInstance(Locale.GERMAN);
                df.applyPattern("#,###,##0.00");
                doubleString = df.format(numround);
                if (doubleString.endsWith("00")) {
                    doubleString = doubleString.substring(0, doubleString.length() - 3);
                }
            } catch (NumberFormatException e) {
            }
            st.removeAttribute("selection");
            st.setAttribute("selection", doubleString);
        } // if undefined or unknown value, erase entries from num field
        else if (UndefinedValue.isUndefinedValue(value)
                || value.equals(Unknown.getInstance())) {
            st.removeAttribute("selection");
            st.setAttribute("selection", "");
        }

        sb.append(st.toString());

        super.makeTables(to, to, cc, sb);

        return sb.toString();
    }

    /**
     * Trims .0 from doubles.
     *
     * @created 16.05.2011
     *
     * @param d is the double to trim
     * @return trimmed String representation of the double
     */
    private String trimPZ(double d) {
        String trimmed = String.valueOf(d);
        if (trimmed.endsWith(".0")) {
            trimmed = trimmed.substring(0, trimmed.length() - 2);
        }
        return trimmed;
    }
}
