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
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.proket.d3web.utils.D3webUtils;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;
import java.util.ArrayList;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
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
public class ITreeQuestionD3webRenderer extends AbstractD3webRenderer implements IQuestionD3webRenderer {

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
            TerminologyObject to, TerminologyObject parent, int loc, HttpSession httpSession,
            HttpServletRequest request) {

        StringBuilder sb = new StringBuilder();


        // get the fitting template. In case user prefix was specified, the
        // specific TemplateName is returned, otherwise, the base object name.
        StringTemplate st = TemplateUtils.getStringTemplate(
                super.getTemplateName("ITreeQuestion"), "html");

        // set some basic properties
        st.setAttribute("fullId", getID(to));
        st.setAttribute("title", D3webUtils.getTOPrompt(to, loc));

        // Set bonus text: is displayed in auxinfo panel
        String bonustext =
                to.getInfoStore().getValue(ProKEtProperties.POPUP);
        st.setAttribute("bonusText", bonustext);


        /*
         * BASIC FOLDING RENDERING and INIT QUESTIONS
         */
        // Basic rendering of folding-arrows --> check if question has children
        if (to.getChildren().length > 0) {
            st.setAttribute("typeimg", "img/closedArrow.png");
        } else {
            st.setAttribute("typeimg", "img/transpSquare.png");
        }
        // Re-check state for init questions
        Boolean itreeinit = to.getInfoStore().getValue(ProKEtProperties.ITREEINIT);
        if (itreeinit != null && itreeinit.equals(true)) {
            // IF INIT QUESTION
            // show children and arrow  
            st.setAttribute("showitree", true);
            if (to.getChildren().length > 0) {
                st.removeAttribute("typeimg");
                st.setAttribute("typeimg", "img/openedArrow.png");
            }
        } else if (itreeinit != null && itreeinit.equals(false)) {
            // IF NOT INIT QUESTION
            // do not expand children but if it has children, show corresponding
            // arrow state
            st.removeAttribute("showitree");
            if (to.getChildren().length > 0) {
                st.removeAttribute("typeimg");
                st.setAttribute("typeimg", "img/closedArrow.png");
            }
        }

        // Check for COOKIES...
        Cookie[] cookies = request.getCookies();
        Boolean cookieShow = null;
        if (((itreeinit != null && !itreeinit)
                || itreeinit == null)
                && cookies != null) {
            cookieShow = getShowStateFromCookie(to, cookies);
        }
        //...and INDICATION STATE
        Blackboard bb = d3webSession.getBlackboard();
        ArrayList<Boolean> indicatedChildren = new ArrayList<Boolean>();
        for (TerminologyObject child : to.getChildren()) {
            if (D3webUtils.isIndicated(child, bb)) {
                indicatedChildren.add(true);
            }
        }

        /*
         * COOKIE BASED SHOW STATE
         */
        // If cookie has saved show-state for question: show always
        if (cookieShow != null && cookieShow.equals(true)) {
            st.setAttribute("showitree", true);
            if (to.getChildren().length > 0) {
                st.removeAttribute("typeimg");
                st.setAttribute("typeimg", "img/openedArrow.png");
            }
        }


        /*
         * FOLLOW UP QUESTIONS
         */
        if (to.getInfoStore().getValue(ProKEtProperties.HIDDENFU) != null
                && to.getInfoStore().getValue(ProKEtProperties.HIDDENFU)) {
            // if the hidden follow up has not at least 1 indicated child
            // DO NOT SHOW
            if (!indicatedChildren.contains(true)) {
                st.removeAttribute("typeimg");
                st.setAttribute("typeimg", "img/transpSquare.png");
            } // otherwise if at least 1 indicated child THEN SHOW
            else {
                st.setAttribute("showitree", true);
                if (to.getChildren().length > 0) {
                    st.removeAttribute("typeimg");
                    st.setAttribute("typeimg", "img/openedArrow.png");
                }
            }
        } /*
         * else { st.setAttribute("showitree", true); if
         * (to.getChildren().length > 0) { st.removeAttribute("typeimg");
         * st.setAttribute("typeimg", "img/openedArrow.png"); } }
         */

        /*
         * READ FLOW - AND/OR/Score/Rules verbalization
         */
        // for topmost element, do not render any read flow verbalization
        if (parent.getName().equals("Q000")) {
            st.setAttribute("readimg", "img/transpSquare.png");
        } 
        // if the parent is of ruletype, than show formula sign for its children
        else if (parent.getInfoStore().getValue(ProKEtProperties.RULETYPE) != null
                && parent.getInfoStore().getValue(ProKEtProperties.RULETYPE).equals(true)) {
            st.setAttribute("readimg", "img/Formula.png");
            st.setAttribute("qtype", "ruletype");
            st.setAttribute("imgwidth", "28px");
            st.setAttribute("imgheight", "30px");
        } else if (parent.getInfoStore().getValue(ProKEtProperties.ORTYPE) != null
                && parent.getInfoStore().getValue(ProKEtProperties.ORTYPE).equals(true)) {
            st.setAttribute("readimg", "img/Or.png");
        } else {
            st.setAttribute("readimg", "img/And.png");

        }


        /*
         * Check if parent question has scoring question correspondant. If yes,
         * set property in ST
         */
        TerminologyObject scoringCor =
                d3webSession.getKnowledgeBase().getManager().search(parent.getName().replace(parent.getName(), parent.getName() + "_n"));
        if (scoringCor != null) {
            st.setAttribute("qtype", "scoretype");
            st.removeAttribute("readimg");
            st.setAttribute("readimg", "img/Score.png");
            st.setAttribute("imgwidth", "28px");
            st.setAttribute("imgheight", "20px");
        }

        /*
         * RENDER VALUE STATE OF THE QUESTION -> coloring
         */
        Value val = bb.getValue((ValueObject) to);
        st.removeAttribute("qrating");

        if (UndefinedValue.isNotUndefinedValue(val)) {
            if (val.toString().equals(JNV.J.toString())) {
                st.setAttribute("qrating", "rating-high");
            } else if (val.toString().equals(JNV.N.toString())) {
                st.setAttribute("qrating", "rating-low");
            } else if (val.toString().equals(JNV.V.toString())) {
                st.setAttribute("qrating", "rating-medium");
            }
        } else {
            st.removeAttribute("qrating");
        }

        // remove previously set attributes
        st.removeAttribute("tty");
        st.removeAttribute("ttn");
        st.removeAttribute("ttu");
        st.removeAttribute("ttnan");
        st.removeAttribute("ratingY");
        st.removeAttribute("ratingN");
        st.removeAttribute("swap");

        // set coloring of question buttons and rating value according to type 
        // of question (normal question or swapped), set tooltips 
        // YES NO Buttons first
        if (to.getInfoStore().getValue(ProKEtProperties.NO_DEFINING) != null
                && to.getInfoStore().getValue(ProKEtProperties.NO_DEFINING)) {
            // IF SWAPPED QUESTION
            st.setAttribute("ratingY", "rating-low");
            st.setAttribute("ratingN", "rating-high");
            st.setAttribute("swap", "swap");
            st.setAttribute("ratingNrY", "3");
            st.setAttribute("ratingNrN", "1");
            st.setAttribute("tty", TT_YES_REV);
            st.setAttribute("ttn", TT_NO_REV);
        } else {
            // NORMAL QUESTION
            st.setAttribute("ratingY", "rating-high");
            st.setAttribute("ratingN", "rating-low");
            st.setAttribute("ratingNrY", "1");
            st.setAttribute("ratingNrN", "3");
            st.setAttribute("tty", TT_YES);
            st.setAttribute("ttn", TT_NO);
        }

        // set remaining tooltips
        st.setAttribute("ttu", TT_UN);
        st.setAttribute("ttnan", TT_NAN);
        st.setAttribute("tooltip", TT_PROP_ERROR);

        // initiate children rendering
        super.renderChildrenITreeNum(st, d3webSession, cc, to, loc, httpSession, request);

        // return everything as String
        sb.append(st.toString());
        return sb.toString();
    }
}
