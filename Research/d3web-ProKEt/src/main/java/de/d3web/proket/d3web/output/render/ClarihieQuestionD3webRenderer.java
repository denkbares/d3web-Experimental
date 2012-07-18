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
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionZC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.interviewmanager.Form;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.jurisearch.JuriModel;
import de.d3web.jurisearch.JuriRule;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.data.LegalQuestion;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;
import java.util.HashSet;
import java.util.Set;
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
public class ClarihieQuestionD3webRenderer extends AbstractD3webRenderer implements IQuestionD3webRenderer {

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
    final KnowledgeKind<JuriModel> JURIMODEL = new KnowledgeKind<JuriModel>(
            "JuriModel", JuriModel.class);
    public static final String YESSTRING = "ja";
    public static final String NOSTRING = "nein";
    public static final String MAYBESTRING = "vielleicht";

    @Override
    /**
     * Adapted specifically for question rendering
     */
    public String renderTerminologyObject(Session d3webSession, ContainerCollection cc,
            TerminologyObject to, TerminologyObject parent, int loc, HttpSession httpSession) {


        /*
         * ClariHIE specific stuff
         */
        JuriModel juriModel =
                d3webSession.getKnowledgeBase().getKnowledgeStore().getKnowledge(JURIMODEL);
        Set juriRules = juriModel.getRules();



        Boolean hidden = to.getInfoStore().getValue(ProKEtProperties.HIDE);
        // return if the InterviewObject is null
        if (to == null || (hidden != null && hidden)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();


        // get the fitting template. In case user prefix was specified, the
        // specific TemplateName is returned, otherwise, the base object name.
        StringTemplate st = TemplateUtils.getStringTemplate(
                super.getTemplateName("ClarihieQuestion"), "html");

        // set some basic properties
        st.setAttribute("fullId", getID(to));
        st.setAttribute("title", D3webUtils.getTOPrompt(to, loc).replace("[jnv]", ""));
        

        // get d3web properties
        Blackboard bb = d3webSession.getBlackboard();
        Value val = bb.getValue((ValueObject) to);

        Boolean itreeshown = to.getInfoStore().getValue(ProKEtProperties.ITREESHOWN);
        Boolean itreeinit = to.getInfoStore().getValue(ProKEtProperties.ITREEINIT);
        
        // for questions to be initially shown in the tree
        if (itreeinit != null && itreeinit.equals(true) ) {
            st.setAttribute("showITree", true);
        } else {
            st.removeAttribute("showITree");
        }
        
         // if questions are toggled in and out during tree navigation
        if(itreeshown != null && itreeshown.equals(true)){
            st.setAttribute("showITree", true);
        } else {
           st.removeAttribute("showITree");
        }
        
        //System.out.println(D3webUtils.getTOPrompt(to, loc).replace("[jnv]", "") + " " + val);
        
        // set bonus text: is displayed in auxinfo panel
        String bonustext =
                to.getInfoStore().getValue(ProKEtProperties.POPUP);
        st.setAttribute("bonusText", bonustext);


        // render arrows: --> check whether question has children,
        if (!(getChildQuestionsFromJuriRules(to, juriRules)).isEmpty()) {
            st.setAttribute("typeimg", "img/closedArrow.png");
        } else {
            st.setAttribute("typeimg", "img/transpSquare.png");
        }

        if (parent.getName().equals("Q000")) {
            st.setAttribute("readimg", "img/transpSquare.png");
        } else {

            // render read flow according to and/or type
            if (isOrType(to, juriRules)) {
                st.setAttribute("readimg", "img/Or.png");
            } else {
                st.setAttribute("readimg", "img/And.png");
            }
        }

        // TODO: render the value i.e. coloring of the question
        st.removeAttribute("qrating");
        if (UndefinedValue.isNotUndefinedValue(val)) {
            if (val.equals(JuriRule.YES_VALUE)) {
                //System.out.println("YES: " + to.getName());
                st.setAttribute("qrating", "rating-high");
            } else if (val.equals(JuriRule.NO_VALUE)) {
                //System.out.println("NO: " + to.getName());
                st.setAttribute("qrating", "rating-low");
            } else if (val.equals(JuriRule.MAYBE_VALUE)) {
                //System.out.println("MAYBE: " + to.getName());
                st.setAttribute("qrating", "rating-medium");
            }
        } else {
            //System.out.println("UNDEFINED:  " + to.getName());
            st.removeAttribute("qrating");
        }
        //System.out.println(val);
        

        st.removeAttribute("tty");
        st.removeAttribute("ttn");
        st.removeAttribute("ttu");
        st.removeAttribute("ttnan");
        st.removeAttribute("ratingY");
        st.removeAttribute("ratingN");
        st.removeAttribute("swap");

        // set coloring of question buttons according to type of question
        // (normal question or swapped)
        if (isNoDefining(to, juriRules)) {
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

        super.renderChildrenClariHIE(st, d3webSession, cc, to, loc, httpSession);

        sb.append(st.toString());

        return sb.toString();
    }
}
