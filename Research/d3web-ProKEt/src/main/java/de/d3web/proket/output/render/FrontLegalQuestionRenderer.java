/**
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.proket.output.render;

import de.d3web.proket.data.IDialogObject;
import de.d3web.proket.data.LegalQuestion;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.GlobalSettings;
import java.util.Vector;
import org.antlr.stringtemplate.StringTemplate;

/**
 *
 * @author Martina Freiberg
 *
 */
public class FrontLegalQuestionRenderer extends Renderer {

    private static String TT_YES = "Wertet Eltern-Frage <b>positiv</b>.";
    private static String TT_NO = "Wertet Eltern-Frage <b>negativ</b>.";
    private static String TT_UN = "Wertet Eltern-Frage <b>unsicher/neutral</b>.";
    private static String TT_NAN = "Antwort <b>zurücksetzen</b>.";
    private static String TT_YES_REV = "Wertet übergeordnete Frage <b>negativ</b>.";
    private static String TT_NO_REV = "Wertet übergeordnete Frage <b>positiv</b>.";
    
    private static String TT_PROP_ERROR = "<b>Gewählte Antwort widerspricht der aus den Detailfragen hergeleiteten Bewertung.</b> "
            + "<br />Löschen Sie mindestens eine Antwort durch Klick auf den X-Button der jeweiligen Detailfrage, "
            + "wenn Sie eine andere als die bisher hergeleitete Bewertung setzen möchten.";

    @Override
    protected void renderChildren(StringTemplate st, ContainerCollection cc,
            IDialogObject dialogObject, boolean force) {

        IDialogObject parent = dialogObject.getParent();
        String pTitle = parent.getTitle();

        if ((pTitle != null && parent != null)
                || (pTitle == null && parent instanceof LegalQuestion && ((LegalQuestion) parent).getDummy())) {

            Vector<IDialogObject> children = parent.getChildren();
            StringBuffer childrenHTML = new StringBuffer();
            for (IDialogObject child : children) {

                if (child.getXMLTag().getAttribute("parent-id").equals(dialogObject.getId())) {

                    IRenderer renderer = Renderer.getRenderer(child);
                    String childHTML = renderer.renderDialogObject(cc, child);
                    if (childHTML != null) {
                        childrenHTML.append(childHTML);
                    }

                }
            }

            // Check if this question has subquestions
            if (!dialogObject.getChildren().isEmpty()) {
                st.setAttribute("typeimg", "img/closedArrow.png");
            } else {
                st.setAttribute("typeimg", "img/transpSquare.png");
            }

            String andOrTypePar = parent.getInheritableAttributes().getAndOrType();
            st.removeAttribute("readimg");
            if (andOrTypePar.equals("OR")) {
                st.setAttribute("readimg", "img/Or.png");
            } else if (andOrTypePar.equals("AND")) {
                st.setAttribute("readimg", "img/And.png");
            }

            // workaround for removing a doubled-answertype setting in template
            st.removeAttribute("answerType");
            st.setAttribute("answerType", dialogObject.getInheritableAttributes().getAnswerType());

            st.removeAttribute("andOrType");
            st.setAttribute("andOrType", dialogObject.getInheritableAttributes().getAndOrType());


            /*
             * We have children not necessarily direct dialog object children
             * (subquestions) but smthg. like the image panel etc.
             */
            if (childrenHTML.length() > 0) {
                // we have children
                st.setAttribute("children", childrenHTML.toString());
                st.setAttribute("hasChildren", "");

            } else {
                // no child questions
                st.setAttribute("noChildren", "");
            }
        } else {
            st.setAttribute("readimg", "img/transpSquare.png");

            if (!dialogObject.getChildren().isEmpty()) {
                st.setAttribute("typeimg", "img/closedArrow.png");
            } else {
                st.setAttribute("typeimg", "img/transpSquare.png");
            }

        }

        st.removeAttribute("ratingY");
        st.removeAttribute("ratingN");
        st.removeAttribute("ratingNrY");
        st.removeAttribute("ratingNrN");

        st.removeAttribute("tty");
        st.removeAttribute("ttn");
        st.removeAttribute("ttu");
        st.removeAttribute("ttnan");


        if (dialogObject instanceof LegalQuestion
                && ((LegalQuestion) dialogObject).getDefining() != null
                && ((LegalQuestion) dialogObject).getDefining().equals("nein")) {

            st.setAttribute("ratingY", "rating-low");
            st.setAttribute("ratingN", "rating-high");
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
        
        // get possibly linked resources; are linked in the form [res1##showif]###[res2]
        String res = dialogObject.getResources();
        if(res != null && !res.equals("")){
            String[] splitRes = res.split("###");
            
            // go through all resource definitions
            if(splitRes != null && splitRes.length >0){
                String linkedResources = "";String rdef = "";String rif = "";
                
                for(String r: splitRes){
                    r = r.replace("[", "").replace("]", "");
                    
                    String[] rsplit = r.split("##");
                    rdef = rsplit[0];
                    rif = rsplit[1];
                    
                    // get the corresponding resources from the resources
                    // folder in: /webapp/resources
                    String resource = GlobalSettings.getInstance().getResourcesPath()
                                + rdef;
                    String link = "<a href=" + resource + ">" + rdef + "</a><br />";
                    linkedResources += link;
                    
                }
                st.setAttribute("linkedResources", linkedResources);
                st.setAttribute("showif", rif);
            }
        }

        super.renderChildren(st, cc, dialogObject, force);
    }
}
