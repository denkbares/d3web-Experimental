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
import de.d3web.proket.output.container.ContainerCollection;
import java.util.Vector;
import org.antlr.stringtemplate.StringTemplate;

/**
 *
 * @author Martina Freiberg
 *
 */
public class LegalOcQuestionRenderer extends Renderer {

    @Override
    protected void renderChildren(StringTemplate st, ContainerCollection cc,
            IDialogObject dialogObject, boolean force) {

        IDialogObject parent = dialogObject.getParent();

        if (parent != null) {
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

            // check if this question has subquestions
            if (!dialogObject.getChildren().isEmpty()) {
                
                st.removeAttribute("typeimg");
                st.setAttribute("typeimg", "img/closedArrowAnd.png");

            } else {

                st.removeAttribute("typeimg");
                st.setAttribute("typeimg", "img/transpSquare.png");
            }

            // workaround for removing a doubled-answertype setting in template
            st.removeAttribute("answerType");
            st.setAttribute("answerType", dialogObject.getInheritableAttributes().getAnswerType());

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

        }

        super.renderChildren(st, cc, dialogObject, force);
    }
}
