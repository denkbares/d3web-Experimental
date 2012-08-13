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
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.jurisearch.JuriModel;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * TODO: make super-class for clari hie?!
 *
 * @author Martina Freiberg @created 22.04.2012
 */
public class ITreeDummyQuestionD3webRenderer extends AbstractD3webRenderer implements IQuestionD3webRenderer {

    protected final KnowledgeKind<JuriModel> JURIMODEL = new KnowledgeKind<JuriModel>(
            "JuriModel", JuriModel.class);
    protected static String TT_PROP_ERROR = "<b>Gewählte Antwort widerspricht der aus den Detailfragen hergeleiteten Bewertung.</b> "
            + "<br />Löschen Sie mindestens eine Antwort durch Klick auf den X-Button der jeweiligen Detailfrage, "
            + "wenn Sie eine andere als die bisher hergeleitete Bewertung setzen möchten.";
    

    @Override
    /**
     * Adapted specifically for question rendering
     */
    public String renderTerminologyObject(Session d3webSession, ContainerCollection cc,
            TerminologyObject to, TerminologyObject parent, int loc, HttpSession httpSession,
            HttpServletRequest request) {

      Boolean hidden = to.getInfoStore().getValue(ProKEtProperties.HIDE);
        // return if the InterviewObject is null
        if (to == null || (hidden != null && hidden)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();


        // get the fitting template. In case user prefix was specified, the
        // specific TemplateName is returned, otherwise, the base object name.
        StringTemplate st = TemplateUtils.getStringTemplate(
                super.getTemplateName("ITreeDummyQuestion"), "html");

        // set some basic properties
        st.setAttribute("fullId", getID(to));
        st.setAttribute("title", D3webUtils.getTOPrompt(to, loc).replace("[jnv]", ""));
        
        
        // set bonus text: is displayed in auxinfo panel
        String bonustext = 
                to.getInfoStore().getValue(ProKEtProperties.POPUP);
        st.setAttribute("bonusText", bonustext);
        
        // get d3web properties
        Blackboard bb = d3webSession.getBlackboard();
        Value val = bb.getValue((ValueObject) to);


        // render arrows: --> check whether question has children,
        if(to.getChildren().length > 0){
         st.setAttribute("typeimg", "img/closedArrow.png");
        } else {
            st.setAttribute("typeimg", "img/transpSquare.png");
        }

        // render read flow according to and/or type
        if (to.getInfoStore().getValue(ProKEtProperties.ORTYPE) != null
                && to.getInfoStore().getValue(ProKEtProperties.ORTYPE)) {
            st.setAttribute("readimg", "img/Or.png");
            st.setAttribute("andOrType", "OR");
        } else {
            st.setAttribute("readimg", "img/And.png");
            st.setAttribute("andOrType", "AND");
        }

        // TODO: render the value i.e. coloring of the question
        // System.out.println(getID(to) + ": " + val);

        st.removeAttribute("tty");
        st.removeAttribute("ttn");
        st.removeAttribute("ttu");
        st.removeAttribute("ttnan");


     

        st.setAttribute("tooltip", TT_PROP_ERROR);

        super.renderChildrenITreeNum(st, d3webSession, cc, to, loc, httpSession, request);

        sb.append(st.toString());

        return sb.toString();
    }
}
