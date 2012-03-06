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

import de.d3web.core.session.Session;
import de.d3web.proket.data.IDialogObject;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.GlobalSettings;
import de.d3web.proket.utils.TemplateUtils;
import java.util.Vector;
import org.antlr.stringtemplate.StringTemplate;

/**
 *
 * @author Martina Freiberg
 *
 */
public class OQQuestionRenderer extends Renderer {

    @Override
    public String renderDialogObject(ContainerCollection cc, IDialogObject dialogObject,
            boolean recurseCount, boolean excludeChildren, boolean force, Session session) {

        // TODO maybe null is not such a good idea here?
        // already rendered somewhere? If yes, and if render-force is not set
        // then return null, i.e. no representation
        if (dialogObject.isRendered() && !force) {
            return null;
        }




        StringBuilder result = new StringBuilder();

        // get the HTML template for the dialog object
        StringTemplate st = TemplateUtils.getStringTemplate(
                dialogObject.getVirtualClassName(), "html");

        if (dialogObject.getParent().getXMLTag().toString().contains("dialog")) {
            st.setAttribute("hide", "hide");
        } else {
            st.setAttribute("hide", "show");

            String qcnew = "";
            String questioncount = GlobalSettings.getInstance().getQuestionCount();
            String[] parts = questioncount.split("\\.");
            System.out.println(recurseCount);

            if (parts.length > 1) {

                if (recurseCount) {
                    qcnew = questioncount + ".1";
                } else {
                    int count = Integer.parseInt(parts[parts.length - 1]);
                    count++;
                    for (int i = 0; i < parts.length - 1; i++) {
                        qcnew += parts[i] + ".";
                    }
                    qcnew += count;

                }

               } else {
                if (recurseCount) {
                    qcnew = questioncount + ".1";
                } else {

                    int count = Integer.parseInt(questioncount);
                    qcnew = Integer.toString(++count);
                }

            }


            st.setAttribute("questioncount", qcnew);
            GlobalSettings.getInstance().setQuestionCount(qcnew);
        }

        // get the inh.attributes of this objects and inherit missing
        // attributes where needed from parents
        dialogObject.getInheritableAttributes().compileInside();

        // fill the stringtemplate with attributes/getters from the object
        fillTemplate(dialogObject, st);

        // include css styles
        handleCss(cc, dialogObject);
        // children: if they are not to be excluded, just render them forcedly
        if (!excludeChildren) {
            renderChildren(st, cc, dialogObject, force);
        }

        // append filled template to result string
        result.append(st.toString());

        // optional tables
        makeTables(dialogObject, cc, result);

        // visible?
        if (!dialogObject.isVisible()) {
            cc.css.addStyle("display: none;", "#" + dialogObject.getFullId());
        }

        // mark as rendered
        dialogObject.setRendered(
                true);

        // save to output
        return result.toString();
    }

    @Override
    protected void renderChildren(StringTemplate st, ContainerCollection cc, 
            IDialogObject dialogObject, boolean force) {

        IDialogObject parent = dialogObject.getParent();
        String pTitle = parent.getTitle();

        if (dialogObject.getChildren().isEmpty()) {
            st.setAttribute("hideDetails", "hide");
        }


        if (pTitle != null && parent != null) {



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

            String andOrTypePar = parent.getInheritableAttributes().getAndOrType();

            // Check if this question has subquestions
            if (dialogObject.getChildren().size() != 0) {

                st.removeAttribute("typeimg");
                if (andOrTypePar.equals("OR")) {
                    st.setAttribute("typeimg", "img/closedArrowOr.png");
                } else if (andOrTypePar.equals("AND")) {
                    st.setAttribute("typeimg", "img/closedArrowAnd.png");
                }
            } else {

                st.removeAttribute("typeimg");
                if (andOrTypePar.equals("OR")) {
                    st.setAttribute("typeimg", "img/transpOr.png");
                } else if (andOrTypePar.equals("AND")) {
                    st.setAttribute("typeimg", "img/transpAnd.png");
                }

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

            st.removeAttribute("typeimg");
            if (!dialogObject.getChildren().isEmpty()) {

                st.setAttribute("typeimg", "img/closedArrow.png");
            } else {


                st.setAttribute("typeimg", "img/transpSquare.png");
            }
        }

        super.renderChildren(st, cc, dialogObject, force);

    }
}
