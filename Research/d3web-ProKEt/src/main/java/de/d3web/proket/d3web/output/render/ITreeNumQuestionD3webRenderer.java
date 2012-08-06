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

import de.d3web.core.inference.KnowledgeKind;
import org.antlr.stringtemplate.StringTemplate;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.jurisearch.JuriModel;
import de.d3web.jurisearch.JuriRule;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;
import javax.servlet.http.HttpSession;

/**
 * Renderer for rendering basic Questions.
 *
 * TODO CHECK: 1) basic properties for questions
 *
 * TODO LATER: 1) further question types needed?
 *
 * @author Martina Freiberg @created 15.01.2011
 */
public class ITreeNumQuestionD3webRenderer extends AbstractD3webRenderer implements IQuestionD3webRenderer {

    // TODO remove from here to global config o.ä.
    private static String TT_YES = "Wertet übergeordnete Frage <b>positiv</b>.";
    private static String TT_NO = "Wertet übergeordnete Frage <b>negativ</b>.";
    private static String TT_UN = "Wertet übergeordnete Frage <b>unsicher/neutral</b>.";
    private static String TT_NAN = "Antwort <b>zurücksetzen</b>.";
    private static String TT_YES_REV = "Wertet übergeordnete Frage <b>negativ</b>.";
    private static String TT_NO_REV = "Wertet übergeordnete Frage <b>positiv</b>.";
    private static String TT_PROP_ERROR = "<b>Gewählte Antwort widerspricht der aus den Detailfragen hergeleiteten Bewertung.</b> "
            + "<br />Löschen Sie mindestens eine Antwort durch Klick auf den X-Button der jeweiligen Detailfrage, "
            + "wenn Sie eine andere als die bisher hergeleitete Bewertung setzen möchten.";

    @Override
    /**
     * Adapted specifically for question rendering
     */
    public String renderTerminologyObject(Session d3webSession, ContainerCollection cc,
            TerminologyObject to, TerminologyObject parent, int loc, HttpSession httpSession) {



        //Boolean hidden = to.getInfoStore().getValue(ProKEtProperties.HIDE);
        // return if the InterviewObject is null
        //if (to == null || (hidden != null && hidden)) {
        //  return "";
        //}
        StringBuilder sb = new StringBuilder();


        // get the fitting template. In case user prefix was specified, the
        // specific TemplateName is returned, otherwise, the base object name.
        StringTemplate st = TemplateUtils.getStringTemplate(
                super.getTemplateName("ITreeNumQuestion"), "html");

        // set some basic properties
        st.setAttribute("fullId", getID(to));
        st.setAttribute("title", D3webUtils.getTOPrompt(to, loc));



        // get d3web properties
        Blackboard bb = d3webSession.getBlackboard();

        // set bonus text: is displayed in auxinfo panel
        String bonustext =
                to.getInfoStore().getValue(ProKEtProperties.POPUP);
        st.setAttribute("bonusText", bonustext);



        // render arrows: --> check whether question has children,
        if (to.getChildren().length > 0) {
            st.setAttribute("typeimg", "img/closedArrow.png");
        } else {
            st.setAttribute("typeimg", "img/transpSquare.png");
        }

        // for questions to be initially shown in the tree
        Boolean itreeinit = to.getInfoStore().getValue(ProKEtProperties.ITREEINIT);
        if (itreeinit != null && itreeinit.equals(true)) {
            st.setAttribute("showitree", true);

            if (to.getChildren().length > 0) {
                st.removeAttribute("typeimg");
                st.setAttribute("typeimg", "img/openedArrow.png");
            }
        }

        if (parent.getName().equals("Q000")) {
            st.setAttribute("readimg", "img/transpSquare.png");
        } else {

            // render read flow according to and/or type
            if (parent.getInfoStore().getValue(ProKEtProperties.ORTYPE) != null
                    && parent.getInfoStore().getValue(ProKEtProperties.ORTYPE).equals(true)) {
                st.setAttribute("readimg", "img/Or.png");
            } else {
                st.setAttribute("readimg", "img/And.png");
            }
        }

        Value val = getAbstractValue(to, bb, d3webSession);
        st.removeAttribute("qrating");


        if (UndefinedValue.isNotUndefinedValue(val)) {

            if (val.toString().equals(JNV.J.toString())) {

                // check if we have "swapped" questions //
                if (to.getInfoStore().getValue(ProKEtProperties.NO_DEFINING) != null
                        && to.getInfoStore().getValue(ProKEtProperties.NO_DEFINING)) {

                    st.setAttribute("qrating", "rating-low");
                } else {
                    st.setAttribute("qrating", "rating-high");
                }

            } else if (val.toString().equals(JNV.N.toString())) {
                // check if we have swapped" questions //
                if (to.getInfoStore().getValue(ProKEtProperties.NO_DEFINING) != null
                        && to.getInfoStore().getValue(ProKEtProperties.NO_DEFINING)) {
                    st.setAttribute("qrating", "rating-high");
                } else {
                    st.setAttribute("qrating", "rating-low");
                }

            } else if (val.toString().equals(JNV.V.toString())) {
                st.setAttribute("qrating", "rating-medium");
            }
        } else {
            st.removeAttribute("qrating");
        }



        st.removeAttribute("tty");
        st.removeAttribute("ttn");
        st.removeAttribute("ttu");
        st.removeAttribute("ttnan");
        st.removeAttribute("ratingY");
        st.removeAttribute("ratingN");
        st.removeAttribute("swap");

        // set coloring of question buttons according to type of question
        // (normal question or swapped)
        if (to.getInfoStore().getValue(ProKEtProperties.NO_DEFINING) != null
                && to.getInfoStore().getValue(ProKEtProperties.NO_DEFINING)) {
            st.setAttribute("ratingY", "rating-low");
            st.setAttribute("ratingN", "rating-high");
            st.setAttribute("swap", "swap");
            st.setAttribute("ratingNrY", "3");
            st.setAttribute("ratingNrN", "1");

            st.setAttribute("tty", TT_YES_REV);
            st.setAttribute("ttn", TT_NO_REV);
        } else {
            st.setAttribute("ratingY", "rating-high");
            st.setAttribute("ratingN", "rating-low");
            st.setAttribute("ratingNrY", "1");
            st.setAttribute("ratingNrN", "3");

            st.setAttribute("tty", TT_YES);
            st.setAttribute("ttn", TT_NO);
        }




        st.setAttribute("ttu", TT_UN);
        st.setAttribute("ttnan", TT_NAN);
        st.setAttribute("tooltip", TT_PROP_ERROR);

        super.renderChildrenITreeNum(st, d3webSession, cc, to, loc, httpSession);

        sb.append(st.toString());

        return sb.toString();
    }

    /**
     * In itree dialogs, there exist normal "num" questions as base objects due
     * to need to model scoring behaviour and corresponding choice abstraction
     * questions for mapping to values yes no and maybe. This method returns the
     * abstraction value of the respective num base object.
     *
     * @param numTO
     * @param blackboard
     * @param sess
     * @return
     */
    private Value getAbstractValue(TerminologyObject numTO, Blackboard blackboard,
            Session sess) {

        TerminologyObject abstractTO =
                sess.getKnowledgeBase().getManager().searchQuestion(numTO.getName().replace("_n", ""));

        return blackboard.getValue((ValueObject) abstractTO);
    }

    private Value getNumValue(TerminologyObject numTO, Blackboard blackboard,
            Session sess) {

        TerminologyObject num =
                sess.getKnowledgeBase().getManager().searchQuestion(numTO.getName());

        return blackboard.getValue((ValueObject) num);
    }
}